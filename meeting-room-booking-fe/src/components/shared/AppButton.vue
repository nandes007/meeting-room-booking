<script setup lang="ts">
interface Props {
  label: string;
  variant?: 'primary' | 'outline' | 'social';
  disabled?: boolean;
  fullWidth?: boolean;
  type?: 'button' | 'submit' | 'reset';
}

withDefaults(defineProps<Props>(), {
  variant: 'primary',
  disabled: false,
  fullWidth: true,
  type: 'button'
});

defineEmits(['click']);
</script>

<template>
  <button
    :type="type"
    :disabled="disabled"
    class="flex items-center justify-center gap-2 py-3 px-4 transition-all duration-200 cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed"
    :class="[
      variant === 'primary' ? 'bg-lime text-gray-900 font-semibold rounded-3xl hover:bg-lime-dark' : '',
      variant === 'outline' ? 'bg-white border border-gray-200 text-gray-700 font-medium rounded-3xl hover:bg-gray-50' : '',
      variant === 'social' ? 'bg-white border border-gray-200 text-gray-700 font-medium rounded-xl hover:bg-gray-50' : '',
      fullWidth ? 'w-full' : ''
    ]"
    @click="$emit('click')"
  >
    <slot name="icon" />
    <span>{{ label }}</span>
  </button>
</template>
