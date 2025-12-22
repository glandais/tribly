import { useTranslation } from 'react-i18next'
import { CalendarIcon, ClockIcon, UsersIcon, RectangleStackIcon } from '@heroicons/react/24/outline'
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
  const rideDate = new Date(ride.date)
  const formattedDate = rideDate.toLocaleDateString(i18n.language, {
    weekday: 'short',
    month: 'short',
    day: 'numeric',
  })

  const calendarIcon = <CalendarIcon />
  const clockIcon = <ClockIcon />
  const participantsIcon = <UsersIcon />
  const groupsIcon = <RectangleStackIcon />

  return (
    <Card to={`/teams/${teamSlug}/rides/${ride.slug}`}>
      <CardContent>
        <div className="flex items-start justify-between mb-4">
          <div className="flex-1 min-w-0">
            <CardTitle>{ride.title}</CardTitle>
            {ride.description && <CardDescription>{ride.description}</CardDescription>}
          </div>
          <div className="ml-3 flex flex-col items-end gap-1">
            <Badge variant={statusVariants[ride.status]}>{t(`status.${ride.status}`)}</Badge>
            <VisibilityBadge visibility={ride.visibility} />
          </div>
        </div>

        <StatGroup>
          <Stat icon={calendarIcon}>{formattedDate}</Stat>
          {ride.startTime && <Stat icon={clockIcon}>{ride.startTime.substring(0, 5)}</Stat>}
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
