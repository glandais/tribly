import { useMemo } from 'react'
import { useTranslation } from 'react-i18next'
import { useQueries } from '@tanstack/react-query'
import { Button, Divider, Paper, Stack, Text } from '@mantine/core'
import { IconSearchOff } from '@tabler/icons-react'
import {
  getCountAllRoutesQueryOptions,
  getCountRoutesQueryOptions,
  getListAllRoutesQueryOptions,
  getListRoutesQueryOptions,
} from '@/api/endpoints/routes/routes'
import { EmptyState } from '../common/EmptyState'
import { RouteRow } from './RouteRow'
import type { RouteFilters } from '@/hooks/filters/routeFilters'

/**
 * The filter keys a dead-end may propose lifting. Range bounds are lifted in pairs:
 * dropping only `minDistance` leaves a half-open range that is not what the user set.
 */
type LiftableKey = 'distance' | 'elevationGain' | 'hilliness' | 'surfaceType' | 'windDirection'

const LIFTABLE: { key: LiftableKey | 'search'; clears: (keyof RouteFilters)[] }[] = [
  { key: 'search', clears: ['search'] },
  { key: 'distance', clears: ['minDistance', 'maxDistance'] },
  { key: 'elevationGain', clears: ['minElevationGain', 'maxElevationGain'] },
  { key: 'hilliness', clears: ['hilliness'] },
  { key: 'surfaceType', clears: ['surfaceType'] },
  { key: 'windDirection', clears: ['windDirection'] },
]

interface RouteDeadEndProps {
  filters: RouteFilters
  /** Team list when set, cross-team list when not. */
  teamSlug?: string
  onSetFilters: (patch: Partial<RouteFilters>) => void
  onClearFilters: () => void
}

/**
 * The "cul-de-sac": a search plus filters that return nothing.
 *
 * Rather than a bare "no results", it names the term and the filters at fault, works out
 * which single filter brings back the most routes if lifted, and previews three of them.
 */
export function RouteDeadEnd({
  filters,
  teamSlug,
  onSetFilters,
  onClearFilters,
}: RouteDeadEndProps) {
  const { t } = useTranslation()

  const active = useMemo(
    () => LIFTABLE.filter((entry) => entry.clears.some((k) => filters[k] !== undefined)),
    [filters]
  )

  const cleared = (entry: (typeof LIFTABLE)[number]) => {
    const next: Record<string, unknown> = { ...filters, page: 0 }
    entry.clears.forEach((k) => {
      next[k] = undefined
    })
    delete next.density
    return next
  }

  // One `useQueries` rather than a hook per filter: the number of active filters changes
  // between renders, and a variable number of hook calls breaks the rules of hooks.
  const counts = useQueries({
    queries: active.map((entry) => {
      const params = cleared(entry)
      return teamSlug
        ? getCountRoutesQueryOptions(teamSlug, params)
        : getCountAllRoutesQueryOptions(params)
    }),
  })

  // Keyed on the totals rather than on `counts`, which `useQueries` rebuilds each render.
  const countsSignature = counts.map((c) => c.data?.total).join(',')

  // The single lift that brings back the most routes — nothing if none brings back any.
  const best = useMemo(() => {
    let found: { entry: (typeof LIFTABLE)[number]; total: number } | null = null
    active.forEach((entry, i) => {
      const total = counts[i]?.data?.total ?? 0
      if (total > 0 && (!found || total > found.total)) found = { entry, total }
    })
    return found as { entry: (typeof LIFTABLE)[number]; total: number } | null
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [active, countsSignature])

  // `.map` over a 0-or-1 array rather than a conditional literal: a conditional would infer
  // a tuple type and `useQueries` would reject the one-element branch.
  const previewQueries = useQueries({
    queries: (best ? [best] : []).map((b) => {
      const params = { ...cleared(b.entry), size: 3 }
      return teamSlug
        ? getListRoutesQueryOptions(teamSlug, params)
        : getListAllRoutesQueryOptions(params)
    }),
  })
  const preview = previewQueries[0]?.data?.routes ?? []

  const activeLabels = active
    .filter((entry) => entry.key !== 'search')
    .map((entry) =>
      t(`routes.list.filters.${entry.key as LiftableKey satisfies LiftableKey}.label`)
    )

  const description = filters.search
    ? t('routes.deadEnd.descriptionWithSearch', {
        search: filters.search,
        filters: activeLabels.join(t('routes.deadEnd.filterSeparator')),
        count: activeLabels.length,
      })
    : t('routes.deadEnd.description', {
        filters: activeLabels.join(t('routes.deadEnd.filterSeparator')),
      })

  const bestLabel = best
    ? best.entry.key === 'search'
      ? t('routes.deadEnd.searchTerm')
      : t(`routes.list.filters.${best.entry.key satisfies LiftableKey}.label`)
    : ''

  return (
    <Paper withBorder radius="md">
      <EmptyState
        variant="filtered"
        icon={<IconSearchOff size={48} />}
        title={t('routes.deadEnd.title')}
        description={description}
        actions={
          <>
            {best && (
              <Button
                variant="outline"
                size="sm"
                onClick={() =>
                  onSetFilters(
                    Object.fromEntries([
                      ...best!.entry.clears.map((k) => [k, undefined]),
                      ['page', 0],
                    ])
                  )
                }
              >
                {t('routes.deadEnd.removeFilter', { filter: bestLabel })}
              </Button>
            )}
            <Button variant="outline" size="sm" onClick={onClearFilters}>
              {t('routes.deadEnd.reset')}
            </Button>
          </>
        }
      />

      {best && preview.length > 0 && (
        <>
          <Divider />
          <Stack gap="xs" p="md">
            <Text size="xs" fw={600}>
              {t('routes.deadEnd.previewHeading', { filter: bestLabel, count: best.total })}
            </Text>
            {preview.map((route) => (
              <RouteRow key={route.id} route={route} compact />
            ))}
          </Stack>
        </>
      )}
    </Paper>
  )
}
