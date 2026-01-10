import { Link, Navigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { Container, Group, Title, Tabs, Anchor, Box } from '@mantine/core'
import { IconArrowLeft } from '@tabler/icons-react'
import type { TeamDetailDto } from '@/api/dto'
import { paths } from '@/config/paths'

export type AdminTab = 'ride-templates' | 'places' | 'pages' | 'members' | 'settings'

interface TeamAdminLayoutProps {
  team: TeamDetailDto
  currentTab: AdminTab
  children: React.ReactNode
}

export function TeamAdminLayout({ team, currentTab, children }: TeamAdminLayoutProps) {
  const { t } = useTranslation()

  const isAdmin = team.role === 'ADMIN'
  const isOrganizer = team.role === 'ADMIN' || team.role === 'ORGANIZER'

  // Only ADMIN or ORGANIZER can access admin section
  if (!isOrganizer) {
    return <Navigate to={paths.team(team.slug)} replace />
  }

  const tabs: { id: AdminTab; path: string; label: string; adminOnly?: boolean }[] = [
    {
      id: 'ride-templates',
      path: paths.rideTemplates(team.slug),
      label: t('teams.admin.tabs.rideTemplates'),
    },
    {
      id: 'places',
      path: paths.teamAdminPlaces(team.slug),
      label: t('places.title'),
    },
    {
      id: 'pages',
      path: paths.teamAdminPages(team.slug),
      label: t('teams.admin.tabs.pages'),
      adminOnly: true,
    },
    {
      id: 'members',
      path: paths.teamMembers(team.slug),
      label: t('teams.admin.tabs.members'),
      adminOnly: true,
    },
    {
      id: 'settings',
      path: paths.teamSettings(team.slug),
      label: t('teams.admin.tabs.settings'),
      adminOnly: true,
    },
  ]

  // Filter tabs based on role
  const visibleTabs = tabs.filter((tab) => !tab.adminOnly || isAdmin)

  return (
    <Container size="xl" py="xl">
      {/* Header with back link */}
      <Box mb="xl">
        <Anchor component={Link} to={paths.team(team.slug)} c="dimmed" size="sm">
          <Group gap={4}>
            <IconArrowLeft size={16} />
            {t('teams.admin.backToTeam', { teamName: team.name })}
          </Group>
        </Anchor>
        <Title order={2} mt="xs">
          {t('teams.admin.title')}
        </Title>
      </Box>

      {/* Admin Navigation */}
      <Tabs value={currentTab} mb="xl">
        <Tabs.List>
          {visibleTabs.map((tab) => (
            <Tabs.Tab
              key={tab.id}
              value={tab.id}
              renderRoot={(props) => <Link {...props} to={tab.path} />}
            >
              {tab.label}
            </Tabs.Tab>
          ))}
        </Tabs.List>
      </Tabs>

      {/* Page Content */}
      <Box>{children}</Box>
    </Container>
  )
}
