import { useTranslation } from 'react-i18next'
import { CalendarIcon, UsersIcon, RectangleStackIcon } from '@heroicons/react/24/outline'
import { Status } from '../../hooks/useTrip'
import type { TripDto } from '../../hooks/useTrip'
import { Card, CardContent, CardTitle, CardDescription } from '../common/card'
import { Badge, VisibilityBadge, Stat, StatGroup, CardSkeleton } from '../common/card'
import { EntityLogo } from '../common/EntityLogo'
import { useFormattedDate } from '../../utils/dateFormat'
import { paths } from '@/config/paths'

interface TripCardProps {
  trip: TripDto
  teamSlug: string
  showTypeBadge?: boolean
}

const statusVariants: Record<Status, 'gray' | 'green' | 'red'> = {
  [Status.Draft]: 'gray',
  [Status.Published]: 'green',
  [Status.Cancelled]: 'red',
}

export function TripCard({ trip, teamSlug, showTypeBadge = false }: TripCardProps) {
  const { t } = useTranslation('trips')
  const { t: tCommon } = useTranslation('common')
  const { formatDateTime } = useFormattedDate()
  const formattedDate = formatDateTime(trip.dateTime)

  const calendarIcon = <CalendarIcon />
  const participantsIcon = <UsersIcon />
  const stagesIcon = <RectangleStackIcon />

  return (
    <Card to={paths.trip(teamSlug, trip.slug)}>
      <CardContent>
        <div className="flex items-start justify-between mb-4">
          <div className="flex items-start gap-3 flex-1 min-w-0">
            <EntityLogo
              logo={trip.media.assets.logo}
              alt={trip.name}
              size="md"
              className="shrink-0"
            />
            <div className="flex-1 min-w-0">
              <CardTitle>{trip.name}</CardTitle>
              <CardDescription markdown={true} media={trip.media} />
            </div>
          </div>
          <div className="ml-3 flex flex-col items-end gap-1">
            {showTypeBadge && <Badge variant="indigo">{tCommon('publicationType.trip')}</Badge>}
            <Badge variant={statusVariants[trip.status]}>{t(`status.${trip.status}`)}</Badge>
            <VisibilityBadge visibility={trip.visibility} />
          </div>
        </div>

        <StatGroup>
          <Stat icon={calendarIcon}>{formattedDate}</Stat>
          <Stat icon={participantsIcon}>
            {t('card.participantCount', { count: trip.participantCount })}
          </Stat>
          <Stat icon={stagesIcon}>{t('card.stageCount', { count: trip.stageCount })}</Stat>
        </StatGroup>
      </CardContent>
    </Card>
  )
}

export function TripCardSkeleton() {
  return <CardSkeleton count={1} statCount={4} badgeCount={2} />
}
