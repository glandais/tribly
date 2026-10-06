import { useQueryClient } from '@tanstack/react-query'
import { useTranslation } from 'react-i18next'
import { getGetMeQueryKey, useUpdateMyPreferences } from '@/api/endpoints/users/users'
import { TimezoneSelect } from '@/components/common/TimezoneSelect'
import { useBrowserTimezone } from '@/utils/timezones'

interface TimezonePreferenceProps {
  /** `UserDto.timezone` — null means the user never chose one, so the browser's own zone (the
   * value the effective-timezone resolution already falls back to) is prefilled here. */
  timezone: string | null | undefined
}

export function TimezonePreference({ timezone }: TimezonePreferenceProps) {
  const { t } = useTranslation()
  const queryClient = useQueryClient()
  const mutation = useUpdateMyPreferences()
  const fallbackZone = useBrowserTimezone()

  const handleChange = (value: string | null) => {
    if (!value) return
    // Partial PATCH: send only the field being changed, never the whole preference set.
    mutation.mutate(
      { data: { timezone: value } },
      { onSuccess: () => queryClient.invalidateQueries({ queryKey: getGetMeQueryKey() }) }
    )
  }

  return (
    <TimezoneSelect
      label={t('profile.preferences.timezone.label')}
      value={timezone ?? fallbackZone}
      onChange={handleChange}
      disabled={mutation.isPending}
    />
  )
}
