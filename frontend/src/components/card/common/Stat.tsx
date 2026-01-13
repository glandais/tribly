import { ReactNode } from 'react'
import { Group, Text, Box } from '@mantine/core'

interface StatProps {
  icon: ReactNode
  children: ReactNode
}

export function Stat({ icon, children }: StatProps) {
  return (
    <Group gap={4} wrap="nowrap">
      <Box
        w={16}
        h={16}
        style={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}
      >
        {icon}
      </Box>
      <Text size="sm" c="dimmed">
        {children}
      </Text>
    </Group>
  )
}

interface StatGroupProps {
  children: ReactNode
}

export function StatGroup({ children }: StatGroupProps) {
  return <Group my="xs">{children}</Group>
}
