import { Paper, Skeleton, Group, Stack, Box } from '@mantine/core'

interface CardSkeletonProps {
  count?: number
  hasImage?: boolean
  imageHeight?: string
  statCount?: number
  badgeCount?: number
}

export function CardSkeleton({
  count = 1,
  hasImage = false,
  imageHeight = '128px',
  statCount = 2,
  badgeCount = 3,
}: CardSkeletonProps) {
  return (
    <>
      {Array.from({ length: count }).map((_, i) => (
        <Paper key={i} withBorder radius="md">
          {hasImage && (
            <Skeleton
              h={imageHeight}
              radius={0}
              style={{
                borderTopLeftRadius: 'var(--mantine-radius-md)',
                borderTopRightRadius: 'var(--mantine-radius-md)',
              }}
            />
          )}
          <Box p="md">
            <Stack gap="xs">
              <Skeleton h={20} w="75%" radius="sm" />
              <Skeleton h={16} w="100%" radius="sm" />
              <Skeleton h={16} w="66%" radius="sm" />
            </Stack>

            {statCount > 0 && (
              <Group gap="md" mt="md">
                {Array.from({ length: statCount }).map((_, j) => (
                  <Skeleton key={j} h={16} w={64} radius="sm" />
                ))}
              </Group>
            )}

            {badgeCount > 0 && (
              <Group gap="xs" mt="sm">
                {Array.from({ length: badgeCount }).map((_, j) => (
                  <Skeleton key={j} h={20} w={64} radius="xl" />
                ))}
              </Group>
            )}
          </Box>
        </Paper>
      ))}
    </>
  )
}
