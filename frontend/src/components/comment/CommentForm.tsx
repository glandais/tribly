import { useTranslation } from 'react-i18next'
import { useForm } from '@mantine/form'
import { zod4Resolver } from 'mantine-form-zod-resolver'
import { z } from 'zod'
import { Group, Textarea, Button, ActionIcon, Stack, Loader } from '@mantine/core'
import { IconSend } from '@tabler/icons-react'

const commentSchema = z.object({
  content: z.string().min(1),
})

type CommentFormValues = z.infer<typeof commentSchema>

interface CommentFormProps {
  onSubmit: (content: string) => void
  onCancel?: () => void
  isLoading: boolean
  placeholder?: string
  autoFocus?: boolean
}

export function CommentForm({
  onSubmit,
  onCancel,
  isLoading,
  placeholder,
  autoFocus = false,
}: CommentFormProps) {
  const { t } = useTranslation()

  const form = useForm<CommentFormValues>({
    validate: zod4Resolver(commentSchema),
    initialValues: { content: '' },
    validateInputOnChange: true,
  })

  const handleSubmit = (values: CommentFormValues) => {
    onSubmit(values.content.trim())
    form.reset()
  }

  return (
    <form onSubmit={form.onSubmit(handleSubmit)}>
      <Group gap="xs" align="flex-start">
        <Textarea
          {...form.getInputProps('content')}
          placeholder={placeholder || t('comments.form.placeholder')}
          autoFocus={autoFocus}
          rows={2}
          autosize
          disabled={isLoading}
          style={{ flex: 1 }}
        />
        <Stack gap={4}>
          <ActionIcon
            type="submit"
            disabled={isLoading || !form.isValid()}
            size="lg"
            variant="filled"
          >
            {isLoading ? <Loader size="sm" color="white" /> : <IconSend size={18} />}
          </ActionIcon>
          {onCancel && (
            <Button variant="subtle" size="xs" onClick={onCancel}>
              {t('actions.cancelAction')}
            </Button>
          )}
        </Stack>
      </Group>
    </form>
  )
}
