import { Paper, Skeleton, Group, Stack, Box } from '@mantine/core'

interface CardSkeletonProps {
  count?: number
  hasImage?: boolean
  imageHeight?: string
  statCount?: number
  badgeCount?: number
  hasLogo?: boolean
  hasAction?: boolean
}

export function CardSkeleton({
  count = 1,
  hasImage = false,
  imageHeight = '128px',
  statCount = 2,
  badgeCount = 3,
  hasLogo = false,
  hasAction = false,
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
              {/* Title row with optional logo */}
              {hasLogo ? (
                <Group gap="sm" wrap="nowrap">
                  <Skeleton h={32} w={32} radius="md" />
                  <Skeleton h={20} w="60%" radius="sm" />
                </Group>
              ) : (
                <Skeleton h={20} w="75%" radius="sm" />
              )}
              <Skeleton h={16} w="100%" radius="sm" />
              <Skeleton h={16} w="66%" radius="sm" />
            </Stack>

            {statCount > 0 && (
              <Group mt="md">
                {Array.from({ length: statCount }).map((_, j) => (
                  <Skeleton key={j} h={16} w={64} radius="sm" />
                ))}
              </Group>
            )}

            <Group gap="xs" mt="sm" justify={hasAction ? 'space-between' : 'flex-start'}>
              {badgeCount > 0 && (
                <Group gap="xs">
                  {Array.from({ length: badgeCount }).map((_, j) => (
                    <Skeleton key={j} h={20} w={64} radius="xl" />
                  ))}
                </Group>
              )}
              {hasAction && <Skeleton h={30} w={80} radius="sm" />}
            </Group>
          </Box>
        </Paper>
      ))}
    </>
  )
}
