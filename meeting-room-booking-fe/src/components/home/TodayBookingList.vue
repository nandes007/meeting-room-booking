<script setup lang="ts">
import { CalendarDaysIcon } from '@heroicons/vue/24/outline';
import BookingCard from '@/components/calendar/BookingCard.vue';
import type { Booking } from '@/types/booking';

interface Props {
  bookings: Booking[];
  isLoading: boolean;
}

defineProps<Props>();
</script>

<template>
  <div class="flex flex-col gap-4 mt-8">
    <div class="flex items-center justify-between px-1">
      <h2 class="text-xl font-bold font-display text-gray-900">Today's Schedule</h2>
      <span v-if="bookings.length > 0" class="text-xs font-bold text-gray-400 uppercase tracking-widest bg-gray-50 px-2 py-1 rounded-lg">
        {{ bookings.length }} {{ bookings.length === 1 ? 'meeting' : 'meetings' }}
      </span>
    </div>

    <!-- Loading State -->
    <div v-if="isLoading" class="flex justify-center py-12">
      <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-lime"></div>
    </div>

    <!-- Bookings List -->
    <div v-else-if="bookings.length > 0" class="flex flex-col gap-2">
      <BookingCard 
        v-for="booking in bookings" 
        :key="booking.id" 
        :booking="booking"
        class="bg-white border border-gray-50 shadow-sm hover:shadow-md transition-shadow duration-300"
      />
    </div>

    <!-- Empty State -->
    <div v-else class="flex flex-col items-center text-center py-16 px-6 bg-gray-50 rounded-3xl border border-dashed border-gray-200">
      <div class="w-20 h-20 bg-white rounded-full shadow-sm flex items-center justify-center mb-6">
        <CalendarDaysIcon class="w-10 h-10 text-gray-200" />
      </div>
      <h3 class="text-lg font-bold text-gray-900 font-display">No meetings today!</h3>
      <p class="text-sm text-gray-400 mt-2 max-w-[200px]">
        Your schedule is clear — enjoy the free time! 🎉
      </p>
    </div>
  </div>
</template>
