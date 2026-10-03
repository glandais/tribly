import { useTranslation } from 'react-i18next'
import { Group, Paper, Stack, Text } from '@mantine/core'
import { IconKey } from '@tabler/icons-react'
import type { Visibility } from '@/api/dto'
import { VisibilityBadge } from '@/components/card/common'
import { VisualFrame } from './VisualFrame'

/** The three visibility levels, with the badges and labels the product actually uses. */
export function PrivacyVisual() {
  const { t } = useTranslation()
  const levels: { visibility: Visibility; description: string }[] = [
    { visibility: 'TEAM', description: t('features.demo.privacy.team') },
    { visibility: 'PUBLIC_UNLISTED', description: t('features.demo.privacy.unlisted') },
    { visibility: 'PUBLIC', description: t('features.demo.privacy.public') },
  ]

  return (
    <VisualFrame color="gray">
      <Stack gap="sm">
        {levels.map((level) => (
          <Paper key={level.visibility} withBorder radius="md" px="md" py="sm">
            <Group justify="space-between" wrap="nowrap" gap="sm">
              <Text size="sm">{level.description}</Text>
              <VisibilityBadge visibility={level.visibility} />
            </Group>
          </Paper>
        ))}
        <Group
          gap="sm"
          wrap="nowrap"
          px="md"
          py="sm"
          style={{
            borderRadius: 'var(--mantine-radius-md)',
            backgroundColor: 'var(--mantine-primary-color-light)',
            color: 'var(--mantine-primary-color-light-color)',
          }}
        >
          <IconKey size={18} style={{ flex: 'none' }} />
          <Text size="sm" fw={500}>
            {t('features.demo.privacy.passwordless')}
          </Text>
        </Group>
      </Stack>
    </VisualFrame>
  )
}
