<script setup lang="ts">
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import { 
  HomeIcon as HomeOutline, 
  CalendarDaysIcon as CalendarOutline, 
  UsersIcon as UsersOutline, 
  UserCircleIcon as UserOutline 
} from '@heroicons/vue/24/outline';
import { 
  HomeIcon as HomeSolid, 
  CalendarDaysIcon as CalendarSolid, 
  UsersIcon as UsersSolid, 
  UserCircleIcon as UserSolid 
} from '@heroicons/vue/24/solid';

const route = useRoute();

const navItems = [
  { 
    label: 'Home', 
    path: '/', 
    outlineIcon: HomeOutline, 
    solidIcon: HomeSolid 
  },
  { 
    label: 'Calendar', 
    path: '/calendar', 
    outlineIcon: CalendarOutline, 
    solidIcon: CalendarSolid 
  },
  { 
    label: 'Users', 
    path: '/users', 
    outlineIcon: UsersOutline, 
    solidIcon: UsersSolid 
  },
  { 
    label: 'Profile', 
    path: '/profile', 
    outlineIcon: UserOutline, 
    solidIcon: UserSolid 
  }
];

const isActive = (path: string) => {
  if (path === '/' && route.path === '/') return true;
  if (path !== '/' && route.path.startsWith(path)) return true;
  return false;
};
</script>

<template>
  <nav class="fixed bottom-0 left-0 right-0 z-50 bg-white border-t border-gray-100 pb-safe">
    <div class="max-w-2xl mx-auto flex justify-around items-center h-16 px-4">
      <router-link
        v-for="item in navItems"
        :key="item.path"
        :to="item.path"
        class="flex flex-col items-center gap-1 min-w-[64px] transition-colors duration-200"
        :class="isActive(item.path) ? 'text-lime' : 'text-gray-400'"
      >
        <component 
          :is="isActive(item.path) ? item.solidIcon : item.outlineIcon" 
          class="w-6 h-6"
        />
        <span class="text-[10px] font-semibold uppercase tracking-wider">
          {{ item.label }}
        </span>
        <div 
          v-if="isActive(item.path)" 
          class="w-1 h-1 rounded-full bg-lime mt-[-2px]"
        ></div>
      </router-link>
    </div>
  </nav>
</template>

<style scoped>
.pb-safe {
  padding-bottom: env(safe-area-inset-bottom);
}
</style>
