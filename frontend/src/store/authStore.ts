import { create } from 'zustand'
import type { UserDto } from '@/api/dto'
import { paths } from '@/config/paths'

export interface AuthState {
  user: UserDto | null
  accessToken: string | null
  hasPasskeys: boolean
  isAuthenticated: boolean
  isInitialized: boolean
  isInitializing: boolean
  isLoading: boolean
  error: string | null
}

export interface AuthActions {
  initialize: () => Promise<void>
  setAccessToken: (token: string | null) => void
  setUser: (user: UserDto | null) => void
  setHasPasskeys: (hasPasskeys: boolean) => void
  setLoading: (isLoading: boolean) => void
  setError: (error: string | null) => void
  clearError: () => void
  getToken: () => string | null
  logout: () => Promise<void>
  redirectToLogin: () => void
}

export type AuthStore = AuthState & AuthActions

const initialState: AuthState = {
  user: null,
  accessToken: null,
  hasPasskeys: false,
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
      // Try to refresh token on startup (will work if refresh_token cookie exists)
      const response = await fetch('/api/auth/refresh', {
        method: 'POST',
        credentials: 'include',
      })

      if (response.ok) {
        const data = await response.json()
        set({
          accessToken: data.accessToken,
          user: data.user,
          hasPasskeys: data.hasPasskeys ?? false,
          isAuthenticated: true,
          isInitialized: true,
          isInitializing: false,
          isLoading: false,
        })
      } else {
        // No valid session, but initialization succeeded
        set({
          isAuthenticated: false,
          isInitialized: true,
          isInitializing: false,
          isLoading: false,
        })
      }
    } catch (error) {
      console.error('Auth initialization failed:', error)
      set({
        isAuthenticated: false,
        isInitialized: true,
        isInitializing: false,
        isLoading: false,
      })
    }
  },

  setAccessToken: (token) => set({ accessToken: token }),

  setUser: (user) =>
    set({
      user,
      isAuthenticated: user !== null,
      isLoading: false,
    }),

  setHasPasskeys: (hasPasskeys) => set({ hasPasskeys }),

  setLoading: (isLoading) => set({ isLoading }),

  setError: (error) => set({ error, isLoading: false }),

  clearError: () => set({ error: null }),

  getToken: () => get().accessToken,

  logout: async () => {
    try {
      await fetch('/api/auth/logout', {
        method: 'POST',
        credentials: 'include',
      })
    } catch (error) {
      console.error('Logout failed:', error)
    }

    set({ ...initialState, isInitialized: true, isLoading: false })
    window.location.href = paths.login()
  },

  redirectToLogin: () => {
    window.location.href = paths.login()
  },
}))

export const selectUser = (state: AuthStore) => state.user
export const selectAccessToken = (state: AuthStore) => state.accessToken
export const selectHasPasskeys = (state: AuthStore) => state.hasPasskeys
export const selectIsAuthenticated = (state: AuthStore) => state.isAuthenticated
export const selectIsInitialized = (state: AuthStore) => state.isInitialized
export const selectIsLoading = (state: AuthStore) => state.isLoading
export const selectError = (state: AuthStore) => state.error
