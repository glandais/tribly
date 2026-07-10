import { useMemo } from 'react'
import { useLocation } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import {
  findMatchingRoute,
  buildBreadcrumbChain,
  buildRoutePath,
  getRouteById,
} from '../config/routeUtils'
import { useBreadcrumbData } from './useBreadcrumbData'
import type { BreadcrumbItemType, BreadcrumbSubItemType } from '../components/common/Breadcrumb'
import { BreadcrumbLabel } from '@/config/routes.types'
import { isSingleTeam } from '@/config/appConfig'

export interface UseBreadcrumbResult {
  items: BreadcrumbItemType[]
  showBackLink: boolean
}

/**
 * Generate breadcrumb items from route configuration
 * Single source of truth: uses route config for hierarchy, fetches entity names for dynamic labels
 */
export function useBreadcrumb(): UseBreadcrumbResult {
  const location = useLocation()
  const { t } = useTranslation()

  // Find current route and extract params
  const matchResult = useMemo(() => findMatchingRoute(location.pathname), [location.pathname])

  // Fetch entity data for dynamic labels
  const entityData = useBreadcrumbData(matchResult?.params ?? {})

  // Build breadcrumb items
  const items = useMemo<BreadcrumbItemType[]>(() => {
    if (!matchResult) {
      return []
    }

    const { route, params } = matchResult
    // On a single-team site the "Teams" crumb points at a page that redirects away — drop it.
    const singleTeam = isSingleTeam()
    const chain = buildBreadcrumbChain(route.id).filter(
      (rc) => !(singleTeam && rc.hideWhenSingleTeam)
    )

    function resolveLabel(bc: BreadcrumbLabel): string {
      if (bc.type === 'static') {
        return t(bc.i18nKey)
      } else {
        // Dynamic label from entity
        const data = entityData[bc.entity]
        return data.name ?? '...'
      }
    }

    return chain.map((routeConfig, index) => {
      const isLast = index === chain.length - 1
      const breadcrumb = routeConfig.breadcrumb!

      // Resolve label
      const label = resolveLabel(breadcrumb)

      // Build path (undefined for last item = current page)
      const path = isLast ? undefined : buildRoutePath(routeConfig, params)

      // Build subItems from subRouteIds
      let subItems: BreadcrumbSubItemType[] | undefined
      if (routeConfig.subRouteIds && routeConfig.subRouteIds.length > 0) {
        subItems = routeConfig.subRouteIds
          .map((subRouteId) => {
            const subRoute = getRouteById(subRouteId)
            if (!subRoute || !subRoute.breadcrumb) return null
            if (singleTeam && subRoute.hideWhenSingleTeam) return null
            const subLabel = resolveLabel(subRoute.breadcrumb)
            const subPath = buildRoutePath(subRoute, params)
            return { label: subLabel, path: subPath }
          })
          .filter((item) => item !== null)
      }

      return { label, path, subItems }
    })
  }, [matchResult, entityData, t])

  const showBackLink = matchResult?.route.showBackLink ?? false

  return { items, showBackLink }
}
