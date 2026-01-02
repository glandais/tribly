import { useState, useEffect } from 'react'
import { useTranslation } from 'react-i18next'
import { MapIcon, ArrowsPointingOutIcon, ArrowUpIcon } from '@heroicons/react/24/outline'
import { useRoutes } from '../../hooks/useRoute'
import type { RouteDto } from '../../api/api'
import { MarkdownDisplay } from '../../components/common/MarkdownDisplay'
import { LoadingSpinner } from '../common/LoadingSpinner'
import { Pagination } from '../common/Pagination'
import { usePagination } from '../../hooks/usePagination'
import { SearchInput } from '../common/SearchInput'
import { Modal } from '../common/Modal'

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
  const { t: tCommon } = useTranslation('common')
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

  const routes = routesResponse?.routes || []

  const searchBar = (
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
  )

  const footerContent = (
    <>
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
        {tCommon('buttons.cancel')}
      </button>
    </>
  )

  return (
    <Modal
      isOpen={isOpen}
      onClose={handleClose}
      title={title || t('picker.title')}
      size="4xl"
      subheader={searchBar}
      footer={footerContent}
    >
      {isLoading ? (
        <div className="flex flex-col items-center justify-center py-12">
          <LoadingSpinner />
          <p className="mt-2 text-gray-500">{t('picker.loading')}</p>
        </div>
      ) : error ? (
        <div className="text-center py-12 text-red-600">{tCommon('error.loading')}</div>
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
                  src={route.media.assets.thumbnail?.url}
                  alt={route.name}
                  className="w-full h-32 object-cover rounded mb-3"
                />
                <h3 className="font-medium text-gray-900 truncate">{route.name}</h3>
                <MarkdownDisplay
                  markdown={route.media.markdown || ''}
                  preview={true}
                  maxLength={120}
                  className="mt-1 text-sm text-gray-500"
                />
                <div className="flex gap-3 mt-2 text-xs text-gray-500">
                  <span className="flex items-center gap-1">
                    <ArrowsPointingOutIcon className="w-3.5 h-3.5" />
                    {(route.distance / 1000).toFixed(1)} {tCommon('units.km')}
                  </span>
                  <span className="flex items-center gap-1">
                    <ArrowUpIcon className="w-3.5 h-3.5" />
                    {route.elevationGain}
                    {tCommon('units.m')}
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
    </Modal>
  )
}
