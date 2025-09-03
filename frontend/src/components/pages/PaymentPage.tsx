import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import ApiService from '../../services/apiService';

interface BookingDetails {
  id: number;
  confirmationNumber: string;
  totalAmount: number;
  paidAmount: number;
  roomNumber: string;
  roomType: string;
  guestName: string;
  checkInDate: string;
  checkOutDate: string;
  numberOfNights: number;
  status: string;
}

interface PaymentForm {
  cardNumber: string;
  expiryDate: string;
  cvv: string;
  cardName: string;
  paymentMethod: string;
}

const PaymentPage: React.FC = () => {
  const { bookingId } = useParams<{ bookingId: string }>();
  const navigate = useNavigate();
  const [booking, setBooking] = useState<BookingDetails | null>(null);
  const [loading, setLoading] = useState(true);
  const [paymentLoading, setPaymentLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [paymentForm, setPaymentForm] = useState<PaymentForm>({
    cardNumber: '',
    expiryDate: '',
    cvv: '',
    cardName: '',
    paymentMethod: 'card'
  });

  useEffect(() => {
    if (bookingId) {
      loadBookingDetails();
    }
  }, [bookingId]);

  const loadBookingDetails = async () => {
    try {
      setLoading(true);
      const response = await fetch(`http://localhost:8081/api/bookings/${bookingId}`);
      if (response.ok) {
        const bookingData = await response.json();
        setBooking({
          id: bookingData.id,
          confirmationNumber: `HTL${bookingData.id}${Math.random().toString(36).substr(2, 4).toUpperCase()}`,
          totalAmount: bookingData.totalAmount,
          paidAmount: bookingData.paidAmount || 0,
          roomNumber: bookingData.room?.roomNumber || 'N/A',
          roomType: bookingData.room?.roomType || 'STANDARD',
          guestName: bookingData.guestName,
          checkInDate: bookingData.checkInDate,
          checkOutDate: bookingData.checkOutDate,
          numberOfNights: bookingData.numberOfNights,
          status: bookingData.status
        });
      } else {
        setError('Booking not found');
      }
    } catch (err) {
      setError('Failed to load booking details');
    } finally {
      setLoading(false);
    }
  };

  const handlePayment = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!booking) return;

    // Validate payment form
    if (!paymentForm.cardNumber || !paymentForm.expiryDate || !paymentForm.cvv || !paymentForm.cardName) {
      alert('Please fill in all payment details');
      return;
    }

    try {
      setPaymentLoading(true);

      // Create payment data for the Payment Service
      const paymentData = {
        bookingId: booking.id,
        amount: booking.totalAmount - booking.paidAmount,
        paymentMethod: paymentForm.paymentMethod === 'card' ? 'CREDIT_CARD' : 
                      paymentForm.paymentMethod === 'upi' ? 'UPI' : 
                      paymentForm.paymentMethod === 'netbanking' ? 'BANK_TRANSFER' : 'CREDIT_CARD',
        status: 'PENDING',
        customerEmail: 'guest@hotel.com', // In real app, get from auth context
        customerName: paymentForm.cardName,
        description: `Payment for Room ${booking.roomNumber} booking`,
        cardLastFourDigits: paymentForm.cardNumber.slice(-4),
        cardType: 'UNKNOWN' // Could be determined from card number
      };

      // Process payment through Payment Service
      const paymentResponse = await fetch('http://localhost:8082/api/v1/payments', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(paymentData)
      });

      if (paymentResponse.ok) {
        const paymentResult = await paymentResponse.json();
        
        // Update booking status to confirmed after successful payment
        const updateBookingResponse = await fetch(`http://localhost:8081/api/bookings/${booking.id}/status`, {
          method: 'PATCH',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({ status: 'CONFIRMED' })
        });

        if (updateBookingResponse.ok) {
          alert('Payment successful! Your booking is confirmed.');
          navigate('/reservations');
        } else {
          alert('Payment processed but booking status update failed. Please contact support.');
        }
      } else {
        const errorData = await paymentResponse.json();
        console.error('Payment error:', errorData);
        
        let errorMessage = 'Payment failed. Please try again.';
        if (errorData.message) {
          errorMessage = `Payment failed: ${errorData.message}`;
        } else if (errorData.errors && errorData.errors.length > 0) {
          errorMessage = `Payment failed: ${errorData.errors.join(', ')}`;
        }
        
        alert(errorMessage);
      }
      
    } catch (err) {
      console.error('Payment error:', err);
      alert('Payment failed. Please try again.');
    } finally {
      setPaymentLoading(false);
    }
  };

  const formatCardNumber = (value: string) => {
    // Remove all non-digit characters
    const v = value.replace(/\s+/g, '').replace(/[^0-9]/gi, '');
    // Add spaces every 4 digits
    const matches = v.match(/\d{4,16}/g);
    const match = matches && matches[0] || '';
    const parts = [];
    for (let i = 0, len = match.length; i < len; i += 4) {
      parts.push(match.substring(i, i + 4));
    }
    if (parts.length) {
      return parts.join(' ');
    } else {
      return v;
    }
  };

  const formatExpiryDate = (value: string) => {
    const v = value.replace(/\s+/g, '').replace(/[^0-9]/gi, '');
    if (v.length >= 2) {
      return v.substring(0, 2) + '/' + v.substring(2, 4);
    }
    return v;
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-amber-600"></div>
          <p className="mt-4 text-gray-600">Loading payment details...</p>
        </div>
      </div>
    );
  }

  if (error || !booking) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <h1 className="text-2xl font-bold text-gray-900 mb-4">Payment Error</h1>
          <p className="text-gray-600 mb-4">{error || 'Booking not found'}</p>
          <button
            onClick={() => navigate('/reservations')}
            className="bg-amber-600 text-white px-6 py-2 rounded-lg hover:bg-amber-700"
          >
            View Reservations
          </button>
        </div>
      </div>
    );
  }

  // If already paid, redirect to reservations
  if (booking.paidAmount >= booking.totalAmount) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <h1 className="text-2xl font-bold text-green-600 mb-4">✅ Payment Complete</h1>
          <p className="text-gray-600 mb-4">This booking has already been paid for.</p>
          <button
            onClick={() => navigate('/reservations')}
            className="bg-amber-600 text-white px-6 py-2 rounded-lg hover:bg-amber-700"
          >
            View Reservations
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Header */}
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">Complete Your Payment</h1>
          <p className="text-gray-600">Secure payment for your hotel reservation</p>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          {/* Booking Summary */}
          <div className="bg-white rounded-lg shadow-md p-6">
            <h2 className="text-xl font-semibold text-gray-900 mb-4">Booking Summary</h2>
            
            <div className="space-y-3">
              <div className="flex justify-between">
                <span className="text-gray-600">Confirmation Number:</span>
                <span className="font-medium">{booking.confirmationNumber}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Room:</span>
                <span className="font-medium">Room {booking.roomNumber} ({booking.roomType})</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Guest:</span>
                <span className="font-medium">{booking.guestName}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Check-in:</span>
                <span className="font-medium">{new Date(booking.checkInDate).toLocaleDateString()}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Check-out:</span>
                <span className="font-medium">{new Date(booking.checkOutDate).toLocaleDateString()}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Nights:</span>
                <span className="font-medium">{booking.numberOfNights}</span>
              </div>
              <div className="border-t pt-3">
                <div className="flex justify-between text-lg font-semibold">
                  <span>Total Amount:</span>
                  <span>₹{booking.totalAmount.toLocaleString()}</span>
                </div>
                {booking.paidAmount > 0 && (
                  <div className="flex justify-between text-green-600">
                    <span>Paid:</span>
                    <span>₹{booking.paidAmount.toLocaleString()}</span>
                  </div>
                )}
                <div className="flex justify-between text-xl font-bold text-amber-600">
                  <span>Amount Due:</span>
                  <span>₹{(booking.totalAmount - booking.paidAmount).toLocaleString()}</span>
                </div>
              </div>
            </div>
          </div>

          {/* Payment Form */}
          <div className="bg-white rounded-lg shadow-md p-6">
            <h2 className="text-xl font-semibold text-gray-900 mb-4">Payment Details</h2>
            
            <form onSubmit={handlePayment} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Payment Method
                </label>
                <select
                  value={paymentForm.paymentMethod}
                  onChange={(e) => setPaymentForm({ ...paymentForm, paymentMethod: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-500"
                >
                  <option value="card">Credit/Debit Card</option>
                  <option value="upi">UPI</option>
                  <option value="netbanking">Net Banking</option>
                </select>
              </div>

              {paymentForm.paymentMethod === 'card' && (
                <>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Card Number
                    </label>
                    <input
                      type="text"
                      placeholder="1234 5678 9012 3456"
                      value={paymentForm.cardNumber}
                      onChange={(e) => setPaymentForm({ 
                        ...paymentForm, 
                        cardNumber: formatCardNumber(e.target.value) 
                      })}
                      maxLength={19}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-500"
                      required
                    />
                  </div>

                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">
                        Expiry Date
                      </label>
                      <input
                        type="text"
                        placeholder="MM/YY"
                        value={paymentForm.expiryDate}
                        onChange={(e) => setPaymentForm({ 
                          ...paymentForm, 
                          expiryDate: formatExpiryDate(e.target.value) 
                        })}
                        maxLength={5}
                        className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-500"
                        required
                      />
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">
                        CVV
                      </label>
                      <input
                        type="text"
                        placeholder="123"
                        value={paymentForm.cvv}
                        onChange={(e) => setPaymentForm({ 
                          ...paymentForm, 
                          cvv: e.target.value.replace(/\D/g, '').substring(0, 3) 
                        })}
                        maxLength={3}
                        className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-500"
                        required
                      />
                    </div>
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Cardholder Name
                    </label>
                    <input
                      type="text"
                      placeholder="Name on card"
                      value={paymentForm.cardName}
                      onChange={(e) => setPaymentForm({ ...paymentForm, cardName: e.target.value })}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-500"
                      required
                    />
                  </div>
                </>
              )}

              {paymentForm.paymentMethod === 'upi' && (
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    UPI ID
                  </label>
                  <input
                    type="text"
                    placeholder="yourname@paytm"
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-500"
                    required
                  />
                </div>
              )}

              {paymentForm.paymentMethod === 'netbanking' && (
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Select Bank
                  </label>
                  <select className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-500">
                    <option>State Bank of India</option>
                    <option>HDFC Bank</option>
                    <option>ICICI Bank</option>
                    <option>Axis Bank</option>
                  </select>
                </div>
              )}

              <div className="pt-4">
                <button
                  type="submit"
                  disabled={paymentLoading}
                  className="w-full bg-green-600 text-white py-3 px-4 rounded-lg hover:bg-green-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {paymentLoading ? (
                    <div className="flex items-center justify-center">
                      <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white mr-2"></div>
                      Processing Payment...
                    </div>
                  ) : (
                    `Pay ₹${(booking.totalAmount - booking.paidAmount).toLocaleString()}`
                  )}
                </button>
              </div>

              <div className="pt-2">
                <button
                  type="button"
                  onClick={() => navigate('/reservations')}
                  className="w-full bg-gray-200 text-gray-700 py-2 px-4 rounded-lg hover:bg-gray-300 transition-colors"
                >
                  Pay Later
                </button>
              </div>
            </form>

            <div className="mt-6 text-center text-sm text-gray-500">
              <p>🔒 Your payment information is secure and encrypted</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default PaymentPage;
