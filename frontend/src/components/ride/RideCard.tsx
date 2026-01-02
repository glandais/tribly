import { useTranslation } from 'react-i18next'
import { CalendarIcon, UsersIcon, RectangleStackIcon } from '@heroicons/react/24/outline'
import { Status } from '../../hooks/useRide'
import type { RideDto } from '../../hooks/useRide'
import { Card, CardContent, CardTitle, CardDescription } from '../common/card'
import { Badge, VisibilityBadge, Stat, StatGroup, CardSkeleton } from '../common/card'
import { EntityLogo } from '../common/EntityLogo'
import { useFormattedDate } from '../../utils/dateFormat'
import { paths } from '@/config/paths'

interface RideCardProps {
  ride: RideDto
  teamSlug: string
  showTypeBadge?: boolean
}

const statusVariants: Record<Status, 'gray' | 'green' | 'red'> = {
  [Status.Draft]: 'gray',
  [Status.Published]: 'green',
  [Status.Cancelled]: 'red',
}

export function RideCard({ ride, teamSlug, showTypeBadge = false }: RideCardProps) {
  const { t } = useTranslation('rides')
  const { t: tCommon } = useTranslation('common')
  const { formatDateTime } = useFormattedDate()
  const formattedDate = formatDateTime(ride.dateTime)

  const calendarIcon = <CalendarIcon />
  const participantsIcon = <UsersIcon />
  const groupsIcon = <RectangleStackIcon />

  return (
    <Card to={paths.ride(teamSlug, ride.slug)}>
      <CardContent>
        <div className="flex items-start justify-between mb-4">
          <div className="flex items-start gap-3 flex-1 min-w-0">
            <EntityLogo
              logo={ride.media.assets.logo}
              alt={ride.name}
              size="md"
              className="shrink-0"
            />
            <div className="flex-1 min-w-0">
              <CardTitle>{ride.name}</CardTitle>
              <CardDescription markdown={true} media={ride.media} />
            </div>
          </div>
          <div className="ml-3 flex flex-col items-end gap-1">
            {showTypeBadge && <Badge variant="indigo">{tCommon('publicationType.ride')}</Badge>}
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
