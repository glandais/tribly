import { useState, useCallback } from 'react'
import { useTranslation } from 'react-i18next'
import { Group, Avatar, Text } from '@mantine/core'
import { Autocomplete } from './Autocomplete'
import { useSearchUsers } from '@/api/endpoints/users/users'
import type { PublicUserDto } from '@/api/dto'

interface UserAutocompleteProps {
  onSelect: (user: PublicUserDto) => void
  placeholder?: string
}

export function UserAutocomplete({ onSelect, placeholder }: UserAutocompleteProps) {
  const { t } = useTranslation()
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
      <Group gap="sm" wrap="nowrap">
        <Avatar src={user.avatarUrl} alt={user.displayName} size="sm" color="indigo">
          {user.displayName.charAt(0).toUpperCase()}
        </Avatar>
        <Text size="sm">{user.displayName}</Text>
      </Group>
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
      minChars={2}
      clearOnSelect={true}
    />
  )
}
