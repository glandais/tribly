import { useState, useRef, useEffect, useCallback, useMemo, type KeyboardEvent } from 'react'
import { TextInput, Paper, Stack, Text, Loader, Box, Group, ActionIcon } from '@mantine/core'
import { IconMapPin, IconX } from '@tabler/icons-react'
import { useTranslation } from 'react-i18next'
import { useQuery } from '@tanstack/react-query'
import type { GeoJsonPoint } from '@/api/dto'
import classes from './Autocomplete.module.css'

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
      headers: { 'User-Agent': 'Tribly/1.0' },
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
  // Track which query the user explicitly dismissed (click outside, escape, or select)
  const [dismissedQuery, setDismissedQuery] = useState('')
  const [selectedIndex, setSelectedIndex] = useState(-1)
  const [selectedName, setSelectedName] = useState<string | null>(null)
  const wrapperRef = useRef<HTMLDivElement>(null)
  const inputRef = useRef<HTMLInputElement>(null)

  // Use React Query for fetching
  const { data: results = [], isFetching: isLoading } = useQuery({
    queryKey: ['nominatim', debouncedQuery],
    queryFn: ({ signal }) => searchNominatim(debouncedQuery, signal),
    enabled: debouncedQuery.length >= 3,
    staleTime: 5 * 60 * 1000, // Cache for 5 minutes
  })

  // Dropdown is open when: query is valid, has results, and user hasn't dismissed this query
  const isOpen =
    debouncedQuery.length >= 3 && results.length > 0 && dismissedQuery !== debouncedQuery

  // Close dropdown when clicking outside
  useEffect(() => {
    function handleClickOutside(event: MouseEvent) {
      if (wrapperRef.current && !wrapperRef.current.contains(event.target as Node)) {
        setDismissedQuery(debouncedQuery)
      }
    }

    document.addEventListener('mousedown', handleClickOutside)
    return () => document.removeEventListener('mousedown', handleClickOutside)
  }, [debouncedQuery])

  // Debounce query (300ms)
  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedQuery(query)
    }, 300)
    return () => clearTimeout(timer)
  }, [query])

  // Filter results based on query length
  const filteredResults = useMemo(
    () => (debouncedQuery.length < 3 ? [] : results),
    [debouncedQuery, results]
  )

  const handleInputChange = useCallback(
    (inputValue: string) => {
      setQuery(inputValue)
      setSelectedIndex(-1)
      setSelectedName(null)
      // Reset dismissed state when user types new query
      if (inputValue !== query) {
        setDismissedQuery('')
      }
    },
    [query]
  )

  const handleSelect = useCallback(
    (result: NominatimResult) => {
      onChange({
        type: 'Point',
        coordinates: [parseFloat(result.lon), parseFloat(result.lat)],
      })
      setSelectedName(result.display_name)
      setQuery('')
      setDismissedQuery(debouncedQuery)
      setSelectedIndex(-1)
    },
    [onChange, debouncedQuery]
  )

  const handleClear = useCallback(() => {
    onChange(null)
    setQuery('')
    setSelectedName(null)
  }, [onChange])

  const handleKeyDown = useCallback(
    (e: KeyboardEvent) => {
      if (!isOpen || filteredResults.length === 0) return

      switch (e.key) {
        case 'ArrowDown':
          e.preventDefault()
          setSelectedIndex((prev) => (prev < filteredResults.length - 1 ? prev + 1 : prev))
          break
        case 'ArrowUp':
          e.preventDefault()
          setSelectedIndex((prev) => (prev > 0 ? prev - 1 : -1))
          break
        case 'Enter':
          e.preventDefault()
          if (selectedIndex >= 0 && selectedIndex < filteredResults.length) {
            handleSelect(filteredResults[selectedIndex])
          }
          break
        case 'Escape':
          e.preventDefault()
          setDismissedQuery(debouncedQuery)
          setSelectedIndex(-1)
          break
      }
    },
    [isOpen, filteredResults, selectedIndex, handleSelect, debouncedQuery]
  )

  const handleFocus = useCallback(() => {
    // Reset dismissed state on focus to re-show dropdown if we have results
    if (query.trim().length >= 3 && filteredResults.length > 0) {
      setDismissedQuery('')
    }
  }, [query, filteredResults.length])

  // If we have a value but no selectedName (e.g., on initial load), don't show address
  const hasValue = value && value.coordinates && value.coordinates.length === 2

  const showDropdown = isOpen && query.trim().length >= 3
  const showNoResults =
    query.trim().length >= 3 &&
    !isLoading &&
    filteredResults.length === 0 &&
    dismissedQuery !== debouncedQuery

  // Show selected location with clear button if we have coordinates
  if (hasValue && selectedName) {
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
                {selectedName}
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
    <Box ref={wrapperRef} pos="relative">
      <TextInput
        ref={inputRef}
        value={query}
        onChange={(e) => handleInputChange(e.currentTarget.value)}
        onKeyDown={handleKeyDown}
        onFocus={handleFocus}
        label={label}
        description={description}
        placeholder={placeholder ?? t('geocoder.placeholder')}
        error={error}
        required={required}
        disabled={disabled}
        rightSection={isLoading ? <Loader size="xs" /> : null}
        leftSection={<IconMapPin size={16} />}
      />

      {showDropdown && filteredResults.length > 0 && (
        <Paper
          shadow="md"
          pos="absolute"
          w="100%"
          mt="xs"
          style={{ zIndex: 10, maxHeight: 240, overflow: 'auto' }}
          withBorder
        >
          <Stack gap={0}>
            {filteredResults.map((result, index) => (
              <Box
                key={result.place_id}
                component="button"
                type="button"
                onClick={() => handleSelect(result)}
                className={classes.item}
                p="sm"
                w="100%"
                bg={index === selectedIndex ? 'var(--mantine-color-default-hover)' : undefined}
              >
                <Group gap="xs" wrap="nowrap">
                  <IconMapPin size={14} style={{ flexShrink: 0 }} />
                  <Text size="sm" ta="left" truncate style={{ flex: 1 }}>
                    {result.display_name}
                  </Text>
                </Group>
              </Box>
            ))}
          </Stack>
        </Paper>
      )}

      {showNoResults && (
        <Paper shadow="md" pos="absolute" w="100%" mt="xs" p="sm" style={{ zIndex: 10 }} withBorder>
          <Text size="sm" c="dimmed">
            {t('geocoder.noResults')}
          </Text>
        </Paper>
      )}
    </Box>
  )
}
