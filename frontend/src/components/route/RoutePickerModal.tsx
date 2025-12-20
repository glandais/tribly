import { useState, useEffect } from 'react'
import { useTranslation } from 'react-i18next'
import { useRoutes } from '../../hooks/useRoute'
import type { RouteDto } from '../../api/api'
import { LoadingSpinner } from '../common/LoadingSpinner'

interface RoutePickerModalProps {
  isOpen: boolean
  onClose: () => void
  onSelect: (route: RouteDto | null) => void
  teamSlug: string
  selectedRouteId?: string | null
  title?: string
  onCreateNew?: () => void
}

export function RoutePickerModal({
  isOpen,
  onClose,
  onSelect,
  teamSlug,
  selectedRouteId,
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

  const {
    data: routesResponse,
    isLoading,
    error,
  } = useRoutes(teamSlug, page, 20, debouncedSearch || undefined)

  const handleClose = () => {
    setSearch('')
    setPage(0)
    onClose()
  }

  if (!isOpen) return null

  const routes = routesResponse?.routes || []
  const total = routesResponse?.total || 0
  const totalPages = Math.ceil(total / 20)

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black bg-opacity-50">
      <div className="relative w-full max-w-4xl max-h-[90vh] bg-white rounded-lg shadow-xl flex flex-col">
        {/* Header */}
        <div className="flex items-center justify-between p-6 border-b border-gray-200">
          <h2 className="text-xl font-semibold text-gray-900">{title || t('picker.title')}</h2>
          <button onClick={handleClose} className="text-gray-400 hover:text-gray-600" type="button">
            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M6 18L18 6M6 6l12 12"
              />
            </svg>
          </button>
        </div>

        {/* Search bar */}
        <div className="p-6 border-b border-gray-200">
          <div className="flex gap-3">
            <div className="flex-1 relative">
              <input
                type="text"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                placeholder={t('picker.search')}
                className="w-full px-4 py-2 pl-10 border border-gray-300 rounded-lg focus:ring-indigo-500 focus:border-indigo-500"
              />
              <svg
                className="absolute left-3 top-2.5 h-5 w-5 text-gray-400"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
                />
              </svg>
            </div>
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
              <svg
                className="mx-auto h-12 w-12 text-gray-400"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7"
                />
              </svg>
              <p className="mt-2 text-gray-500">{t('picker.noResults')}</p>
              {selectedRouteId && (
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
                      route.id === selectedRouteId
                        ? 'border-indigo-500 bg-indigo-50 ring-2 ring-indigo-500'
                        : 'border-gray-200 hover:border-indigo-300 hover:bg-gray-50'
                    }`}
                  >
                    {route.thumbnailUrl && (
                      <img
                        src={`/api/download/${route.visibility.toLowerCase()}/teams/${teamSlug}/routes/${route.id}/thumbnail`}
                        alt={route.name}
                        className="w-full h-32 object-cover rounded mb-3"
                      />
                    )}
                    <h3 className="font-medium text-gray-900 truncate">{route.name}</h3>
                    {route.description && (
                      <p className="mt-1 text-sm text-gray-500 line-clamp-2">{route.description}</p>
                    )}
                    <div className="flex gap-3 mt-2 text-xs text-gray-500">
                      <span>📏 {(route.distance / 1000).toFixed(1)} km</span>
                      <span>⛰️ {route.elevationGain}m</span>
                      {route.difficulty && <span>💪 {t(`difficulty.${route.difficulty}`)}</span>}
                    </div>
                  </button>
                ))}
              </div>

              {/* Pagination */}
              {totalPages > 1 && (
                <div className="flex items-center justify-center gap-2 mt-6">
                  <button
                    type="button"
                    onClick={() => setPage(Math.max(0, page - 1))}
                    disabled={page === 0}
                    className="px-3 py-1 text-sm border border-gray-300 rounded disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
                  >
                    {t('common:pagination.previous')}
                  </button>
                  <span className="text-sm text-gray-600">
                    {t('common:pagination.page', { current: page + 1, total: totalPages })}
                  </span>
                  <button
                    type="button"
                    onClick={() => setPage(Math.min(totalPages - 1, page + 1))}
                    disabled={page >= totalPages - 1}
                    className="px-3 py-1 text-sm border border-gray-300 rounded disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
                  >
                    {t('common:pagination.next')}
                  </button>
                </div>
              )}
            </>
          )}
        </div>

        {/* Footer */}
        <div className="flex items-center justify-end gap-3 p-6 border-t border-gray-200">
          {selectedRouteId && (
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
