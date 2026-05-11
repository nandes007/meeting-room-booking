<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue';
import { XMarkIcon } from '@heroicons/vue/24/outline';
import AppInput from '@/components/shared/AppInput.vue';
import AppButton from '@/components/shared/AppButton.vue';
import AppSelect from '@/components/shared/AppSelect.vue';
import { useRoomStore } from '@/stores/roomStore';

interface Props {
  isOpen: boolean;
  selectedDate: string;
}

const props = defineProps<Props>();
const emit = defineEmits(['close', 'submit']);

const roomStore = useRoomStore();

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
});

watch(() => props.selectedDate, (newDate) => {
  form.date = newDate;
});

const handleSubmit = async () => {
  if (!form.room_id) return;
  
  isSubmitting.value = true;
  
  // Format to ISO 8601 or backend expected format (e.g. 2026-05-10 09:00:00)
  // The API docs say date-time, so ISO is usually preferred.
  // Example in docs: 2026-05-10 09:00:00
  const start_time = `${form.date} ${form.startTime}:00`;
  const end_time = `${form.date} ${form.endTime}:00`;
  
  emit('submit', {
    description: form.description,
    room_id: form.room_id,
    start_time,
    end_time,
    status: form.status
  });
  
  isSubmitting.value = false;
  resetForm();
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
