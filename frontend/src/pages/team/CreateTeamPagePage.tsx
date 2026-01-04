import { useParams, Navigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { paths } from '../../config/paths'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { TeamAdminLayout } from '../../components/team/TeamAdminLayout'
import { TeamPageForm } from '../../components/team/TeamPageForm'
import { defaultMedia } from '@/lib/apiUtils'
import { Visibility } from '@/api/dto'

export function CreateTeamPagePage() {
  const { t } = useTranslation('teams')
  const { t: tCommon } = useTranslation('common')
  const { teamSlug } = useParams<{ teamSlug: string }>()

  const { data: team, isLoading } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })

  if (isLoading) {
    return <LoadingPage message={tCommon('loading')} />
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
      <div className="py-6">
        <div className="max-w-2xl">
          <h2 className="text-2xl font-bold text-gray-900 mb-6">{t('pages.create.title')}</h2>
          <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
            <TeamPageForm
              teamSlug={team.slug}
              isCreate={true}
              initialValues={initialValues}
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
