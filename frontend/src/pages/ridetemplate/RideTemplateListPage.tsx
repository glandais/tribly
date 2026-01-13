import { useState, useCallback } from 'react'
import { Link, Navigate, useParams } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { notifications } from '@mantine/notifications'
import { paths } from '../../config/paths'
import { IconPlus, IconFiles, IconPencil, IconTrash, IconUsersGroup } from '@tabler/icons-react'
import {
  Box,
  Button,
  Group,
  Stack,
  Title,
  Text,
  Paper,
  Center,
  Badge,
  ActionIcon,
} from '@mantine/core'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import {
  useListTemplates,
  useDeleteTemplate,
  getListTemplatesQueryKey,
  listTemplates,
} from '@/api/endpoints/ride-templates/ride-templates'
import { LoadingPage, LoadingSpinner } from '../../components/common/LoadingSpinner'
import { TeamAdminLayout } from '../../components/team/TeamAdminLayout'
import { Pagination } from '../../components/common/Pagination'
import { usePaginatedQuery } from '../../hooks/usePaginatedQuery'
import { SearchInput } from '../../components/common/SearchInput'
import { ConfirmDialog } from '../../components/common/ConfirmDialog'
import { MarkdownDisplay } from '../../components/common/MarkdownDisplay'
import type { RideTemplateDto } from '@/api/dto'
import { useCanonicalPath } from '../../hooks/useCanonicalPath'
import { useUnits } from '@/hooks/useUnits'

