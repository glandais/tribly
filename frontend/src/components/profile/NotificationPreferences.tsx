import { useEffect, useRef } from 'react'
import { useTranslation } from 'react-i18next'
import { Alert, Divider, Group, Stack, Switch, Table, Text, Title } from '@mantine/core'
import { IconAlertCircle } from '@tabler/icons-react'
import {
  useGetMyNotificationPreferences,
  useUpdateMyNotificationPreferences,
  getGetMyNotificationPreferencesQueryKey,
} from '@/api/endpoints/notifications/notifications'
import { useQueryClient } from '@tanstack/react-query'
import { NotificationChannel, NotificationType } from '@/api/dto'
import type { NotificationPreferencesDto } from '@/api/dto'

/** The fragment notification e-mails link to — `NotificationLinks.PREFERENCES_PATH`, server-side. */
const ANCHOR = 'notifications'

/** The order the matrix lists its rows in — the enum's own, not the API response's. */
const TYPE_ORDER = [
  NotificationType.RIDE_PUBLISHED,
  NotificationType.RIDE_CANCELLED,
  NotificationType.TRIP_PUBLISHED,
  NotificationType.TRIP_CANCELLED,
  NotificationType.POST_PUBLISHED,
  NotificationType.COMMENT_REPLY,
] as const

function cellOf(
  preferences: NotificationPreferencesDto,
  type: NotificationType,
  channel: NotificationChannel
) {
  return preferences.preferences.find((cell) => cell.type === type && cell.channel === channel)
}

/**
 * The type × channel matrix of `GET /api/notifications/preferences`.
 *
 * Only the **channels the server says are configurable** get a column: `IN_APP` never appears (the
 * inbox is always on, and `PUT` refuses it), and `PUSH` only once phase 4 ships an emitter. When
 * that list comes back empty, the whole section is hidden rather than rendered as a matrix with no
 * columns — see `docs/plans/2026-09-18-notifications.md` §5.
 */
export function NotificationPreferences() {
  const { t } = useTranslation()
  const queryClient = useQueryClient()
  const { data, isLoading } = useGetMyNotificationPreferences()
  const mutation = useUpdateMyNotificationPreferences({
    mutation: {
      onSuccess: () =>
        queryClient.invalidateQueries({ queryKey: getGetMyNotificationPreferencesQueryKey() }),
    },
  })

  const visible = !isLoading && !!data && data.channels.length > 0

  // An e-mail's "choose your notifications" link lands on /profile#notifications, but the section
  // only exists once the preferences have loaded — by which time the browser has long given up on
  // the fragment. Scroll to it ourselves, once, when it appears.
  const scrolled = useRef(false)
  useEffect(() => {
    if (!visible || scrolled.current) return
    if (window.location.hash !== `#${ANCHOR}`) return
    scrolled.current = true
    document.getElementById(ANCHOR)?.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }, [visible])

  // Nothing to configure on this server: no header, no empty table.
  if (!visible || !data) return null

  const toggle = (type: NotificationType, channel: NotificationChannel, enabled: boolean) => {
    // One cell per call: the endpoint takes a list of overrides, and sending the whole matrix
    // would turn every default into an explicit override the user never chose.
    mutation.mutate({ data: { preferences: [{ type, channel, enabled }] } })
  }

  // This section carries its own leading `Divider`, unlike its neighbours on the profile page:
  // it is the only one that can render nothing at all, and a divider left behind by the page
  // would show up as a double rule above Passkeys.
  // `scrollMarginTop`: the sticky header would otherwise sit on top of the section's own title.
  return (
    <>
      <Divider />
      <Stack id={ANCHOR} style={{ scrollMarginTop: 80 }}>
        <Title order={3} size="h5">
          {t('notifications.preferences.title')}
        </Title>
        <Text size="sm" c="dimmed">
          {t('notifications.preferences.description')}
        </Text>

        <Alert variant="light" color="blue" icon={<IconAlertCircle size={16} />}>
          {t('notifications.preferences.inAppAlwaysOn')}
        </Alert>

        <Table.ScrollContainer minWidth={360}>
          <Table verticalSpacing="xs">
            <Table.Thead>
              <Table.Tr>
                <Table.Th>{t('notifications.preferences.columnType')}</Table.Th>
                {data.channels.map((channel) => (
                  <Table.Th key={channel} w={120}>
                    {t(`notifications.channel.${channel satisfies NotificationChannel}`)}
                  </Table.Th>
                ))}
              </Table.Tr>
            </Table.Thead>
            <Table.Tbody>
              {TYPE_ORDER.map((type) => (
                <Table.Tr key={type}>
                  <Table.Td>
                    <Text size="sm">
                      {t(`notifications.typeLabel.${type satisfies NotificationType}`)}
                    </Text>
                  </Table.Td>
                  {data.channels.map((channel) => {
                    const cell = cellOf(data, type, channel)
                    if (!cell) return <Table.Td key={channel} />
                    return (
                      <Table.Td key={channel}>
                        <Group gap="xs" wrap="nowrap">
                          <Switch
                            checked={cell.enabled}
                            disabled={mutation.isPending}
                            onChange={(event) => toggle(type, channel, event.currentTarget.checked)}
                            aria-label={t('notifications.preferences.toggleAriaLabel', {
                              type: t(`notifications.typeLabel.${type satisfies NotificationType}`),
                              channel: t(
                                `notifications.channel.${channel satisfies NotificationChannel}`
                              ),
                            })}
                          />
                        </Group>
                      </Table.Td>
                    )
                  })}
                </Table.Tr>
              ))}
            </Table.Tbody>
          </Table>
        </Table.ScrollContainer>
      </Stack>
    </>
  )
}
