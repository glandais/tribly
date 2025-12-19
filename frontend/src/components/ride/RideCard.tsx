import { useTranslation } from 'react-i18next'
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

  const calendarIcon = (
    <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
      <path
        strokeLinecap="round"
        strokeLinejoin="round"
        strokeWidth={2}
        d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"
      />
    </svg>
  )

  const clockIcon = (
    <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
      <path
        strokeLinecap="round"
        strokeLinejoin="round"
        strokeWidth={2}
        d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"
      />
    </svg>
  )

  const participantsIcon = (
    <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
      <path
        strokeLinecap="round"
        strokeLinejoin="round"
        strokeWidth={2}
        d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"
      />
    </svg>
  )

  const groupsIcon = (
    <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
      <path
        strokeLinecap="round"
        strokeLinejoin="round"
        strokeWidth={2}
        d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"
      />
    </svg>
  )

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