export function RideTemplateListPage() {
  const { t } = useTranslation()
  const { speed } = useUnits()
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const queryClient = useQueryClient()
  const [page, setPage] = useState(0)
  const [search, setSearch] = useState('')
  const [templateToDelete, setTemplateToDelete] = useState<RideTemplateDto | null>(null)
  const pageSize = 20

  const { data: team, isLoading: isLoadingTeam } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })
  const { data: templatesData, isLoading: isLoadingTemplates } = useListTemplates(teamSlug!, {
    search: search || undefined,
    page,
    size: pageSize,
  })
  const deleteMutation = useDeleteTemplate()

  const resetPage = () => setPage(0)

  const prefetchPage = useCallback(
    (prefetchPageNum: number) => ({
      queryKey: getListTemplatesQueryKey(teamSlug, {
        search: search || undefined,
        page: prefetchPageNum,
        size: pageSize,
      }),
      queryFn: () =>
        listTemplates(teamSlug!, {
          search: search || undefined,
          page: prefetchPageNum,
          size: pageSize,
        }),
    }),
    [teamSlug, search, pageSize]
  )

  const { totalPages } = usePaginatedQuery({
    page,
    pageSize,
    totalItems: templatesData?.total ?? 0,
    prefetchPage,
  })

  useCanonicalPath(team ? paths.rideTemplates(team.slug) : undefined)

  if (isLoadingTeam) {
    return <LoadingPage message={t('loading')} />
  }

  if (!team) {
    return <Navigate to={paths.teams()} replace />
  }

  const canManage = team.role === 'ADMIN' || team.role === 'ORGANIZER'

  const handleDelete = (template: RideTemplateDto) => {
    setTemplateToDelete(template)
  }

  const confirmDelete = () => {
    if (templateToDelete && teamSlug) {
      deleteMutation.mutate(
        { teamSlug: teamSlug, templateSlug: templateToDelete.slug },
        {
          onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: getListTemplatesQueryKey(teamSlug) })
            notifications.show({
              message: t('rideTemplates.notifications.deleted'),
              color: 'green',
            })
            setTemplateToDelete(null)
          },
        }
      )
    }
  }

  return (
    <TeamAdminLayout team={team} currentTab="ride-templates">
      <Stack py="lg" gap="lg">
        <Group justify="space-between">
          <Title order={2}>{t('rideTemplates.list.title')}</Title>
          {canManage && (
            <Button
              component={Link}
              to={paths.rideTemplateNew(teamSlug!)}
              leftSection={<IconPlus size={16} />}
            >
              {t('rideTemplates.create.title')}
            </Button>
          )}
        </Group>

        {/* Search Input */}
        <SearchInput
          id="templates-search"
          value={search}
          onChange={(value) => {
            setSearch(value)
            resetPage()
          }}
          placeholder={t('rideTemplates.list.search.placeholder')}
          label={t('rideTemplates.list.search.label')}
        />

        {/* Templates List */}
        {isLoadingTemplates ? (
          <Center py="xl">
            <LoadingSpinner />
          </Center>
        ) : templatesData?.templates && templatesData.templates.length > 0 ? (
          <>
            <Stack gap="md">
              {templatesData.templates.map((template) => (
                <Paper key={template.id} withBorder p="md">
                  <Group justify="space-between" align="flex-start" wrap="nowrap">
                    <Stack gap="xs" style={{ flex: 1, minWidth: 0 }}>
                      <Text size="lg" fw={500}>
                        {template.name}
                      </Text>
                      {template.markdown && (
                        <Box>
                          <MarkdownDisplay
                            markdown={template.markdown}
                            preview={true}
                            maxLength={150}
                          />
                        </Box>
                      )}
                      <Group gap="md">
                        <Group gap="xs">
                          <IconUsersGroup size={16} />
                          <Text size="sm" c="dimmed">
                            {t('groups.groupCount', { count: template.groupCount })}
                          </Text>
                        </Group>
                        <Badge
                          variant="light"
                          color={template.visibility === 'PUBLIC' ? 'green' : 'gray'}
                        >
                          {t(
                            `visibility.${template.visibility.toLowerCase() as 'public' | 'team'}`
                          )}
                        </Badge>
                      </Group>
                      {template.groups && template.groups.length > 0 && (
                        <Group gap="xs" mt="xs">
                          {template.groups.map((group) => (
                            <Badge key={group.id} variant="light" color="primary" size="sm">
                              {group.name}
                              {group.averageSpeed && (
                                <Text span ml={4} c="dimmed">
                                  {speed(group.averageSpeed)}
                                </Text>
                              )}
                            </Badge>
                          ))}
                        </Group>
                      )}
                    </Stack>
                    {canManage && (
                      <Group gap="xs" ml="md">
                        <ActionIcon
                          component={Link}
                          to={paths.rideTemplateEdit(teamSlug!, template.slug)}
                          variant="subtle"
                          color="gray"
                          title={t('actions.edit')}
                        >
                          <IconPencil size={20} />
                        </ActionIcon>
                        <ActionIcon
                          variant="subtle"
                          color="danger"
                          onClick={() => handleDelete(template)}
                          title={t('actions.delete')}
                        >
                          <IconTrash size={20} />
                        </ActionIcon>
                      </Group>
                    )}
                  </Group>
                </Paper>
              ))}
            </Stack>

            <Box mt="xl">
              <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />
            </Box>
          </>
        ) : (
          <Paper withBorder py="xl">
            <Center>
              <Stack align="center" gap="md">
                <IconFiles size={48} color="var(--mantine-color-dimmed)" />
                <Title order={3}>
                  {search ? t('rideTemplates.list.noResults') : t('rideTemplates.list.empty.title')}
                </Title>
                {!search && (
                  <Text c="dimmed">
                    {canManage
                      ? t('rideTemplates.list.empty.admin')
                      : t('rideTemplates.list.empty.member')}
                  </Text>
                )}
                {canManage && !search && (
                  <Button component={Link} to={paths.rideTemplateNew(teamSlug!)} mt="sm">
                    {t('rideTemplates.create.title')}
                  </Button>
                )}
              </Stack>
            </Center>
          </Paper>
        )}
      </Stack>

      {/* Delete Confirmation Dialog */}
      <ConfirmDialog
        isOpen={!!templateToDelete}
        onClose={() => setTemplateToDelete(null)}
        onConfirm={confirmDelete}
        title={t('rideTemplates.confirmations.deleteTitle')}
        message={t('rideTemplates.confirmations.delete', { name: templateToDelete?.name })}
        confirmText={t('rideTemplates.buttons.delete')}
        variant="danger"
        isLoading={deleteMutation.isPending}
      />
    </TeamAdminLayout>
  )
}
