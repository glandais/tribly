import { create } from 'zustand'
import { getKeycloak, initKeycloak } from '../config/keycloak'
import type { UserDto } from '@/api/dto'
import { paths } from '@/config/paths'

export interface AuthState {
  user: UserDto | null
  isAuthenticated: boolean
  isInitialized: boolean
  isInitializing: boolean
  isLoading: boolean
  error: string | null
}

export interface AuthActions {
  initialize: () => Promise<void>
  login: () => void
  logout: () => void
  setUser: (user: UserDto | null) => void
  setLoading: (isLoading: boolean) => void
  setError: (error: string | null) => void
  clearError: () => void
  getToken: () => string | undefined
}

export type AuthStore = AuthState & AuthActions

const initialState: AuthState = {
  user: null,
  isAuthenticated: false,
  isInitialized: false,
  isInitializing: false,
  isLoading: true,
  error: null,
}

export const useAuthStore = create<AuthStore>()((set, get) => ({
  ...initialState,

  initialize: async () => {
    if (get().isInitialized || get().isInitializing) {
      return
    }

    set({ isInitializing: true, isLoading: true })

    try {
      const authenticated = await initKeycloak()

      // Don't set user here - useAuth will fetch from /me
      // Keep isLoading: true if authenticated, useAuth will set it to false after user is loaded
      set({
        user: null,
        isAuthenticated: authenticated,
        isInitialized: true,
        isInitializing: false,
        isLoading: authenticated, // Stay loading until user is fetched from /me
      })
    } catch (error) {
      console.error('Auth initialization failed:', error)
      set({
        error: 'Failed to initialize authentication',
        isInitialized: true,
        isInitializing: false,
        isLoading: false,
      })
    }
  },

  login: () => {
    const keycloak = getKeycloak()
    if (keycloak) {
      keycloak.login({
        redirectUri: window.location.origin + '/',
      })
    }
  },

  logout: () => {
    const keycloak = getKeycloak()
    if (keycloak) {
      keycloak.logout({
        redirectUri: window.location.origin + paths.login(),
      })
    }
    set({ ...initialState, isInitialized: true, isLoading: false })
  },

  setUser: (user) => set({ user, isAuthenticated: user !== null }),

  setLoading: (isLoading) => set({ isLoading }),

  setError: (error) => set({ error, isLoading: false }),

  clearError: () => set({ error: null }),

  getToken: () => getKeycloak()?.token,
}))

export const selectUser = (state: AuthStore) => state.user
export const selectIsAuthenticated = (state: AuthStore) => state.isAuthenticated
export const selectIsInitialized = (state: AuthStore) => state.isInitialized
export const selectIsLoading = (state: AuthStore) => state.isLoading
export const selectError = (state: AuthStore) => state.error
