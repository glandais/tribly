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
  Checkbox,
} from '@mantine/core'
import { IconUsers, IconShieldCheck, IconShieldOff } from '@tabler/icons-react'
import { AdminLayout } from '@/components/admin/AdminLayout'
import { SearchInput } from '@/components/common/SearchInput'
import { Pagination } from '@/components/common/Pagination'
import { useListUsers, useAssignPlatformRole } from '@/api/endpoints/admin-users/admin-users'
import { useListDomains } from '@/api/endpoints/admin-domains/admin-domains'
import type { AdminUserDto } from '@/api/dto'
import { formatDate } from '@/utils/dateFormat'

export function AdminUsersPage() {
  const { t, i18n } = useTranslation()
  const [page, setPage] = useState(0)
  const [search, setSearch] = useState('')
  const [domainFilter, setDomainFilter] = useState<string | null>(null)
  const [adminOnly, setAdminOnly] = useState(false)
  const pageSize = 20

  const { data: domainsData } = useListDomains({ page: 0, size: 100 })
  const { data, isLoading, error } = useListUsers({
    page,
    size: pageSize,
    domainId: domainFilter || undefined,
    search: search || undefined,
    adminOnly: adminOnly || undefined,
  })
  const assignRoleMutation = useAssignPlatformRole()

  const handleTogglePlatformAdmin = (userId: string, currentRole: string | null | undefined) => {
    const newRole = currentRole === 'PLATFORM_ADMIN' ? undefined : 'PLATFORM_ADMIN'
    assignRoleMutation.mutate({
      userId,
      data: { role: newRole },
    })
  }

  const totalPages = data ? Math.ceil(data.total / pageSize) : 0

  const domainOptions =
    domainsData?.domains.map((d) => ({
      value: d.id,
      label: d.name,
    })) || []

  const resetPage = () => setPage(0)

  return (
    <AdminLayout currentTab="users">
      <Stack mt="lg">
        <Group align="flex-end" wrap="wrap">
          <SearchInput
            id="user-search"
            value={search}
            onChange={(value) => {
              setSearch(value)
              resetPage()
            }}
            placeholder={t('admin.users.searchPlaceholder')}
            label={t('admin.users.search')}
            style={{ flex: 1, minWidth: 200 }}
          />
          <Select
            placeholder={t('admin.users.filterByDomain')}
            data={domainOptions}
            value={domainFilter}
            onChange={(value) => {
              setDomainFilter(value)
              resetPage()
            }}
            clearable
            w={200}
          />
          <Checkbox
            label={t('admin.users.adminOnly')}
            checked={adminOnly}
            onChange={(e) => {
              setAdminOnly(e.currentTarget.checked)
              resetPage()
            }}
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
        ) : data && data.users.length > 0 ? (
          <Stack>
            <Table.ScrollContainer minWidth={800}>
              <Table striped highlightOnHover>
                <Table.Thead>
                  <Table.Tr>
                    <Table.Th>{t('admin.users.email')}</Table.Th>
                    <Table.Th>{t('admin.users.displayName')}</Table.Th>
                    <Table.Th>{t('admin.users.domain')}</Table.Th>
                    <Table.Th ta="center">{t('admin.users.emailVerified')}</Table.Th>
                    <Table.Th ta="center">{t('admin.users.platformRole')}</Table.Th>
                    <Table.Th>{t('admin.users.createdAt')}</Table.Th>
                    <Table.Th ta="center">{t('admin.users.actions')}</Table.Th>
                  </Table.Tr>
                </Table.Thead>
                <Table.Tbody>
                  {data.users.map((user: AdminUserDto) => (
                    <Table.Tr key={user.id}>
                      <Table.Td>
                        <Text size="sm">{user.email}</Text>
                      </Table.Td>
                      <Table.Td>
                        <Text fw={500}>{user.displayName}</Text>
                      </Table.Td>
                      <Table.Td>
                        <Text size="sm" c="dimmed">
                          {user.domainName}
                        </Text>
                      </Table.Td>
                      <Table.Td ta="center">
                        <Badge color={user.emailVerified ? 'green' : 'gray'} size="sm">
                          {user.emailVerified
                            ? t('admin.users.verified')
                            : t('admin.users.unverified')}
                        </Badge>
                      </Table.Td>
                      <Table.Td ta="center">
                        {user.platformRole === 'PLATFORM_ADMIN' && (
                          <Badge color="blue">{t('admin.users.platformAdmin')}</Badge>
                        )}
                      </Table.Td>
                      <Table.Td>
                        <Text size="sm" c="dimmed">
                          {formatDate(user.createdAt, i18n.language)}
                        </Text>
                      </Table.Td>
                      <Table.Td ta="center">
                        <Tooltip
                          label={
                            user.platformRole === 'PLATFORM_ADMIN'
                              ? t('admin.users.removeAdmin')
                              : t('admin.users.makeAdmin')
                          }
                        >
                          <ActionIcon
                            variant="subtle"
                            color={user.platformRole === 'PLATFORM_ADMIN' ? 'red' : 'blue'}
                            onClick={() => handleTogglePlatformAdmin(user.id, user.platformRole)}
                            loading={assignRoleMutation.isPending}
                          >
                            {user.platformRole === 'PLATFORM_ADMIN' ? (
                              <IconShieldOff size={20} />
                            ) : (
                              <IconShieldCheck size={20} />
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
                <IconUsers size={48} color="var(--mantine-color-dimmed)" />
                <Text fw={500}>{t('admin.users.empty.title')}</Text>
                <Text c="dimmed">{t('admin.users.empty.description')}</Text>
              </Stack>
            </Center>
          </Paper>
        )}
      </Stack>
    </AdminLayout>
  )
}
