import { useMemo } from 'react'
import { useLocation } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import {
  IconNews,
  IconCalendar,
  IconRoute,
  IconUsers,
  IconTags,
  IconInfoCircle,
  IconFileText,
  IconSparkles,
} from '@tabler/icons-react'
import { paths } from '@/config/paths'
import { findMatchingRoute, getRouteById } from '@/config/routeUtils'
import { isSingleTeam } from '@/config/appConfig'
import { useAuth } from './useAuth'
import type { NavButtonItem } from '../components/common/NavButtons'
import type { TeamDetailDto } from '@/api/dto'

/** A main-navigation entry, tied to the route (routes.config.ts id) that its section starts at. */
export interface MainNavItem extends NavButtonItem {
  routeId: string
}

/**
 * Navigation items for the top-level (home) section: calendar requires auth, team browsing is
 * dropped on single-team sites. Single source of truth shared by the header's main navigation
 * (useMainNavItems) and the breadcrumb dropdown.
 */
export function useHomeNavItems(): MainNavItem[] {
  const { t } = useTranslation()
  const { isAuthenticated } = useAuth()
  const singleTeam = isSingleTeam()

  return useMemo(() => {
    const allTabs: (MainNavItem & { requiresAuth?: boolean; hideWhenSingleTeam?: boolean })[] = [
      {
        id: 'feed',
        routeId: 'home',
        path: paths.home(),
        label: t('home.tabs.feed'),
        icon: IconNews,
      },
      {
        id: 'teams',
        routeId: 'teams',
        path: paths.teams(),
        label: t('teams.title'),
        icon: IconUsers,
        // A single-team site has nothing to browse.
        hideWhenSingleTeam: true,
      },
      {
        id: 'calendar',
        routeId: 'calendar',
        path: paths.calendar(),
        label: t('calendar.title'),
        icon: IconCalendar,
        requiresAuth: true,
      },
      {
        id: 'routes',
        routeId: 'all-routes',
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
 * The site's main navigation, shown in the header (and the mobile drawer): the home section's
 * entries, then the features page. The breadcrumb dropdown keeps to useHomeNavItems — the features
 * page is no sibling of the feed in the route tree.
 */
export function useMainNavItems(): MainNavItem[] {
  const { t } = useTranslation()
  const homeItems = useHomeNavItems()

  return useMemo(
    () => [
      ...homeItems,
      {
        id: 'features',
        routeId: 'features',
        path: paths.features(),
        label: t('nav.features'),
        icon: IconSparkles,
      },
    ],
    [homeItems, t]
  )
}

/**
 * The main-navigation entry the given path belongs to: the nearest ancestor of its route (itself
 * included) that starts a section — so the routes map lights « Parcours » and a team page
 * « Équipes », as their parent chain in routes.config.ts says. Undefined off any section (profile,
 * notifications…), where no entry is current.
 */
export function activeMainNavId(pathname: string, items: MainNavItem[]): string | undefined {
  let routeId: string | null | undefined = findMatchingRoute(pathname)?.route.id
  while (routeId) {
    const id: string = routeId
    const item = items.find((candidate) => candidate.routeId === id)
    if (item) return item.id
    routeId = getRouteById(id)?.parentId
  }
  return undefined
}

/** The current entry of useMainNavItems' list, from the location. */
export function useActiveMainNavId(items: MainNavItem[]): string | undefined {
  const { pathname } = useLocation()
  return useMemo(() => activeMainNavId(pathname, items), [pathname, items])
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
