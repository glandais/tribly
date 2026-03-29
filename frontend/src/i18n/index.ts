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

// Update HTML lang attribute and dayjs locale when language changes
const applyLanguage = (lng: string) => {
  document.documentElement.lang = lng
  dayjs.locale(lng)
}

// Set initial lang attribute and dayjs locale
applyLanguage(i18n.language)

// Listen for language changes
i18n.on('languageChanged', applyLanguage)

export default i18n
