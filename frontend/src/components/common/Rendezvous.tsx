import { Tooltip } from '@mantine/core'
import { IconWorld } from '@tabler/icons-react'
import { useRendezvousFormat } from '@/hooks/useRendezvousFormat'
import type { RendezvousMention } from '@/utils/rendezvous'

type DateInput = Date | string | null | undefined

/**
 * A rendezvous — a departure, an estimated return, a trip's dates, a scheduled publication — read
 * in its entity's zone, with the « heure de Tokyo (ven. 01:00 chez vous) » mention when that zone
 * does not have the reader's offset (docs/LEDGER_*.md API-60, plan §7). Timestamps keep
 * `FormattedDate`/`FormattedDateTime` (the reader's zone).
 *
 * - `detail`: the mention inline, « samedi 11 oct. à 08:00 · heure de Tokyo (ven. 01:00 chez vous) ».
 * - `card`: the entity time and a discreet icon carrying the mention in a tooltip.
 */
interface RendezvousProps {
  date: DateInput
  zone: string | null | undefined
  /** What to print: the date and time (default), the date alone, or the time alone. */
  format?: 'dateTime' | 'date' | 'time'
  variant?: 'detail' | 'card'
  /** Wraps the formatted text in a sentence (« Publication programmée pour le {{date}} »). */
  template?: (text: string) => string
}

export function Rendezvous({
  date,
  zone,
  format = 'dateTime',
  variant = 'card',
  template,
}: RendezvousProps) {
  const rendezvous = useRendezvousFormat(zone)
  const text =
    format === 'date'
      ? rendezvous.formatDate(date)
      : format === 'time'
        ? rendezvous.formatTime(date)
        : rendezvous.formatDateTime(date)
  const mention = rendezvous.mention(date)
  return (
    <>
      <span suppressHydrationWarning={rendezvous.isGuessedText}>
        {template ? template(text) : text}
      </span>
      {variant === 'detail' ? (
        <ZoneMentionText mention={mention} isGuessed={rendezvous.isGuessedTimezone} />
      ) : (
        <ZoneMentionIcon mention={mention} isGuessed={rendezvous.isGuessedTimezone} />
      )}
    </>
  )
}

interface ZoneMentionProps {
  mention: RendezvousMention | null
  /** The reader's zone is a guess: the mention may differ between server and client. */
  isGuessed?: boolean
  /** False inside a surface that already shows the mention on hover (a calendar event). */
  withTooltip?: boolean
}

/** « · heure de Tokyo (ven. 01:00 chez vous) », after a detail line; nothing without mention. */
export function ZoneMentionText({ mention, isGuessed }: ZoneMentionProps) {
  if (!mention) return null
  return (
    <span suppressHydrationWarning={isGuessed}>
      {' · '}
      {mention.full}
    </span>
  )
}

/**
 * The discreet indicator of a card, a row or a span: an icon whose tooltip carries the mention
 * (one for a whole span). Nothing without mention.
 */
export function ZoneMentionIcon({ mention, isGuessed, withTooltip = true }: ZoneMentionProps) {
  if (!mention) return null
  const icon = (
    <span
      role="img"
      aria-label={mention.full}
      // Focusable so the tooltip's focus trigger reaches keyboard users; not in a calendar event,
      // whose own tooltip already carries the mention.
      tabIndex={withTooltip ? 0 : undefined}
      suppressHydrationWarning={isGuessed}
      style={{
        display: 'inline-flex',
        verticalAlign: 'middle',
        marginInlineStart: 4,
        color: 'var(--mantine-color-dimmed)',
      }}
    >
      <IconWorld size={14} stroke={1.75} />
    </span>
  )
  if (!withTooltip) return icon
  return (
    <Tooltip label={mention.full} withArrow events={{ hover: true, focus: true, touch: true }}>
      {icon}
    </Tooltip>
  )
}
