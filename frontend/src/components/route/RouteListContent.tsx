import { ReactNode } from 'react'
import { useTranslation } from 'react-i18next'
import { IconMap, IconSearchOff } from '@tabler/icons-react'
import { Box, Button, Center, Paper, SimpleGrid, Stack, Title } from '@mantine/core'
import type { RouteDto } from '@/api/dto'
import { RouteCard, RouteCardSkeleton } from '../card'
import { RouteRow } from './RouteRow'
import type { RouteDensity } from '@/hooks/filters/routeFilters'
import { EmptyState } from '../common/EmptyState'
import { Pagination } from '../common/Pagination'
import { useScrollToListTop } from '@/hooks/useScrollToListTop'

interface RouteListContentProps {
  routes: RouteDto[] | undefined
  isLoading: boolean
  isError?: boolean
  showTeam: boolean
  hasFiltersOrSearch: boolean
  currentPage: number
  totalPages: number
  onPageChange: (page: number) => void
  /** Lets the user out of a filtered empty state — mandatory wherever filters can be active. */
  onClearFilters?: () => void
  /** When set, a filtered empty state renders the richer dead-end instead. */
  deadEnd?: ReactNode
  emptyAction?: ReactNode
  density?: RouteDensity
}

export function RouteListContent({
  routes,
  isLoading,
  isError,
  showTeam,
  hasFiltersOrSearch,
  currentPage,
  totalPages,
  onPageChange,
  onClearFilters,
  deadEnd,
  emptyAction,
  density = 'card',
}: RouteListContentProps) {
  const { t } = useTranslation()
  const { listTopRef, scrollToListTop } = useScrollToListTop()

  const handlePageChange = (page: number) => {
    onPageChange(page)
    scrollToListTop()
  }

  if (isLoading) {
    return (
      <SimpleGrid cols={{ base: 1, sm: 2, lg: 3 }}>
        <RouteCardSkeleton count={6} />
      </SimpleGrid>
    )
  }

  if (isError) {
    return (
      <Paper shadow="xs" withBorder>
        <Center>
          <Stack align="center">
            <IconMap size={48} color="var(--mantine-color-red-filled)" />
            <Title order={3}>{t('error.loading')}</Title>
          </Stack>
        </Center>
      </Paper>
    )
  }

  if (routes && routes.length > 0) {
    return (
      <>
        {density === 'row' ? (
          <Stack ref={listTopRef} gap="xs">
            {routes.map((route) => (
              <RouteRow key={route.id} route={route} />
            ))}
          </Stack>
        ) : (
          <SimpleGrid ref={listTopRef} cols={{ base: 1, sm: 2, lg: 3 }} spacing="lg">
            {routes.map((route) => (
              <RouteCard key={route.id} route={route} showTeam={showTeam} />
            ))}
          </SimpleGrid>
        )}

        <Box mt="xl">
          <Pagination
            currentPage={currentPage}
            totalPages={totalPages}
            onPageChange={handlePageChange}
          />
        </Box>
      </>
    )
  }

  // The filtered case is precisely the one needing the richest copy: it always carries a
  // description and a way out, never a bare title.
  if (hasFiltersOrSearch && deadEnd) {
    return <>{deadEnd}</>
  }

  return (
    <EmptyState
      variant={hasFiltersOrSearch ? 'filtered' : 'absolute'}
      icon={hasFiltersOrSearch ? <IconSearchOff size={48} /> : <IconMap size={48} />}
      title={hasFiltersOrSearch ? t('routes.list.noResultsTitle') : t('routes.list.empty.title')}
      description={
        hasFiltersOrSearch ? t('routes.list.noResults') : t('routes.list.empty.description')
      }
      actions={
        <>
          {hasFiltersOrSearch && onClearFilters && (
            <Button variant="light" onClick={onClearFilters}>
              {t('common.clearFilters')}
            </Button>
          )}
          {emptyAction}
        </>
      }
    />
  )
}
