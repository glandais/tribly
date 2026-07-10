import { useMemo } from 'react'
import { useTranslation } from 'react-i18next'
import {
  IconNews,
  IconCalendar,
  IconRoute,
  IconUsers,
  IconTags,
  IconInfoCircle,
  IconFileText,
} from '@tabler/icons-react'
import { paths } from '@/config/paths'
import { isSingleTeam } from '@/config/appConfig'
import { useAuth } from './useAuth'
import type { NavButtonItem } from '../components/common/NavButtons'
import type { TeamDetailDto } from '@/api/dto'

/**
 * Navigation items for the top-level (home) section, gated exactly like the tab bar:
 * calendar requires auth, team browsing is dropped on single-team sites.
 * Single source of truth shared by HomeLayout and the breadcrumb dropdown.
 */
export function useHomeNavItems(): NavButtonItem[] {
  const { t } = useTranslation()
  const { isAuthenticated } = useAuth()
  const singleTeam = isSingleTeam()

  return useMemo(() => {
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
}

/**
 * Navigation items for a team section, gated exactly like the tab bar: calendar/ads require
 * membership and the relevant feature flag, routes require the feature flag, plus each visible
 * dynamic team page. Single source of truth shared by TeamLayout and the breadcrumb dropdown.
 * Returns [] until the team is loaded.
 */
export function useTeamNavItems(team: TeamDetailDto | undefined): NavButtonItem[] {
  const { t } = useTranslation()

  return useMemo(() => {
    if (!team) return []

    const isMember = !!team.role
    const baseTabs: NavButtonItem[] = [
      {
        id: 'publications',
        path: paths.team(team.slug),
        label: t('teams.publications.list.title'),
        icon: IconNews,
      },
      ...(isMember && (team.enableRides || team.enableTrips)
        ? [
            {
              id: 'calendar',
              path: paths.teamCalendar(team.slug),
              label: t('teams.detail.tabs.calendar'),
              icon: IconCalendar,
            },
          ]
        : []),
      ...(team.enableRoutes
        ? [
            {
              id: 'routes',
              path: paths.routes(team.slug),
              label: t('teams.detail.tabs.routes'),
              icon: IconRoute,
            },
          ]
        : []),
      ...(isMember && team.enableAds
        ? [
            {
              id: 'ads',
              path: paths.ads(team.slug),
              label: t('ads.title'),
              icon: IconTags,
            },
          ]
        : []),
      {
        id: 'about',
        path: paths.teamAbout(team.slug),
        label: t('teams.detail.tabs.about'),
        icon: IconInfoCircle,
      },
    ]

    // Add dynamic pages - filter by visibility (PUBLIC pages or member can see TEAM pages)
    const visiblePages = (team.pages ?? []).filter((page) => page.visibility !== 'TEAM' || isMember)

    const pageTabs: NavButtonItem[] = visiblePages.map((page) => ({
      id: page.slug,
      path: paths.teamPage(team.slug, page.slug),
      label: page.title,
      icon: IconFileText,
    }))

    return [...baseTabs, ...pageTabs]
  }, [team, t])
}
