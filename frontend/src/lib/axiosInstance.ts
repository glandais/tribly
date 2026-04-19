import Axios, { AxiosRequestConfig, AxiosError, InternalAxiosRequestConfig } from 'axios'
import { useAuthStore } from '../store/authStore'
import { notifications } from '@mantine/notifications'
import i18next from 'i18next'
import type { ErrorResponse } from '../api/dto'
import { ApiClientError } from './apiError'

// Create axios instance with credentials support for cookies
export const AXIOS_INSTANCE = Axios.create({
  withCredentials: true,
})

// Track if we're currently refreshing to avoid multiple refresh calls
let isRefreshing = false
let failedQueue: Array<{
  resolve: (value: unknown) => void
  reject: (error: unknown) => void
  config: InternalAxiosRequestConfig
}> = []

const processQueue = (error: unknown | null, token: string | null = null) => {
  failedQueue.forEach(({ resolve, reject, config }) => {
    if (error) {
      reject(error)
    } else {
      if (token) {
        config.headers.Authorization = `Bearer ${token}`
      }
      resolve(AXIOS_INSTANCE(config))
    }
  })
  failedQueue = []
}

// Auth and language interceptor
AXIOS_INSTANCE.interceptors.request.use(
  (config) => {
    const token = useAuthStore.getState().getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    config.headers['Accept-Language'] = i18next.language
    return config
  },
  (error) => Promise.reject(error)
)

// Response interceptor for token refresh
AXIOS_INSTANCE.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean }

    // Only try to refresh on 401 errors
    if (error.response?.status !== 401 || !originalRequest || originalRequest._retry) {
      return Promise.reject(error)
    }

    // Don't try to refresh auth endpoints
    if (originalRequest.url?.includes('/api/auth/')) {
      return Promise.reject(error)
    }

    // Anonymous users have no session to refresh. A 401 on a public page
    // (e.g. an endpoint that requires auth called while browsing anonymously)
    // must not trigger a redirect to login — just propagate the error.
    if (!useAuthStore.getState().isAuthenticated) {
      return Promise.reject(error)
    }

    if (isRefreshing) {
      // If already refreshing, queue this request
      return new Promise((resolve, reject) => {
        failedQueue.push({ resolve, reject, config: originalRequest })
      })
    }

    originalRequest._retry = true
    isRefreshing = true

    try {
      const response = await fetch('/api/auth/refresh', {
        method: 'POST',
        credentials: 'include',
      })

      if (response.ok) {
        const data = await response.json()
        useAuthStore.getState().setAccessToken(data.accessToken)
        useAuthStore.getState().setUser(data.user)

        // Retry original request with new token
        originalRequest.headers.Authorization = `Bearer ${data.accessToken}`
        processQueue(null, data.accessToken)
        return AXIOS_INSTANCE(originalRequest)
      } else {
        // Refresh failed, clear auth state and redirect to login
        processQueue(error)
        useAuthStore.getState().setUser(null)
        useAuthStore.getState().setAccessToken(null)
        window.location.href = '/login'
        return Promise.reject(error)
      }
    } catch (refreshError) {
      processQueue(refreshError)
      useAuthStore.getState().setUser(null)
      useAuthStore.getState().setAccessToken(null)
      window.location.href = '/login'
      return Promise.reject(refreshError)
    } finally {
      isRefreshing = false
    }
  }
)

// Orval mutator function - returns unwrapped data (T, not AxiosResponse<T>)
export const axiosMutator = <T>(
  config: AxiosRequestConfig,
  options?: AxiosRequestConfig
): Promise<T> => {
  return AXIOS_INSTANCE({ ...config, ...options })
    .then(({ data }) => data)
    .catch((error) => {
      // Don't show toast for cancelled requests (e.g., component unmount)
      if (Axios.isCancel(error)) {
        throw error
      }

      if (Axios.isAxiosError(error)) {
        const axiosError = error as AxiosError
        const errorData = axiosError.response?.data as ErrorResponse | undefined

        // Don't show toast for 401 errors (handled by interceptor)
        if (axiosError.response?.status === 401) {
          throw error
        }

        if (errorData?.code) {
          const apiError = new ApiClientError(axiosError.status || 500, errorData)
          notifications.show({
            message: i18next.t('errors.api.' + errorData.code, errorData.errorDetails || {}),
            color: 'red',
          })
          throw apiError
        }
      }
      throw error
    })
}

export type ErrorType<ApiClientError> = AxiosError<ApiClientError>
export type BodyType<BodyData> = BodyData
