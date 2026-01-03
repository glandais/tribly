import { useState } from 'react'
import { Link, useParams, Navigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { paths } from '../../config/paths'
import {
  PlusIcon,
  DocumentDuplicateIcon,
  PencilIcon,
  TrashIcon,
  UserGroupIcon,
} from '@heroicons/react/24/outline'
import { useTeam } from '../../hooks/useTeam'
import { useRideTemplates, useDeleteRideTemplate } from '../../hooks/useRideTemplate'
import { LoadingPage, LoadingSpinner } from '../../components/common/LoadingSpinner'
import { TeamAdminLayout } from '../../components/team/TeamAdminLayout'
import { Pagination } from '../../components/common/Pagination'
import { usePagination } from '../../hooks/usePagination'
import { SearchInput } from '../../components/common/SearchInput'
import { ConfirmDialog } from '../../components/common/ConfirmDialog'
import { MarkdownDisplay } from '../../components/common/MarkdownDisplay'
import type { RideTemplateDto } from '../../api/api'

export function RideTemplateListPage() {
  const { t } = useTranslation('rideTemplates')
  const { t: tCommon } = useTranslation('common')
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const [page, setPage] = useState(0)
  const [search, setSearch] = useState('')
  const [templateToDelete, setTemplateToDelete] = useState<RideTemplateDto | null>(null)
  const pageSize = 20

  const { data: team, isLoading: isLoadingTeam } = useTeam(teamSlug)
  const { data: templatesData, isLoading: isLoadingTemplates } = useRideTemplates(teamSlug, {
    search: search || undefined,
    page,
    size: pageSize,
  })
  const deleteMutation = useDeleteRideTemplate(teamSlug)

  const resetPage = () => setPage(0)

  const { totalPages } = usePagination({
    pageSize,
    totalItems: templatesData?.total ?? 0,
  })

  if (isLoadingTeam) {
    return <LoadingPage message={tCommon('loading')} />
  }

  if (!team) {
    return <Navigate to={paths.teams()} replace />
  }

  const canManage = team.role === 'ADMIN' || team.role === 'ORGANIZER'

  const handleDelete = (template: RideTemplateDto) => {
    setTemplateToDelete(template)
  }

  const confirmDelete = () => {
    if (templateToDelete) {
      deleteMutation.mutate(templateToDelete.slug)
      setTemplateToDelete(null)
    }
  }

  return (
    <TeamAdminLayout team={team} currentTab="ride-templates">
      <div className="py-6">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h2 className="text-2xl font-bold text-gray-900">{t('list.title')}</h2>
          </div>
          {canManage && (
            <Link
              to={paths.rideTemplateNew(teamSlug!)}
              className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-xs text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
            >
              <PlusIcon className="w-4 h-4 mr-2" />
              {t('create.title')}
            </Link>
          )}
        </div>

        {/* Search Input */}
        <SearchInput
          id="templates-search"
          value={search}
          onChange={(value) => {
            setSearch(value)
            resetPage()
          }}
          placeholder={t('list.search.placeholder')}
          label={t('list.search.label')}
          className="mb-6"
        />

        {/* Templates List */}
        {isLoadingTemplates ? (
          <div className="flex justify-center py-12">
            <LoadingSpinner />
          </div>
        ) : templatesData?.templates && templatesData.templates.length > 0 ? (
          <>
            <div className="space-y-4">
              {templatesData.templates.map((template) => (
                <div
                  key={template.id}
                  className="bg-white rounded-lg shadow-sm border border-gray-200 p-4"
                >
                  <div className="flex items-start justify-between">
                    <div className="flex-1 min-w-0">
                      <h3 className="text-lg font-medium text-gray-900">{template.name}</h3>
                      {template.markdown && (
                        <MarkdownDisplay
                          markdown={template.markdown}
                          preview={true}
                          maxLength={150}
                          className="mt-1 text-sm text-gray-500"
                        />
                      )}
                      <div className="mt-3 flex items-center gap-4 text-sm text-gray-500">
                        <span className="flex items-center gap-1">
                          <UserGroupIcon className="w-4 h-4" />
                          {tCommon('groups.groupCount', { count: template.groupCount })}
                        </span>
                        <span
                          className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${
                            template.visibility === 'PUBLIC'
                              ? 'bg-green-100 text-green-800'
                              : 'bg-gray-100 text-gray-800'
                          }`}
                        >
                          {t(
                            `form.visibility.${template.visibility.toLowerCase() as 'public' | 'team'}`
                          )}
                        </span>
                      </div>
                      {template.groups && template.groups.length > 0 && (
                        <div className="mt-3 flex flex-wrap gap-2">
                          {template.groups.map((group) => (
                            <span
                              key={group.id}
                              className="inline-flex items-center px-2 py-1 text-xs font-medium bg-indigo-50 text-indigo-700 rounded"
                            >
                              {group.name}
                              {group.averageSpeed && (
                                <span className="ml-1 text-indigo-400">
                                  {tCommon('speed', { speed: group.averageSpeed })}
                                </span>
                              )}
                            </span>
                          ))}
                        </div>
                      )}
                    </div>
                    {canManage && (
                      <div className="flex items-center gap-2 ml-4">
                        <Link
                          to={paths.rideTemplateEdit(teamSlug!, template.slug)}
                          className="p-2 text-gray-400 hover:text-indigo-600 rounded-lg hover:bg-gray-100"
                          title={tCommon('actions.edit')}
                        >
                          <PencilIcon className="w-5 h-5" />
                        </Link>
                        <button
                          onClick={() => handleDelete(template)}
                          className="p-2 text-gray-400 hover:text-red-600 rounded-lg hover:bg-gray-100"
                          title={tCommon('actions.delete')}
                        >
                          <TrashIcon className="w-5 h-5" />
                        </button>
                      </div>
                    )}
                  </div>
                </div>
              ))}
            </div>

            <Pagination
              currentPage={page}
              totalPages={totalPages}
              onPageChange={setPage}
              className="mt-8"
            />
          </>
        ) : (
          <div className="text-center py-12 bg-white rounded-lg shadow-sm border border-gray-200">
            <DocumentDuplicateIcon className="mx-auto h-12 w-12 text-gray-400" />
            <h3 className="mt-4 text-lg font-medium text-gray-900">
              {search ? t('list.noResults') : t('list.empty.title')}
            </h3>
            {!search && (
              <p className="mt-2 text-gray-500">
                {canManage ? t('list.empty.admin') : t('list.empty.member')}
              </p>
            )}
            {canManage && !search && (
              <Link
                to={paths.rideTemplateNew(teamSlug!)}
                className="mt-4 inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-xs text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
              >
                {t('create.title')}
              </Link>
            )}
          </div>
        )}
      </div>

      {/* Delete Confirmation Dialog */}
      <ConfirmDialog
        isOpen={!!templateToDelete}
        onClose={() => setTemplateToDelete(null)}
        onConfirm={confirmDelete}
        title={t('confirmations.deleteTitle')}
        message={t('confirmations.delete', { name: templateToDelete?.name })}
        confirmText={t('common:buttons.delete')}
        variant="danger"
        isLoading={deleteMutation.isPending}
      />
    </TeamAdminLayout>
  )
}
