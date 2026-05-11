export interface User {
  id: number;
  name: string;
  email: string;
  username: string;
  role: 'SUPERADMIN' | 'EMPLOYEE';
  created_at?: string;
  updated_at?: string;
}

export interface UserRequest {
  name: string;
  email: string;
  username: string;
  password?: string;
  role: 'SUPERADMIN' | 'EMPLOYEE';
}

export interface UserState {
  users: User[];
  isLoading: boolean;
  error: string | null;
  currentPage: number;
  totalPages: number;
  hasMore: boolean;
}
