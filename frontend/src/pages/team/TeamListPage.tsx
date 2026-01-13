import { useState, useCallback } from 'react'
import { Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { paths } from '../../config/paths'
import { IconPlus, IconUsersGroup } from '@tabler/icons-react'
import { useListTeams, listTeams, getListTeamsQueryKey } from '@/api/endpoints/teams/teams'
import { MinRole } from '@/api/dto'
import { useAuth } from '../../hooks/useAuth'
import { usePaginatedQuery } from '../../hooks/usePaginatedQuery'
import { TeamCard, TeamCardSkeleton } from '../../components/card'
import { Pagination } from '../../components/common/Pagination'
import { SearchInput } from '../../components/common/SearchInput'
import { HomeLayout } from '../../components/home/HomeLayout'
import {
  Select,
  Box,
  Group,
  Title,
  Text,
  Stack,
  Button,
  SimpleGrid,
  Paper,
  Center,
  Alert,
} from '@mantine/core'

type FilterValue = 'all' | 'member' | 'organizer' | 'admin'

const filterToMinRole: Record<FilterValue, MinRole | undefined> = {
  all: undefined,
  member: MinRole.MEMBER,
  organizer: MinRole.ORGANIZER,
  admin: MinRole.ADMIN,
}

export function TeamListPage() {
  const { t } = useTranslation()
  const [search, setSearch] = useState('')
  const { isAuthenticated } = useAuth()

  const [page, setPage] = useState(0)
  const pageSize = 12
  const [filter, setFilter] = useState<FilterValue>(isAuthenticated ? 'member' : 'all')

  const {
    data: teamsData,
    isLoading,
    error,
  } = useListTeams({
    search,
    page,
    size: pageSize,
    minRole: filterToMinRole[filter],
  })

  const prefetchPage = useCallback(
    (prefetchPageNum: number) => ({
      queryKey: getListTeamsQueryKey({
        search,
        page: prefetchPageNum,
        size: pageSize,
        minRole: filterToMinRole[filter],
      }),
      queryFn: () =>
        listTeams({
          search,
          page: prefetchPageNum,
          size: pageSize,
          minRole: filterToMinRole[filter],
        }),
    }),
    [search, filter, pageSize]
  )

  const { totalPages } = usePaginatedQuery({
    page,
    pageSize,
    totalItems: teamsData?.total ?? 0,
    prefetchPage,
  })

  const teams = teamsData?.teams

  const resetPage = () => setPage(0)

  return (
    <HomeLayout currentTab="teams">
      <Stack>
        <Group justify="space-between" align="flex-start" wrap="wrap">
          <div>
            <Title order={2}>{t('teams.title')}</Title>
            <Text c="dimmed" mt={4}>
              {t('teams.list.subtitle')}
            </Text>
          </div>
          {isAuthenticated && (
            <Button component={Link} to={paths.teamsNew()} leftSection={<IconPlus size={20} />}>
              {t('teams.create.title')}
            </Button>
          )}
        </Group>

        <Group align="flex-end" wrap="wrap">
          <SearchInput
            id="team-search"
            value={search}
            onChange={(value) => {
              setSearch(value)
              resetPage()
            }}
            placeholder={t('teams.list.search.placeholder')}
            label={t('teams.list.search.label')}
            style={{ flex: 1, minWidth: 200 }}
          />
          {isAuthenticated && (
            <Select
              value={filter}
              onChange={(value) => {
                setFilter(value as FilterValue)
                resetPage()
              }}
              data={[
                { value: 'all', label: t('teams.list.filter.all') },
                { value: 'member', label: t('roles.MEMBER') },
                { value: 'organizer', label: t('roles.ORGANIZER') },
                { value: 'admin', label: t('roles.ADMIN') },
              ]}
              aria-label={t('teams.list.filter.label')}
              w={{ base: '100%', sm: 160 }}
            />
          )}
        </Group>

        {error && (
          <Alert color="red">
            {error instanceof Error ? error.message : t('errors.api.failedToLoad')}
          </Alert>
        )}

        {isLoading ? (
          <SimpleGrid cols={{ base: 1, sm: 2, lg: 3 }} spacing="lg">
            <TeamCardSkeleton count={6} />
          </SimpleGrid>
        ) : teams && teams.length > 0 ? (
          <Stack>
            <SimpleGrid cols={{ base: 1, sm: 2, lg: 3 }} spacing="lg">
              {teams.map((team) => (
                <TeamCard key={team.id} team={team} showRole={true} />
              ))}
            </SimpleGrid>

            <Box mt="lg">
              <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />
            </Box>
          </Stack>
        ) : (
          <Paper withBorder p="xl" radius="md">
            <Center>
              <Stack align="center" gap="sm">
                <IconUsersGroup size={48} color="var(--mantine-color-dimmed)" />
                <Text fw={500}>{t('teams.list.empty.title')}</Text>
                <Text c="dimmed">{t('teams.list.empty.publicTeams')}</Text>
                {isAuthenticated && (
                  <Button
                    component={Link}
                    to={paths.teamsNew()}
                    leftSection={<IconPlus size={20} />}
                    mt="md"
                  >
                    {t('teams.create.title')}
                  </Button>
                )}
              </Stack>
            </Center>
          </Paper>
        )}
      </Stack>
    </HomeLayout>
  )
}
