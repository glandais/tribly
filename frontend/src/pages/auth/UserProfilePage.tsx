import { useState, useRef } from 'react'
import { useTranslation } from 'react-i18next'
import { CameraIcon, XMarkIcon } from '@heroicons/react/24/outline'
import { useAuth } from '../../hooks/useAuth'
import { LoadingSpinner } from '../../components/common/LoadingSpinner'
import { ConfirmDialog } from '../../components/common/ConfirmDialog'
import { UserAvatar } from '../../components/common/UserAvatar'

export function UserProfilePage() {
  const { t } = useTranslation('profile')
  const { t: tCommon } = useTranslation('common')
  const {
    user,
    isLoading,
    updateProfile,
    isUpdatingProfile,
    deleteAccount,
    isDeletingAccount,
    uploadAvatar,
    isUploadingAvatar,
    deleteAvatar,
    isDeletingAvatar,
    logout,
  } = useAuth()

  const avatarInputRef = useRef<HTMLInputElement>(null)
  const [isEditing, setIsEditing] = useState(false)
  const [displayName, setDisplayName] = useState(user?.displayName || '')
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false)

  if (isLoading || !user) {
    return (
      <div className="flex justify-center py-12">
        <LoadingSpinner size="lg" />
      </div>
    )
  }

  const handleSave = () => {
    updateProfile(
      { displayName },
      {
        onSuccess: () => {
          setIsEditing(false)
        },
      }
    )
  }

  const handleDelete = () => {
    deleteAccount()
  }

  const handleAvatarSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (file) {
      uploadAvatar(file)
    }
    if (avatarInputRef.current) {
      avatarInputRef.current.value = ''
    }
  }

  return (
    <div className="max-w-2xl mx-auto">
      <div className="bg-white shadow-sm rounded-lg">
        <div className="px-6 py-4 border-b border-gray-200">
          <h1 className="text-xl font-semibold text-gray-900">{t('title')}</h1>
        </div>

        <div className="p-6 space-y-6">
          <div className="flex items-center gap-6">
            <div className="relative">
              <UserAvatar user={user} size="xl" />
              <div className="absolute -bottom-1 -right-1 flex gap-1">
                <input
                  ref={avatarInputRef}
                  type="file"
                  accept="image/*"
                  onChange={handleAvatarSelect}
                  className="hidden"
                />
                <button
                  onClick={() => avatarInputRef.current?.click()}
                  disabled={isUploadingAvatar}
                  className="p-1.5 rounded-full bg-indigo-600 text-white hover:bg-indigo-700 shadow-sm disabled:opacity-50"
                  title={t('avatar.upload')}
                >
                  {isUploadingAvatar ? (
                    <LoadingSpinner size="sm" color="white" />
                  ) : (
                    <CameraIcon className="h-4 w-4" />
                  )}
                </button>
                {user.avatarUrl && (
                  <button
                    onClick={() => deleteAvatar()}
                    disabled={isDeletingAvatar}
                    className="p-1.5 rounded-full bg-red-600 text-white hover:bg-red-700 shadow-sm disabled:opacity-50"
                    title={t('avatar.remove')}
                  >
                    {isDeletingAvatar ? (
                      <LoadingSpinner size="sm" color="white" />
                    ) : (
                      <XMarkIcon className="h-4 w-4" />
                    )}
                  </button>
                )}
              </div>
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
