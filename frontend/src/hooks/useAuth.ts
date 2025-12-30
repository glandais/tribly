import { useCallback, useEffect, useRef } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import i18next from 'i18next'
import { useAuthStore } from '../store/authStore'
import { useNotificationStore } from '../store/notificationStore'
import { usersApi, unwrapResponse } from '../lib/apiClient'
import type { UpdateUserRequest } from '../api/api'

export function useAuth() {
  const queryClient = useQueryClient()
  const preferencesInitialized = useRef(false)
  const {
    user,
    isAuthenticated,
    isInitialized,
    isLoading,
    error,
    initialize,
    login,
    logout: storeLogout,
    setUser,
    clearError,
  } = useAuthStore()

  // Initialize Keycloak on mount
  useEffect(() => {
    if (!isInitialized) {
      initialize()
    }
  }, [isInitialized, initialize])

  // Fetch current user from backend (syncs Keycloak user to DB)
  const { data: backendUser, refetch: refetchUser } = useQuery({
    queryKey: ['currentUser'],
    queryFn: async () => {
      return await unwrapResponse(usersApi.getCurrentUser())
    },
    enabled: isAuthenticated && isInitialized,
    staleTime: 1000 * 60 * 5,
    retry: false,
  })

  // Update store with backend user data (including database ID)
  useEffect(() => {
    if (backendUser && user) {
      setUser({
        ...user,
        dbId: backendUser.id,
        avatarUrl: backendUser.avatarUrl,
      })
    }
  }, [backendUser?.id])

  // Auto-initialize user preferences from browser on first login
  // Detects if user still has default values and updates with browser info
  useEffect(() => {
    if (backendUser && !preferencesInitialized.current) {
      preferencesInitialized.current = true
    }
  }, [backendUser?.id])

  const updateProfileMutation = useMutation({
    mutationFn: async (data: UpdateUserRequest) => {
      return await unwrapResponse(usersApi.updateCurrentUser(data))
    },
    onSuccess: (updatedUser) => {
      queryClient.setQueryData(['currentUser'], updatedUser)
      if (user && updatedUser) {
        setUser({
          ...user,
          displayName: updatedUser.displayName,
        })
      }

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
    user: user ? { ...user, dbId: backendUser?.id } : null,
    isAuthenticated,
    isInitialized,
    isLoading,
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
