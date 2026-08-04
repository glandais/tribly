import { useState, useCallback } from 'react'
import { hourAlignedNow } from '@/utils/nowIso'

interface DateRange {
  from: string
  to: string
}

interface UseCalendarDateRangeReturn {
  dateRange: DateRange
  handleDateRangeChange: (start: Date, end: Date) => void
}

/**
 * The window the calendar loads up front: the whole of last month through six months ahead —
 * deliberately much wider than any single view, so panning through it costs no query at all (see
 * `handleDateRangeChange`).
 *
 * Both ends are snapped to the **first of a UTC month**, not offset from "now". A rolling
 * `now - 1 month` looks equivalent and is not: the month grid starts on the Monday on or before the
 * 1st, while a rolling window starts on the same day *of the previous month* — which is only a day
 * or two before the 1st once you are late in the current one. So for the last days of every month
 * (40 days a year) the visible grid began before the window, the containment check in
 * `handleDateRangeChange` missed, and the calendar refetched on mount — the exact defect this pair
 * of functions exists to avoid, reappearing for a tenth of the year only.
 * Snapping also means the key only changes at month boundaries instead of hourly.
 *
 * Exported because the `calendar` route's `prefetch` has to produce the byte-identical query key —
 * it is the reason the maths here is deterministic rather than "close enough": `hourAlignedNow()`
 * so the server and the browser agree on the instant, and `getUTCMonth`/`setUTCMonth` so they
 * agree on the month too. Local-month arithmetic would disagree across timezones for the hours
 * either side of a month boundary (2026-08-01T00:30Z is still July in New York), and that
 * disagreement is invisible in dev and permanent in production for whoever sits on the wrong side.
 */
export function getInitialCalendarRange(): DateRange {
  const startOfThisMonth = hourAlignedNow()
  startOfThisMonth.setUTCDate(1)
  startOfThisMonth.setUTCHours(0, 0, 0, 0)

  const from = new Date(startOfThisMonth)
  from.setUTCMonth(from.getUTCMonth() - 1)
  // A week of slack, because the grid is measured in the *visitor's* zone while this window is
  // measured in UTC: the Monday on or before the 1st is up to six days early, and the visitor's
  // month can be one day off the UTC one. Without it the containment check misses for a few hours
  // a month, and only for visitors west of UTC — the kind of failure dev never sees.
  from.setUTCDate(from.getUTCDate() - 7)
  const to = new Date(startOfThisMonth)
  to.setUTCMonth(to.getUTCMonth() + 7)
  return {
    from: from.toISOString(),
    to: to.toISOString(),
  }
}

export function useCalendarDateRange(): UseCalendarDateRangeReturn {
  const [dateRange, setDateRange] = useState<DateRange>(getInitialCalendarRange)

  /**
   * Widen the loaded window only when the newly visible range leaves it.
   *
   * `CalendarView` reports its visible range from a mount effect, unconditionally — the very first
   * report is the month grid around today, which the initial window already contains. Without this
   * check that report replaced the range on every mount, so the SSR-prefetched events were thrown
   * away one render after hydration: the server ran a seven-month query, the grid it painted blanked
   * under the loading overlay, and the refetch was reported as a permanent prefetch gap. Bailing out
   * keeps the same query key (React skips the update when the updater returns the current state),
   * and pays the same dividend on every month/week step inside the window.
   *
   * Comparing the ISO strings directly is safe: both sides come from `toISOString()`, so they are
   * the same fixed-width UTC format and lexicographic order is chronological order.
   */
  const handleDateRangeChange = useCallback((start: Date, end: Date) => {
    const from = start.toISOString()
    const to = end.toISOString()
    setDateRange((current) => (from >= current.from && to <= current.to ? current : { from, to }))
  }, [])

  return { dateRange, handleDateRangeChange }
}
