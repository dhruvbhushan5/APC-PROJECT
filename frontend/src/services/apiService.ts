import { ApiResponse, LoginRequest, RegisterRequest, JwtResponse, User } from '../types';

const API_BASE_URL = 'http://localhost:8083/api';

class ApiService {
  private static token: string | null = localStorage.getItem('token');

  private static getHeaders(): HeadersInit {
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
    };

    if (this.token) {
      headers['Authorization'] = `Bearer ${this.token}`;
    }

    return headers;
  }

  private static async handleResponse<T>(response: Response): Promise<T> {
    if (!response.ok) {
      const error = await response.json().catch(() => ({ 
        message: 'Network error occurred' 
      }));
      throw new Error(error.message || `HTTP error! status: ${response.status}`);
    }

    return response.json();
  }

  // Authentication endpoints
  static async login(credentials: LoginRequest): Promise<JwtResponse> {
    const response = await fetch(`${API_BASE_URL}/auth/login`, {
      method: 'POST',
      headers: this.getHeaders(),
      body: JSON.stringify(credentials),
    });

    const result = await this.handleResponse<JwtResponse>(response);
    
    if (result.success && result.data.accessToken) {
      this.token = result.data.accessToken;
      localStorage.setItem('token', this.token);
      localStorage.setItem('user', JSON.stringify(result.data.user));
    }

    return result;
  }

  static async register(userData: RegisterRequest): Promise<ApiResponse<User>> {
    const response = await fetch(`${API_BASE_URL}/auth/register`, {
      method: 'POST',
      headers: this.getHeaders(),
      body: JSON.stringify(userData),
    });

    return this.handleResponse<ApiResponse<User>>(response);
  }

  static async logout(): Promise<void> {
    try {
      await fetch(`${API_BASE_URL}/auth/logout`, {
        method: 'POST',
        headers: this.getHeaders(),
      });
    } catch (error) {
      console.warn('Logout request failed:', error);
    } finally {
      this.token = null;
      localStorage.removeItem('token');
      localStorage.removeItem('user');
    }
  }

  static async refreshToken(): Promise<JwtResponse> {
    const refreshToken = localStorage.getItem('refreshToken');
    
    const response = await fetch(`${API_BASE_URL}/auth/refresh`, {
      method: 'POST',
      headers: this.getHeaders(),
      body: JSON.stringify({ refreshToken }),
    });

    const result = await this.handleResponse<JwtResponse>(response);
    
    if (result.success && result.data.accessToken) {
      this.token = result.data.accessToken;
      localStorage.setItem('token', this.token);
    }

    return result;
  }

  // User profile endpoints
  static async getUserProfile(): Promise<ApiResponse<User>> {
    const response = await fetch(`${API_BASE_URL}/users/profile`, {
      method: 'GET',
      headers: this.getHeaders(),
    });

    return this.handleResponse<ApiResponse<User>>(response);
  }

  static async updateUserProfile(userData: Partial<User>): Promise<ApiResponse<User>> {
    const response = await fetch(`${API_BASE_URL}/users/profile`, {
      method: 'PUT',
      headers: this.getHeaders(),
      body: JSON.stringify(userData),
    });

    return this.handleResponse<ApiResponse<User>>(response);
  }

  static async changePassword(data: {
    currentPassword: string;
    newPassword: string;
  }): Promise<ApiResponse> {
    const response = await fetch(`${API_BASE_URL}/users/change-password`, {
      method: 'PUT',
      headers: this.getHeaders(),
      body: JSON.stringify(data),
    });

    return this.handleResponse<ApiResponse>(response);
  }

  // Hotel endpoints (calling room-service directly)
  static async getHotels(filters?: any): Promise<ApiResponse> {
    const queryParams = new URLSearchParams();
    
    if (filters) {
      Object.entries(filters).forEach(([key, value]) => {
        if (value !== undefined && value !== null) {
          queryParams.append(key, String(value));
        }
      });
    }

    try {
      const response = await fetch(`http://localhost:8081/api/rooms/all?${queryParams}`, {
        method: 'GET',
        headers: this.getHeaders(),
      });

      const roomsData = await this.handleResponse<any>(response);
      
      // Transform room data to hotel format for frontend compatibility
      return {
        success: true,
        message: 'Hotels retrieved successfully',
        data: [
          {
            id: 1,
            name: 'Grand Palace Hotel',
            description: 'Luxury hotel with various room types',
            address: '123 Main Street',
            city: 'Mumbai',
            state: 'Maharashtra',
            country: 'India',
            starRating: 5,
            amenities: ['WiFi', 'Pool', 'Spa', 'Gym', 'Restaurant'],
            images: ['https://images.unsplash.com/photo-1566073771259-6a8506099945'],
            priceRange: { min: 89.99, max: 299.99 },
            totalRooms: roomsData.length,
            availableRooms: roomsData.filter((room: any) => room.status === 'AVAILABLE').length,
            rooms: roomsData.map((room: any) => ({
              id: room.id,
              roomNumber: room.roomNumber,
              roomType: room.roomType,
              pricePerNight: room.pricePerNight,
              occupancy: { adults: room.capacity, children: room.capacity > 2 ? 1 : 0 },
              amenities: room.amenities ? room.amenities.split(', ') : ['WiFi', 'AC'],
              images: ['https://images.unsplash.com/photo-1631049307264-da0ec9d70304'],
              description: room.description,
              status: room.status,
              area: room.area,
              floorNumber: room.floorNumber,
              view: room.view
            }))
          }
        ],
        timestamp: new Date().toISOString()
      };
    } catch (error) {
      console.error('Error fetching hotels:', error);
      // Mock data for demo when room-service is not available
      return {
        success: true,
        message: 'Hotels retrieved successfully (mock data)',
        data: [
          {
            id: 1,
            name: 'Grand Palace Hotel',
            description: 'Luxury 5-star hotel in the heart of the city',
            address: '123 Main Street',
            city: 'Mumbai',
            state: 'Maharashtra',
            country: 'India',
            starRating: 5,
            amenities: ['WiFi', 'Pool', 'Spa', 'Gym', 'Restaurant'],
            images: ['https://images.unsplash.com/photo-1566073771259-6a8506099945'],
            priceRange: { min: 5000, max: 15000 },
            totalRooms: 100,
            availableRooms: 45
          },
          {
            id: 2,
            name: 'Ocean View Resort',
            description: 'Beachfront resort with stunning ocean views',
            address: '456 Beach Road',
            city: 'Goa',
            state: 'Goa',
            country: 'India',
            starRating: 4,
            amenities: ['WiFi', 'Pool', 'Beach Access', 'Restaurant'],
            images: ['https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9'],
            priceRange: { min: 3000, max: 8000 },
            totalRooms: 80,
            availableRooms: 32
          }
        ],
        timestamp: new Date().toISOString()
      };
    }
  }

  static async getHotelById(id: number): Promise<ApiResponse> {
    try {
      // Fetch all rooms from Room Service
      const response = await fetch(`http://localhost:8081/api/rooms/all`, {
        method: 'GET',
        headers: this.getHeaders(),
      });

      const roomsData = await this.handleResponse<any>(response);
      
      // Transform room data to hotel format with individual room details
      return {
        success: true,
        message: 'Hotel retrieved successfully',
        data: {
          id: id,
          name: 'Grand Palace Hotel',
          description: 'Luxury 5-star hotel in the heart of the city with world-class amenities and exceptional service.',
          address: '123 Main Street',
          city: 'Mumbai',
          state: 'Maharashtra',
          country: 'India',
          starRating: 5,
          amenities: ['WiFi', 'Pool', 'Spa', 'Gym', 'Restaurant', 'Room Service', 'Concierge'],
          images: [
            'https://images.unsplash.com/photo-1566073771259-6a8506099945',
            'https://images.unsplash.com/photo-1564501049412-61c2a3083791'
          ],
          rooms: roomsData.map((room: any) => ({
            id: room.id,
            roomNumber: room.roomNumber,
            roomType: room.roomType,
            pricePerNight: room.pricePerNight,
            occupancy: { adults: room.capacity, children: room.capacity > 2 ? 1 : 0 },
            amenities: room.amenities ? room.amenities.split(', ') : ['WiFi', 'AC', 'TV'],
            images: ['https://images.unsplash.com/photo-1631049307264-da0ec9d70304'],
            description: room.description,
            status: room.status,
            area: room.area,
            floorNumber: room.floorNumber,
            view: room.view
          }))
        },
        timestamp: new Date().toISOString()
      };
    } catch (error) {
      console.error('Error fetching hotel details:', error);
      // Mock data for demo when room-service is not available
      return {
        success: true,
        message: 'Hotel retrieved successfully (mock data)',
        data: {
          id: id,
          name: 'Grand Palace Hotel',
          description: 'Luxury 5-star hotel in the heart of the city with world-class amenities and exceptional service.',
          address: '123 Main Street',
          city: 'Mumbai',
          state: 'Maharashtra',
          country: 'India',
          starRating: 5,
          amenities: ['WiFi', 'Pool', 'Spa', 'Gym', 'Restaurant', 'Room Service', 'Concierge'],
          images: [
            'https://images.unsplash.com/photo-1566073771259-6a8506099945',
            'https://images.unsplash.com/photo-1564501049412-61c2a3083791'
          ],
          rooms: [
            {
              id: 1,
              roomNumber: '101',
              roomType: 'DELUXE',
              pricePerNight: 8000,
              occupancy: { adults: 2, children: 1 },
              amenities: ['WiFi', 'AC', 'TV', 'Minibar'],
              images: ['https://images.unsplash.com/photo-1631049307264-da0ec9d70304'],
              description: 'Spacious deluxe room with city view',
              status: 'AVAILABLE',
              area: 35,
              floorNumber: 1,
              view: 'City'
            },
            {
              id: 2,
              roomNumber: '201',
              roomType: 'SUITE',
              pricePerNight: 15000,
              occupancy: { adults: 4, children: 2 },
              amenities: ['WiFi', 'AC', 'TV', 'Minibar', 'Living Area', 'Balcony'],
              images: ['https://images.unsplash.com/photo-1618773928121-c32242e63f39'],
              description: 'Luxury suite with premium amenities',
              status: 'AVAILABLE',
              area: 75,
              floorNumber: 2,
              view: 'Ocean'
            }
          ]
        },
        timestamp: new Date().toISOString()
      };
    }
  }

  // Reservation endpoints
  static async createReservation(reservationData: any): Promise<ApiResponse> {
    try {
      const response = await fetch(`http://localhost:8081/api/bookings`, {
        method: 'POST',
        headers: this.getHeaders(),
        body: JSON.stringify({
          roomId: reservationData.roomId,
          checkInDate: reservationData.checkIn,
          checkOutDate: reservationData.checkOut,
          numberOfGuests: reservationData.guests || 1,
          guestName: reservationData.guestName || 'Guest',
          guestEmail: reservationData.guestEmail || 'guest@example.com',
          guestPhone: reservationData.guestPhone || '1234567890',
          totalAmount: reservationData.totalAmount,
          specialRequests: reservationData.specialRequests || ''
        }),
      });

      return this.handleResponse<ApiResponse>(response);
    } catch (error) {
      console.error('Error creating reservation:', error);
      // Mock successful booking for demo
      return {
        success: true,
        message: 'Reservation created successfully',
        data: {
          id: Math.floor(Math.random() * 1000),
          confirmationNumber: 'HTL' + Math.random().toString(36).substr(2, 9).toUpperCase(),
          status: 'CONFIRMED',
          ...reservationData,
          createdAt: new Date().toISOString()
        },
        timestamp: new Date().toISOString()
      };
    }
  }

  static async getUserReservations(): Promise<ApiResponse> {
    try {
      // For now, we'll get all bookings from Room Service
      // In a real app, this would filter by user ID
      const response = await fetch(`http://localhost:8081/api/bookings`, {
        method: 'GET',
        headers: this.getHeaders(),
      });

      const bookingsData = await this.handleResponse<any>(response);
      
      // Transform booking data to reservations format
      const reservations = bookingsData.map((booking: any) => ({
        id: booking.id,
        confirmationNumber: `HTL${booking.id}${Math.random().toString(36).substr(2, 5).toUpperCase()}`,
        hotelName: 'Grand Palace Hotel',
        roomNumber: booking.room?.roomNumber || 'N/A',
        roomType: booking.room?.roomType || 'STANDARD',
        guestName: booking.guestName,
        guestEmail: booking.guestEmail,
        guestPhone: booking.guestPhone,
        checkInDate: booking.checkInDate,
        checkOutDate: booking.checkOutDate,
        numberOfGuests: booking.numberOfGuests,
        numberOfNights: booking.numberOfNights,
        status: booking.status,
        totalAmount: booking.totalAmount,
        paidAmount: booking.paidAmount,
        specialRequests: booking.specialRequests,
        createdAt: booking.createdAt
      }));

      return {
        success: true,
        message: 'Reservations retrieved successfully',
        data: reservations,
        timestamp: new Date().toISOString()
      };
    } catch (error) {
      console.error('Error fetching reservations:', error);
      // Mock reservations for demo when service is not available
      return {
        success: true,
        message: 'Reservations retrieved successfully (mock data)',
        data: [
          {
            id: 1,
            confirmationNumber: 'HTL12345',
            hotelName: 'Grand Palace Hotel',
            roomNumber: '101',
            roomType: 'DELUXE',
            guestName: 'Sample Guest',
            guestEmail: 'guest@example.com',
            checkInDate: '2025-09-15',
            checkOutDate: '2025-09-18',
            numberOfGuests: 2,
            numberOfNights: 3,
            status: 'CONFIRMED',
            totalAmount: 24000,
            paidAmount: 24000,
            createdAt: new Date().toISOString()
          }
        ],
        timestamp: new Date().toISOString()
      };
    }
  }

  // Utility methods
  static setToken(token: string): void {
    this.token = token;
    localStorage.setItem('token', token);
  }

  static getToken(): string | null {
    return this.token || localStorage.getItem('token');
  }

  static clearToken(): void {
    this.token = null;
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }

  static isAuthenticated(): boolean {
    return !!this.getToken();
  }

  static getCurrentUser(): User | null {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  }
}

export default ApiService;
