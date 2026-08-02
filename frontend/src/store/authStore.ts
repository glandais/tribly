import { create, type StoreApi } from 'zustand'
import type { UserDto } from '@/api/dto'
import { paths } from '@/config/paths'
import { getSSRAuth } from '@/lib/ssrContext'
import type { SsrAuthSnapshot } from '@/lib/requestContext'

const isServer = typeof window === 'undefined'

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

const authStore = create<AuthStore>()((set, get) => ({
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

/**
 * The auth state for the request being rendered, assembled from the per-request AsyncLocalStorage
 * snapshot rather than from the store's own (process-wide) state.
 *
 * The Zustand store is a module singleton: in the Node SSR process it is shared by every concurrent
 * render. Writing a visitor's session into it would serve that session to whoever else is being
 * rendered at the same instant. So the server never writes it — it reads through here instead, and
 * only the actions (which are stateless closures) come from the store itself.
 *
 * With no session the result is byte-for-byte the old anonymous state, including `isLoading: true`.
 * That is deliberate: it keeps `ProtectedRoute` on its loading branch for anonymous visitors, so
 * guarded routes behave exactly as they did before.
 */
function ssrAuthState(): AuthStore {
  const snapshot = getSSRAuth()
  const actions = authStore.getState()
  if (!snapshot) {
    return { ...actions, ...initialState }
  }
  return {
    ...actions,
    ...initialState,
    accessToken: snapshot.accessToken,
    user: snapshot.user,
    hasPasskeys: snapshot.hasPasskeys,
    isAuthenticated: true,
    isInitialized: true,
    isLoading: false,
  }
}

function useAuthStoreOnServer(): AuthStore
function useAuthStoreOnServer<T>(selector: (state: AuthStore) => T): T
function useAuthStoreOnServer<T>(selector?: (state: AuthStore) => T): T | AuthStore {
  const state = ssrAuthState()
  return selector ? selector(state) : state
}

type UseAuthStore = typeof useAuthStoreOnServer &
  Pick<StoreApi<AuthStore>, 'getState' | 'setState' | 'subscribe'>

/**
 * Same call signature as the raw Zustand hook — `useAuthStore()` or `useAuthStore(selector)` — but
 * request-scoped on the server. See {@link ssrAuthState}.
 */
function useAuthStoreOnClient(): AuthStore
function useAuthStoreOnClient<T>(selector: (state: AuthStore) => T): T
function useAuthStoreOnClient<T>(selector?: (state: AuthStore) => T): T | AuthStore {
  return selector ? authStore(selector) : authStore()
}

// Assign onto a fresh function, never onto `authStore` itself: overwriting the Zustand store's own
// setState with the wrapper below would make the wrapper call itself.
export const useAuthStore: UseAuthStore = Object.assign(
  isServer ? useAuthStoreOnServer : useAuthStoreOnClient,
  {
    getState: () => (isServer ? ssrAuthState() : authStore.getState()),
    setState: ((...args: Parameters<StoreApi<AuthStore>['setState']>) => {
      if (isServer) {
        throw new Error(
          '[SSR] useAuthStore.setState() must not be called on the server: the store is shared by ' +
            'all concurrent renders. Put the session in the request store (SsrRequestStore.auth).'
        )
      }
      return authStore.setState(...args)
    }) as StoreApi<AuthStore>['setState'],
    subscribe: authStore.subscribe,
  }
)

/**
 * Seed the client store from the session the server rendered with, before `hydrateRoot`.
 *
 * This is what makes the first client render byte-identical to the server markup — without it the
 * client would start anonymous and React would report a hydration mismatch on every page. It also
 * means the boot-time `POST /api/auth/refresh` and the post-hydration global invalidation are no
 * longer needed: the session is already known and the dehydrated cache already holds the
 * authenticated view.
 */
let hydratedFromSSR = false

/**
 * Whether the markup this client hydrated was already rendered with the session. When true there is
 * nothing to repair after login settles — the cache holds the authenticated view already.
 */
export function wasHydratedFromSSR(): boolean {
  return hydratedFromSSR
}

export function hydrateAuthFromSSR(snapshot: SsrAuthSnapshot): void {
  hydratedFromSSR = true
  authStore.setState({
    accessToken: snapshot.accessToken,
    user: snapshot.user,
    hasPasskeys: snapshot.hasPasskeys,
    isAuthenticated: true,
    isInitialized: true,
    isLoading: false,
  })
}

export const selectUser = (state: AuthStore) => state.user
export const selectAccessToken = (state: AuthStore) => state.accessToken
export const selectHasPasskeys = (state: AuthStore) => state.hasPasskeys
export const selectIsAuthenticated = (state: AuthStore) => state.isAuthenticated
export const selectIsInitialized = (state: AuthStore) => state.isInitialized
export const selectIsLoading = (state: AuthStore) => state.isLoading
export const selectError = (state: AuthStore) => state.error
export const selectIsPlatformAdmin = (state: AuthStore) =>
  state.user?.platformRole === 'PLATFORM_ADMIN'
