import { useState, useCallback } from 'react'
import { useTranslation } from 'react-i18next'
import { useDebouncedValue } from '@mantine/hooks'
import { Group, Text, ActionIcon, Paper, Box } from '@mantine/core'
import { IconMapPin, IconX } from '@tabler/icons-react'
import { Autocomplete } from './Autocomplete'
import { useListPlaces, useGetPlace } from '../../api/endpoints/places/places'
import { placeAutocompleteParams } from './placeAutocompleteParams'
import type { PlaceDetailDto } from '@/api/dto'

interface PlaceAutocompleteProps {
  teamSlug: string
  value?: string
  onChange: (placeId: string | undefined) => void
  filterStart?: boolean
  filterEnd?: boolean
  placeholder?: string
}

export function PlaceAutocomplete({
  teamSlug,
  value,
  onChange,
  filterStart,
  filterEnd,
  placeholder,
}: PlaceAutocompleteProps) {
  const { t } = useTranslation()
  const [query, setQuery] = useState('')
  const [debouncedQuery] = useDebouncedValue(query, 300)

  const { data: selectedPlaceData } = useGetPlace(teamSlug, value ?? '', {
    query: { enabled: !!value },
  })
  const selectedPlace = value ? selectedPlaceData : undefined

  const { data: placesData } = useListPlaces(
    teamSlug,
    placeAutocompleteParams({ filterStart, filterEnd, search: debouncedQuery })
  )
  const filteredPlaces = placesData?.places ?? []

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
      <Group gap="sm" wrap="nowrap">
        <IconMapPin size={20} color="var(--mantine-color-dimmed)" style={{ flexShrink: 0 }} />
        <Box style={{ minWidth: 0 }}>
          <Text size="sm" fw={500} truncate>
            {place.name}
          </Text>
          {place.address && (
            <Text size="xs" c="dimmed" truncate>
              {place.address}
            </Text>
          )}
        </Box>
      </Group>
    ),
    []
  )

  if (selectedPlace) {
    return (
      <Group gap="xs">
        <Paper withBorder p="sm" style={{ flex: 1 }}>
          <Group gap="sm" wrap="nowrap">
            <IconMapPin size={20} color="var(--mantine-color-dimmed)" style={{ flexShrink: 0 }} />
            <Box style={{ minWidth: 0, flex: 1 }}>
              <Text size="sm" fw={500} truncate>
                {selectedPlace.name}
              </Text>
              {selectedPlace.address && (
                <Text size="xs" c="dimmed" truncate>
                  {selectedPlace.address}
                </Text>
              )}
            </Box>
          </Group>
        </Paper>
        <ActionIcon
          variant="subtle"
          color="gray"
          onClick={handleClear}
          aria-label={t('planner.clear')}
        >
          <IconX size={16} />
        </ActionIcon>
      </Group>
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
      minChars={1}
      clearOnSelect={true}
    />
  )
}
