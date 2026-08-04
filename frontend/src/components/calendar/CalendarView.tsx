import { useCallback, useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { Schedule } from '@mantine/schedule'
import type {
  RenderEventBody,
  ScheduleEventData,
  ScheduleLabelsOverride,
  ScheduleViewLevel,
} from '@mantine/schedule'
import { Box, Image, LoadingOverlay, Stack, Text, Tooltip } from '@mantine/core'
import dayjs from 'dayjs'
import utc from 'dayjs/plugin/utc'
import timezone from 'dayjs/plugin/timezone'
import { paths } from '@/config/paths'
import { useUnits } from '@/hooks/useUnits'
import { useEffectiveTimezone } from '@/utils/dateFormat'
import type { CalendarEventDto, CalendarEventType } from '@/api/dto'

dayjs.extend(utc)
dayjs.extend(timezone)

interface CalendarViewProps {
  events: CalendarEventDto[]
  isLoading: boolean
  onDateRangeChange: (start: Date, end: Date) => void
}

/**
 * `@mantine/schedule` types `renderEvent` internally but does not re-export the type from its
 * entry point (only `RenderEventBody` is public), so we restate the same signature here.
 */
type RenderEvent = (
  event: ScheduleEventData,
  props: React.ComponentPropsWithoutRef<'button'> & { children: React.ReactNode }
) => React.ReactElement

interface CalendarEventPayload {
  dto: CalendarEventDto
  [key: PropertyKey]: unknown
}

/**
 * Mantine palette *names*, not hex values — same hues as before (`blue`/`green` are the shades the
 * literals `#228be6`/`#40c057` came from), but they survive dark mode.
 *
 * `@mantine/schedule` runs this through `theme.variantColorResolver` with the `light` variant. Given
 * a name it resolves to `--mantine-color-blue-light` / `-light-color`, which are defined per colour
 * scheme. Given a raw hex it can only blend `rgba(hex, 0.1)` for the background and keep the hex as
 * the text colour — a pair computed for a white surface, which in dark mode painted mid-blue text on
 * a near-transparent tint and made every event unreadable.
 */
const EVENT_COLORS: Record<CalendarEventType, string> = {
  RIDE: 'blue',
  TRIP_STAGE: 'green',
}

const SEPARATOR = ' · '

function getPayloadDto(event: ScheduleEventData): CalendarEventDto | undefined {
  return (event.payload as CalendarEventPayload | undefined)?.dto
}

function getVisibleRange(date: string, view: ScheduleViewLevel): { start: Date; end: Date } {
  const d = dayjs(date)
  if (view === 'year') {
    return { start: d.startOf('year').toDate(), end: d.endOf('year').toDate() }
  }
  if (view === 'month') {
    const firstOfMonth = d.startOf('month')
    // firstDayOfWeek=1 (Monday): (day()+6)%7 gives Mon=0..Sun=6
    const daysFromMonday = (firstOfMonth.day() + 6) % 7
    const startOfGrid = firstOfMonth.subtract(daysFromMonday, 'day')
    // Schedule always renders 6 weeks (42 days)
    const endOfGrid = startOfGrid.add(42, 'day').subtract(1, 'millisecond')
    return { start: startOfGrid.toDate(), end: endOfGrid.toDate() }
  }
  return { start: d.startOf('month').toDate(), end: d.endOf('month').toDate() }
}

export function CalendarView({
  events,
  isLoading,
  onDateRangeChange,
}: CalendarViewProps): React.ReactElement {
  const navigate = useNavigate()
  const { t, i18n } = useTranslation()
  const { distance: formatDistance, elevation: formatElevation } = useUnits()
  const { timezone: tz } = useEffectiveTimezone()

  const labels = useMemo<ScheduleLabelsOverride>(
    () => ({
      today: t('calendar.schedule.today'),
      next: t('calendar.schedule.next'),
      previous: t('calendar.schedule.previous'),
      day: t('calendar.schedule.day'),
      week: t('calendar.schedule.week'),
      month: t('calendar.schedule.month'),
      year: t('calendar.schedule.year'),
      allDay: t('calendar.schedule.allDay'),
      weekday: t('calendar.schedule.weekday'),
      timeSlot: t('calendar.schedule.timeSlot'),
      selectMonth: t('calendar.schedule.selectMonth'),
      selectYear: t('calendar.schedule.selectYear'),
      switchToDayView: t('calendar.schedule.switchToDayView'),
      switchToWeekView: t('calendar.schedule.switchToWeekView'),
      switchToMonthView: t('calendar.schedule.switchToMonthView'),
      switchToYearView: t('calendar.schedule.switchToYearView'),
      viewSelectLabel: t('calendar.schedule.viewSelectLabel'),
      noEvents: t('calendar.schedule.noEvents'),
      moreLabel: (count) => t('calendar.schedule.moreLabel', { count }),
    }),
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [t, i18n.language]
  )

  const [view, setView] = useState<ScheduleViewLevel>('month')
  const [date, setDate] = useState(dayjs().format('YYYY-MM-DD'))

  useEffect(() => {
    const { start, end } = getVisibleRange(date, view)
    onDateRangeChange(start, end)
  }, [date, view]) // eslint-disable-line react-hooks/exhaustive-deps

  const scheduleEvents = useMemo<ScheduleEventData[]>(
    () =>
      events.map((event) => ({
        id: event.id,
        title: event.title,
        start: dayjs(event.start).tz(tz).format('YYYY-MM-DD HH:mm:ss'),
        end: dayjs(event.end ?? event.start)
          .tz(tz)
          .format('YYYY-MM-DD HH:mm:ss'),
        color: EVENT_COLORS[event.type],
        payload: { dto: event } satisfies CalendarEventPayload,
      })),
    [events, tz]
  )

  const eventMap = useMemo(() => new Map(events.map((e) => [String(e.id), e])), [events])

  /** Metric line ("42 km · 850 m"), empty when the entity carries no route. */
  const buildMetrics = useCallback(
    (dto: CalendarEventDto): string =>
      [
        dto.distance != null ? formatDistance(dto.distance) : null,
        dto.elevationGain != null ? formatElevation(dto.elevationGain) : null,
      ]
        .filter(Boolean)
        .join(SEPARATOR),
    [formatDistance, formatElevation]
  )

  /** "team · time · start place" — the summary line required on every event. */
  const buildSummary = useCallback(
    (dto: CalendarEventDto): string =>
      [
        dto.teamName,
        dto.allDay ? t('calendar.schedule.allDay') : dayjs(dto.start).tz(tz).format('HH:mm'),
        dto.startPlaceName ?? null,
      ]
        .filter(Boolean)
        .join(SEPARATOR),
    [t, tz]
  )

  /** "Inscrit" / "Inscrit · Groupe A", empty when the user is not registered. */
  const buildRegistration = useCallback(
    (dto: CalendarEventDto): string => {
      if (!dto.registered) {
        return ''
      }
      return dto.groupName
        ? t('calendar.event.registeredInGroup', { group: dto.groupName })
        : t('calendar.event.registered')
    },
    [t]
  )

  /**
   * Month-view event rows have a fixed height (one text line), so the body stays on a single
   * truncated line and the tooltip carries the structured detail.
   */
  const renderEventBody = useCallback<RenderEventBody>(
    (event) => {
      const dto = getPayloadDto(event)
      if (!dto) {
        return event.title
      }
      const details = [buildSummary(dto), buildMetrics(dto), buildRegistration(dto)]
        .filter(Boolean)
        .join(SEPARATOR)
      return (
        <Text size="xs" lh={1.3} truncate>
          <Text span inherit fw={600}>
            {dto.title}
          </Text>
          <Text span inherit c="dimmed">
            {details ? SEPARATOR + details : ''}
          </Text>
        </Text>
      )
    },
    [buildMetrics, buildRegistration, buildSummary]
  )

  const renderEvent = useCallback<RenderEvent>(
    (event, props) => {
      const dto = getPayloadDto(event)
      const isPast = dayjs(event.end ?? event.start).isBefore(dayjs())
      const thumbnail = dto?.thumbnailUrl?.replace('{size}', '128')
      const metrics = dto ? buildMetrics(dto) : ''
      const registration = dto ? buildRegistration(dto) : ''

      const button = (
        <button
          {...props}
          style={{
            ...props.style,
            opacity: isPast ? 0.55 : undefined,
            borderInlineStart: dto?.registered
              ? '3px solid var(--mantine-primary-color-filled)'
              : undefined,
          }}
        />
      )

      if (!dto) {
        return button
      }

      // `c="inherit"` is load-bearing: Mantine's Tooltip inverts its own colours per scheme
      // (dark surface + white text in light mode, light surface + black text in dark mode),
      // while `Text` defaults to `--mantine-color-text` — the opposite value in both. Without
      // it the label renders dark-on-dark in light mode and light-on-light in dark mode.
      const tooltip = (
        <Stack gap={4}>
          {thumbnail ? (
            <Image src={thumbnail} alt={dto.title} w={200} h={80} fit="cover" radius="sm" />
          ) : null}
          <Text size="sm" fw={600} c="inherit">
            {dto.title}
          </Text>
          <Text size="xs" c="inherit">
            {buildSummary(dto)}
          </Text>
          {metrics ? (
            <Text size="xs" c="inherit">
              {metrics}
            </Text>
          ) : null}
          {registration ? (
            <Text size="xs" fw={500} c="inherit">
              {registration}
            </Text>
          ) : null}
          {dto.status !== 'PUBLISHED' ? (
            <Text size="xs" c="inherit">
              {t(`status.${dto.status satisfies 'DRAFT' | 'PUBLISHED' | 'CANCELLED'}`)}
            </Text>
          ) : null}
        </Stack>
      )

      return (
        <Tooltip label={tooltip} withArrow multiline w={220} openDelay={250} position="top">
          {button}
        </Tooltip>
      )
    },
    [buildMetrics, buildRegistration, buildSummary, t]
  )

  const handleEventClick = useCallback(
    (event: ScheduleEventData) => {
      const original = eventMap.get(String(event.id))
      if (!original) return
      switch (original.type) {
        case 'RIDE':
          navigate(paths.ride(original.teamSlug, original.entitySlug))
          break
        case 'TRIP_STAGE':
          if (original.tripSlug) {
            navigate(paths.stage(original.teamSlug, original.tripSlug, original.entitySlug))
          }
          break
      }
    },
    [eventMap, navigate]
  )

  return (
    <Box pos="relative">
      <LoadingOverlay visible={isLoading} />
      <Schedule
        events={scheduleEvents}
        view={view}
        onViewChange={setView}
        date={date}
        onDateChange={setDate}
        onEventClick={handleEventClick}
        locale={i18n.language}
        labels={labels}
        renderEventBody={renderEventBody}
        monthViewProps={{ firstDayOfWeek: 1, renderEvent }}
        weekViewProps={{ renderEvent }}
        dayViewProps={{ renderEvent }}
        mobileMonthViewProps={{ renderEvent }}
      />
    </Box>
  )
}
