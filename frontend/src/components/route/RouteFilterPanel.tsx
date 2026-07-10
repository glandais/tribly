import { useCallback } from 'react'
import { useTranslation } from 'react-i18next'
import { IconFilter, IconChevronDown, IconChevronUp, IconX } from '@tabler/icons-react'
import { Collapse, Select, Button, Text, Paper, Group, SimpleGrid, Stack } from '@mantine/core'
import { RangeInput } from '@/components/common/RangeInput'
import { Hilliness, SurfaceType, WindDirection, RouteSortBy, SortDirection } from '@/api/dto'
import type { RouteFilters } from '@/hooks/filters/routeFilters'
import { useDebouncedSearch } from '@/hooks/useDebouncedSearch'
import { SearchInput } from '../common/SearchInput'
import { useUnits } from '@/hooks/useUnits'
import { DEFAULT_ROUTE_SORT_BY, DEFAULT_ROUTE_SORT_DIR } from './routeFilterDefaults'

const NONE_VALUE = '_none'

interface RouteFilterPanelProps {
  filters: RouteFilters
  /** Replaces the whole filter set: omitted keys fall back to their default. */
  onFiltersChange: (filters: Partial<RouteFilters>) => void
  isOpen: boolean
  onOpenChange: (open: boolean) => void
  /** The map view orders nothing, so it hides the sort rather than showing an inert control. */
  showSort?: boolean
}

