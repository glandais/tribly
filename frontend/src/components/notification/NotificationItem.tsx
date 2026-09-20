import { useTranslation } from 'react-i18next'
import { Box, Group, Stack, Text, ThemeIcon, UnstyledButton } from '@mantine/core'
import { PrefetchLink } from '@/components/common/PrefetchLink'
import { useFormattedDate } from '@/utils/dateFormat'
import type { NotificationDto } from '@/api/dto'
import { NotificationType } from '@/api/dto'
import { notificationColor, notificationIcon, notificationPath } from './notificationDisplay'

interface NotificationItemProps {
  notification: NotificationDto
  /** Marks it read. Called before navigating, so opening one clears its dot. */
  onOpen: (notification: NotificationDto) => void
  /** The bell's dropdown is tighter than the page's list. */
  compact?: boolean
}

/**
 * One inbox entry, shared by the bell dropdown and the notifications page.
 *
 * The wording is built here from the type and the structured fields — the API sends no rendered
 * text (`docs/plans/2026-09-18-notifications.md` §3). An unread entry is marked by a dot and a
 * heavier title rather than a background tint: the row is a link, and a tinted link fights the
 * hover state.
 */
export function NotificationItem({ notification, onOpen, compact = false }: NotificationItemProps) {
  const { t } = useTranslation()
  const { formatRelative } = useFormattedDate()

  const Icon = notificationIcon(notification.type)
  const color = notificationColor(notification.type)

  // The subject's own name carries the "what", so the sentence only has to say the "why".
  const title = t(
    `notifications.type.${notification.type satisfies NotificationType}`,
    // `actorName` is absent for a scheduled publication: the fallback keeps the sentence whole
    // rather than rendering "undefined".
    {
      actor: notification.actorName ?? notification.teamName,
      team: notification.teamName,
    }
  )

  return (
    <UnstyledButton
      component={PrefetchLink}
      to={notificationPath(notification)}
      onClick={() => onOpen(notification)}
      p={compact ? 'xs' : 'sm'}
      style={{ borderRadius: 'var(--mantine-radius-md)', display: 'block' }}
    >
      <Group gap="sm" wrap="nowrap" align="flex-start">
        <ThemeIcon variant="light" color={color} size={compact ? 'md' : 'lg'} radius="xl">
          <Icon size={compact ? 16 : 18} />
        </ThemeIcon>
        <Stack gap={2} style={{ flex: 1, minWidth: 0 }}>
          <Text size="sm" fw={notification.read ? 400 : 600} lineClamp={2}>
            {title}
          </Text>
          <Text size="sm" c="dimmed" lineClamp={compact ? 1 : 2}>
            {notification.subjectName}
          </Text>
          {notification.excerpt && (
            <Text size="xs" c="dimmed" fs="italic" lineClamp={2}>
              {notification.excerpt}
            </Text>
          )}
          {/* "il y a 2 minutes" is read off the clock, not off a timezone: server and client
              legitimately render different text a few seconds apart. */}
          <Text size="xs" c="dimmed" suppressHydrationWarning>
            {t('notifications.item.meta', {
              team: notification.teamName,
              when: formatRelative(notification.createdAt),
            })}
          </Text>
        </Stack>
        {!notification.read && (
          <Box
            w={8}
            h={8}
            mt={6}
            style={{ borderRadius: '50%', backgroundColor: `var(--mantine-color-${color}-filled)` }}
            aria-label={t('notifications.item.unread')}
          />
        )}
      </Group>
    </UnstyledButton>
  )
}
