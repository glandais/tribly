import { useFormattedDate } from '@/utils/dateFormat'

interface FormattedDateProps {
  date: Date | string | null | undefined
}

/**
 * Renders a date/time in the visitor's effective timezone (see `useEffectiveTimezone`). When that
 * timezone is a guess (anonymous visitor, or a signed-in user who never set one) rather than a
 * confirmed preference, the server and the client can legitimately render different text — the
 * server has no way to know an anonymous visitor's real zone. `suppressHydrationWarning` accepts
 * that difference instead of forcing a same-value-on-both-renders workaround.
 */
export function FormattedDate({ date }: FormattedDateProps) {
  const { formatDate, isGuessedTimezone } = useFormattedDate()
  return <span suppressHydrationWarning={isGuessedTimezone}>{formatDate(date)}</span>
}

export function FormattedDateTime({ date }: FormattedDateProps) {
  const { formatDateTime, isGuessedTimezone } = useFormattedDate()
  return <span suppressHydrationWarning={isGuessedTimezone}>{formatDateTime(date)}</span>
}
