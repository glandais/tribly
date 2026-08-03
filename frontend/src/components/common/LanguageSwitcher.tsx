import { useTranslation } from 'react-i18next'
import { NativeSelect } from '@mantine/core'
import { supportedLanguages, languageNames, type SupportedLanguage } from '../../i18n'
import { useUpdateMyPreferences } from '@/api/endpoints/users/users'
import { useAuthStore, selectIsAuthenticated } from '@/store/authStore'

export function LanguageSwitcher() {
  const { i18n, t } = useTranslation()
  const isAuthenticated = useAuthStore(selectIsAuthenticated)
  const mutation = useUpdateMyPreferences()

  const handleChange = (language: string) => {
    i18n.changeLanguage(language)
    // Anonymous visitors have nothing to persist the language to; partial PATCH sends only the
    // changed field, never the whole preference set.
    if (isAuthenticated) {
      mutation.mutate({ data: { language } })
    }
  }

  return (
    <NativeSelect
      value={i18n.language}
      onChange={(e) => handleChange(e.currentTarget.value)}
      aria-label={t('nav.language')}
      data={supportedLanguages.map((lang) => ({
        value: lang,
        label: languageNames[lang as SupportedLanguage],
      }))}
      size="xs"
      w="auto"
    />
  )
}
