import i18next from 'i18next'
import { DEFAULT_LOCALE, type Locale } from './paths.generated'

export function getCurrentLocale(): Locale {
  return (i18next.resolvedLanguage as Locale | undefined) ?? DEFAULT_LOCALE
}
