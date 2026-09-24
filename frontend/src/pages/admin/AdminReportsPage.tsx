import { useQueryClient } from '@tanstack/react-query'
import { Box } from '@mantine/core'
import {
  useResolveAdminReports,
  getListAdminReportsQueryKey,
} from '@/api/endpoints/admin-reports/admin-reports'
import { getListTeamReportsQueryKey } from '@/api/endpoints/moderation/moderation'
import { AdminLayout } from '@/components/admin/AdminLayout'
import { ModerationQueue } from '@/components/moderation/ModerationQueue'
import { invalidateModeratedContent } from '@/lib/moderationCacheInvalidation'
import { useAdminReportsData } from './adminReportsData'

/** The platform moderation queue: every team of the domain, with who reported. */
export function AdminReportsPage() {
  const queryClient = useQueryClient()
  const { filters, setFilters, reports } = useAdminReportsData()
  const resolveMutation = useResolveAdminReports()

  return (
    <AdminLayout currentTab="reports">
      <Box mt="lg">
        <ModerationQueue
          scope="platform"
          status={filters.status}
          onStatusChange={(status) => setFilters({ status })}
          data={reports.data}
          isLoading={reports.isLoading}
          error={reports.error}
          onRetry={() => void reports.refetch()}
          resolve={(decision) =>
            resolveMutation.mutateAsync(
              { data: decision },
              {
                onSuccess: (_data, { data }) => {
                  void queryClient.invalidateQueries({ queryKey: getListAdminReportsQueryKey() })
                  const item = reports.data?.items.find(
                    (i) => i.targetType === data.targetType && i.targetId === data.targetId
                  )
                  if (item) {
                    void queryClient.invalidateQueries({
                      queryKey: getListTeamReportsQueryKey(item.teamSlug),
                    })
                  }
                  void invalidateModeratedContent(queryClient)
                },
              }
            )
          }
        />
      </Box>
    </AdminLayout>
  )
}
