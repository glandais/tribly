import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import {
  useComments,
  useCreateComment,
  useDeleteComment,
  type EntityType,
} from '../../hooks/useComments'
import { CommentItem } from './CommentItem'
import { CommentForm } from './CommentForm'
import { LoadingSpinner } from '../common/LoadingSpinner'
import type { CommentDto } from '../../api/dto'
import { useAuth } from '../../hooks/useAuth'

interface CommentSectionProps {
  teamSlug: string
  entityType: EntityType
  entitySlug: string
  isOrganizer: boolean
}

export function CommentSection({
  teamSlug,
  entityType,
  entitySlug,
  isOrganizer,
}: CommentSectionProps) {
  const { t } = useTranslation('comments')
  const { user } = useAuth()
  const [replyingTo, setReplyingTo] = useState<string | null>(null)

  const { data, isLoading } = useComments(teamSlug, entityType, entitySlug)
  const createMutation = useCreateComment(teamSlug, entityType, entitySlug)
  const deleteMutation = useDeleteComment(teamSlug, entityType, entitySlug)

  const handleSubmit = (content: string, parentId?: string) => {
    createMutation.mutate(
      { content, parentId: parentId || undefined },
      {
        onSuccess: () => setReplyingTo(null),
      }
    )
  }

  const canDeleteComment = (comment: CommentDto) => {
    // Author can delete own, organizers/admins can delete any
    const isAuthor = comment.author.id === user?.id
    return isAuthor || isOrganizer
  }

  if (isLoading) {
    return (
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <div className="flex justify-center">
          <LoadingSpinner />
        </div>
      </div>
    )
  }

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
      <h2 className="text-lg font-semibold text-gray-900 mb-4">
        {t('title')} ({data?.total || 0})
      </h2>

      {/* Comment form for new top-level comments */}
      <CommentForm
        onSubmit={(content) => handleSubmit(content)}
        isLoading={createMutation.isPending}
        placeholder={t('form.placeholder')}
      />

      {/* Comments list */}
      <div className="mt-6 space-y-4">
        {data?.items.map((comment) => (
          <CommentItem
            key={comment.id}
            comment={comment}
            canDeleteComment={canDeleteComment}
            onDeleteComment={(commentId) => deleteMutation.mutate(commentId)}
            onReply={() => setReplyingTo(comment.id)}
            replyingTo={replyingTo}
            onReplySubmit={(content) => handleSubmit(content, comment.id)}
            onCancelReply={() => setReplyingTo(null)}
            isDeleting={deleteMutation.isPending}
            isReplying={createMutation.isPending}
          />
        ))}
        {(!data?.items || data.items.length === 0) && (
          <p className="text-gray-500 text-center py-4">{t('empty')}</p>
        )}
      </div>
    </div>
  )
}
