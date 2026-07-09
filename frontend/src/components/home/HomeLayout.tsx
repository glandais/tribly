import { useMemo } from 'react'
import { useTranslation } from 'react-i18next'
import { Stack } from '@mantine/core'
import { IconNews, IconCalendar, IconRoute, IconUsers } from '@tabler/icons-react'
import { paths } from '@/config/paths'
import { useAuth } from '@/hooks/useAuth'
import { isSingleTeam } from '@/config/appConfig'
import { NavButtons, type NavButtonItem } from '../common/NavButtons'

export type HomeTab = 'feed' | 'routes' | 'teams' | 'calendar'

interface HomeLayoutProps {
  header?: React.ReactNode
  currentTab: HomeTab
  children: React.ReactNode
}

export function HomeLayout({ header = <></>, currentTab, children }: HomeLayoutProps) {
  const { t } = useTranslation()
  const { isAuthenticated } = useAuth()
  const singleTeam = isSingleTeam()

  const tabs = useMemo(() => {
    const allTabs: (NavButtonItem & { requiresAuth?: boolean; hideWhenSingleTeam?: boolean })[] = [
      {
        id: 'feed',
        path: paths.home(),
        label: t('home.tabs.feed'),
        icon: IconNews,
      },
      {
        id: 'teams',
        path: paths.teams(),
        label: t('teams.title'),
        icon: IconUsers,
        // A single-team site has nothing to browse.
        hideWhenSingleTeam: true,
      },
      {
        id: 'calendar',
        path: paths.calendar(),
        label: t('calendar.title'),
        icon: IconCalendar,
        requiresAuth: true,
      },
      {
        id: 'routes',
        path: paths.allRoutes(),
        label: t('nav.routes'),
        icon: IconRoute,
      },
    ]
    return allTabs.filter(
      (tab) => (!tab.requiresAuth || isAuthenticated) && !(tab.hideWhenSingleTeam && singleTeam)
    )
  }, [t, isAuthenticated, singleTeam])

  return (
    <Stack>
      {header}

      {/* Home Navigation */}
      <NavButtons items={tabs} currentId={currentTab} />

      {/* Page Content */}
      <div>{children}</div>
    </Stack>
  )
}
