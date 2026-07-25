import { useQueryClient } from '@tanstack/react-query'
import { notifications } from '@mantine/notifications'
import i18next from 'i18next'
import {
  useGetLatestExport,
  useRequestExport,
  getGetLatestExportQueryKey,
} from '@/api/endpoints/users/users'
import { UserExportStatus, type UserExportDto } from '@/api/dto'
import { useAuth } from './useAuth'

const IN_FLIGHT: UserExportStatus[] = [UserExportStatus.PENDING, UserExportStatus.PROCESSING]

/**
 * Personal data export (GDPR).
 *
 * The archive is built in the background and the download link arrives by email, so this only ever
 * requests one and watches its status — it never downloads anything itself.
 */
export function useDataExport() {
  const queryClient = useQueryClient()
  const { isAuthenticated } = useAuth()

  const { data: latestExport, isLoading } = useGetLatestExport({
    query: {
      enabled: isAuthenticated,
      // Poll only while something is actually running, then stop.
      refetchInterval: (query) => {
        const status = (query.state.data as UserExportDto | undefined)?.status
        return status && IN_FLIGHT.includes(status) ? 5000 : false
      },
    },
  })

  const { mutate: request, isPending: isRequesting } = useRequestExport({
    mutation: {
      onSuccess: (created) => {
        queryClient.setQueryData(getGetLatestExportQueryKey(), created)
        notifications.show({
          message: i18next.t('profile.dataExport.requested'),
          color: 'green',
        })
      },
      // Failures (429 included) already surface a toast from the axios mutator, keyed by error code.
    },
  })

  const status = (latestExport as UserExportDto | undefined)?.status
  const isExporting = !!status && IN_FLIGHT.includes(status)

  return {
    latestExport: latestExport as UserExportDto | undefined,
    isLoading,
    requestExport: request,
    isRequesting,
    isExporting,
  }
}
