import { describe, expect, it } from 'vitest'
import { nextWeekdayAt } from './dateFormat'

describe('nextWeekdayAt', () => {
  // Friday 2026-09-25, 23:30 UTC — already Saturday 01:30 in Paris.
  const now = new Date('2026-09-25T23:30:00Z')

  it('reads the wall clock of the given zone, not the process', () => {
    expect(nextWeekdayAt(7, 8, 'UTC', now)).toBe('2026-09-27T08:00:00.000Z')
    expect(nextWeekdayAt(7, 8, 'Europe/Paris', now)).toBe('2026-09-27T06:00:00.000Z')
  })

  it('never returns today', () => {
    // Saturday in Paris: next Saturday is a week away; in UTC it is still Friday.
    expect(nextWeekdayAt(6, 8, 'Europe/Paris', now)).toBe('2026-10-03T06:00:00.000Z')
    expect(nextWeekdayAt(6, 8, 'UTC', now)).toBe('2026-09-26T08:00:00.000Z')
  })

  it('follows a DST change between today and the target day', () => {
    // Paris leaves summer time on 2026-10-25.
    expect(nextWeekdayAt(7, 8, 'Europe/Paris', new Date('2026-10-20T12:00:00Z'))).toBe(
      '2026-10-25T07:00:00.000Z'
    )
  })
})
