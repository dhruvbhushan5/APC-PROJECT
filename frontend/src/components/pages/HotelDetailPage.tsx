import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import ApiService from '../../services/apiService';

interface Room {
  id: number;
  roomNumber: string;
  roomType: string;
  pricePerNight: number;
  occupancy: { adults: number; children: number };
  amenities: string[];
  images: string[];
  description: string;
  status: string;
  area: number;
  floorNumber: number;
  view: string;
}

interface Hotel {
  id: number;
  name: string;
  description: string;
  address: string;
  city: string;
  state: string;
  country: string;
  starRating: number;
  amenities: string[];
  images: string[];
  rooms: Room[];
}

interface BookingForm {
  checkIn: string;
  checkOut: string;
  guests: number;
  guestName: string;
  guestEmail: string;
  guestPhone: string;
  specialRequests: string;
}

const HotelDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [hotel, setHotel] = useState<Hotel | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedRoom, setSelectedRoom] = useState<Room | null>(null);
  const [showBookingForm, setShowBookingForm] = useState(false);
  const [bookingForm, setBookingForm] = useState<BookingForm>({
    checkIn: '',
    checkOut: '',
    guests: 1,
    guestName: '',
    guestEmail: '',
    guestPhone: '',
    specialRequests: ''
  });
  const [bookingLoading, setBookingLoading] = useState(false);

  useEffect(() => {
    if (id) {
      loadHotelDetails();
    }
  }, [id]);

  const loadHotelDetails = async () => {
    try {
      setLoading(true);
      const response = await ApiService.getHotelById(parseInt(id!));
      if (response.success) {
        setHotel(response.data);
      } else {
        setError(response.message || 'Failed to load hotel details');
      }
    } catch (err) {
      setError('An error occurred while loading hotel details');
    } finally {
      setLoading(false);
    }
  };

  const handleBookRoom = (room: Room) => {
    setSelectedRoom(room);
    setShowBookingForm(true);
  };

  const handleBookingSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedRoom || !bookingForm.checkIn || !bookingForm.checkOut) {
      alert('Please fill in all required fields');
      return;
    }

    try {
      setBookingLoading(true);
      
      // Calculate total amount
      const checkIn = new Date(bookingForm.checkIn);
      const checkOut = new Date(bookingForm.checkOut);
      const nights = Math.ceil((checkOut.getTime() - checkIn.getTime()) / (1000 * 60 * 60 * 24));
      const totalAmount = selectedRoom.pricePerNight * nights;

      const reservationData = {
        roomId: selectedRoom.id,
        checkIn: bookingForm.checkIn,
        checkOut: bookingForm.checkOut,
        guests: bookingForm.guests,
        guestName: bookingForm.guestName,
        guestEmail: bookingForm.guestEmail,
        guestPhone: bookingForm.guestPhone,
        specialRequests: bookingForm.specialRequests,
        totalAmount: totalAmount,
        nights: nights
      };

      const response = await ApiService.createReservation(reservationData);
      
      if (response.success) {
        const bookingData = response.data;
        if (bookingData.status === 'PENDING_PAYMENT') {
          // Redirect to payment page
          alert(`Booking created! Please complete payment. Confirmation: ${bookingData.confirmationNumber}`);
          navigate(`/payment/${bookingData.id}`);
        } else {
          alert(`Booking confirmed! Confirmation number: ${bookingData.confirmationNumber}`);
          navigate('/reservations');
        }
        setShowBookingForm(false);
        setSelectedRoom(null);
        // Reload hotel details to update room availability
        loadHotelDetails();
      } else {
        alert('Booking failed: ' + response.message);
      }
    } catch (err) {
      alert('An error occurred while creating the booking');
    } finally {
      setBookingLoading(false);
    }
  };

  const renderStars = (rating: number) => {
    return Array.from({ length: 5 }, (_, i) => (
      <svg
        key={i}
        className={`w-5 h-5 ${i < rating ? 'text-amber-400' : 'text-gray-300'}`}
        fill="currentColor"
        viewBox="0 0 20 20"
      >
        <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
      </svg>
    ));
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-amber-600"></div>
          <p className="mt-4 text-gray-600">Loading hotel details...</p>
        </div>
      </div>
    );
  }

  if (error || !hotel) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <h1 className="text-2xl font-bold text-gray-900 mb-4">Error</h1>
          <p className="text-gray-600 mb-4">{error || 'Hotel not found'}</p>
          <button
            onClick={() => navigate('/hotels')}
            className="bg-amber-600 text-white px-6 py-2 rounded-lg hover:bg-amber-700"
          >
            Back to Hotels
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Hotel Header */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-8">
          <div className="flex items-start justify-between mb-4">
            <div>
              <h1 className="text-3xl font-bold text-gray-900 mb-2">{hotel.name}</h1>
              <div className="flex items-center mb-2">
                <div className="flex">{renderStars(hotel.starRating)}</div>
                <span className="ml-2 text-gray-600">({hotel.starRating} Star Hotel)</span>
              </div>
              <p className="text-gray-600">{hotel.address}, {hotel.city}, {hotel.state}</p>
            </div>
            <button
              onClick={() => navigate('/hotels')}
              className="bg-gray-200 text-gray-700 px-4 py-2 rounded-lg hover:bg-gray-300"
            >
              ← Back to Hotels
            </button>
          </div>
          
          <p className="text-gray-700 mb-4">{hotel.description}</p>
          
          {/* Amenities */}
          <div className="mb-4">
            <h3 className="text-lg font-semibold text-gray-900 mb-2">Hotel Amenities</h3>
            <div className="flex flex-wrap gap-2">
              {hotel.amenities.map((amenity, index) => (
                <span
                  key={index}
                  className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-blue-100 text-blue-800"
                >
                  {amenity}
                </span>
              ))}
            </div>
          </div>
        </div>

        {/* Available Rooms */}
        <div className="bg-white rounded-lg shadow-md p-6">
          <h2 className="text-2xl font-bold text-gray-900 mb-6">Available Rooms</h2>
          
          {hotel.rooms && hotel.rooms.length > 0 ? (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {hotel.rooms.map((room) => (
                <div key={room.id} className="border border-gray-200 rounded-lg overflow-hidden">
                  <div className="p-4">
                    <div className="flex items-center justify-between mb-2">
                      <h3 className="text-lg font-semibold text-gray-900">
                        Room {room.roomNumber}
                      </h3>
                      <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                        room.status === 'AVAILABLE' 
                          ? 'bg-green-100 text-green-800'
                          : 'bg-red-100 text-red-800'
                      }`}>
                        {room.status}
                      </span>
                    </div>
                    
                    <p className="text-gray-600 mb-2">{room.description}</p>
                    
                    <div className="grid grid-cols-2 gap-2 text-sm text-gray-600 mb-3">
                      <div>Type: {room.roomType}</div>
                      <div>Floor: {room.floorNumber}</div>
                      <div>Area: {room.area}m²</div>
                      <div>View: {room.view}</div>
                      <div>Capacity: {room.occupancy.adults} adults</div>
                    </div>
                    
                    <div className="mb-3">
                      <div className="text-sm text-gray-600 mb-1">Amenities:</div>
                      <div className="flex flex-wrap gap-1">
                        {room.amenities.map((amenity, index) => (
                          <span
                            key={index}
                            className="inline-block px-2 py-1 text-xs bg-gray-100 text-gray-700 rounded"
                          >
                            {amenity}
                          </span>
                        ))}
                      </div>
                    </div>
                    
                    <div className="flex items-center justify-between">
                      <div>
                        <div className="text-xl font-bold text-gray-900">
                          ₹{room.pricePerNight.toLocaleString()}
                        </div>
                        <div className="text-sm text-gray-600">per night</div>
                      </div>
                      
                      {room.status === 'AVAILABLE' ? (
                        <button
                          onClick={() => handleBookRoom(room)}
                          className="bg-amber-600 text-white px-4 py-2 rounded-lg hover:bg-amber-700 transition-colors"
                        >
                          Book Now
                        </button>
                      ) : (
                        <button
                          disabled
                          className="bg-gray-300 text-gray-500 px-4 py-2 rounded-lg cursor-not-allowed"
                        >
                          Not Available
                        </button>
                      )}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="text-center py-8">
              <p className="text-gray-600">No rooms available at this hotel.</p>
            </div>
          )}
        </div>
      </div>

      {/* Booking Modal */}
      {showBookingForm && selectedRoom && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg max-w-md w-full max-h-[90vh] overflow-y-auto">
            <div className="p-6">
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-lg font-semibold">Book Room {selectedRoom.roomNumber}</h3>
                <button
                  onClick={() => setShowBookingForm(false)}
                  className="text-gray-400 hover:text-gray-600"
                >
                  ✕
                </button>
              </div>
              
              <form onSubmit={handleBookingSubmit} className="space-y-4">
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Check-in Date
                    </label>
                    <input
                      type="date"
                      required
                      value={bookingForm.checkIn}
                      onChange={(e) => setBookingForm({ ...bookingForm, checkIn: e.target.value })}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-500"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Check-out Date
                    </label>
                    <input
                      type="date"
                      required
                      value={bookingForm.checkOut}
                      onChange={(e) => setBookingForm({ ...bookingForm, checkOut: e.target.value })}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-500"
                    />
                  </div>
                </div>
                
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Number of Guests
                  </label>
                  <select
                    value={bookingForm.guests}
                    onChange={(e) => setBookingForm({ ...bookingForm, guests: parseInt(e.target.value) })}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-500"
                  >
                    {Array.from({ length: selectedRoom.occupancy.adults }, (_, i) => (
                      <option key={i + 1} value={i + 1}>{i + 1} Guest{i > 0 ? 's' : ''}</option>
                    ))}
                  </select>
                </div>
                
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Guest Name
                  </label>
                  <input
                    type="text"
                    required
                    value={bookingForm.guestName}
                    onChange={(e) => setBookingForm({ ...bookingForm, guestName: e.target.value })}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-500"
                    placeholder="Enter your full name"
                  />
                </div>
                
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Email
                  </label>
                  <input
                    type="email"
                    required
                    value={bookingForm.guestEmail}
                    onChange={(e) => setBookingForm({ ...bookingForm, guestEmail: e.target.value })}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-500"
                    placeholder="Enter your email"
                  />
                </div>
                
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Phone Number
                  </label>
                  <input
                    type="tel"
                    required
                    value={bookingForm.guestPhone}
                    onChange={(e) => setBookingForm({ ...bookingForm, guestPhone: e.target.value })}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-500"
                    placeholder="Enter your phone number"
                  />
                </div>
                
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Special Requests (Optional)
                  </label>
                  <textarea
                    value={bookingForm.specialRequests}
                    onChange={(e) => setBookingForm({ ...bookingForm, specialRequests: e.target.value })}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-500"
                    rows={3}
                    placeholder="Any special requirements or requests"
                  />
                </div>
                
                {bookingForm.checkIn && bookingForm.checkOut && (
                  <div className="bg-gray-50 p-4 rounded-lg">
                    <div className="flex justify-between text-sm">
                      <span>Room Rate:</span>
                      <span>₹{selectedRoom.pricePerNight.toLocaleString()} per night</span>
                    </div>
                    <div className="flex justify-between text-sm">
                      <span>Number of Nights:</span>
                      <span>{Math.ceil((new Date(bookingForm.checkOut).getTime() - new Date(bookingForm.checkIn).getTime()) / (1000 * 60 * 60 * 24))}</span>
                    </div>
                    <div className="flex justify-between font-semibold text-lg border-t pt-2 mt-2">
                      <span>Total Amount:</span>
                      <span>₹{(selectedRoom.pricePerNight * Math.ceil((new Date(bookingForm.checkOut).getTime() - new Date(bookingForm.checkIn).getTime()) / (1000 * 60 * 60 * 24))).toLocaleString()}</span>
                    </div>
                  </div>
                )}
                
                <div className="flex space-x-3 pt-4">
                  <button
                    type="button"
                    onClick={() => setShowBookingForm(false)}
                    className="flex-1 bg-gray-200 text-gray-800 py-2 px-4 rounded-lg hover:bg-gray-300 transition-colors"
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    disabled={bookingLoading}
                    className="flex-1 bg-amber-600 text-white py-2 px-4 rounded-lg hover:bg-amber-700 transition-colors disabled:opacity-50"
                  >
                    {bookingLoading ? 'Booking...' : 'Confirm Booking'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default HotelDetailPage;
