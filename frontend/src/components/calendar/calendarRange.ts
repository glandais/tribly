import dayjs from 'dayjs'
import type { ScheduleViewLevel } from '@mantine/schedule'

/** Monday of `d`'s week — `firstDayOfWeek=1`, the Mantine default no `DatesProvider` overrides. */
function startOfWeek(d: dayjs.Dayjs): dayjs.Dayjs {
  // (day()+6)%7 gives Mon=0..Sun=6
  return d.startOf('day').subtract((d.day() + 6) % 7, 'day')
}

/**
 * The window a view actually paints, which is what the events query has to cover.
 *
 * Every branch is a pure function of `date` and `view` — no viewport, no measurement — which is why
 * `useCalendarDateRange` can tell whether the window it prefetched already contains it, and why
 * `SSR-BUGS.md` was wrong to file the calendar's second query as an un-prefetchable viewport read.
 *
 * The week and day branches used to fall through to the *month* bounds. A visible week straddles
 * months (2026-08-31 shows Aug 31 → Sep 6), so those six September days were queried as August and
 * their events silently never rendered.
 */
export function getVisibleRange(date: string, view: ScheduleViewLevel): { start: Date; end: Date } {
  const d = dayjs(date)
  switch (view) {
    case 'year':
      return { start: d.startOf('year').toDate(), end: d.endOf('year').toDate() }
    case 'month': {
      // Whole weeks from the Monday on or before the 1st to the Sunday on or after the last day —
      // 4 to 6 rows depending on the month, *not* always 6: `getMonthDays` only pads to six weeks
      // under `consistentWeeks && withOutsideDays`, and neither is set here.
      const start = startOfWeek(d.startOf('month'))
      const end = startOfWeek(d.endOf('month')).add(7, 'day').subtract(1, 'millisecond')
      return { start: start.toDate(), end: end.toDate() }
    }
    case 'week': {
      const start = startOfWeek(d)
      return { start: start.toDate(), end: start.add(7, 'day').subtract(1, 'millisecond').toDate() }
    }
    case 'day':
      return { start: d.startOf('day').toDate(), end: d.endOf('day').toDate() }
  }
}
