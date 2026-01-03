import {
  TeamsApi,
  RidesApi,
  RideTemplatesApi,
  PostsApi,
  PublicationsApi,
  RoutesApi,
  TeamMembersApi,
  UsersApi,
  ConfigurationApi,
  ErrorResponse,
  AssetsApi,
  PlacesApi,
  TripsApi,
  RouterApi,
  RideCommentsApi,
  PostCommentsApi,
  TripCommentsApi,
  RouteCommentsApi,
} from '../api/api'
import { Configuration } from '../api/configuration'
import { toast } from 'sonner'
import i18next from 'i18next'
import { useAuthStore } from '../store/authStore'
import axios, { AxiosError, AxiosResponse } from 'axios'

// Create custom axios instance with auth interceptor
const axiosInstance = axios.create()

// Add request interceptor to automatically inject Bearer token
axiosInstance.interceptors.request.use(
  (config) => {
    const token = useAuthStore.getState().getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// Configuration with dynamic Bearer token (retrieved fresh on each request)
// Note: basePath is empty because generated API code already includes /api prefix
// Note: No Content-Type header in baseOptions - let Axios and generated code handle it
const apiConfiguration = new Configuration({
  basePath: '',
  baseOptions: {},
})

// Singleton API instances (reused across calls, token injected via axios interceptor)
export const assetsApi = new AssetsApi(apiConfiguration, '', axiosInstance)
export const teamsApi = new TeamsApi(apiConfiguration, '', axiosInstance)
export const teamMembersApi = new TeamMembersApi(apiConfiguration, '', axiosInstance)
export const ridesApi = new RidesApi(apiConfiguration, '', axiosInstance)
export const rideTemplatesApi = new RideTemplatesApi(apiConfiguration, '', axiosInstance)
export const postsApi = new PostsApi(apiConfiguration, '', axiosInstance)
export const publicationsApi = new PublicationsApi(apiConfiguration, '', axiosInstance)
export const routesApi = new RoutesApi(apiConfiguration, '', axiosInstance)
export const usersApi = new UsersApi(apiConfiguration, '', axiosInstance)
export const configurationApi = new ConfigurationApi(apiConfiguration, '', axiosInstance)
export const placesApi = new PlacesApi(apiConfiguration, '', axiosInstance)
export const tripsApi = new TripsApi(apiConfiguration, '', axiosInstance)
export const routerApi = new RouterApi(apiConfiguration, '', axiosInstance)
export const rideCommentsApi = new RideCommentsApi(apiConfiguration, '', axiosInstance)
export const postCommentsApi = new PostCommentsApi(apiConfiguration, '', axiosInstance)
export const tripCommentsApi = new TripCommentsApi(apiConfiguration, '', axiosInstance)
export const routeCommentsApi = new RouteCommentsApi(apiConfiguration, '', axiosInstance)

export class ApiClientError extends Error {
  constructor(
    public status: number,
    public error: ErrorResponse
  ) {
    super(error?.message || 'API Client Error')
    this.name = 'ApiClientError'
  }
}

// Response unwrapper with automatic error handling (AxiosResponse<T> -> T)
export const unwrapResponse = <T>(promise: Promise<AxiosResponse<T>>): Promise<T> => {
  return promise
    .then((response) => response.data)
    .catch((error) => {
      if (axios.isAxiosError(error)) {
        const axiosError = error as AxiosError
        const errorData = axiosError.response?.data as ErrorResponse
        const apiError = new ApiClientError(axiosError.status || 500, errorData)

        // Show error notification
        toast.error(errorData?.message || i18next.t('errors:api.unknown'))

        throw apiError
      }
      throw new Error('An unexpected error occurred')
    })
}
