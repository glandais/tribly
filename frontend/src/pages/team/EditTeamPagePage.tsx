import { useParams, Navigate } from 'react-router-dom'
import { useCanonicalPath } from '../../hooks/useCanonicalPath'
import { useTranslation } from 'react-i18next'
import { paths } from '../../config/paths'
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
      <div className="py-6">
        <div className="max-w-2xl">
          <h2 className="text-2xl font-bold text-gray-900 mb-6">
            {t('teams.pages.edit.title', { name: page.title })}
          </h2>
          <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
            <TeamPageForm
              teamSlug={team.slug}
              pageSlug={page.slug}
              initialValues={page}
              isCreate={false}
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
