import { useTranslation } from 'react-i18next'
import { Stack, Title, Group } from '@mantine/core'
import { useGetEvents } from '@/api/endpoints/calendar/calendar'
import { CalendarView } from '@/components/calendar/CalendarView'
import { IcsFeedSettings } from '@/components/calendar/IcsFeedSettings'
import { HomeLayout } from '@/components/home/HomeLayout'
import { useCalendarDateRange } from '@/hooks/useCalendarDateRange'

export function CalendarPage(): React.ReactElement {
  const { t } = useTranslation()
  const { dateRange, handleDateRangeChange } = useCalendarDateRange()

  const { data: eventsData, isLoading: isLoadingEvents } = useGetEvents(
    { from: dateRange.from, to: dateRange.to },
    { query: { staleTime: 1000 * 60 * 5 } }
  )

  return (
    <HomeLayout currentTab="calendar">
      <Stack gap="lg" p="md">
        <Group justify="space-between" align="center">
          <Title order={2}>{t('calendar.title')}</Title>
        </Group>

        <CalendarView
          events={eventsData?.events ?? []}
          isLoading={isLoadingEvents}
          onDateRangeChange={handleDateRangeChange}
        />

        <IcsFeedSettings />
      </Stack>
    </HomeLayout>
  )
}
