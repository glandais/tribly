import { useState } from 'react'
import { Link, useParams, Navigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import i18next from 'i18next'
import { paths } from '../../config/paths'
import {
  PlusIcon,
  DocumentTextIcon,
  PencilIcon,
  TrashIcon,
  Bars3Icon,
} from '@heroicons/react/24/outline'
import { useGetTeam, getGetTeamQueryKey } from '@/api/endpoints/teams/teams'
import {
  useListPages,
  useDeletePage,
  useReorderPages,
  getListPagesQueryKey,
} from '@/api/endpoints/team-pages/team-pages'
import { LoadingPage, LoadingSpinner } from '../../components/common/LoadingSpinner'
import { TeamAdminLayout } from '../../components/team/TeamAdminLayout'
import { ConfirmDialog } from '../../components/common/ConfirmDialog'
import { VisibilityBadge } from '../../components/common/card/VisibilityBadge'
import type { TeamPageSummaryDto } from '@/api/dto'

const MAX_ADDITIONAL_PAGES = 3

export function TeamPagesAdminPage() {
  const { t } = useTranslation()
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const queryClient = useQueryClient()
  const [pageToDelete, setPageToDelete] = useState<TeamPageSummaryDto | null>(null)
  const [draggedItem, setDraggedItem] = useState<TeamPageSummaryDto | null>(null)

  const { data: team, isLoading: isLoadingTeam } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })
  const { data: pages, isLoading: isLoadingPages } = useListPages(teamSlug!, {
    query: { enabled: !!teamSlug },
  })
  const deleteMutation = useDeletePage()
  const reorderMutation = useReorderPages()

  if (isLoadingTeam) {
    return <LoadingPage message={t('loading')} />
  }

  if (!team) {
    return <Navigate to={paths.teams()} replace />
  }

  const isAdmin = team.role === 'ADMIN'
  if (!isAdmin) {
    return <Navigate to={paths.team(team.slug)} replace />
  }

  const canAddMore = (pages?.length ?? 0) < MAX_ADDITIONAL_PAGES

  const handleDelete = (page: TeamPageSummaryDto) => {
    setPageToDelete(page)
  }

  const confirmDelete = () => {
    if (pageToDelete && teamSlug) {
      deleteMutation.mutate(
        { slug: teamSlug, pageSlug: pageToDelete.slug },
        {
          onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: getListPagesQueryKey(teamSlug) })
            queryClient.invalidateQueries({ queryKey: getGetTeamQueryKey(teamSlug) })
            toast.success(i18next.t('teams.pages.notifications.deleted'))
            setPageToDelete(null)
          },
        }
      )
    }
  }

  const handleDragStart = (e: React.DragEvent, page: TeamPageSummaryDto) => {
    setDraggedItem(page)
    e.dataTransfer.effectAllowed = 'move'
  }

  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault()
    e.dataTransfer.dropEffect = 'move'
  }

  const handleDrop = (e: React.DragEvent, targetPage: TeamPageSummaryDto) => {
    e.preventDefault()
    if (!draggedItem || !pages || draggedItem.id === targetPage.id || !teamSlug) {
      setDraggedItem(null)
      return
    }

    const currentOrder = pages.map((p) => p.id)
    const draggedIndex = currentOrder.indexOf(draggedItem.id)
    const targetIndex = currentOrder.indexOf(targetPage.id)

    // Reorder the array
    currentOrder.splice(draggedIndex, 1)
    currentOrder.splice(targetIndex, 0, draggedItem.id)

    reorderMutation.mutate(
      { slug: teamSlug, data: { pageIds: currentOrder } },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getListPagesQueryKey(teamSlug) })
          queryClient.invalidateQueries({ queryKey: getGetTeamQueryKey(teamSlug) })
          toast.success(i18next.t('teams.pages.notifications.reordered'))
        },
      }
    )
    setDraggedItem(null)
  }

  const handleDragEnd = () => {
    setDraggedItem(null)
  }

  return (
    <TeamAdminLayout team={team} currentTab="pages">
      <div className="py-6">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h2 className="text-2xl font-bold text-gray-900">{t('teams.pages.title')}</h2>
            <p className="mt-1 text-sm text-gray-500">
              {t('teams.pages.subtitle', { count: pages?.length ?? 0, max: MAX_ADDITIONAL_PAGES })}
            </p>
          </div>
          {canAddMore && (
            <Link
              to={paths.teamAdminPageNew(teamSlug!)}
              className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-xs text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
            >
              <PlusIcon className="w-4 h-4 mr-2" />
              {t('teams.pages.add')}
            </Link>
          )}
        </div>

        {/* Pages List */}
        {isLoadingPages ? (
          <div className="flex justify-center py-12">
            <LoadingSpinner />
          </div>
        ) : pages && pages.length > 0 ? (
          <div className="space-y-2">
            {pages.map((page) => (
              <div
                key={page.id}
                draggable
                onDragStart={(e) => handleDragStart(e, page)}
                onDragOver={handleDragOver}
                onDrop={(e) => handleDrop(e, page)}
                onDragEnd={handleDragEnd}
                className={`bg-white rounded-lg shadow-sm border border-gray-200 p-4 cursor-move transition-opacity ${
                  draggedItem?.id === page.id ? 'opacity-50' : ''
                }`}
              >
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-3 flex-1 min-w-0">
                    <Bars3Icon className="w-5 h-5 text-gray-400 flex-shrink-0" />
                    <div className="flex items-center gap-2 min-w-0">
                      <h3 className="text-lg font-medium text-gray-900 truncate">{page.title}</h3>
                      <VisibilityBadge visibility={page.visibility} />
                    </div>
                  </div>
                  <div className="flex items-center gap-2 ml-4">
                    <Link
                      to={paths.teamAdminPageEdit(teamSlug!, page.slug)}
                      className="p-2 text-gray-400 hover:text-indigo-600 rounded-lg hover:bg-gray-100"
                      title={t('teams.actions.edit')}
                    >
                      <PencilIcon className="w-5 h-5" />
                    </Link>
                    <button
                      onClick={() => handleDelete(page)}
                      className="p-2 text-gray-400 hover:text-red-600 rounded-lg hover:bg-gray-100"
                      title={t('teams.buttons.delete')}
                    >
                      <TrashIcon className="w-5 h-5" />
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="text-center py-12 bg-white rounded-lg shadow-sm border border-gray-200">
            <DocumentTextIcon className="mx-auto h-12 w-12 text-gray-400" />
            <h3 className="mt-4 text-lg font-medium text-gray-900">
              {t('teams.pages.empty.title')}
            </h3>
            <p className="mt-2 text-gray-500">{t('teams.pages.empty.description')}</p>
            <Link
              to={paths.teamAdminPageNew(teamSlug!)}
              className="mt-4 inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-xs text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
            >
              <PlusIcon className="w-4 h-4 mr-2" />
              {t('teams.pages.add')}
            </Link>
          </div>
        )}
      </div>

      {/* Delete Confirmation Dialog */}
      <ConfirmDialog
        isOpen={!!pageToDelete}
        onClose={() => setPageToDelete(null)}
        onConfirm={confirmDelete}
        title={t('teams.pages.confirmations.deleteTitle')}
        message={t('teams.pages.confirmations.delete', { name: pageToDelete?.title })}
        confirmText={t('teams.buttons.delete')}
        variant="danger"
        isLoading={deleteMutation.isPending}
      />
    </TeamAdminLayout>
  )
}
