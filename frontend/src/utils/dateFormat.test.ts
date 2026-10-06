import { describe, expect, it } from 'vitest'
import { addCalendarDays } from './dateFormat'

// docs/LEDGER_*.md WEB-71: « ajouter une étape » is day n + 1 at the same wall-clock time.
describe('addCalendarDays', () => {
  it('keeps the wall-clock time across a daylight-saving change', () => {
    // 08:00 in Paris on Saturday 28 March 2026 (UTC+1); Paris is UTC+2 from the next day.
    expect(addCalendarDays('2026-03-28T07:00:00.000Z', 1, 'Europe/Paris')).toBe(
      '2026-03-29T06:00:00.000Z'
    )
  })

  it('counts days in the given zone, not the browser’s', () => {
    // 00:30 in Tokyo on 11 October is still 10 October in UTC and in Paris.
    expect(addCalendarDays('2026-10-10T15:30:00.000Z', 2, 'Asia/Tokyo')).toBe(
      '2026-10-12T15:30:00.000Z'
    )
  })

  it('leaves the instant alone for zero days', () => {
    expect(addCalendarDays('2026-10-11T06:30:00.000Z', 0, 'Europe/Paris')).toBe(
      '2026-10-11T06:30:00.000Z'
    )
  })
})
