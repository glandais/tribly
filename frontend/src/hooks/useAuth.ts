import { useCallback, useEffect, useRef } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useAuthStore } from '../store/authStore'
import { useNotificationStore } from '../store/notificationStore'
import { usersApi, unwrapResponse } from '../lib/apiClient'
import type { UserDto, UpdateUserRequest } from '../api/api'

// Get browser's preferred language (e.g., "en", "fr", "en-US" -> "en")
function getBrowserLocale(): string {
  const lang = navigator.language || (navigator as { userLanguage?: string }).userLanguage || 'en'
  // Return just the language code (e.g., "en" from "en-US")
  return lang.split('-')[0].toLowerCase()
}

// Get browser's timezone (e.g., "Europe/Paris", "America/New_York")
function getBrowserTimezone(): string {
  try {
    return Intl.DateTimeFormat().resolvedOptions().timeZone
  } catch {
    return 'UTC'
  }
}

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
        locale: backendUser.locale || undefined,
        timezone: backendUser.timezone || undefined,
      })
    }
  }, [backendUser?.id])

  // Auto-initialize user preferences from browser on first login
  // Detects if user still has default values and updates with browser info
  useEffect(() => {
    if (
      backendUser &&
      !preferencesInitialized.current &&
      backendUser.locale === 'en' &&
      backendUser.timezone === 'UTC'
    ) {
      preferencesInitialized.current = true
      const browserLocale = getBrowserLocale()
      const browserTimezone = getBrowserTimezone()

      // Only update if browser values differ from defaults
      if (browserLocale !== 'en' || browserTimezone !== 'UTC') {
        unwrapResponse(
          usersApi.updateCurrentUser({
            locale: browserLocale,
            timezone: browserTimezone,
          })
        )
          .then((updatedUser: UserDto) => {
            queryClient.setQueryData(['currentUser'], updatedUser)
            if (user) {
              setUser({
                ...user,
                locale: updatedUser.locale ?? undefined,
                timezone: updatedUser.timezone ?? undefined,
              })
            }
          })
          .catch(() => {
            // Silently fail - this is a nice-to-have feature
          })
      }
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
          locale: updatedUser.locale ?? undefined,
          timezone: updatedUser.timezone ?? undefined,
        })
      }

      // Show success notification
      useNotificationStore.getState().addNotification({
        message: '',
        type: 'success',
        duration: 4000,
        translationKey: 'notifications.profileUpdated',
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
        message: '',
        type: 'success',
        duration: 4000,
        translationKey: 'notifications.accountDeleted',
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
