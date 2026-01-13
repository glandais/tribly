import { ReactNode } from 'react'
import { useTranslation } from 'react-i18next'
import { IconMap } from '@tabler/icons-react'
import { Box, Center, Paper, SimpleGrid, Stack, Text, Title } from '@mantine/core'
import type { RouteDto } from '@/api/dto'
import { RouteCard, RouteCardSkeleton } from '../card'
import { Pagination } from '../common/Pagination'

interface RouteListContentProps {
  routes: RouteDto[] | undefined
  isLoading: boolean
  isError?: boolean
  showTeam: boolean
  hasFiltersOrSearch: boolean
  currentPage: number
  totalPages: number
  onPageChange: (page: number) => void
  emptyAction?: ReactNode
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
  emptyAction,
}: RouteListContentProps) {
  const { t } = useTranslation()

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
        <SimpleGrid cols={{ base: 1, sm: 2, lg: 3 }} spacing="lg">
          {routes.map((route) => (
            <RouteCard key={route.id} route={route} showTeam={showTeam} />
          ))}
        </SimpleGrid>

        <Box mt="xl">
          <Pagination
            currentPage={currentPage}
            totalPages={totalPages}
            onPageChange={onPageChange}
          />
        </Box>
      </>
    )
  }

  return (
    <Paper shadow="xs" withBorder py="xl">
      <Center>
        <Stack align="center">
          <IconMap size={48} color="var(--mantine-color-dimmed)" />
          <Title order={3}>
            {hasFiltersOrSearch ? t('routes.list.noResults') : t('routes.list.empty.title')}
          </Title>
          {!hasFiltersOrSearch && <Text c="dimmed">{t('routes.list.empty.description')}</Text>}
          {emptyAction}
        </Stack>
      </Center>
    </Paper>
  )
}
