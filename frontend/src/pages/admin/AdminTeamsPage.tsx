import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import {
  Stack,
  Table,
  Badge,
  Group,
  Text,
  Skeleton,
  Alert,
  ActionIcon,
  Tooltip,
  Paper,
  Center,
  Select,
} from '@mantine/core'
import { IconBuildingCommunity, IconToggleLeft, IconToggleRight } from '@tabler/icons-react'
import { AdminLayout } from '@/components/admin/AdminLayout'
import { Pagination } from '@/components/common/Pagination'
import {
  useAdminListTeams,
  useAdminToggleTeamDeleted,
} from '@/api/endpoints/admin-teams/admin-teams'
import { useListDomains } from '@/api/endpoints/admin-domains/admin-domains'
import type { AdminTeamDto, Visibility } from '@/api/dto'

export function AdminTeamsPage() {
  const { t } = useTranslation()
  const [page, setPage] = useState(0)
  const [domainFilter, setDomainFilter] = useState<string | null>(null)
  const pageSize = 20

  const { data: domainsData } = useListDomains({ page: 0, size: 100 })
  const { data, isLoading, error } = useAdminListTeams({
    page,
    size: pageSize,
    domainId: domainFilter || undefined,
  })
  const toggleMutation = useAdminToggleTeamDeleted()

  const handleToggleDeleted = (teamId: string) => {
    toggleMutation.mutate({ teamId })
  }

  const totalPages = data ? Math.ceil(data.total / pageSize) : 0

  const domainOptions =
    domainsData?.domains.map((d) => ({
      value: d.id,
      label: d.name,
    })) || []

  return (
    <AdminLayout currentTab="teams">
      <Stack mt="lg">
        <Group>
          <Select
            placeholder={t('admin.teams.filterByDomain')}
            data={domainOptions}
            value={domainFilter}
            onChange={(value) => {
              setDomainFilter(value)
              setPage(0)
            }}
            clearable
            w={200}
          />
        </Group>

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
        ) : data && data.teams.length > 0 ? (
          <Stack>
            <Table.ScrollContainer minWidth={800}>
              <Table striped highlightOnHover>
                <Table.Thead>
                  <Table.Tr>
                    <Table.Th>{t('admin.teams.name')}</Table.Th>
                    <Table.Th>{t('admin.teams.domain')}</Table.Th>
                    <Table.Th>{t('admin.teams.slug')}</Table.Th>
                    <Table.Th ta="center">{t('admin.teams.visibility')}</Table.Th>
                    <Table.Th ta="center">{t('admin.teams.members')}</Table.Th>
                    <Table.Th ta="center">{t('admin.teams.status')}</Table.Th>
                    <Table.Th ta="center">{t('admin.teams.actions')}</Table.Th>
                  </Table.Tr>
                </Table.Thead>
                <Table.Tbody>
                  {data.teams.map((team: AdminTeamDto) => (
                    <Table.Tr key={team.id}>
                      <Table.Td>
                        <Text fw={500}>{team.name}</Text>
                      </Table.Td>
                      <Table.Td>
                        <Text size="sm" c="dimmed">
                          {team.domainName}
                        </Text>
                      </Table.Td>
                      <Table.Td>
                        <Text size="sm" c="dimmed">
                          {team.slug}
                        </Text>
                      </Table.Td>
                      <Table.Td ta="center">
                        <Badge variant="light">
                          {t(
                            `visibility.${team.visibility.toLowerCase() as Lowercase<Visibility>}`
                          )}
                        </Badge>
                      </Table.Td>
                      <Table.Td ta="center">{team.memberCount}</Table.Td>
                      <Table.Td ta="center">
                        <Badge color={team.deleted ? 'red' : 'green'}>
                          {team.deleted ? t('admin.teams.archived') : t('admin.teams.active')}
                        </Badge>
                      </Table.Td>
                      <Table.Td ta="center">
                        <Tooltip
                          label={team.deleted ? t('admin.teams.restore') : t('admin.teams.archive')}
                        >
                          <ActionIcon
                            variant="subtle"
                            color={team.deleted ? 'green' : 'red'}
                            onClick={() => handleToggleDeleted(team.id)}
                            loading={toggleMutation.isPending}
                          >
                            {team.deleted ? (
                              <IconToggleLeft size={20} />
                            ) : (
                              <IconToggleRight size={20} />
                            )}
                          </ActionIcon>
                        </Tooltip>
                      </Table.Td>
                    </Table.Tr>
                  ))}
                </Table.Tbody>
              </Table>
            </Table.ScrollContainer>

            <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />
          </Stack>
        ) : (
          <Paper withBorder p="xl" radius="md">
            <Center>
              <Stack align="center" gap="sm">
                <IconBuildingCommunity size={48} color="var(--mantine-color-dimmed)" />
                <Text fw={500}>{t('admin.teams.empty.title')}</Text>
                <Text c="dimmed">{t('admin.teams.empty.description')}</Text>
              </Stack>
            </Center>
          </Paper>
        )}
      </Stack>
    </AdminLayout>
  )
}
