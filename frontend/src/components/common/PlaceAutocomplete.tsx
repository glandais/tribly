import { useState, useCallback, useMemo } from 'react'
import { useTranslation } from 'react-i18next'
import { MapPinIcon } from '@heroicons/react/24/outline'
import { Autocomplete } from './Autocomplete'
import { usePlaces } from '../../hooks/usePlaces'
import type { PlaceDetailDto } from '../../api/api'

interface PlaceAutocompleteProps {
  teamSlug: string
  onSelect: (place: PlaceDetailDto) => void
  /** Filter to only show places that can be used as start */
  filterStart?: boolean
  /** Filter to only show places that can be used as end */
  filterEnd?: boolean
  placeholder?: string
  className?: string
}

export function PlaceAutocomplete({
  teamSlug,
  onSelect,
  filterStart,
  filterEnd,
  placeholder,
  className = '',
}: PlaceAutocompleteProps) {
  const { t } = useTranslation('common')
  const [query, setQuery] = useState('')

  const { data: placesData } = usePlaces(teamSlug)

  // Filter places based on query and start/end filters
  const filteredPlaces = useMemo(() => {
    const places = placesData?.places ?? []

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
  }, [placesData?.places, query, filterStart, filterEnd])

  const handleQueryChange = useCallback((newQuery: string) => {
    setQuery(newQuery)
  }, [])

  const handleSelect = useCallback(
    (place: PlaceDetailDto) => {
      onSelect(place)
      setQuery('')
    },
    [onSelect]
  )

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
