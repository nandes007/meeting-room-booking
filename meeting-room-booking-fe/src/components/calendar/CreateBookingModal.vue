<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue';
import { XMarkIcon } from '@heroicons/vue/24/outline';
import AppInput from '@/components/shared/AppInput.vue';
import AppButton from '@/components/shared/AppButton.vue';
import AppSelect from '@/components/shared/AppSelect.vue';
import { useRoomStore } from '@/stores/roomStore';
import { useBookingStore } from '@/stores/bookingStore';
import { ExclamationCircleIcon } from '@heroicons/vue/24/outline';

interface Props {
  isOpen: boolean;
  selectedDate: string;
}

const props = defineProps<Props>();
const emit = defineEmits(['close', 'submit']);

const roomStore = useRoomStore();
const bookingStore = useBookingStore();

const form = reactive({
  description: '',
  room_id: null as number | null,
  date: props.selectedDate,
  startTime: '10:00',
  endTime: '11:00',
  status: 'pending'
});

const isSubmitting = ref(false);

onMounted(() => {
  roomStore.fetchRooms();
  bookingStore.clearError();
});

watch(() => props.isOpen, (newVal) => {
  if (newVal) {
    bookingStore.clearError();
  }
});

watch(() => props.selectedDate, (newDate) => {
  form.date = newDate;
});

watch(() => form, () => {
  if (bookingStore.error) {
    bookingStore.clearError();
  }
}, { deep: true });

const handleSubmit = async () => {
  if (!form.room_id) return;
  
  isSubmitting.value = true;
  
  const bookingData = {
    description: form.description,
    room_id: form.room_id,
    start_time: `${form.date} ${form.startTime}:00`,
    end_time: `${form.date} ${form.endTime}:00`,
    status: form.status
  };

  const success = await bookingStore.createBooking(bookingData);
  
  if (success) {
    emit('close');
    resetForm();
  }
  
  isSubmitting.value = false;
};

const resetForm = () => {
  form.description = '';
  form.room_id = null;
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

          <!-- Error Message -->
          <Transition name="slide-down">
            <div v-if="bookingStore.error" class="mb-6 p-4 rounded-2xl bg-red-50 border border-red-100 flex items-start gap-4 animate-shake">
              <div class="w-10 h-10 rounded-full bg-red-100 flex items-center justify-center shrink-0">
                <ExclamationCircleIcon class="w-6 h-6 text-red-600" />
              </div>
              <div class="flex flex-col gap-0.5 pt-0.5">
                <span class="text-sm font-bold text-red-900">Oops! Something went wrong</span>
                <p class="text-sm text-red-600 leading-relaxed">{{ bookingStore.error }}</p>
              </div>
            </div>
          </Transition>

          <form @submit.prevent="handleSubmit" class="flex flex-col gap-4">
            <AppInput 
              label="Meeting Description" 
              v-model="form.description" 
              placeholder="e.g. Sync Session" 
              required
            />
            
            <AppSelect 
              label="Room"
              v-model="form.room_id"
              :options="roomStore.rooms"
              placeholder="Select a room"
              required
            />

            <AppInput 
              label="Date" 
              v-model="form.date"
              type="date"
              required
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
                :is-loading="isSubmitting"
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

.animate-shake {
  animation: shake 0.5s cubic-bezier(.36,.07,.19,.97) both;
}

@keyframes shake {
  10%, 90% { transform: translate3d(-1px, 0, 0); }
  20%, 80% { transform: translate3d(2px, 0, 0); }
  30%, 50%, 70% { transform: translate3d(-4px, 0, 0); }
  40%, 60% { transform: translate3d(4px, 0, 0); }
}

.slide-down-enter-active,
.slide-down-leave-active {
  transition: all 0.3s ease-out;
}

.slide-down-enter-from {
  opacity: 0;
  transform: translateY(-20px);
}

.slide-down-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
