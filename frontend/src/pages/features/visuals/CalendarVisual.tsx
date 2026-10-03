import { useTranslation } from 'react-i18next'
import { Box, Group, Paper, SimpleGrid, Stack, Text, ThemeIcon } from '@mantine/core'
import { IconBell, IconCalendarShare } from '@tabler/icons-react'
import { VisualFrame } from './VisualFrame'

type DemoEvent = { label: string; kind: 'ride' | 'stage' }

/**
 * Three weeks of a team calendar (the 1st is a Monday), then a notification. Event colours are the calendar's own
 * (BRANDING §3.7): a ride is blue, a stage green, both in the `light` variant.
 */
export function CalendarVisual() {
  const { t } = useTranslation()
  const weekdays = t('features.demo.calendar.weekdays').split(',')
  const events: Record<number, DemoEvent> = {
    2: { label: t('features.demo.calendar.tuesday'), kind: 'ride' },
    7: { label: t('publicationType.ride'), kind: 'ride' },
    9: { label: t('features.demo.calendar.tuesday'), kind: 'ride' },
    13: { label: t('features.demo.calendar.stage1'), kind: 'stage' },
    14: { label: t('features.demo.calendar.stage2'), kind: 'stage' },
    16: { label: t('features.demo.calendar.tuesday'), kind: 'ride' },
    20: { label: t('features.demo.calendar.gravel'), kind: 'ride' },
  }
  const days = Array.from({ length: 21 }, (_, i) => i + 1)

  return (
    <VisualFrame color="primary">
      <Stack gap="md">
        <SimpleGrid cols={7} spacing={4} verticalSpacing={4}>
          {weekdays.map((day, i) => (
            <Text key={i} size="xs" fw={600} c="dimmed" ta="center">
              {day}
            </Text>
          ))}
          {days.map((day) => {
            const event = events[day]
            const color = event?.kind === 'stage' ? 'green' : 'blue'
            return (
              <Box
                key={day}
                mih={46}
                p={4}
                miw={0}
                style={{
                  borderRadius: 'var(--mantine-radius-sm)',
                  backgroundColor: 'var(--mantine-color-body)',
                }}
              >
                <Text size="xs">{day}</Text>
                {event && (
                  <Text
                    size="10px"
                    fw={600}
                    mt={2}
                    px={4}
                    truncate
                    style={{
                      borderRadius: 'var(--mantine-radius-xs)',
                      backgroundColor: `var(--mantine-color-${color}-light)`,
                      color: `var(--mantine-color-${color}-light-color)`,
                    }}
                  >
                    {event.label}
                  </Text>
                )}
              </Box>
            )
          })}
        </SimpleGrid>
        <Paper withBorder radius="lg" p="sm" shadow="md">
          <Group gap="sm" wrap="nowrap" align="flex-start">
            <ThemeIcon variant="light" size={36} radius="md">
              <IconBell size={20} />
            </ThemeIcon>
            <Stack gap={0} style={{ minWidth: 0 }}>
              <Text size="sm" fw={600}>
                {t('features.demo.calendar.notificationTitle')}
              </Text>
              <Text size="sm" c="dimmed">
                {t('features.demo.calendar.notificationBody')}
              </Text>
            </Stack>
          </Group>
        </Paper>
        <Group gap="xs" wrap="nowrap" align="flex-start">
          <IconCalendarShare size={18} style={{ flex: 'none' }} />
          <Text size="sm">{t('features.demo.calendar.ics')}</Text>
        </Group>
      </Stack>
    </VisualFrame>
  )
}
