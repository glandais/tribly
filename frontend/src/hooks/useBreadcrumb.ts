import { useMemo } from 'react'
import { useLocation } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { findMatchingRoute, buildBreadcrumbChain, buildRoutePath } from '../config/routeUtils'
import { useBreadcrumbData } from './useBreadcrumbData'
import type { BreadcrumbItemType } from '../components/common/Breadcrumb'

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
    const chain = buildBreadcrumbChain(route.id)

    return chain.map((routeConfig, index) => {
      const isLast = index === chain.length - 1
      const breadcrumb = routeConfig.breadcrumb!

      // Resolve label
      let label: string
      if (breadcrumb.type === 'static') {
        label = t(breadcrumb.i18nKey)
      } else {
        // Dynamic label from entity
        const data = entityData[breadcrumb.entity]
        label = data.name ?? '...'
      }

      // Build path (undefined for last item = current page)
      const path = isLast ? undefined : buildRoutePath(routeConfig, params)

      return { label, path }
    })
  }, [matchResult, entityData, t])

  const showBackLink = matchResult?.route.showBackLink ?? false

  return { items, showBackLink }
}
