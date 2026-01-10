import { useParams, Navigate } from 'react-router-dom'
import { useCanonicalPath } from '../../hooks/useCanonicalPath'
import { useTranslation } from 'react-i18next'
import { paths } from '../../config/paths'
import { Box, Paper, Title } from '@mantine/core'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { TeamAdminLayout } from '../../components/team/TeamAdminLayout'
import { TeamPageForm } from '../../components/team/TeamPageForm'
import { defaultMedia } from '@/lib/apiUtils'
import { Visibility } from '@/api/dto'

export function CreateTeamPagePage() {
  const { t } = useTranslation()
  const { teamSlug } = useParams<{ teamSlug: string }>()

  const { data: team, isLoading } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })

  useCanonicalPath(team ? paths.teamAdminPageNew(team.slug) : undefined)

  if (isLoading) {
    return <LoadingPage message={t('loading')} />
  }

  if (!team) {
    return <Navigate to={paths.teams()} replace />
  }

  const isAdmin = team.role === 'ADMIN'
  if (!isAdmin) {
    return <Navigate to={paths.team(team.slug)} replace />
  }

  const initialValues = { title: '', visibility: Visibility.TEAM, media: defaultMedia() }

  return (
    <TeamAdminLayout team={team} currentTab="pages">
      <Box py="md">
        <Box maw={672}>
          <Title order={2} mb="lg">
            {t('teams.pages.create.title')}
          </Title>
          <Paper p="lg" withBorder>
            <TeamPageForm
              teamSlug={team.slug}
              isCreate={true}
              initialValues={initialValues}
              onSuccess={() => {
                // Navigation is handled by the hook
              }}
            />
          </Paper>
        </Box>
      </Box>
    </TeamAdminLayout>
  )
}
