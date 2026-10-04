import { useSyncExternalStore } from 'react'
import { useQueryClient } from '@tanstack/react-query'
import { useTranslation } from 'react-i18next'
import { Select } from '@mantine/core'
import { getGetMeQueryKey, useUpdateMyPreferences } from '@/api/endpoints/users/users'

const TIMEZONE_OPTIONS = Intl.supportedValuesOf('timeZone')

/**
 * The browser's own zone, read only once hydrated: the server rendering the page has its own zone,
 * not the visitor's, and a field prefilled with it would not match the markup the client hydrates.
 */
const noSubscription = () => () => {}
const browserZone = () => Intl.DateTimeFormat().resolvedOptions().timeZone
const noZoneOnServer = () => null

interface TimezonePreferenceProps {
  /** `UserDto.timezone` — null means the user never chose one, so the browser's own zone (the
   * value the effective-timezone resolution already falls back to) is prefilled here. */
  timezone: string | null | undefined
}

export function TimezonePreference({ timezone }: TimezonePreferenceProps) {
  const { t } = useTranslation()
  const queryClient = useQueryClient()
  const mutation = useUpdateMyPreferences()
  const fallbackZone = useSyncExternalStore(noSubscription, browserZone, noZoneOnServer)

  const handleChange = (value: string | null) => {
    if (!value) return
    // Partial PATCH: send only the field being changed, never the whole preference set.
    mutation.mutate(
      { data: { timezone: value } },
      { onSuccess: () => queryClient.invalidateQueries({ queryKey: getGetMeQueryKey() }) }
    )
  }

  return (
    <Select
      label={t('profile.preferences.timezone.label')}
      searchable
      value={timezone ?? fallbackZone}
      onChange={handleChange}
      disabled={mutation.isPending}
      data={TIMEZONE_OPTIONS}
      maxDropdownHeight={280}
    />
  )
}
