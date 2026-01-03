import { Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import {
  CalendarIcon,
  UsersIcon,
  RectangleStackIcon,
  ChevronRightIcon,
} from '@heroicons/react/24/outline'
import { Card, CardContent, CardTitle, CardDescription } from '../common/card'
import { Badge, VisibilityBadge, Stat, StatGroup, CardSkeleton } from '../common/card'
import { EntityLogo } from '../common/EntityLogo'
import { useFormattedDate } from '../../utils/dateFormat'
import { paths } from '@/config/paths'
import type { PublicationDto, RideDto, TripDto } from '../../api/api'

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
  const { t: tCommon } = useTranslation('common')
  const { t: tTrips } = useTranslation('trips')
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
    return tCommon(`status.${publication.status satisfies 'DRAFT' | 'PUBLISHED' | 'CANCELLED'}`)
  }

  // Get the type translation
  const getTypeLabel = () => {
    switch (publication.type) {
      case 'RIDE':
        return tCommon('publicationType.ride')
      case 'POST':
        return tCommon('publicationType.post')
      case 'TRIP':
        return tCommon('publicationType.trip')
    }
  }

  const formattedDate = formatDateTime(publication.dateTime)

  // Render stats based on publication type
  const renderStats = () => {
    const calendarIcon = <CalendarIcon />
    const participantsIcon = <UsersIcon />
    const groupsIcon = <RectangleStackIcon />

    switch (publication.type) {
      case 'RIDE': {
        const ride = publication as RideDto
        return (
          <StatGroup>
            <Stat icon={calendarIcon}>{formattedDate}</Stat>
            <Stat icon={participantsIcon}>
              {tCommon('participantCount', { count: ride.participantCount })}
            </Stat>
            <Stat icon={groupsIcon}>
              {tCommon('groups.groupCount', { count: ride.groupCount })}
            </Stat>
          </StatGroup>
        )
      }
      case 'TRIP': {
        const trip = publication as TripDto
        return (
          <StatGroup>
            <Stat icon={calendarIcon}>{formattedDate}</Stat>
            <Stat icon={participantsIcon}>
              {tCommon('participantCount', { count: trip.participantCount })}
            </Stat>
            <Stat icon={groupsIcon}>{tTrips('card.stageCount', { count: trip.stageCount })}</Stat>
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
          <Link
            to={paths.team(publication.team.slug)}
            onClick={(e) => e.stopPropagation()}
            className="flex items-center gap-2 mb-3 text-sm text-gray-600 hover:text-indigo-600 transition-colors group"
          >
            <UsersIcon className="h-4 w-4" />
            <span className="font-medium">{publication.team.name}</span>
            <ChevronRightIcon className="h-3 w-3 opacity-0 -translate-x-1 group-hover:opacity-100 group-hover:translate-x-0 transition-all" />
          </Link>
        )}

        {/* Main content */}
        <div className="flex items-start justify-between mb-4">
          <div className="flex items-start gap-3 flex-1 min-w-0">
            <EntityLogo
              logo={publication.media.assets.logo}
              alt={publication.name}
              size="md"
              className="shrink-0"
            />
            <div className="flex-1 min-w-0">
              <CardTitle>{publication.name}</CardTitle>
              <CardDescription markdown={true} media={publication.media} />
            </div>
          </div>
          <div className="ml-3 flex flex-col items-end gap-1">
            <Badge variant={typeBadgeVariants[publication.type] || 'indigo'}>
              {getTypeLabel()}
            </Badge>
            <Badge variant={statusVariants[publication.status] || 'gray'}>{getStatusLabel()}</Badge>
            <VisibilityBadge visibility={publication.visibility} />
          </div>
        </div>

        {renderStats()}
      </CardContent>
    </Card>
  )
}

export function PublicationCardSkeleton() {
  return <CardSkeleton count={1} statCount={3} badgeCount={2} />
}
