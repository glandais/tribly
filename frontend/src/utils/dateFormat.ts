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

/**
 * The timezone to render dates/times in, and whether it's a guess rather than the visitor's own
 * confirmed preference. `useAuthStore` already resolves the current user per-request server-side
 * (via `getSSRAuth()`) and reactively client-side, so no dedicated store/module-load seeding is
 * needed here the way `theme`/`language` required.
 */
export function useEffectiveTimezone(): { timezone: string; isGuessed: boolean } {
  const user = useAuthStore(selectUser)
  const browserTimezone = useSyncExternalStore(
    subscribeToNothing,
    getBrowserTimezone,
    getServerTimezone
  )
  if (user?.timezone) return { timezone: user.timezone, isGuessed: false }
  return { timezone: browserTimezone, isGuessed: true }
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
 * Convert to datetime-local input format: "2025-06-15T09:00", in the given timezone.
 */
export function toDateTimeLocalValue(
  date: Date | string | null | undefined,
  timeZone: string = Intl.DateTimeFormat().resolvedOptions().timeZone
): string {
  const dateObj = toDate(date)
  if (!dateObj) return ''

  return formatInTimeZone(dateObj, timeZone, "yyyy-MM-dd'T'HH:mm")
}

/**
 * Parse datetime-local input value to Date
 * Assumes browser timezone
 */
export function fromDateTimeLocalValue(value: string): Date {
  return parseISO(value)
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
    formatRelative: (date: Date | string | null | undefined) => formatRelative(date, language),
    toDateTimeLocalValue: (date: Date | string | null | undefined) =>
      toDateTimeLocalValue(date, timezone),
    fromDateTimeLocalValue,
    isGuessedTimezone: isGuessed,
  }
}
