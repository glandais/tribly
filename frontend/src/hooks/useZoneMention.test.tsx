import { describe, it, expect, vi } from 'vitest'
import { renderHook } from '@testing-library/react'

vi.mock('react-i18next', () => ({
  useTranslation: () => ({
    t: (key: string, options: { ofCity: string }) => `${key}:${options.ofCity}`,
    i18n: { language: 'fr' },
  }),
}))

import { useZoneMention } from './useZoneMention'

// docs/LEDGER_*.md API-60, plan §7: the reader is in Europe/Paris (Vitest's fixed zone, no user).
const mention = (zone: string, wall?: string) =>
  renderHook(() => useZoneMention(zone, wall)).result.current

describe('useZoneMention', () => {
  it('names a zone whose offset differs from the reader’s', () => {
    expect(mention('Asia/Tokyo', '2026-10-11T08:00:00')).toBe('timezone.zoneMention:de Tokyo')
    expect(mention('Europe/London', '2026-10-11T08:00:00')).toBe('timezone.zoneMention:de Londres')
  })

  it('elides and contracts in French', () => {
    expect(mention('Europe/Athens', '2026-10-11T08:00:00')).toBe("timezone.zoneMention:d'Athènes")
    expect(mention('Africa/Cairo', '2026-07-11T08:00:00')).toBe('timezone.zoneMention:du Caire')
    expect(mention('Atlantic/Azores', '2026-10-11T08:00:00')).toBe(
      'timezone.zoneMention:des Açores'
    )
  })

  it('says nothing for a zone with the reader’s offset', () => {
    expect(mention('Europe/Paris', '2026-10-11T08:00:00')).toBeNull()
    expect(mention('Europe/Brussels', '2026-10-11T08:00:00')).toBeNull()
  })

  it('compares offsets at the event’s own date', () => {
    // Paris has left summer time on 25 October 2026, Casablanca stays at UTC+1 all year: same
    // offset in winter, different in summer.
    expect(mention('Africa/Casablanca', '2026-12-05T08:00:00')).toBeNull()
    expect(mention('Africa/Casablanca', '2026-07-04T08:00:00')).toBe(
      'timezone.zoneMention:de Casablanca'
    )
  })
})
