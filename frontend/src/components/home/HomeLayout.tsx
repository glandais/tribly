import { Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { Stack, Title, Text, Tabs } from '@mantine/core'
import { paths } from '@/config/paths'

export type HomeTab = 'feed' | 'routes' | 'teams' | 'calendar'

interface HomeLayoutProps {
  currentTab: HomeTab
  children: React.ReactNode
}

export function HomeLayout({ currentTab, children }: HomeLayoutProps) {
  const { t } = useTranslation()

  const tabs: { id: HomeTab; path: string; label: string }[] = [
    {
      id: 'feed',
      path: paths.home(),
      label: t('home.tabs.feed'),
    },
    {
      id: 'calendar',
      path: paths.calendar(),
      label: t('calendar.title'),
    },
    {
      id: 'routes',
      path: paths.allRoutes(),
      label: t('nav.routes'),
    },
    {
      id: 'teams',
      path: paths.teams(),
      label: t('teams.title'),
    },
  ]

  return (
    <Stack gap="lg">
      {/* Header */}
      <div>
        <Title order={1}>{t('welcome')}</Title>
        <Text c="dimmed" mt={4}>
          {t('home.subtitle')}
        </Text>
      </div>

      {/* Home Navigation */}
      <Tabs value={currentTab}>
        <Tabs.List>
          {tabs.map((tab) => (
            <Tabs.Tab
              key={tab.id}
              value={tab.id}
              renderRoot={(props) => <Link to={tab.path} {...props} />}
            >
              {tab.label}
            </Tabs.Tab>
          ))}
        </Tabs.List>
      </Tabs>

      {/* Page Content */}
      <div>{children}</div>
    </Stack>
  )
}
