import { useTranslation } from 'react-i18next'
import { keepPreviousData } from '@tanstack/react-query'
import { Title, Group } from '@mantine/core'
import { useGetEvents } from '@/api/endpoints/calendar/calendar'
import { CalendarView } from '@/components/calendar/CalendarView'
import { IcsFeedSettings } from '@/components/calendar/IcsFeedSettings'
import { HomeLayout } from '@/components/home/HomeLayout'
import { useCalendarDateRange } from '@/hooks/useCalendarDateRange'

export function CalendarPage(): React.ReactElement {
  const { t } = useTranslation()
  const { dateRange, handleDateRangeChange } = useCalendarDateRange()

  // `keepPreviousData` + `isFetching`: leaving the loaded window (a year view, a distant jump)
  // re-keys the query, and without it `events` would fall back to `[]` and empty the grid under
  // the overlay. The previous month's events stay on screen, dimmed by the overlay, until the new
  // ones arrive.
  const { data: eventsData, isFetching: isFetchingEvents } = useGetEvents(
    { from: dateRange.from, to: dateRange.to },
    { query: { staleTime: 1000 * 60 * 5, placeholderData: keepPreviousData } }
  )

  return (
    <HomeLayout currentTab="calendar">
      <Group justify="space-between" align="center">
        <Title order={2}>{t('calendar.title')}</Title>
      </Group>

      <CalendarView
        events={eventsData?.events ?? []}
        isLoading={isFetchingEvents}
        onDateRangeChange={handleDateRangeChange}
      />

      <IcsFeedSettings />
    </HomeLayout>
  )
}
