import { create } from 'zustand'
import { getKeycloak, initKeycloak, getUserProfile, KeycloakUserProfile } from '../config/keycloak'

export interface User {
  id: string
  email: string
  displayName: string
  avatarUrl?: string | null
  dbId?: string
}

export interface AuthState {
  user: User | null
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
  setUser: (user: User | null) => void
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

const mapKeycloakProfileToUser = (profile: KeycloakUserProfile | null): User | null => {
  if (!profile) return null

  return {
    id: profile.id,
    email: profile.email,
    displayName: profile.displayName,
  }
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

      if (authenticated) {
        const profile = getUserProfile()
        set({
          user: mapKeycloakProfileToUser(profile),
          isAuthenticated: true,
          isInitialized: true,
          isInitializing: false,
          isLoading: false,
        })
      } else {
        set({
          user: null,
          isAuthenticated: false,
          isInitialized: true,
          isInitializing: false,
          isLoading: false,
        })
      }
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
        redirectUri: window.location.origin + '/login',
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
