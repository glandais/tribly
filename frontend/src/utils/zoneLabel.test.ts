import { describe, expect, it } from 'vitest'
import { sameOffsetAt, zoneCityName, zoneCityOf } from './zoneLabel'
import fr from '@/locales/fr/common.json'
import en from '@/locales/en/common.json'

// docs/LEDGER_*.md API-60, plan §7 « La mention ».
describe('zoneCityName', () => {
  it('names a zone by the last segment of its identifier', () => {
    expect(zoneCityName('Asia/Tokyo', 'fr')).toBe('Tokyo')
    expect(zoneCityName('Europe/Paris', 'en')).toBe('Paris')
    expect(zoneCityName('America/Argentina/Buenos_Aires', 'en')).toBe('Buenos Aires')
  })

  it('turns underscores into spaces', () => {
    expect(zoneCityName('America/New_York', 'fr')).toBe('New York')
    expect(zoneCityName('America/Los_Angeles', 'en')).toBe('Los Angeles')
  })

  it('translates the common cities in French only', () => {
    expect(zoneCityName('Europe/London', 'fr')).toBe('Londres')
    expect(zoneCityName('Europe/Brussels', 'fr')).toBe('Bruxelles')
    expect(zoneCityName('Europe/Lisbon', 'fr-FR')).toBe('Lisbonne')
    expect(zoneCityName('Africa/Cairo', 'fr')).toBe('Le Caire')
    expect(zoneCityName('Asia/Singapore', 'fr')).toBe('Singapour')
    expect(zoneCityName('America/Montreal', 'fr')).toBe('Montréal')
    expect(zoneCityName('Indian/Reunion', 'fr')).toBe('La Réunion')

    expect(zoneCityName('Europe/London', 'en')).toBe('London')
    expect(zoneCityName('Africa/Cairo', 'en')).toBe('Cairo')
  })

  it('names UTC and the fixed offsets the backend returns at sea', () => {
    expect(zoneCityName('UTC', 'fr')).toBe('UTC')
    expect(zoneCityName('Etc/UTC', 'en')).toBe('UTC')
    expect(zoneCityName('Etc/GMT', 'fr')).toBe('UTC')
    // POSIX inverts the sign: Etc/GMT-9 is nine hours ahead of UTC.
    expect(zoneCityName('Etc/GMT-9', 'fr')).toBe('UTC+9')
    expect(zoneCityName('Etc/GMT+5', 'en')).toBe('UTC-5')
  })
})

describe('zoneCityOf', () => {
  it('builds the French « of » phrase: de, elision, contraction', () => {
    expect(zoneCityOf('Asia/Tokyo', 'fr')).toBe('de Tokyo')
    expect(zoneCityOf('Europe/Athens', 'fr')).toBe("d'Athènes")
    expect(zoneCityOf('Africa/Algiers', 'fr')).toBe("d'Alger")
    expect(zoneCityOf('Europe/Istanbul', 'fr')).toBe("d'Istanbul")
    expect(zoneCityOf('Europe/Helsinki', 'fr')).toBe("d'Helsinki")
    expect(zoneCityOf('Asia/Hong_Kong', 'fr')).toBe('de Hong Kong')
    expect(zoneCityOf('Africa/Cairo', 'fr')).toBe('du Caire')
    expect(zoneCityOf('Atlantic/Azores', 'fr')).toBe('des Açores')
    expect(zoneCityOf('Atlantic/Canary', 'fr-FR')).toBe('des Canaries')
    expect(zoneCityOf('Indian/Reunion', 'fr')).toBe('de La Réunion')
  })

  it('leaves UTC bare in French: « heure UTC+9 »', () => {
    expect(zoneCityOf('UTC', 'fr')).toBe('UTC')
    expect(zoneCityOf('Etc/GMT-9', 'fr')).toBe('UTC+9')
  })

  it('is the bare city in English', () => {
    expect(zoneCityOf('Europe/Athens', 'en')).toBe('Athens')
    expect(zoneCityOf('Africa/Cairo', 'en')).toBe('Cairo')
    expect(zoneCityOf('Atlantic/Azores', 'en')).toBe('Azores')
  })

  it('reads right once interpolated in both catalogs', () => {
    const fill = (text: string, zone: string, language: string) =>
      text
        .replace('{{city}}', zoneCityName(zone, language))
        .replace('{{ofCity}}', zoneCityOf(zone, language))
    expect(fill(fr['timezone.zoneMention'], 'Europe/Athens', 'fr')).toBe("heure d'Athènes")
    expect(fill(fr['timezone.zoneMention'], 'Africa/Cairo', 'fr')).toBe('heure du Caire')
    expect(fill(fr['timezone.zoneMention'], 'Atlantic/Azores', 'fr')).toBe('heure des Açores')
    expect(fill(en['timezone.zoneMention'], 'Africa/Cairo', 'en')).toBe('Cairo time')
  })
})

describe('sameOffsetAt', () => {
  const summer = '2026-07-11T06:00:00Z'
  const winter = '2026-01-10T07:00:00Z'

  it('compares offsets, not identifiers: Paris read from Brussels needs no mention', () => {
    expect(sameOffsetAt(summer, 'Europe/Paris', 'Europe/Brussels')).toBe(true)
    expect(sameOffsetAt(winter, 'Europe/Paris', 'Europe/Brussels')).toBe(true)
  })

  it('differs for Tokyo read from Paris', () => {
    expect(sameOffsetAt(summer, 'Asia/Tokyo', 'Europe/Paris')).toBe(false)
  })

  it('depends on the instant across daylight saving time', () => {
    // London is UTC+0 in winter like Etc/UTC, UTC+1 in summer.
    expect(sameOffsetAt(winter, 'Europe/London', 'UTC')).toBe(true)
    expect(sameOffsetAt(summer, 'Europe/London', 'UTC')).toBe(false)
    // Lagos stays at UTC+1; Paris leaves it on 29 March 2026.
    expect(sameOffsetAt('2026-03-22T12:00:00Z', 'Europe/Paris', 'Africa/Lagos')).toBe(true)
    expect(sameOffsetAt('2026-04-05T12:00:00Z', 'Europe/Paris', 'Africa/Lagos')).toBe(false)
  })

  it('accepts a Date or epoch milliseconds', () => {
    expect(sameOffsetAt(new Date(summer), 'Europe/Paris', 'Europe/Madrid')).toBe(true)
    expect(sameOffsetAt(Date.parse(summer), 'Europe/Paris', 'Asia/Tokyo')).toBe(false)
  })

  it('never matches an unknown identifier', () => {
    expect(sameOffsetAt(summer, 'Mars/Olympus_Mons', 'Europe/Paris')).toBe(false)
  })
})

describe('Vitest timezone', () => {
  it('runs under the fixed Europe/Paris zone (vite.config.ts test.env)', () => {
    expect(Intl.DateTimeFormat().resolvedOptions().timeZone).toBe('Europe/Paris')
    expect(new Date('2026-07-11T06:00:00Z').getTimezoneOffset()).toBe(-120)
  })
})
