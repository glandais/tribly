import { useCallback, useMemo } from 'react'
import { Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { paths } from '../../config/paths'
import { IconPlus, IconUsersGroup } from '@tabler/icons-react'
import { useListTeams, listTeams, getListTeamsQueryKey } from '@/api/endpoints/teams/teams'
import { useAuth } from '../../hooks/useAuth'
import { getAppConfig } from '../../config/appConfig'
import { usePaginatedQuery } from '../../hooks/usePaginatedQuery'
import { useUrlFilters } from '../../hooks/useUrlFilters'
import { useDebouncedSearch } from '../../hooks/useDebouncedSearch'
import { useMembershipDefault } from '../../hooks/useMembershipDefault'
import { useScrollToListTop } from '../../hooks/useScrollToListTop'
import {
  makeTeamFiltersSchema,
  teamFiltersAlias,
  teamFiltersAlwaysSerialize,
} from '../../hooks/filters/teamFilters'
import { membershipToMinRole, type MembershipFilterValue } from '../../hooks/filters/membership'
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

export function TeamListPage() {
  const { t } = useTranslation()
  const { isAuthenticated } = useAuth()
  const config = getAppConfig()

  const membershipDefault = useMembershipDefault()
  const schema = useMemo(() => makeTeamFiltersSchema(membershipDefault), [membershipDefault])
  const { filters, setFilters } = useUrlFilters({
    schema,
    alias: teamFiltersAlias,
    alwaysSerialize: teamFiltersAlwaysSerialize,
  })
  const commitSearch = useCallback(
    (value: string) => setFilters({ search: value || undefined }),
    [setFilters]
  )
  const [search, setSearch] = useDebouncedSearch(filters.search ?? '', commitSearch)
  const { listTopRef, scrollToListTop } = useScrollToListTop()

  const apiParams = useMemo(
    () => ({
      search: filters.search,
      page: filters.page,
      size: filters.size,
      minRole: membershipToMinRole[filters.membership],
    }),
    [filters]
  )

  const { data: teamsData, isLoading, error } = useListTeams(apiParams)

  const prefetchPage = useCallback(
    (prefetchPageNum: number) => ({
      queryKey: getListTeamsQueryKey({ ...apiParams, page: prefetchPageNum }),
      queryFn: () => listTeams({ ...apiParams, page: prefetchPageNum }),
    }),
    [apiParams]
  )

  const { totalPages } = usePaginatedQuery({
    page: filters.page,
    pageSize: filters.size,
    totalItems: teamsData?.total ?? 0,
    prefetchPage,
  })

  const teams = teamsData?.teams
  const hasTeams = (teamsData?.total ?? 0) > 0
  const canCreateTeam = isAuthenticated && (!config?.singleTeam || !hasTeams)

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
          {canCreateTeam && (
            <Button component={Link} to={paths.teamsNew()} leftSection={<IconPlus size={20} />}>
              {t('teams.create.title')}
            </Button>
          )}
        </Group>

        <Group align="flex-end" wrap="wrap">
          <SearchInput
            id="team-search"
            value={search}
            onChange={setSearch}
            placeholder={t('teams.list.search.placeholder')}
            label={t('teams.list.search.label')}
            style={{ flex: 1, minWidth: 200 }}
          />
          {isAuthenticated && (
            <Select
              value={filters.membership}
              onChange={(value) => setFilters({ membership: value as MembershipFilterValue })}
              data={[
                { value: 'all', label: t('teams.list.filter.all') },
                { value: 'member', label: t('roles.MEMBER') },
                { value: 'organizer', label: t('roles.ORGANIZER') },
                { value: 'admin', label: t('roles.ADMIN') },
              ]}
              aria-label={t('teams.list.filter.label')}
              allowDeselect={false}
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
            <SimpleGrid ref={listTopRef} cols={{ base: 1, sm: 2, lg: 3 }} spacing="lg">
              {teams.map((team) => (
                <TeamCard key={team.id} team={team} showRole={true} />
              ))}
            </SimpleGrid>

            <Box mt="lg">
              <Pagination
                currentPage={filters.page}
                totalPages={totalPages}
                onPageChange={(page) => {
                  setFilters({ page })
                  scrollToListTop()
                }}
              />
            </Box>
          </Stack>
        ) : (
          <Paper withBorder p="xl" radius="md">
            <Center>
              <Stack align="center" gap="sm">
                <IconUsersGroup size={48} color="var(--mantine-color-dimmed)" />
                <Text fw={500}>{t('teams.list.empty.title')}</Text>
                <Text c="dimmed">{t('teams.list.empty.publicTeams')}</Text>
                {canCreateTeam && (
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
