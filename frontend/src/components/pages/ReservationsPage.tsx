import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import ApiService from '../../services/apiService';

interface Reservation {
  id: number;
  confirmationNumber: string;
  hotelName: string;
  roomNumber: string;
  roomType: string;
  guestName: string;
  guestEmail: string;
  guestPhone?: string;
  checkInDate: string;
  checkOutDate: string;
  numberOfGuests: number;
  numberOfNights: number;
  status: string;
  totalAmount: number;
  paidAmount?: number;
  specialRequests?: string;
  createdAt: string;
}

const ReservationsPage: React.FC = () => {
  const navigate = useNavigate();
  const [reservations, setReservations] = useState<Reservation[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadReservations();
  }, []);

  const loadReservations = async () => {
    try {
      setLoading(true);
      const response = await ApiService.getUserReservations();
      if (response.success) {
        setReservations(response.data);
      } else {
        setError(response.message || 'Failed to load reservations');
      }
    } catch (err) {
      setError('An error occurred while loading reservations');
    } finally {
      setLoading(false);
    }
  };

  const getStatusColor = (status: string) => {
    switch (status.toUpperCase()) {
      case 'CONFIRMED':
        return 'bg-green-100 text-green-800';
      case 'PENDING':
        return 'bg-yellow-100 text-yellow-800';
      case 'CHECKED_IN':
        return 'bg-blue-100 text-blue-800';
      case 'CHECKED_OUT':
        return 'bg-gray-100 text-gray-800';
      case 'CANCELLED':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  };

  const formatCurrency = (amount: number) => {
    return `₹${amount.toLocaleString()}`;
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-amber-600"></div>
          <p className="mt-4 text-gray-600">Loading your reservations...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <h1 className="text-2xl font-bold text-gray-900 mb-4">Error</h1>
          <p className="text-gray-600 mb-4">{error}</p>
          <button
            onClick={() => navigate('/hotels')}
            className="bg-amber-600 text-white px-6 py-2 rounded-lg hover:bg-amber-700"
          >
            Browse Hotels
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Header */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-8">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-3xl font-bold text-gray-900 mb-2">My Reservations</h1>
              <p className="text-gray-600">
                {reservations.length === 0 
                  ? 'You have no reservations yet'
                  : `You have ${reservations.length} reservation${reservations.length !== 1 ? 's' : ''}`
                }
              </p>
            </div>
            <button
              onClick={() => navigate('/hotels')}
              className="bg-amber-600 text-white px-6 py-2 rounded-lg hover:bg-amber-700 transition-colors"
            >
              Book New Stay
            </button>
          </div>
        </div>

        {reservations.length === 0 ? (
          <div className="bg-white rounded-lg shadow-md p-12 text-center">
            <svg className="mx-auto h-16 w-16 text-gray-400 mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
            </svg>
            <h3 className="text-lg font-medium text-gray-900 mb-2">No reservations yet</h3>
            <p className="text-gray-600 mb-6">Start planning your next trip by browsing our available hotels.</p>
            <button
              onClick={() => navigate('/hotels')}
              className="bg-amber-600 text-white px-6 py-3 rounded-lg hover:bg-amber-700 transition-colors"
            >
              Explore Hotels
            </button>
          </div>
        ) : (
          <div className="space-y-6">
            {reservations.map((reservation) => (
              <div key={reservation.id} className="bg-white rounded-lg shadow-md overflow-hidden">
                <div className="p-6">
                  {/* Reservation Header */}
                  <div className="flex items-start justify-between mb-4">
                    <div>
                      <h3 className="text-xl font-semibold text-gray-900 mb-1">
                        {reservation.hotelName}
                      </h3>
                      <p className="text-gray-600">
                        Confirmation: {reservation.confirmationNumber}
                      </p>
                    </div>
                    <span className={`px-3 py-1 rounded-full text-sm font-medium ${getStatusColor(reservation.status)}`}>
                      {reservation.status}
                    </span>
                  </div>

                  {/* Reservation Details Grid */}
                  <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-6">
                    <div>
                      <h4 className="text-sm font-medium text-gray-500 mb-1">Room Details</h4>
                      <p className="text-gray-900">Room {reservation.roomNumber}</p>
                      <p className="text-gray-600 text-sm">{reservation.roomType}</p>
                    </div>
                    
                    <div>
                      <h4 className="text-sm font-medium text-gray-500 mb-1">Dates</h4>
                      <p className="text-gray-900">
                        {formatDate(reservation.checkInDate)} - {formatDate(reservation.checkOutDate)}
                      </p>
                      <p className="text-gray-600 text-sm">
                        {reservation.numberOfNights} night{reservation.numberOfNights !== 1 ? 's' : ''}
                      </p>
                    </div>
                    
                    <div>
                      <h4 className="text-sm font-medium text-gray-500 mb-1">Guests</h4>
                      <p className="text-gray-900">{reservation.numberOfGuests} guest{reservation.numberOfGuests !== 1 ? 's' : ''}</p>
                      <p className="text-gray-600 text-sm">{reservation.guestName}</p>
                    </div>
                    
                    <div>
                      <h4 className="text-sm font-medium text-gray-500 mb-1">Total Amount</h4>
                      <p className="text-gray-900 text-lg font-semibold">
                        {formatCurrency(reservation.totalAmount)}
                      </p>
                      {reservation.paidAmount && reservation.paidAmount > 0 && (
                        <p className="text-green-600 text-sm">
                          Paid: {formatCurrency(reservation.paidAmount)}
                        </p>
                      )}
                    </div>
                  </div>

                  {/* Contact Information */}
                  <div className="border-t pt-4 mb-4">
                    <h4 className="text-sm font-medium text-gray-500 mb-2">Contact Information</h4>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div>
                        <p className="text-gray-900">{reservation.guestEmail}</p>
                      </div>
                      {reservation.guestPhone && (
                        <div>
                          <p className="text-gray-900">{reservation.guestPhone}</p>
                        </div>
                      )}
                    </div>
                  </div>

                  {/* Special Requests */}
                  {reservation.specialRequests && (
                    <div className="border-t pt-4 mb-4">
                      <h4 className="text-sm font-medium text-gray-500 mb-2">Special Requests</h4>
                      <p className="text-gray-700">{reservation.specialRequests}</p>
                    </div>
                  )}

                  {/* Booking Date */}
                  <div className="border-t pt-4">
                    <p className="text-gray-500 text-sm">
                      Booked on {formatDate(reservation.createdAt)}
                    </p>
                  </div>
                </div>

                {/* Action Buttons */}
                <div className="bg-gray-50 px-6 py-3">
                  <div className="flex space-x-3">
                    {reservation.status === 'PENDING' && (
                      <>
                        <button className="flex-1 bg-red-600 text-white py-2 px-4 rounded-lg hover:bg-red-700 transition-colors">
                          Cancel Booking
                        </button>
                        <button className="flex-1 bg-green-600 text-white py-2 px-4 rounded-lg hover:bg-green-700 transition-colors">
                          Confirm & Pay
                        </button>
                      </>
                    )}
                    {reservation.status === 'CONFIRMED' && (
                      <button className="bg-amber-600 text-white py-2 px-4 rounded-lg hover:bg-amber-700 transition-colors">
                        View Details
                      </button>
                    )}
                    {reservation.status === 'CHECKED_IN' && (
                      <button className="bg-blue-600 text-white py-2 px-4 rounded-lg hover:bg-blue-700 transition-colors">
                        Check Out
                      </button>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default ReservationsPage;
