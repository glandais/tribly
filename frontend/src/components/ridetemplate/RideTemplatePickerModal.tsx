import { useState, useEffect } from 'react'
import { useTranslation } from 'react-i18next'
import { keepPreviousData } from '@tanstack/react-query'
import { DocumentDuplicateIcon, UserGroupIcon } from '@heroicons/react/24/outline'
import { useListTemplates } from '@/api/endpoints/ride-templates/ride-templates'
import type { RideTemplateDto } from '@/api/dto'
import { MarkdownDisplay } from '../common/MarkdownDisplay'
import { LoadingSpinner } from '../common/LoadingSpinner'
import { Pagination } from '../common/Pagination'
import { usePagination } from '../../hooks/usePagination'
import { SearchInput } from '../common/SearchInput'
import { Modal } from '../common/Modal'

interface RideTemplatePickerModalProps {
  isOpen: boolean
  onClose: () => void
  onSelect: (template: RideTemplateDto) => void
  teamSlug: string
  title?: string
}

export function RideTemplatePickerModal({
  isOpen,
  onClose,
  onSelect,
  teamSlug,
  title,
}: RideTemplatePickerModalProps) {
  const { t } = useTranslation()
  const [page, setPage] = useState(0)
  const [search, setSearch] = useState('')
  const [debouncedSearch, setDebouncedSearch] = useState('')

  // Debounce search input
  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearch(search)
      setPage(0)
    }, 300)

    return () => clearTimeout(timer)
  }, [search])

  const pageSize = 20

  const {
    data: templatesResponse,
    isLoading,
    error,
  } = useListTemplates(
    teamSlug,
    {
      search: debouncedSearch || undefined,
      page,
      size: pageSize,
    },
    { query: { placeholderData: keepPreviousData } }
  )

  const { totalPages } = usePagination({
    pageSize,
    totalItems: templatesResponse?.total ?? 0,
  })

  const handleClose = () => {
    setSearch('')
    setPage(0)
    onClose()
  }

  const handleSelect = (template: RideTemplateDto) => {
    onSelect(template)
    handleClose()
  }

  const templates = templatesResponse?.templates || []

  const searchBar = (
    <SearchInput
      value={search}
      onChange={setSearch}
      placeholder={t('rideTemplates.picker.search')}
      fullWidth
    />
  )

  const footerContent = (
    <button
      type="button"
      onClick={handleClose}
      className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50"
    >
      {t('actions.cancelAction')}
    </button>
  )

  return (
    <Modal
      isOpen={isOpen}
      onClose={handleClose}
      title={title || t('rideTemplates.picker.title')}
      size="2xl"
      subheader={searchBar}
      footer={footerContent}
    >
      {isLoading ? (
        <div className="flex flex-col items-center justify-center py-12">
          <LoadingSpinner />
          <p className="mt-2 text-gray-500">{t('loading')}</p>
        </div>
      ) : error ? (
        <div className="text-center py-12 text-red-600">{t('error.loading')}</div>
      ) : templates.length === 0 ? (
        <div className="text-center py-12">
          <DocumentDuplicateIcon className="mx-auto h-12 w-12 text-gray-400" />
          <p className="mt-2 text-gray-500">{t('rideTemplates.list.noResults')}</p>
        </div>
      ) : (
        <>
          <div className="space-y-3">
            {templates.map((template) => (
              <button
                key={template.id}
                type="button"
                onClick={() => handleSelect(template)}
                className="w-full text-left p-4 border border-gray-200 rounded-lg hover:border-indigo-300 hover:bg-gray-50 transition-all"
              >
                <div className="flex items-start justify-between">
                  <div className="flex-1 min-w-0">
                    <h3 className="font-medium text-gray-900 truncate">{template.name}</h3>
                    {template.markdown && (
                      <MarkdownDisplay
                        markdown={template.markdown}
                        preview={true}
                        maxLength={120}
                        className="mt-1 text-sm text-gray-500"
                      />
                    )}
                  </div>
                  <div className="flex items-center gap-1 ml-4 text-sm text-gray-500">
                    <UserGroupIcon className="w-4 h-4" />
                    <span>{t('groups.groupCount', { count: template.groupCount })}</span>
                  </div>
                </div>
                {template.groups && template.groups.length > 0 && (
                  <div className="mt-3 flex flex-wrap gap-2">
                    {template.groups.map((group) => (
                      <span
                        key={group.id}
                        className="inline-flex items-center px-2 py-1 text-xs font-medium bg-gray-100 text-gray-700 rounded"
                      >
                        {group.name}
                        {group.averageSpeed && (
                          <span className="ml-1 text-gray-400">
                            {t('speed', { speed: group.averageSpeed })}
                          </span>
                        )}
                      </span>
                    ))}
                  </div>
                )}
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
