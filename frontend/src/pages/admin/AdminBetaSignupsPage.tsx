import { useTranslation } from 'react-i18next'
import { Stack, Table, Text, Skeleton, Alert, Paper, Center } from '@mantine/core'
import { IconMailFast } from '@tabler/icons-react'
import { AdminLayout } from '@/components/admin/AdminLayout'
import { Pagination } from '@/components/common/Pagination'
import { useListBetaSignups } from '@/api/endpoints/admin-beta-signups/admin-beta-signups'
import { useUrlFilters } from '@/hooks/useUrlFilters'
import { useScrollToListTop } from '@/hooks/useScrollToListTop'
import {
  adminBetaSignupFiltersSchema,
  adminBetaSignupFiltersAlias,
} from '@/hooks/filters/adminFilters'
import type { BetaSignupDto } from '@/api/dto'
import { FormattedDate } from '@/components/common/FormattedDate'

export function AdminBetaSignupsPage() {
  const { t } = useTranslation()

  const { filters, setFilters } = useUrlFilters({
    schema: adminBetaSignupFiltersSchema,
    alias: adminBetaSignupFiltersAlias,
  })
  const { listTopRef, scrollToListTop } = useScrollToListTop()

  const { data, isLoading, error } = useListBetaSignups(filters)

  const totalPages = data ? Math.ceil(data.total / filters.size) : 0

  return (
    <AdminLayout currentTab="beta-signups">
      <Stack mt="lg">
        {error && (
          <Alert color="red">
            {error instanceof Error ? error.message : t('errors.api.failedToLoad')}
          </Alert>
        )}

        {isLoading ? (
          <Stack>
            {Array.from({ length: 5 }).map((_, i) => (
              <Skeleton key={i} height={50} />
            ))}
          </Stack>
        ) : data && data.signups.length > 0 ? (
          <Stack>
            <Table.ScrollContainer ref={listTopRef} minWidth={500}>
              <Table striped highlightOnHover>
                <Table.Thead>
                  <Table.Tr>
                    <Table.Th>{t('admin.betaSignups.email')}</Table.Th>
                    <Table.Th>{t('admin.betaSignups.createdAt')}</Table.Th>
                  </Table.Tr>
                </Table.Thead>
                <Table.Tbody>
                  {data.signups.map((signup: BetaSignupDto) => (
                    <Table.Tr key={signup.id}>
                      <Table.Td>
                        <Text size="sm">{signup.email}</Text>
                      </Table.Td>
                      <Table.Td>
                        <Text size="sm" c="dimmed">
                          <FormattedDate date={signup.createdAt} />
                        </Text>
                      </Table.Td>
                    </Table.Tr>
                  ))}
                </Table.Tbody>
              </Table>
            </Table.ScrollContainer>

            <Pagination
              currentPage={filters.page}
              totalPages={totalPages}
              onPageChange={(page) => {
                setFilters({ page })
                scrollToListTop()
              }}
            />
          </Stack>
        ) : (
          <Paper withBorder p="xl" radius="md">
            <Center>
              <Stack align="center" gap="sm">
                <IconMailFast size={48} color="var(--mantine-color-dimmed)" />
                <Text fw={500}>{t('admin.betaSignups.empty.title')}</Text>
                <Text c="dimmed">{t('admin.betaSignups.empty.description')}</Text>
              </Stack>
            </Center>
          </Paper>
        )}
      </Stack>
    </AdminLayout>
  )
}
