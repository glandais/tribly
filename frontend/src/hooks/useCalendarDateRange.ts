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
 * The window the calendar loads before FullCalendar reports its own visible grid: one month back,
 * six months ahead.
 *
 * Exported because the `calendar` route's `prefetch` has to produce the byte-identical query key —
 * it is the reason the maths here is deterministic rather than "close enough": `hourAlignedNow()`
 * so the server and the browser agree on the instant, and `getUTCMonth`/`setUTCMonth` so they
 * agree on the month too. Local-month arithmetic would disagree across timezones for the hours
 * either side of a month boundary (2026-08-01T00:30Z is still July in New York), and that
 * disagreement is invisible in dev and permanent in production for whoever sits on the wrong side.
 */
export function getInitialCalendarRange(): DateRange {
  const now = hourAlignedNow()
  const from = new Date(now)
  from.setUTCMonth(from.getUTCMonth() - 1)
  const to = new Date(now)
  to.setUTCMonth(to.getUTCMonth() + 6)
  return {
    from: from.toISOString(),
    to: to.toISOString(),
  }
}

export function useCalendarDateRange(): UseCalendarDateRangeReturn {
  const [dateRange, setDateRange] = useState<DateRange>(getInitialCalendarRange)

  const handleDateRangeChange = useCallback((start: Date, end: Date) => {
    setDateRange({
      from: start.toISOString(),
      to: end.toISOString(),
    })
  }, [])

  return { dateRange, handleDateRangeChange }
}
