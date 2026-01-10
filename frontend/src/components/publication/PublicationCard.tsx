import { Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { IconCalendar, IconUsers, IconStack2, IconChevronRight } from '@tabler/icons-react'
import { Group, Box, Stack, Anchor, Text } from '@mantine/core'
import { Card, CardContent, CardTitle, CardDescription } from '../common/card'
import { Badge, VisibilityBadge, Stat, StatGroup, CardSkeleton } from '../common/card'
import { EntityLogo } from '../common/EntityLogo'
import { useFormattedDate } from '../../utils/dateFormat'
import { paths } from '@/config/paths'
import type { PublicationDto, RideDto, TripDto } from '@/api/dto'

// Status variants for badges
const statusVariants: Record<string, 'gray' | 'green' | 'red'> = {
  DRAFT: 'gray',
  PUBLISHED: 'green',
  CANCELLED: 'red',
}

// Type badge variants
const typeBadgeVariants: Record<string, 'indigo' | 'purple'> = {
  RIDE: 'indigo',
  POST: 'purple',
  TRIP: 'indigo',
}

interface PublicationCardProps {
  publication: PublicationDto
  showTeam: boolean
}

export function PublicationCard({ publication, showTeam }: PublicationCardProps) {
  const { t } = useTranslation()
  const { formatDateTime } = useFormattedDate()

  // Get the appropriate path based on publication type
  const getPublicationPath = () => {
    switch (publication.type) {
      case 'RIDE':
        return paths.ride(publication.team.slug, publication.slug)
      case 'POST':
        return paths.post(publication.team.slug, publication.slug)
      case 'TRIP':
        return paths.trip(publication.team.slug, publication.slug)
    }
  }

  // Get the status translation - consolidated in common
  const getStatusLabel = () => {
    return t(`status.${publication.status satisfies 'DRAFT' | 'PUBLISHED' | 'CANCELLED'}`)
  }

  // Get the type translation
  const getTypeLabel = () => {
    switch (publication.type) {
      case 'RIDE':
        return t('publicationType.ride')
      case 'POST':
        return t('publicationType.post')
      case 'TRIP':
        return t('publicationType.trip')
    }
  }

  const formattedDate = formatDateTime(publication.dateTime)

  // Render stats based on publication type
  const renderStats = () => {
    const calendarIcon = <IconCalendar size={16} />
    const participantsIcon = <IconUsers size={16} />
    const groupsIcon = <IconStack2 size={16} />

    switch (publication.type) {
      case 'RIDE': {
        const ride = publication as RideDto
        return (
          <StatGroup>
            <Stat icon={calendarIcon}>{formattedDate}</Stat>
            <Stat icon={participantsIcon}>
              {t('participantCount', { count: ride.participantCount })}
            </Stat>
            <Stat icon={groupsIcon}>{t('groups.groupCount', { count: ride.groupCount })}</Stat>
          </StatGroup>
        )
      }
      case 'TRIP': {
        const trip = publication as TripDto
        return (
          <StatGroup>
            <Stat icon={calendarIcon}>{formattedDate}</Stat>
            <Stat icon={participantsIcon}>
              {t('participantCount', { count: trip.participantCount })}
            </Stat>
            <Stat icon={groupsIcon}>{t('trips.card.stageCount', { count: trip.stageCount })}</Stat>
          </StatGroup>
        )
      }
      case 'POST':
        return (
          <StatGroup>
            <Stat icon={calendarIcon}>{formattedDate}</Stat>
          </StatGroup>
        )
    }
  }

  return (
    <Card to={getPublicationPath()}>
      <CardContent>
        {/* Team header - clickable link to team page */}
        {showTeam && (
          <Anchor
            component={Link}
            to={paths.team(publication.team.slug)}
            onClick={(e) => e.stopPropagation()}
            underline="hover"
            mb="sm"
            style={{ display: 'block' }}
          >
            <Group gap="xs">
              <IconUsers size={16} />
              <Text size="sm" fw={500}>
                {publication.team.name}
              </Text>
              <IconChevronRight size={12} />
            </Group>
          </Anchor>
        )}

        {/* Main content */}
        <Group align="flex-start" justify="space-between" mb="md" wrap="nowrap">
          <Group align="flex-start" gap="sm" style={{ flex: 1, minWidth: 0 }}>
            <EntityLogo logo={publication.media.assets.logo} alt={publication.name} size="md" />
            <Box style={{ flex: 1, minWidth: 0 }}>
              <CardTitle>{publication.name}</CardTitle>
              <CardDescription markdown={true} media={publication.media} />
            </Box>
          </Group>
          <Stack gap={4} align="flex-end" ml="sm">
            <Badge variant={typeBadgeVariants[publication.type] || 'indigo'}>
              {getTypeLabel()}
            </Badge>
            <Badge variant={statusVariants[publication.status] || 'gray'}>{getStatusLabel()}</Badge>
            <VisibilityBadge visibility={publication.visibility} />
          </Stack>
        </Group>

        {renderStats()}
      </CardContent>
    </Card>
  )
}

export function PublicationCardSkeleton() {
  return <CardSkeleton count={1} statCount={3} badgeCount={2} />
}
