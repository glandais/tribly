import { useState, useEffect, useCallback, useMemo } from 'react'
import { Paper, Text, Box, Group, ActionIcon } from '@mantine/core'
import { IconMapPin, IconX } from '@tabler/icons-react'
import { useTranslation } from 'react-i18next'
import { useQuery } from '@tanstack/react-query'
import { Autocomplete } from './Autocomplete'
import type { GeoJsonPoint } from '@/api/dto'

interface NominatimResult {
  place_id: number
  display_name: string
  lat: string
  lon: string
  boundingbox: string[]
}

async function searchNominatim(query: string, signal: AbortSignal): Promise<NominatimResult[]> {
  const response = await fetch(
    `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(query)}&limit=5`,
    {
      signal,
    }
  )
  return response.json()
}

export interface GeocoderAutocompleteProps {
  value?: GeoJsonPoint | null
  onChange: (point: GeoJsonPoint | null) => void
  label?: string
  description?: string
  placeholder?: string
  error?: string
  required?: boolean
  disabled?: boolean
}

export function GeocoderAutocomplete({
  value,
  onChange,
  label,
  description,
  placeholder,
  error,
  required,
  disabled,
}: GeocoderAutocompleteProps) {
  const { t } = useTranslation()
  const [query, setQuery] = useState('')
  const [debouncedQuery, setDebouncedQuery] = useState('')
  const [selectedName, setSelectedName] = useState<string | null>(null)

  // Debounce query (300ms)
  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedQuery(query)
    }, 300)
    return () => clearTimeout(timer)
  }, [query])

  // Use React Query for fetching
  const { data: results = [], isFetching: isLoading } = useQuery({
    queryKey: ['nominatim', debouncedQuery],
    queryFn: ({ signal }) => searchNominatim(debouncedQuery, signal),
    enabled: debouncedQuery.length >= 3,
    staleTime: 5 * 60 * 1000, // Cache for 5 minutes
  })

  // Filter results based on query length
  const filteredResults = useMemo(
    () => (debouncedQuery.length < 3 ? [] : results),
    [debouncedQuery, results]
  )

  const handleQueryChange = useCallback((newQuery: string) => {
    setQuery(newQuery)
  }, [])

  const handleSelect = useCallback(
    (result: NominatimResult) => {
      onChange({
        type: 'Point',
        coordinates: [parseFloat(result.lon), parseFloat(result.lat)],
      })
      setSelectedName(result.display_name)
      setQuery('')
    },
    [onChange]
  )

  const handleClear = useCallback(() => {
    onChange(null)
    setQuery('')
    setSelectedName(null)
  }, [onChange])

  const renderItem = useCallback(
    (result: NominatimResult) => (
      <Group gap="xs" wrap="nowrap">
        <IconMapPin size={14} style={{ flexShrink: 0 }} />
        <Text size="sm" ta="left" truncate style={{ flex: 1 }}>
          {result.display_name}
        </Text>
      </Group>
    ),
    []
  )

  const hasValue = value && value.coordinates && value.coordinates.length === 2
  const displayName =
    selectedName ??
    (hasValue ? `${value!.coordinates[1].toFixed(6)}, ${value!.coordinates[0].toFixed(6)}` : null)

  if (hasValue && displayName) {
    return (
      <Box>
        {label && (
          <Text size="sm" fw={500} mb={4}>
            {label}
            {required && (
              <Text component="span" c="red">
                {' '}
                *
              </Text>
            )}
          </Text>
        )}
        {description && (
          <Text size="xs" c="dimmed" mb={4}>
            {description}
          </Text>
        )}
        <Paper p="sm" withBorder>
          <Group justify="space-between" wrap="nowrap">
            <Group gap="xs" wrap="nowrap" style={{ flex: 1, minWidth: 0 }}>
              <IconMapPin size={16} style={{ flexShrink: 0 }} />
              <Text size="sm" truncate style={{ flex: 1 }}>
                {displayName}
              </Text>
            </Group>
            <ActionIcon
              variant="subtle"
              size="sm"
              onClick={handleClear}
              disabled={disabled}
              aria-label={t('geocoder.clear')}
            >
              <IconX size={14} />
            </ActionIcon>
          </Group>
        </Paper>
        {error && (
          <Text size="xs" c="red" mt={4}>
            {error}
          </Text>
        )}
      </Box>
    )
  }

  return (
    <Autocomplete<NominatimResult>
      items={filteredResults}
      isLoading={isLoading}
      onQueryChange={handleQueryChange}
      onSelect={handleSelect}
      renderItem={renderItem}
      getItemKey={(result) => result.place_id}
      placeholder={placeholder ?? t('geocoder.placeholder')}
      noResultsMessage={t('geocoder.noResults')}
      minChars={3}
      clearOnSelect={true}
      label={label}
      description={description}
      error={error}
      required={required}
      disabled={disabled}
      leftSection={<IconMapPin size={16} />}
    />
  )
}
