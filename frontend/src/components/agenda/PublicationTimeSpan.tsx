import { useTranslation } from 'react-i18next'
import { useFormattedDate } from '@/utils/dateFormat'
import type { DatedPublication } from '@/utils/publicationTiming'

interface PublicationTimeSpanProps {
  publication: DatedPublication
  /**
   * `full` (a card): the date, then the times — « 11 oct. 2026 à 08:30 → retour vers 14:10 ».
   * `time` (a row, whose day box already says the day): « 08:30 → retour vers 14:10 ».
   */
  mode?: 'full' | 'time'
}

/**
 * When a ride or a trip starts and ends, from `dateTime` and the stored `endDateTime` (API-85). A
 * ride reads « 08:30 → retour vers 14:10 » — « vers », the end being an estimate from its
 * groups' distance and pace; a trip, which spans days, « ven. 16 → dim. 18 oct. ». A ride back
 * on another day names that day.
 */
export function PublicationTimeSpan({ publication, mode = 'full' }: PublicationTimeSpanProps) {
  const { t } = useTranslation()
  const { formatDateTime, formatTime, formatPattern, isGuessedTimezone } = useFormattedDate()
  const start = publication.dateTime
  const end = publication.endDateTime
  const sameDay = formatPattern(start, 'yyyy-MM-dd') === formatPattern(end, 'yyyy-MM-dd')

  let text: string
  if (publication.type === 'TRIP') {
    const sameMonth = formatPattern(start, 'yyyy-MM') === formatPattern(end, 'yyyy-MM')
    text = sameDay
      ? formatPattern(start, 'EEE d MMM')
      : `${formatPattern(start, sameMonth ? 'EEE d' : 'EEE d MMM')} → ${formatPattern(end, 'EEE d MMM')}`
  } else {
    const from = mode === 'full' ? formatDateTime(start) : formatTime(start)
    const back = sameDay ? formatTime(end) : formatPattern(end, 'EEE d MMM p')
    text = `${from} → ${t('agenda.returnAround', { time: back })}`
  }
  return <span suppressHydrationWarning={isGuessedTimezone}>{text}</span>
}
