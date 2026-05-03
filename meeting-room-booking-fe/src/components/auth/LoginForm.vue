<script setup lang="ts">
import { ref } from 'vue';
import AuthTabs from './AuthTabs.vue';
import PhoneInput from './PhoneInput.vue';
import EmailInput from './EmailInput.vue';
import PasswordInput from './PasswordInput.vue';
import AppButton from '../shared/AppButton.vue';
import AppDivider from '../shared/AppDivider.vue';
import SocialLogin from './SocialLogin.vue';
import { useValidation } from '../../composables/useValidation';
import { useAuthStore } from '../../stores/authStore';
import type { InputType } from '../../types/auth';

const emit = defineEmits(['switch-to-register', 'forgot-password', 'success']);

const authStore = useAuthStore();
const { errors, validateEmail, validatePhone, validatePassword, clearErrors } = useValidation();

const inputType = ref<InputType>('phone');
const phone = ref('');
const email = ref('');
const password = ref('');
const rememberMe = ref(false);

const inputTypeTabs = [
  { value: 'phone', label: 'Phone Number' },
  { value: 'email', label: 'Email' }
];

const handleSubmit = async () => {
  clearErrors();
  
  if (inputType.value === 'email') {
    errors.value.email = validateEmail(email.value);
  } else {
    errors.value.phone = validatePhone(phone.value);
  }
  errors.value.password = validatePassword(password.value);

  const hasErrors = Object.values(errors.value).some(err => err !== '');
  if (hasErrors) return;

  await authStore.login({
    email: email.value,
    phone: phone.value,
    password: password.value,
    rememberMe: rememberMe.value
  });

  if (authStore.isAuthenticated) {
    emit('success');
  }
};
</script>

<template>
  <div class="flex flex-col gap-6">
    <div class="text-center">
      <h1 class="text-2xl font-bold text-gray-900">Welcome Back</h1>
      <p class="text-sm text-gray-400 mt-1">Login to access your account</p>
    </div>

    <AuthTabs v-model="inputType" :tabs="inputTypeTabs" />

    <form @submit.prevent="handleSubmit" class="flex flex-col gap-5">
      <PhoneInput
        v-if="inputType === 'phone'"
        v-model="phone"
        :error="errors.phone"
      />
      <EmailInput
        v-else
        v-model="email"
        :error="errors.email"
      />

      <PasswordInput
        v-model="password"
        :error="errors.password"
      />

      <div class="flex items-center justify-between">
        <label class="flex items-center gap-2 cursor-pointer">
          <input
            type="checkbox"
            v-model="rememberMe"
            class="w-4 h-4 rounded border-gray-300 text-lime focus:ring-lime"
          />
          <span class="text-sm text-gray-600">Remember me</span>
        </label>
        <button
          type="button"
          class="text-sm text-gray-400 hover:text-gray-600"
          @click="$emit('forgot-password')"
        >
          Forgot password?
        </button>
      </div>

      <AppButton
        type="submit"
        label="Log In"
        :disabled="authStore.isLoading"
      />

      <AppDivider />

      <SocialLogin />

      <p class="text-center text-sm text-gray-400 mt-2">
        Don't have an account? 
        <button
          type="button"
          class="text-lime font-semibold hover:text-lime-dark cursor-pointer ml-1"
          @click="$emit('switch-to-register')"
        >
          Sign Up
        </button>
      </p>
    </form>
  </div>
</template>
