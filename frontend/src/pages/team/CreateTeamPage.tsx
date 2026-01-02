import { useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { paths } from '../../config/paths'
import { TeamForm } from '../../components/team/TeamForm'
import { TeamDetailDto } from '../../api/api'

export function CreateTeamPage() {
  const { t } = useTranslation('teams')
  const navigate = useNavigate()

  const handleSuccess = (team: TeamDetailDto) => {
    navigate(paths.team(team.slug))
  }

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900">{t('create.title')}</h1>
        <p className="mt-1 text-gray-600">{t('create.subtitle')}</p>
      </div>

      <TeamForm onSuccess={handleSuccess} create={true} />
    </div>
  )
}
