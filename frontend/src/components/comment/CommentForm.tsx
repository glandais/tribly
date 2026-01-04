import { useTranslation } from 'react-i18next'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { PaperAirplaneIcon } from '@heroicons/react/24/solid'
import { Button } from '@/components/ui/button'
import { Textarea } from '@/components/ui/textarea'
import { Form, FormControl, FormField, FormItem } from '@/components/ui/form'
import { LoadingSpinner } from '../common/LoadingSpinner'

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
  const { t } = useTranslation('comments')
  const { t: tCommon } = useTranslation('common')

  const form = useForm<CommentFormValues>({
    resolver: zodResolver(commentSchema),
    mode: 'onChange',
    defaultValues: { content: '' },
  })

  const handleSubmit = (values: CommentFormValues) => {
    onSubmit(values.content.trim())
    form.reset()
  }

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(handleSubmit)} className="flex gap-2">
        <FormField
          control={form.control}
          name="content"
          render={({ field }) => (
            <FormItem className="flex-1">
              <FormControl>
                <Textarea
                  {...field}
                  placeholder={placeholder || t('form.placeholder')}
                  autoFocus={autoFocus}
                  rows={2}
                  className="min-h-0 resize-none"
                  disabled={isLoading}
                />
              </FormControl>
            </FormItem>
          )}
        />
        <div className="flex flex-col gap-1">
          <Button type="submit" disabled={isLoading || !form.formState.isValid} size="icon">
            {isLoading ? <LoadingSpinner size="sm" /> : <PaperAirplaneIcon className="size-4" />}
          </Button>
          {onCancel && (
            <Button type="button" variant="ghost" size="sm" onClick={onCancel} className="text-xs">
              {tCommon('actions.cancelAction')}
            </Button>
          )}
        </div>
      </form>
    </Form>
  )
}
