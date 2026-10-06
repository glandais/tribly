import { useSyncExternalStore } from 'react'
import { formatDistanceToNow, parseISO } from 'date-fns'
import { formatInTimeZone } from 'date-fns-tz'
import { fr } from 'date-fns/locale/fr'
import { enUS } from 'date-fns/locale/en-US'
import type { Locale } from 'date-fns'
import { useTranslation } from 'react-i18next'
import i18n from '../i18n'
import { useAuthStore, selectUser } from '../store/authStore'

// Deterministic value used when a visitor's real timezone truly cannot be known: an anonymous SSR
// render, or a signed-in user who never set one. Arbitrary but must be stable, since it's what the
// server commits to in the markup.
const SERVER_FALLBACK_TIMEZONE = 'UTC'

// The browser's zone, delivered as an external store so the *hydration* render still reads
// `SERVER_FALLBACK_TIMEZONE` (`getServerSnapshot`) and React re-renders with the real zone right
// after — the same post-hydration correction `hydrateAnonymousPreferences` performs for
// unitSystem/theme/language.
//
// Returning the browser's zone directly during hydration is the trap this replaces: the text
// differs from the server's, and `suppressHydrationWarning` does not *patch* a mismatched text
// node, it only silences the warning — so the server's UTC text stayed on screen until some
// unrelated re-render, and every SSR-rendered date read an hour or two off for any visitor without
// a `timezone` preference.
const subscribeToNothing = () => () => {}
const getBrowserTimezone = () => Intl.DateTimeFormat().resolvedOptions().timeZone
const getServerTimezone = () => SERVER_FALLBACK_TIMEZONE
const isClientSnapshot = () => true
const isServerSnapshot = () => false

/**
 * The timezone to render dates/times in, and whether it's a guess rather than the visitor's own
 * confirmed preference. `useAuthStore` already resolves the current user per-request server-side
 * (via `getSSRAuth()`) and reactively client-side, so no dedicated store/module-load seeding is
 * needed here the way `theme`/`language` required.
 */
export function useEffectiveTimezone(): {
  timezone: string
  isGuessed: boolean
  /**
   * The zone is the server's placeholder (`UTC`), not the reader's: an anonymous SSR render or the
   * hydration render of a reader without a preference. Nothing may be said *about the reader's own
   * clock* then — the « heure de Paris (06:00 chez vous) » mention of a rendezvous waits for the
   * post-hydration render (docs/LEDGER_*.md API-60).
   */
  isPlaceholder: boolean
} {
  const user = useAuthStore(selectUser)
  const browserTimezone = useSyncExternalStore(
    subscribeToNothing,
    getBrowserTimezone,
    getServerTimezone
  )
  const hydrated = useSyncExternalStore(subscribeToNothing, isClientSnapshot, isServerSnapshot)
  if (user?.timezone) return { timezone: user.timezone, isGuessed: false, isPlaceholder: false }
  return { timezone: browserTimezone, isGuessed: true, isPlaceholder: !hydrated }
}

// Locale map for quick access
const locales: Record<string, Locale> = {
  fr: fr,
  en: enUS,
}

/**
 * Get date-fns locale for the given language code
 */
function getLocale(language: string): Locale {
  return locales[language] || enUS
}

/**
 * Convert various date inputs to Date object
 * Handles null/undefined gracefully
 */
function toDate(date: Date | string | null | undefined): Date | null {
  if (!date) return null
  if (date instanceof Date) return date
  try {
    return parseISO(date)
  } catch {
    return null
  }
}

/**
 * Format full date: "15 juin 2025" (fr) / "June 15, 2025" (en)
 */
export function formatDate(
  date: Date | string | null | undefined,
  language: string = 'fr',
  timeZone: string = SERVER_FALLBACK_TIMEZONE
): string {
  const dateObj = toDate(date)
  if (!dateObj) return ''

  const locale = getLocale(language)
  return formatInTimeZone(dateObj, timeZone, 'PPP', { locale })
}

/**
 * Format date + time: "15 juin 2025 à 09:00" (fr) / "June 15, 2025 at 9:00 AM" (en)
 */
export function formatDateTime(
  date: Date | string | null | undefined,
  language: string = 'fr',
  timeZone: string = SERVER_FALLBACK_TIMEZONE
): string {
  const dateObj = toDate(date)
  if (!dateObj) return ''

  const locale = getLocale(language)
  const pattern = i18n.t('dateFormats.dateTime', { lng: language })
  return formatInTimeZone(dateObj, timeZone, pattern, { locale })
}

/**
 * Format with a date-fns pattern ("EEE", "d", "MMM", "HH:mm"…) in the given language and timezone:
 * the pieces of a date a layout draws separately (the day box of a calendar row).
 */
export function formatPattern(
  date: Date | string | null | undefined,
  pattern: string,
  language: string = 'fr',
  timeZone: string = SERVER_FALLBACK_TIMEZONE
): string {
  const dateObj = toDate(date)
  if (!dateObj) return ''
  return formatInTimeZone(dateObj, timeZone, pattern, { locale: getLocale(language) })
}

/**
 * Format the time of day alone: "09:00" (fr) / "9:00 AM" (en) — the passages of a ride, the hour
 * a forecast was fetched.
 */
export function formatTime(
  date: Date | string | null | undefined,
  language: string = 'fr',
  timeZone: string = SERVER_FALLBACK_TIMEZONE
): string {
  const dateObj = toDate(date)
  if (!dateObj) return ''

  return formatInTimeZone(dateObj, timeZone, 'p', { locale: getLocale(language) })
}

/**
 * Whether `date` falls on the same calendar day as `now`, on the wall clock of `timeZone` — the
 * test that picks a bare hour (« à 18:00 ») over a full date for a recent moment.
 */
export function isSameDay(
  date: Date | string | null | undefined,
  timeZone: string = SERVER_FALLBACK_TIMEZONE,
  now: Date = new Date()
): boolean {
  const dateObj = toDate(date)
  if (!dateObj) return false
  return (
    formatInTimeZone(dateObj, timeZone, 'yyyy-MM-dd') ===
    formatInTimeZone(now, timeZone, 'yyyy-MM-dd')
  )
}

/**
 * Format relative: "il y a 2 heures" (fr) / "2 hours ago" (en)
 */
export function formatRelative(
  date: Date | string | null | undefined,
  language: string = 'fr'
): string {
  const dateObj = toDate(date)
  if (!dateObj) return ''

  const locale = getLocale(language)
  return formatDistanceToNow(dateObj, { addSuffix: true, locale })
}

/**
 * React hook that provides date formatting functions auto-synced with i18n language and the
 * visitor's effective timezone (see `useEffectiveTimezone`).
 */
export function useFormattedDate() {
  const { i18n } = useTranslation()
  const language = i18n.language
  const { timezone, isGuessed } = useEffectiveTimezone()

  return {
    formatDate: (date: Date | string | null | undefined) => formatDate(date, language, timezone),
    formatDateTime: (date: Date | string | null | undefined) =>
      formatDateTime(date, language, timezone),
    formatTime: (date: Date | string | null | undefined) => formatTime(date, language, timezone),
    formatRelative: (date: Date | string | null | undefined) => formatRelative(date, language),
    formatPattern: (date: Date | string | null | undefined, pattern: string) =>
      formatPattern(date, pattern, language, timezone),
    isToday: (date: Date | string | null | undefined) => isSameDay(date, timezone),
    isGuessedTimezone: isGuessed,
  }
}
