import { useState } from 'react'
import { Link, useParams, Navigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { PlusIcon, DocumentTextIcon } from '@heroicons/react/24/outline'
import { useTeam } from '../../hooks/useTeam'
import { usePosts } from '../../hooks/usePost'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { PostCard, PostCardSkeleton } from '../../components/post/PostCard'
import { TeamLayout } from '../../components/team/TeamLayout'
import { Pagination } from '../../components/common/Pagination'
import { usePagination } from '../../hooks/usePagination'
import { SearchInput } from '../../components/common/SearchInput'

export function PostListPage() {
  const { t } = useTranslation('posts')
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const [page, setPage] = useState(0)
  const [search, setSearch] = useState('')
  const pageSize = 20

  const { data: team, isLoading: isLoadingTeam } = useTeam(teamSlug)
  const { data: postsData, isLoading: isLoadingPosts } = usePosts(teamSlug, {
    search: search || undefined,
    page,
    size: pageSize,
  })

  const resetPage = () => setPage(0)

  // Use usePagination only for totalPages calculation
  const { totalPages } = usePagination({
    pageSize,
    totalItems: postsData?.total ?? 0,
  })

  if (isLoadingTeam) {
    return <LoadingPage message={t('loading')} />
  }

  if (!team) {
    return <Navigate to="/teams" replace />
  }

  const canCreate = team.role === 'ADMIN' || team.role === 'ORGANIZER'

  return (
    <TeamLayout team={team} currentTab="posts">
      <div className="py-6">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h2 className="text-2xl font-bold text-gray-900">{t('list.title')}</h2>
          </div>
          {canCreate && (
            <Link
              to={`/teams/${teamSlug}/posts/new`}
              className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-xs text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
            >
              <PlusIcon className="w-4 h-4 mr-2" />
              {t('list.createPost')}
            </Link>
          )}
        </div>

        {/* Search Input */}
        <SearchInput
          id="posts-search"
          value={search}
          onChange={(value) => {
            setSearch(value)
            resetPage()
          }}
          placeholder={t('list.search.placeholder')}
          label={t('list.search.label')}
          className="mb-6"
        />

        {/* Posts List */}
        {isLoadingPosts ? (
          <div className="space-y-4">
            {[...Array(3)].map((_, i) => (
              <PostCardSkeleton key={i} />
            ))}
          </div>
        ) : postsData?.posts && postsData.posts.length > 0 ? (
          <>
            <div className="space-y-4">
              {postsData.posts.map((post) => (
                <PostCard key={post.id} post={post} teamSlug={teamSlug!} />
              ))}
            </div>

            <Pagination
              currentPage={page}
              totalPages={totalPages}
              onPageChange={setPage}
              className="mt-8"
            />
          </>
        ) : (
          <div className="text-center py-12 bg-white rounded-lg shadow-sm border border-gray-200">
            <DocumentTextIcon className="mx-auto h-12 w-12 text-gray-400" />
            <h3 className="mt-4 text-lg font-medium text-gray-900">
              {search
                ? t('list.noResults')
                : canCreate
                  ? t('list.emptyAdmin')
                  : t('list.emptyMember')}
            </h3>
            {canCreate && !search && (
              <Link
                to={`/teams/${teamSlug}/posts/new`}
                className="mt-4 inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-xs text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
              >
                {t('list.createPost')}
              </Link>
            )}
          </div>
        )}
      </div>
    </TeamLayout>
  )
}
