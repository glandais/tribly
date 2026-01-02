import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useCreatePlace, useUpdatePlace } from '../../hooks/usePlaces'
import type { PlaceDetailDto, PlaceRequest } from '../../api/api'
import { Modal } from '../common/Modal'

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

  const [formData, setFormData] = useState<PlaceRequest>({
    name: place?.name ?? '',
    address: place?.address ?? undefined,
    link: place?.link ?? undefined,
    startPlace: place?.startPlace ?? true,
    endPlace: place?.endPlace ?? true,
    coordinates: undefined,
  })

  const isEditing = !!place
  const mutation = isEditing ? updateMutation : createMutation

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    mutation.mutate(formData, {
      onSuccess: () => onClose(),
    })
  }

  const footerContent = (
    <>
      <button
        type="button"
        onClick={onClose}
        className="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
      >
        {tCommon('buttons.cancel')}
      </button>
      <button
        type="submit"
        form="place-form"
        disabled={mutation.isPending}
        className="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 disabled:bg-indigo-300"
      >
        {mutation.isPending
          ? tCommon('buttons.loading')
          : isEditing
            ? t('places.form.save')
            : t('places.form.create')}
      </button>
    </>
  )

  return (
    <Modal
      isOpen={true}
      onClose={onClose}
      title={isEditing ? t('places.form.editTitle') : t('places.form.createTitle')}
      size="md"
      footer={footerContent}
    >
      <form id="place-form" onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-700">
            {t('places.form.name.label')} *
          </label>
          <input
            type="text"
            required
            value={formData.name}
            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
            placeholder={t('places.form.name.placeholder')}
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">
            {t('places.form.address.label')}
          </label>
          <input
            type="text"
            value={formData.address ?? ''}
            onChange={(e) => setFormData({ ...formData, address: e.target.value || undefined })}
            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
            placeholder={t('places.form.address.placeholder')}
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">
            {t('places.form.link.label')}
          </label>
          <input
            type="url"
            value={formData.link ?? ''}
            onChange={(e) => setFormData({ ...formData, link: e.target.value || undefined })}
            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
            placeholder={t('places.form.link.placeholder')}
          />
        </div>

        <div className="flex space-x-4">
          <label className="flex items-center">
            <input
              type="checkbox"
              checked={formData.startPlace}
              onChange={(e) => setFormData({ ...formData, startPlace: e.target.checked })}
              className="h-4 w-4 rounded border-gray-300 text-indigo-600 focus:ring-indigo-500"
            />
            <span className="ml-2 text-sm text-gray-700">{t('places.form.startPlace')}</span>
          </label>
          <label className="flex items-center">
            <input
              type="checkbox"
              checked={formData.endPlace}
              onChange={(e) => setFormData({ ...formData, endPlace: e.target.checked })}
              className="h-4 w-4 rounded border-gray-300 text-indigo-600 focus:ring-indigo-500"
            />
            <span className="ml-2 text-sm text-gray-700">{t('places.form.endPlace')}</span>
          </label>
        </div>
      </form>
    </Modal>
  )
}
