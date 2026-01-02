import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { TrashIcon, ChatBubbleLeftIcon } from '@heroicons/react/24/outline'
import { UserAvatar } from '../common/UserAvatar'
import { CommentForm } from './CommentForm'
import { ConfirmDialog } from '../common/ConfirmDialog'
import { useFormattedDate } from '../../utils/dateFormat'
import type { CommentDto } from '../../api/api'

interface CommentItemProps {
  comment: CommentDto
  canDeleteComment: (comment: CommentDto) => boolean
  onDeleteComment: (commentId: string) => void
  onReply?: () => void
  replyingTo: string | null
  onReplySubmit?: (content: string) => void
  onCancelReply?: () => void
  isDeleting: boolean
  isReplying: boolean
  isReply?: boolean
}

export function CommentItem({
  comment,
  canDeleteComment,
  onDeleteComment,
  onReply,
  replyingTo,
  onReplySubmit,
  onCancelReply,
  isDeleting,
  isReplying,
  isReply = false,
}: CommentItemProps) {
  const canDelete = canDeleteComment(comment)
  const { t } = useTranslation('comments')
  const { formatRelative } = useFormattedDate()
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false)

  const isReplyingToThis = replyingTo === comment.id

  return (
    <div className={`${isReply ? 'ml-8 border-l-2 border-gray-200 pl-4' : ''}`}>
      <div className="flex items-start gap-3">
        <UserAvatar user={comment.author} size="sm" />
        <div className="flex-1 min-w-0">
          <div className="flex items-center gap-2">
            <span className="font-medium text-gray-900">{comment.author.displayName}</span>
            <span className="text-xs text-gray-500">{formatRelative(comment.createdAt)}</span>
          </div>
          <p className="mt-1 text-gray-700 whitespace-pre-wrap break-words">{comment.content}</p>

          <div className="mt-2 flex items-center gap-4">
            {!isReply && onReply && (
              <button
                onClick={onReply}
                className="text-sm text-gray-500 hover:text-gray-700 flex items-center gap-1"
              >
                <ChatBubbleLeftIcon className="w-4 h-4" />
                {t('actions.reply')}
              </button>
            )}
            {canDelete && (
              <button
                onClick={() => setShowDeleteConfirm(true)}
                className="text-sm text-red-500 hover:text-red-700 flex items-center gap-1"
              >
                <TrashIcon className="w-4 h-4" />
                {t('actions.delete')}
              </button>
            )}
          </div>
        </div>
      </div>

      {/* Reply form */}
      {isReplyingToThis && onReplySubmit && onCancelReply && (
        <div className="mt-3 ml-11">
          <CommentForm
            onSubmit={onReplySubmit}
            onCancel={onCancelReply}
            isLoading={isReplying}
            placeholder={t('form.replyPlaceholder')}
            autoFocus
          />
        </div>
      )}

      {/* Nested replies */}
      {comment.replies && comment.replies.length > 0 && (
        <div className="mt-4 space-y-3">
          {comment.replies.map((reply) => (
            <CommentItem
              key={reply.id}
              comment={reply}
              canDeleteComment={canDeleteComment}
              onDeleteComment={onDeleteComment}
              replyingTo={null}
              isDeleting={isDeleting}
              isReplying={isReplying}
              isReply
            />
          ))}
        </div>
      )}

      <ConfirmDialog
        isOpen={showDeleteConfirm}
        onClose={() => setShowDeleteConfirm(false)}
        onConfirm={() => {
          onDeleteComment(comment.id)
          setShowDeleteConfirm(false)
        }}
        title={t('confirmDelete.title')}
        message={t('confirmDelete.message')}
        confirmText={t('actions.delete')}
        variant="danger"
        isLoading={isDeleting}
      />
    </div>
  )
}
