import React, { createContext, useContext, useReducer, useEffect, ReactNode } from 'react';
import { User, JwtResponse } from '../types';
import ApiService from '../services/apiService';

interface AuthState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
}

type AuthAction =
  | { type: 'AUTH_START' }
  | { type: 'AUTH_SUCCESS'; payload: { user: User; token: string } }
  | { type: 'AUTH_FAILURE'; payload: string }
  | { type: 'LOGOUT' }
  | { type: 'CLEAR_ERROR' }
  | { type: 'UPDATE_USER'; payload: User };

const initialState: AuthState = {
  user: null,
  token: null,
  isAuthenticated: false,
  isLoading: false,
  error: null,
};

const authReducer = (state: AuthState, action: AuthAction): AuthState => {
  switch (action.type) {
    case 'AUTH_START':
      return {
        ...state,
        isLoading: true,
        error: null,
      };
    case 'AUTH_SUCCESS':
      return {
        ...state,
        user: action.payload.user,
        token: action.payload.token,
        isAuthenticated: true,
        isLoading: false,
        error: null,
      };
    case 'AUTH_FAILURE':
      return {
        ...state,
        user: null,
        token: null,
        isAuthenticated: false,
        isLoading: false,
        error: action.payload,
      };
    case 'LOGOUT':
      return {
        ...state,
        user: null,
        token: null,
        isAuthenticated: false,
        isLoading: false,
        error: null,
      };
    case 'CLEAR_ERROR':
      return {
        ...state,
        error: null,
      };
    case 'UPDATE_USER':
      return {
        ...state,
        user: action.payload,
      };
    default:
      return state;
  }
};

interface AuthContextType extends AuthState {
  login: (email: string, password: string) => Promise<boolean>;
  register: (userData: {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
    phoneNumber?: string;
  }) => Promise<boolean>;
  logout: () => Promise<void>;
  clearError: () => void;
  updateUser: (userData: Partial<User>) => Promise<boolean>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [state, dispatch] = useReducer(authReducer, initialState);

  // Check for existing token on mount
  useEffect(() => {
    const token = ApiService.getToken();
    const user = ApiService.getCurrentUser();
    
    if (token && user) {
      dispatch({
        type: 'AUTH_SUCCESS',
        payload: { user, token }
      });
    }
  }, []);

  const login = async (email: string, password: string): Promise<boolean> => {
    try {
      dispatch({ type: 'AUTH_START' });
      
      const response = await ApiService.login({ email, password });
      
      if (response.success && response.data) {
        dispatch({
          type: 'AUTH_SUCCESS',
          payload: {
            user: response.data.user,
            token: response.data.accessToken
          }
        });
        return true;
      } else {
        dispatch({
          type: 'AUTH_FAILURE',
          payload: response.message || 'Login failed'
        });
        return false;
      }
    } catch (error) {
      dispatch({
        type: 'AUTH_FAILURE',
        payload: error instanceof Error ? error.message : 'Login failed'
      });
      return false;
    }
  };

  const register = async (userData: {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
    phoneNumber?: string;
  }): Promise<boolean> => {
    try {
      dispatch({ type: 'AUTH_START' });
      
      const response = await ApiService.register(userData);
      
      if (response.success) {
        // After successful registration, log the user in
        return await login(userData.email, userData.password);
      } else {
        dispatch({
          type: 'AUTH_FAILURE',
          payload: response.message || 'Registration failed'
        });
        return false;
      }
    } catch (error) {
      dispatch({
        type: 'AUTH_FAILURE',
        payload: error instanceof Error ? error.message : 'Registration failed'
      });
      return false;
    }
  };

  const logout = async (): Promise<void> => {
    try {
      await ApiService.logout();
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      dispatch({ type: 'LOGOUT' });
    }
  };

  const clearError = (): void => {
    dispatch({ type: 'CLEAR_ERROR' });
  };

  const updateUser = async (userData: Partial<User>): Promise<boolean> => {
    try {
      const response = await ApiService.updateUserProfile(userData);
      
      if (response.success && response.data) {
        dispatch({ type: 'UPDATE_USER', payload: response.data });
        return true;
      } else {
        dispatch({
          type: 'AUTH_FAILURE',
          payload: response.message || 'Profile update failed'
        });
        return false;
      }
    } catch (error) {
      dispatch({
        type: 'AUTH_FAILURE',
        payload: error instanceof Error ? error.message : 'Profile update failed'
      });
      return false;
    }
  };

  const value: AuthContextType = {
    ...state,
    login,
    register,
    logout,
    clearError,
    updateUser,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
