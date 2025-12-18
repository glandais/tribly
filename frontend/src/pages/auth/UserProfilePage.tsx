import { useState, useEffect } from 'react'
import { useTranslation } from 'react-i18next'
import { useAuth } from '../../hooks/useAuth'
import { LoadingSpinner } from '../../components/common/LoadingSpinner'
import { ConfirmDialog } from '../../components/common/ConfirmDialog'

// Available timezones with their IANA identifiers
const TIMEZONES = [
  'UTC',
  'Europe/London',
  'Europe/Paris',
  'Europe/Berlin',
  'America/New_York',
  'America/Los_Angeles',
]

// Format timezone name using browser's Intl API
function formatTimezoneName(timezone: string, locale: string): string {
  try {
    const formatter = new Intl.DateTimeFormat(locale, {
      timeZone: timezone,
      timeZoneName: 'long',
    })
    const parts = formatter.formatToParts(new Date())
    const tzPart = parts.find((p) => p.type === 'timeZoneName')
    return tzPart?.value || timezone
  } catch {
    return timezone
  }
}

export function UserProfilePage() {
  const { t, i18n } = useTranslation('profile')
  const { t: tCommon } = useTranslation('common')
  const {
    user,
    isLoading,
    updateProfile,
    isUpdatingProfile,
    deleteAccount,
    isDeletingAccount,
    logout,
  } = useAuth()

  const [isEditing, setIsEditing] = useState(false)
  const [displayName, setDisplayName] = useState(user?.displayName || '')
  const [locale, setLocale] = useState(user?.locale || 'en')
  const [timezone, setTimezone] = useState(user?.timezone || 'UTC')
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false)

  // Sync i18n language when user locale changes
  useEffect(() => {
    if (user?.locale && user.locale !== i18n.language) {
      i18n.changeLanguage(user.locale)
    }
  }, [user?.locale, i18n])

  if (isLoading || !user) {
    return (
      <div className="flex justify-center py-12">
        <LoadingSpinner size="lg" />
      </div>
    )
  }

  const handleSave = () => {
    updateProfile(
      { displayName, locale, timezone },
      {
        onSuccess: () => {
          setIsEditing(false)
          // Change i18n language immediately
          if (locale !== i18n.language) {
            i18n.changeLanguage(locale)
          }
        },
      }
    )
  }

  const handleDelete = () => {
    deleteAccount()
  }

  return (
    <div className="max-w-2xl mx-auto">
      <div className="bg-white shadow-sm rounded-lg">
        <div className="px-6 py-4 border-b border-gray-200">
          <h1 className="text-xl font-semibold text-gray-900">{t('title')}</h1>
        </div>

        <div className="p-6 space-y-6">
          <div className="flex items-center gap-6">
            <div className="h-16 w-16 rounded-full bg-indigo-500 flex items-center justify-center text-white text-xl font-medium">
              {user.displayName
                .split(' ')
                .map((n) => n[0])
                .join('')
                .toUpperCase()
                .slice(0, 2)}
            </div>
            <div>
              <h2 className="text-lg font-medium text-gray-900">{user.displayName}</h2>
              <p className="text-gray-500">{user.email}</p>
            </div>
          </div>

          <hr className="border-gray-200" />

          {isEditing ? (
            <div className="space-y-4">
              <div>
                <label htmlFor="displayName" className="block text-sm font-medium text-gray-700">
                  {t('form.displayName.label')}
                </label>
                <input
                  type="text"
                  id="displayName"
                  value={displayName}
                  onChange={(e) => setDisplayName(e.target.value)}
                  className="mt-1 block w-full rounded-md border-gray-300 shadow-xs focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                />
              </div>

              <div>
                <label htmlFor="locale" className="block text-sm font-medium text-gray-700">
                  {t('form.language.label')}
                </label>
                <select
                  id="locale"
                  value={locale}
                  onChange={(e) => setLocale(e.target.value)}
                  className="mt-1 block w-full rounded-md border-gray-300 shadow-xs focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                >
                  <option value="en">{t('form.language.options.en')}</option>
                  <option value="fr">{t('form.language.options.fr')}</option>
                </select>
              </div>

              <div>
                <label htmlFor="timezone" className="block text-sm font-medium text-gray-700">
                  {t('form.timezone.label')}
                </label>
                <select
                  id="timezone"
                  value={timezone}
                  onChange={(e) => setTimezone(e.target.value)}
                  className="mt-1 block w-full rounded-md border-gray-300 shadow-xs focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                >
                  {TIMEZONES.map((tz) => (
                    <option key={tz} value={tz}>
                      {formatTimezoneName(tz, i18n.language)}
                    </option>
                  ))}
                </select>
              </div>

              <div className="flex gap-3 pt-4">
                <button
                  onClick={handleSave}
                  disabled={isUpdatingProfile}
                  className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50"
                >
                  {isUpdatingProfile ? (
                    <LoadingSpinner size="sm" color="white" />
                  ) : (
                    t('actions.save')
                  )}
                </button>
                <button
                  onClick={() => setIsEditing(false)}
                  className="inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50"
                >
                  {tCommon('buttons.cancel')}
                </button>
              </div>
            </div>
          ) : (
            <div className="space-y-4">
              <div>
                <dt className="text-sm font-medium text-gray-500">{t('form.displayName.label')}</dt>
                <dd className="mt-1 text-sm text-gray-900">{user.displayName}</dd>
              </div>
              <div>
                <dt className="text-sm font-medium text-gray-500">{t('form.email.label')}</dt>
                <dd className="mt-1 text-sm text-gray-900">{user.email}</dd>
              </div>
              <div>
                <dt className="text-sm font-medium text-gray-500">{t('form.language.label')}</dt>
                <dd className="mt-1 text-sm text-gray-900">
                  {t(`form.language.options.${user.locale}`)}
                </dd>
              </div>
              <div>
                <dt className="text-sm font-medium text-gray-500">{t('form.timezone.label')}</dt>
                <dd className="mt-1 text-sm text-gray-900">
                  {formatTimezoneName(user.timezone || 'UTC', i18n.language)}
                </dd>
              </div>

              <div className="pt-4">
                <button
                  onClick={() => setIsEditing(true)}
                  className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700"
                >
                  {t('actions.editProfile')}
                </button>
              </div>
            </div>
          )}

          <hr className="border-gray-200" />

          <div className="space-y-4">
            <h3 className="text-lg font-medium text-gray-900">{t('account.title')}</h3>

            <button
              onClick={logout}
              className="inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50"
            >
              {t('account.signOut')}
            </button>

            <div className="pt-4 border-t border-gray-200">
              <h4 className="text-sm font-medium text-red-600">{t('account.dangerZone.title')}</h4>
              <p className="mt-1 text-sm text-gray-500">
                {t('account.dangerZone.deleteDescription')}
              </p>

              <button
                onClick={() => setShowDeleteConfirm(true)}
                className="mt-4 inline-flex items-center px-4 py-2 border border-red-300 text-sm font-medium rounded-md text-red-700 bg-white hover:bg-red-50"
              >
                {t('account.dangerZone.deleteButton')}
              </button>
            </div>
          </div>
        </div>
      </div>

      <ConfirmDialog
        isOpen={showDeleteConfirm}
        onClose={() => setShowDeleteConfirm(false)}
        onConfirm={handleDelete}
        title={t('account.dangerZone.title')}
        message={t('account.dangerZone.confirmMessage')}
        confirmText={t('account.dangerZone.confirmButton')}
        variant="danger"
        isLoading={isDeletingAccount}
      />
    </div>
  )
}
