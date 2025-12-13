import { create } from 'zustand';
import keycloak, {
  initKeycloak,
  getUserProfile,
  KeycloakUserProfile,
} from '../config/keycloak';

export interface User {
  id: string;
  email: string;
  displayName: string;
  avatarUrl: string | null;
  stravaId: string | null;
  locale?: string;
  timezone?: string;
  dbId?: number;
}

export interface AuthState {
  user: User | null;
  isAuthenticated: boolean;
  isInitialized: boolean;
  isLoading: boolean;
  error: string | null;
}

export interface AuthActions {
  initialize: () => Promise<void>;
  login: (idpHint?: string) => void;
  loginWithStrava: () => void;
  logout: () => void;
  setUser: (user: User | null) => void;
  setLoading: (isLoading: boolean) => void;
  setError: (error: string | null) => void;
  clearError: () => void;
  getToken: () => string | undefined;
}

export type AuthStore = AuthState & AuthActions;

const initialState: AuthState = {
  user: null,
  isAuthenticated: false,
  isInitialized: false,
  isLoading: true,
  error: null,
};

const mapKeycloakProfileToUser = (
  profile: KeycloakUserProfile | null
): User | null => {
  if (!profile) return null;

  return {
    id: profile.id,
    email: profile.email,
    displayName: profile.displayName,
    avatarUrl: profile.avatarUrl,
    stravaId: profile.stravaId,
  };
};

export const useAuthStore = create<AuthStore>()((set, get) => ({
  ...initialState,

  initialize: async () => {
    if (get().isInitialized) {
      return;
    }

    set({ isLoading: true });

    try {
      const authenticated = await initKeycloak();

      if (authenticated) {
        const profile = getUserProfile();
        set({
          user: mapKeycloakProfileToUser(profile),
          isAuthenticated: true,
          isInitialized: true,
          isLoading: false,
        });
      } else {
        set({
          user: null,
          isAuthenticated: false,
          isInitialized: true,
          isLoading: false,
        });
      }
    } catch (error) {
      console.error('Auth initialization failed:', error);
      set({
        error: 'Failed to initialize authentication',
        isInitialized: true,
        isLoading: false,
      });
    }
  },

  login: (idpHint?: string) => {
    const options: Keycloak.KeycloakLoginOptions = {
      redirectUri: window.location.origin + '/',
    };
    if (idpHint) {
      options.idpHint = idpHint;
    }
    keycloak.login(options);
  },

  loginWithStrava: () => {
    keycloak.login({
      redirectUri: window.location.origin + '/',
      idpHint: 'strava',
    });
  },

  logout: () => {
    keycloak.logout({
      redirectUri: window.location.origin + '/login',
    });
    set({ ...initialState, isInitialized: true, isLoading: false });
  },

  setUser: (user) =>
    set({ user, isAuthenticated: user !== null }),

  setLoading: (isLoading) => set({ isLoading }),

  setError: (error) => set({ error, isLoading: false }),

  clearError: () => set({ error: null }),

  getToken: () => keycloak.token,
}));

export const selectUser = (state: AuthStore) => state.user;
export const selectIsAuthenticated = (state: AuthStore) => state.isAuthenticated;
export const selectIsInitialized = (state: AuthStore) => state.isInitialized;
export const selectIsLoading = (state: AuthStore) => state.isLoading;
export const selectError = (state: AuthStore) => state.error;
