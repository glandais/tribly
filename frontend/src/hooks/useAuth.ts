import { useCallback, useEffect, useRef } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useAuthStore } from '../store/authStore'
import apiClient, { ApiClientError } from '../api/client'

interface UpdateUserRequest {
  displayName?: string
  locale?: string
  timezone?: string
}

interface BackendUser {
  id: string
  email: string
  displayName: string
  avatarUrl: string | null
  locale: string
  timezone: string
  createdAt: string | null
}

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
    setError,
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
      const response = await apiClient.get<BackendUser>('/users/me')
      return response
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
        locale: backendUser.locale,
        timezone: backendUser.timezone,
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
        apiClient
          .put<BackendUser>('/users/me', {
            locale: browserLocale,
            timezone: browserTimezone,
          })
          .then((updatedUser) => {
            queryClient.setQueryData(['currentUser'], updatedUser)
            if (user) {
              setUser({
                ...user,
                locale: updatedUser.locale,
                timezone: updatedUser.timezone,
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
      return apiClient.put<BackendUser>('/users/me', data)
    },
    onSuccess: (updatedUser) => {
      queryClient.setQueryData(['currentUser'], updatedUser)
      if (user) {
        setUser({
          ...user,
          displayName: updatedUser.displayName,
          locale: updatedUser.locale,
          timezone: updatedUser.timezone,
        })
      }
    },
    onError: (error: ApiClientError) => {
      setError(error.error.message)
    },
  })

  const deleteAccountMutation = useMutation({
    mutationFn: async () => {
      return apiClient.delete('/users/me')
    },
    onSuccess: () => {
      logout()
    },
    onError: (error: ApiClientError) => {
      setError(error.error.message)
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
