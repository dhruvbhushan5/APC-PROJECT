import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';

const HomePage: React.FC = () => {
  const { isAuthenticated, user } = useAuth();

  return (
    <div className="bg-white">
      {/* Hero Section */}
      <div className="relative bg-gradient-to-r from-amber-600 to-orange-600 overflow-hidden">
        <div className="absolute inset-0">
          <img
            src="https://images.unsplash.com/photo-1566073771259-6a8506099945?ixlib=rb-4.0.3&auto=format&fit=crop&w=2000&q=80"
            alt="Luxury Hotel"
            className="w-full h-full object-cover opacity-30"
          />
        </div>
        <div className="relative max-w-7xl mx-auto py-24 px-4 sm:py-32 sm:px-6 lg:px-8">
          <h1 className="text-4xl font-extrabold tracking-tight text-white sm:text-5xl lg:text-6xl">
            Experience Luxury
            <span className="block text-amber-200">Beyond Expectations</span>
          </h1>
          <p className="mt-6 max-w-3xl text-xl text-amber-100">
            Discover the world's finest hotels and resorts. From boutique hideaways to grand luxury destinations, 
            create memories that last a lifetime with LuxStay's curated collection.
          </p>
          <div className="mt-10 flex flex-col sm:flex-row gap-4">
            <Link
              to="/hotels"
              className="inline-flex items-center px-8 py-3 border border-transparent text-base font-medium rounded-lg text-amber-600 bg-white hover:bg-amber-50 transition-all duration-200 shadow-lg"
            >
              Explore Hotels
              <svg className="ml-2 -mr-1 w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M14 5l7 7m0 0l-7 7m7-7H3" />
              </svg>
            </Link>
            {!isAuthenticated && (
              <Link
                to="/register"
                className="inline-flex items-center px-8 py-3 border-2 border-white text-base font-medium rounded-lg text-white hover:bg-white hover:text-amber-600 transition-all duration-200"
              >
                Join LuxStay
              </Link>
            )}
          </div>
        </div>
      </div>

      {/* Welcome Message for Authenticated Users */}
      {isAuthenticated && (
        <div className="bg-amber-50 border-b border-amber-200">
          <div className="max-w-7xl mx-auto py-4 px-4 sm:px-6 lg:px-8">
            <div className="flex items-center justify-between">
              <div className="flex items-center">
                <div className="flex-shrink-0">
                  <svg className="h-8 w-8 text-amber-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                  </svg>
                </div>
                <div className="ml-3">
                  <p className="text-sm font-medium text-amber-800">
                    Welcome back, {user?.firstName}! Ready for your next luxury getaway?
                  </p>
                </div>
              </div>
              <div className="flex space-x-4">
                <Link
                  to="/reservations"
                  className="text-sm font-medium text-amber-600 hover:text-amber-500"
                >
                  My Reservations
                </Link>
                <Link
                  to="/hotels"
                  className="text-sm font-medium text-amber-600 hover:text-amber-500"
                >
                  Book Now
                </Link>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Features Section */}
      <div className="py-16 bg-gray-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center">
            <h2 className="text-3xl font-extrabold text-gray-900 sm:text-4xl">
              Why Choose LuxStay?
            </h2>
            <p className="mt-4 max-w-3xl mx-auto text-xl text-gray-600">
              Experience the difference with our premium hotel booking platform
            </p>
          </div>

          <div className="mt-16 grid grid-cols-1 gap-8 sm:grid-cols-2 lg:grid-cols-3">
            {/* Feature 1 */}
            <div className="bg-white rounded-xl shadow-lg p-8 hover:shadow-xl transition-shadow">
              <div className="w-12 h-12 bg-amber-100 rounded-lg flex items-center justify-center mb-6">
                <svg className="w-6 h-6 text-amber-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 3v4M3 5h4M6 17v4m-2-2h4m5-16l2.286 6.857L21 12l-5.714 2.143L13 21l-2.286-6.857L5 12l5.714-2.143L13 3z" />
                </svg>
              </div>
              <h3 className="text-xl font-semibold text-gray-900 mb-3">Luxury Collection</h3>
              <p className="text-gray-600">
                Handpicked selection of the world's finest hotels and resorts, each offering unparalleled luxury and service.
              </p>
            </div>

            {/* Feature 2 */}
            <div className="bg-white rounded-xl shadow-lg p-8 hover:shadow-xl transition-shadow">
              <div className="w-12 h-12 bg-amber-100 rounded-lg flex items-center justify-center mb-6">
                <svg className="w-6 h-6 text-amber-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
                </svg>
              </div>
              <h3 className="text-xl font-semibold text-gray-900 mb-3">Secure Booking</h3>
              <p className="text-gray-600">
                Your personal information and payments are protected with industry-leading security measures and encryption.
              </p>
            </div>

            {/* Feature 3 */}
            <div className="bg-white rounded-xl shadow-lg p-8 hover:shadow-xl transition-shadow">
              <div className="w-12 h-12 bg-amber-100 rounded-lg flex items-center justify-center mb-6">
                <svg className="w-6 h-6 text-amber-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M18.364 5.636l-3.536 3.536m0 5.656l3.536 3.536M9.172 9.172L5.636 5.636m3.536 9.192L5.636 18.364M12 12h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              <h3 className="text-xl font-semibold text-gray-900 mb-3">24/7 Support</h3>
              <p className="text-gray-600">
                Our dedicated concierge team is available around the clock to assist with your travel needs and reservations.
              </p>
            </div>

            {/* Feature 4 */}
            <div className="bg-white rounded-xl shadow-lg p-8 hover:shadow-xl transition-shadow">
              <div className="w-12 h-12 bg-amber-100 rounded-lg flex items-center justify-center mb-6">
                <svg className="w-6 h-6 text-amber-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1" />
                </svg>
              </div>
              <h3 className="text-xl font-semibold text-gray-900 mb-3">Best Price Guarantee</h3>
              <p className="text-gray-600">
                We guarantee the best rates for your luxury hotel bookings, or we'll match the difference.
              </p>
            </div>

            {/* Feature 5 */}
            <div className="bg-white rounded-xl shadow-lg p-8 hover:shadow-xl transition-shadow">
              <div className="w-12 h-12 bg-amber-100 rounded-lg flex items-center justify-center mb-6">
                <svg className="w-6 h-6 text-amber-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 10V3L4 14h7v7l9-11h-7z" />
                </svg>
              </div>
              <h3 className="text-xl font-semibold text-gray-900 mb-3">Instant Confirmation</h3>
              <p className="text-gray-600">
                Receive immediate booking confirmation and enjoy the peace of mind that comes with guaranteed reservations.
              </p>
            </div>

            {/* Feature 6 */}
            <div className="bg-white rounded-xl shadow-lg p-8 hover:shadow-xl transition-shadow">
              <div className="w-12 h-12 bg-amber-100 rounded-lg flex items-center justify-center mb-6">
                <svg className="w-6 h-6 text-amber-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                </svg>
              </div>
              <h3 className="text-xl font-semibold text-gray-900 mb-3">Personalized Experience</h3>
              <p className="text-gray-600">
                Tailored recommendations and exclusive perks based on your preferences and travel history.
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* CTA Section */}
      <div className="bg-gradient-to-r from-amber-600 to-orange-600">
        <div className="max-w-7xl mx-auto py-16 px-4 sm:px-6 lg:px-8">
          <div className="bg-white bg-opacity-10 backdrop-blur-sm rounded-2xl p-8 text-center">
            <h2 className="text-3xl font-extrabold text-white sm:text-4xl">
              Ready to Experience Luxury?
            </h2>
            <p className="mt-4 text-xl text-amber-100">
              Start your journey with LuxStay today and discover your perfect getaway.
            </p>
            <div className="mt-8">
              <Link
                to="/hotels"
                className="inline-flex items-center px-8 py-4 border border-transparent text-lg font-medium rounded-lg text-amber-600 bg-white hover:bg-amber-50 transition-all duration-200 shadow-lg"
              >
                Browse Hotels
                <svg className="ml-2 -mr-1 w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M14 5l7 7m0 0l-7 7m7-7H3" />
                </svg>
              </Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default HomePage;
