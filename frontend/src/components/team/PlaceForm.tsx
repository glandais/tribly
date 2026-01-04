import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import i18next from 'i18next'
import {
  useCreatePlace,
  useUpdatePlace,
  getListPlacesQueryKey,
  getGetPlaceQueryKey,
} from '../../api/endpoints/places/places'
import { createPlaceBody } from '../../api/zod/places/places.zod'
import type { PlaceDetailDto, PlaceRequest } from '../../api/dto'
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
import { useEffect } from 'react'

const placeSchema = createPlaceBody

interface PlaceFormProps {
  teamSlug: string
  place: PlaceDetailDto
  onClose: () => void
}

export function PlaceForm({ teamSlug, place, onClose }: PlaceFormProps) {
  const { t } = useTranslation('teams')
  const { t: tCommon } = useTranslation('common')
  const queryClient = useQueryClient()
  const createMutation = useCreatePlace()
  const updateMutation = useUpdatePlace()

  const isEditing = place.id !== ''

  const form = useForm<PlaceRequest>({
    resolver: zodResolver(placeSchema),
    mode: 'onChange',
  })

  // Sync form values when initial props change (for edit mode)
  useEffect(() => {
    form.reset(place)
  }, [place, form])

  const handleSubmit = (values: PlaceRequest) => {
    const data = values

    if (isEditing && place) {
      updateMutation.mutate(
        { slug: teamSlug, placeId: place.id, data },
        {
          onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: getListPlacesQueryKey(teamSlug) })
            queryClient.invalidateQueries({ queryKey: getGetPlaceQueryKey(teamSlug, place.id) })
            toast.success(i18next.t('teams:notifications.placeUpdated'))
            onClose()
          },
        }
      )
    } else {
      createMutation.mutate(
        { slug: teamSlug, data },
        {
          onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: getListPlacesQueryKey(teamSlug) })
            toast.success(i18next.t('teams:notifications.placeCreated'))
            onClose()
          },
        }
      )
    }
  }

  const mutation = isEditing ? updateMutation : createMutation

  const footerContent = (
    <>
      <Button type="button" variant="outline" onClick={onClose}>
        {tCommon('actions.cancelAction')}
      </Button>
      <Button
        type="submit"
        form="place-form"
        disabled={mutation.isPending || !form.formState.isValid}
      >
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
