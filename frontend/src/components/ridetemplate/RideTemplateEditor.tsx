import { useTranslation } from 'react-i18next'
import { XMarkIcon } from '@heroicons/react/24/outline'
import { LoadingSpinner } from '../common/LoadingSpinner'
import { ReorderControls } from '../common/ReorderControls'
import { MarkdownEditor } from '../common/MarkdownEditor'
import type { RideTemplateRequest, TeamDetailDto } from '@/api/dto'
import { createTemplateBody } from '@/api/zod/ride-templates/ride-templates.zod'
import { useFieldArray, useForm, useWatch } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { Input } from '@/components/ui/input'

import { Button } from '@/components/ui/button'
import { Label } from '@/components/ui/label'
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group'
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form'

interface RideTemplateEditorProps {
  team: TeamDetailDto
  teamSlug: string
  initialValues: RideTemplateRequest
  onSubmit: (data: RideTemplateRequest) => void | Promise<void>
  onCancel: () => void
  isPending: boolean
  submitButtonText?: string
}

export function RideTemplateEditor({
  team,
  teamSlug: _teamSlug,
  initialValues,
  onSubmit,
  onCancel,
  isPending,
  submitButtonText,
}: RideTemplateEditorProps) {
  const { t } = useTranslation('rideTemplates')
  const { t: tCommon } = useTranslation('common')
  const { t: tRides } = useTranslation('rides')

  const form = useForm<RideTemplateRequest>({
    resolver: zodResolver(createTemplateBody),
    mode: 'onChange',
    defaultValues: initialValues,
  })

  const groups = useWatch({ control: form.control, name: 'groups' })

  const {
    fields: groupFieldArray,
    append,
    remove,
    move,
  } = useFieldArray({
    control: form.control,
    name: 'groups',
  })

  const handleAddGroup = () => {
    append({
      name: `Groupe ${groups.length + 1}`,
    })
  }

  const handleRemoveGroup = (index: number) => {
    remove(index)
  }

  const handleMoveGroup = (index: number, direction: 'up' | 'down') => {
    const newIndex = direction === 'up' ? index - 1 : index + 1
    if (newIndex >= 0 && newIndex < groupFieldArray.length) {
      move(index, newIndex)
    }
  }

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
        {/* Name */}
        <FormField
          control={form.control}
          name="name"
          render={({ field }) => (
            <FormItem>
              <FormLabel>
                {t('form.name.label')} <span className="text-destructive">*</span>
              </FormLabel>
              <FormControl>
                <Input placeholder={t('form.name.placeholder')} {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        {/* Description */}
        <FormField
          control={form.control}
          name="markdown"
          render={({ field }) => (
            <FormItem>
              <FormLabel>{tCommon('form.description')}</FormLabel>
              <FormControl>
                <MarkdownEditor
                  value={field.value || ''}
                  onChange={field.onChange}
                  placeholder={tCommon('form.description')}
                  minHeight="150px"
                  maxHeight="300px"
                  disabled={isPending}
                  ariaLabel={tCommon('form.description')}
                />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        {/* Visibility */}
        {team.visibility !== 'TEAM' && (
          <FormField
            control={form.control}
            name="visibility"
            render={({ field }) => (
              <FormItem>
                <FormLabel>{tCommon('visibility.label')}</FormLabel>
                <FormControl>
                  <RadioGroup
                    value={field.value}
                    onValueChange={field.onChange}
                    className="space-y-2"
                  >
                    <div className="flex items-center space-x-2">
                      <RadioGroupItem value="TEAM" id="visibility-team" />
                      <Label htmlFor="visibility-team">{tCommon('visibility.team')}</Label>
                    </div>
                    <div className="flex items-center space-x-2">
                      <RadioGroupItem value="PUBLIC" id="visibility-public" />
                      <Label htmlFor="visibility-public">{tCommon('visibility.public')}</Label>
                    </div>
                  </RadioGroup>
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
        )}

        {/* Status */}
        <FormField
          control={form.control}
          name="status"
          render={({ field }) => (
            <FormItem>
              <FormLabel>{tCommon('form.status')}</FormLabel>
              <FormControl>
                <RadioGroup
                  value={field.value}
                  onValueChange={field.onChange}
                  className="space-y-2"
                >
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="DRAFT" id="status-draft" />
                    <Label htmlFor="status-draft">{tCommon('status.DRAFT')}</Label>
                  </div>
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="PUBLISHED" id="status-published" />
                    <Label htmlFor="status-published">{tCommon('status.PUBLISHED')}</Label>
                  </div>
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="CANCELLED" id="status-cancelled" />
                    <Label htmlFor="status-cancelled">{tCommon('status.CANCELLED')}</Label>
                  </div>
                </RadioGroup>
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        {/* Groups */}
        <div className="space-y-2">
          <div className="flex items-center justify-between">
            <Label>{tRides('create.form.groups.label')}</Label>
            <Button
              type="button"
              variant="link"
              size="sm"
              className="h-auto p-0"
              onClick={handleAddGroup}
            >
              {tCommon('groups.add')}
            </Button>
          </div>
          <div className="space-y-3">
            {groupFieldArray.map((field, index) => {
              const group = groups?.[index]
              if (!group) return null

              return (
                <div key={field.id} className={`border rounded-lg p-4 border-border`}>
                  <div className="flex items-center justify-between mb-3">
                    <div className="flex items-center gap-2">
                      <ReorderControls
                        index={index}
                        total={groupFieldArray.length}
                        onMove={(dir) => handleMoveGroup(index, dir)}
                      />
                      <span className="text-sm font-medium">
                        {group.name ||
                          tRides('create.form.groups.defaultName', { number: index + 1 })}
                      </span>
                    </div>
                    <Button
                      type="button"
                      variant="link"
                      size="sm"
                      className="h-auto p-0 text-destructive"
                      onClick={() => handleRemoveGroup(index)}
                    >
                      {tRides('create.form.groups.remove')}
                    </Button>
                  </div>
                  <div className="grid grid-cols-1 sm:grid-cols-4 gap-3">
                    <FormField
                      control={form.control}
                      name={`groups.${index}.name`}
                      render={({ field }) => (
                        <FormItem>
                          <FormControl>
                            <Input
                              placeholder={tRides('create.form.groups.name.placeholder')}
                              {...field}
                            />
                          </FormControl>
                        </FormItem>
                      )}
                    />
                    <div className="relative">
                      <FormField
                        control={form.control}
                        name={`groups.${index}.time`}
                        render={({ field }) => (
                          <FormItem>
                            <FormControl>
                              <Input
                                type="time"
                                {...field}
                                value={field.value || ''}
                                onChange={(e) => field.onChange(e.target.value || undefined)}
                              />
                            </FormControl>
                          </FormItem>
                        )}
                      />
                      {group.time && (
                        <Button
                          type="button"
                          variant="ghost"
                          size="icon"
                          className="absolute right-8 top-1/2 -translate-y-1/2 size-6"
                          onClick={() => form.setValue(`groups.${index}.time`, undefined)}
                        >
                          <XMarkIcon className="size-4" />
                        </Button>
                      )}
                    </div>
                    <FormField
                      control={form.control}
                      name={`groups.${index}.averageSpeed`}
                      render={({ field }) => (
                        <FormItem>
                          <FormControl>
                            <Input
                              type="number"
                              placeholder={tRides('create.form.groups.speed.placeholder')}
                              min={0}
                              {...field}
                              value={field.value ?? ''}
                              onChange={(e) =>
                                field.onChange(e.target.value ? Number(e.target.value) : undefined)
                              }
                            />
                          </FormControl>
                        </FormItem>
                      )}
                    />
                    <FormField
                      control={form.control}
                      name={`groups.${index}.maxParticipants`}
                      render={({ field }) => (
                        <FormItem>
                          <FormControl>
                            <Input
                              type="number"
                              placeholder={tRides('create.form.groups.maxParticipants.placeholder')}
                              min={1}
                              {...field}
                              value={field.value ?? ''}
                              onChange={(e) =>
                                field.onChange(e.target.value ? Number(e.target.value) : undefined)
                              }
                            />
                          </FormControl>
                        </FormItem>
                      )}
                    />
                  </div>
                </div>
              )
            })}
            {groupFieldArray.length === 0 && (
              <p className="text-sm text-muted-foreground italic">
                {tRides('create.form.groups.empty')}
              </p>
            )}
          </div>
          <p className="text-sm text-muted-foreground">{tRides('create.form.groups.hint')}</p>
        </div>

        {/* Actions */}
        <div className="pt-4 flex items-center justify-end gap-3">
          <Button type="button" variant="outline" onClick={onCancel} disabled={isPending}>
            {tCommon('actions.cancelAction')}
          </Button>
          <Button type="submit" disabled={isPending || !form.formState.isValid}>
            {isPending ? (
              <>
                <LoadingSpinner size="sm" color="white" className="mr-2" />
                {tCommon('status.saving')}
              </>
            ) : (
              submitButtonText || tCommon('actions.save')
            )}
          </Button>
        </div>
      </form>
    </Form>
  )
}
