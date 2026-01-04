import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { useTranslation } from 'react-i18next'
import { useCreatePlace, useUpdatePlace } from '../../hooks/usePlaces'
import type { PlaceDetailDto } from '../../api/api'
import { Modal } from '../common/Modal'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Checkbox } from '@/components/ui/checkbox'
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form'

const placeSchema = z.object({
  name: z.string().min(1),
  address: z.string().optional(),
  link: z.url().optional(),
  startPlace: z.boolean(),
  endPlace: z.boolean(),
})

type PlaceFormValues = z.infer<typeof placeSchema>

interface PlaceFormProps {
  teamSlug: string
  place?: PlaceDetailDto
  onClose: () => void
}

export function PlaceForm({ teamSlug, place, onClose }: PlaceFormProps) {
  const { t } = useTranslation('teams')
  const { t: tCommon } = useTranslation('common')
  const createMutation = useCreatePlace(teamSlug)
  const updateMutation = useUpdatePlace(teamSlug, place?.id ?? '')

  const isEditing = !!place
  const mutation = isEditing ? updateMutation : createMutation

  const form = useForm<PlaceFormValues>({
    resolver: zodResolver(placeSchema),
    defaultValues: {
      name: place?.name ?? '',
      address: place?.address ?? '',
      link: place?.link ?? '',
      startPlace: place?.startPlace ?? true,
      endPlace: place?.endPlace ?? true,
    },
  })

  const handleSubmit = (values: PlaceFormValues) => {
    mutation.mutate(
      {
        name: values.name,
        address: values.address || undefined,
        link: values.link || undefined,
        startPlace: values.startPlace,
        endPlace: values.endPlace,
        coordinates: undefined,
      },
      {
        onSuccess: () => onClose(),
      }
    )
  }

  const footerContent = (
    <>
      <Button type="button" variant="outline" onClick={onClose}>
        {tCommon('actions.cancelAction')}
      </Button>
      <Button type="submit" form="place-form" disabled={mutation.isPending}>
        {mutation.isPending
          ? tCommon('loading')
          : isEditing
            ? tCommon('actions.save')
            : t('places.form.create')}
      </Button>
    </>
  )

  return (
    <Modal
      isOpen={true}
      onClose={onClose}
      title={isEditing ? t('places.form.editTitle') : t('places.add')}
      size="md"
      footer={footerContent}
    >
      <Form {...form}>
        <form id="place-form" onSubmit={form.handleSubmit(handleSubmit)} className="space-y-4">
          <FormField
            control={form.control}
            name="name"
            render={({ field }) => (
              <FormItem>
                <FormLabel>{t('places.form.name.label')} *</FormLabel>
                <FormControl>
                  <Input placeholder={t('places.form.name.placeholder')} {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          <FormField
            control={form.control}
            name="address"
            render={({ field }) => (
              <FormItem>
                <FormLabel>{t('places.form.address.label')}</FormLabel>
                <FormControl>
                  <Input placeholder={t('places.form.address.placeholder')} {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          <FormField
            control={form.control}
            name="link"
            render={({ field }) => (
              <FormItem>
                <FormLabel>{t('places.form.link.label')}</FormLabel>
                <FormControl>
                  <Input type="url" placeholder={t('places.form.link.placeholder')} {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          <div className="flex space-x-4">
            <FormField
              control={form.control}
              name="startPlace"
              render={({ field }) => (
                <FormItem className="flex items-center space-x-2 space-y-0">
                  <FormControl>
                    <Checkbox checked={field.value} onCheckedChange={field.onChange} />
                  </FormControl>
                  <FormLabel className="font-normal">{t('places.form.startPlace')}</FormLabel>
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="endPlace"
              render={({ field }) => (
                <FormItem className="flex items-center space-x-2 space-y-0">
                  <FormControl>
                    <Checkbox checked={field.value} onCheckedChange={field.onChange} />
                  </FormControl>
                  <FormLabel className="font-normal">{t('places.form.endPlace')}</FormLabel>
                </FormItem>
              )}
            />
          </div>
        </form>
      </Form>
    </Modal>
  )
}
