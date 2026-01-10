import Axios, { AxiosRequestConfig, AxiosError } from 'axios'
import { useAuthStore } from '../store/authStore'
import { notifications } from '@mantine/notifications'
import i18next from 'i18next'
import type { ErrorResponse } from '../api/dto'
import { ApiClientError } from './apiError'

export const AXIOS_INSTANCE = Axios.create()

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
        const errorData = axiosError.response?.data as ErrorResponse
        const apiError = new ApiClientError(axiosError.status || 500, errorData)
        notifications.show({
          message: i18next.t('errors.api.' + errorData.code, errorData.errorDetails || {}),
          color: 'red',
        })
        throw apiError
      }
      throw error
    })
}

export type ErrorType<ApiClientError> = AxiosError<ApiClientError>
export type BodyType<BodyData> = BodyData
