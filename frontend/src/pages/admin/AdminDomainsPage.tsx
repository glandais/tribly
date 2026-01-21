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
  Button,
} from '@mantine/core'
import {
  IconWorld,
  IconToggleLeft,
  IconToggleRight,
  IconPlus,
  IconPencil,
} from '@tabler/icons-react'
import { AdminLayout } from '@/components/admin/AdminLayout'
import { DomainFormModal } from '@/components/admin/DomainFormModal'
import { Pagination } from '@/components/common/Pagination'
import { useListDomains, useToggleDomainActive } from '@/api/endpoints/admin-domains/admin-domains'
import type { AdminDomainDto } from '@/api/dto'

export function AdminDomainsPage() {
  const { t } = useTranslation()
  const [page, setPage] = useState(0)
  const pageSize = 20
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [editingDomain, setEditingDomain] = useState<AdminDomainDto | undefined>(undefined)

  const { data, isLoading, error } = useListDomains({ page, size: pageSize })
  const toggleMutation = useToggleDomainActive()

  const handleToggleActive = (domainId: string) => {
    toggleMutation.mutate({ domainId })
  }

  const handleCreateDomain = () => {
    setEditingDomain(undefined)
    setIsModalOpen(true)
  }

  const handleEditDomain = (domain: AdminDomainDto) => {
    setEditingDomain(domain)
    setIsModalOpen(true)
  }

  const handleCloseModal = () => {
    setIsModalOpen(false)
    setEditingDomain(undefined)
  }

  const totalPages = data ? Math.ceil(data.total / pageSize) : 0

  return (
    <AdminLayout currentTab="domains">
      <Stack mt="lg">
        <Group justify="flex-end">
          <Button leftSection={<IconPlus size={16} />} onClick={handleCreateDomain}>
            {t('admin.domains.create.button')}
          </Button>
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
        ) : data && data.domains.length > 0 ? (
          <Stack>
            <Table.ScrollContainer minWidth={800}>
              <Table striped highlightOnHover>
                <Table.Thead>
                  <Table.Tr>
                    <Table.Th>{t('admin.domains.name')}</Table.Th>
                    <Table.Th>{t('admin.domains.domain')}</Table.Th>
                    <Table.Th>{t('admin.domains.baseUrl')}</Table.Th>
                    <Table.Th ta="center">{t('admin.domains.teams')}</Table.Th>
                    <Table.Th ta="center">{t('admin.domains.users')}</Table.Th>
                    <Table.Th ta="center">{t('admin.domains.status')}</Table.Th>
                    <Table.Th ta="center">{t('admin.domains.actions')}</Table.Th>
                  </Table.Tr>
                </Table.Thead>
                <Table.Tbody>
                  {data.domains.map((domain: AdminDomainDto) => (
                    <Table.Tr key={domain.id}>
                      <Table.Td>
                        <Group gap="xs">
                          {domain.singleTeam && (
                            <Badge size="xs" variant="light">
                              {t('admin.domains.singleTeam')}
                            </Badge>
                          )}
                          <Text fw={500}>{domain.name}</Text>
                        </Group>
                      </Table.Td>
                      <Table.Td>
                        <Text size="sm" c="dimmed">
                          {domain.domain}
                        </Text>
                      </Table.Td>
                      <Table.Td>
                        <Text size="sm" c="dimmed">
                          {domain.baseUrl}
                        </Text>
                      </Table.Td>
                      <Table.Td ta="center">{domain.teamCount}</Table.Td>
                      <Table.Td ta="center">{domain.userCount}</Table.Td>
                      <Table.Td ta="center">
                        <Badge color={domain.active ? 'green' : 'gray'}>
                          {domain.active ? t('admin.status.active') : t('admin.status.inactive')}
                        </Badge>
                      </Table.Td>
                      <Table.Td ta="center">
                        <Group gap="xs" justify="center">
                          <Tooltip label={t('actions.edit')}>
                            <ActionIcon variant="subtle" onClick={() => handleEditDomain(domain)}>
                              <IconPencil size={18} />
                            </ActionIcon>
                          </Tooltip>
                          <Tooltip
                            label={
                              domain.active
                                ? t('admin.domains.deactivate')
                                : t('admin.domains.activate')
                            }
                          >
                            <ActionIcon
                              variant="subtle"
                              onClick={() => handleToggleActive(domain.id)}
                              loading={toggleMutation.isPending}
                            >
                              {domain.active ? (
                                <IconToggleRight size={20} />
                              ) : (
                                <IconToggleLeft size={20} />
                              )}
                            </ActionIcon>
                          </Tooltip>
                        </Group>
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
                <IconWorld size={48} color="var(--mantine-color-dimmed)" />
                <Text fw={500}>{t('admin.domains.empty.title')}</Text>
                <Text c="dimmed">{t('admin.domains.empty.description')}</Text>
                <Button leftSection={<IconPlus size={16} />} onClick={handleCreateDomain} mt="sm">
                  {t('admin.domains.create.button')}
                </Button>
              </Stack>
            </Center>
          </Paper>
        )}
      </Stack>

      <DomainFormModal isOpen={isModalOpen} onClose={handleCloseModal} domain={editingDomain} />
    </AdminLayout>
  )
}
