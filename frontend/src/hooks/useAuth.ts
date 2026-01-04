import { useCallback, useEffect } from 'react'
import { useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import i18next from 'i18next'
import { useAuthStore } from '../store/authStore'
import {
  useGetCurrentUser,
  useUpdateCurrentUser,
  useDeleteCurrentUser,
  useUploadAvatar,
  useDeleteAvatar,
  getGetCurrentUserQueryKey,
} from '../api/endpoints/users/users'
import type { UpdateUserRequest } from '@/api/dto'

export function useAuth() {
  const queryClient = useQueryClient()
  const {
    user,
    isAuthenticated,
    isInitialized,
    isLoading,
    error,
    login,
    logout: storeLogout,
    setUser,
    setLoading,
    clearError,
  } = useAuthStore()

  // Fetch current user from backend - this creates/syncs the user on first call
  const { data: backendUser, refetch: refetchUser } = useGetCurrentUser({
    query: {
      enabled: isAuthenticated && isInitialized,
      staleTime: 1000 * 60 * 5,
      retry: 1,
    },
  })

  // Update store when backend user is fetched - store UserDto directly
  useEffect(() => {
    if (backendUser) {
      setUser(backendUser)
      setLoading(false) // User is loaded, app can render content
    }
  }, [backendUser, setUser, setLoading])

  const updateProfileMutation = useUpdateCurrentUser({
    mutation: {
      onSuccess: (updatedUser) => {
        queryClient.setQueryData(getGetCurrentUserQueryKey(), updatedUser)
        setUser(updatedUser)
        toast.success(i18next.t('profile:notifications.updated'))
      },
    },
  })

  const deleteAccountMutation = useDeleteCurrentUser({
    mutation: {
      onSuccess: () => {
        toast.success(i18next.t('profile:notifications.accountDeleted'))
        logout()
      },
    },
  })

  const uploadAvatarMutation = useUploadAvatar({
    mutation: {
      onSuccess: (updatedUser) => {
        queryClient.setQueryData(getGetCurrentUserQueryKey(), updatedUser)
        setUser(updatedUser)
        toast.success(i18next.t('profile:notifications.avatarUpdated'))
      },
    },
  })

  const deleteAvatarMutation = useDeleteAvatar({
    mutation: {
      onSuccess: (updatedUser) => {
        queryClient.setQueryData(getGetCurrentUserQueryKey(), updatedUser)
        setUser(updatedUser)
        toast.success(i18next.t('profile:notifications.avatarDeleted'))
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
    isLoading, // True until user is fetched from /me
    error,
    login,
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
