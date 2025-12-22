import { useState, useRef, useEffect } from 'react'
import { ArrowPathIcon } from '@heroicons/react/24/outline'
import { useUserSearch } from '../../hooks/useUserSearch'
import type { PublicUserDto } from '../../hooks/useUserSearch'

interface UserAutocompleteProps {
  onSelect: (user: PublicUserDto) => void
  placeholder?: string
  className?: string
}

export function UserAutocomplete({ onSelect, placeholder, className = '' }: UserAutocompleteProps) {
  const [query, setQuery] = useState('')
  const [isOpen, setIsOpen] = useState(false)
  const [selectedIndex, setSelectedIndex] = useState(-1)
  const wrapperRef = useRef<HTMLDivElement>(null)
  const inputRef = useRef<HTMLInputElement>(null)

  const { data: users = [], isLoading } = useUserSearch(query, isOpen)

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

  const handleInputChange = (value: string) => {
    setQuery(value)
    setIsOpen(value.trim().length >= 2)
    setSelectedIndex(-1)
  }

  const handleSelect = (user: PublicUserDto) => {
    onSelect(user)
    setQuery('')
    setIsOpen(false)
    setSelectedIndex(-1)
  }

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (!isOpen || users.length === 0) return

    switch (e.key) {
      case 'ArrowDown':
        e.preventDefault()
        setSelectedIndex((prev) => (prev < users.length - 1 ? prev + 1 : prev))
        break
      case 'ArrowUp':
        e.preventDefault()
        setSelectedIndex((prev) => (prev > 0 ? prev - 1 : -1))
        break
      case 'Enter':
        e.preventDefault()
        if (selectedIndex >= 0 && selectedIndex < users.length) {
          handleSelect(users[selectedIndex])
        }
        break
      case 'Escape':
        e.preventDefault()
        setIsOpen(false)
        setSelectedIndex(-1)
        break
    }
  }

  return (
    <div ref={wrapperRef} className={`relative ${className}`}>
      <div className="relative">
        <input
          ref={inputRef}
          type="text"
          value={query}
          onChange={(e) => handleInputChange(e.target.value)}
          onKeyDown={handleKeyDown}
          onFocus={() => query.trim().length >= 2 && setIsOpen(true)}
          placeholder={placeholder || 'Search users...'}
          className="block w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-indigo-500 focus:border-indigo-500"
        />
        {isLoading && (
          <div className="absolute right-3 top-1/2 transform -translate-y-1/2">
            <ArrowPathIcon className="animate-spin h-5 w-5 text-gray-400" />
          </div>
        )}
      </div>

      {isOpen && users.length > 0 && (
        <div className="absolute z-10 mt-1 w-full bg-white shadow-lg max-h-60 rounded-md border border-gray-200 overflow-auto">
          <ul className="py-1">
            {users.map((user: PublicUserDto, index: number) => (
              <li key={user.id}>
                <button
                  type="button"
                  onClick={() => handleSelect(user)}
                  className={`w-full text-left px-4 py-2 hover:bg-gray-100 flex items-center gap-3 ${
                    index === selectedIndex ? 'bg-gray-100' : ''
                  }`}
                >
                  {user.avatarUrl ? (
                    <img
                      src={user.avatarUrl}
                      alt={user.displayName}
                      className="h-8 w-8 rounded-full"
                    />
                  ) : (
                    <div className="h-8 w-8 rounded-full bg-indigo-500 flex items-center justify-center">
                      <span className="text-white text-sm font-medium">
                        {user.displayName.charAt(0).toUpperCase()}
                      </span>
                    </div>
                  )}
                  <span className="text-sm text-gray-900">{user.displayName}</span>
                </button>
              </li>
            ))}
          </ul>
        </div>
      )}

      {isOpen && !isLoading && query.trim().length >= 2 && users.length === 0 && (
        <div className="absolute z-10 mt-1 w-full bg-white shadow-lg rounded-md border border-gray-200 py-2 px-4">
          <p className="text-sm text-gray-500">No users found</p>
        </div>
      )}
    </div>
  )
}
