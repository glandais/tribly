import { Group, ActionIcon, Badge } from '@mantine/core'
import { IconChevronUp, IconChevronDown } from '@tabler/icons-react'
import { useTranslation } from 'react-i18next'

export interface ReorderControlsProps {
  index: number
  total: number
  onMove: (direction: 'up' | 'down') => void
  showIndex?: boolean
  disabled?: boolean
}

export function ReorderControls({
  index,
  total,
  onMove,
  showIndex = true,
  disabled = false,
}: ReorderControlsProps) {
  const { t } = useTranslation()
  const isFirst = index === 0
  const isLast = index >= total - 1

  return (
    <Group gap={4}>
      {showIndex && (
        <Badge size="sm" circle color="indigo" variant="light">
          {index + 1}
        </Badge>
      )}
      <ActionIcon
        variant="subtle"
        color="gray"
        size="sm"
        onClick={() => onMove('up')}
        disabled={disabled || isFirst}
        aria-label={t('aria.moveUp')}
      >
        <IconChevronUp size={16} />
      </ActionIcon>
      <ActionIcon
        variant="subtle"
        color="gray"
        size="sm"
        onClick={() => onMove('down')}
        disabled={disabled || isLast}
        aria-label={t('aria.moveDown')}
      >
        <IconChevronDown size={16} />
      </ActionIcon>
    </Group>
  )
}
