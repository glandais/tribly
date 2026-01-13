import { useTranslation } from 'react-i18next'
import { IconCalendar, IconMapPin, IconCurrencyEuro } from '@tabler/icons-react'
import { Group, Box, Stack } from '@mantine/core'
import { Card, CardContent, CardTitle, CardDescription } from './common'
import { Badge, VisibilityBadge, Stat, StatGroup, CardSkeleton } from './common'
import { EntityLogo } from '../common/EntityLogo'
import { useFormattedDate } from '../../utils/dateFormat'
import { paths } from '@/config/paths'
import { AdDto, AdType, RentalPeriod } from '@/api/dto'

const statusVariants: Record<string, 'gray' | 'green' | 'red'> = {
  DRAFT: 'gray',
  PUBLISHED: 'green',
  CANCELLED: 'red',
}

const adTypeBadgeVariants: Record<AdType, 'primary' | 'purple' | 'orange'> = {
  [AdType.SALE]: 'primary',
  [AdType.RENTAL]: 'purple',
  [AdType.WANTED]: 'orange',
}

interface AdCardProps {
  ad: AdDto
}

export function AdCard({ ad }: AdCardProps) {
  const { t } = useTranslation()
  const { formatDateTime } = useFormattedDate()

  const formattedDate = formatDateTime(ad.createdAt)

  const formatPrice = (price: number | undefined, adType: AdType, rentalPeriod?: RentalPeriod) => {
    if (price === undefined || price === null) {
      return t('ads.detail.priceNegotiable')
    }
    const formattedPrice = new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: 'EUR',
    }).format(price)

    if (adType === AdType.RENTAL && rentalPeriod) {
      const period = t(
        `ads.rentalPeriod.${rentalPeriod satisfies 'DAY' | 'WEEK' | 'MONTH'}`
      ).toLowerCase()
      return t('ads.pricePerPeriod', { price: formattedPrice, period })
    }
    return formattedPrice
  }

  return (
    <Card to={paths.ad(ad.team.slug, ad.slug)}>
      <CardContent>
        <Stack gap="md">
          <Group justify="space-between" mb="md" align="flex-start">
            <Group gap="sm" wrap="nowrap" style={{ flex: 1, minWidth: 0 }}>
              <EntityLogo logo={ad.media.assets.logo} alt={ad.name} size="md" />
              <Box style={{ flex: 1, minWidth: 0 }}>
                <CardTitle>{ad.name}</CardTitle>
                <CardDescription markdown={true} media={ad.media} />
              </Box>
            </Group>
            <Stack gap="xs" align="flex-end">
              <Badge variant={adTypeBadgeVariants[ad.adType]}>
                {t(`ads.adType.${ad.adType satisfies 'SALE' | 'RENTAL' | 'WANTED'}`)}
              </Badge>
              <Badge variant={statusVariants[ad.status] || 'gray'}>
                {t(`status.${ad.status satisfies 'DRAFT' | 'PUBLISHED' | 'CANCELLED'}`)}
              </Badge>
              <VisibilityBadge visibility={ad.visibility} />
            </Stack>
          </Group>

          <StatGroup>
            <Stat icon={<IconCurrencyEuro size={16} />}>
              {formatPrice(ad.price, ad.adType, ad.rentalPeriod)}
            </Stat>
            <Stat icon={<IconCalendar size={16} />}>{formattedDate}</Stat>
            {ad.locationDescription && (
              <Stat icon={<IconMapPin size={16} />}>{ad.locationDescription}</Stat>
            )}
          </StatGroup>
        </Stack>
      </CardContent>
    </Card>
  )
}

export function AdCardSkeleton() {
  return <CardSkeleton count={1} statCount={3} badgeCount={3} />
}
