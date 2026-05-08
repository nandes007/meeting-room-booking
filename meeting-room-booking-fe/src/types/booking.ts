export interface Booking {
  id: number;
  user_id: number;
  room_id: number;
  start_time: string;
  end_time: string;
  status: string;
  description: string;
  created_at: string;
  updated_at: string;
}

export interface DayScheduleData {
  date: Date;
  dateLabel: string; // e.g., "4 May"
  dayLabel: string;  // e.g., "Today", "Tomorrow", "Wednesday"
  bookings: Booking[];
}
