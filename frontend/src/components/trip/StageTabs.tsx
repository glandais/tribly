import { useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { Tabs, Stack, Text } from '@mantine/core'
import { IconHome } from '@tabler/icons-react'
import type { TripDto } from '@/api/dto'
import { useFormattedDate } from '../../utils/dateFormat'
import { paths } from '@/config/paths'

interface StageTabsProps {
  trip: TripDto
  teamSlug: string
  currentTab: 'overview' | string // 'overview' or stageSlug
}

export function StageTabs({ trip, teamSlug, currentTab }: StageTabsProps) {
  const { t } = useTranslation()
  const navigate = useNavigate()
  const { formatDate } = useFormattedDate()

  const handleTabChange = (value: string | null) => {
    if (!value) return

    if (value === 'overview') {
      navigate(paths.trip(teamSlug, trip.slug))
    } else {
      navigate(paths.stage(teamSlug, trip.slug, value))
    }
  }

  return (
    <Tabs orientation="vertical" value={currentTab} onChange={handleTabChange} variant="pills">
      <Tabs.List>
        <Tabs.Tab value="overview" leftSection={<IconHome size={16} />}>
          {t('trips.tabs.overview')}
        </Tabs.Tab>

        {trip.stages?.map((stage, index) => (
          <Tabs.Tab
            key={stage.id}
            value={stage.slug}
            leftSection={
              <Text size="sm" fw={700} w={20} ta="center">
                {index + 1}
              </Text>
            }
          >
            <Stack gap={0}>
              <Text size="sm" fw={500} lineClamp={1}>
                {stage.name}
              </Text>
              <Text size="xs" opacity={0.7}>
                {formatDate(stage.dateTime)}
              </Text>
            </Stack>
          </Tabs.Tab>
        ))}
      </Tabs.List>
    </Tabs>
  )
}
