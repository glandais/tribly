import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { ActionIcon, Menu } from '@mantine/core'
import type { ActionIconProps } from '@mantine/core'
import { IconDots, IconFlag, IconUserOff } from '@tabler/icons-react'
import type { ReportTargetType } from '@/api/dto'
import { useAuthStore, selectIsAuthenticated, selectUser } from '@/store/authStore'
import { ReportModal } from './ReportModal'
import { BlockUserDialog } from './BlockUserDialog'

interface ContentActionsMenuProps {
  teamSlug: string
  teamName: string
  targetType: ReportTargetType
  targetId: string
  /**
   * The author the page already shows next to the content (a comment's, an ad's). Enables "Block
   * {name}". Leave it out where no author is displayed — a post must not start exposing its own.
   */
  author?: { id: string; displayName: string }
  /**
   * Hides "Report" on the reader's own content when the page can tell without an author (the
   * server refuses it anyway, with `REPORT_SELF`). With an `author`, it is derived from it.
   */
  isOwn?: boolean
  /** Called once the report is sent — a detail page uses it to go back to its list. */
  onReported?: () => void
  onBlocked?: () => void
  /** `header` sits next to a detail page's edit buttons; `inline` in a comment's action row. */
  placement?: 'header' | 'inline'
}

const TRIGGER_PROPS: Record<'header' | 'inline', ActionIconProps> = {
  header: { variant: 'default', size: 'input-sm' },
  inline: { variant: 'subtle', color: 'gray', size: 'sm' },
}

/**
 * The `⋯` menu every signed-in reader gets on a piece of content: "Report", and "Block {name}"
 * where the author is shown. Kept apart from the editors' split button, which only organizers see.
 *
 * Renders nothing for an anonymous visitor, and nothing when no action applies (one's own
 * content with no one else to block).
 */
export function ContentActionsMenu({
  teamSlug,
  teamName,
  targetType,
  targetId,
  author,
  isOwn = false,
  onReported,
  onBlocked,
  placement = 'header',
}: ContentActionsMenuProps) {
  const { t } = useTranslation()
  const isAuthenticated = useAuthStore(selectIsAuthenticated)
  const currentUserId = useAuthStore(selectUser)?.id
  const [reportOpened, setReportOpened] = useState(false)
  const [blockOpened, setBlockOpened] = useState(false)

  if (!isAuthenticated) return null

  const authorIsMe = !!author && author.id === currentUserId
  const canReport = !isOwn && !authorIsMe
  const canBlock = !!author && !authorIsMe

  if (!canReport && !canBlock) return null

  const iconSize = placement === 'header' ? 18 : 14

  return (
    <>
      <Menu position="bottom-end" withinPortal>
        <Menu.Target>
          <ActionIcon
            {...TRIGGER_PROPS[placement]}
            aria-label={t('moderation.menu.ariaLabel')}
            title={t('moderation.menu.ariaLabel')}
          >
            <IconDots size={iconSize} />
          </ActionIcon>
        </Menu.Target>
        <Menu.Dropdown>
          {canReport && (
            <Menu.Item leftSection={<IconFlag size={16} />} onClick={() => setReportOpened(true)}>
              {t('moderation.report.menu')}
            </Menu.Item>
          )}
          {canBlock && author && (
            <Menu.Item
              color="danger"
              leftSection={<IconUserOff size={16} />}
              onClick={() => setBlockOpened(true)}
            >
              {t('moderation.block.menu', { name: author.displayName })}
            </Menu.Item>
          )}
        </Menu.Dropdown>
      </Menu>

      {canReport && (
        <ReportModal
          opened={reportOpened}
          onClose={() => setReportOpened(false)}
          teamSlug={teamSlug}
          teamName={teamName}
          targetType={targetType}
          targetId={targetId}
          onReported={onReported}
        />
      )}
      {canBlock && author && (
        <BlockUserDialog
          opened={blockOpened}
          onClose={() => setBlockOpened(false)}
          user={author}
          onBlocked={onBlocked}
        />
      )}
    </>
  )
}
