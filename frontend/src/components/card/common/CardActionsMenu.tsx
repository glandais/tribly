import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { ActionIcon, Menu } from '@mantine/core'
import { IconCalendarPlus, IconDots, IconPencil, IconSend, IconTrash } from '@tabler/icons-react'
import { PrefetchLink } from '@/components/common/PrefetchLink'
import { ConfirmDialog } from '@/components/common/ConfirmDialog'

interface CardActionsMenuProps {
  /**
   * The card's title, for the trigger's accessible name (« Actions — Sortie du dimanche »). Each
   * card of a list has its own, and none takes « Options de gestion », the detail page's chevron.
   */
  title: string
  /** « Modifier » — the edit page. */
  editPath?: string
  /** « Publier » — shown for a draft only; the caller decides. */
  onPublish?: () => Promise<unknown>
  /** « Supprimer » — always behind a confirmation. */
  onDelete?: () => Promise<unknown>
  deleteMessage?: string
  /** « Ajouter au calendrier » — a plain link to the `.ics` endpoint, downloaded by the browser. */
  icsUrl?: string
}

/**
 * The `⋯` menu of a list card (docs/LEDGER_DONE.md WEB-33). The card places it in its top-right
 * corner, outside the link (`Card`'s `actions`), so neither the trigger nor the menu opens the
 * card.
 *
 * Renders nothing when no action applies. Deciding which actions apply — the reader's role, the
 * status — belongs to the per-type wrappers (`PublicationCardActions`…), not to this menu.
 */
export function CardActionsMenu({
  title,
  editPath,
  onPublish,
  onDelete,
  deleteMessage,
  icsUrl,
}: CardActionsMenuProps) {
  const { t } = useTranslation()
  const [confirmOpen, setConfirmOpen] = useState(false)
  const [busy, setBusy] = useState(false)

  if (!editPath && !onPublish && !onDelete && !icsUrl) {
    return null
  }

  /** True when the action went through. A failure is already toasted by `axiosMutator`. */
  const run = async (action: () => Promise<unknown>): Promise<boolean> => {
    setBusy(true)
    try {
      await action()
      return true
    } catch {
      return false
    } finally {
      setBusy(false)
    }
  }

  return (
    <>
      <Menu position="bottom-end" withinPortal>
        <Menu.Target>
          <ActionIcon
            variant="default"
            size="md"
            loading={busy}
            aria-label={t('cards.actions.menu', { name: title })}
            title={t('cards.actions.menu', { name: title })}
          >
            <IconDots size={16} />
          </ActionIcon>
        </Menu.Target>
        <Menu.Dropdown>
          {editPath && (
            <Menu.Item
              component={PrefetchLink}
              to={editPath}
              leftSection={<IconPencil size={16} />}
            >
              {t('actions.edit')}
            </Menu.Item>
          )}
          {onPublish && (
            <Menu.Item leftSection={<IconSend size={16} />} onClick={() => run(onPublish)}>
              {t('actions.publish')}
            </Menu.Item>
          )}
          {icsUrl && (
            <Menu.Item
              component="a"
              href={icsUrl}
              download
              leftSection={<IconCalendarPlus size={16} />}
            >
              {t('cards.actions.addToCalendar')}
            </Menu.Item>
          )}
          {onDelete && (
            <>
              {(editPath || onPublish || icsUrl) && <Menu.Divider />}
              <Menu.Item
                color="danger"
                leftSection={<IconTrash size={16} />}
                onClick={() => setConfirmOpen(true)}
              >
                {t('actions.delete')}
              </Menu.Item>
            </>
          )}
        </Menu.Dropdown>
      </Menu>

      {onDelete && (
        <ConfirmDialog
          isOpen={confirmOpen}
          onClose={() => setConfirmOpen(false)}
          onConfirm={async () => {
            if (await run(onDelete)) {
              setConfirmOpen(false)
            }
          }}
          title={t('actions.delete')}
          message={deleteMessage ?? ''}
          confirmText={t('actions.delete')}
          variant="danger"
          isLoading={busy}
        />
      )}
    </>
  )
}
