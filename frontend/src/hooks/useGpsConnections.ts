import { useCallback } from 'react'
import { useQueryClient, useMutation } from '@tanstack/react-query'
import { notifications } from '@mantine/notifications'
import i18next from 'i18next'
import { useAuth } from './useAuth'
import { AXIOS_INSTANCE } from '@/lib/axiosInstance'
import { getGetMeQueryKey } from '@/api/endpoints/users/users'
import { useGetAvailableServices } from '@/api/endpoints/gps-services/gps-services'
import type { GpsServiceType, GpsServiceConnectionDto } from '@/api/dto'

interface GpsOAuthUrlResponse {
  authorizationUrl: string
}

interface RouteUploadResponse {
  success: boolean
  message?: string
  externalRouteId?: string
}

/**
 * Hook for managing GPS service connections.
 */
export function useGpsConnections() {
  const queryClient = useQueryClient()
  const { user, refetchUser } = useAuth()

  // Fetch available services for this domain
  const { data: availableServices = [], isLoading: isLoadingAvailable } = useGetAvailableServices()

  const connectedServices = user?.connectedServices ?? []

  const isConnected = useCallback(
    (serviceType: GpsServiceType) => {
      return connectedServices.some((s) => s.serviceType === serviceType)
    },
    [connectedServices]
  )

  const getConnection = useCallback(
    (serviceType: GpsServiceType): GpsServiceConnectionDto | undefined => {
      return connectedServices.find((s) => s.serviceType === serviceType)
    },
    [connectedServices]
  )

  const isServiceAvailable = useCallback(
    (serviceType: GpsServiceType): boolean => {
      return availableServices.includes(serviceType)
    },
    [availableServices]
  )

  /**
   * Initiate OAuth flow for connecting a GPS service.
   * Opens the authorization URL in a new window/tab.
   */
  const initiateConnect = useCallback(async (serviceType: GpsServiceType) => {
    try {
      const response = await AXIOS_INSTANCE.get<GpsOAuthUrlResponse>(
        `/api/gps/connect/${serviceType}`
      )
      // Redirect to the authorization URL
      window.location.href = response.data.authorizationUrl
    } catch {
      notifications.show({
        message: i18next.t('gps.notifications.connectFailed'),
        color: 'red',
      })
    }
  }, [])

  /**
   * Disconnect a GPS service.
   */
  const disconnectMutation = useMutation({
    mutationFn: async (serviceType: GpsServiceType) => {
      await AXIOS_INSTANCE.delete(`/api/gps/disconnect/${serviceType}`)
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: getGetMeQueryKey() })
      refetchUser()
      notifications.show({
        message: i18next.t('gps.notifications.disconnected'),
        color: 'green',
      })
    },
    onError: () => {
      notifications.show({
        message: i18next.t('gps.notifications.disconnectFailed'),
        color: 'red',
      })
    },
  })

  /**
   * Upload a route to a connected GPS service.
   */
  const uploadRouteMutation = useMutation({
    mutationFn: async ({
      serviceType,
      teamSlug,
      routeSlug,
    }: {
      serviceType: GpsServiceType
      teamSlug: string
      routeSlug: string
    }) => {
      const response = await AXIOS_INSTANCE.post<RouteUploadResponse>(
        `/api/gps/upload/${serviceType}/${teamSlug}/${routeSlug}`
      )
      return response.data
    },
    onSuccess: (data) => {
      if (data.success) {
        notifications.show({
          message: i18next.t('gps.notifications.uploadSuccess'),
          color: 'green',
        })
      } else {
        notifications.show({
          message: data.message || i18next.t('gps.notifications.uploadFailed'),
          color: 'red',
        })
      }
    },
    onError: () => {
      notifications.show({
        message: i18next.t('gps.notifications.uploadFailed'),
        color: 'red',
      })
    },
  })

  return {
    availableServices,
    isLoadingAvailable,
    isServiceAvailable,
    connectedServices,
    isConnected,
    getConnection,
    initiateConnect,
    disconnect: disconnectMutation.mutate,
    isDisconnecting: disconnectMutation.isPending,
    uploadRoute: uploadRouteMutation.mutate,
    uploadRouteAsync: uploadRouteMutation.mutateAsync,
    isUploading: uploadRouteMutation.isPending,
  }
}
