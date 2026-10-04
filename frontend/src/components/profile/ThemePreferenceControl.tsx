import { useQueryClient } from '@tanstack/react-query'
import { useTranslation } from 'react-i18next'
import { SegmentedControl, Stack, Text, useMantineColorScheme } from '@mantine/core'
import { getGetMeQueryKey, useUpdateMyPreferences } from '@/api/endpoints/users/users'
import { ThemePreference } from '@/api/dto'
import { mapThemePreference } from '@/lib/theme'

interface ThemePreferenceControlProps {
  /** `UserDto.theme` — null when never chosen, which the site treats as following the device. */
  theme: ThemePreference | null | undefined
}

/**
 * The colour scheme, with « Système » as a choice of its own — which the header's sun/moon toggle,
 * a two-state switch, cannot offer.
 *
 * The value is the account's, never Mantine's local state: the server renders it from the session,
 * where `useMantineColorScheme()` would read localStorage on the client and hydrate differently.
 */
export function ThemePreferenceControl({ theme }: ThemePreferenceControlProps) {
  const { t } = useTranslation()
  const queryClient = useQueryClient()
  const { setColorScheme } = useMantineColorScheme()
  const mutation = useUpdateMyPreferences()

  const handleChange = (value: string) => {
    const next = value as ThemePreference
    setColorScheme(mapThemePreference(next))
    // Partial PATCH: send only the field being changed, never the whole preference set.
    mutation.mutate(
      { data: { theme: next } },
      { onSuccess: (user) => queryClient.setQueryData(getGetMeQueryKey(), user) }
    )
  }

  return (
    <Stack gap="xs">
      <Text size="sm" fw={500}>
        {t('profile.preferences.theme.label')}
      </Text>
      <SegmentedControl
        value={theme ?? ThemePreference.SYSTEM}
        onChange={handleChange}
        disabled={mutation.isPending}
        data={[
          { value: ThemePreference.SYSTEM, label: t('profile.preferences.theme.SYSTEM') },
          { value: ThemePreference.LIGHT, label: t('profile.preferences.theme.LIGHT') },
          { value: ThemePreference.DARK, label: t('profile.preferences.theme.DARK') },
        ]}
      />
    </Stack>
  )
}
