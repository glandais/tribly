import { useQueryClient } from '@tanstack/react-query'
import { useTranslation } from 'react-i18next'
import { Stack, Switch, Text } from '@mantine/core'
import { getGetMeQueryKey, useUpdateMyPreferences } from '@/api/endpoints/users/users'

interface ContactPreferenceProps {
  /** `UserDto.contactableByMembers` — required by the contract, `true` on legacy accounts. */
  contactableByMembers: boolean
}

/**
 * Opt out of the classified-ad contact relay.
 *
 * This is the first preference the web persists server-side: `preferencesStore` was purely
 * local, so nothing here read or wrote `PATCH /api/users/me/preferences` before.
 */
export function ContactPreference({ contactableByMembers }: ContactPreferenceProps) {
  const { t } = useTranslation()
  const queryClient = useQueryClient()
  const mutation = useUpdateMyPreferences()

  const handleChange = (checked: boolean) => {
    // Partial PATCH: send only the field being changed, never the whole preference set.
    mutation.mutate(
      { data: { contactableByMembers: checked } },
      { onSuccess: () => queryClient.invalidateQueries({ queryKey: getGetMeQueryKey() }) }
    )
  }

  return (
    <Stack gap={4}>
      <Switch
        checked={contactableByMembers}
        onChange={(event) => handleChange(event.currentTarget.checked)}
        disabled={mutation.isPending}
        label={t('profile.contactable.label')}
      />
      <Text size="xs" c="dimmed">
        {t('profile.contactable.description')}
      </Text>
    </Stack>
  )
}
