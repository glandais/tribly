import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import {
  IconBolt,
  IconUsers,
  IconClock,
  IconMap,
  IconDownload,
  IconDeviceMobile,
  IconArrowsMaximize,
  IconArrowUp,
  IconShieldCheck,
  IconAlertTriangle,
} from '@tabler/icons-react'
import {
  Alert,
  Paper,
  Group,
  Text,
  Button,
  Badge,
  UnstyledButton,
  Anchor,
  Menu,
  Loader,
} from '@mantine/core'
import type { RideGroupDto, GpsServiceType, RouteDetailDto } from '@/api/dto'
import { UserAvatar, UserAvatarGroup } from '../common/UserAvatar'
import { ParticipantListModal } from './ParticipantListModal'
import { paths } from '@/config/paths'
import { useUnits } from '@/hooks/useUnits'
import { useAuth } from '@/hooks/useAuth'
import { useGpsConnections } from '@/hooks/useGpsConnections'

interface RideGroupCardProps {
  group: RideGroupDto
  teamSlug: string
  rideRouteSlug?: string
  /**
   * Every route referenced by the ride, keyed by slug — the parent (`RideDetailPage`) fetches
   * them all in one `getRoutesBulk` call and shares the map across every group card, rather
   * than each card issuing its own `getRoute`.
   */
  routesBySlug: Map<string, RouteDetailDto>
  canJoin?: boolean
  onJoin?: () => void
  onLeave?: () => void
  isLoading?: boolean
  onHover?: (groupId: string | null) => void
  isHighlighted?: boolean
  /** Why the last join/leave on THIS group failed. Persists until the next attempt. */
  error?: string
}

