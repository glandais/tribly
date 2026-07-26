import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { Group, Stack, Text, Button, Box } from '@mantine/core'
import { IconTrash, IconMessage } from '@tabler/icons-react'
import { UserAvatar } from '../common/UserAvatar'
import { CommentForm } from './CommentForm'
import { ConfirmDialog } from '../common/ConfirmDialog'
import { useFormattedDate } from '../../utils/dateFormat'
import type { CommentDto } from '@/api/dto'
import { useCommentReplies, type EntityType } from '../../hooks/useComments'

interface CommentItemProps {
  comment: CommentDto
  /** Needed to expand a thread whose replies were not embedded in the page. */
  teamSlug?: string
  entityType?: EntityType
  entitySlug?: string
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
  teamSlug,
  entityType,
  entitySlug,
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
  const { t } = useTranslation()
  const { formatRelative } = useFormattedDate()
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false)
  const [threadExpanded, setThreadExpanded] = useState(false)

  const isReplyingToThis = replyingTo === comment.id

  // A page of top-level comments does not necessarily embed every reply; `replyCount` is
  // authoritative, so anything it counts beyond what we hold is fetched on demand.
  const embedded = comment.replies ?? []
  const hasHiddenReplies = comment.replyCount > embedded.length
  const { data: fetchedReplies, isFetching: isFetchingReplies } = useCommentReplies(
    teamSlug,
    entityType ?? 'rides',
    entitySlug,
    comment.id,
    threadExpanded && hasHiddenReplies && !!teamSlug && !!entitySlug
  )
  const replies = threadExpanded && fetchedReplies ? fetchedReplies.items : embedded

  return (
    <Box
      ml={isReply ? 'xl' : 0}
      pl={isReply ? 'md' : 0}
      style={isReply ? { borderLeft: '2px solid var(--mantine-color-default-border)' } : undefined}
    >
      <Group align="flex-start" gap="sm">
        <UserAvatar user={comment.author} size="sm" />
        <Stack gap={4} style={{ flex: 1, minWidth: 0 }}>
          <Group gap="xs">
            <Text fw={500}>{comment.author.displayName}</Text>
            <Text size="xs" c="dimmed">
              {formatRelative(comment.createdAt)}
            </Text>
          </Group>
          <Text style={{ whiteSpace: 'pre-wrap', wordBreak: 'break-word' }}>{comment.content}</Text>

          <Group mt={4}>
            {!isReply && onReply && (
              <Button
                variant="subtle"
                size="xs"
                color="gray"
                leftSection={<IconMessage size={14} />}
                onClick={onReply}
              >
                {t('comments.actions.reply')}
              </Button>
            )}
            {canDelete && (
              <Button
                variant="subtle"
                size="xs"
                color="danger"
                leftSection={<IconTrash size={14} />}
                onClick={() => setShowDeleteConfirm(true)}
              >
                {t('actions.delete')}
              </Button>
            )}
          </Group>
        </Stack>
      </Group>

      {isReplyingToThis && onReplySubmit && onCancelReply && (
        <Box mt="sm" ml={44}>
          <CommentForm
            onSubmit={onReplySubmit}
            onCancel={onCancelReply}
            isLoading={isReplying}
            placeholder={t('comments.form.replyPlaceholder')}
            autoFocus
          />
        </Box>
      )}

      {!isReply && hasHiddenReplies && !threadExpanded && (
        <Button
          variant="subtle"
          size="xs"
          color="gray"
          mt="xs"
          ml={44}
          loading={isFetchingReplies}
          onClick={() => setThreadExpanded(true)}
        >
          {t('comments.showReplies', { count: comment.replyCount })}
        </Button>
      )}

      {replies.length > 0 && (
        <Stack gap="sm" mt="md">
          {replies.map((reply) => (
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
        </Stack>
      )}

      <ConfirmDialog
        isOpen={showDeleteConfirm}
        onClose={() => setShowDeleteConfirm(false)}
        onConfirm={() => {
          onDeleteComment(comment.id)
          setShowDeleteConfirm(false)
        }}
        title={t('comments.confirmDelete.title')}
        message={t('comments.confirmDelete.message')}
        confirmText={t('actions.delete')}
        variant="danger"
        isLoading={isDeleting}
      />
    </Box>
  )
}
