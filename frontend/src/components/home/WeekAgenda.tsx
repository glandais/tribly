import { useMemo } from 'react'
import { useTranslation } from 'react-i18next'
import { formatInTimeZone } from 'date-fns-tz'
import { fr } from 'date-fns/locale/fr'
import { enUS } from 'date-fns/locale/en-US'
import {
  Anchor,
  Badge,
  Box,
  Group,
  Paper,
  SimpleGrid,
  Skeleton,
  Stack,
  Text,
  Title,
  UnstyledButton,
} from '@mantine/core'
import { IconChevronRight } from '@tabler/icons-react'
import type { CalendarEventDto } from '@/api/dto'
import { PrefetchLink } from '@/components/common/PrefetchLink'
import { StatusBadge } from '@/components/card/common'
import { paths } from '@/config/paths'
import classes from './Home.module.css'
import { eventPath } from './memberHomeHelpers'
import { useEffectiveTimezone } from '@/utils/dateFormat'
import { PUBLICATION_TYPE_COLORS } from '@/lib/badgeColors.generated'

const DAY_MS = 24 * 60 * 60 * 1000
/** Rows listed under the strip; the calendar holds the rest. */
const MAX_ROWS = 5

interface WeekAgendaProps {
  /** The hour-aligned `now` the events were fetched from — the strip's first day. */
  nowIso: string
  events: CalendarEventDto[] | undefined
  isLoading: boolean
  isError?: boolean
  /**
   * The ride « Ma prochaine sortie » already shows beside the agenda: left out of the rows (as
   * the app does), still counted in the strip's dots.
   */
  nextRide?: { teamSlug: string; slug: string }
}

/**
 * « Cette semaine » on the member home: a seven-day strip from today, a dot on each day with an
 * event, then the next events of all the member's teams — from one `GET /api/calendar/events`
 * call. Days are read in the effective timezone (the member's preference, else the browser's).
 */
export function WeekAgenda({ nowIso, events, isLoading, isError, nextRide }: WeekAgendaProps) {
  const { t, i18n } = useTranslation()
  const { timezone } = useEffectiveTimezone()
  const locale = i18n.language === 'fr' ? fr : enUS
  const dayKey = (date: Date | string) => formatInTimeZone(date, timezone, 'yyyy-MM-dd')

  const days = useMemo(() => {
    const start = new Date(nowIso).getTime()
    return Array.from({ length: 7 }, (_, i) => new Date(start + i * DAY_MS))
  }, [nowIso])

  const dayKeys = new Set(days.map((d) => dayKey(d)))
  const inWeek = (events ?? [])
    .filter((e) => !e.finished && dayKeys.has(dayKey(e.start)))
    .sort((a, b) => a.start.localeCompare(b.start))
  const busyDays = new Set(inWeek.map((e) => dayKey(e.start)))
  const todayKey = dayKey(days[0])
  const rows = inWeek.filter(
    (e) =>
      !(
        nextRide &&
        e.type === 'RIDE' &&
        e.teamSlug === nextRide.teamSlug &&
        e.entitySlug === nextRide.slug
      )
  )

  return (
    <Paper withBorder radius="md" p="md" component="section" aria-labelledby="home-week-title">
      <Group justify="space-between" mb="sm">
        <Title id="home-week-title" order={2} size="h4">
          {t('home.week.title')}
        </Title>
        {/* Not « Calendrier »: the home tabs already hold a link of that name. */}
        <Anchor component={PrefetchLink} to={paths.calendar()} size="sm">
          {t('home.week.calendarLink')}
        </Anchor>
      </Group>

      <SimpleGrid cols={7} spacing={4} mb="sm" aria-hidden>
        {days.map((day) => {
          const key = dayKey(day)
          const isToday = key === todayKey
          return (
            <Stack
              key={key}
              gap={2}
              align="center"
              py={6}
              style={{
                borderRadius: 'var(--mantine-radius-md)',
                backgroundColor: isToday ? 'var(--mantine-primary-color-light)' : undefined,
              }}
            >
              <Text size="xs" c="dimmed" tt="uppercase">
                {formatInTimeZone(day, timezone, 'EEEEE', { locale })}
              </Text>
              <Text size="sm" fw={isToday ? 700 : 500} c={isToday ? 'primary' : undefined}>
                {formatInTimeZone(day, timezone, 'd', { locale })}
              </Text>
              <Box
                w={6}
                h={6}
                style={{
                  borderRadius: '50%',
                  backgroundColor: busyDays.has(key)
                    ? `var(--mantine-color-${PUBLICATION_TYPE_COLORS.RIDE}-filled)`
                    : 'transparent',
                }}
              />
            </Stack>
          )
        })}
      </SimpleGrid>

      {isLoading ? (
        <Stack gap="xs">
          {[0, 1, 2].map((i) => (
            <Skeleton key={i} h={48} radius="md" />
          ))}
        </Stack>
      ) : isError ? (
        <Text size="sm" c="dimmed">
          {t('error.loading')}
        </Text>
      ) : rows.length === 0 ? (
        <Text size="sm" c="dimmed">
          {inWeek.length > 0 ? t('home.week.nothingElse') : t('home.week.empty')}
        </Text>
      ) : (
        <Stack gap={4}>
          {rows.slice(0, MAX_ROWS).map((event) => (
            <UnstyledButton
              key={`${event.type}-${event.id}`}
              component={PrefetchLink}
              to={eventPath(event)}
              p={6}
              className={classes.row}
            >
              <Group gap="sm" wrap="nowrap">
                <Stack
                  gap={0}
                  align="center"
                  w={44}
                  py={4}
                  style={{
                    flexShrink: 0,
                    borderRadius: 'var(--mantine-radius-md)',
                    backgroundColor: 'var(--mantine-color-default-hover)',
                  }}
                >
                  <Text size="xs" c="dimmed">
                    {formatInTimeZone(event.start, timezone, 'EEE', { locale })}
                  </Text>
                  <Text fw={700} lh={1.1}>
                    {formatInTimeZone(event.start, timezone, 'd', { locale })}
                  </Text>
                </Stack>
                <Box style={{ flex: 1, minWidth: 0 }}>
                  <Group gap={6} wrap="nowrap">
                    <Text size="sm" fw={600} truncate>
                      {event.title}
                    </Text>
                    {event.status !== 'PUBLISHED' && (
                      <StatusBadge status={event.status}>{t(`status.${event.status}`)}</StatusBadge>
                    )}
                    {event.registered && (
                      <Badge size="xs" color="primary" variant="light" style={{ flexShrink: 0 }}>
                        {t('publications.registered')}
                      </Badge>
                    )}
                  </Group>
                  <Text size="xs" c="dimmed" truncate>
                    {[
                      event.allDay
                        ? null
                        : formatInTimeZone(event.start, timezone, 'p', { locale }),
                      event.teamName,
                      event.groupName,
                    ]
                      .filter(Boolean)
                      .join(' · ')}
                  </Text>
                </Box>
                <IconChevronRight size={16} color="var(--mantine-color-dimmed)" />
              </Group>
            </UnstyledButton>
          ))}
        </Stack>
      )}
    </Paper>
  )
}
