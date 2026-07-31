import { useState, useCallback, useMemo } from 'react'
import { useTranslation } from 'react-i18next'
import { Group, Avatar, Text } from '@mantine/core'
import { Autocomplete } from './Autocomplete'
import { useGetMembers } from '@/api/endpoints/team-members/team-members'
import type { PublicUserDto } from '@/api/dto'

const RESULT_LIMIT = 20

interface TeamMemberAutocompleteProps {
  /** The team whose roster is searched. Required — there is no domain-wide people search. */
  teamSlug: string
  onSelect: (user: PublicUserDto) => void
  placeholder?: string
}

/**
 * Picks someone from a team's roster.
 *
 * Replaces the old `UserAutocomplete`, which searched every user of the domain through
 * `GET /api/users/search`. That endpoint is gone: a picker that must yield a *member* has no
 * business ranging over the whole domain, and the member directory now answers the narrower
 * question directly. Organisers may read it whatever the team's `enableMemberDirectory` setting,
 * which is exactly what keeps the ride-group leader picker working.
 */
export function TeamMemberAutocomplete({
  teamSlug,
  onSelect,
  placeholder,
}: TeamMemberAutocompleteProps) {
  const { t } = useTranslation()
  const [query, setQuery] = useState('')

  const shouldSearch = query.trim().length >= 2

  const { data, isLoading } = useGetMembers(
    teamSlug,
    { search: query.trim(), size: RESULT_LIMIT },
    { query: { enabled: shouldSearch } }
  )

  const users = useMemo(() => (data?.members ?? []).map((member) => member.user), [data])

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
        <Avatar src={user.avatarUrl} alt={user.displayName} size="sm" color="primary">
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
      placeholder={placeholder || t('autocomplete.searchMembers')}
      noResultsMessage={t('autocomplete.noUsersFound')}
      minChars={2}
      clearOnSelect={true}
    />
  )
}
