import { Box, Text } from '@mantine/core'
import { useFormattedDate } from '@/utils/dateFormat'

/**
 * The day box on the left of a dated row: « sam. / 10 / oct. ». Shared by the dashboard's
 * « Mes prochaines » and the agenda's « Lignes » view.
 */
export function DayBox({ date }: { date: string }) {
  const { formatPattern } = useFormattedDate()
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
