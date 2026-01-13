import { Link } from 'react-router-dom'
import { Group, UnstyledButton, Text, ScrollArea, Box } from '@mantine/core'
import type { TablerIcon } from '@tabler/icons-react'

export interface NavButtonItem {
  id: string
  path: string
  label: string
  icon: TablerIcon
}

interface NavButtonsProps {
  items: NavButtonItem[]
  currentId: string
}

export function NavButtons({ items, currentId }: NavButtonsProps) {
  return (
    <ScrollArea type="never" offsetScrollbars={false}>
      <Group gap="xs" wrap="nowrap" align="flex-start">
        {items.map((item) => {
          const isActive = item.id === currentId
          const Icon = item.icon

          return (
            <UnstyledButton
              key={item.id}
              component={Link}
              to={item.path}
              style={{
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                gap: 4,
                padding: 'var(--mantine-spacing-xs)',
                borderRadius: 'var(--mantine-radius-md)',
                backgroundColor: isActive ? 'var(--mantine-color-default-hover)' : undefined,
                minWidth: 64,
                maxWidth: 80,
              }}
            >
              <Box
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  width: 48,
                  height: 48,
                  borderRadius: 'var(--mantine-radius-md)',
                  backgroundColor: isActive
                    ? 'var(--mantine-primary-color-filled)'
                    : 'var(--mantine-color-default-hover)',
                }}
              >
                <Icon size={24} color={isActive ? 'white' : 'var(--mantine-color-text)'} />
              </Box>
              <Text
                size="xs"
                fw={isActive ? 600 : 400}
                c={isActive ? undefined : 'dimmed'}
                ta="center"
                lineClamp={2}
                style={{ lineHeight: 1.2 }}
              >
                {item.label}
              </Text>
            </UnstyledButton>
          )
        })}
      </Group>
    </ScrollArea>
  )
}
