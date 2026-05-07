import { defineStore } from 'pinia';
import type { Booking } from '../types/booking';
import apiClient from '../api/axios';

export const useBookingStore = defineStore('booking', {
  state: () => ({
    bookings: [] as Booking[],
    isLoading: false,
    error: null as string | null,
    currentPage: 0,
    totalPages: 0,
    hasMore: true,
  }),

  actions: {
    async fetchBookings(page = 1, limit = 10, append = false) {
      if (this.isLoading) return;
      
      this.isLoading = true;
      this.error = null;
      try {
        const response = await apiClient.get('/api/v1/bookings', {
          params: { page, limit }
        });
        
        const { content, page: pageInfo } = response.data.data;
        
        if (append) {
          this.bookings = [...this.bookings, ...content];
        } else {
          this.bookings = content;
        }
        
        this.currentPage = pageInfo.number + 1;
        this.totalPages = pageInfo.totalPages;
        this.hasMore = this.currentPage < this.totalPages;
      } catch (err: any) {
        this.error = err.response?.data?.message || 'Failed to fetch bookings';
      } finally {
        this.isLoading = false;
      }
    },

    async fetchMoreBookings() {
      if (!this.hasMore || this.isLoading) return;
      await this.fetchBookings(this.currentPage + 1, 10, true);
    },

    async createBooking(bookingData: any) {
      this.isLoading = true;
      this.error = null;
      try {
        const response = await apiClient.post('/api/v1/bookings', bookingData);
        const newBooking = response.data.data;
        this.bookings.unshift(newBooking);
        return true;
      } catch (err: any) {
        this.error = err.response?.data?.message || 'Failed to create booking';
        return false;
      } finally {
        this.isLoading = false;
      }
    },

    getBookingsByDate(date: string) {
      // date format from calendar is likely YYYY-MM-DD
      // start_time in API is 2026-05-10 09:00:00 or ISO
      return this.bookings.filter(b => b.start_time.startsWith(date));
    }
  }
});
