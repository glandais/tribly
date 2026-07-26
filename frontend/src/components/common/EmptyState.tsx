import { Box, Center, Group, Paper, Stack, Text, Title } from '@mantine/core'
import type { ReactNode } from 'react'

interface EmptyStateProps {
  /** Domain icon (absolute variant) or IconSearchOff (filtered variant), sized 48. */
  icon: ReactNode
  title: string
  description?: string
  /** Buttons letting the user out of the state — mandatory in practice for `filtered`. */
  actions?: ReactNode
  /**
   * `absolute`: nothing exists yet. `filtered`: things exist, the current
   * filters hide them. The two must never read the same.
   */
  variant?: 'absolute' | 'filtered'
}

/**
 * The single empty-state surface of the site. Uses only `dimmed` tokens so it
 * stays legible in both color schemes (no hard-coded `gray-N`).
 */
export function EmptyState({
  icon,
  title,
  description,
  actions,
  variant = 'absolute',
}: EmptyStateProps) {
  return (
    <Paper withBorder py="xl" px="md" radius="md" data-variant={variant}>
      <Center>
        <Stack align="center" gap="md">
          <Box
            p="lg"
            style={{
              backgroundColor: 'var(--mantine-color-default-hover)',
              borderRadius: '50%',
              color: 'var(--mantine-color-dimmed)',
              lineHeight: 0,
            }}
          >
            {icon}
          </Box>
          <Title order={3} ta="center">
            {title}
          </Title>
          {description && (
            <Text c="dimmed" ta="center" maw={400}>
              {description}
            </Text>
          )}
          {actions && (
            <Group justify="center" gap="sm">
              {actions}
            </Group>
          )}
        </Stack>
      </Center>
    </Paper>
  )
}
