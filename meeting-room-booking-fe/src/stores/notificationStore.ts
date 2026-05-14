import { defineStore } from 'pinia';
import type { Booking } from '../types/booking';
import apiClient from '../api/axios';

export const useNotificationStore = defineStore('notification', {
  state: () => ({
    pendingBookings: [] as Booking[],
    isLoading: false,
    error: null as string | null,
  }),

  getters: {
    pendingCount: (state) => state.pendingBookings.length,
    hasPending: (state) => state.pendingBookings.length > 0,
  },

  actions: {
    async fetchPendingBookings() {
      this.isLoading = true;
      this.error = null;
      try {
        const response = await apiClient.get('/api/v1/bookings', {
          params: { status: 'pending' }
        });
        
        // The API response format has the bookings in data.content
        this.pendingBookings = response.data.data.content;
      } catch (err: any) {
        this.error = err.response?.data?.message || 'Failed to fetch pending bookings';
      } finally {
        this.isLoading = false;
      }
    },

    async approveBooking(id: number) {
      this.error = null;
      try {
        await apiClient.patch(`/api/v1/bookings/${id}/approve`, {
          status: 'approved'
        });
        
        // Remove from local state on success
        this.pendingBookings = this.pendingBookings.filter(b => b.id !== id);
        return true;
      } catch (err: any) {
        this.error = err.response?.data?.message || 'Failed to approve booking';
        return false;
      }
    },

    clearError() {
      this.error = null;
    }
  }
});
