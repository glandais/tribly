import { useTranslation } from 'react-i18next'
import { IconCalendar, IconUsers, IconStack2 } from '@tabler/icons-react'
import { Group, Box, Stack } from '@mantine/core'
import { Card, CardContent, CardTitle, CardDescription, CardImage, CardTeamLink } from './common'
import { Badge, VisibilityBadge, Stat, StatGroup, CardSkeleton } from './common'
import { UserAvatarGroup } from '../common/UserAvatar'
import { useFormattedDate } from '../../utils/dateFormat'
import { paths } from '@/config/paths'
import { PublicationCardProgress } from './PublicationCardProgress'
import { RouteThumbnail } from '../route/RouteThumbnail'
import type { PublicationDto, RideDto, TripDto } from '@/api/dto'

// Status variants for badges
const statusVariants: Record<string, 'gray' | 'green' | 'red'> = {
  DRAFT: 'gray',
  PUBLISHED: 'green',
  CANCELLED: 'red',
}

// Type badge variants
const typeBadgeVariants: Record<string, 'primary' | 'purple'> = {
  RIDE: 'primary',
  POST: 'purple',
  TRIP: 'primary',
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

  // Get participants for avatar display
  const getParticipants = () => {
    if (publication.type === 'TRIP') {
      return (publication as TripDto).participants || []
    }
    if (publication.type === 'RIDE') {
      return (publication as RideDto).topParticipants || []
    }
    return []
  }

  // Get route thumbnail URL for rides and trips
  const getRouteThumbnailUrl = () => {
    if (publication.type === 'RIDE') {
      return (publication as RideDto).routeThumbnailUrl
    }
    if (publication.type === 'TRIP') {
      return (publication as TripDto).routeThumbnailUrl
    }
    return undefined
  }

  const participants = getParticipants()
  const routeThumbnailUrl = getRouteThumbnailUrl()

  return (
    <Card to={getPublicationPath()}>
      {/* Featured image with fallback chain */}
      <CardImage
        media={publication.media}
        alt={publication.name}
        type={publication.type}
        height={160}
      />

      <CardContent>
        {showTeam && (
          <CardTeamLink teamSlug={publication.team.slug} teamName={publication.team.name} />
        )}

        {/* Main content - title, description, badges */}
        <Group align="flex-start" justify="space-between" mb="md" wrap="nowrap">
          <Box style={{ flex: 1, minWidth: 0 }}>
            <CardTitle>{publication.name}</CardTitle>
            <CardDescription markdown={true} media={publication.media} />
          </Box>
          <Stack gap={4} align="flex-end" ml="sm">
            <Badge variant={typeBadgeVariants[publication.type] || 'primary'}>
              {getTypeLabel()}
            </Badge>
            <Badge variant={statusVariants[publication.status] || 'gray'}>{getStatusLabel()}</Badge>
            <VisibilityBadge visibility={publication.visibility} />
          </Stack>
        </Group>

        {/* Participants section - avatars, progress, and route thumbnail */}
        {(participants.length > 0 || publication.type === 'RIDE' || routeThumbnailUrl) && (
          <Group justify="space-between" align="center" mb="md" wrap="nowrap">
            <Group style={{ flex: 1 }}>
              {participants.length > 0 && (
                <UserAvatarGroup users={participants} max={5} size="sm" />
              )}
              {publication.type === 'RIDE' && (
                <PublicationCardProgress ride={publication as RideDto} />
              )}
            </Group>
            {routeThumbnailUrl && <RouteThumbnail thumbnailUrl={routeThumbnailUrl} size="sm" />}
          </Group>
        )}

        {renderStats()}
      </CardContent>
    </Card>
  )
}

export function PublicationCardSkeleton() {
  return <CardSkeleton count={1} statCount={3} badgeCount={2} />
}
