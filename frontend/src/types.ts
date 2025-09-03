// User types
export interface User {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber?: string;
  address?: string;
  emailVerified: boolean;
  enabled: boolean;
  createdAt: string;
  roles: UserRole[];
}

export interface UserRole {
  id: string;
  role: 'ADMIN' | 'MANAGER' | 'STAFF' | 'CUSTOMER' | 'GUEST';
  active: boolean;
  assignedAt: string;
}

// Authentication types
export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  phoneNumber?: string;
  address?: string;
}

export interface JwtResponse {
  success: boolean;
  message: string;
  data: {
    accessToken: string;
    refreshToken: string;
    tokenType: string;
    expiresIn: number;
    user: User;
  };
}

// Hotel types
export interface Hotel {
  id: number;
  name: string;
  description: string;
  address: string;
  city: string;
  state: string;
  country: string;
  zipCode: string;
  phoneNumber: string;
  email: string;
  starRating: number;
  amenities: string[];
  images: string[];
  checkInTime: string;
  checkOutTime: string;
  cancellationPolicy: string;
  totalRooms: number;
  availableRooms: number;
  priceRange: {
    min: number;
    max: number;
  };
  location: {
    latitude: number;
    longitude: number;
  };
  createdAt: string;
  updatedAt: string;
}

// Room types
export interface Room {
  id: number;
  hotelId: number;
  roomNumber: string;
  roomType: RoomType;
  pricePerNight: number;
  occupancy: {
    adults: number;
    children: number;
  };
  bedType: string;
  bedCount: number;
  size: number; // in square feet
  amenities: string[];
  images: string[];
  description: string;
  isAvailable: boolean;
  status: 'AVAILABLE' | 'OCCUPIED' | 'MAINTENANCE' | 'OUT_OF_ORDER';
  createdAt: string;
  updatedAt: string;
}

export type RoomType = 'STANDARD' | 'DELUXE' | 'SUITE' | 'PRESIDENTIAL_SUITE' | 'FAMILY' | 'SINGLE' | 'DOUBLE';

// Booking types
export interface Reservation {
  id: number;
  userId: string;
  hotelId: number;
  roomId: number;
  checkInDate: string;
  checkOutDate: string;
  guests: {
    adults: number;
    children: number;
  };
  specialRequests?: string;
  totalAmount: number;
  status: ReservationStatus;
  paymentStatus: PaymentStatus;
  confirmationNumber: string;
  createdAt: string;
  updatedAt: string;
  // Populated fields
  user?: User;
  hotel?: Hotel;
  room?: Room;
  payment?: Payment;
}

export type ReservationStatus = 'PENDING' | 'CONFIRMED' | 'CHECKED_IN' | 'CHECKED_OUT' | 'CANCELLED' | 'NO_SHOW';
export type PaymentStatus = 'PENDING' | 'COMPLETED' | 'FAILED' | 'REFUNDED' | 'PARTIALLY_REFUNDED';

// Payment types
export interface Payment {
  id: string;
  reservationId: number;
  amount: number;
  currency: string;
  paymentMethod: PaymentMethod;
  status: PaymentStatus;
  transactionId: string;
  paymentDate: string;
  refundAmount?: number;
  refundDate?: string;
  createdAt: string;
  updatedAt: string;
}

export interface PaymentMethod {
  type: 'CREDIT_CARD' | 'DEBIT_CARD' | 'UPI' | 'NET_BANKING' | 'WALLET';
  cardNumber?: string; // Last 4 digits
  cardType?: string; // Visa, MasterCard, etc.
  bankName?: string;
  upiId?: string;
}

export interface PaymentRequest {
  reservationId: number;
  amount: number;
  currency: string;
  paymentMethod: PaymentMethod;
  cardDetails?: {
    number: string;
    expiryMonth: number;
    expiryYear: number;
    cvv: string;
    holderName: string;
  };
}

// API Response types
export interface ApiResponse<T = any> {
  success: boolean;
  message: string;
  data?: T;
  error?: string;
  timestamp: string;
}

// Filter and search types
export interface HotelFilters {
  city?: string;
  state?: string;
  checkInDate?: string;
  checkOutDate?: string;
  guests?: number;
  priceMin?: number;
  priceMax?: number;
  starRating?: number[];
  amenities?: string[];
  roomType?: RoomType[];
  sortBy?: 'price' | 'rating' | 'distance' | 'popularity';
  sortOrder?: 'asc' | 'desc';
  page?: number;
  limit?: number;
}

export interface SearchResult<T> {
  data: T[];
  total: number;
  page: number;
  limit: number;
  totalPages: number;
}

// Booking flow types
export interface BookingState {
  step: number;
  hotel?: Hotel;
  room?: Room;
  checkInDate?: string;
  checkOutDate?: string;
  guests: {
    adults: number;
    children: number;
  };
  guestDetails?: {
    firstName: string;
    lastName: string;
    email: string;
    phoneNumber: string;
    specialRequests?: string;
  };
  paymentMethod?: PaymentMethod;
  totalAmount: number;
  taxes: number;
  discounts: number;
}

// Dashboard types
export interface DashboardStats {
  totalReservations: number;
  totalRevenue: number;
  occupancyRate: number;
  averageRating: number;
  recentBookings: Reservation[];
  popularHotels: Hotel[];
  revenueChart: {
    labels: string[];
    values: number[];
  };
  monthlyStats: {
    month: string;
    bookings: number;
    revenue: number;
    occupancy: number;
  }[];
}

// Error types
export interface ErrorState {
  message: string;
  code?: string;
  details?: any;
}

// Loading states
export interface LoadingState {
  [key: string]: boolean;
}

// Form types
export interface FormErrors {
  [key: string]: string;
}

// Date range picker
export interface DateRange {
  from: Date | undefined;
  to: Date | undefined;
}

// Navigation types
export interface NavigationItem {
  label: string;
  path: string;
  icon?: string;
  requiresAuth?: boolean;
  roles?: UserRole['role'][];
}

// Notification types
export interface Notification {
  id: string;
  type: 'success' | 'error' | 'warning' | 'info';
  title: string;
  message: string;
  autoClose?: boolean;
  duration?: number;
}

// Theme types
export interface Theme {
  colors: {
    primary: string;
    secondary: string;
    success: string;
    error: string;
    warning: string;
    info: string;
    background: string;
    surface: string;
    text: string;
  };
  spacing: {
    xs: string;
    sm: string;
    md: string;
    lg: string;
    xl: string;
  };
  borderRadius: {
    sm: string;
    md: string;
    lg: string;
  };
}
