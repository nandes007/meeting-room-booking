import { defineStore } from 'pinia';
import type { RoomState, Room } from '../types/room';
import apiClient from '../api/axios';

export const useRoomStore = defineStore('room', {
  state: (): RoomState => ({
    rooms: [],
    isLoading: false,
    error: null,
  }),

  actions: {
    async fetchRooms() {
      this.isLoading = true;
      this.error = null;
      try {
        const response = await apiClient.get('/api/v1/rooms');
        // The API returns a pageable object with content array
        this.rooms = response.data.data.content;
      } catch (err: any) {
        this.error = err.response?.data?.message || 'Failed to fetch rooms';
      } finally {
        this.isLoading = false;
      }
    }
  }
});
