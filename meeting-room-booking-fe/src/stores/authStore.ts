import { defineStore } from 'pinia';
import type { AuthState, User } from '../types/auth';

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    user: null,
    token: null,
    isAuthenticated: false,
    isLoading: false,
    error: null,
    pendingVerificationEmail: null
  }),

  actions: {
    async login(credentials: any) {
      this.isLoading = true;
      this.error = null;
      try {
        // Simulate API call
        await new Promise(resolve => setTimeout(resolve, 1500));
        
        this.token = 'mock-jwt-token';
        this.user = {
          id: '1',
          firstName: 'John',
          lastName: 'Doe',
          email: credentials.email || 'john@example.com',
          phone: credentials.phone || '+628123456789',
          birthDate: '1990-01-01'
        };
        this.isAuthenticated = true;
      } catch (err: any) {
        this.error = err.message || 'Login failed';
      } finally {
        this.isLoading = false;
      }
    },

    async register(data: any) {
      this.isLoading = true;
      this.error = null;
      try {
        // Simulate API call
        await new Promise(resolve => setTimeout(resolve, 1500));
        
        this.pendingVerificationEmail = data.email;
        // Navigation to verify screen handled by component
      } catch (err: any) {
        this.error = err.message || 'Registration failed';
      } finally {
        this.isLoading = false;
      }
    },

    async verifyOtp(code: string) {
      this.isLoading = true;
      this.error = null;
      try {
        // Simulate API call
        await new Promise(resolve => setTimeout(resolve, 1500));
        
        if (code === '1234') {
          this.isAuthenticated = true;
          this.token = 'mock-jwt-token-after-otp';
          return true;
        } else {
          throw new Error('Invalid verification code');
        }
      } catch (err: any) {
        this.error = err.message || 'Verification failed';
        return false;
      } finally {
        this.isLoading = false;
      }
    },

    async resendOtp() {
      console.log('Resending OTP...');
      await new Promise(resolve => setTimeout(resolve, 1000));
    },

    logout() {
      this.user = null;
      this.token = null;
      this.isAuthenticated = false;
    }
  }
});
