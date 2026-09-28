import { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { Alert, Button, Group, Text } from '@mantine/core'
import { IconDeviceMobile } from '@tabler/icons-react'
import { useInstallOffer } from '@/lib/install/useInstallOffer'
import { useAppName } from '@/hooks/useAppName'

const DISMISSED_KEY = 'pedalons.installBanner.dismissedAt'

/** A refusal holds for a season; after that, the offer comes back once. */
const REOFFER_AFTER_MS = 90 * 24 * 60 * 60 * 1000

function isDismissed(): boolean {
  try {
    const at = Number(localStorage.getItem(DISMISSED_KEY))
    return Number.isFinite(at) && at > 0 && Date.now() - at < REOFFER_AFTER_MS
  } catch {
    return false
  }
}

function dismiss(): void {
  try {
    localStorage.setItem(DISMISSED_KEY, String(Date.now()))
  } catch {
    // Private mode: it closes for this page only.
  }
}

/**
 * Phones only: the mobile app from its store once it is published, the site installed as an app
 * until then. Nothing on the first render (the offer and the refusal are only known in the
 * browser), and nothing once installed or closed.
 */
export function InstallBanner() {
  const { t } = useTranslation()
  const appName = useAppName()
  const { offer, install } = useInstallOffer({ withStore: true })
  // Hidden until localStorage has been read, so a closed banner never flashes.
  const [dismissed, setDismissed] = useState(true)

  useEffect(() => {
    setDismissed(isDismissed())
  }, [])

  if (!offer || dismissed) return null

  const close = () => {
    dismiss()
    setDismissed(true)
  }

  return (
    <Alert
      hiddenFrom="sm"
      variant="light"
      color="primary"
      icon={<IconDeviceMobile size={18} />}
      title={t('install.banner.title', { appName })}
      withCloseButton
      closeButtonLabel={t('install.banner.close')}
      onClose={close}
      mb="md"
    >
      <Group justify="space-between" align="center" wrap="wrap" gap="xs">
        <Text size="sm">
          {offer.kind === 'store' ? t('install.banner.storeMessage') : t('install.banner.message')}
        </Text>
        <Button size="xs" onClick={install}>
          {offer.kind === 'store' ? t('install.banner.openStore') : t('install.action')}
        </Button>
      </Group>
    </Alert>
  )
}
