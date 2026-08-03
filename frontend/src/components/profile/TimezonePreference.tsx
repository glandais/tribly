import { useQueryClient } from '@tanstack/react-query'
import { useTranslation } from 'react-i18next'
import { Select, Stack, Text } from '@mantine/core'
import { getGetMeQueryKey, useUpdateMyPreferences } from '@/api/endpoints/users/users'

const TIMEZONE_OPTIONS = Intl.supportedValuesOf('timeZone')

interface TimezonePreferenceProps {
  /** `UserDto.timezone` — null means the user never chose one, so the browser's own zone (the
   * value the effective-timezone resolution already falls back to) is prefilled here. */
  timezone: string | null | undefined
}

export function TimezonePreference({ timezone }: TimezonePreferenceProps) {
  const { t } = useTranslation()
  const queryClient = useQueryClient()
  const mutation = useUpdateMyPreferences()

  const handleChange = (value: string | null) => {
    if (!value) return
    // Partial PATCH: send only the field being changed, never the whole preference set.
    mutation.mutate(
      { data: { timezone: value } },
      { onSuccess: () => queryClient.invalidateQueries({ queryKey: getGetMeQueryKey() }) }
    )
  }

  return (
    <Stack gap={4}>
      <Text size="sm" fw={500}>
        {t('profile.preferences.timezone.label')}
      </Text>
      <Select
        searchable
        value={timezone ?? Intl.DateTimeFormat().resolvedOptions().timeZone}
        onChange={handleChange}
        disabled={mutation.isPending}
        data={TIMEZONE_OPTIONS}
        maxDropdownHeight={280}
      />
    </Stack>
  )
}
