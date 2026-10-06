import { Box, Text } from '@mantine/core'
import { useTranslation } from 'react-i18next'
import { formatPattern as formatInZone, useFormattedDate } from '@/utils/dateFormat'
import { isSupportedZone } from '@/utils/zoneLabel'

/**
 * The day box on the left of a dated row: « sam. / 10 / oct. ». Shared by the dashboard's
 * « Mes prochaines » and the agenda's « Lignes » view.
 *
 * `zone`: the day of a rendezvous, in its entity's zone, so the box and the time printed next to it
 * describe the same local day (docs/LEDGER_*.md API-60). Without it, or in a zone the browser's
 * `Intl` lacks, the reader's zone.
 */
export function DayBox({ date, zone }: { date: string; zone?: string }) {
  const { i18n } = useTranslation()
  const reader = useFormattedDate()
  const formatPattern = (value: string, pattern: string) =>
    isSupportedZone(zone)
      ? formatInZone(value, pattern, i18n.language, zone)
      : reader.formatPattern(value, pattern)
  return (
    <Box
      w={60}
      py={8}
      ta="center"
      style={{
        flex: '0 0 60px',
        borderRadius: 'var(--mantine-radius-md)',
        background: 'var(--mantine-color-primary-light)',
        color: 'var(--mantine-color-primary-light-color)',
      }}
    >
      <Text size="xs" tt="uppercase" fw={600} suppressHydrationWarning>
        {formatPattern(date, 'EEE')}
      </Text>
      <Text fz={22} fw={700} lh={1.1} suppressHydrationWarning>
        {formatPattern(date, 'd')}
      </Text>
      <Text size="xs" tt="uppercase" suppressHydrationWarning>
        {formatPattern(date, 'MMM')}
      </Text>
    </Box>
  )
}
