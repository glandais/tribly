import i18next from 'i18next'
import { getSSRLocale } from '@/lib/ssrContext'
import { DEFAULT_LOCALE, type Locale } from './paths.generated'

export function getCurrentLocale(): Locale {
  // During SSR the per-request locale wins (i18next is a shared singleton on the server);
  // off-server it falls back to the resolved client language, then the default.
  return getSSRLocale() ?? (i18next.resolvedLanguage as Locale | undefined) ?? DEFAULT_LOCALE
}
