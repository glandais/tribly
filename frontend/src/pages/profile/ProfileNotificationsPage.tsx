import { useTranslation } from 'react-i18next'
import { Button } from '@mantine/core'
import { IconInbox } from '@tabler/icons-react'
import { PrefetchLink } from '@/components/common/PrefetchLink'
import { NotificationPreferences } from '@/components/profile/NotificationPreferences'
import { ProfileShell } from '@/components/profile/ProfileShell'
import { paths } from '@/config/paths'

/**
 * « Notifications » of the profile: what interrupts the member, by e-mail and push. Every
 * notification e-mail links here (`NotificationLinks.PREFERENCES_PATH`), and the inbox too; this
 * page links back to the inbox.
 */
export function ProfileNotificationsPage() {
  const { t } = useTranslation()
  return (
    <ProfileShell
      section="notifications"
      title={t('profile.nav.notifications')}
      description={t('notifications.preferences.inAppAlwaysOn')}
      actions={
        <Button
          component={PrefetchLink}
          to={paths.notifications()}
          variant="default"
          size="sm"
          leftSection={<IconInbox size={16} />}
        >
          {t('notifications.preferences.openInbox')}
        </Button>
      }
    >
      <NotificationPreferences />
    </ProfileShell>
  )
}
