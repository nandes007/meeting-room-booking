<script setup lang="ts">
import { ref, computed } from 'vue';
import UserCard from '@/components/users/UserCard.vue';
import { MagnifyingGlassIcon } from '@heroicons/vue/24/outline';

const searchQuery = ref('');

const users = ref([
  { id: '1', name: 'John Doe', email: 'john@example.com', role: 'admin' },
  { id: '2', name: 'Jane Smith', email: 'jane@example.com', role: 'user' },
  { id: '3', name: 'Alice Johnson', email: 'alice@example.com', role: 'user' },
  { id: '4', name: 'Bob Wilson', email: 'bob@example.com', role: 'admin' },
  { id: '5', name: 'Sarah Parker', email: 'sarah@example.com', role: 'user' },
  { id: '6', name: 'Michael Chen', email: 'michael@example.com', role: 'user' },
]);

const filteredUsers = computed(() => {
  if (!searchQuery.value) return users.value;
  const query = searchQuery.value.toLowerCase();
  return users.value.filter(u => 
    u.name.toLowerCase().includes(query) || 
    u.email.toLowerCase().includes(query)
  );
});
</script>

<template>
  <div class="min-h-screen bg-white max-w-2xl mx-auto pb-24 px-4 pt-8">
    <div class="mb-8">
      <h1 class="text-2xl font-bold font-display text-gray-900">Team Members</h1>
      <p class="text-sm text-gray-400 mt-1 font-medium">
        Manage and view all team members in the system
      </p>
    </div>

    <!-- Search Bar -->
    <div class="relative mb-6">
      <MagnifyingGlassIcon class="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
      <input 
        v-model="searchQuery"
        type="text" 
        placeholder="Search by name or email..."
        class="w-full pl-12 pr-4 py-3 bg-gray-50 border border-gray-100 rounded-2xl text-sm focus:outline-none focus:ring-2 focus:ring-lime/50 transition-all"
      />
    </div>

    <!-- User Count -->
    <div class="flex items-center justify-between mb-4 px-1">
      <span class="text-xs font-bold text-gray-400 uppercase tracking-widest">
        {{ filteredUsers.length }} Members
      </span>
      <button class="text-xs font-bold text-lime-dark uppercase tracking-widest hover:underline transition-all">
        Add New
      </button>
    </div>

    <!-- Users List -->
    <div class="flex flex-col gap-1">
      <UserCard 
        v-for="user in filteredUsers" 
        :key="user.id" 
        :user="user"
      />
    </div>

    <!-- Empty State -->
    <div v-if="filteredUsers.length === 0" class="flex flex-col items-center text-center py-20 px-6">
      <p class="text-sm text-gray-400 font-medium italic">
        No members found matching your search
      </p>
    </div>
  </div>
</template>
