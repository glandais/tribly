import { useState } from 'react'
import { Link, useParams, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { ChevronLeftIcon } from '@heroicons/react/24/outline'
import { useTeam, useDeleteTeam } from '../../hooks/useTeam'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { ConfirmDialog } from '../../components/common/ConfirmDialog'
import { TeamForm } from '../../components/team/TeamForm'
import { PlaceList } from '../../components/team/PlaceList'
import { TeamDetailDto } from '../../api/api'

export function TeamSettingsPage() {
  const { t } = useTranslation('teams')
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const navigate = useNavigate()

  const { data: team, isLoading, error } = useTeam(teamSlug)
  const deleteMutation = useDeleteTeam(teamSlug || '')

  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false)

  if (isLoading) {
    return <LoadingPage message={t('settings.loading')} />
  }

  if (error || !team) {
    return (
      <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="text-center py-12">
          <h1 className="text-2xl font-bold text-gray-900 mb-2">{t('settings.error.title')}</h1>
          <p className="text-gray-600 mb-6">{t('settings.error.loadFailed')}</p>
          <Link
            to="/teams"
            className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-xs text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
          >
            {t('detail.notFound.backToTeams')}
          </Link>
        </div>
      </div>
    )
  }

  if (team.role !== 'ADMIN') {
    return (
      <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="text-center py-12">
          <h1 className="text-2xl font-bold text-gray-900 mb-2">
            {t('settings.error.accessDenied')}
          </h1>
          <p className="text-gray-600 mb-6">{t('settings.error.adminRequired')}</p>
          <Link
            to={`/teams/${teamSlug}`}
            className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-xs text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
          >
            {t('settings.backToTeam', { teamName: team.name })}
          </Link>
        </div>
      </div>
    )
  }

  const handleSuccess = (updatedTeam: TeamDetailDto) => {
    navigate(`/teams/${updatedTeam.slug}`)
  }

  const handleDelete = () => {
    deleteMutation.mutate()
  }

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <Link
          to={`/teams/${teamSlug}`}
          className="inline-flex items-center text-sm text-gray-500 hover:text-gray-700"
        >
          <ChevronLeftIcon className="w-4 h-4 mr-1" />
          {t('settings.backToTeam', { teamName: team.name })}
        </Link>
        <h1 className="mt-4 text-3xl font-bold text-gray-900">{t('settings.title')}</h1>
        <p className="mt-1 text-gray-600">{t('settings.subtitle')}</p>
      </div>

      <TeamForm
        teamSlug={teamSlug}
        initialName={team.name}
        initialMedia={team.media}
        initialVisibility={team.visibility}
        initialEnableTrips={team.enableTrips}
        onSuccess={handleSuccess}
        create={false}
      />

      {/* Places Management */}
      <div className="mt-12 pt-8 border-t border-gray-200">
        <PlaceList teamSlug={teamSlug!} canManage={true} />
      </div>

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
  )
}
