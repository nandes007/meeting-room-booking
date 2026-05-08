export interface Room {
  id: number;
  name: string;
  capacity: number;
  location: string;
  is_available: boolean;
  created_at: string;
  updated_at: string;
}

export interface RoomState {
  rooms: Room[];
  isLoading: boolean;
  error: string | null;
}
