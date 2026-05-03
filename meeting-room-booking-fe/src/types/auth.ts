export interface User {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  birthDate: string;
}

export interface AuthState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
  pendingVerificationEmail: string | null;
}

export type AuthMode = 'login' | 'signup';
export type InputType = 'phone' | 'email';
