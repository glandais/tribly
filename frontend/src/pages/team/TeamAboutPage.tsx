import { Navigate, useParams } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { IconUsers, IconCalendar } from '@tabler/icons-react'
import { Box, Group, Paper, SimpleGrid, Stack, Text, Title } from '@mantine/core'
import { paths } from '../../config/paths'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { TeamLayout } from '../../components/team/TeamLayout'
import { MediaDisplay } from '../../components/common/MediaDisplay'
import { useCanonicalPath } from '../../hooks/useCanonicalPath'

export function TeamAboutPage() {
  const { t, i18n } = useTranslation()
  const { teamSlug } = useParams<{ teamSlug: string }>()

  const { data: team, isLoading } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })

  useCanonicalPath(team ? paths.teamAbout(team.slug) : undefined)

  if (isLoading) {
    return <LoadingPage message={t('loading')} />
  }

  if (!team) {
    return <Navigate to={paths.teams()} replace />
  }

  return (
    <TeamLayout team={team} currentTab="about">
      <Box py="md">
        <Paper p="lg" withBorder>
          <Title order={2} mb="md">
            {t('teams.detail.about.title')}
          </Title>

          {/* Description */}
          <Box mb="lg">
            <MediaDisplay media={team.about} />
            {!team.about?.markdown && (
              <Text c="dimmed" fs="italic">
                {t('teams.detail.about.noDescription')}
              </Text>
            )}
          </Box>

          {/* Stats */}
          <Box pt="md" style={{ borderTop: '1px solid var(--mantine-color-gray-3)' }}>
            <SimpleGrid cols={{ base: 1, sm: 2 }} spacing="md">
              <Group gap="sm">
                <Box style={{ flexShrink: 0 }}>
                  <IconUsers size={20} color="var(--mantine-color-gray-5)" />
                </Box>
                <Stack gap={0}>
                  <Text size="sm" c="dimmed">
                    {t('teams.detail.about.members')}
                  </Text>
                  <Text size="lg" fw={500}>
                    {t('memberCount', { count: team.memberCount })}
                  </Text>
                </Stack>
              </Group>

              {team.createdAt && (
                <Group gap="sm">
                  <Box style={{ flexShrink: 0 }}>
                    <IconCalendar size={20} color="var(--mantine-color-gray-5)" />
                  </Box>
                  <Stack gap={0}>
                    <Text size="sm" c="dimmed">
                      {t('teams.detail.about.created')}
                    </Text>
                    <Text size="lg" fw={500}>
                      {new Date(team.createdAt).toLocaleDateString(i18n.language, {
                        day: 'numeric',
                        month: 'long',
                        year: 'numeric',
                      })}
                    </Text>
                  </Stack>
                </Group>
              )}
            </SimpleGrid>
          </Box>
        </Paper>
      </Box>
    </TeamLayout>
  )
}
