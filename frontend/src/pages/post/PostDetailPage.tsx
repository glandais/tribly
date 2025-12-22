import { useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { ClockIcon, CalendarIcon, PencilIcon } from '@heroicons/react/24/outline'
import { useTeam } from '../../hooks/useTeam'
import { usePost, useUpdatePost, useDeletePost } from '../../hooks/usePost'
import { Status } from '../../hooks/usePost'
import { LoadingPage, LoadingSpinner } from '../../components/common/LoadingSpinner'
import { ConfirmDialog } from '../../components/common/ConfirmDialog'
import { useFormattedDate } from '../../utils/dateFormat'

const statusColors: Record<Status, string> = {
  [Status.Draft]: 'bg-gray-100 text-gray-800',
  [Status.Published]: 'bg-green-100 text-green-800',
  [Status.Cancelled]: 'bg-red-100 text-red-800',
}

export function PostDetailPage() {
  const { t } = useTranslation('posts')
  const { formatDate, formatDateTime, formatTime } = useFormattedDate()
  const { teamSlug, postSlug } = useParams<{ teamSlug: string; postSlug: string }>()
  const [showUnpublishConfirm, setShowUnpublishConfirm] = useState(false)
  const [showCancelConfirm, setShowCancelConfirm] = useState(false)
  const [showUncancelConfirm, setShowUncancelConfirm] = useState(false)
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false)

  const { data: team, isLoading: isLoadingTeam } = useTeam(teamSlug)
  const { data: post, isLoading: isLoadingPost, error } = usePost(teamSlug, postSlug)

  const updateMutation = useUpdatePost(teamSlug, postSlug!)
  const deleteMutation = useDeletePost(teamSlug)

  if (isLoadingTeam || isLoadingPost) {
    return <LoadingPage message={t('loading')} />
  }

  if (error || !post) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="text-center py-12">
          <h1 className="text-2xl font-bold text-gray-900 mb-2">{t('detail.notFound.title')}</h1>
          <p className="text-gray-600 mb-6">{t('detail.notFound.message')}</p>
          <Link
            to={`/teams/${teamSlug}/posts`}
            className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-xs text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
          >
            {t('breadcrumb.posts')}
          </Link>
        </div>
      </div>
    )
  }

  const isAdmin = team?.role === 'ADMIN'
  const isOrganizer = team?.role === 'ORGANIZER'
  const canEdit = isAdmin || isOrganizer

  const formattedDate = formatDate(post.dateTime)

  const handlePublish = () => {
    updateMutation.mutate({ ...post, status: Status.Published })
  }

  const handleUnpublish = () => {
    updateMutation.mutate({ ...post, status: Status.Draft })
    setShowUnpublishConfirm(false)
  }

  const handleCancel = () => {
    updateMutation.mutate({ ...post, status: Status.Cancelled })
    setShowCancelConfirm(false)
  }

  const handleUncancel = () => {
    updateMutation.mutate({ ...post, status: Status.Published })
    setShowUncancelConfirm(false)
  }

  const handleDelete = () => {
    deleteMutation.mutate(postSlug!)
    setShowDeleteConfirm(false)
  }

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Header */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6 mb-6">
        <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-4">
          <div className="flex-1">
            <div className="flex items-center gap-3">
              <h1 className="text-2xl font-bold text-gray-900">{post.name}</h1>
              <span
                className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${statusColors[post.status]}`}
              >
                {t(`status.${post.status}`)}
              </span>
            </div>
            {post.description && (
              <div className="mt-4 prose prose-sm max-w-none">
                <p className="text-gray-600 whitespace-pre-wrap">{post.description}</p>
              </div>
            )}
            {post.status === Status.Draft && post.publishAt && (
              <div className="mt-2 text-sm text-amber-600 flex items-center">
                <ClockIcon className="w-4 h-4 mr-1" />
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
              <span className="flex items-center">
                <ClockIcon className="w-4 h-4 mr-1" />
                {formatTime(post.dateTime)}
              </span>
            </div>
          </div>

          {canEdit && (
            <div className="flex items-center gap-2">
              <Link
                to={`/teams/${teamSlug}/posts/${postSlug}/edit`}
                className="inline-flex items-center px-3 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
              >
                <PencilIcon className="w-4 h-4 mr-1" />
                {t('detail.actions.edit')}
              </Link>
              {post.status === Status.Draft && (
                <button
                  onClick={handlePublish}
                  disabled={updateMutation.isPending}
                  className="inline-flex items-center px-3 py-2 border border-transparent rounded-md text-sm font-medium text-white bg-green-600 hover:bg-green-700 disabled:opacity-50"
                >
                  {updateMutation.isPending ? <LoadingSpinner size="sm" className="mr-2" /> : null}
                  {t('detail.actions.publish')}
                </button>
              )}
              {post.status === Status.Published && (
                <>
                  <button
                    onClick={() => setShowUnpublishConfirm(true)}
                    className="inline-flex items-center px-3 py-2 border border-yellow-300 rounded-md text-sm font-medium text-yellow-700 bg-white hover:bg-yellow-50"
                  >
                    {t('detail.actions.unpublish')}
                  </button>
                  <button
                    onClick={() => setShowCancelConfirm(true)}
                    className="inline-flex items-center px-3 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
                  >
                    {t('detail.actions.cancel')}
                  </button>
                </>
              )}
              {post.status === Status.Cancelled && (
                <button
                  onClick={() => setShowUncancelConfirm(true)}
                  className="inline-flex items-center px-3 py-2 border border-green-300 rounded-md text-sm font-medium text-green-700 bg-white hover:bg-green-50"
                >
                  {t('detail.actions.uncancel')}
                </button>
              )}
              <button
                onClick={() => setShowDeleteConfirm(true)}
                className="inline-flex items-center px-3 py-2 border border-red-300 rounded-md text-sm font-medium text-red-700 bg-white hover:bg-red-50"
              >
                {t('detail.actions.delete')}
              </button>
            </div>
          )}
        </div>
      </div>

      {/* Confirmation Dialogs */}
      <ConfirmDialog
        isOpen={showUnpublishConfirm}
        onClose={() => setShowUnpublishConfirm(false)}
        onConfirm={handleUnpublish}
        title={t('detail.actions.unpublish')}
        message={t('detail.confirmations.unpublish')}
        confirmText={t('detail.actions.unpublish')}
        variant="warning"
        isLoading={updateMutation.isPending}
      />
      <ConfirmDialog
        isOpen={showCancelConfirm}
        onClose={() => setShowCancelConfirm(false)}
        onConfirm={handleCancel}
        title={t('detail.actions.cancel')}
        message={t('detail.confirmations.cancel')}
        confirmText={t('detail.actions.cancel')}
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
        title={t('detail.actions.delete')}
        message={t('detail.confirmations.delete')}
        confirmText={t('detail.actions.delete')}
        variant="danger"
        isLoading={deleteMutation.isPending}
      />
    </div>
  )
}
