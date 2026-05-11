<script setup lang="ts">
import { onMounted, ref, computed, nextTick } from 'vue';
import { PlusIcon } from '@heroicons/vue/24/solid';
import { useCalendar } from '@/composables/useCalendar';
import { useBookingStore } from '@/stores/bookingStore';
import CalendarHeader from '@/components/calendar/CalendarHeader.vue';
import WeekStrip from '@/components/calendar/WeekStrip.vue';
import DaySchedule from '@/components/calendar/DaySchedule.vue';
import CreateBookingModal from '@/components/calendar/CreateBookingModal.vue';
import type { DayScheduleData } from '@/types/booking';

const { 
  currentDate, 
  currentWeekDays, 
  currentMonthLabel, 
  goToNextWeek, 
  goToPreviousWeek, 
  selectDate,
  formatDateLabel,
  getDayLabel,
  formatDateKey,
  isToday
} = useCalendar();

const bookingStore = useBookingStore();
const isModalOpen = ref(false);

onMounted(async () => {
  await bookingStore.fetchBookings();
  
  // Wait for DOM to render the list
  await nextTick();
  
  // Automatically scroll to today's date on initialization
  const today = new Date();
  const dateKey = formatDateKey(today);
  const element = document.getElementById(`day-${dateKey}`);
  if (element) {
    element.scrollIntoView({ behavior: 'smooth', block: 'center' });
  }
});

const scheduleData = computed<DayScheduleData[]>(() => {
  // Use currentWeekDays as the basis for the list to ensure we can always check any day in the current week view
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

const handleDateSelect = (date: Date) => {
  selectDate(date);
  const dateKey = formatDateKey(date);
  const element = document.getElementById(`day-${dateKey}`);
  if (element) {
    element.scrollIntoView({ behavior: 'smooth', block: 'center' });
  }
};

</script>

<template>
  <div class="min-h-screen bg-white max-w-2xl mx-auto pb-32 relative">
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
        @select="handleDateSelect" 
      />
    </div>

    <!-- Schedule List -->
    <main class="px-4 mt-2">
      <div v-if="bookingStore.isLoading && scheduleData.length === 0" class="flex flex-col gap-4 py-10">
        <div v-for="i in 5" :key="i" class="h-32 bg-gray-50 rounded-3xl animate-pulse"></div>
      </div>
      
      <div v-else class="flex flex-col gap-6">
        <DaySchedule 
          v-for="(day, index) in scheduleData" 
          :key="day.date.toISOString()" 
          :day-data="day" 
          :index="index"
          :class="{ 'ring-2 ring-lime ring-offset-4 rounded-3xl transition-all': formatDateKey(day.date) === formatDateKey(currentDate) }"
          :id="`day-${formatDateKey(day.date)}`"
        />
        
        <div v-if="bookingStore.isLoading" class="h-20 flex items-center justify-center">
          <div class="animate-spin rounded-full h-6 w-6 border-b-2 border-lime"></div>
        </div>
      </div>
    </main>

    <!-- FAB -->
    <button 
      @click="isModalOpen = true"
      class="fixed bottom-24 right-8 w-14 h-14 bg-lime text-gray-900 rounded-full shadow-lg flex items-center justify-center hover:bg-lime-dark hover:scale-105 transition-all duration-300 z-40 focus:outline-none focus:ring-4 focus:ring-lime/30"
      aria-label="Add booking"
    >
      <PlusIcon class="w-8 h-8" />
    </button>

    <!-- Create Modal -->
    <CreateBookingModal 
      :is-open="isModalOpen" 
      :selected-date="formatDateKey(currentDate)" 
      @close="isModalOpen = false" 
    />
  </div>
</template>

<style scoped>
/* Ensure smooth scrolling when selecting dates if needed */
main {
  scroll-behavior: smooth;
}
</style>
