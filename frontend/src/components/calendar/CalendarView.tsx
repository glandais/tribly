import { useCallback, useRef } from 'react'
import { useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import FullCalendar from '@fullcalendar/react'
import dayGridPlugin from '@fullcalendar/daygrid'
import timeGridPlugin from '@fullcalendar/timegrid'
import interactionPlugin from '@fullcalendar/interaction'
import type { EventClickArg, DatesSetArg } from '@fullcalendar/core'
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

export function CalendarView({ events, isLoading, onDateRangeChange }: CalendarViewProps) {
  const navigate = useNavigate()
  const { i18n } = useTranslation()
  const calendarRef = useRef<FullCalendar>(null)

  const handleEventClick = useCallback(
    (info: EventClickArg) => {
      const event = info.event.extendedProps as CalendarEventDto
      if (event.type === 'RIDE') {
        navigate(paths.ride(event.teamSlug, event.entitySlug))
      } else if (event.type === 'TRIP_STAGE' && event.tripSlug) {
        navigate(paths.stage(event.teamSlug, event.tripSlug, event.entitySlug))
      }
    },
    [navigate]
  )

  const handleDatesSet = useCallback(
    (arg: DatesSetArg) => {
      onDateRangeChange(arg.start, arg.end)
    },
    [onDateRangeChange]
  )

  const fullCalendarEvents = events.map((event) => ({
    id: event.id,
    title: event.title,
    start: event.start,
    end: event.end ?? undefined,
    allDay: event.allDay,
    backgroundColor: event.color ?? EVENT_COLORS[event.type],
    borderColor: event.color ?? EVENT_COLORS[event.type],
    extendedProps: event,
  }))

  return (
    <Box pos="relative">
      <LoadingOverlay visible={isLoading} />
      <FullCalendar
        ref={calendarRef}
        plugins={[dayGridPlugin, timeGridPlugin, interactionPlugin]}
        initialView="dayGridMonth"
        headerToolbar={{
          left: 'prev,next today',
          center: 'title',
          right: 'dayGridMonth,timeGridWeek,timeGridDay',
        }}
        events={fullCalendarEvents}
        eventClick={handleEventClick}
        datesSet={handleDatesSet}
        locale={i18n.language}
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
