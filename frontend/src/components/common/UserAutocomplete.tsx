import { useState, useCallback } from 'react'
import { useTranslation } from 'react-i18next'
import { Autocomplete } from './Autocomplete'
import { useSearchUsers } from '@/api/endpoints/users/users'
import type { PublicUserDto } from '@/api/dto'

interface UserAutocompleteProps {
  onSelect: (user: PublicUserDto) => void
  placeholder?: string
  className?: string
}

export function UserAutocomplete({ onSelect, placeholder, className = '' }: UserAutocompleteProps) {
  const { t } = useTranslation('common')
  const [query, setQuery] = useState('')

  const shouldSearch = query.trim().length >= 2

  const { data: users = [], isLoading } = useSearchUsers(
    { q: query },
    { query: { enabled: shouldSearch } }
  )

  const handleQueryChange = useCallback((newQuery: string) => {
    setQuery(newQuery)
  }, [])

  const handleSelect = useCallback(
    (user: PublicUserDto) => {
      onSelect(user)
      setQuery('')
    },
    [onSelect]
  )

  const renderUser = useCallback(
    (user: PublicUserDto) => (
      <div className="flex items-center gap-3">
        {user.avatarUrl ? (
          <img src={user.avatarUrl} alt={user.displayName} className="h-8 w-8 rounded-full" />
        ) : (
          <div className="h-8 w-8 rounded-full bg-indigo-500 flex items-center justify-center">
            <span className="text-white text-sm font-medium">
              {user.displayName.charAt(0).toUpperCase()}
            </span>
          </div>
        )}
        <span className="text-sm text-gray-900">{user.displayName}</span>
      </div>
    ),
    []
  )

  return (
    <Autocomplete<PublicUserDto>
      items={users}
      isLoading={isLoading}
      onQueryChange={handleQueryChange}
      onSelect={handleSelect}
      renderItem={renderUser}
      getItemKey={(user) => user.id}
      placeholder={placeholder || t('autocomplete.searchUsers')}
      noResultsMessage={t('autocomplete.noUsersFound')}
      className={className}
      minChars={2}
      clearOnSelect={true}
    />
  )
}
