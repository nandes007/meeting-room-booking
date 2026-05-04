import { defineStore } from 'pinia';
import type { Booking } from '../types/booking';

export const useBookingStore = defineStore('booking', {
  state: () => ({
    bookings: [] as Booking[],
    isLoading: false,
    error: null as string | null,
  }),

  actions: {
    async fetchBookings() {
      this.isLoading = true;
      try {
        // Simulate API call with mock data
        await new Promise(resolve => setTimeout(resolve, 800));
        
        const today = new Date();
        const formatDate = (date: Date) => {
          const year = date.getFullYear();
          const month = String(date.getMonth() + 1).padStart(2, '0');
          const day = String(date.getDate()).padStart(2, '0');
          return `${year}-${month}-${day}`;
        };

        const todayKey = formatDate(today);
        const tomorrow = new Date(today);
        tomorrow.setDate(today.getDate() + 1);
        const tomorrowKey = formatDate(tomorrow);
        
        const wednesday = new Date(today);
        wednesday.setDate(today.getDate() + (3 - today.getDay())); // Get this week's Wednesday
        const wednesdayKey = formatDate(wednesday);

        this.bookings = [
          {
            id: '1',
            title: 'Daily Report The Core',
            roomName: 'Microsoft Teams Meeting',
            startTime: '11:00',
            endTime: '11:30',
            date: todayKey,
            duration: '30min',
            organizer: 'John Doe',
            isRecurring: true
          },
          {
            id: '2',
            title: 'Weekly Sync',
            roomName: 'Room A',
            startTime: '14:00',
            endTime: '15:00',
            date: todayKey,
            duration: '1h',
            organizer: 'Jane Smith',
            isRecurring: true
          },
          {
            id: '3',
            title: 'Project Kickoff',
            roomName: 'Main Hall',
            startTime: '09:00',
            endTime: '10:30',
            date: tomorrowKey,
            duration: '1.5h',
            organizer: 'Alice Johnson'
          },
          {
            id: '4',
            title: 'Daily Report The Core',
            roomName: 'Microsoft Teams Meeting',
            startTime: '11:00',
            endTime: '11:30',
            date: wednesdayKey,
            duration: '30min',
            organizer: 'John Doe',
            isRecurring: true
          }
        ];
      } catch (err: any) {
        this.error = err.message || 'Failed to fetch bookings';
      } finally {
        this.isLoading = false;
      }
    },

    async createBooking(bookingData: Omit<Booking, 'id'>) {
      this.isLoading = true;
      try {
        await new Promise(resolve => setTimeout(resolve, 1000));
        const newBooking = {
          ...bookingData,
          id: Math.random().toString(36).substr(2, 9)
        };
        this.bookings.push(newBooking);
        return true;
      } catch (err: any) {
        this.error = err.message || 'Failed to create booking';
        return false;
      } finally {
        this.isLoading = false;
      }
    },

    getBookingsByDate(dateKey: string) {
      return this.bookings.filter(b => b.date === dateKey);
    }
  }
});
