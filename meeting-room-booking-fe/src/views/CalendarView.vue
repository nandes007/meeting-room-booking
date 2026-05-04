<script setup lang="ts">
import { onMounted, ref, computed } from 'vue';
import { PlusIcon } from '@heroicons/vue/24/solid';
import { useCalendar } from '../composables/useCalendar';
import { useBookingStore } from '../stores/bookingStore';
import CalendarHeader from '../components/calendar/CalendarHeader.vue';
import WeekStrip from '../components/calendar/WeekStrip.vue';
import DaySchedule from '../components/calendar/DaySchedule.vue';
import CreateBookingModal from '../components/calendar/CreateBookingModal.vue';
import type { DayScheduleData } from '../types/booking';

const { 
  currentDate, 
  currentWeekDays, 
  currentMonthLabel, 
  goToNextWeek, 
  goToPreviousWeek, 
  selectDate,
  formatDateLabel,
  getDayLabel,
  formatDateKey
} = useCalendar();

const bookingStore = useBookingStore();
const isModalOpen = ref(false);

onMounted(async () => {
  await bookingStore.fetchBookings();
});

const scheduleData = computed<DayScheduleData[]>(() => {
  return currentWeekDays.value.map(date => {
    const dateKey = formatDateKey(date);
    return {
      date,
      dateLabel: formatDateLabel(date),
      dayLabel: getDayLabel(date),
      bookings: bookingStore.getBookingsByDate(dateKey)
    };
  });
});

const handleCreateBooking = async (bookingData: any) => {
  const success = await bookingStore.createBooking(bookingData);
  if (success) {
    isModalOpen.value = false;
  }
};
</script>

<template>
  <div class="min-h-screen bg-white max-w-2xl mx-auto pb-24 relative">
    <!-- Sticky Top Section -->
    <div class="sticky top-0 bg-white/90 backdrop-blur-md z-30 pt-4 pb-2 border-b border-gray-100">
      <CalendarHeader 
        :month-label="currentMonthLabel" 
        @prev="goToPreviousWeek" 
        @next="goToNextWeek" 
      />
      <WeekStrip 
        :days="currentWeekDays" 
        :selected-date="currentDate" 
        @select="selectDate" 
      />
    </div>

    <!-- Schedule List -->
    <main class="px-4 mt-2">
      <div v-if="bookingStore.isLoading && scheduleData.length === 0" class="flex justify-center py-20">
        <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-lime"></div>
      </div>
      
      <div v-else>
        <DaySchedule 
          v-for="(day, index) in scheduleData" 
          :key="day.date.toISOString()" 
          :day-data="day" 
          :index="index"
        />
      </div>
    </main>

    <!-- FAB -->
    <button 
      @click="isModalOpen = true"
      class="fixed bottom-8 right-8 w-14 h-14 bg-lime text-gray-900 rounded-full shadow-lg flex items-center justify-center hover:bg-lime-dark hover:scale-105 transition-all duration-300 z-40 focus:outline-none focus:ring-4 focus:ring-lime/30"
      aria-label="Add booking"
    >
      <PlusIcon class="w-8 h-8" />
    </button>

    <!-- Create Modal -->
    <CreateBookingModal 
      :is-open="isModalOpen" 
      :selected-date="formatDateKey(currentDate)" 
      @close="isModalOpen = false" 
      @submit="handleCreateBooking"
    />
  </div>
</template>

<style scoped>
/* Ensure smooth scrolling when selecting dates if needed */
main {
  scroll-behavior: smooth;
}
</style>