export function RouteFilterPanel({
  filters,
  onFiltersChange,
  isOpen,
  onOpenChange,
  showSort = true,
}: RouteFilterPanelProps) {
  const { t } = useTranslation()
  const { config, distanceUnit, elevationUnit } = useUnits()

  const updateFilter = <K extends keyof RouteFilters>(key: K, value: RouteFilters[K]) => {
    onFiltersChange({ ...filters, [key]: value, page: 0 })
  }

  const commitSearch = useCallback(
    (value: string) => onFiltersChange({ ...filters, search: value || undefined, page: 0 }),
    [filters, onFiltersChange]
  )
  const [search, setSearch] = useDebouncedSearch(filters.search ?? '', commitSearch)

  // Resets each clearable key by name rather than omitting it, so filters this panel does not own
  // — the search, the membership filter on the cross-team page, and the sort when it is hidden —
  // survive.
  const clearFilters = () => {
    onFiltersChange({
      ...filters,
      minDistance: undefined,
      maxDistance: undefined,
      minElevationGain: undefined,
      maxElevationGain: undefined,
      hilliness: undefined,
      surfaceType: undefined,
      windDirection: undefined,
      ...(showSort ? { sortBy: DEFAULT_ROUTE_SORT_BY, sortDir: DEFAULT_ROUTE_SORT_DIR } : {}),
      page: 0,
    })
  }

  const hasNonDefaultSort =
    showSort &&
    ((filters.sortBy !== undefined && filters.sortBy !== DEFAULT_ROUTE_SORT_BY) ||
      (filters.sortDir !== undefined && filters.sortDir !== DEFAULT_ROUTE_SORT_DIR))

  const hasActiveFilters =
    filters.minDistance !== undefined ||
    filters.maxDistance !== undefined ||
    filters.minElevationGain !== undefined ||
    filters.maxElevationGain !== undefined ||
    filters.hilliness !== undefined ||
    filters.surfaceType !== undefined ||
    filters.windDirection !== undefined ||
    hasNonDefaultSort

  return (
    <Stack>
      <Group gap="sm">
        <Button
          variant="default"
          size="sm"
          leftSection={<IconFilter size={16} />}
          rightSection={isOpen ? <IconChevronUp size={16} /> : <IconChevronDown size={16} />}
          onClick={() => onOpenChange(!isOpen)}
        >
          {isOpen ? t('routes.list.filters.hide') : t('routes.list.filters.show')}
        </Button>
        {hasActiveFilters && (
          <Button
            variant="subtle"
            size="sm"
            leftSection={<IconX size={16} />}
            onClick={clearFilters}
          >
            {t('routes.list.filters.clear')}
          </Button>
        )}
      </Group>

      <Collapse expanded={isOpen}>
        <Paper withBorder p="md" mb="md">
          <SearchInput
            id="routes-search"
            value={search}
            onChange={setSearch}
            placeholder={t('routes.list.search.placeholder')}
            label={t('routes.list.search.label')}
            fullWidth
            mb="md"
          />

          <SimpleGrid cols={{ base: 1, sm: 2, lg: 3, xl: 4 }} spacing="md">
            {/* Distance Range */}
            <RangeInput
              label={t('routes.detail.stats.distance')}
              minValue={filters.minDistance}
              maxValue={filters.maxDistance}
              onMinChange={(v) => updateFilter('minDistance', v)}
              onMaxChange={(v) => updateFilter('maxDistance', v)}
              minPlaceholder={t('routes.list.filters.distance.min')}
              maxPlaceholder={t('routes.list.filters.distance.max')}
              unit={distanceUnit()}
              step={5}
              displayMultiplier={config.distanceMultiplier}
            />

            {/* Elevation Gain Range */}
            <RangeInput
              label={t('routes.list.filters.elevationGain.label')}
              minValue={filters.minElevationGain}
              maxValue={filters.maxElevationGain}
              onMinChange={(v) => updateFilter('minElevationGain', v)}
              onMaxChange={(v) => updateFilter('maxElevationGain', v)}
              minPlaceholder={t('routes.list.filters.elevationGain.min')}
              maxPlaceholder={t('routes.list.filters.elevationGain.max')}
              unit={elevationUnit()}
              step={50}
              displayMultiplier={config.elevationMultiplier}
            />

            {/* Hilliness Preset */}
            <Stack gap={4}>
              <Text size="sm" fw={500}>
                {t('routes.list.filters.hilliness.label')}
              </Text>
              <Select
                value={filters.hilliness ?? NONE_VALUE}
                onChange={(value) =>
                  updateFilter('hilliness', value === NONE_VALUE ? undefined : (value as Hilliness))
                }
                placeholder={t('routes.list.filters.hilliness.placeholder')}
                data={[
                  { value: NONE_VALUE, label: t('routes.list.filters.hilliness.placeholder') },
                  ...Object.values(Hilliness).map((type) => ({
                    value: type,
                    label: t(
                      `routes.list.filters.hilliness.${type satisfies 'FLAT' | 'HILLY' | 'MOUNTAINOUS'}`
                    ),
                  })),
                ]}
              />
            </Stack>

            {/* Surface Type */}
            <Stack gap={4}>
              <Text size="sm" fw={500}>
                {t('routes.list.filters.surfaceType.label')}
              </Text>
              <Select
                value={filters.surfaceType ?? NONE_VALUE}
                onChange={(value) =>
                  updateFilter(
                    'surfaceType',
                    value === NONE_VALUE ? undefined : (value as SurfaceType)
                  )
                }
                placeholder={t('routes.list.filters.surfaceType.placeholder')}
                data={[
                  { value: NONE_VALUE, label: t('routes.list.filters.surfaceType.placeholder') },
                  ...Object.values(SurfaceType).map((type) => ({
                    value: type,
                    label: t(
                      `routes.surfaceType.${type satisfies 'ROAD' | 'GRAVEL' | 'MTB' | 'MIXED'}`
                    ),
                  })),
                ]}
              />
            </Stack>

            {/* Wind Direction */}
            <Stack gap={4}>
              <Text size="sm" fw={500}>
                {t('routes.list.filters.windDirection.label')}
              </Text>
              <Select
                value={filters.windDirection ?? NONE_VALUE}
                onChange={(value) =>
                  updateFilter(
                    'windDirection',
                    value === NONE_VALUE ? undefined : (value as WindDirection)
                  )
                }
                placeholder={t('routes.list.filters.windDirection.placeholder')}
                data={[
                  { value: NONE_VALUE, label: t('routes.list.filters.windDirection.placeholder') },
                  ...Object.values(WindDirection).map((dir) => ({
                    value: dir,
                    label: t(
                      `routes.list.filters.windDirection.${dir satisfies 'NORTH' | 'NORTH_EAST' | 'EAST' | 'SOUTH_EAST' | 'SOUTH' | 'SOUTH_WEST' | 'WEST' | 'NORTH_WEST'}`
                    ),
                  })),
                ]}
              />
            </Stack>

            {/* Sort Options */}
            {showSort && (
              <Stack gap={4}>
                <Text size="sm" fw={500}>
                  {t('routes.list.filters.sort.label')}
                </Text>
                <Group gap="xs">
                  <Select
                    value={filters.sortBy ?? RouteSortBy.DATE_TIME}
                    onChange={(value) => updateFilter('sortBy', value as RouteSortBy)}
                    style={{ flex: 1 }}
                    data={Object.values(RouteSortBy).map((sort) => ({
                      value: sort,
                      label: t(
                        `routes.list.filters.sort.${sort satisfies 'DISTANCE' | 'ELEVATION_GAIN' | 'HILLINESS' | 'DATE_TIME'}`
                      ),
                    }))}
                  />
                  <Button
                    variant="default"
                    size="sm"
                    onClick={() =>
                      updateFilter(
                        'sortDir',
                        filters.sortDir === SortDirection.ASC
                          ? SortDirection.DESC
                          : SortDirection.ASC
                      )
                    }
                    title={t(
                      `routes.list.filters.sort.${filters.sortDir === SortDirection.ASC ? 'ASC' : 'DESC'}`
                    )}
                  >
                    {filters.sortDir === SortDirection.ASC ? (
                      <IconChevronUp size={16} />
                    ) : (
                      <IconChevronDown size={16} />
                    )}
                  </Button>
                </Group>
              </Stack>
            )}
          </SimpleGrid>
        </Paper>
      </Collapse>
    </Stack>
  )
}
