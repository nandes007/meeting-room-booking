<script setup lang="ts">
import { computed } from 'vue';

interface User {
  id: string;
  name: string;
  email: string;
  role: string;
}

interface Props {
  user: User;
}

const props = defineProps<Props>();

const initials = computed(() => {
  return props.user.name
    .split(' ')
    .map(n => n[0])
    .join('')
    .toUpperCase();
});

const roleClasses = computed(() => {
  if (props.user.role === 'admin') {
    return 'bg-lime/20 text-lime-dark';
  }
  return 'bg-gray-100 text-gray-500';
});
</script>

<template>
  <div class="flex items-center gap-4 py-4 px-3 rounded-2xl hover:bg-gray-50 transition-all duration-200 cursor-pointer group">
    <!-- Avatar -->
    <div class="w-12 h-12 rounded-full bg-lime/10 flex items-center justify-center border-2 border-white shadow-sm group-hover:scale-105 transition-transform">
      <span class="text-sm font-bold text-lime-dark tracking-tighter">
        {{ initials }}
      </span>
    </div>

    <!-- Info -->
    <div class="flex-1 flex flex-col gap-0.5">
      <div class="flex items-center justify-between">
        <h3 class="text-sm font-bold text-gray-900 group-hover:text-lime-dark transition-colors">
          {{ user.name }}
        </h3>
        <span 
          class="px-2 py-0.5 rounded-full text-[10px] font-bold uppercase tracking-wider"
          :class="roleClasses"
        >
          {{ user.role }}
        </span>
      </div>
      <p class="text-xs text-gray-400 font-medium truncate max-w-[180px]">
        {{ user.email }}
      </p>
    </div>
    
    <!-- Action Indicator (Simple arrow) -->
    <div class="text-gray-200 group-hover:text-lime-dark transition-colors">
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" class="w-5 h-5">
        <path fill-rule="evenodd" d="M8.22 5.22a.75.75 0 0 1 1.06 0l4.25 4.25a.75.75 0 0 1 0 1.06l-4.25 4.25a.75.75 0 0 1-1.06-1.06L11.94 10 8.22 6.28a.75.75 0 0 1 0-1.06Z" clip-rule="evenodd" />
      </svg>
    </div>
  </div>
</template>
