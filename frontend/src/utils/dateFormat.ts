import { formatDistanceToNow, parseISO } from 'date-fns'
import { formatInTimeZone } from 'date-fns-tz'
import { fr } from 'date-fns/locale/fr'
import { enUS } from 'date-fns/locale/en-US'
import type { Locale } from 'date-fns'
import { useTranslation } from 'react-i18next'
import i18n from '../i18n'
import { useAuthStore, selectUser } from '../store/authStore'

const isServer = typeof window === 'undefined'

// Deterministic value used when a visitor's real timezone truly cannot be known: an anonymous SSR
// render, or a signed-in user who never set one during SSR. Arbitrary but must be stable, since
// it's what the server commits to in the markup — the client then renders its own best guess
// (the stored preference, or else the browser's zone), and any resulting text difference is
// accepted and marked via `FormattedDate`/`FormattedDateTime`'s `suppressHydrationWarning`, rather
// than deferred to a post-hydration correction the way `hydrateAnonymousPreferences` handles
// unitSystem/theme/language.
const SERVER_FALLBACK_TIMEZONE = 'UTC'

/**
 * The timezone to render dates/times in, and whether it's a guess rather than the visitor's own
 * confirmed preference. `useAuthStore` already resolves the current user per-request server-side
 * (via `getSSRAuth()`) and reactively client-side, so no dedicated store/module-load seeding is
 * needed here the way `theme`/`language` required.
 */
export function useEffectiveTimezone(): { timezone: string; isGuessed: boolean } {
  const user = useAuthStore(selectUser)
  if (user?.timezone) return { timezone: user.timezone, isGuessed: false }
  if (isServer) return { timezone: SERVER_FALLBACK_TIMEZONE, isGuessed: true }
  return { timezone: Intl.DateTimeFormat().resolvedOptions().timeZone, isGuessed: true }
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
