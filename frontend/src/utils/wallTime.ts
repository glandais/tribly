import { formatInTimeZone, fromZonedTime } from 'date-fns-tz'

/**
 * Wall times: what an event form holds and sends (docs/LEDGER_*.md API-60, plan §6).
 *
 * A rendezvous is typed as the clock reads where it happens — `2026-10-11T08:00:00`, no offset —
 * and the backend reads it in the zone it resolves for the entity (start place, route, team). The
 * form therefore never converts: these helpers only move between a wall time and an instant at the
 * edges (an entity loaded from the API, a « publish later » that must be in the future), and do
 * calendar arithmetic on the string itself.
 */

/** A wall time without offset, `yyyy-MM-ddTHH:mm:ss` (seconds optional on input). */
export type WallTime = string

const WALL_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"
const WALL_PATTERN = /^(\d{4})-(\d{2})-(\d{2})[T ](\d{2}):(\d{2})(?::(\d{2}))?/

/**
 * A picker's value as the API wants it. Mantine's `DateTimePicker` hands back
 * `YYYY-MM-DD HH:mm:ss`, with a space, which the backend's ISO parser refuses: the space becomes a
 * `T`, and missing seconds are added.
 */
export function normalizeWallTime(value: string): WallTime {
  const match = WALL_PATTERN.exec(value)
  if (!match) return value
  const [, y, mo, d, h, mi, s] = match
  return `${y}-${mo}-${d}T${h}:${mi}:${s ?? '00'}`
}

/** `instant` (an API date, or a `Date`) as the clock reads it in `timeZone`. */
export function instantToWallTime(instant: string | Date, timeZone: string): WallTime {
  return formatInTimeZone(instant, timeZone, WALL_FORMAT)
}

/** The instant `wall` names in `timeZone`. */
export function wallTimeToInstant(wall: WallTime, timeZone: string): Date {
  return fromZonedTime(normalizeWallTime(wall).replace('T', ' '), timeZone)
}

/** `instant` as a wall time of `timeZone`, or `undefined` for no date. */
export function optionalWallTime(
  instant: string | null | undefined,
  timeZone: string
): WallTime | undefined {
  return instant ? instantToWallTime(instant, timeZone) : undefined
}

/**
 * `wall` moved `days` calendar days, at the same wall-clock time — on the string itself, so that
 * neither the browser's zone nor a daylight-saving change between the two days can move the hour
 * (docs/LEDGER_*.md WEB-71, API-60: « ajouter une étape » is J+n at the same wall time).
 */
export function addDaysToWallTime(wall: WallTime, days: number): WallTime {
  const normalized = normalizeWallTime(wall)
  const day = new Date(`${normalized.slice(0, 10)}T00:00:00Z`)
  day.setUTCDate(day.getUTCDate() + days)
  return `${day.toISOString().slice(0, 10)}${normalized.slice(10)}`
}

/**
 * The next `isoWeekday` (1 = Monday … 7 = Sunday, never today) at `hour`:00, as a wall time of
 * `timeZone` — a form's default date. « Today » is read in `timeZone` (the team's), so the SSR
 * server and the browser compute the same string and the form hydrates.
 */
export function nextWeekdayWallTime(
  isoWeekday: number,
  hour: number,
  timeZone: string,
  now: Date = new Date()
): WallTime {
  const todayWeekday = Number(formatInTimeZone(now, timeZone, 'i'))
  const days = (isoWeekday - todayWeekday + 7) % 7 || 7
  const today = `${formatInTimeZone(now, timeZone, 'yyyy-MM-dd')}T${String(hour).padStart(2, '0')}:00:00`
  return addDaysToWallTime(today, days)
}
