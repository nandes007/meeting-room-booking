<script setup lang="ts">
import { ref } from 'vue';
import PasswordInput from './PasswordInput.vue';
import AppButton from '../shared/AppButton.vue';
import { useAuthStore } from '../../stores/authStore';

const emit = defineEmits(['switch-to-register', 'forgot-password', 'success']);

const authStore = useAuthStore();

const username = ref('');
const password = ref('');
const rememberMe = ref(false);

const handleSubmit = async () => {
  if (!username.value || !password.value) return;

  try {
    await authStore.login({
      username: username.value,
      password: password.value
    });

    if (authStore.isAuthenticated) {
      emit('success');
    }
  } catch (err) {
    // Error handled in store
  }
};
</script>

<template>
  <div class="flex flex-col gap-6">
    <div class="text-center">
      <h1 class="text-2xl font-bold text-gray-900">Welcome Back</h1>
      <p class="text-sm text-gray-400 mt-1">Login to access your account</p>
    </div>

    <form @submit.prevent="handleSubmit" class="flex flex-col gap-5">
      <div class="flex flex-col gap-2">
        <label class="text-sm font-medium text-gray-700">Username</label>
        <input
          v-model="username"
          type="text"
          placeholder="Enter your username"
          class="w-full px-4 py-3 rounded-2xl bg-gray-50 border border-transparent focus:bg-white focus:border-gray-200 focus:ring-0 transition-all outline-hidden text-sm"
          required
        />
      </div>

      <PasswordInput
        v-model="password"
      />

      <div v-if="authStore.error" class="text-red-500 text-sm text-center">
        {{ authStore.error }}
      </div>

      <div class="flex items-center justify-between">
        <label class="flex items-center gap-2 cursor-pointer">
          <input
            type="checkbox"
            v-model="rememberMe"
            class="w-4 h-4 rounded border-gray-300 text-lime focus:ring-lime"
          />
          <span class="text-sm text-gray-600">Remember me</span>
        </label>
      </div>

      <AppButton
        type="submit"
        label="Log In"
        :disabled="authStore.isLoading"
      />
    </form>
  </div>
</template>
