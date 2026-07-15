import i18n from 'i18next'
import { initReactI18next } from 'react-i18next'
import LanguageDetector from 'i18next-browser-languagedetector'
import dayjs from 'dayjs'
import 'dayjs/locale/fr'
import 'dayjs/locale/en'

// French translations
import frCommon from '../locales/fr/common.json'

// English translations
import enCommon from '../locales/en/common.json'

export const supportedLanguages = ['fr', 'en'] as const
export type SupportedLanguage = (typeof supportedLanguages)[number]

export const languageNames: Record<SupportedLanguage, string> = {
  fr: 'Français',
  en: 'English',
}

const resources = {
  fr: {
    common: frCommon,
  },
  en: {
    common: enCommon,
  },
}

const isServer = typeof window === 'undefined'

// Shared config used by both the client singleton and per-request server instances.
const i18nBaseConfig = {
  resources,
  fallbackLng: 'fr' as const, // French is the default
  supportedLngs: supportedLanguages,
  defaultNS: 'common',
  ns: ['common'],
  interpolation: {
    escapeValue: false, // React already escapes values
  },
}

/**
 * Create a per-request i18n instance for SSR.
 * Avoids mutating the global singleton, which would race under concurrent requests.
 * No browser language detector is used; the language is resolved by the server.
 * dayjs locale is NOT set here — dayjs.locale() is global and would race under
 * concurrent requests. Use dayjs(date).locale(lng) at call sites if needed.
 */
export async function createServerI18n(language: string) {
  const instance = i18n.createInstance()
  await instance.use(initReactI18next).init({
    ...i18nBaseConfig,
    lng: language || 'fr',
  })
  return instance
}

// Client-only initialization: browser language detection, dayjs locale, and the HTML lang
// attribute. Guarded so none of it runs during SSR (no document / no global dayjs mutation).
async function initClientI18n() {
  try {
    await i18n
      .use(LanguageDetector)
      .use(initReactI18next)
      .init({
        ...i18nBaseConfig,
        detection: {
          // htmlTag is highest priority so the initial (hydration) client render matches
          // the server-emitted <html lang>, which the server resolves from Accept-Language.
          // The persisted preference is re-applied below, after init, to avoid a mismatch.
          order: ['htmlTag', 'localStorage', 'navigator'],
          // Cache user language preference
          caches: ['localStorage'],
          lookupLocalStorage: 'i18nextLng',
        },
      })

    // Honor a persisted language preference only AFTER the hydration render. The server
    // only sees Accept-Language, so a saved preference that differs from <html lang> must
    // be applied post-hydration — this is a normal client re-render, not a mismatch.
    const saved = localStorage.getItem('i18nextLng')
    if (
      saved &&
      saved !== i18n.language &&
      supportedLanguages.includes(saved as SupportedLanguage)
    ) {
      await i18n.changeLanguage(saved)
    }
  } catch (err) {
    console.error('[i18n] Client initialization failed:', err)
  }

  // Update HTML lang attribute and dayjs locale when language changes
  const applyLanguage = (lng: string) => {
    document.documentElement.lang = lng
    dayjs.locale(lng)
  }

  applyLanguage(i18n.language)
  i18n.on('languageChanged', applyLanguage)
}

// Server-side, the global instance is initialized once with the bundled resources so that
// module-level t() call sites (date patterns, validation messages…) work during SSR. Its
// language is pinned to the fallback and NEVER mutated — per-request language lives in the
// createServerI18n instances; call sites that need the request language must pass { lng }
// explicitly (as useFormattedDate does). initAsync: false makes init synchronous, so the
// instance is ready before the first render.
if (isServer) {
  void i18n.use(initReactI18next).init({ ...i18nBaseConfig, lng: 'fr', initAsync: false })
}

// Resolves once client i18n init completes. entry-client awaits this before hydrating so the
// initial client render matches the server markup. On the server it resolves immediately
// (the synchronous init above has already run by module-load end).
export const i18nReady: Promise<void> = isServer
  ? Promise.resolve()
  : initClientI18n().catch((err) => {
      console.error('[i18n] Unexpected error:', err)
    })

export default i18n
