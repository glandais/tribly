import { useState, useRef, useEffect, useCallback, type ReactNode } from 'react'
import { TextInput, Paper, Stack, Text, Loader, Box } from '@mantine/core'
import classes from './Autocomplete.module.css'

export interface AutocompleteProps<T> {
  items: T[]
  isLoading?: boolean
  onQueryChange: (query: string) => void
  onSelect: (item: T) => void
  renderItem: (item: T, isSelected: boolean) => ReactNode
  getItemKey: (item: T) => string | number
  placeholder?: string
  minChars?: number
  noResultsMessage?: string
  clearOnSelect?: boolean
}

export function Autocomplete<T>({
  items,
  isLoading = false,
  onQueryChange,
  onSelect,
  renderItem,
  getItemKey,
  placeholder = 'Search...',
  minChars = 2,
  noResultsMessage = 'No results found',
  clearOnSelect = true,
}: AutocompleteProps<T>) {
  const [query, setQuery] = useState('')
  const [isOpen, setIsOpen] = useState(false)
  const [selectedIndex, setSelectedIndex] = useState(-1)
  const wrapperRef = useRef<HTMLDivElement>(null)
  const inputRef = useRef<HTMLInputElement>(null)

  // Close dropdown when clicking outside
  useEffect(() => {
    function handleClickOutside(event: MouseEvent) {
      if (wrapperRef.current && !wrapperRef.current.contains(event.target as Node)) {
        setIsOpen(false)
      }
    }

    document.addEventListener('mousedown', handleClickOutside)
    return () => document.removeEventListener('mousedown', handleClickOutside)
  }, [])

  const handleInputChange = useCallback(
    (value: string) => {
      setQuery(value)
      const shouldOpen = value.trim().length >= minChars
      setIsOpen(shouldOpen)
      setSelectedIndex(-1)
      onQueryChange(value)
    },
    [minChars, onQueryChange]
  )

  const handleSelect = useCallback(
    (item: T) => {
      onSelect(item)
      if (clearOnSelect) {
        setQuery('')
      }
      setIsOpen(false)
      setSelectedIndex(-1)
    },
    [onSelect, clearOnSelect]
  )

  const handleKeyDown = useCallback(
    (e: React.KeyboardEvent) => {
      if (!isOpen || items.length === 0) return

      switch (e.key) {
        case 'ArrowDown':
          e.preventDefault()
          setSelectedIndex((prev) => (prev < items.length - 1 ? prev + 1 : prev))
          break
        case 'ArrowUp':
          e.preventDefault()
          setSelectedIndex((prev) => (prev > 0 ? prev - 1 : -1))
          break
        case 'Enter':
          e.preventDefault()
          if (selectedIndex >= 0 && selectedIndex < items.length) {
            handleSelect(items[selectedIndex])
          }
          break
        case 'Escape':
          e.preventDefault()
          setIsOpen(false)
          setSelectedIndex(-1)
          break
      }
    },
    [isOpen, items, selectedIndex, handleSelect]
  )

  const handleFocus = useCallback(() => {
    if (query.trim().length >= minChars) {
      setIsOpen(true)
    }
  }, [query, minChars])

  const showDropdown = isOpen && query.trim().length >= minChars
  const showNoResults = showDropdown && !isLoading && items.length === 0

  return (
    <Box ref={wrapperRef} pos="relative">
      <TextInput
        ref={inputRef}
        value={query}
        onChange={(e) => handleInputChange(e.currentTarget.value)}
        onKeyDown={handleKeyDown}
        onFocus={handleFocus}
        placeholder={placeholder}
        rightSection={isLoading ? <Loader size="xs" /> : null}
      />

      {showDropdown && items.length > 0 && (
        <Paper
          shadow="md"
          pos="absolute"
          w="100%"
          mt="xs"
          style={{ zIndex: 10, maxHeight: 240, overflow: 'auto' }}
          withBorder
        >
          <Stack gap={0}>
            {items.map((item, index) => (
              <Box
                key={getItemKey(item)}
                component="button"
                type="button"
                onClick={() => handleSelect(item)}
                className={classes.item}
                p="sm"
                w="100%"
                bg={index === selectedIndex ? 'var(--mantine-color-default-hover)' : undefined}
              >
                {renderItem(item, index === selectedIndex)}
              </Box>
            ))}
          </Stack>
        </Paper>
      )}

      {showNoResults && (
        <Paper shadow="md" pos="absolute" w="100%" mt="xs" p="sm" style={{ zIndex: 10 }} withBorder>
          <Text size="sm" c="dimmed">
            {noResultsMessage}
          </Text>
        </Paper>
      )}
    </Box>
  )
}
