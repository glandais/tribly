import { describe, expect, it } from 'vitest'
import { getVisibleRange } from './calendarRange'

/**
 * `getVisibleRange` is what the events query is keyed on, so a range narrower than the grid means
 * events that are painted but never fetched — invisible, with no error anywhere.
 */
describe('getVisibleRange', () => {
  const local = (d: Date) =>
    `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`

  it('covers the month grid from the Monday before the 1st to the Sunday after the last day', () => {
    // August 2026: the 1st is a Saturday, the 31st a Monday — the grid runs 2026-07-27 → 2026-09-06
    const { start, end } = getVisibleRange('2026-08-15', 'month')
    expect(local(start)).toBe('2026-07-27')
    expect(local(end)).toBe('2026-09-06')
  })

  it('covers a week that straddles two months', () => {
    // The week of Monday 2026-08-31 runs into September; the month bounds used before cut it short
    const { start, end } = getVisibleRange('2026-09-02', 'week')
    expect(local(start)).toBe('2026-08-31')
    expect(local(end)).toBe('2026-09-06')
  })

  it('treats a Sunday as the last day of its week, not the first', () => {
    const { start, end } = getVisibleRange('2026-08-02', 'week')
    expect(local(start)).toBe('2026-07-27')
    expect(local(end)).toBe('2026-08-02')
  })

  it('covers exactly the day in day view', () => {
    const { start, end } = getVisibleRange('2026-08-15', 'day')
    expect(local(start)).toBe('2026-08-15')
    expect(local(end)).toBe('2026-08-15')
    expect(start.getHours()).toBe(0)
  })

  it('covers the whole year in year view', () => {
    const { start, end } = getVisibleRange('2026-08-15', 'year')
    expect(local(start)).toBe('2026-01-01')
    expect(local(end)).toBe('2026-12-31')
  })
})
