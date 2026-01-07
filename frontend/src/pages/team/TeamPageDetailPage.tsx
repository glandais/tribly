import { useParams, Navigate } from 'react-router-dom'
import { useCanonicalPath } from '../../hooks/useCanonicalPath'
import { useTranslation } from 'react-i18next'
import { paths } from '../../config/paths'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import { useGetPage } from '@/api/endpoints/team-pages/team-pages'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { TeamLayout } from '../../components/team/TeamLayout'
import { MediaDisplay } from '../../components/common/MediaDisplay'
import { VisibilityBadge } from '../../components/common/card/VisibilityBadge'

export function TeamPageDetailPage() {
  const { teamSlug, pageSlug } = useParams<{ teamSlug: string; pageSlug: string }>()

  const { data: team, isLoading: isTeamLoading } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })
  const { data: page, isLoading: isPageLoading } = useGetPage(teamSlug!, pageSlug!, {
    query: { enabled: !!teamSlug && !!pageSlug },
  })

  const { t } = useTranslation()

  useCanonicalPath(team && page ? paths.teamPage(team.slug, page.slug) : undefined)

  if (isTeamLoading || isPageLoading) {
    return <LoadingPage message={t('loading')} />
  }

  if (!team) {
    return <Navigate to={paths.teams()} replace />
  }

  if (!page) {
    return <Navigate to={paths.team(team.slug)} replace />
  }

  const isMember = !!team.role

  return (
    <TeamLayout team={team} currentTab={page.slug}>
      <div className="py-6">
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div className="flex items-center gap-3 mb-4">
            <h2 className="text-xl font-semibold text-gray-900">{page.title}</h2>
            {page.visibility === 'TEAM' && isMember && (
              <VisibilityBadge visibility={page.visibility} />
            )}
          </div>

          {/* Page Content */}
          <div>
            <MediaDisplay media={page.media} className="text-gray-600" />
            {!page.media?.markdown && (
              <p className="text-gray-500 italic">{t('teams.pages.noContent')}</p>
            )}
          </div>
        </div>
      </div>
    </TeamLayout>
  )
}
