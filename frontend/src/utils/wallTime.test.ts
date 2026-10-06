import { describe, expect, it } from 'vitest'
import {
  addDaysToWallTime,
  instantToWallTime,
  nextWeekdayWallTime,
  normalizeWallTime,
  optionalWallTime,
  wallTimeToInstant,
} from './wallTime'

// docs/LEDGER_*.md API-60: event forms hold wall times; Vitest runs in Europe/Paris (vite.config.ts),
// so every case below names a zone other than the process's where it matters.

describe('normalizeWallTime', () => {
  it('turns Mantine’s space into the ISO T the API parses', () => {
    expect(normalizeWallTime('2026-10-11 08:00:00')).toBe('2026-10-11T08:00:00')
  })

  it('adds missing seconds and keeps an ISO wall time as it is', () => {
    expect(normalizeWallTime('2026-10-11T08:30')).toBe('2026-10-11T08:30:00')
    expect(normalizeWallTime('2026-10-11T08:30:15')).toBe('2026-10-11T08:30:15')
  })
})

describe('instantToWallTime / wallTimeToInstant', () => {
  it('reads an instant on the clock of the given zone, not the process’s', () => {
    expect(instantToWallTime('2026-10-10T23:00:00Z', 'Asia/Tokyo')).toBe('2026-10-11T08:00:00')
    expect(instantToWallTime('2026-10-11T06:00:00Z', 'Europe/Paris')).toBe('2026-10-11T08:00:00')
  })

  it('round-trips a wall time through its zone', () => {
    expect(wallTimeToInstant('2026-10-11T08:00:00', 'Asia/Tokyo').toISOString()).toBe(
      '2026-10-10T23:00:00.000Z'
    )
    expect(wallTimeToInstant('2026-10-11 08:00:00', 'America/New_York').toISOString()).toBe(
      '2026-10-11T12:00:00.000Z'
    )
  })

  it('leaves an absent date absent', () => {
    expect(optionalWallTime(undefined, 'Asia/Tokyo')).toBeUndefined()
    expect(optionalWallTime('2026-10-10T23:00:00Z', 'Asia/Tokyo')).toBe('2026-10-11T08:00:00')
  })
})

describe('addDaysToWallTime', () => {
  it('keeps the wall-clock time across a daylight-saving change', () => {
    // Paris moves to summer time on 29 March 2026: 08:00 stays 08:00.
    expect(addDaysToWallTime('2026-03-28T08:00:00', 1)).toBe('2026-03-29T08:00:00')
  })

  it('crosses months and years on the calendar', () => {
    expect(addDaysToWallTime('2026-12-31T23:30:00', 1)).toBe('2027-01-01T23:30:00')
    expect(addDaysToWallTime('2026-02-28T00:30:00', 1)).toBe('2026-03-01T00:30:00')
  })

  it('accepts the picker’s form and leaves zero days alone', () => {
    expect(addDaysToWallTime('2026-10-11 06:30:00', 0)).toBe('2026-10-11T06:30:00')
  })
})

describe('nextWeekdayWallTime', () => {
  // Friday 2026-09-25, 23:30 UTC — already Saturday 01:30 in Paris, Saturday 08:30 in Tokyo.
  const now = new Date('2026-09-25T23:30:00Z')

  it('reads « today » in the given zone', () => {
    expect(nextWeekdayWallTime(7, 8, 'UTC', now)).toBe('2026-09-27T08:00:00')
    expect(nextWeekdayWallTime(6, 8, 'UTC', now)).toBe('2026-09-26T08:00:00')
    // Saturday already in Paris and Tokyo: next Saturday is a week away.
    expect(nextWeekdayWallTime(6, 8, 'Europe/Paris', now)).toBe('2026-10-03T08:00:00')
    expect(nextWeekdayWallTime(6, 9, 'Asia/Tokyo', now)).toBe('2026-10-03T09:00:00')
  })

  it('stays at the same wall time across a DST change', () => {
    expect(nextWeekdayWallTime(7, 8, 'Europe/Paris', new Date('2026-10-20T12:00:00Z'))).toBe(
      '2026-10-25T08:00:00'
    )
  })
})
