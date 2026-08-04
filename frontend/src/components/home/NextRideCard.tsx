import { useState } from 'react'
import { PrefetchLink } from '@/components/common/PrefetchLink'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { notifications } from '@mantine/notifications'
import { Badge, Box, Button, Group, Paper, Stack, Title } from '@mantine/core'
import {
  IconArrowsMaximize,
  IconArrowUp,
  IconCalendar,
  IconCheck,
  IconClock,
  IconUsers,
} from '@tabler/icons-react'
import type { RideDto } from '@/api/dto'
import { useLeaveGroup } from '@/api/endpoints/rides/rides'
import { getListMyParticipationsQueryKey } from '@/api/endpoints/users/users'
import { CardImage, CardTeamLink, Stat, StatGroup } from '../card/common'
import { UserAvatarGroup } from '../common/UserAvatar'
import { ConfirmDialog } from '../common/ConfirmDialog'
import { paths } from '@/config/paths'
import { useUnits } from '@/hooks/useUnits'
import { useFormattedDate } from '@/utils/dateFormat'
import { FormattedDateTime } from '../common/FormattedDate'

interface NextRideCardProps {
  ride: RideDto
}

/**
 * "Ma prochaine sortie" — the ride the user is registered for that comes next.
 *
 * Metrics come from the group the user actually joined (`registeredGroupId`)
 * and fall back on the ride when that group cannot be resolved.
 */
export function NextRideCard({ ride }: NextRideCardProps) {
  const { t } = useTranslation()
  const { distance, elevation } = useUnits()
  const { formatRelative } = useFormattedDate()
  const queryClient = useQueryClient()
  const leaveMutation = useLeaveGroup()
  const [showLeaveConfirm, setShowLeaveConfirm] = useState(false)

  const group = ride.groups?.find((g) => g.id === ride.registeredGroupId)
  const rideDistance = group?.distance
  const rideElevation = group?.elevationGain
  const ridePath = paths.ride(ride.team.slug, ride.slug)

  const handleLeave = () => {
    if (!ride.registeredGroupId) return
    leaveMutation.mutate(
      { teamSlug: ride.team.slug, rideSlug: ride.slug, groupId: ride.registeredGroupId },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getListMyParticipationsQueryKey() })
          notifications.show({ message: t('rides.notifications.left'), color: 'green' })
          setShowLeaveConfirm(false)
        },
      }
    )
  }

  return (
    <Paper withBorder radius="md" style={{ overflow: 'hidden' }}>
      <CardImage media={ride.media} alt={ride.name} type="RIDE" height={200} />

      <Stack gap="xs" p="md">
        <Group gap={4}>
          <Badge size="sm" color="primary" variant="light" leftSection={<IconCheck size={12} />}>
            {t('publications.registered')}
          </Badge>
          <Badge size="sm" color="gray" variant="light">
            {formatRelative(ride.dateTime)}
          </Badge>
        </Group>

        <CardTeamLink teamSlug={ride.team.slug} teamName={ride.team.name} />

        <Title order={4} lineClamp={1}>
          {ride.name}
        </Title>

        <Stack gap={6} mt={4}>
          <Stat icon={<IconCalendar size={16} />}>
            <FormattedDateTime date={ride.dateTime} />
          </Stat>
          {group && (
            <Stat icon={<IconClock size={16} />}>
              {group.time
                ? t('home.nextRide.groupAndTime', { group: group.name, time: group.time })
                : group.name}
            </Stat>
          )}
          {ride.startPlace && (
            <Stat icon={<Box w={10} h={10} bg="var(--mantine-color-green-6)" style={dotStyle} />}>
              {ride.startPlace.name}
            </Stat>
          )}
        </Stack>

        <StatGroup>
          {rideDistance !== undefined && (
            <Stat icon={<IconArrowsMaximize size={16} />}>{distance(rideDistance)}</Stat>
          )}
          {rideElevation !== undefined && (
            <Stat icon={<IconArrowUp size={16} />}>{elevation(rideElevation)}</Stat>
          )}
          <Stat icon={<IconUsers size={16} />}>
            {group?.maxParticipants
              ? t('rides.detail.groups.participants', {
                  current: group.countParticipants,
                  max: group.maxParticipants,
                })
              : t('rides.detail.groups.participantsNoMax', {
                  current: group?.countParticipants ?? ride.participantCount,
                })}
          </Stat>
        </StatGroup>

        {ride.topParticipants.length > 0 && (
          <UserAvatarGroup users={ride.topParticipants} max={5} size="sm" />
        )}

        <Group gap="xs" mt="xs">
          <Button component={PrefetchLink} to={ridePath} style={{ flex: 1 }}>
            {t('home.nextRide.view')}
          </Button>
          {ride.registeredGroupId && (
            <Button variant="outline" onClick={() => setShowLeaveConfirm(true)}>
              {t('home.nextRide.leave')}
            </Button>
          )}
        </Group>
      </Stack>

      <ConfirmDialog
        isOpen={showLeaveConfirm}
        onClose={() => setShowLeaveConfirm(false)}
        onConfirm={handleLeave}
        title={t('home.nextRide.leaveConfirm.title')}
        message={t('home.nextRide.leaveConfirm.message', { name: ride.name })}
        confirmText={t('home.nextRide.leave')}
        variant="warning"
        isLoading={leaveMutation.isPending}
      />
    </Paper>
  )
}

const dotStyle = { borderRadius: '50%', flexShrink: 0 }
