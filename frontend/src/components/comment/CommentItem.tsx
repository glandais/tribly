import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { Group, Stack, Text, Button, Box } from '@mantine/core'
import { IconTrash, IconMessage } from '@tabler/icons-react'
import { UserAvatar } from '../common/UserAvatar'
import { CommentForm } from './CommentForm'
import { ConfirmDialog } from '../common/ConfirmDialog'
import { useFormattedDate } from '../../utils/dateFormat'
import type { CommentDto } from '@/api/dto'

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
  const { t } = useTranslation()
  const { formatRelative } = useFormattedDate()
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false)

  const isReplyingToThis = replyingTo === comment.id

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

      {comment.replies && comment.replies.length > 0 && (
        <Stack gap="sm" mt="md">
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
