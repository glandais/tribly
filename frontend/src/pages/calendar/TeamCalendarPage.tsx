import { Navigate, useParams } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { Stack, Title } from '@mantine/core'
import { LoadingPage } from '@/components/common/LoadingSpinner'
import { CalendarView } from '@/components/calendar/CalendarView'
import { IcsFeedSettings } from '@/components/calendar/IcsFeedSettings'
import { TeamLayout } from '@/components/team/TeamLayout'
import { paths } from '@/config/paths'
import { useCanonicalPath } from '@/hooks/useCanonicalPath'
import { useTeamCalendarData } from '@/pages/calendar/teamCalendarData'

export function TeamCalendarPage(): React.ReactElement {
  const { t } = useTranslation()
  const { teamSlug } = useParams<{ teamSlug: string }>()

  const { team, events, handleDateRangeChange } = useTeamCalendarData(teamSlug)
  const { data: teamData, isLoading: isLoadingTeam } = team
  const { data: eventsData, isLoading: isLoadingEvents } = events

  useCanonicalPath(teamData ? paths.teamCalendar(teamData.slug) : undefined)

  if (isLoadingTeam) {
    return <LoadingPage message={t('calendar.title')} />
  }

  if (!teamData) {
    return <Navigate to={paths.teams()} replace />
  }

  return (
    <TeamLayout team={teamData} currentTab="calendar">
      <Stack>
        <Title order={2}>{t('calendar.title')}</Title>

        <CalendarView
          events={eventsData?.events ?? []}
          isLoading={isLoadingEvents}
          onDateRangeChange={handleDateRangeChange}
        />

        <IcsFeedSettings teamSlug={teamData.slug} />
      </Stack>
    </TeamLayout>
  )
}
