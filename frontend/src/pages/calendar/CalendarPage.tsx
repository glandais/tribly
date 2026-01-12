import { useState, useCallback } from 'react'
import { useTranslation } from 'react-i18next'
import { Stack, Title, Group } from '@mantine/core'
import { useGetEvents } from '@/api/endpoints/calendar/calendar'
import { CalendarView } from '@/components/calendar/CalendarView'
import { IcsFeedSettings } from '@/components/calendar/IcsFeedSettings'
import { HomeLayout } from '@/components/home/HomeLayout'

export function CalendarPage() {
  const { t } = useTranslation()
  const [dateRange, setDateRange] = useState<{ from: string; to: string }>(() => {
    const now = new Date()
    const from = new Date(now)
    from.setMonth(from.getMonth() - 1)
    const to = new Date(now)
    to.setMonth(to.getMonth() + 6)
    return {
      from: from.toISOString(),
      to: to.toISOString(),
    }
  })

  const { data: eventsData, isLoading: isLoadingEvents } = useGetEvents(
    { from: dateRange.from, to: dateRange.to },
    { query: { staleTime: 1000 * 60 * 5 } }
  )

  const handleDateRangeChange = useCallback((start: Date, end: Date) => {
    setDateRange({
      from: start.toISOString(),
      to: end.toISOString(),
    })
  }, [])

  return (
    <HomeLayout currentTab="calendar">
      <Stack gap="lg" p="md">
        <Group justify="space-between" align="center">
          <Title order={2}>{t('calendar.title')}</Title>
        </Group>

        <CalendarView
          events={eventsData?.events || []}
          isLoading={isLoadingEvents}
          onDateRangeChange={handleDateRangeChange}
        />

        <IcsFeedSettings />
      </Stack>
    </HomeLayout>
  )
}
