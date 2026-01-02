import { Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'

export function NotFoundPage() {
  const { t } = useTranslation('errors')
  return (
    <div className="flex flex-col items-center justify-center min-h-[60vh]">
      <h1 className="text-4xl font-bold text-gray-900 mb-4">{t('notFound.title')}</h1>
      <p className="text-lg text-gray-600 mb-6">{t('notFound.message')}</p>
      <Link to="/" className="text-indigo-600 hover:text-indigo-500 font-medium">
        {t('notFound.backHome')}
      </Link>
    </div>
  )
}
