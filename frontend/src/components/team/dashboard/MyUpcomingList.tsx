import { useTranslation } from 'react-i18next'
import { Anchor, Badge, Box, Button, Group, Paper, Stack, Text } from '@mantine/core'
import {
  IconCalendar,
  IconCheck,
  IconClock,
  IconMapPin,
  IconUsers,
  IconRoute,
} from '@tabler/icons-react'
import { PrefetchLink } from '@/components/common/PrefetchLink'
import { Stat, TypeBadge } from '@/components/card/common'
import type { PublicationDto, RideDto, TripDto } from '@/api/dto'
import { useUnits } from '@/hooks/useUnits'
import { useFormattedDate } from '@/utils/dateFormat'
import { isTrip, publicationPath, shortTime } from './dashboardHelpers'
import { SendToDeviceMenu } from './SendToDeviceMenu'

/**
 * « Vos prochaines sorties »: the team's rides and trips the user is registered to, soonest first.
 * A ride's group is `registeredGroup` — the list row carries it although `groups` is empty — with
 * its name and pace. No leader here: it belongs to the ride page.
 */
export function MyUpcomingList({ publications }: { publications: PublicationDto[] }) {
  return (
    <Stack gap="sm">
      {publications.map((publication) =>
        isTrip(publication) ? (
          <TripRow key={publication.id} trip={publication} />
        ) : publication.type === 'RIDE' ? (
          <RideRow key={publication.id} ride={publication as RideDto} />
        ) : null
      )}
    </Stack>
  )
}

/** The day box on the left of a row: « sam. / 10 / oct. ». */
function DayBox({ date }: { date: string }) {
  const { formatPattern } = useFormattedDate()
  return (
    <Box
      w={60}
      py={8}
      ta="center"
      style={{
        flex: '0 0 60px',
        borderRadius: 'var(--mantine-radius-md)',
        background: 'var(--mantine-color-primary-light)',
        color: 'var(--mantine-color-primary-light-color)',
      }}
    >
      <Text size="xs" tt="uppercase" fw={600} suppressHydrationWarning>
        {formatPattern(date, 'EEE')}
      </Text>
      <Text fz={22} fw={700} lh={1.1} suppressHydrationWarning>
        {formatPattern(date, 'd')}
      </Text>
      <Text size="xs" tt="uppercase" suppressHydrationWarning>
        {formatPattern(date, 'MMM')}
      </Text>
    </Box>
  )
}

function RegisteredBadge() {
  const { t } = useTranslation()
  return (
    <Badge color="primary" variant="light" size="sm" leftSection={<IconCheck size={12} />}>
      {t('publications.registered')}
    </Badge>
  )
}

function RideRow({ ride }: { ride: RideDto }) {
  const { t } = useTranslation()
  const { speed } = useUnits()
  const { formatPattern } = useFormattedDate()
  const group = ride.registeredGroup
  const routeSlug = group?.routeSlug ?? ride.routeSlug
  const groupTime = shortTime(group?.time)

  return (
    <Paper withBorder radius="md" p="md">
      <Group wrap="wrap" gap="md" align="center">
        <DayBox date={ride.dateTime} />
        <Stack gap={4} style={{ flex: '1 1 220px', minWidth: 0 }}>
          <Group gap="xs" wrap="wrap">
            <Anchor component={PrefetchLink} to={publicationPath(ride)} fw={600} c="inherit">
              {ride.name}
            </Anchor>
            <RegisteredBadge />
          </Group>
          <Group gap="md" wrap="wrap">
            <Stat icon={<IconClock size={16} />}>
              <span suppressHydrationWarning>{groupTime ?? formatPattern(ride.dateTime, 'p')}</span>
            </Stat>
            {ride.startPlace && <Stat icon={<IconMapPin size={16} />}>{ride.startPlace.name}</Stat>}
            {group && (
              <Stat icon={<IconUsers size={16} />}>
                {group.averageSpeed
                  ? t('teams.dashboard.groupPace', {
                      group: group.name,
                      pace: speed(group.averageSpeed),
                    })
                  : group.name}
              </Stat>
            )}
          </Group>
        </Stack>
        <Group gap="xs">
          {routeSlug && <SendToDeviceMenu teamSlug={ride.team.slug} routeSlug={routeSlug} />}
          <Button component={PrefetchLink} to={publicationPath(ride)} variant="light" size="xs">
            {t('home.nextRide.view')}
          </Button>
        </Group>
      </Group>
    </Paper>
  )
}

function TripRow({ trip }: { trip: TripDto }) {
  const { t } = useTranslation()
  const { distance } = useUnits()
  const { formatPattern } = useFormattedDate()

  const range = trip.endDate
    ? `${formatPattern(trip.dateTime, 'd MMM')} – ${formatPattern(trip.endDate, 'd MMM')}`
    : formatPattern(trip.dateTime, 'd MMM')

  return (
    <Paper withBorder radius="md" p="md">
      <Group wrap="wrap" gap="md" align="center">
        <DayBox date={trip.dateTime} />
        <Stack gap={4} style={{ flex: '1 1 220px', minWidth: 0 }}>
          <Group gap="xs" wrap="wrap">
            <Anchor component={PrefetchLink} to={publicationPath(trip)} fw={600} c="inherit">
              {trip.name}
            </Anchor>
            <TypeBadge type="TRIP">{t('publicationType.trip')}</TypeBadge>
            <RegisteredBadge />
          </Group>
          <Group gap="md" wrap="wrap">
            <Stat icon={<IconCalendar size={16} />}>
              <span suppressHydrationWarning>{range}</span>
            </Stat>
            <Stat icon={<IconRoute size={16} />}>
              {t('trips.card.stageCount', { count: trip.stageCount })}
            </Stat>
            {trip.totalDistance !== undefined && (
              <Stat icon={<IconRoute size={16} />}>{distance(trip.totalDistance)}</Stat>
            )}
          </Group>
        </Stack>
        <Button component={PrefetchLink} to={publicationPath(trip)} variant="light" size="xs">
          {t('teams.dashboard.myUpcoming.viewTrip')}
        </Button>
      </Group>
    </Paper>
  )
}
