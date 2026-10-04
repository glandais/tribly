import { useTranslation } from 'react-i18next'
import { NativeSelect } from '@mantine/core'
import {
  supportedLanguages,
  languageNames,
  persistLanguageChoice,
  type SupportedLanguage,
} from '../../i18n'
import { useQueryClient } from '@tanstack/react-query'
import { getGetMeQueryKey, useUpdateMyPreferences } from '@/api/endpoints/users/users'
import { useAuthStore, selectIsAuthenticated } from '@/store/authStore'

interface LanguageSwitcherProps {
  /** A visible label (the profile's preferences page); the header's compact select has none. */
  label?: string
}

export function LanguageSwitcher({ label }: LanguageSwitcherProps = {}) {
  const { i18n, t } = useTranslation()
  const queryClient = useQueryClient()
  const isAuthenticated = useAuthStore(selectIsAuthenticated)
  const mutation = useUpdateMyPreferences()

  const handleChange = (language: string) => {
    i18n.changeLanguage(language)
    // The cookie is what the SSR server renders the next document in — the only persistence an
    // anonymous visitor has. A signed-in visitor also gets the backend preference, which the server
    // applies first; partial PATCH sends only the changed field, never the whole preference set.
    persistLanguageChoice(language)
    if (isAuthenticated) {
      mutation.mutate(
        { data: { language } },
        { onSuccess: (user) => queryClient.setQueryData(getGetMeQueryKey(), user) }
      )
    }
  }

  return (
    <NativeSelect
      value={i18n.language}
      onChange={(e) => handleChange(e.currentTarget.value)}
      label={label}
      aria-label={label ? undefined : t('nav.language')}
      data={supportedLanguages.map((lang) => ({
        value: lang,
        label: languageNames[lang as SupportedLanguage],
      }))}
      size={label ? 'sm' : 'xs'}
      w={label ? undefined : 'auto'}
    />
  )
}
