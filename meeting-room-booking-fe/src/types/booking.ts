export interface Booking {
  id: string;
  title: string;
  roomName: string;
  startTime: string; // HH:mm
  endTime: string;   // HH:mm
  date: string;      // YYYY-MM-DD
  duration: string;  // e.g., "30min"
  organizer: string;
  isRecurring?: boolean;
}

export interface DayScheduleData {
  date: Date;
  dateLabel: string; // e.g., "4 May"
  dayLabel: string;  // e.g., "Today", "Tomorrow", "Wednesday"
  bookings: Booking[];
}
