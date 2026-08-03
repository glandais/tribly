import { useCallback, useEffect } from 'react'
import { useQueryClient } from '@tanstack/react-query'
import { notifications } from '@mantine/notifications'
import i18next from 'i18next'
import { useMantineColorScheme } from '@mantine/core'
import { useAuthStore } from '../store/authStore'
import { usePreferencesStore, hydrateAnonymousPreferences } from '../store/preferencesStore'
import { mapThemePreference } from '../lib/theme'
import {
  useGetMe,
  useUpdateMe,
  useDeleteCurrentUser,
  useUploadAvatar,
  useDeleteAvatar,
  getGetMeQueryKey,
} from '../api/endpoints/users/users'
import type { UpdateUserRequest } from '@/api/dto'

export function useAuth() {
  const queryClient = useQueryClient()
  const {
    user,
    accessToken,
    isAuthenticated,
    isInitialized,
    isInitializing,
    isLoading,
    error,
    initialize,
    logout: storeLogout,
    setUser,
    setLoading,
    clearError,
  } = useAuthStore()

  // Initialize auth state on mount
  useEffect(() => {
    initialize()
    // Only affects an anonymously-rendered visitor with a locally-stored preference — a no-op
    // otherwise. See preferencesStore.ts for why this can't run at module load instead.
    hydrateAnonymousPreferences()
  }, [initialize])

  // Fetch current user from backend when authenticated
  const { data: backendUser, refetch: refetchUser } = useGetMe({
    query: {
      enabled: isAuthenticated && isInitialized && !!accessToken,
      staleTime: 1000 * 60 * 5,
      retry: 1,
    },
  })

  // Sync preferences from server
  const syncFromServer = usePreferencesStore((state) => state.syncFromServer)
  const { colorScheme, setColorScheme } = useMantineColorScheme()

  // Update store when backend user is fetched
  useEffect(() => {
    if (backendUser) {
      setUser(backendUser)
      setLoading(false)
      syncFromServer(backendUser.unitSystem)

      // Reconciles a page that rendered anonymously (e.g. an expired access token) and turned out
      // to be an authenticated visitor once /api/auth/refresh + /me resolved — the same role
      // syncFromServer plays for unitSystem above. A page already rendered with the right value
      // (the common case: SSR/module-load already derived it from the session) is a same-value
      // no-op here.
      const serverColorScheme = mapThemePreference(backendUser.theme)
      if (serverColorScheme !== colorScheme) {
        setColorScheme(serverColorScheme)
      }
      if (backendUser.language && backendUser.language !== i18next.language) {
        void i18next.changeLanguage(backendUser.language)
      }
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [backendUser, setUser, setLoading, syncFromServer])

  const updateProfileMutation = useUpdateMe({
    mutation: {
      onSuccess: (updatedUser) => {
        queryClient.setQueryData(getGetMeQueryKey(), updatedUser)
        setUser(updatedUser)
        notifications.show({ message: i18next.t('profile.notifications.updated'), color: 'green' })
      },
    },
  })

  const deleteAccountMutation = useDeleteCurrentUser({
    mutation: {
      onSuccess: () => {
        notifications.show({
          message: i18next.t('profile.notifications.accountDeleted'),
          color: 'green',
        })
        logout()
      },
    },
  })

  const uploadAvatarMutation = useUploadAvatar({
    mutation: {
      onSuccess: (updatedUser) => {
        queryClient.setQueryData(getGetMeQueryKey(), updatedUser)
        setUser(updatedUser)
        notifications.show({
          message: i18next.t('profile.notifications.avatarUpdated'),
          color: 'green',
        })
      },
    },
  })

  const deleteAvatarMutation = useDeleteAvatar({
    mutation: {
      onSuccess: (updatedUser) => {
        queryClient.setQueryData(getGetMeQueryKey(), updatedUser)
        setUser(updatedUser)
        notifications.show({
          message: i18next.t('profile.notifications.avatarDeleted'),
          color: 'green',
        })
      },
    },
  })

  const logout = useCallback(() => {
    queryClient.clear()
    storeLogout()
  }, [storeLogout, queryClient])

  // Wrap mutations to maintain existing API
  const updateProfile = useCallback(
    (
      data: UpdateUserRequest,
      options?: { onSuccess?: () => void; onError?: (error: unknown) => void }
    ) => updateProfileMutation.mutate({ data }, options),
    [updateProfileMutation]
  )

  const uploadAvatar = useCallback(
    (file: File, options?: { onSuccess?: () => void; onError?: (error: unknown) => void }) =>
      uploadAvatarMutation.mutate({ data: { file } }, options),
    [uploadAvatarMutation]
  )

  return {
    user,
    isAuthenticated,
    isInitialized,
    isInitializing,
    isLoading,
    error,
    logout,
    updateProfile,
    isUpdatingProfile: updateProfileMutation.isPending,
    deleteAccount: deleteAccountMutation.mutate,
    isDeletingAccount: deleteAccountMutation.isPending,
    uploadAvatar,
    isUploadingAvatar: uploadAvatarMutation.isPending,
    deleteAvatar: deleteAvatarMutation.mutate,
    isDeletingAvatar: deleteAvatarMutation.isPending,
    refetchUser,
    clearError,
  }
}
