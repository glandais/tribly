import { useState, useEffect } from 'react'
import { useTranslation } from 'react-i18next'
import { XMarkIcon, MapIcon, ArrowsPointingOutIcon, ArrowUpIcon } from '@heroicons/react/24/outline'
import { useRoutes } from '../../hooks/useRoute'
import type { RouteDto } from '../../api/api'
import { MarkdownDisplay } from '../../components/common/MarkdownDisplay'
import { LoadingSpinner } from '../common/LoadingSpinner'
import { Pagination } from '../common/Pagination'
import { usePagination } from '../../hooks/usePagination'
import { SearchInput } from '../common/SearchInput'

interface RoutePickerModalProps {
  isOpen: boolean
  onClose: () => void
  onSelect: (route: RouteDto | null) => void
  teamSlug: string
  selectedRouteSlug?: string | null
  title?: string
  onCreateNew?: () => void
}

export function RoutePickerModal({
  isOpen,
  onClose,
  onSelect,
  teamSlug,
  selectedRouteSlug,
  title,
  onCreateNew,
}: RoutePickerModalProps) {
  const { t } = useTranslation('routes')
  const [page, setPage] = useState(0)
  const [search, setSearch] = useState('')
  const [debouncedSearch, setDebouncedSearch] = useState('')

  // Debounce search input
  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearch(search)
      setPage(0) // Reset to first page on search
    }, 300)

    return () => clearTimeout(timer)
  }, [search])

  const pageSize = 20

  const {
    data: routesResponse,
    isLoading,
    error,
  } = useRoutes(teamSlug, page, pageSize, debouncedSearch || undefined)

  // Use usePagination only for totalPages calculation
  const { totalPages } = usePagination({
    pageSize,
    totalItems: routesResponse?.total ?? 0,
  })

  const handleClose = () => {
    setSearch('')
    setPage(0)
    onClose()
  }

  if (!isOpen) return null

  const routes = routesResponse?.routes || []

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black bg-opacity-50">
      <div className="relative w-full max-w-4xl max-h-[90vh] bg-white rounded-lg shadow-xl flex flex-col">
        {/* Header */}
        <div className="flex items-center justify-between p-6 border-b border-gray-200">
          <h2 className="text-xl font-semibold text-gray-900">{title || t('picker.title')}</h2>
          <button onClick={handleClose} className="text-gray-400 hover:text-gray-600" type="button">
            <XMarkIcon className="w-6 h-6" />
          </button>
        </div>

        {/* Search bar */}
        <div className="p-6 border-b border-gray-200">
          <div className="flex gap-3">
            <SearchInput
              value={search}
              onChange={setSearch}
              placeholder={t('picker.search')}
              fullWidth
              className="flex-1"
            />
            {onCreateNew && (
              <button
                type="button"
                onClick={onCreateNew}
                className="px-4 py-2 text-sm font-medium text-white bg-indigo-600 rounded-lg hover:bg-indigo-700"
              >
                {t('picker.createNew')}
              </button>
            )}
          </div>
        </div>

        {/* Route list */}
        <div className="flex-1 overflow-y-auto p-6">
          {isLoading ? (
            <div className="flex flex-col items-center justify-center py-12">
              <LoadingSpinner />
              <p className="mt-2 text-gray-500">{t('picker.loading')}</p>
            </div>
          ) : error ? (
            <div className="text-center py-12 text-red-600">{t('common:error.loading')}</div>
          ) : routes.length === 0 ? (
            <div className="text-center py-12">
              <MapIcon className="mx-auto h-12 w-12 text-gray-400" />
              <p className="mt-2 text-gray-500">{t('picker.noResults')}</p>
              {selectedRouteSlug && (
                <button
                  type="button"
                  onClick={() => onSelect(null)}
                  className="mt-4 text-sm text-indigo-600 hover:text-indigo-700"
                >
                  {t('picker.clearSelection')}
                </button>
              )}
            </div>
          ) : (
            <>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {routes.map((route) => (
                  <button
                    key={route.id}
                    type="button"
                    onClick={() => onSelect(route)}
                    className={`text-left p-4 border rounded-lg transition-all ${
                      route.slug === selectedRouteSlug
                        ? 'border-indigo-500 bg-indigo-50 ring-2 ring-indigo-500'
                        : 'border-gray-200 hover:border-indigo-300 hover:bg-gray-50'
                    }`}
                  >
                    <img
                      src={`/api/download/${route.visibility.toLowerCase()}/teams/${teamSlug}/routes/${route.slug}/thumbnail`}
                      alt={route.name}
                      className="w-full h-32 object-cover rounded mb-3"
                    />
                    <h3 className="font-medium text-gray-900 truncate">{route.name}</h3>
                    {route.description && (
                      <MarkdownDisplay
                        content={route.description}
                        preview={true}
                        maxLength={120}
                        className="mt-1 text-sm text-gray-500"
                      />
                    )}
                    <div className="flex gap-3 mt-2 text-xs text-gray-500">
                      <span className="flex items-center gap-1">
                        <ArrowsPointingOutIcon className="w-3.5 h-3.5" />
                        {(route.distance / 1000).toFixed(1)} km
                      </span>
                      <span className="flex items-center gap-1">
                        <ArrowUpIcon className="w-3.5 h-3.5" />
                        {route.elevationGain}m
                      </span>
                    </div>
                  </button>
                ))}
              </div>

              <Pagination
                currentPage={page}
                totalPages={totalPages}
                onPageChange={setPage}
                variant="compact"
                className="mt-6"
              />
            </>
          )}
        </div>

        {/* Footer */}
        <div className="flex items-center justify-end gap-3 p-6 border-t border-gray-200">
          {selectedRouteSlug && (
            <button
              type="button"
              onClick={() => onSelect(null)}
              className="px-4 py-2 text-sm font-medium text-red-600 hover:text-red-700"
            >
              {t('picker.clearSelection')}
            </button>
          )}
          <button
            type="button"
            onClick={handleClose}
            className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50"
          >
            {t('common:buttons.cancel')}
          </button>
        </div>
      </div>
    </div>
  )
}
