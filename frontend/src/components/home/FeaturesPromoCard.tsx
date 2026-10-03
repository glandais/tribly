import { useTranslation } from 'react-i18next'
import { Group, Paper, Stack, Text, ThemeIcon } from '@mantine/core'
import { IconChevronRight, IconDeviceWatch } from '@tabler/icons-react'
import { PrefetchLink } from '@/components/common/PrefetchLink'
import { paths } from '@/config/paths'

/** The member home's closing card: sending a route to a bike computer, and the features page. */
export function FeaturesPromoCard() {
  const { t } = useTranslation()

  return (
    <Paper
      withBorder
      radius="md"
      p="md"
      component={PrefetchLink}
      to={paths.features()}
      style={{ color: 'inherit', textDecoration: 'none' }}
    >
      <Group wrap="nowrap" gap="md">
        <ThemeIcon variant="light" size={44} radius="md">
          <IconDeviceWatch size={24} />
        </ThemeIcon>
        <Stack gap={2} style={{ flex: 1, minWidth: 0 }}>
          <Text fw={600}>{t('home.member.promo.title')}</Text>
          <Text size="sm" c="dimmed">
            {t('home.member.promo.text')}
          </Text>
        </Stack>
        <IconChevronRight size={18} color="var(--mantine-color-dimmed)" />
      </Group>
    </Paper>
  )
}
