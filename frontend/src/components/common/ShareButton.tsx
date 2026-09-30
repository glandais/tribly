import { useTranslation } from 'react-i18next'
import { ActionIcon } from '@mantine/core'
import { notifications } from '@mantine/notifications'
import { IconShare } from '@tabler/icons-react'

interface ShareButtonProps {
  /** The content's name: the share sheet's title, next to the link. */
  title: string
}

/**
 * Shares the page being read — the web counterpart of the mobile app's `shareAppLink`.
 *
 * The link is the address bar's, not a `paths.xxx()` rebuild: on a host pinned to one team the
 * router path is prefixed while the visible URL is not, and it is the visible one that works for
 * the recipient. The query string is kept, as it carries the page's filters.
 *
 * Uses the system share sheet where there is one (phones, Safari, Edge), and copies the link
 * otherwise — Firefox and Chrome on desktop have no `navigator.share`.
 */
export function ShareButton({ title }: ShareButtonProps) {
  const { t } = useTranslation()

  const handleShare = async () => {
    const url = window.location.origin + window.location.pathname + window.location.search
    if (typeof navigator.share === 'function') {
      try {
        await navigator.share({ title, url })
        return
      } catch (err) {
        // The visitor closed the sheet: nothing to report.
        if (err instanceof DOMException && err.name === 'AbortError') return
        // Any other refusal (no user activation, a blocked target) falls back to the copy.
      }
    }
    try {
      await navigator.clipboard.writeText(url)
      notifications.show({ message: t('share.copied'), color: 'green' })
    } catch {
      notifications.show({ message: t('share.copyFailed'), color: 'red' })
    }
  }

  return (
    <ActionIcon
      variant="default"
      size="input-sm"
      onClick={handleShare}
      aria-label={t('share.button')}
      title={t('share.button')}
    >
      <IconShare size={18} />
    </ActionIcon>
  )
}
