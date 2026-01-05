import { useTranslation } from 'react-i18next'
import { FunnelIcon, ChevronDownIcon, ChevronUpIcon, XMarkIcon } from '@heroicons/react/24/outline'
import { Collapsible, CollapsibleContent, CollapsibleTrigger } from '@/components/ui/collapsible'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Button } from '@/components/ui/button'
import { Label } from '@/components/ui/label'
import { RangeInput } from '@/components/common/RangeInput'
import { Hilliness, SurfaceType, WindDirection, RouteSortBy, SortDirection } from '@/api/dto'
import type { ListRoutesParams } from '@/api/dto'
import { SearchInput } from '../common/SearchInput'

const NONE_VALUE = '_none'

interface RouteFilterPanelProps {
  filters: ListRoutesParams
  onFiltersChange: (filters: ListRoutesParams) => void
  isOpen: boolean
  onOpenChange: (open: boolean) => void
}

export function RouteFilterPanel({
  filters,
  onFiltersChange,
  isOpen,
  onOpenChange,
}: RouteFilterPanelProps) {
  const { t } = useTranslation('routes')

  const updateFilter = <K extends keyof ListRoutesParams>(key: K, value: ListRoutesParams[K]) => {
    onFiltersChange({ ...filters, [key]: value, page: 0 })
  }

  const clearFilters = () => {
    onFiltersChange({
      search: filters.search,
      page: 0,
      size: filters.size,
    })
  }

  const hasActiveFilters =
    filters.minDistance !== undefined ||
    filters.maxDistance !== undefined ||
    filters.minElevationGain !== undefined ||
    filters.maxElevationGain !== undefined ||
    filters.hilliness !== undefined ||
    filters.surfaceType !== undefined ||
    filters.windDirection !== undefined

  return (
    <Collapsible open={isOpen} onOpenChange={onOpenChange}>
      <div className="flex items-center gap-2 mb-4">
        <CollapsibleTrigger asChild>
          <Button variant="outline" size="sm">
            <FunnelIcon className="w-4 h-4 mr-2" />
            {isOpen ? t('list.filters.hide') : t('list.filters.show')}
            {isOpen ? (
              <ChevronUpIcon className="w-4 h-4 ml-2" />
            ) : (
              <ChevronDownIcon className="w-4 h-4 ml-2" />
            )}
          </Button>
        </CollapsibleTrigger>
        {hasActiveFilters && (
          <Button variant="ghost" size="sm" onClick={clearFilters}>
            <XMarkIcon className="w-4 h-4 mr-1" />
            {t('list.filters.clear')}
          </Button>
        )}
      </div>

      <CollapsibleContent>
        <div className="bg-gray-50 rounded-lg p-4 mb-6 border border-gray-200">
          {/* Search Input */}
          <SearchInput
            id="routes-search"
            value={filters.search ?? ''}
            onChange={(value) => updateFilter('search', value || undefined)}
            placeholder={t('list.search.placeholder')}
            label={t('list.search.label')}
            className="mb-4"
          />

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
            {/* Distance Range */}
            <RangeInput
              label={t('detail.stats.distance')}
              minValue={filters.minDistance}
              maxValue={filters.maxDistance}
              onMinChange={(v) => updateFilter('minDistance', v)}
              onMaxChange={(v) => updateFilter('maxDistance', v)}
              minPlaceholder={t('list.filters.distance.min')}
              maxPlaceholder={t('list.filters.distance.max')}
              unit={t('list.filters.distance.unit')}
              step={5}
              displayMultiplier={0.001} // meters to km
            />

            {/* Elevation Gain Range */}
            <RangeInput
              label={t('list.filters.elevationGain.label')}
              minValue={filters.minElevationGain}
              maxValue={filters.maxElevationGain}
              onMinChange={(v) => updateFilter('minElevationGain', v)}
              onMaxChange={(v) => updateFilter('maxElevationGain', v)}
              minPlaceholder={t('list.filters.elevationGain.min')}
              maxPlaceholder={t('list.filters.elevationGain.max')}
              unit={t('list.filters.elevationGain.unit')}
              step={50}
            />

            {/* Hilliness Preset */}
            <div>
              <Label className="text-sm font-medium text-gray-700 mb-2 block">
                {t('list.filters.hilliness.label')}
              </Label>
              <Select
                value={filters.hilliness ?? NONE_VALUE}
                onValueChange={(value) =>
                  updateFilter('hilliness', value === NONE_VALUE ? undefined : (value as Hilliness))
                }
              >
                <SelectTrigger className="w-full">
                  <SelectValue placeholder={t('list.filters.hilliness.placeholder')} />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value={NONE_VALUE}>
                    {t('list.filters.hilliness.placeholder')}
                  </SelectItem>
                  {Object.values(Hilliness).map((type) => (
                    <SelectItem key={type} value={type}>
                      {t(
                        `list.filters.hilliness.${type satisfies 'FLAT' | 'HILLY' | 'MOUNTAINOUS'}`
                      )}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            {/* Surface Type */}
            <div>
              <Label className="text-sm font-medium text-gray-700 mb-2 block">
                {t('list.filters.surfaceType.label')}
              </Label>
              <Select
                value={filters.surfaceType ?? NONE_VALUE}
                onValueChange={(value) =>
                  updateFilter(
                    'surfaceType',
                    value === NONE_VALUE ? undefined : (value as SurfaceType)
                  )
                }
              >
                <SelectTrigger className="w-full">
                  <SelectValue placeholder={t('list.filters.surfaceType.placeholder')} />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value={NONE_VALUE}>
                    {t('list.filters.surfaceType.placeholder')}
                  </SelectItem>
                  {Object.values(SurfaceType).map((type) => (
                    <SelectItem key={type} value={type}>
                      {t(`surfaceType.${type satisfies 'ROAD' | 'GRAVEL' | 'MTB' | 'MIXED'}`)}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            {/* Wind Direction */}
            <div>
              <Label className="text-sm font-medium text-gray-700 mb-2 block">
                {t('list.filters.windDirection.label')}
              </Label>
              <Select
                value={filters.windDirection ?? NONE_VALUE}
                onValueChange={(value) =>
                  updateFilter(
                    'windDirection',
                    value === NONE_VALUE ? undefined : (value as WindDirection)
                  )
                }
              >
                <SelectTrigger className="w-full">
                  <SelectValue placeholder={t('list.filters.windDirection.placeholder')} />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value={NONE_VALUE}>
                    {t('list.filters.windDirection.placeholder')}
                  </SelectItem>
                  {Object.values(WindDirection).map((dir) => (
                    <SelectItem key={dir} value={dir}>
                      {t(
                        `list.filters.windDirection.${dir satisfies 'NORTH' | 'NORTH_EAST' | 'EAST' | 'SOUTH_EAST' | 'SOUTH' | 'SOUTH_WEST' | 'WEST' | 'NORTH_WEST'}`
                      )}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            {/* Sort Options */}
            <div>
              <Label className="text-sm font-medium text-gray-700 mb-2 block">
                {t('list.filters.sort.label')}
              </Label>
              <div className="flex gap-2">
                <Select
                  value={filters.sortBy ?? RouteSortBy.DATE_TIME}
                  onValueChange={(value) => updateFilter('sortBy', value as RouteSortBy)}
                >
                  <SelectTrigger className="flex-1">
                    <SelectValue placeholder={t('list.filters.sort.DATE_TIME')} />
                  </SelectTrigger>
                  <SelectContent>
                    {Object.values(RouteSortBy).map((sort) => (
                      <SelectItem key={sort} value={sort}>
                        {t(
                          `list.filters.sort.${sort satisfies 'DISTANCE' | 'ELEVATION_GAIN' | 'HILLINESS' | 'DATE_TIME'}`
                        )}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
                <Button
                  variant="outline"
                  size="icon"
                  onClick={() =>
                    updateFilter(
                      'sortDir',
                      filters.sortDir === SortDirection.ASC ? SortDirection.DESC : SortDirection.ASC
                    )
                  }
                  title={t(
                    `list.filters.sort.${filters.sortDir === SortDirection.ASC ? 'ASC' : 'DESC'}`
                  )}
                >
                  {filters.sortDir === SortDirection.ASC ? (
                    <ChevronUpIcon className="w-4 h-4" />
                  ) : (
                    <ChevronDownIcon className="w-4 h-4" />
                  )}
                </Button>
              </div>
            </div>
          </div>
        </div>
      </CollapsibleContent>
    </Collapsible>
  )
}
