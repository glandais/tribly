import { describe, expect, it } from 'vitest'
import { formatInTimeZone } from 'date-fns-tz'
import { getVisibleRange } from './calendarRange'

/**
 * `getVisibleRange` is what the events query is keyed on, so a range narrower than the grid means
 * events that are painted but never fetched — invisible, with no error anywhere.
 */
describe('getVisibleRange', () => {
  const tz = 'Europe/Paris'
  const local = (d: Date) => formatInTimeZone(d, tz, 'yyyy-MM-dd')

  it('covers the month grid from the Monday before the 1st to the Sunday after the last day', () => {
    // August 2026: the 1st is a Saturday, the 31st a Monday — the grid runs 2026-07-27 → 2026-09-06
    const { start, end } = getVisibleRange('2026-08-15', 'month', tz)
    expect(local(start)).toBe('2026-07-27')
    expect(local(end)).toBe('2026-09-06')
  })

  it('covers a week that straddles two months', () => {
    // The week of Monday 2026-08-31 runs into September; the month bounds used before cut it short
    const { start, end } = getVisibleRange('2026-09-02', 'week', tz)
    expect(local(start)).toBe('2026-08-31')
    expect(local(end)).toBe('2026-09-06')
  })

  it('treats a Sunday as the last day of its week, not the first', () => {
    const { start, end } = getVisibleRange('2026-08-02', 'week', tz)
    expect(local(start)).toBe('2026-07-27')
    expect(local(end)).toBe('2026-08-02')
  })

  it('covers exactly the day in day view', () => {
    const { start, end } = getVisibleRange('2026-08-15', 'day', tz)
    expect(local(start)).toBe('2026-08-15')
    expect(local(end)).toBe('2026-08-15')
    expect(formatInTimeZone(start, tz, 'HH:mm')).toBe('00:00')
  })

  it('covers the whole year in year view', () => {
    const { start, end } = getVisibleRange('2026-08-15', 'year', tz)
    expect(local(start)).toBe('2026-01-01')
    expect(local(end)).toBe('2026-12-31')
  })

  it('starts and ends at midnights of the effective zone, not of the browser', () => {
    // A member set to Tokyo: the day runs 15:00 UTC the eve → 14:59:59.999 UTC.
    const { start, end } = getVisibleRange('2026-08-15', 'day', 'Asia/Tokyo')
    expect(start.toISOString()).toBe('2026-08-14T15:00:00.000Z')
    expect(end.toISOString()).toBe('2026-08-15T14:59:59.999Z')
  })

  it('keeps whole days across a daylight-saving change', () => {
    // The week of Sunday 2026-03-29, when Paris moves from UTC+1 to UTC+2.
    const { start, end } = getVisibleRange('2026-03-25', 'week', tz)
    expect(start.toISOString()).toBe('2026-03-22T23:00:00.000Z')
    expect(end.toISOString()).toBe('2026-03-29T21:59:59.999Z')
  })
})
