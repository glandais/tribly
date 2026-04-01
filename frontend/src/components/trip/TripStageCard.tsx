import { Link, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { IconCalendar, IconMapPin } from '@tabler/icons-react'
import { Paper, Group, Text, UnstyledButton, Badge, Box } from '@mantine/core'
import type { TripStageDto } from '@/api/dto'
import { useFormattedDate } from '../../utils/dateFormat'
import { MediaDisplay } from '../common/MediaDisplay'
import { EntityLogo } from '../common/EntityLogo'
import { paths } from '@/config/paths'

interface TripStageCardProps {
  stage: TripStageDto
  index: number
  teamSlug: string
  tripSlug: string
  onHover?: (stageId: string | null) => void
  isHighlighted?: boolean
}

export function TripStageCard({
  stage,
  index,
  teamSlug,
  tripSlug,
  onHover,
  isHighlighted = false,
}: TripStageCardProps) {
  const { t } = useTranslation()
  const { formatDateTime } = useFormattedDate()
  const navigate = useNavigate()

  return (
    <Paper
      component={Link}
      to={paths.stage(teamSlug, tripSlug, stage.slug)}
      withBorder
      p="md"
      radius="md"
      style={{
        borderColor: isHighlighted ? 'var(--mantine-primary-color-filled)' : undefined,
        backgroundColor: isHighlighted ? 'var(--mantine-primary-color-light)' : undefined,
        boxShadow: isHighlighted ? 'var(--mantine-shadow-md)' : undefined,
        transition: 'all 0.2s',
        textDecoration: 'none',
        color: 'inherit',
        cursor: 'pointer',
      }}
      onMouseEnter={() => onHover?.(stage.id)}
      onMouseLeave={() => onHover?.(null)}
    >
      <Group justify="space-between" mb="sm" wrap="nowrap">
        <Group gap="sm" wrap="nowrap" style={{ minWidth: 0 }}>
          {stage.media.assets.logo ? (
            <EntityLogo logo={stage.media.assets.logo} alt={stage.name} size="md" />
          ) : (
            <Badge size="lg" variant="light" color="primary" circle style={{ flexShrink: 0 }}>
              {index + 1}
            </Badge>
          )}
          <Text fw={600} truncate>
            {stage.name}
          </Text>
        </Group>
        <Group gap={4} wrap="nowrap" style={{ flexShrink: 0 }}>
          <IconCalendar size={16} color="var(--mantine-color-dimmed)" />
          <Text size="sm" c="dimmed">
            {formatDateTime(stage.dateTime)}
          </Text>
        </Group>
      </Group>

      {stage.media.markdown && (
        <Box mb="sm">
          <MediaDisplay media={stage.media} />
        </Box>
      )}

      {(stage.startPlace || stage.endPlace) && (
        <Group wrap="wrap">
          {stage.startPlace && (
            <Group gap={4} wrap="nowrap">
              <IconMapPin size={16} color="var(--mantine-color-green-text)" />
              <Text size="sm" c="var(--mantine-color-green-text)" fw={500}>
                {t('startPlace')}:
              </Text>
              <Text size="sm">{stage.startPlace.name}</Text>
            </Group>
          )}
          {stage.endPlace && (
            <Group gap={4} wrap="nowrap">
              <IconMapPin size={16} color="var(--mantine-color-red-text)" />
              <Text size="sm" c="var(--mantine-color-red-text)" fw={500}>
                {t('endPlace')}:
              </Text>
              <Text size="sm">{stage.endPlace.name}</Text>
            </Group>
          )}
        </Group>
      )}

      {stage.routeSlug && (
        <Box mt="sm">
          <UnstyledButton
            onClick={(e) => {
              e.preventDefault()
              e.stopPropagation()
              navigate(paths.route(teamSlug, stage.routeSlug!))
            }}
            styles={{
              root: {
                fontSize: 'var(--mantine-font-size-sm)',
                fontWeight: 500,
                color: 'var(--mantine-color-anchor)',
                '&:hover': {
                  textDecoration: 'underline',
                },
              },
            }}
          >
            {t('trips.stage.viewRoute')}
          </UnstyledButton>
        </Box>
      )}
    </Paper>
  )
}
