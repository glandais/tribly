import { useState, useCallback } from 'react'
import { Navigate, useParams } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { Stack, Title } from '@mantine/core'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import { useGetTeamEvents } from '@/api/endpoints/calendar/calendar'
import { LoadingPage } from '@/components/common/LoadingSpinner'
import { CalendarView } from '@/components/calendar/CalendarView'
import { IcsFeedSettings } from '@/components/calendar/IcsFeedSettings'
import { TeamLayout } from '@/components/team/TeamLayout'
import { paths } from '@/config/paths'
import { useCanonicalPath } from '@/hooks/useCanonicalPath'

export function TeamCalendarPage() {
  const { t } = useTranslation()
  const { teamSlug } = useParams<{ teamSlug: string }>()
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

  const { data: team, isLoading: isLoadingTeam } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })

  const { data: eventsData, isLoading: isLoadingEvents } = useGetTeamEvents(
    teamSlug!,
    { from: dateRange.from, to: dateRange.to },
    { query: { enabled: !!teamSlug, staleTime: 1000 * 60 * 5 } }
  )

  const handleDateRangeChange = useCallback((start: Date, end: Date) => {
    setDateRange({
      from: start.toISOString(),
      to: end.toISOString(),
    })
  }, [])

  useCanonicalPath(team ? paths.teamCalendar(team.slug) : undefined)

  if (isLoadingTeam) {
    return <LoadingPage message={t('calendar.title')} />
  }

  if (!team) {
    return <Navigate to={paths.teams()} replace />
  }

  return (
    <TeamLayout team={team} currentTab="calendar">
      <Stack gap="lg">
        <Title order={2}>{t('calendar.title')}</Title>

        <CalendarView
          events={eventsData?.events || []}
          isLoading={isLoadingEvents}
          onDateRangeChange={handleDateRangeChange}
        />

        <IcsFeedSettings teamSlug={team.slug} />
      </Stack>
    </TeamLayout>
  )
}
