import { useParams, Navigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { UsersIcon, CalendarIcon } from '@heroicons/react/24/outline'
import { paths } from '../../config/paths'
import { useTeam } from '../../hooks/useTeam'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { TeamLayout } from '../../components/team/TeamLayout'
import { MediaDisplay } from '../../components/common/MediaDisplay'

export function TeamAboutPage() {
  const { t, i18n } = useTranslation('teams')
  const { teamSlug } = useParams<{ teamSlug: string }>()

  const { data: team, isLoading } = useTeam(teamSlug)

  if (isLoading) {
    return <LoadingPage message={t('detail.loading')} />
  }

  if (!team) {
    return <Navigate to={paths.teams()} replace />
  }

  return (
    <TeamLayout team={team} currentTab="about">
      <div className="py-6">
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <h2 className="text-xl font-semibold text-gray-900 mb-4">{t('detail.about.title')}</h2>

          {/* Description */}
          <div className="mb-6">
            <MediaDisplay media={team.media} className="text-gray-600" />
            {!team.media?.markdown && (
              <p className="text-gray-500 italic">{t('detail.about.noDescription')}</p>
            )}
          </div>

          {/* Stats */}
          <div className="border-t border-gray-200 pt-4">
            <dl className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div className="flex items-center gap-3">
                <div className="flex-shrink-0">
                  <UsersIcon className="w-5 h-5 text-gray-400" />
                </div>
                <div>
                  <dt className="text-sm text-gray-500">{t('detail.about.members')}</dt>
                  <dd className="text-lg font-medium text-gray-900">
                    {t('detail.info.memberCount', { count: team.memberCount })}
                  </dd>
                </div>
              </div>

              {team.createdAt && (
                <div className="flex items-center gap-3">
                  <div className="flex-shrink-0">
                    <CalendarIcon className="w-5 h-5 text-gray-400" />
                  </div>
                  <div>
                    <dt className="text-sm text-gray-500">{t('detail.about.created')}</dt>
                    <dd className="text-lg font-medium text-gray-900">
                      {new Date(team.createdAt).toLocaleDateString(i18n.language, {
                        day: 'numeric',
                        month: 'long',
                        year: 'numeric',
                      })}
                    </dd>
                  </div>
                </div>
              )}
            </dl>
          </div>
        </div>
      </div>
    </TeamLayout>
  )
}
