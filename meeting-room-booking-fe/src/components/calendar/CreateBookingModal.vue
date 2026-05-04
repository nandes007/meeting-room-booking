<script setup lang="ts">
import { ref, reactive } from 'vue';
import { XMarkIcon } from '@heroicons/vue/24/outline';
import AppInput from '../shared/AppInput.vue';
import AppButton from '../shared/AppButton.vue';
import AppDateInput from '../shared/AppDateInput.vue';

interface Props {
  isOpen: boolean;
  selectedDate: string;
}

const props = defineProps<Props>();
const emit = defineEmits(['close', 'submit']);

const form = reactive({
  title: '',
  roomName: '',
  date: props.selectedDate,
  startTime: '10:00',
  endTime: '11:00',
  organizer: 'Current User' // Mock
});

const isSubmitting = ref(false);

const handleSubmit = async () => {
  isSubmitting.value = true;
  // Calculate duration string (simplified)
  const duration = '1h'; 
  
  emit('submit', { ...form, duration });
  isSubmitting.value = false;
  resetForm();
};

const resetForm = () => {
  form.title = '';
  form.roomName = '';
  form.startTime = '10:00';
  form.endTime = '11:00';
};
</script>

<template>
  <Transition name="fade">
    <div v-if="isOpen" class="fixed inset-0 z-50 flex items-center justify-center p-4">
      <!-- Backdrop -->
      <div 
        class="absolute inset-0 bg-black/40 backdrop-blur-sm" 
        @click="emit('close')"
      ></div>

      <!-- Modal Content -->
      <div class="relative w-full max-w-md bg-white rounded-3xl shadow-2xl overflow-hidden glass animate-in fade-in zoom-in duration-300">
        <div class="p-6">
          <div class="flex items-center justify-between mb-6">
            <h2 class="text-2xl font-bold text-gray-900 font-display">New Booking</h2>
            <button 
              @click="emit('close')"
              class="p-2 rounded-full hover:bg-gray-100 text-gray-400 transition-colors"
            >
              <XMarkIcon class="w-6 h-6" />
            </button>
          </div>

          <form @submit.prevent="handleSubmit" class="flex flex-col gap-4">
            <AppInput 
              label="Meeting Title" 
              v-model="form.title" 
              placeholder="e.g. Sync Session" 
              required
            />
            
            <AppInput 
              label="Room Name" 
              v-model="form.roomName" 
              placeholder="e.g. Meeting Room A" 
              required
            />

            <AppDateInput 
              label="Date" 
              v-model="form.date" 
            />

            <div class="grid grid-cols-2 gap-4">
              <AppInput 
                label="Start Time" 
                v-model="form.startTime" 
                type="time" 
                required
              />
              <AppInput 
                label="End Time" 
                v-model="form.endTime" 
                type="time" 
                required
              />
            </div>

            <div class="mt-4 flex gap-3">
              <AppButton 
                label="Cancel" 
                variant="outline" 
                @click="emit('close')" 
              />
              <AppButton 
                label="Create Booking" 
                type="submit" 
                :disabled="isSubmitting"
              />
            </div>
          </form>
        </div>
      </div>
    </div>
  </Transition>
</template>

<style scoped>
.animate-in {
  animation-duration: 0.3s;
  animation-fill-mode: both;
}

@keyframes zoom-in {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.zoom-in {
  animation-name: zoom-in;
}
</style>
