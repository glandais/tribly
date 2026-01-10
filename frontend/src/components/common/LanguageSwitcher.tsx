import { useTranslation } from 'react-i18next'
import { NativeSelect } from '@mantine/core'
import { supportedLanguages, languageNames, type SupportedLanguage } from '../../i18n'

export function LanguageSwitcher() {
  const { i18n, t } = useTranslation()

  return (
    <NativeSelect
      value={i18n.language}
      onChange={(e) => i18n.changeLanguage(e.currentTarget.value)}
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
