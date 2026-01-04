import { Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import {
  CalendarIcon,
  UsersIcon,
  ChevronRightIcon,
  MapPinIcon,
  CurrencyEuroIcon,
} from '@heroicons/react/24/outline'
import { Card, CardContent, CardTitle, CardDescription } from '../common/card'
import { Badge, VisibilityBadge, Stat, StatGroup, CardSkeleton } from '../common/card'
import { EntityLogo } from '../common/EntityLogo'
import { useFormattedDate } from '../../utils/dateFormat'
import { paths } from '@/config/paths'
import { AdDto, AdType, RentalPeriod } from '@/api/dto'

// Status variants for badges
const statusVariants: Record<string, 'gray' | 'green' | 'red'> = {
  DRAFT: 'gray',
  PUBLISHED: 'green',
  CANCELLED: 'red',
}

// Ad type badge variants
const adTypeBadgeVariants: Record<AdType, 'indigo' | 'purple' | 'orange'> = {
  [AdType.SALE]: 'indigo',
  [AdType.RENTAL]: 'purple',
  [AdType.WANTED]: 'orange',
}

interface AdCardProps {
  ad: AdDto
  showTeam?: boolean
}

export function AdCard({ ad, showTeam = false }: AdCardProps) {
  const { t } = useTranslation('ads')
  const { t: tCommon } = useTranslation('common')
  const { formatDateTime } = useFormattedDate()

  const formattedDate = formatDateTime(ad.dateTime)

  const formatPrice = (price: number | undefined, adType: AdType, rentalPeriod?: RentalPeriod) => {
    if (price === undefined || price === null) {
      return t('detail.priceNegotiable')
    }
    const formattedPrice = new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: 'EUR',
    }).format(price)

    if (adType === AdType.RENTAL && rentalPeriod) {
      const period = t(
        `rentalPeriod.${rentalPeriod satisfies 'DAY' | 'WEEK' | 'MONTH'}`
      ).toLowerCase()
      return t('pricePerPeriod', { price: formattedPrice, period })
    }
    return formattedPrice
  }

  return (
    <Card to={paths.ad(ad.team.slug, ad.slug)}>
      <CardContent>
        {/* Team header - clickable link to team page */}
        {showTeam && (
          <Link
            to={paths.team(ad.team.slug)}
            onClick={(e) => e.stopPropagation()}
            className="flex items-center gap-2 mb-3 text-sm text-gray-600 hover:text-indigo-600 transition-colors group"
          >
            <UsersIcon className="h-4 w-4" />
            <span className="font-medium">{ad.team.name}</span>
            <ChevronRightIcon className="h-3 w-3 opacity-0 -translate-x-1 group-hover:opacity-100 group-hover:translate-x-0 transition-all" />
          </Link>
        )}

        {/* Main content */}
        <div className="flex items-start justify-between mb-4">
          <div className="flex items-start gap-3 flex-1 min-w-0">
            <EntityLogo logo={ad.media.assets.logo} alt={ad.name} size="md" className="shrink-0" />
            <div className="flex-1 min-w-0">
              <CardTitle>{ad.name}</CardTitle>
              <CardDescription markdown={true} media={ad.media} />
            </div>
          </div>
          <div className="ml-3 flex flex-col items-end gap-1">
            <Badge variant={adTypeBadgeVariants[ad.adType]}>
              {t(`adType.${ad.adType satisfies 'SALE' | 'RENTAL' | 'WANTED'}`)}
            </Badge>
            <Badge variant={statusVariants[ad.status] || 'gray'}>
              {tCommon(`status.${ad.status satisfies 'DRAFT' | 'PUBLISHED' | 'CANCELLED'}`)}
            </Badge>
            <VisibilityBadge visibility={ad.visibility} />
          </div>
        </div>

        <StatGroup>
          <Stat icon={<CurrencyEuroIcon />}>
            {formatPrice(ad.price, ad.adType, ad.rentalPeriod)}
          </Stat>
          <Stat icon={<CalendarIcon />}>{formattedDate}</Stat>
          {ad.locationDescription && <Stat icon={<MapPinIcon />}>{ad.locationDescription}</Stat>}
        </StatGroup>
      </CardContent>
    </Card>
  )
}

export function AdCardSkeleton() {
  return <CardSkeleton count={1} statCount={3} badgeCount={3} />
}
