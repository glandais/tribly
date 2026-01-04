import { useState } from 'react'
import { Link, useParams, useNavigate, Navigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import i18next from 'i18next'
import { paths } from '../../config/paths'
import {
  useGetTeam,
  useDeleteTeam,
  getListTeamsQueryKey,
  getGetTeamQueryKey,
} from '@/api/endpoints/teams/teams'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { ConfirmDialog } from '../../components/common/ConfirmDialog'
import { TeamForm } from '../../components/team/TeamForm'
import { TeamAdminLayout } from '../../components/team/TeamAdminLayout'
import { TeamDetailDto } from '@/api/dto'

export function TeamSettingsPage() {
  const { t } = useTranslation('teams')
  const { t: tCommon } = useTranslation('common')
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const navigate = useNavigate()
  const queryClient = useQueryClient()

  const {
    data: team,
    isLoading,
    error,
  } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })
  const deleteMutation = useDeleteTeam()

  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false)

  if (isLoading) {
    return <LoadingPage message={tCommon('loading')} />
  }

  if (error || !team) {
    return (
      <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="text-center py-12">
          <h1 className="text-2xl font-bold text-gray-900 mb-2">{t('settings.error.title')}</h1>
          <p className="text-gray-600 mb-6">{t('settings.error.loadFailed')}</p>
          <Link
            to={paths.teams()}
            className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-xs text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
          >
            {t('detail.notFound.backToTeams')}
          </Link>
        </div>
      </div>
    )
  }

  if (team.role !== 'ADMIN') {
    return <Navigate to={paths.team(teamSlug!)} replace />
  }

  const handleSuccess = (updatedTeam: TeamDetailDto) => {
    navigate(paths.team(updatedTeam.slug))
  }

  const handleDelete = () => {
    if (!teamSlug) return
    deleteMutation.mutate(
      { slug: teamSlug },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getListTeamsQueryKey() })
          queryClient.removeQueries({ queryKey: getGetTeamQueryKey(teamSlug) })
          toast.success(i18next.t('teams:notifications.deleted'))
          navigate(paths.teams())
        },
      }
    )
  }

  return (
    <TeamAdminLayout team={team} currentTab="settings">
      <div className="py-6 max-w-2xl">
        <div className="mb-8">
          <h2 className="text-xl font-semibold text-gray-900">{t('settings.title')}</h2>
          <p className="mt-1 text-gray-600">{t('settings.subtitle')}</p>
        </div>

        <TeamForm
          teamSlug={teamSlug}
          initialValues={{ ...team, media: team.about }}
          onSuccess={handleSuccess}
          create={false}
        />

        {/* Danger Zone */}
        <div className="mt-12 pt-8 border-t border-gray-200">
          <h2 className="text-lg font-semibold text-red-600">{t('settings.dangerZone.title')}</h2>
          <p className="mt-1 text-sm text-gray-600">{t('settings.dangerZone.description')}</p>

          <button
            onClick={() => setShowDeleteConfirm(true)}
            className="mt-4 inline-flex items-center px-4 py-2 border border-red-300 rounded-md shadow-xs text-sm font-medium text-red-700 bg-white hover:bg-red-50 focus:outline-hidden focus:ring-2 focus:ring-offset-2 focus:ring-red-500"
          >
            {t('settings.dangerZone.deleteTeam')}
          </button>
        </div>

        <ConfirmDialog
          isOpen={showDeleteConfirm}
          onClose={() => setShowDeleteConfirm(false)}
          onConfirm={handleDelete}
          title={t('settings.dangerZone.title')}
          message={t('settings.dangerZone.deleteWarning', { teamName: team?.name })}
          confirmText={t('settings.dangerZone.confirmDelete')}
          variant="danger"
          isLoading={deleteMutation.isPending}
        />
      </div>
    </TeamAdminLayout>
  )
}
