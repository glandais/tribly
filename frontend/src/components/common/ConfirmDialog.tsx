import { useTranslation } from 'react-i18next'
import { Modal, Button, Group, Text, Stack, Title } from '@mantine/core'

interface ConfirmDialogProps {
  isOpen: boolean
  onClose: () => void
  onConfirm: () => void
  title: string
  message: string
  confirmText?: string
  cancelText?: string
  variant?: 'danger' | 'warning' | 'info'
  isLoading?: boolean
}

const variantColors: Record<'danger' | 'warning' | 'info', string> = {
  danger: 'danger',
  warning: 'warning',
  info: 'primary',
}

export function ConfirmDialog({
  isOpen,
  onClose,
  onConfirm,
  title,
  message,
  confirmText,
  cancelText,
  variant = 'warning',
  isLoading = false,
}: ConfirmDialogProps) {
  const { t } = useTranslation()

  return (
    <Modal opened={isOpen} onClose={onClose} title={<Title order={4}>{title}</Title>} centered>
      <Stack>
        <Text c="dimmed">{message}</Text>
        <Group justify="flex-end" mt="md">
          <Button variant="default" onClick={onClose} disabled={isLoading}>
            {cancelText || t('actions.cancelAction')}
          </Button>
          <Button color={variantColors[variant]} onClick={onConfirm} loading={isLoading}>
            {confirmText || t('buttons.confirm')}
          </Button>
        </Group>
      </Stack>
    </Modal>
  )
}
