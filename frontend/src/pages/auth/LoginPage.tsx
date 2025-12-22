import { useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useTranslation, Trans } from 'react-i18next'
import { ArrowRightOnRectangleIcon } from '@heroicons/react/24/outline'
import { useAuth } from '../../hooks/useAuth'

export function LoginPage() {
  const { t } = useTranslation('auth')
  const navigate = useNavigate()
  const { isAuthenticated, login } = useAuth()

  useEffect(() => {
    if (isAuthenticated) {
      navigate('/')
    }
  }, [isAuthenticated, navigate])

  return (
    <div className="min-h-[70vh] flex items-center justify-center">
      <div className="max-w-md w-full space-y-8 p-8 bg-white rounded-xl shadow-lg">
        <div className="text-center">
          <h1 className="text-3xl font-bold text-gray-900">{t('login.title')}</h1>
          <p className="mt-2 text-gray-600">{t('login.subtitle')}</p>
        </div>

        <div className="mt-8">
          <button
            onClick={() => login()}
            className="w-full flex items-center justify-center gap-3 px-4 py-3 border border-transparent text-base font-medium rounded-lg text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-hidden focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition-colors"
          >
            <ArrowRightOnRectangleIcon className="w-5 h-5" />
            {t('login.button')}
          </button>
        </div>

        <p className="mt-8 text-center text-sm text-gray-500">
          <Trans
            i18nKey="login.termsText"
            ns="auth"
            components={{
              termsLink: (
                <a href="/terms" className="font-medium text-indigo-600 hover:text-indigo-500" />
              ),
              privacyLink: (
                <a href="/privacy" className="font-medium text-indigo-600 hover:text-indigo-500" />
              ),
            }}
          />
        </p>
      </div>
    </div>
  )
}
