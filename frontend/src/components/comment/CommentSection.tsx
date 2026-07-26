import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { Paper, Title, Stack, Center, Loader } from '@mantine/core'
import { IconMessageCircle } from '@tabler/icons-react'
import { EmptyState } from '../common/EmptyState'
import {
  useComments,
  useCreateComment,
  useDeleteComment,
  type EntityType,
} from '../../hooks/useComments'
import { CommentItem } from './CommentItem'
import { CommentForm } from './CommentForm'
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
  const { t } = useTranslation()
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
      <Paper p="xl" shadow="xs" withBorder>
        <Center>
          <Loader />
        </Center>
      </Paper>
    )
  }

  return (
    <Paper p="xl" shadow="xs" withBorder>
      <Title order={4} mb="md">
        {t('comments.title')} ({data?.total || 0})
      </Title>

      {/* Comment form for new top-level comments */}
      <CommentForm
        onSubmit={(content) => handleSubmit(content)}
        isLoading={createMutation.isPending}
        placeholder={t('comments.form.placeholder')}
      />

      {/* Comments list */}
      <Stack mt="xl">
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
          <EmptyState
            icon={<IconMessageCircle size={48} />}
            title={t('comments.emptyTitle')}
            description={t('comments.empty')}
          />
        )}
      </Stack>
    </Paper>
  )
}
