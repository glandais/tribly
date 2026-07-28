import { useTranslation } from 'react-i18next'
import { IconCalendar, IconMapPin, IconCurrencyEuro } from '@tabler/icons-react'
import { Group, Box, Stack } from '@mantine/core'
import { Card, CardContent, CardTitle, CardDescription, CardImage } from './common'
import { TypeBadge, StatusBadge, VisibilityBadge, Stat, StatGroup, CardSkeleton } from './common'
import { TYPE_COLORS } from './common'
import { EntityLogo } from '../common/EntityLogo'
import { useFormattedDate } from '../../utils/dateFormat'
import { paths } from '@/config/paths'
import { AdDto, AdType, RentalPeriod } from '@/api/dto'

// Map ad types to TypeBadge type keys
const adTypeToTypeKey: Record<AdType, keyof typeof TYPE_COLORS> = {
  [AdType.SALE]: 'SALE',
  [AdType.RENTAL]: 'RENTAL',
  [AdType.WANTED]: 'WANTED',
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
      {/* Header image. `thumbnailUrl` is the ad's first picture, computed server-side, and is
          present whatever the list `view` — `media.assets` is trimmed in COMPACT. */}
      <CardImage
        media={ad.media}
        thumbnailUrl={ad.thumbnailUrl}
        alt={ad.name}
        height={120}
        type="AD"
      />

      <CardContent>
        {/* Title row with logo */}
        <Group justify="space-between" mb="md" align="flex-start" wrap="nowrap">
          <Group gap="sm" wrap="nowrap" style={{ flex: 1, minWidth: 0 }}>
            <EntityLogo logo={ad.media.assets.logo} alt={ad.name} size="md" />
            <Box style={{ flex: 1, minWidth: 0 }}>
              <CardTitle>{ad.name}</CardTitle>
              <CardDescription markdown={true} media={ad.media} excerpt={ad.excerpt} />
            </Box>
          </Group>
          <Stack gap={4} align="flex-end" ml="sm">
            <TypeBadge type={adTypeToTypeKey[ad.adType]}>
              {t(`ads.adType.${ad.adType satisfies 'SALE' | 'RENTAL' | 'WANTED'}`)}
            </TypeBadge>
            <StatusBadge status={ad.status}>
              {t(`status.${ad.status satisfies 'DRAFT' | 'PUBLISHED' | 'CANCELLED'}`)}
            </StatusBadge>
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
      </CardContent>
    </Card>
  )
}

export function AdCardSkeleton() {
  return (
    <CardSkeleton count={1} hasImage imageHeight="120px" hasLogo statCount={3} badgeCount={3} />
  )
}
