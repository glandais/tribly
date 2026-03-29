import { useCallback, useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { Schedule } from '@mantine/schedule'
import type { ScheduleEventData, ScheduleLabelsOverride, ScheduleViewLevel } from '@mantine/schedule'
import { LoadingOverlay, Box } from '@mantine/core'
import dayjs from 'dayjs'
import { paths } from '@/config/paths'
import type { CalendarEventDto, CalendarEventType } from '@/api/dto'

interface CalendarViewProps {
  events: CalendarEventDto[]
  isLoading: boolean
  onDateRangeChange: (start: Date, end: Date) => void
}

const EVENT_COLORS: Record<CalendarEventType, string> = {
  RIDE: '#228be6',
  TRIP_STAGE: '#40c057',
}

function getVisibleRange(date: string, view: ScheduleViewLevel): { start: Date; end: Date } {
  const d = dayjs(date)
  if (view === 'year') {
    return { start: d.startOf('year').toDate(), end: d.endOf('year').toDate() }
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
        start: dayjs(event.start).format('YYYY-MM-DD HH:mm:ss'),
        end: dayjs(event.end ?? event.start).format('YYYY-MM-DD HH:mm:ss'),
        color: EVENT_COLORS[event.type],
      })),
    [events]
  )

  const eventMap = useMemo(
    () => new Map(events.map((e) => [String(e.id), e])),
    [events]
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
        monthViewProps={{ firstDayOfWeek: 1 }}
      />
    </Box>
  )
}
