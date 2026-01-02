import { format, formatDistanceToNow, parseISO } from 'date-fns'
import { formatInTimeZone } from 'date-fns-tz'
import { fr } from 'date-fns/locale/fr'
import { enUS } from 'date-fns/locale/en-US'
import type { Locale } from 'date-fns'
import { useTranslation } from 'react-i18next'
import i18n from '../i18n'

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
  language: string = 'fr'
): string {
  const dateObj = toDate(date)
  if (!dateObj) return ''

  const locale = getLocale(language)
  return format(dateObj, 'PPP', { locale })
}

/**
 * Format date + time: "15 juin 2025 à 09:00" (fr) / "June 15, 2025 at 9:00 AM" (en)
 */
export function formatDateTime(
  date: Date | string | null | undefined,
  language: string = 'fr'
): string {
  const dateObj = toDate(date)
  if (!dateObj) return ''

  const locale = getLocale(language)
  const pattern = i18n.t('dateFormats.dateTime', { lng: language })
  return format(dateObj, pattern, { locale })
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
 * Convert to datetime-local input format: "2025-06-15T09:00"
 * Uses browser timezone explicitly
 */
export function toDateTimeLocalValue(date: Date | string | null | undefined): string {
  const dateObj = toDate(date)
  if (!dateObj) return ''

  const browserTz = Intl.DateTimeFormat().resolvedOptions().timeZone
  return formatInTimeZone(dateObj, browserTz, "yyyy-MM-dd'T'HH:mm")
}

/**
 * Parse datetime-local input value to Date
 * Assumes browser timezone
 */
export function fromDateTimeLocalValue(value: string): Date {
  return parseISO(value)
}

/**
 * React hook that provides date formatting functions auto-synced with i18n language
 */
export function useFormattedDate() {
  const { i18n } = useTranslation()
  const language = i18n.language

  return {
    formatDate: (date: Date | string | null | undefined) => formatDate(date, language),
    formatDateTime: (date: Date | string | null | undefined) => formatDateTime(date, language),
    formatRelative: (date: Date | string | null | undefined) => formatRelative(date, language),
    toDateTimeLocalValue,
    fromDateTimeLocalValue,
  }
}
