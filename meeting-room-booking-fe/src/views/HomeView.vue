<script setup lang="ts">
import { onMounted, computed } from 'vue';
import { useAuthStore } from '@/stores/authStore';
import { useBookingStore } from '@/stores/bookingStore';
import WelcomeBanner from '@/components/home/WelcomeBanner.vue';
import TodayBookingList from '@/components/home/TodayBookingList.vue';

const authStore = useAuthStore();
const bookingStore = useBookingStore();

onMounted(async () => {
  // Always fetch to ensure we have latest bookings
  await bookingStore.fetchBookings();
});

const todayKey = computed(() => {
  const today = new Date();
  const year = today.getFullYear();
  const month = String(today.getMonth() + 1).padStart(2, '0');
  const day = String(today.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
});

const todayBookings = computed(() => {
  return bookingStore.getBookingsByDate(todayKey.value);
});

const firstName = computed(() => authStore.user?.firstName || 'there');
</script>

<template>
  <div class="min-h-screen bg-white max-w-2xl mx-auto pb-24 px-4 pt-8">
    <WelcomeBanner :user-name="firstName" />
    
    <TodayBookingList 
      :bookings="todayBookings" 
      :is-loading="bookingStore.isLoading" 
    />
    
    <!-- Quick Stats or Actions (Optional visual flair) -->
    <div class="grid grid-cols-2 gap-4 mt-8">
      <div class="p-4 bg-purple-light rounded-3xl border border-purple-primary/10">
        <span class="text-xs font-bold text-purple-primary uppercase tracking-wider">Rooms</span>
        <p class="text-xl font-bold text-gray-900 mt-1">Available</p>
      </div>
      <router-link to="/calendar" class="p-4 bg-gray-50 rounded-3xl border border-gray-100 flex flex-col justify-between">
        <span class="text-xs font-bold text-gray-400 uppercase tracking-wider">Calendar</span>
        <p class="text-xl font-bold text-gray-900 mt-1">View All</p>
      </router-link>
    </div>
  </div>
</template>
