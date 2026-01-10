import { useParams, Navigate } from 'react-router-dom'
import { useCanonicalPath } from '../../hooks/useCanonicalPath'
import { useTranslation } from 'react-i18next'
import { paths } from '../../config/paths'
import { Box, Paper, Title } from '@mantine/core'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import { useGetPage } from '@/api/endpoints/team-pages/team-pages'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { TeamAdminLayout } from '../../components/team/TeamAdminLayout'
import { TeamPageForm } from '../../components/team/TeamPageForm'

export function EditTeamPagePage() {
  const { teamSlug, pageSlug } = useParams<{ teamSlug: string; pageSlug: string }>()

  const { data: team, isLoading: isTeamLoading } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })
  const { data: page, isLoading: isPageLoading } = useGetPage(teamSlug!, pageSlug!, {
    query: { enabled: !!teamSlug && !!pageSlug },
  })
  const { t } = useTranslation()

  useCanonicalPath(team && page ? paths.teamAdminPageEdit(team.slug, page.slug) : undefined)

  if (isTeamLoading || isPageLoading) {
    return <LoadingPage message={t('loading')} />
  }

  if (!team) {
    return <Navigate to={paths.teams()} replace />
  }

  const isAdmin = team.role === 'ADMIN'
  if (!isAdmin) {
    return <Navigate to={paths.team(team.slug)} replace />
  }

  if (!page) {
    return <Navigate to={paths.teamAdminPages(team.slug)} replace />
  }

  return (
    <TeamAdminLayout team={team} currentTab="pages">
      <Box py="md">
        <Box maw={672}>
          <Title order={2} mb="lg">
            {t('teams.pages.edit.title', { name: page.title })}
          </Title>
          <Paper p="lg" withBorder>
            <TeamPageForm
              teamSlug={team.slug}
              pageSlug={page.slug}
              initialValues={page}
              isCreate={false}
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
