import { useTranslation } from 'react-i18next'
import { supportedLanguages, languageNames, type SupportedLanguage } from '../../i18n'

export function LanguageSwitcher() {
  const { i18n, t } = useTranslation()

  const handleChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    i18n.changeLanguage(e.target.value)
  }

  return (
    <select
      value={i18n.language}
      onChange={handleChange}
      aria-label={t('nav.language')}
      className="text-sm border-gray-300 rounded-md focus:ring-indigo-500 focus:border-indigo-500"
    >
      {supportedLanguages.map((lang) => (
        <option key={lang} value={lang}>
          {languageNames[lang as SupportedLanguage]}
        </option>
      ))}
    </select>
  )
}
