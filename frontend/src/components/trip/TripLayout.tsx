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
 * Mobile-first: tabs on top for mobile, sidebar for desktop.
 */
export function TripLayout({ trip, teamSlug, currentTab, children }: TripLayoutProps) {
  return (
    <SimpleGrid cols={{ base: 1, md: 4 }} spacing={{ base: 'md', sm: 'lg' }}>
      {/* Stage tabs - sidebar on desktop, on top for mobile */}
      <Box>
        <StageTabs trip={trip} teamSlug={teamSlug} currentTab={currentTab} />
      </Box>

      {/* Content - spans 3 columns on md+ via CSS class */}
      <Box className="trip-layout-content">{children}</Box>
    </SimpleGrid>
  )
}
