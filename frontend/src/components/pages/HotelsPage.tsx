import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import ApiService from '../../services/apiService';

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
  priceRange: { min: number; max: number };
  totalRooms: number;
  availableRooms: number;
}

const HotelsPage: React.FC = () => {
  const [hotels, setHotels] = useState<Hotel[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadHotels();
  }, []);

  const loadHotels = async () => {
    try {
      setLoading(true);
      const response = await ApiService.getHotels();
      if (response.success) {
        setHotels(response.data);
      } else {
        setError(response.message || 'Failed to load hotels');
      }
    } catch (err) {
      setError('An error occurred while loading hotels');
    } finally {
      setLoading(false);
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
          <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-amber-600 mx-auto"></div>
          <p className="mt-4 text-lg text-gray-600">Loading luxury hotels...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <svg className="mx-auto h-12 w-12 text-red-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
          </svg>
          <h3 className="mt-2 text-sm font-medium text-gray-900">Error loading hotels</h3>
          <p className="mt-1 text-sm text-gray-500">{error}</p>
          <div className="mt-6">
            <button
              onClick={loadHotels}
              className="inline-flex items-center px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-amber-600 hover:bg-amber-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-amber-500"
            >
              Try Again
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-white shadow">
        <div className="max-w-7xl mx-auto py-8 px-4 sm:px-6 lg:px-8">
          <div className="text-center">
            <h1 className="text-3xl font-bold text-gray-900 sm:text-4xl">
              Luxury Hotels & Resorts
            </h1>
            <p className="mt-4 text-xl text-gray-600">
              Discover exceptional accommodations for your perfect getaway
            </p>
          </div>
        </div>
      </div>

      {/* Hotels Grid */}
      <div className="max-w-7xl mx-auto py-12 px-4 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 gap-8 sm:grid-cols-2 lg:grid-cols-3">
          {hotels.map((hotel) => (
            <div key={hotel.id} className="bg-white rounded-xl shadow-lg overflow-hidden hover:shadow-xl transition-shadow">
              {/* Hotel Image */}
              <div className="relative h-64 bg-gray-300">
                <img
                  src={hotel.images[0] || 'https://images.unsplash.com/photo-1566073771259-6a8506099945'}
                  alt={hotel.name}
                  className="w-full h-full object-cover"
                />
                <div className="absolute top-4 right-4 bg-white bg-opacity-90 backdrop-blur-sm rounded-lg px-3 py-1">
                  <div className="flex items-center space-x-1">
                    {renderStars(hotel.starRating)}
                  </div>
                </div>
              </div>

              {/* Hotel Details */}
              <div className="p-6">
                <div className="flex items-start justify-between">
                  <div className="flex-1">
                    <h3 className="text-xl font-semibold text-gray-900 mb-2">{hotel.name}</h3>
                    <p className="text-sm text-gray-600 mb-2">
                      {hotel.city}, {hotel.state}, {hotel.country}
                    </p>
                    <p className="text-gray-700 text-sm line-clamp-3 mb-4">
                      {hotel.description}
                    </p>
                  </div>
                </div>

                {/* Amenities */}
                <div className="mb-4">
                  <div className="flex flex-wrap gap-2">
                    {hotel.amenities.slice(0, 3).map((amenity, index) => (
                      <span
                        key={index}
                        className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-amber-100 text-amber-800"
                      >
                        {amenity}
                      </span>
                    ))}
                    {hotel.amenities.length > 3 && (
                      <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-800">
                        +{hotel.amenities.length - 3} more
                      </span>
                    )}
                  </div>
                </div>

                {/* Price and Availability */}
                <div className="flex items-center justify-between mb-4">
                  <div>
                    <div className="text-2xl font-bold text-gray-900">
                      ₹{hotel.priceRange.min.toLocaleString()}
                    </div>
                    <div className="text-sm text-gray-600">per night</div>
                  </div>
                  <div className="text-right">
                    <div className="text-sm text-gray-600">Available Rooms</div>
                    <div className="text-lg font-semibold text-green-600">
                      {hotel.availableRooms}
                    </div>
                  </div>
                </div>

                {/* Actions */}
                <div className="flex space-x-3">
                  <Link
                    to={`/hotels/${hotel.id}`}
                    className="flex-1 bg-amber-600 text-white text-center py-2 px-4 rounded-lg font-medium hover:bg-amber-700 transition-colors"
                  >
                    View Details
                  </Link>
                  <Link
                    to={`/hotels/${hotel.id}`}
                    className="flex-1 border-2 border-amber-600 text-amber-600 text-center py-2 px-4 rounded-lg font-medium hover:bg-amber-50 transition-colors"
                  >
                    Book Now
                  </Link>
                </div>
              </div>
            </div>
          ))}
        </div>

        {hotels.length === 0 && (
          <div className="text-center py-12">
            <svg className="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
            </svg>
            <h3 className="mt-2 text-sm font-medium text-gray-900">No hotels found</h3>
            <p className="mt-1 text-sm text-gray-500">We couldn't find any hotels matching your criteria.</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default HotelsPage;
