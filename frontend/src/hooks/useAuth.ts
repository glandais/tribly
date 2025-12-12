import { useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useAuthStore, User } from '../store/authStore';
import apiClient, { ApiClientError } from '../api/client';

interface UpdateUserRequest {
  displayName?: string;
  locale?: string;
  timezone?: string;
}

export function useAuth() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const {
    user,
    token,
    isAuthenticated,
    isLoading,
    error,
    login,
    logout: storeLogout,
    setLoading,
    setError,
    clearError,
  } = useAuthStore();

  const { data: currentUser, refetch: refetchUser } = useQuery({
    queryKey: ['currentUser'],
    queryFn: async () => {
      const response = await apiClient.get<User>('/users/me');
      return response;
    },
    enabled: isAuthenticated && !!token,
    staleTime: 1000 * 60 * 5,
    retry: false,
  });

  const updateProfileMutation = useMutation({
    mutationFn: async (data: UpdateUserRequest) => {
      return apiClient.put<User>('/users/me', data);
    },
    onSuccess: (updatedUser) => {
      queryClient.setQueryData(['currentUser'], updatedUser);
      useAuthStore.getState().setUser(updatedUser);
    },
    onError: (error: ApiClientError) => {
      setError(error.error.message);
    },
  });

  const deleteAccountMutation = useMutation({
    mutationFn: async () => {
      return apiClient.delete('/users/me');
    },
    onSuccess: () => {
      logout();
    },
    onError: (error: ApiClientError) => {
      setError(error.error.message);
    },
  });

  const logout = useCallback(() => {
    apiClient.post('/auth/logout').catch(() => {});

    storeLogout();
    queryClient.clear();
    navigate('/login');
  }, [storeLogout, queryClient, navigate]);

  const loginWithStrava = useCallback(() => {
    const redirectUri = `${window.location.origin}/auth/callback/strava`;
    const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || '/v1';
    window.location.href = `${apiBaseUrl}/auth/oauth/strava?redirect_uri=${encodeURIComponent(redirectUri)}`;
  }, []);

  const refreshToken = useCallback(async () => {
    if (!token) return false;

    try {
      setLoading(true);
      const response = await apiClient.post<{
        accessToken: string;
        refreshToken: string;
        expiresIn: number;
        user: User;
      }>('/auth/refresh', { refreshToken: token });

      login(response.user, response.accessToken);
      return true;
    } catch (error) {
      logout();
      return false;
    } finally {
      setLoading(false);
    }
  }, [token, login, logout, setLoading]);

  return {
    user: currentUser || user,
    token,
    isAuthenticated,
    isLoading,
    error,
    login,
    logout,
    loginWithStrava,
    refreshToken,
    updateProfile: updateProfileMutation.mutate,
    isUpdatingProfile: updateProfileMutation.isPending,
    deleteAccount: deleteAccountMutation.mutate,
    isDeletingAccount: deleteAccountMutation.isPending,
    refetchUser,
    clearError,
  };
}
