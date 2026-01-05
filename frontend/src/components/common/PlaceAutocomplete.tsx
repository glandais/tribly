import { useState, useCallback, useMemo } from 'react'
import { useTranslation } from 'react-i18next'
import { MapPinIcon, XMarkIcon } from '@heroicons/react/24/outline'
import { Autocomplete } from './Autocomplete'
import { useListPlaces } from '../../api/endpoints/places/places'
import type { PlaceDetailDto } from '@/api/dto'
import { Button } from '@/components/ui/button'

interface PlaceAutocompleteProps {
  teamSlug: string
  /** Current place ID value (controlled input) */
  value?: string
  /** Called when place changes (with place id or undefined when cleared) */
  onChange: (placeId: string | undefined) => void
  /** Filter to only show places that can be used as start */
  filterStart?: boolean
  /** Filter to only show places that can be used as end */
  filterEnd?: boolean
  placeholder?: string
  className?: string
}

export function PlaceAutocomplete({
  teamSlug,
  value,
  onChange,
  filterStart,
  filterEnd,
  placeholder,
  className = '',
}: PlaceAutocompleteProps) {
  const { t } = useTranslation()
  const [query, setQuery] = useState('')

  const { data: placesData } = useListPlaces(teamSlug)

  const places = placesData?.places

  // Find the selected place from the value
  const selectedPlace = useMemo(() => {
    if (!value || !places) return undefined
    return places.find((place) => place.id === value)
  }, [value, places])

  // Filter places based on query and start/end filters
  const filteredPlaces = useMemo(() => {
    if (!places) return []

    return places.filter((place) => {
      // Filter by start/end capability
      if (filterStart && !place.startPlace) return false
      if (filterEnd && !place.endPlace) return false

      // Filter by search query
      if (query.trim().length < 1) return true
      const searchLower = query.toLowerCase()
      return (
        place.name.toLowerCase().includes(searchLower) ||
        place.address?.toLowerCase().includes(searchLower)
      )
    })
  }, [places, query, filterStart, filterEnd])

  const handleQueryChange = useCallback((newQuery: string) => {
    setQuery(newQuery)
  }, [])

  const handleSelect = useCallback(
    (place: PlaceDetailDto) => {
      onChange(place.id)
      setQuery('')
    },
    [onChange]
  )

  const handleClear = useCallback(() => {
    onChange(undefined)
  }, [onChange])

  const renderPlace = useCallback(
    (place: PlaceDetailDto) => (
      <div className="flex items-center gap-3">
        <MapPinIcon className="h-5 w-5 text-gray-400 shrink-0" />
        <div className="min-w-0">
          <p className="text-sm font-medium text-gray-900 truncate">{place.name}</p>
          {place.address && <p className="text-xs text-gray-500 truncate">{place.address}</p>}
        </div>
      </div>
    ),
    []
  )

  // Show selected place with clear button when a value is set
  if (selectedPlace) {
    return (
      <div className={`flex items-center gap-2 ${className}`}>
        <div className="flex-1 flex items-center gap-3 px-4 py-2 border border-gray-300 rounded-md bg-white">
          <MapPinIcon className="h-5 w-5 text-gray-400 shrink-0" />
          <div className="min-w-0 flex-1">
            <p className="text-sm font-medium text-gray-900 truncate">{selectedPlace.name}</p>
            {selectedPlace.address && (
              <p className="text-xs text-gray-500 truncate">{selectedPlace.address}</p>
            )}
          </div>
        </div>
        <Button
          type="button"
          variant="ghost"
          size="icon"
          onClick={handleClear}
          aria-label={t('planner.clear')}
        >
          <XMarkIcon className="size-4" />
        </Button>
      </div>
    )
  }

  return (
    <Autocomplete<PlaceDetailDto>
      items={filteredPlaces}
      isLoading={false}
      onQueryChange={handleQueryChange}
      onSelect={handleSelect}
      renderItem={renderPlace}
      getItemKey={(place) => place.id}
      placeholder={placeholder || t('autocomplete.searchPlaces')}
      noResultsMessage={t('autocomplete.noPlacesFound')}
      className={className}
      minChars={1}
      clearOnSelect={true}
    />
  )
}
