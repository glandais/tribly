import { Paper, Skeleton, Stack } from '@mantine/core'
import { useMapHeight } from '@/hooks/useResponsive'

interface DetailPageSkeletonProps {
  /** Detail pages that carry no map (post, ad) skip the leading map block. */
  withMap?: boolean
  /** Number of content blocks below the map. */
  blocks?: number
}

/**
 * Structured placeholder for detail pages — a map block plus N content blocks —
 * instead of a bare full-page spinner, so the layout does not jump on arrival.
 */
export function DetailPageSkeleton({ withMap = true, blocks = 3 }: DetailPageSkeletonProps) {
  const mapHeight = useMapHeight('standard')

  return (
    <Stack gap="md">
      <Skeleton height={32} width="60%" radius="sm" />
      {withMap && <Skeleton height={mapHeight} radius="md" />}
      {Array.from({ length: blocks }, (_, i) => (
        <Paper key={i} withBorder p="md" radius="md">
          <Stack gap="sm">
            <Skeleton height={20} width="40%" radius="sm" />
            <Skeleton height={12} radius="sm" />
            <Skeleton height={12} width="80%" radius="sm" />
          </Stack>
        </Paper>
      ))}
    </Stack>
  )
}
