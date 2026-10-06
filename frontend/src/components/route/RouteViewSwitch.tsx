import { useLocation, useNavigate } from 'react-router-dom'
import { paths } from '@/config/paths'
import { COMMON_ALIAS } from '@/hooks/filters/common'
import { routeFiltersAlias, type RouteDensity } from '@/hooks/filters/routeFilters'
import { ListViewSwitch } from '@/components/common/ListViewSwitch'

export type RouteView = RouteDensity | 'map'

const ROUTE_VIEWS = ['card', 'row', 'map'] as const satisfies readonly RouteView[]

export interface RouteViewSwitchProps {
  /** The view on screen: the resolved density on a list page, `map` on a map page. */
  current: RouteView
  /** Omit for the cross-team routes pages. */
  teamSlug?: string
  /**
   * How the list page changes its density (`?view=`, through its `useUrlFilters`). Omitted on a
   * map page, where picking « Vignettes » or « Lignes » navigates to the list instead.
   */
  onDensityChange?: (density: RouteDensity) => void
}

/**
 * Parcours' « Vignettes · Lignes · Carte » (ledger `WEB-69`). Vignettes and Lignes are one page
 * whose density lives in `?view=`; the map keeps its own path (`routesMap`, `allRoutesMap`) for
 * the links already out there, so picking it navigates — carrying the query string, so the
 * filters survive the trip both ways.
 */
export function RouteViewSwitch({ current, teamSlug, onDensityChange }: RouteViewSwitchProps) {
  const navigate = useNavigate()
  const { search } = useLocation()

  const listPath = teamSlug ? paths.routes(teamSlug) : paths.allRoutes()
  const mapPath = teamSlug ? paths.routesMap(teamSlug) : paths.allRoutesMap()

  const go = (pathname: string, view: RouteDensity | undefined) => {
    const params = new URLSearchParams(search)
    // A page number means nothing to the other view, and the map has no density.
    params.delete(COMMON_ALIAS.page)
    if (view) params.set(routeFiltersAlias.density, view)
    else params.delete(routeFiltersAlias.density)
    const query = params.toString()
    navigate({ pathname, search: query ? `?${query}` : '' })
  }

  const onChange = (view: RouteView) => {
    if (view === current) return
    if (view === 'map') go(mapPath, undefined)
    else if (onDensityChange) onDensityChange(view)
    else go(listPath, view)
  }

  return <ListViewSwitch views={ROUTE_VIEWS} value={current} onChange={onChange} />
}
