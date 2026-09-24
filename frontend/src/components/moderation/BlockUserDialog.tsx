import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { notifications } from '@mantine/notifications'
import { useBlockUser, getListMyBlockedUsersQueryKey } from '@/api/endpoints/moderation/moderation'
import { ConfirmDialog } from '../common/ConfirmDialog'
import { invalidateModeratedContent } from '@/lib/moderationCacheInvalidation'

interface BlockUserDialogProps {
  opened: boolean
  onClose: () => void
  user: { id: string; displayName: string }
  onBlocked?: () => void
}

/**
 * Confirms a block. A block is silent and one-way: the blocked member is not told, and only the
 * reader's own comments, posts and ads lists change — so every list is refetched once it is done.
 */
export function BlockUserDialog({ opened, onClose, user, onBlocked }: BlockUserDialogProps) {
  const { t } = useTranslation()
  const queryClient = useQueryClient()
  const blockMutation = useBlockUser()

  const handleConfirm = () => {
    blockMutation.mutate(
      { userId: user.id },
      {
        onSuccess: () => {
          void invalidateModeratedContent(queryClient)
          void queryClient.invalidateQueries({ queryKey: getListMyBlockedUsersQueryKey() })
          notifications.show({
            message: t('moderation.block.success', { name: user.displayName }),
            color: 'green',
          })
          onClose()
          onBlocked?.()
        },
      }
    )
  }

  return (
    <ConfirmDialog
      isOpen={opened}
      onClose={onClose}
      onConfirm={handleConfirm}
      title={t('moderation.block.confirmTitle', { name: user.displayName })}
      message={t('moderation.block.confirmMessage', { name: user.displayName })}
      confirmText={t('moderation.block.confirm')}
      variant="danger"
      isLoading={blockMutation.isPending}
    />
  )
}
