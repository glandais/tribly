import { useState, useRef, useEffect, useCallback, type ReactNode } from 'react'
import { ArrowPathIcon } from '@heroicons/react/24/outline'

export interface AutocompleteProps<T> {
  /** Items to display in the dropdown */
  items: T[]
  /** Whether the items are currently loading */
  isLoading?: boolean
  /** Called when the query changes */
  onQueryChange: (query: string) => void
  /** Called when an item is selected */
  onSelect: (item: T) => void
  /** Render function for each item */
  renderItem: (item: T, isSelected: boolean) => ReactNode
  /** Get unique key for each item */
  getItemKey: (item: T) => string | number
  /** Placeholder text for the input */
  placeholder?: string
  /** Additional class name for the wrapper */
  className?: string
  /** Minimum characters before showing dropdown */
  minChars?: number
  /** Message to show when no items found */
  noResultsMessage?: string
  /** Clear input after selection */
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
  className = '',
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
    <div ref={wrapperRef} className={`relative ${className}`}>
      <div className="relative">
        <input
          ref={inputRef}
          type="text"
          value={query}
          onChange={(e) => handleInputChange(e.target.value)}
          onKeyDown={handleKeyDown}
          onFocus={handleFocus}
          placeholder={placeholder}
          className="block w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-indigo-500 focus:border-indigo-500"
        />
        {isLoading && (
          <div className="absolute right-3 top-1/2 transform -translate-y-1/2">
            <ArrowPathIcon className="animate-spin h-5 w-5 text-gray-400" />
          </div>
        )}
      </div>

      {showDropdown && items.length > 0 && (
        <div className="absolute z-10 mt-1 w-full bg-white shadow-lg max-h-60 rounded-md border border-gray-200 overflow-auto">
          <ul className="py-1">
            {items.map((item, index) => (
              <li key={getItemKey(item)}>
                <button
                  type="button"
                  onClick={() => handleSelect(item)}
                  className={`w-full text-left px-4 py-2 hover:bg-gray-100 ${
                    index === selectedIndex ? 'bg-gray-100' : ''
                  }`}
                >
                  {renderItem(item, index === selectedIndex)}
                </button>
              </li>
            ))}
          </ul>
        </div>
      )}

      {showNoResults && (
        <div className="absolute z-10 mt-1 w-full bg-white shadow-lg rounded-md border border-gray-200 py-2 px-4">
          <p className="text-sm text-gray-500">{noResultsMessage}</p>
        </div>
      )}
    </div>
  )
}
