import i18n from 'i18next'
import { initReactI18next } from 'react-i18next'
import LanguageDetector from 'i18next-browser-languagedetector'

// French translations
import frCommon from '../locales/fr/common.json'
import frAuth from '../locales/fr/auth.json'
import frTeams from '../locales/fr/teams.json'
import frRides from '../locales/fr/rides.json'
import frRoutes from '../locales/fr/routes.json'
import frProfile from '../locales/fr/profile.json'
import frErrors from '../locales/fr/errors.json'

// English translations
import enCommon from '../locales/en/common.json'
import enAuth from '../locales/en/auth.json'
import enTeams from '../locales/en/teams.json'
import enRides from '../locales/en/rides.json'
import enRoutes from '../locales/en/routes.json'
import enProfile from '../locales/en/profile.json'
import enErrors from '../locales/en/errors.json'

export const supportedLanguages = ['fr', 'en'] as const
export type SupportedLanguage = (typeof supportedLanguages)[number]

export const languageNames: Record<SupportedLanguage, string> = {
  fr: 'Français',
  en: 'English',
}

const resources = {
  fr: {
    common: frCommon,
    auth: frAuth,
    teams: frTeams,
    rides: frRides,
    routes: frRoutes,
    profile: frProfile,
    errors: frErrors,
  },
  en: {
    common: enCommon,
    auth: enAuth,
    teams: enTeams,
    rides: enRides,
    routes: enRoutes,
    profile: enProfile,
    errors: enErrors,
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
    ns: ['common', 'auth', 'teams', 'rides', 'routes', 'profile', 'errors'],
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

export default i18n
