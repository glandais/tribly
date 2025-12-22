import { useTranslation } from 'react-i18next'
import { CalendarIcon, UsersIcon, RectangleStackIcon } from '@heroicons/react/24/outline'
import { RideStatus } from '../../hooks/useRide'
import type { RideDto } from '../../hooks/useRide'
import { Card, CardContent, CardTitle, CardDescription } from '../common/card'
import { Badge, VisibilityBadge, Stat, StatGroup, CardSkeleton } from '../common/card'

interface RideCardProps {
  ride: RideDto
  teamSlug: string
}

const statusVariants: Record<RideStatus, 'gray' | 'green' | 'red'> = {
  [RideStatus.Draft]: 'gray',
  [RideStatus.Published]: 'green',
  [RideStatus.Cancelled]: 'red',
}

export function RideCard({ ride, teamSlug }: RideCardProps) {
  const { t, i18n } = useTranslation('rides')
  const rideDate = new Date(ride.dateTime)
  const formattedDate = rideDate.toLocaleString(i18n.language, {
    dateStyle: 'medium',
    timeStyle: 'short',
  })

  const calendarIcon = <CalendarIcon />
  const participantsIcon = <UsersIcon />
  const groupsIcon = <RectangleStackIcon />

  return (
    <Card to={`/teams/${teamSlug}/rides/${ride.slug}`}>
      <CardContent>
        <div className="flex items-start justify-between mb-4">
          <div className="flex-1 min-w-0">
            <CardTitle>{ride.name}</CardTitle>
            {ride.description && <CardDescription>{ride.description}</CardDescription>}
          </div>
          <div className="ml-3 flex flex-col items-end gap-1">
            <Badge variant={statusVariants[ride.status]}>{t(`status.${ride.status}`)}</Badge>
            <VisibilityBadge visibility={ride.visibility} />
          </div>
        </div>

        <StatGroup>
          <Stat icon={calendarIcon}>{formattedDate}</Stat>
          <Stat icon={participantsIcon}>
            {t('card.participantCount', { count: ride.participantCount })}
          </Stat>
          <Stat icon={groupsIcon}>{t('card.groupCount', { count: ride.groupCount })}</Stat>
        </StatGroup>
      </CardContent>
    </Card>
  )
}

export function RideCardSkeleton() {
  return <CardSkeleton count={1} statCount={4} badgeCount={2} />
}
