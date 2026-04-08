import { useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { paths } from '../../config/paths'
import { Box, Stack, Text, Title } from '@mantine/core'
import { TeamForm } from '../../components/team/TeamForm'
import { TeamDetailDto, Visibility } from '@/api/dto'
import { defaultMedia } from '@/lib/apiUtils'
import { getAppConfig } from '../../config/appConfig'
import { useListTeams } from '@/api/endpoints/teams/teams'

export function CreateTeamPage() {
  const { t } = useTranslation()
  const navigate = useNavigate()
  const config = getAppConfig()
  const { data: teamsData } = useListTeams({ page: 0, size: 1 })

  useEffect(() => {
    if (config?.singleTeam && teamsData && teamsData.total > 0) {
      navigate(paths.teams())
    }
  }, [config, teamsData, navigate])

  const handleSuccess = (team: TeamDetailDto) => {
    navigate(paths.team(team.slug))
  }

  const initialValues = {
    name: '',
    media: defaultMedia(),
    visibility: Visibility.TEAM,
    enableTrips: true,
    enableAds: true,
    enablePosts: true,
    enableRides: true,
    enableRoutes: true,
  }

  return (
    <Box maw={672} mx="auto" px={{ base: 'md', sm: 'lg' }} py="xl">
      <Stack gap="xs">
        <Title order={1}>{t('teams.create.title')}</Title>
        <Text c="dimmed">{t('teams.create.subtitle')}</Text>
      </Stack>

      <TeamForm onSuccess={handleSuccess} create={true} initialValues={initialValues} />
    </Box>
  )
}
