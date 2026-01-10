import { useState, useCallback } from 'react'
import { useTranslation } from 'react-i18next'
import { Group, Text, TextInput, ActionIcon, Stack } from '@mantine/core'
import { notifications } from '@mantine/notifications'
import { IconPencil, IconCheck, IconX } from '@tabler/icons-react'

interface SlugEditorProps {
  currentSlug: string
  baseUrl: string
  onSlugChange: (newSlug: string) => Promise<void>
  disabled?: boolean
}

const SLUG_PATTERN = /^[a-z0-9]+(-[a-z0-9]+)*$/

export function SlugEditor({
  currentSlug,
  baseUrl,
  onSlugChange,
  disabled = false,
}: SlugEditorProps) {
  const { t } = useTranslation()
  const [isEditing, setIsEditing] = useState(false)
  const [newSlug, setNewSlug] = useState(currentSlug)
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const validateSlug = useCallback(
    (slug: string): string | null => {
      if (!slug || slug.length === 0) {
        return t('slug.error.empty')
      }
      if (slug.length > 200) {
        return t('slug.error.tooLong')
      }
      if (!SLUG_PATTERN.test(slug)) {
        return t('slug.error.invalid')
      }
      return null
    },
    [t]
  )

  const handleStartEdit = () => {
    setNewSlug(currentSlug)
    setError(null)
    setIsEditing(true)
  }

  const handleCancel = () => {
    setNewSlug(currentSlug)
    setError(null)
    setIsEditing(false)
  }

  const handleSave = async () => {
    const validationError = validateSlug(newSlug)
    if (validationError) {
      setError(validationError)
      return
    }

    if (newSlug === currentSlug) {
      setIsEditing(false)
      return
    }

    setIsLoading(true)
    setError(null)

    try {
      await onSlugChange(newSlug)
      notifications.show({
        message: t('slug.success'),
        color: 'green',
      })
      setIsEditing(false)
    } catch (err) {
      if (err && typeof err === 'object' && 'status' in err && err.status === 409) {
        setError(t('slug.error.taken'))
      }
    } finally {
      setIsLoading(false)
    }
  }

  const handleInputChange = (value: string) => {
    const cleanValue = value.toLowerCase().replace(/[^a-z0-9-]/g, '')
    setNewSlug(cleanValue)
    setError(null)
  }

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      e.preventDefault()
      handleSave()
    } else if (e.key === 'Escape') {
      handleCancel()
    }
  }

  if (!isEditing) {
    return (
      <Group gap="xs">
        <Text size="sm" fw={500} c="dimmed">
          {t('slug.label')}:
        </Text>
        <Text size="sm" ff="monospace">
          {baseUrl + currentSlug}
        </Text>
        {!disabled && (
          <ActionIcon
            variant="subtle"
            color="gray"
            size="sm"
            onClick={handleStartEdit}
            title={t('slug.edit')}
          >
            <IconPencil size={16} />
          </ActionIcon>
        )}
      </Group>
    )
  }

  return (
    <Stack gap="xs">
      <Group gap="xs">
        <Text size="sm" fw={500} c="dimmed">
          {t('slug.label')}:
        </Text>
        <Text size="sm" c="dimmed">
          {baseUrl}
        </Text>
        <TextInput
          value={newSlug}
          onChange={(e) => handleInputChange(e.currentTarget.value)}
          onKeyDown={handleKeyDown}
          disabled={isLoading}
          w={200}
          size="xs"
          ff="monospace"
          autoFocus
          error={error}
        />
        <ActionIcon
          variant="subtle"
          color="green"
          size="sm"
          onClick={handleSave}
          disabled={isLoading}
          title={t('actions.save')}
        >
          <IconCheck size={16} />
        </ActionIcon>
        <ActionIcon
          variant="subtle"
          color="gray"
          size="sm"
          onClick={handleCancel}
          disabled={isLoading}
          title={t('actions.cancelAction')}
        >
          <IconX size={16} />
        </ActionIcon>
      </Group>
      <Text size="xs" c="dimmed">
        {t('slug.hint')}
      </Text>
    </Stack>
  )
}
