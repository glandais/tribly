import { useForm } from '@mantine/form'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { notifications } from '@mantine/notifications'
import i18next from 'i18next'
import {
  Button,
  ColorSwatch,
  Group,
  Input,
  Modal,
  Stack,
  Text,
  TextInput,
  Tooltip,
} from '@mantine/core'
import { IconCheck } from '@tabler/icons-react'
import {
  useCreateTeamTag,
  useUpdateTeamTag,
  getListTeamTagsQueryKey,
} from '@/api/endpoints/tags/tags'
import { invalidateTeamTags } from '@/lib/tagCacheInvalidation'
import { z } from 'zod'
import { CreateTeamTagBody } from '@/api/zod/tags/tags.zod'
import { zodFormValidator } from '@/lib/formUtils'
import { TagColor, type TagTarget, type TagWithUsageDto } from '@/api/dto'
import { TagChip } from './TagList'
import { tagFamily } from './tagFamily'

interface TagFormValues {
  label: string
  color: TagColor
}

/**
 * A label's length once trimmed (plan D16). Not the generated schema's: the API only caps the raw
 * label loosely there, and checks the 32 after its own trim (`TAG_LABEL_INVALID`).
 */
const TAG_LABEL_MAX_LENGTH = 32

// Checked on the trimmed values: blank and too long are both judged after the trim, as the API does.
const validateTag = zodFormValidator<TagFormValues>(
  CreateTeamTagBody.pick({ color: true }).extend({
    label: z.string().min(1).max(TAG_LABEL_MAX_LENGTH),
  })
)

// The API trims the label (plan D17); trimming here too keeps « Col » and « Col  » from reading as
// two labels in the form's preview and its length check.
const trimLabel = (values: TagFormValues): TagFormValues => ({
  ...values,
  label: values.label.trim(),
})

interface TagFormModalProps {
  teamSlug: string
  /** The kind a new tag gets; an edited tag keeps its own — a tag never changes kind. */
  type: TagTarget
  /** The tag being renamed or recoloured; absent, a new tag is created. */
  tag?: TagWithUsageDto
  onClose: () => void
}

/**
 * Creates a tag, or renames and recolours one (plan D12). Admin screen only: no content form ever
 * creates a tag (D4). Uniqueness of the label — per team and kind, whatever the case — is the
 * API's to check: its `TAG_LABEL_TAKEN` comes back as the usual error toast and the modal stays
 * open.
 */
export function TagFormModal({ teamSlug, type, tag, onClose }: TagFormModalProps) {
  const { t } = useTranslation()
  const queryClient = useQueryClient()
  const createMutation = useCreateTeamTag()
  const updateMutation = useUpdateTeamTag()
  const mutation = tag ? updateMutation : createMutation

  const form = useForm<TagFormValues>({
    initialValues: { label: tag?.label ?? '', color: tag?.color ?? TagColor.GRAY },
    validate: (values) => validateTag(trimLabel(values)),
    transformValues: trimLabel,
    validateInputOnChange: true,
  })

  const onSaved = (message: string) => {
    if (tag) {
      // A rename or a new colour shows on the cached contents too.
      invalidateTeamTags(queryClient, teamSlug)
    } else {
      // The common prefix: the admin list, and each kind's list read by the filters and pickers.
      queryClient.invalidateQueries({ queryKey: getListTeamTagsQueryKey(teamSlug) })
    }
    notifications.show({ message, color: 'green' })
    onClose()
  }

  const handleSubmit = (values: TagFormValues) => {
    if (tag) {
      updateMutation.mutate(
        { teamSlug, tagId: tag.id, data: values },
        { onSuccess: () => onSaved(i18next.t('tags.admin.notifications.updated')) }
      )
    } else {
      createMutation.mutate(
        { teamSlug, data: { ...values, type } },
        { onSuccess: () => onSaved(i18next.t('tags.admin.notifications.created')) }
      )
    }
  }

  const preview = form.values.label.trim()

  return (
    <Modal
      opened
      onClose={onClose}
      title={tag ? t('tags.admin.form.editTitle') : t('tags.admin.form.createTitle')}
      size="md"
    >
      <form onSubmit={form.onSubmit(handleSubmit)}>
        <Stack>
          <TextInput
            label={t('tags.admin.form.label')}
            placeholder={t('tags.admin.form.labelPlaceholder')}
            description={t('tags.admin.form.labelHint', { max: TAG_LABEL_MAX_LENGTH })}
            maxLength={TAG_LABEL_MAX_LENGTH}
            withAsterisk
            data-autofocus
            {...form.getInputProps('label')}
          />

          <Input.Wrapper label={t('tags.admin.form.color')} withAsterisk>
            <Group gap="xs" mt={4} role="radiogroup" aria-label={t('tags.admin.form.color')}>
              {Object.values(TagColor).map((color) => {
                const selected = form.values.color === color
                const name = t(`tags.color.${color satisfies TagColor}`)
                return (
                  <Tooltip key={color} label={name} withArrow>
                    <ColorSwatch
                      component="button"
                      type="button"
                      role="radio"
                      aria-checked={selected}
                      aria-label={name}
                      color={`var(--mantine-color-${tagFamily(color)}-6)`}
                      onClick={() => form.setFieldValue('color', color)}
                      style={{ cursor: 'pointer', color: 'var(--mantine-color-white)' }}
                      size={28}
                    >
                      {selected && <IconCheck size={16} />}
                    </ColorSwatch>
                  </Tooltip>
                )
              })}
            </Group>
          </Input.Wrapper>

          {preview && (
            <Group gap="xs">
              <Text size="sm" c="dimmed">
                {t('tags.admin.form.preview')}
              </Text>
              <TagChip tag={{ label: preview, color: form.values.color }} />
            </Group>
          )}

          <Group justify="flex-end" pt="md">
            <Button variant="default" onClick={onClose} disabled={mutation.isPending}>
              {t('actions.cancelAction')}
            </Button>
            <Button type="submit" loading={mutation.isPending} disabled={!form.isValid()}>
              {tag ? t('actions.save') : t('tags.admin.form.create')}
            </Button>
          </Group>
        </Stack>
      </form>
    </Modal>
  )
}
