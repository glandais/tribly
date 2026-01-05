import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import i18next from 'i18next'
import { PencilIcon, TrashIcon, PlusIcon, MapPinIcon } from '@heroicons/react/24/outline'
import {
  useListPlaces,
  useDeletePlace,
  getListPlacesQueryKey,
} from '../../api/endpoints/places/places'
import { ConfirmDialog } from '../common/ConfirmDialog'
import { LoadingSpinner } from '../common/LoadingSpinner'
import { PlaceForm } from './PlaceForm'
import type { PlaceDetailDto } from '../../api/dto'

interface PlaceListProps {
  teamSlug: string
  canManage: boolean
}

export function PlaceList({ teamSlug, canManage }: PlaceListProps) {
  const { t } = useTranslation()
  const queryClient = useQueryClient()
  const { data: placesData, isLoading } = useListPlaces(teamSlug)
  const deleteMutation = useDeletePlace()

  const [editingPlace, setEditingPlace] = useState<PlaceDetailDto | null>(null)
  const [showCreateForm, setShowCreateForm] = useState(false)
  const [deleteConfirm, setDeleteConfirm] = useState<string | null>(null)

  if (isLoading) {
    return (
      <div className="flex justify-center py-8">
        <LoadingSpinner />
      </div>
    )
  }

  const places = placesData?.places ?? []

  const handleDelete = (placeId: string) => {
    deleteMutation.mutate(
      { slug: teamSlug, placeId },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getListPlacesQueryKey(teamSlug) })
          toast.success(i18next.t('teams.notifications.placeDeleted'))
          setDeleteConfirm(null)
        },
      }
    )
  }

  function newPlace(): PlaceDetailDto {
    return {
      id: '',
      name: '',
      startPlace: true,
      endPlace: true,
    }
  }

  return (
    <div className="space-y-4">
      <div className="flex justify-between items-center">
        <h3 className="text-lg font-medium text-gray-900">{t('places.title')}</h3>
        {canManage && (
          <button
            onClick={() => setShowCreateForm(true)}
            className="inline-flex items-center px-3 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700"
          >
            <PlusIcon className="h-4 w-4 mr-1" />
            {t('places.add')}
          </button>
        )}
      </div>

      {places.length === 0 ? (
        <p className="text-gray-500 text-sm py-4">{t('places.empty')}</p>
      ) : (
        <ul className="divide-y divide-gray-200 border border-gray-200 rounded-lg">
          {places.map((place) => (
            <li key={place.id} className="py-4 px-4 flex justify-between items-center">
              <div className="flex items-start space-x-3">
                <MapPinIcon className="h-5 w-5 text-gray-400 mt-0.5" />
                <div>
                  <p className="text-sm font-medium text-gray-900">{place.name}</p>
                  {place.address && <p className="text-sm text-gray-500">{place.address}</p>}
                  {place.link && (
                    <a
                      href={place.link}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="text-sm text-indigo-600 hover:text-indigo-800"
                    >
                      {t('places.viewLink')}
                    </a>
                  )}
                  <div className="flex space-x-2 mt-1">
                    {place.startPlace && (
                      <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-green-100 text-green-800">
                        {t('startPlace')}
                      </span>
                    )}
                    {place.endPlace && (
                      <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-blue-100 text-blue-800">
                        {t('endPlace')}
                      </span>
                    )}
                  </div>
                </div>
              </div>
              {canManage && (
                <div className="flex space-x-2">
                  <button
                    onClick={() => setEditingPlace(place)}
                    className="p-1 text-gray-400 hover:text-gray-600"
                    title={t('actions.edit')}
                  >
                    <PencilIcon className="h-4 w-4" />
                  </button>
                  <button
                    onClick={() => setDeleteConfirm(place.id)}
                    className="p-1 text-gray-400 hover:text-red-600"
                    title={t('places.delete')}
                  >
                    <TrashIcon className="h-4 w-4" />
                  </button>
                </div>
              )}
            </li>
          ))}
        </ul>
      )}

      {/* Create Form Modal */}
      {showCreateForm && (
        <PlaceForm
          teamSlug={teamSlug}
          place={newPlace()}
          onClose={() => setShowCreateForm(false)}
        />
      )}

      {/* Edit Form Modal */}
      {editingPlace && (
        <PlaceForm teamSlug={teamSlug} place={editingPlace} onClose={() => setEditingPlace(null)} />
      )}

      {/* Delete Confirmation */}
      <ConfirmDialog
        isOpen={!!deleteConfirm}
        onClose={() => setDeleteConfirm(null)}
        onConfirm={() => deleteConfirm && handleDelete(deleteConfirm)}
        title={t('places.deleteConfirm.title')}
        message={t('places.deleteConfirm.message')}
        confirmText={t('places.deleteConfirm.confirm')}
        variant="danger"
        isLoading={deleteMutation.isPending}
      />
    </div>
  )
}
