<script setup lang="ts">
import { onMounted, ref, onUnmounted } from 'vue';
import { PlusIcon, PencilSquareIcon, TrashIcon, UserIcon } from '@heroicons/vue/24/outline';
import { useUserStore } from '@/stores/userStore';
import UserFormModal from '@/components/users/UserFormModal.vue';
import type { User } from '@/types/user';

const userStore = useUserStore();
const isModalOpen = ref(false);
const selectedUser = ref<User | null>(null);
const loader = ref<HTMLElement | null>(null);

let observer: IntersectionObserver | null = null;

onMounted(async () => {
  await userStore.fetchUsers();
  
  observer = new IntersectionObserver((entries) => {
    if (entries[0].isIntersecting && userStore.hasMore && !userStore.isLoading) {
      userStore.fetchMoreUsers();
    }
  }, { threshold: 0.1 });

  if (loader.value) {
    observer.observe(loader.value);
  }
});

onUnmounted(() => {
  if (observer) observer.disconnect();
});

const openCreateModal = () => {
  selectedUser.value = null;
  isModalOpen.value = true;
};

const openEditModal = (user: User) => {
  selectedUser.value = user;
  isModalOpen.value = true;
};

const confirmDelete = async (user: User) => {
  if (confirm(`Are you sure you want to delete user "${user.name}"?`)) {
    await userStore.deleteUser(user.id);
  }
};
</script>

<template>
  <div class="min-h-screen bg-gray-50/50 pb-20">
    <header class="sticky top-0 bg-white/80 backdrop-blur-md z-30 border-b border-gray-100 px-6 py-6 flex items-center justify-between">
      <div>
        <h1 class="text-3xl font-bold text-gray-900 font-display">Users</h1>
        <p class="text-gray-500 text-sm mt-1">Manage team members and roles</p>
      </div>
      <button 
        @click="openCreateModal"
        class="bg-lime text-gray-900 px-6 py-3 rounded-2xl font-bold hover:bg-lime-dark hover:scale-105 transition-all duration-300 flex items-center gap-2 shadow-lg shadow-lime/20"
      >
        <PlusIcon class="w-5 h-5" />
        New User
      </button>
    </header>

    <main class="max-w-4xl mx-auto p-6">
      <div v-if="userStore.users.length > 0" class="flex flex-col gap-4">
        <div 
          v-for="user in userStore.users" 
          :key="user.id"
          class="bg-white p-5 rounded-3xl border border-gray-100 flex items-center justify-between group hover:shadow-xl hover:shadow-gray-200/50 transition-all duration-300"
        >
          <div class="flex items-center gap-4">
            <div class="w-12 h-12 rounded-2xl bg-lime/10 flex items-center justify-center text-lime-dark group-hover:bg-lime group-hover:text-gray-900 transition-all duration-500">
              <UserIcon class="w-6 h-6" />
            </div>
            <div class="flex flex-col">
              <span class="text-lg font-bold text-gray-900">{{ user.name }}</span>
              <div class="flex items-center gap-3 text-sm text-gray-500 mt-0.5">
                <span class="bg-gray-100 px-2 py-0.5 rounded-lg font-medium text-[11px] uppercase tracking-wider">{{ user.role }}</span>
                <span class="w-1 h-1 rounded-full bg-gray-300"></span>
                <span>{{ user.email }}</span>
              </div>
            </div>
          </div>

          <div class="flex items-center gap-2">
            <button 
              @click="openEditModal(user)"
              class="p-2.5 rounded-xl hover:bg-gray-50 text-gray-400 hover:text-gray-900 transition-all"
              title="Edit User"
            >
              <PencilSquareIcon class="w-5 h-5" />
            </button>
            <button 
              @click="confirmDelete(user)"
              class="p-2.5 rounded-xl hover:bg-red-50 text-gray-400 hover:text-red-500 transition-all"
              title="Delete User"
            >
              <TrashIcon class="w-5 h-5" />
            </button>
          </div>
        </div>

        <div ref="loader" class="h-20 flex items-center justify-center">
          <div v-if="userStore.isLoading" class="animate-spin rounded-full h-8 w-8 border-b-2 border-lime"></div>
          <span v-else-if="!userStore.hasMore" class="text-sm text-gray-400 font-medium italic">End of list</span>
        </div>
      </div>

      <div v-else-if="userStore.isLoading" class="flex flex-col gap-4">
        <div v-for="i in 5" :key="i" class="h-24 bg-white rounded-3xl animate-pulse border border-gray-100"></div>
      </div>

      <div v-else class="py-20 flex flex-col items-center justify-center text-center gap-4">
        <div class="w-20 h-20 bg-gray-100 rounded-full flex items-center justify-center text-gray-300">
          <UserIcon class="w-10 h-10" />
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900">No users found</h3>
          <p class="text-gray-500 mt-1">Start by adding a new member to the team</p>
        </div>
      </div>
    </main>

    <UserFormModal 
      :is-open="isModalOpen" 
      :user="selectedUser" 
      @close="isModalOpen = false" 
    />
  </div>
</template>
