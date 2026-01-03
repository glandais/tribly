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
import { Checkbox } from '@/components/ui/checkbox'
import { Label } from '@/components/ui/label'
import { RangeInput } from '@/components/common/RangeInput'
import {
  Hilliness,
  SurfaceType,
  WindDirection,
  RouteSortBy,
  SortDirection,
  type RouteFilters,
} from '@/hooks/useRoute'

const NONE_VALUE = '_none'

interface RouteFilterPanelProps {
  filters: RouteFilters
  onFiltersChange: (filters: RouteFilters) => void
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

  const updateFilter = <K extends keyof RouteFilters>(key: K, value: RouteFilters[K]) => {
    onFiltersChange({ ...filters, [key]: value, page: 0 })
  }

  const toggleSurfaceType = (type: (typeof SurfaceType)[keyof typeof SurfaceType]) => {
    const current = filters.surfaceTypes || []
    const updated = current.includes(type) ? current.filter((t) => t !== type) : [...current, type]
    updateFilter('surfaceTypes', updated.length > 0 ? updated : undefined)
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
    (filters.surfaceTypes?.length ?? 0) > 0 ||
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
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
            {/* Distance Range */}
            <RangeInput
              label={t('list.filters.distance.label')}
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
              <div className="flex flex-wrap gap-2">
                {Object.values(Hilliness).map((value) => (
                  <Button
                    key={value}
                    variant={filters.hilliness === value ? 'default' : 'outline'}
                    size="sm"
                    onClick={() =>
                      updateFilter('hilliness', filters.hilliness === value ? undefined : value)
                    }
                  >
                    {t(`list.filters.hilliness.${value}`)}
                  </Button>
                ))}
              </div>
            </div>

            {/* Surface Types Multi-select */}
            <div>
              <Label className="text-sm font-medium text-gray-700 mb-2 block">
                {t('list.filters.surfaceType.label')}
              </Label>
              <div className="flex flex-wrap gap-3">
                {Object.values(SurfaceType).map((type) => (
                  <div key={type} className="flex items-center gap-2">
                    <Checkbox
                      id={`surface-${type}`}
                      checked={filters.surfaceTypes?.includes(type) ?? false}
                      onCheckedChange={() => toggleSurfaceType(type)}
                    />
                    <Label htmlFor={`surface-${type}`} className="text-sm cursor-pointer">
                      {t(`surfaceType.${type}`)}
                    </Label>
                  </div>
                ))}
              </div>
            </div>

            {/* Wind Direction Single-select */}
            <div>
              <Label className="text-sm font-medium text-gray-700 mb-2 block">
                {t('list.filters.windDirection.label')}
              </Label>
              <Select
                value={filters.windDirection ?? NONE_VALUE}
                onValueChange={(value) =>
                  updateFilter(
                    'windDirection',
                    value === NONE_VALUE
                      ? undefined
                      : (value as (typeof WindDirection)[keyof typeof WindDirection])
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
                      {t(`list.filters.windDirection.${dir}`)}
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
                  value={filters.sortBy ?? RouteSortBy.DateTime}
                  onValueChange={(value) =>
                    updateFilter('sortBy', value as (typeof RouteSortBy)[keyof typeof RouteSortBy])
                  }
                >
                  <SelectTrigger className="flex-1">
                    <SelectValue placeholder={t('list.filters.sort.DATE_TIME')} />
                  </SelectTrigger>
                  <SelectContent>
                    {Object.values(RouteSortBy).map((sort) => (
                      <SelectItem key={sort} value={sort}>
                        {t(`list.filters.sort.${sort}`)}
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
                      filters.sortDir === SortDirection.Asc ? SortDirection.Desc : SortDirection.Asc
                    )
                  }
                  title={t(
                    `list.filters.sort.${filters.sortDir === SortDirection.Asc ? 'ASC' : 'DESC'}`
                  )}
                >
                  {filters.sortDir === SortDirection.Asc ? (
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
