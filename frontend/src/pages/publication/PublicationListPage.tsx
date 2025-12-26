import { useParams, Navigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { NewspaperIcon } from '@heroicons/react/24/outline'
import { useTeam } from '../../hooks/useTeam'
import { usePublications } from '../../hooks/usePublications'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { RideCard, RideCardSkeleton } from '../../components/ride/RideCard'
import { PostCard, PostCardSkeleton } from '../../components/post/PostCard'
import { TeamLayout } from '../../components/team/TeamLayout'
import type { RideDto, PostDto } from '../../api/api'

export function PublicationListPage() {
  const { t } = useTranslation('teams')
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const { data: team, isLoading: isLoadingTeam } = useTeam(teamSlug)
  const { data: publicationsData, isLoading: isLoadingPublications } = usePublications(teamSlug)

  if (isLoadingTeam) {
    return <LoadingPage message={t('publications.list.title')} />
  }

  if (!team) {
    return <Navigate to="/teams" replace />
  }

  return (
    <TeamLayout team={team} currentTab="publications">
      <div className="py-6">
        <div className="mb-6">
          <h2 className="text-2xl font-bold text-gray-900">{t('publications.list.title')}</h2>
        </div>

        {/* Publications List */}
        {isLoadingPublications ? (
          <div className="space-y-4">
            {[...Array(3)].map((_, i) =>
              // Alternate between ride and post skeletons for visual variety
              i % 2 === 0 ? <RideCardSkeleton key={i} /> : <PostCardSkeleton key={i} />
            )}
          </div>
        ) : publicationsData?.publications && publicationsData.publications.length > 0 ? (
          <div className="space-y-4">
            {publicationsData.publications.map((publication) => {
              // Discriminated union type narrowing based on 'type' field
              if (publication.type === 'ride') {
                return (
                  <RideCard
                    key={publication.id}
                    ride={publication as RideDto}
                    teamSlug={teamSlug!}
                    showTypeBadge={true}
                  />
                )
              }
              return (
                <PostCard
                  key={publication.id}
                  post={publication as PostDto}
                  teamSlug={teamSlug!}
                  showTypeBadge={true}
                />
              )
            })}
          </div>
        ) : (
          <div className="text-center py-12 bg-white rounded-lg shadow-sm border border-gray-200">
            <NewspaperIcon className="mx-auto h-12 w-12 text-gray-400" />
            <h3 className="mt-4 text-lg font-medium text-gray-900">
              {t('publications.list.empty')}
            </h3>
            <p className="mt-2 text-gray-500">{t('publications.list.emptyDescription')}</p>
          </div>
        )}
      </div>
    </TeamLayout>
  )
}
