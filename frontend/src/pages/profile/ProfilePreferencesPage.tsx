import { useTranslation } from 'react-i18next'
import { Center, Loader, Stack } from '@mantine/core'
import { LanguageSwitcher } from '@/components/common/LanguageSwitcher'
import { UnitSystemSwitcher } from '@/components/common/UnitSystemSwitcher'
import { ProfileCard, ProfileShell } from '@/components/profile/ProfileShell'
import { ThemePreferenceControl } from '@/components/profile/ThemePreferenceControl'
import { TimezonePreference } from '@/components/profile/TimezonePreference'
import { useAuth } from '@/hooks/useAuth'

/**
 * « Préférences »: units, timezone, theme and language — the same four as in the app. Theme and
 * language stay in the header too, where an anonymous visitor finds them.
 */
export function ProfilePreferencesPage() {
  const { t } = useTranslation()
  const { user, isLoading } = useAuth()

  if (isLoading || !user) {
    return (
      <Center py="xl">
        <Loader size="lg" />
      </Center>
    )
  }

  return (
    <ProfileShell section="preferences" title={t('profile.nav.preferences')}>
      <ProfileCard>
        <Stack gap="lg">
          <UnitSystemSwitcher />
          <TimezonePreference timezone={user.timezone} />
          <ThemePreferenceControl theme={user.theme} />
          <LanguageSwitcher label={t('profile.preferences.language.label')} />
        </Stack>
      </ProfileCard>
    </ProfileShell>
  )
}
