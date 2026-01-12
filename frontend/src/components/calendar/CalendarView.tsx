import { useCallback, useMemo, useRef } from 'react'
import { useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import FullCalendar from '@fullcalendar/react'
import dayGridPlugin from '@fullcalendar/daygrid'
import multimonthPlugin from '@fullcalendar/multimonth'
import timeGridPlugin from '@fullcalendar/timegrid'
import interactionPlugin from '@fullcalendar/interaction'
import classicThemePlugin from '@fullcalendar/theme-classic'
import type { LocaleInput, EventClickData, DatesSetData } from '@fullcalendar/core'
import frLocale from '@fullcalendar/core/locales/fr'
import enLocale from '@fullcalendar/core/locales/en-gb'
import { LoadingOverlay, Box } from '@mantine/core'
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

const locales: Record<'fr' | 'en', LocaleInput> = {
  fr: frLocale,
  en: enLocale,
}

function getLocale(lang: string): LocaleInput {
  return lang in locales ? locales[lang as keyof typeof locales] : enLocale
}

export function CalendarView({
  events,
  isLoading,
  onDateRangeChange,
}: CalendarViewProps): React.ReactElement {
  const navigate = useNavigate()
  const { i18n } = useTranslation()
  const calendarRef = useRef<FullCalendar>(null)

  const handleEventClick = useCallback(
    (info: EventClickData) => {
      const event = info.event.extendedProps as CalendarEventDto
      switch (event.type) {
        case 'RIDE':
          navigate(paths.ride(event.teamSlug, event.entitySlug))
          break
        case 'TRIP_STAGE':
          if (event.tripSlug) {
            navigate(paths.stage(event.teamSlug, event.tripSlug, event.entitySlug))
          }
          break
      }
    },
    [navigate]
  )

  const handleDatesSet = useCallback(
    (arg: DatesSetData) => {
      onDateRangeChange(arg.start, arg.end)
    },
    [onDateRangeChange]
  )

  const fullCalendarEvents = useMemo(
    () =>
      events.map((event) => {
        const color = EVENT_COLORS[event.type]
        return {
          id: event.id,
          title: event.title,
          start: event.start,
          end: event.end ?? undefined,
          allDay: event.allDay,
          backgroundColor: color,
          borderColor: color,
          extendedProps: event,
        }
      }),
    [events]
  )

  return (
    <Box pos="relative">
      <LoadingOverlay visible={isLoading} />
      <FullCalendar
        ref={calendarRef}
        plugins={[
          multimonthPlugin,
          dayGridPlugin,
          timeGridPlugin,
          interactionPlugin,
          classicThemePlugin,
        ]}
        initialView="dayGridMonth"
        headerToolbar={{
          left: 'prev,today,next',
          center: 'title',
          right: 'multiMonthYear,dayGridMonth',
        }}
        events={fullCalendarEvents}
        eventClick={handleEventClick}
        datesSet={handleDatesSet}
        locale={getLocale(i18n.language)}
        firstDay={1}
        height="auto"
        eventTimeFormat={{
          hour: '2-digit',
          minute: '2-digit',
          hour12: false,
        }}
      />
    </Box>
  )
}
