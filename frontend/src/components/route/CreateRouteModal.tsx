import { useTranslation } from 'react-i18next'
import { XMarkIcon } from '@heroicons/react/24/outline'
import { Visibility } from '../../api/api'
import type { RouteDto } from '../../api/api'
import { RouteForm } from './RouteForm'

interface CreateRouteModalProps {
  isOpen: boolean
  onClose: () => void
  onRouteCreated: (route: RouteDto) => void
  teamSlug: string
  teamVisibility?: Visibility
}

export function CreateRouteModal({
  isOpen,
  onClose,
  onRouteCreated,
  teamSlug,
  teamVisibility,
}: CreateRouteModalProps) {
  const { t } = useTranslation('routes')

  const handleSuccess = (route: RouteDto) => {
    onRouteCreated(route)
    onClose()
  }

  if (!isOpen) return null

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black bg-opacity-50">
      <div className="relative w-full max-w-2xl max-h-[90vh] bg-white rounded-lg shadow-xl flex flex-col">
        {/* Header */}
        <div className="flex items-center justify-between p-6 border-b border-gray-200">
          <h2 className="text-xl font-semibold text-gray-900">{t('createModal.title')}</h2>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600" type="button">
            <XMarkIcon className="w-6 h-6" />
          </button>
        </div>

        {/* Form */}
        <div className="flex-1 overflow-y-auto p-6">
          <RouteForm
            teamSlug={teamSlug}
            teamVisibility={teamVisibility}
            onSuccess={handleSuccess}
            onCancel={onClose}
            submitButtonText={t('createModal.create')}
            showCancelButton={false}
          />
        </div>
      </div>
    </div>
  )
}
