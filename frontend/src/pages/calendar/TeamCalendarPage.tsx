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
import { useCalendarDateRange } from '@/hooks/useCalendarDateRange'

export function TeamCalendarPage(): React.ReactElement {
  const { t } = useTranslation()
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const { dateRange, handleDateRangeChange } = useCalendarDateRange()

  const { data: team, isLoading: isLoadingTeam } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })

  const { data: eventsData, isLoading: isLoadingEvents } = useGetTeamEvents(
    teamSlug!,
    { from: dateRange.from, to: dateRange.to },
    { query: { enabled: !!teamSlug, staleTime: 1000 * 60 * 5 } }
  )

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
          events={eventsData?.events ?? []}
          isLoading={isLoadingEvents}
          onDateRangeChange={handleDateRangeChange}
        />

        <IcsFeedSettings teamSlug={team.slug} />
      </Stack>
    </TeamLayout>
  )
}