export function RideGroupCard({
  group,
  teamSlug,
  rideRouteSlug,
  routesBySlug,
  canJoin,
  onJoin,
  onLeave,
  isLoading,
  onHover,
  isHighlighted = false,
  error,
}: RideGroupCardProps) {
  const { t } = useTranslation()
  const { speed, distance, elevation } = useUnits()
  const { isAuthenticated } = useAuth()
  const { connectedServices, uploadRoute, isUploading } = useGpsConnections()
  const [showParticipants, setShowParticipants] = useState(false)
  // `full` and `registered` are computed server-side (contract 1.3.0) — never recompute them here.
  const isFull = group.full
  const isJoined = group.registered

  // Determine effective route slug (group route or ride route as fallback)
  const effectiveRouteSlug = group.routeSlug || rideRouteSlug

  // Distance and elevation come from the group itself (RideGroupDto). The route is only
  // needed to build the GPX/FIT links and the device upload; `routesBySlug` was fetched once
  // for every group of the ride in a single bulk call by the parent.
  const route = effectiveRouteSlug ? routesBySlug.get(effectiveRouteSlug) : undefined

  const handleSendToDevice = (serviceType: GpsServiceType) => {
    if (effectiveRouteSlug) {
      uploadRoute({ serviceType, teamSlug, routeSlug: effectiveRouteSlug })
    }
  }

  return (
    <Paper
      shadow={isHighlighted ? 'md' : 'xs'}
      p="md"
      withBorder
      style={{
        borderColor: isHighlighted
          ? 'var(--mantine-primary-color-filled)'
          : isJoined
            ? 'var(--mantine-primary-color-filled)'
            : undefined,
        backgroundColor: isHighlighted ? 'var(--mantine-primary-color-light)' : undefined,
        boxShadow: isJoined ? '0 0 0 1px var(--mantine-primary-color-filled)' : undefined,
        transition: 'all 150ms ease',
      }}
      onMouseEnter={() => onHover?.(group.id)}
      onMouseLeave={() => onHover?.(null)}
    >
      {/* Header row: title + badge + button */}
      <Group justify="space-between" wrap="nowrap">
        <Group gap="sm" style={{ minWidth: 0 }}>
          <Text fw={500} truncate>
            {group.name}
          </Text>
          {isJoined && (
            <Badge size="sm" color="primary" variant="light">
              {t('rides.detail.groups.joined')}
            </Badge>
          )}
        </Group>

        {(canJoin || isJoined) && (
          <>
            {isJoined ? (
              <Button variant="outline" size="xs" onClick={onLeave} disabled={isLoading}>
                {t('rides.detail.groups.leave')}
              </Button>
            ) : canJoin && !isFull ? (
              <Button size="xs" onClick={onJoin} disabled={isLoading}>
                {t('rides.detail.groups.join')}
              </Button>
            ) : isFull ? (
              <Badge color="gray" variant="light">
                {t('rides.detail.groups.full')}
              </Badge>
            ) : null}
          </>
        )}
      </Group>

      {/* Leader — rendered only when one is designated; no fallback on the ride's creator */}
      {group.leader && (
        <Group gap="xs" mt="xs" wrap="nowrap">
          <UserAvatar user={group.leader} size="sm" />
          <Text size="sm" truncate>
            {group.leader.displayName}
          </Text>
          <Badge
            size="sm"
            variant="light"
            leftSection={<IconShieldCheck size={12} />}
            style={{ flexShrink: 0 }}
          >
            {t('rides.detail.groups.leader')}
          </Badge>
        </Group>
      )}

      {/* Details row */}
      <Group mt="xs">
        {group.time && (
          <Group gap={4}>
            <IconClock size={16} />
            <Text size="sm" c="dimmed">
              {group.time}
            </Text>
          </Group>
        )}
        {group.averageSpeed && (
          <Group gap={4}>
            <IconBolt size={16} />
            <Text size="sm" c="dimmed">
              {speed(group.averageSpeed)}
            </Text>
          </Group>
        )}
        {group.distance !== undefined && (
          <Group gap="md" wrap="nowrap">
            <Group gap={4}>
              <IconArrowsMaximize size={16} />
              <Text size="sm" c="dimmed">
                {distance(group.distance)}
              </Text>
            </Group>
            {group.elevationGain !== undefined && (
              <Group gap={4}>
                <IconArrowUp size={16} />
                <Text size="sm" c="dimmed">
                  {elevation(group.elevationGain)}
                </Text>
              </Group>
            )}
          </Group>
        )}
        <UnstyledButton
          onClick={() => setShowParticipants(true)}
          title={t('rides.detail.groups.viewParticipants')}
          aria-label={t('rides.detail.groups.viewParticipants')}
          style={{ borderRadius: 'var(--mantine-radius-sm)' }}
        >
          <Group gap="sm">
            {group.participants.length > 0 && (
              <UserAvatarGroup users={group.participants} max={5} size="sm" />
            )}
            <Group gap={4}>
              <IconUsers size={16} />
              <Text size="sm" c="dimmed">
                {group.maxParticipants
                  ? t('rides.detail.groups.participants', {
                      current: group.countParticipants,
                      max: group.maxParticipants,
                    })
                  : t('rides.detail.groups.participantsNoMax', {
                      current: group.countParticipants,
                    })}
              </Text>
            </Group>
            {group.participants.length > 5 && (
              <Text size="xs" fw={500} c="var(--mantine-primary-color-filled)">
                {t('rides.detail.groups.viewAll')}
              </Text>
            )}
          </Group>
        </UnstyledButton>
      </Group>

      {/* Route actions */}
      {effectiveRouteSlug && (
        <Group mt="sm" gap="xs">
          <Anchor
            component={Link}
            to={paths.route(teamSlug, effectiveRouteSlug)}
            size="xs"
            c="dimmed"
          >
            <Group gap={4}>
              <IconMap size={16} />
              {t('rides.detail.groups.route.view')}
            </Group>
          </Anchor>
          {route?.media?.assets?.gpx?.url && (
            <Anchor href={route.media.assets.gpx.url} size="xs" c="dimmed" download>
              <Group gap={4}>
                <IconDownload size={16} />
                {t('rides.detail.groups.route.downloadGpx')}
              </Group>
            </Anchor>
          )}
          {route?.media?.assets?.fit?.url && (
            <Anchor href={route.media.assets.fit.url} size="xs" c="dimmed" download>
              <Group gap={4}>
                <IconDownload size={16} />
                {t('rides.detail.groups.route.downloadFit')}
              </Group>
            </Anchor>
          )}
          {isAuthenticated && connectedServices.length > 0 && (
            <Menu shadow="md" width={200}>
              <Menu.Target>
                <UnstyledButton disabled={isUploading} aria-label={t('routes.detail.sendToDevice')}>
                  <Group gap={4}>
                    {isUploading ? <Loader size={16} /> : <IconDeviceMobile size={16} />}
                    <Text size="xs" c="dimmed">
                      {t('routes.detail.sendToDevice')}
                    </Text>
                  </Group>
                </UnstyledButton>
              </Menu.Target>
              <Menu.Dropdown>
                {connectedServices.map((service) => (
                  <Menu.Item
                    key={service.serviceType}
                    onClick={() => handleSendToDevice(service.serviceType)}
                  >
                    {t(`gps.services.${service.serviceType.toLowerCase() as 'hammerhead'}`)}
                  </Menu.Item>
                ))}
              </Menu.Dropdown>
            </Menu>
          )}
        </Group>
      )}

      {error && (
        <Alert color="red" variant="light" mt="sm" icon={<IconAlertTriangle size={16} />}>
          {error}
        </Alert>
      )}

      <ParticipantListModal
        isOpen={showParticipants}
        onClose={() => setShowParticipants(false)}
        participants={group.participants}
        groupName={group.name}
        leaderId={group.leader?.id}
      />
    </Paper>
  )
}
