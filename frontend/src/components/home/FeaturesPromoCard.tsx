import { useTranslation } from 'react-i18next'
import { Group, Paper, Stack, Text, ThemeIcon } from '@mantine/core'
import { IconChevronRight, IconDeviceWatch } from '@tabler/icons-react'
import { useListPairedDevices } from '@/api/endpoints/users/users'
import { PrefetchLink } from '@/components/common/PrefetchLink'
import { paths } from '@/config/paths'
import { useAuth } from '@/hooks/useAuth'

/**
 * The member home's closing card: sending a route to a bike computer. Only for a member who has
 * neither a connected GPS service nor a paired device — the others already do it — and it leads to
 * the profile's « Appareils et services », where the thing is done, not to the features page, which only sells it
 * (docs/LEDGER_*.md WEB-51). Hidden while the devices load: it would flash for those who have one.
 */
export function FeaturesPromoCard() {
  const { t } = useTranslation()
  const { user } = useAuth()
  const { data: devices, isLoading } = useListPairedDevices()

  const hasService = (user?.connectedServices?.length ?? 0) > 0
  if (!user || hasService || isLoading || !devices || devices.length > 0) return null

  return (
    <Paper
      withBorder
      radius="md"
      p="md"
      component={PrefetchLink}
      to={paths.profileDevices()}
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
