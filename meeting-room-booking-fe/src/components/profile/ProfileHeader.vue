<script setup lang="ts">
import { computed } from 'vue';
import type { User } from '@/types/auth';

interface Props {
  user: User | null;
}

const props = defineProps<Props>();

const initials = computed(() => {
  if (!props.user) return '??';
  return `${props.user.firstName[0]}${props.user.lastName[0]}`.toUpperCase();
});

const fullName = computed(() => {
  if (!props.user) return 'Guest User';
  return `${props.user.firstName} ${props.user.lastName}`;
});
</script>

<template>
  <div class="flex flex-col items-center py-6">
    <div class="w-24 h-24 rounded-full bg-lime/10 flex items-center justify-center border-4 border-white shadow-xl relative overflow-hidden group">
      <span class="text-3xl font-bold text-lime-dark font-display tracking-tight">
        {{ initials }}
      </span>
      <div class="absolute inset-0 bg-lime/20 opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
    </div>
    
    <h2 class="text-2xl font-bold font-display text-gray-900 mt-5">
      {{ fullName }}
    </h2>
    <p class="text-sm text-gray-400 font-medium mt-1">
      {{ user?.email || 'Not logged in' }}
    </p>
    
    <div class="mt-4 px-3 py-1 bg-lime/10 text-lime-dark text-[10px] font-bold uppercase tracking-widest rounded-full border border-lime/20">
      Verified Profile
    </div>
  </div>
</template>
