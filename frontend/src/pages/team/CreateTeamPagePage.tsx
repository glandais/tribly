import { useParams, Navigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { paths } from '../../config/paths'
import { useTeam } from '../../hooks/useTeam'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { TeamAdminLayout } from '../../components/team/TeamAdminLayout'
import { TeamPageForm } from '../../components/team/TeamPageForm'

export function CreateTeamPagePage() {
  const { t } = useTranslation('teams')
  const { teamSlug } = useParams<{ teamSlug: string }>()

  const { data: team, isLoading } = useTeam(teamSlug)

  if (isLoading) {
    return <LoadingPage message={t('pages.loading')} />
  }

  if (!team) {
    return <Navigate to={paths.teams()} replace />
  }

  const isAdmin = team.role === 'ADMIN'
  if (!isAdmin) {
    return <Navigate to={paths.team(team.slug)} replace />
  }

  return (
    <TeamAdminLayout team={team} currentTab="pages">
      <div className="py-6">
        <div className="max-w-2xl">
          <h2 className="text-2xl font-bold text-gray-900 mb-6">{t('pages.create.title')}</h2>
          <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
            <TeamPageForm
              teamSlug={team.slug}
              isCreate={true}
              onSuccess={() => {
                // Navigation is handled by the hook
              }}
            />
          </div>
        </div>
      </div>
    </TeamAdminLayout>
  )
}
