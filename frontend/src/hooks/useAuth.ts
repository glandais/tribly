import { useCallback, useEffect } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import i18next from 'i18next'
import { useAuthStore } from '../store/authStore'
import { useNotificationStore } from '../store/notificationStore'
import { usersApi, unwrapResponse } from '../lib/apiClient'
import type { UpdateUserRequest } from '../api/api'

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
  const { data: backendUser, refetch: refetchUser } = useQuery({
    queryKey: ['currentUser'],
    queryFn: async () => {
      return await unwrapResponse(usersApi.getCurrentUser())
    },
    enabled: isAuthenticated && isInitialized,
    staleTime: 1000 * 60 * 5,
    retry: 1,
  })

  // Update store when backend user is fetched - store UserDto directly
  useEffect(() => {
    if (backendUser) {
      setUser(backendUser)
      setLoading(false) // User is loaded, app can render content
    }
  }, [backendUser, setUser, setLoading])

  const updateProfileMutation = useMutation({
    mutationFn: async (data: UpdateUserRequest) => {
      return await unwrapResponse(usersApi.updateCurrentUser(data))
    },
    onSuccess: (updatedUser) => {
      queryClient.setQueryData(['currentUser'], updatedUser)
      setUser(updatedUser)

      // Show success notification
      useNotificationStore.getState().addNotification({
        type: 'success',
        translatedMessage: i18next.t('profile:notifications.updated'),
        duration: 4000,
      })
    },
  })

  const deleteAccountMutation = useMutation({
    mutationFn: async () => {
      await unwrapResponse(usersApi.deleteCurrentUser())
    },
    onSuccess: () => {
      // Show success notification before logout
      useNotificationStore.getState().addNotification({
        type: 'success',
        translatedMessage: i18next.t('profile:notifications.accountDeleted'),
        duration: 4000,
      })

      logout()
    },
  })

  const logout = useCallback(() => {
    queryClient.clear()
    storeLogout()
  }, [storeLogout, queryClient])

  return {
    user,
    isAuthenticated,
    isInitialized,
    isLoading, // True until user is fetched from /me
    error,
    login,
    logout,
    updateProfile: updateProfileMutation.mutate,
    isUpdatingProfile: updateProfileMutation.isPending,
    deleteAccount: deleteAccountMutation.mutate,
    isDeletingAccount: deleteAccountMutation.isPending,
    refetchUser,
    clearError,
  }
}
