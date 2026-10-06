import dayjs from 'dayjs'
import utc from 'dayjs/plugin/utc'
import timezone from 'dayjs/plugin/timezone'
import type { ScheduleViewLevel } from '@mantine/schedule'

dayjs.extend(utc)
dayjs.extend(timezone)

/** Monday of `d`'s week — `firstDayOfWeek=1`, the Mantine default no `DatesProvider` overrides. */
function startOfWeek(d: dayjs.Dayjs): dayjs.Dayjs {
  // (day()+6)%7 gives Mon=0..Sun=6
  return d.startOf('day').subtract((d.day() + 6) % 7, 'day')
}

/** Midnight starting `day` (a calendar date, held in UTC) in `tz`. */
function midnight(day: dayjs.Dayjs, tz: string): Date {
  return dayjs.tz(day.format('YYYY-MM-DD'), tz).toDate()
}

/**
 * The window a view actually paints, which is what the events query has to cover.
 *
 * Every branch is a pure function of `date`, `view` and `tz` — no viewport, no measurement — which is why
 * `useCalendarDateRange` can tell whether the window it prefetched already contains it, and why
 * it was wrong to file the calendar's second query as an un-prefetchable viewport read (August 2026;
 * the lesson is in docs/SSR-data-loading.md, « Reading a prefetch gap »).
 *
 * The week and day branches used to fall through to the *month* bounds. A visible week straddles
 * months (2026-08-31 shows Aug 31 → Sep 6), so those six September days were queried as August and
 * their events silently never rendered.
 *
 * The grid is drawn in the effective zone `tz` (the member's preference, else the browser's), so
 * its bounds are midnights *there*: a member set to Tokyo browsing from Paris used to get Paris
 * midnights, and lose the first hours of the grid (docs/LEDGER_*.md WEB-71). The days themselves
 * are counted on UTC dates, which have no daylight-saving change to skip or repeat an hour.
 */
export function getVisibleRange(
  date: string,
  view: ScheduleViewLevel,
  tz: string
): { start: Date; end: Date } {
  const d = dayjs.utc(date)
  let first: dayjs.Dayjs
  let afterLast: dayjs.Dayjs
  switch (view) {
    case 'year':
      first = d.startOf('year')
      afterLast = first.add(1, 'year')
      break
    case 'month':
      // Whole weeks from the Monday on or before the 1st to the Sunday on or after the last day —
      // 4 to 6 rows depending on the month, *not* always 6: `getMonthDays` only pads to six weeks
      // under `consistentWeeks && withOutsideDays`, and neither is set here.
      first = startOfWeek(d.startOf('month'))
      afterLast = startOfWeek(d.endOf('month')).add(7, 'day')
      break
    case 'week':
      first = startOfWeek(d)
      afterLast = first.add(7, 'day')
      break
    case 'day':
      first = d.startOf('day')
      afterLast = first.add(1, 'day')
      break
  }
  return { start: midnight(first, tz), end: new Date(midnight(afterLast, tz).getTime() - 1) }
}
