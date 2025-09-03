import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';

// Components
import Header from './components/layout/Header';
import Footer from './components/layout/Footer';
import LoginPage from './components/auth/LoginPage';
import RegisterPage from './components/auth/RegisterPage';
import HomePage from './components/pages/HomePage';
import HotelsPage from './components/pages/HotelsPage';
import HotelDetailPage from './components/pages/HotelDetailPage';
import BookingPage from './components/pages/BookingPage';
import ProfilePage from './components/pages/ProfilePage';
import ReservationsPage from './components/pages/ReservationsPage';
import PaymentPage from './components/pages/PaymentPage';
import ProtectedRoute from './components/auth/ProtectedRoute';

const App: React.FC = () => {
  return (
    <AuthProvider>
      <Router>
        <div className="min-h-screen bg-gray-50 flex flex-col">
          <Header />
          
          <main className="flex-grow">
            <Routes>
              {/* Public Routes */}
              <Route path="/" element={<HomePage />} />
              <Route path="/login" element={<LoginPage />} />
              <Route path="/register" element={<RegisterPage />} />
              <Route path="/hotels" element={<HotelsPage />} />
              <Route path="/hotels/:id" element={<HotelDetailPage />} />
              
              {/* Protected Routes */}
              <Route path="/booking" element={
                <ProtectedRoute>
                  <BookingPage />
                </ProtectedRoute>
              } />
              <Route path="/profile" element={
                <ProtectedRoute>
                  <ProfilePage />
                </ProtectedRoute>
              } />
              <Route path="/reservations" element={
                <ProtectedRoute>
                  <ReservationsPage />
                </ProtectedRoute>
              } />
              <Route path="/payment/:bookingId" element={<PaymentPage />} />
              
              {/* Redirect unknown routes to home */}
              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </main>
          
          <Footer />
        </div>
      </Router>
    </AuthProvider>
  );
};

export default App;
