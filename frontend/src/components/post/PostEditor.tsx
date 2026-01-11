import { useEffect } from 'react'
import { useForm } from '@mantine/form'
import { zod4Resolver } from 'mantine-form-zod-resolver'
import { useTranslation } from 'react-i18next'
import { TextInput, Radio, Stack, Group, Button, Text } from '@mantine/core'
import { DateTimePicker } from '@mantine/dates'
import type { TeamDetailDto } from '@/api/dto'
import { MediaEditor } from '../common/MediaEditor'
import { SlugEditor } from '../common/SlugEditor'
import { Status, PostRequest } from '@/api/dto'
import { createPostBody } from '@/api/zod/posts/posts.zod'

const postSchema = createPostBody.refine(
  (data) => {
    if (data.status === Status.DRAFT && data.publishAt) {
      return new Date(data.publishAt) > new Date()
    }
    return true
  },
  {
    message: 'Publish date must be in the future for draft posts',
    path: ['publishAt'],
  }
)

interface PostEditorProps {
  team: TeamDetailDto
  teamSlug: string
  initialValues: PostRequest
  onSubmit: (data: PostRequest) => void | Promise<void>
  onCancel: () => void
  isPending: boolean
  submitButtonText?: string
  cancelButtonText?: string
  currentSlug?: string
  onSlugChange?: (newSlug: string) => Promise<void>
  canEditSlug?: boolean
}

export function PostEditor({
  team,
  teamSlug,
  initialValues,
  onSubmit,
  onCancel,
  isPending,
  submitButtonText,
  cancelButtonText,
  currentSlug,
  onSlugChange,
  canEditSlug = false,
}: PostEditorProps) {
  const { t } = useTranslation()

  const form = useForm<PostRequest>({
    validate: zod4Resolver(postSchema) as any,
    initialValues,
    validateInputOnChange: true,
  })

  const status = form.values.status

  useEffect(() => {
    form.validateField('publishAt')
  }, [status, form])

  const handleSubmit = (values: PostRequest) => {
    onSubmit(values)
  }

  return (
    <form onSubmit={form.onSubmit(handleSubmit)}>
      <Stack gap="lg">
        <TextInput
          label={
            <>
              {t('form.title')}{' '}
              <Text span c="red">
                *
              </Text>
            </>
          }
          placeholder={t('posts.create.namePlaceholder')}
          {...form.getInputProps('name')}
        />

        {currentSlug && onSlugChange && (
          <SlugEditor
            currentSlug={currentSlug}
            baseUrl={`/teams/${teamSlug}/posts/`}
            onSlugChange={onSlugChange}
            disabled={!canEditSlug}
          />
        )}

        <Stack gap="xs">
          <Text size="sm" fw={500}>
            {t('form.description')}
          </Text>
          <MediaEditor
            value={form.values.media}
            onChange={(val) => form.setFieldValue('media', val)}
            placeholder={t('posts.create.descriptionPlaceholder')}
            minHeight="200px"
            maxHeight="400px"
            disabled={isPending}
            ariaLabel={t('form.description')}
            teamSlug={teamSlug}
          />
        </Stack>

        {team.visibility !== 'TEAM' && (
          <Radio.Group label={t('visibility.label')} {...form.getInputProps('visibility')}>
            <Stack gap="xs" mt="xs">
              <Radio value="TEAM" label={t('visibility.team')} />
              <Radio value="PUBLIC" label={t('visibility.public')} />
            </Stack>
          </Radio.Group>
        )}

        <Radio.Group label={t('form.status')} {...form.getInputProps('status')}>
          <Stack gap="xs" mt="xs">
            <Radio value="DRAFT" label={t('status.DRAFT')} />
            <Radio value="PUBLISHED" label={t('status.PUBLISHED')} />
          </Stack>
        </Radio.Group>

        {status === Status.PUBLISHED && (
          <DateTimePicker
            label={
              <>
                {t('posts.create.dateTimeLabel')}{' '}
                <Text span c="red">
                  *
                </Text>
              </>
            }
            description={t('posts.create.dateTimeHint')}
            value={form.values.dateTime ? new Date(form.values.dateTime) : null}
            onChange={(date) => {
              if (date) form.setFieldValue('dateTime', new Date(date).toISOString())
            }}
            error={form.errors.dateTime}
          />
        )}

        {status === Status.DRAFT && (
          <DateTimePicker
            label={t('posts.create.publishAtLabel')}
            description={t('form.publishAtHint')}
            value={form.values.publishAt ? new Date(form.values.publishAt) : null}
            onChange={(date) =>
              form.setFieldValue('publishAt', date ? new Date(date).toISOString() : undefined)
            }
            error={form.errors.publishAt}
            clearable
          />
        )}

        <Group justify="flex-end" pt="md">
          <Button variant="default" onClick={onCancel}>
            {cancelButtonText || t('actions.cancelAction')}
          </Button>
          <Button type="submit" disabled={isPending || !form.isValid()} loading={isPending}>
            {submitButtonText || t('actions.save')}
          </Button>
        </Group>
      </Stack>
    </form>
  )
}
