import { useTranslation } from 'react-i18next'
import { Group, Progress, Stack, Text, Tooltip } from '@mantine/core'
import type { WindExposureDto, WindSegmentDto } from '@/api/dto'
import { RelativeWind } from '@/api/dto'
import { useUnits } from '@/hooks/useUnits'
import { WindArrow } from './WindArrow'
import { useWeatherLabels } from './useWeatherLabels'
import { exposureSections, relativeWindColor } from './weatherDisplay'

interface WindExposureBarProps {
  segments: WindSegmentDto[]
  exposure: WindExposureDto
  distance: number
}

/** Arrow angles of the legend: pushing (0), across (90), in the face (180). */
const LEGEND_ANGLES: Record<RelativeWind, number> = {
  [RelativeWind.TAIL]: 0,
  [RelativeWind.CROSS]: 90,
  [RelativeWind.HEAD]: 180,
}

const LEGEND_ORDER: RelativeWind[] = [RelativeWind.HEAD, RelativeWind.CROSS, RelativeWind.TAIL]

/**
 * The route as a bar, stretch by stretch in route order, coloured by how the rider meets the wind
 * (the generated `RELATIVE_WIND_COLORS`), then the totals under it — always written out with an
 * arrow, never by colour alone.
 */
export function WindExposureBar({
  segments,
  exposure,
  distance: legDistance,
}: WindExposureBarProps) {
  const { t } = useTranslation()
  const { distance } = useUnits()
  const labels = useWeatherLabels()
  const sections = exposureSections(segments, legDistance)
  const totals: Record<RelativeWind, number> = {
    [RelativeWind.HEAD]: exposure.head,
    [RelativeWind.CROSS]: exposure.cross,
    [RelativeWind.TAIL]: exposure.tail,
  }

  if (sections.length === 0) return null

  return (
    <Stack gap={6}>
      <Text size="sm" fw={600}>
        {t('rides.weather.exposure.title')}
      </Text>
      <Progress.Root size={14} radius="sm" aria-label={t('rides.weather.exposure.title')}>
        {sections.map((section) => {
          const label = t('rides.weather.exposure.segment', {
            relativeWind: labels.relativeWind(section.relativeWind),
            from: distance(section.fromDistance),
            to: distance(section.toDistance),
          })
          return (
            <Tooltip key={section.fromDistance} label={label} withArrow>
              <Progress.Section
                value={section.percent}
                color={relativeWindColor(section.relativeWind)}
                aria-label={label}
              />
            </Tooltip>
          )
        })}
      </Progress.Root>
      <Group gap="md" wrap="wrap">
        {LEGEND_ORDER.filter((kind) => totals[kind] > 0).map((kind) => (
          <Group key={kind} gap={4} wrap="nowrap">
            <WindArrow
              angle={LEGEND_ANGLES[kind]}
              relativeWind={kind}
              label={labels.relativeWind(kind)}
              size={14}
            />
            <Text size="xs" c="dimmed">
              {t('rides.weather.exposure.total', {
                relativeWind: labels.relativeWindShort(kind),
                distance: distance(totals[kind]),
              })}
            </Text>
          </Group>
        ))}
      </Group>
    </Stack>
  )
}
