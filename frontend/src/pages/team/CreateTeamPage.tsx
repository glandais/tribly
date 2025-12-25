import { Link, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { ChevronLeftIcon } from '@heroicons/react/24/outline'
import { TeamForm } from '../../components/team/TeamForm'
import { TeamDetailDto } from '../../api/api'

export function CreateTeamPage() {
  const { t } = useTranslation('teams')
  const navigate = useNavigate()

  const handleSuccess = (team: TeamDetailDto) => {
    navigate(`/teams/${team.slug}`)
  }

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <Link
          to="/teams"
          className="inline-flex items-center text-sm text-gray-500 hover:text-gray-700"
        >
          <ChevronLeftIcon className="w-4 h-4 mr-1" />
          {t('create.backToTeams')}
        </Link>
        <h1 className="mt-4 text-3xl font-bold text-gray-900">{t('create.title')}</h1>
        <p className="mt-1 text-gray-600">{t('create.subtitle')}</p>
      </div>

      <TeamForm
        onSuccess={handleSuccess}
        submitButtonText={t('create.button')}
        submitLoadingText={t('create.creating')}
        cancelLink="/teams"
        disableSubmitWhenEmpty={true}
        namespace="create"
      />
    </div>
  )
}
