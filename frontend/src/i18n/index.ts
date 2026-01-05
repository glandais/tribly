import i18n from 'i18next'
import { initReactI18next } from 'react-i18next'
import LanguageDetector from 'i18next-browser-languagedetector'

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

i18n
  .use(LanguageDetector)
  .use(initReactI18next)
  .init({
    resources,
    fallbackLng: 'fr', // French is the default
    supportedLngs: supportedLanguages,
    defaultNS: 'common',
    ns: ['common'],
    interpolation: {
      escapeValue: false, // React already escapes values
    },
    detection: {
      // Order of language detection
      order: ['localStorage', 'navigator'],
      // Cache user language preference
      caches: ['localStorage'],
      lookupLocalStorage: 'i18nextLng',
    },
  })

// Update HTML lang attribute when language changes
// This ensures native browser elements (like datetime-local inputs) use the correct locale
const updateHtmlLang = (lng: string) => {
  document.documentElement.lang = lng
}

// Set initial lang attribute
updateHtmlLang(i18n.language)

// Listen for language changes
i18n.on('languageChanged', updateHtmlLang)

export default i18n
