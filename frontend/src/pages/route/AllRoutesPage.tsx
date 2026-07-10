import { useCallback, useMemo } from 'react'
import { useTranslation } from 'react-i18next'
import { keepPreviousData } from '@tanstack/react-query'
import { Group, Select, Stack } from '@mantine/core'
import {
  useListAllRoutes,
  listAllRoutes,
  getListAllRoutesQueryKey,
} from '@/api/endpoints/routes/routes'
import { usePaginatedQuery } from '../../hooks/usePaginatedQuery'
import { useRouteFilters } from '../../hooks/useRouteFilters'
import { useMembershipDefault } from '../../hooks/useMembershipDefault'
import { useAuth } from '../../hooks/useAuth'
import {
  makeAllRouteFiltersSchema,
  allRouteFiltersAlias,
  allRouteFiltersAlwaysSerialize,
} from '../../hooks/filters/routeFilters'
import { membershipToMinRole, type MembershipFilterValue } from '../../hooks/filters/membership'
import { HomeLayout } from '../../components/home/HomeLayout'
import { RouteFilterPanel } from '../../components/route/RouteFilterPanel'
import { RouteListContent } from '../../components/route/RouteListContent'
import { RouteViewToggle } from '../../components/route/RouteViewToggle'

export function AllRoutesPage() {
  const { t } = useTranslation()
  const { isAuthenticated } = useAuth()

  const membershipDefault = useMembershipDefault()
  const schema = useMemo(() => makeAllRouteFiltersSchema(membershipDefault), [membershipDefault])

  const {
    filters,
    setFilters,
    setFiltersOpen,
    filtersOpen,
    handleFiltersChange,
    handlePageChange,
    hasFiltersOrSearch,
    pageSize,
  } = useRouteFilters({
    schema,
    alias: allRouteFiltersAlias,
    alwaysSerialize: allRouteFiltersAlwaysSerialize,
  })

  // `membership` is the page's own value; the API wants a MinRole.
  const apiParams = useMemo(() => {
    const { membership, ...rest } = filters
    return { ...rest, minRole: membershipToMinRole[membership] }
  }, [filters])

  const {
    data: routesData,
    isLoading,
    isError,
  } = useListAllRoutes(apiParams, {
    query: { placeholderData: keepPreviousData },
  })

  const prefetchPage = useCallback(
    (prefetchPageNum: number) => ({
      queryKey: getListAllRoutesQueryKey({ ...apiParams, page: prefetchPageNum }),
      queryFn: () => listAllRoutes({ ...apiParams, page: prefetchPageNum }),
    }),
    [apiParams]
  )

  const { totalPages } = usePaginatedQuery({
    page: filters.page,
    pageSize,
    totalItems: routesData?.total ?? 0,
    prefetchPage,
  })

  return (
    <HomeLayout currentTab="routes">
      <Stack my="lg">
        <Group justify="space-between" wrap="wrap">
          {isAuthenticated ? (
            <Select
              value={filters.membership}
              onChange={(value) => setFilters({ membership: value as MembershipFilterValue })}
              data={[
                { value: 'all', label: t('filters.membership.all') },
                { value: 'member', label: t('roles.MEMBER') },
                { value: 'organizer', label: t('roles.ORGANIZER') },
                { value: 'admin', label: t('roles.ADMIN') },
              ]}
              aria-label={t('filters.membership.label')}
              allowDeselect={false}
              w={{ base: '100%', sm: 200 }}
            />
          ) : (
            <div />
          )}
          <RouteViewToggle current="list" />
        </Group>

        <RouteFilterPanel
          filters={filters}
          onFiltersChange={handleFiltersChange}
          isOpen={filtersOpen}
          onOpenChange={setFiltersOpen}
        />

        <RouteListContent
          routes={routesData?.routes}
          isLoading={isLoading}
          isError={isError}
          showTeam={true}
          hasFiltersOrSearch={hasFiltersOrSearch}
          currentPage={filters.page}
          totalPages={totalPages}
          onPageChange={handlePageChange}
        />
      </Stack>
    </HomeLayout>
  )
}
