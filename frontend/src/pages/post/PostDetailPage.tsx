import { useState } from 'react'
import { Link, useParams, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import i18next from 'i18next'
import { CalendarIcon, PencilIcon, ChevronDownIcon } from '@heroicons/react/24/outline'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import {
  useGetPost,
  useUpdatePost,
  useDeletePost,
  getListPostsQueryKey,
  getGetPostQueryKey,
} from '../../api/endpoints/posts/posts'
import { Status } from '../../api/dto'
import { LoadingPage, LoadingSpinner } from '../../components/common/LoadingSpinner'
import { ConfirmDialog } from '../../components/common/ConfirmDialog'
import { MediaDisplay } from '../../components/common/MediaDisplay'
import { EntityLogo } from '../../components/common/EntityLogo'
import { CommentSection } from '../../components/comment'
import { useFormattedDate } from '../../utils/dateFormat'
import { Button } from '@/components/ui/button'
import { ButtonGroup } from '@/components/ui/button-group'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { paths } from '@/config/paths'

const statusColors: Record<Status, string> = {
  [Status.DRAFT]: 'bg-gray-100 text-gray-800',
  [Status.PUBLISHED]: 'bg-green-100 text-green-800',
  [Status.CANCELLED]: 'bg-red-100 text-red-800',
}

export function PostDetailPage() {
  const { t } = useTranslation('posts')
  const { t: tCommon } = useTranslation('common')
  const { formatDateTime } = useFormattedDate()
  const { teamSlug, postSlug } = useParams<{ teamSlug: string; postSlug: string }>()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const [showUnpublishConfirm, setShowUnpublishConfirm] = useState(false)
  const [showCancelConfirm, setShowCancelConfirm] = useState(false)
  const [showUncancelConfirm, setShowUncancelConfirm] = useState(false)
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false)

  const { data: team, isLoading: isLoadingTeam } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })
  const {
    data: post,
    isLoading: isLoadingPost,
    error,
  } = useGetPost(teamSlug!, postSlug!, { query: { enabled: !!teamSlug && !!postSlug } })

  const updateMutation = useUpdatePost()
  const deleteMutation = useDeletePost()

  if (isLoadingTeam || isLoadingPost) {
    return <LoadingPage message={tCommon('loading')} />
  }

  if (error || !post) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="text-center py-12">
          <h1 className="text-2xl font-bold text-gray-900 mb-2">{t('detail.notFound.title')}</h1>
          <p className="text-gray-600 mb-6">{t('detail.notFound.message')}</p>
          <Link
            to={paths.team(teamSlug!)}
            className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-xs text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
          >
            {t('breadcrumb.posts')}
          </Link>
        </div>
      </div>
    )
  }

  const isMember = !!team?.role
  const isAdmin = team?.role === 'ADMIN'
  const isOrganizer = team?.role === 'ORGANIZER'
  const canEdit = isAdmin || isOrganizer

  const formattedDate = formatDateTime(post.dateTime)

  const invalidatePosts = () => {
    queryClient.invalidateQueries({ queryKey: getListPostsQueryKey(teamSlug!) })
    queryClient.invalidateQueries({ queryKey: getGetPostQueryKey(teamSlug!, postSlug!) })
  }

  const handlePublish = () => {
    updateMutation.mutate(
      { slug: teamSlug!, postSlug: postSlug!, data: { ...post, status: Status.PUBLISHED } },
      {
        onSuccess: () => {
          invalidatePosts()
          toast.success(i18next.t('posts:notifications.updated'))
        },
      }
    )
  }

  const handleUnpublish = () => {
    updateMutation.mutate(
      { slug: teamSlug!, postSlug: postSlug!, data: { ...post, status: Status.DRAFT } },
      {
        onSuccess: () => {
          invalidatePosts()
          toast.success(i18next.t('posts:notifications.updated'))
          setShowUnpublishConfirm(false)
        },
      }
    )
  }

  const handleCancel = () => {
    updateMutation.mutate(
      { slug: teamSlug!, postSlug: postSlug!, data: { ...post, status: Status.CANCELLED } },
      {
        onSuccess: () => {
          invalidatePosts()
          toast.success(i18next.t('posts:notifications.updated'))
          setShowCancelConfirm(false)
        },
      }
    )
  }

  const handleUncancel = () => {
    updateMutation.mutate(
      { slug: teamSlug!, postSlug: postSlug!, data: { ...post, status: Status.PUBLISHED } },
      {
        onSuccess: () => {
          invalidatePosts()
          toast.success(i18next.t('posts:notifications.updated'))
          setShowUncancelConfirm(false)
        },
      }
    )
  }

  const handleDelete = () => {
    deleteMutation.mutate(
      { slug: teamSlug!, postSlug: postSlug! },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getListPostsQueryKey(teamSlug!) })
          toast.success(i18next.t('posts:notifications.deleted'))
          setShowDeleteConfirm(false)
          navigate(paths.team(teamSlug!))
        },
      }
    )
  }

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Header */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6 mb-6">
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
          <div className="flex items-center gap-3 min-w-0">
            <EntityLogo logo={post.media.assets.logo} alt={post.name} size="lg" />
            <h1 className="text-2xl font-bold text-gray-900 truncate">{post.name}</h1>
            <span
              className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium shrink-0 ${statusColors[post.status]}`}
            >
              {tCommon(`status.${post.status satisfies 'DRAFT' | 'PUBLISHED' | 'CANCELLED'}`)}
            </span>
          </div>

          {canEdit && (
            <ButtonGroup>
              <Button asChild variant="outline">
                <Link to={paths.postEdit(teamSlug!, postSlug!)}>
                  <PencilIcon className="w-4 h-4" />
                  {tCommon('actions.edit')}
                </Link>
              </Button>
              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <Button variant="outline" className="!pl-2">
                    <ChevronDownIcon className="w-4 h-4" />
                  </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end">
                  {post.status === Status.DRAFT && (
                    <DropdownMenuItem
                      onClick={handlePublish}
                      disabled={updateMutation.isPending}
                      className="text-green-700"
                    >
                      {updateMutation.isPending && <LoadingSpinner size="sm" />}
                      {tCommon('actions.publish')}
                    </DropdownMenuItem>
                  )}
                  {post.status === Status.PUBLISHED && (
                    <>
                      <DropdownMenuItem
                        onClick={() => setShowUnpublishConfirm(true)}
                        className="text-yellow-700"
                      >
                        {tCommon('actions.unpublish')}
                      </DropdownMenuItem>
                      <DropdownMenuItem
                        onClick={() => setShowCancelConfirm(true)}
                        className="text-yellow-700"
                      >
                        {tCommon('actions.cancelAction')}
                      </DropdownMenuItem>
                    </>
                  )}
                  {post.status === Status.CANCELLED && (
                    <DropdownMenuItem
                      onClick={() => setShowUncancelConfirm(true)}
                      className="text-green-700"
                    >
                      {t('detail.actions.uncancel')}
                    </DropdownMenuItem>
                  )}
                  <DropdownMenuSeparator />
                  <DropdownMenuItem
                    onClick={() => setShowDeleteConfirm(true)}
                    variant="destructive"
                  >
                    {tCommon('actions.delete')}
                  </DropdownMenuItem>
                </DropdownMenuContent>
              </DropdownMenu>
            </ButtonGroup>
          )}
        </div>

        <div className="mt-4">
          <MediaDisplay media={post.media} className="text-gray-600" />
        </div>
        {post.status === Status.DRAFT && post.publishAt && (
          <div className="mt-2 text-sm text-amber-600 flex items-center">
            <CalendarIcon className="w-4 h-4 mr-1" />
            {t('detail.scheduledPublish', {
              date: formatDateTime(post.publishAt),
            })}
          </div>
        )}
        <div className="mt-4 flex flex-wrap items-center gap-4 text-sm text-gray-500">
          <span className="flex items-center">
            <CalendarIcon className="w-4 h-4 mr-1" />
            {formattedDate}
          </span>
        </div>
      </div>

      {/* Comments Section - only visible to team members */}
      {isMember && (
        <CommentSection
          teamSlug={teamSlug!}
          entityType="posts"
          entitySlug={postSlug!}
          isOrganizer={canEdit}
        />
      )}

      {/* Confirmation Dialogs */}
      <ConfirmDialog
        isOpen={showUnpublishConfirm}
        onClose={() => setShowUnpublishConfirm(false)}
        onConfirm={handleUnpublish}
        title={tCommon('actions.unpublish')}
        message={t('detail.confirmations.unpublish')}
        confirmText={tCommon('actions.unpublish')}
        variant="warning"
        isLoading={updateMutation.isPending}
      />
      <ConfirmDialog
        isOpen={showCancelConfirm}
        onClose={() => setShowCancelConfirm(false)}
        onConfirm={handleCancel}
        title={tCommon('actions.cancelAction')}
        message={t('detail.confirmations.cancel')}
        confirmText={tCommon('actions.cancelAction')}
        variant="warning"
        isLoading={updateMutation.isPending}
      />
      <ConfirmDialog
        isOpen={showUncancelConfirm}
        onClose={() => setShowUncancelConfirm(false)}
        onConfirm={handleUncancel}
        title={t('detail.actions.uncancel')}
        message={t('detail.confirmations.uncancel')}
        confirmText={t('detail.actions.uncancel')}
        variant="info"
        isLoading={updateMutation.isPending}
      />
      <ConfirmDialog
        isOpen={showDeleteConfirm}
        onClose={() => setShowDeleteConfirm(false)}
        onConfirm={handleDelete}
        title={tCommon('actions.delete')}
        message={t('detail.confirmations.delete')}
        confirmText={tCommon('actions.delete')}
        variant="danger"
        isLoading={deleteMutation.isPending}
      />
    </div>
  )
}
