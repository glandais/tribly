import { SimpleGrid, Box } from '@mantine/core'
import type { TripDto } from '@/api/dto'
import { StageTabs } from './StageTabs'

interface TripLayoutProps {
  trip: TripDto
  teamSlug: string
  currentTab: 'overview' | string // 'overview' or stageSlug
  children: React.ReactNode
}

/**
 * Layout component that adds stage tabs sidebar to trip pages.
 * Used by TripDetailPage and StageDetailPage.
 */
export function TripLayout({ trip, teamSlug, currentTab, children }: TripLayoutProps) {
  return (
    <SimpleGrid cols={{ base: 1, md: 4 }} spacing="lg">
      {/* Stage tabs on left (1 column) */}
      <Box>
        <StageTabs trip={trip} teamSlug={teamSlug} currentTab={currentTab} />
      </Box>

      {/* Content on right (3 columns on md+) */}
      <Box style={{ gridColumn: 'span 3' }} hiddenFrom="md">
        {children}
      </Box>
      <Box style={{ gridColumn: 'span 3' }} visibleFrom="md">
        {children}
      </Box>
    </SimpleGrid>
  )
}
