<script setup lang="ts">
import { ArrowPathIcon } from '@heroicons/vue/24/outline';
import type { Booking } from '../../types/booking';

interface Props {
  booking: Booking;
}

defineProps<Props>();
</script>

<template>
  <div 
    class="flex gap-4 py-4 px-3 rounded-2xl hover:bg-gray-50 transition-all duration-200 cursor-pointer group"
  >
    <div class="flex flex-col items-end min-w-[50px]">
      <span class="text-sm font-bold text-gray-900">{{ booking.startTime }}</span>
      <span class="text-xs text-gray-400 font-medium">{{ booking.duration }}</span>
    </div>
    
    <div class="border-l-4 border-lime pl-4 flex-1">
      <div class="flex items-center justify-between gap-2">
        <h3 class="text-sm font-bold text-gray-900 group-hover:text-lime-dark transition-colors">
          {{ booking.title }}
        </h3>
        <ArrowPathIcon v-if="booking.isRecurring" class="w-4 h-4 text-gray-400" />
      </div>
      <p class="text-xs text-gray-500 mt-1 font-medium">
        {{ booking.roomName }}
      </p>
    </div>
  </div>
</template>
