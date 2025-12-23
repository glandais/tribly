import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import { teamsApi, teamMembersApi, unwrapResponse } from '../lib/apiClient'
import { useAuthStore } from '../store/authStore'
import { useNotificationStore } from '../store/notificationStore'
import type { TeamDetailDto, MemberDto, TeamRequest } from '../api/api'
import { TeamRole } from '../api/api'

// Re-export types for convenience
export type { TeamDetailDto, MemberDto }
export { TeamRole }

interface UseTeamsOptions {
  search?: string
  page?: number
  size?: number
}

export function useTeams(options: UseTeamsOptions = {}) {
  const { search, page = 0, size = 20 } = options

  return useQuery({
    queryKey: ['teams', { search, page, size }],
    queryFn: async () => {
      return await unwrapResponse(teamsApi.listTeams(undefined, page, search, size))
    },
    staleTime: 1000 * 60 * 2,
  })
}

export function useMyTeams() {
  return useQuery({
    queryKey: ['myTeams'],
    queryFn: async () => {
      return await unwrapResponse(teamsApi.listTeams(true, 0, undefined, 100))
    },
    staleTime: 1000 * 60 * 2,
  })
}

export function useTeam(slug: string | undefined) {
  // Include isAuthenticated in query key to refetch when auth state changes
  // This ensures userRole is correctly populated after login
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated)

  return useQuery({
    queryKey: ['team', slug, { isAuthenticated }],
    queryFn: async () => {
      if (!slug) throw new Error('Team slug is required')
      return await unwrapResponse(teamsApi.getTeam(slug))
    },
    enabled: !!slug,
    staleTime: 1000 * 60 * 2,
  })
}

export function useTeamMembers(slug: string | undefined, page = 0, size = 50) {
  return useQuery({
    queryKey: ['teamMembers', slug, page, size],
    queryFn: async () => {
      if (!slug) throw new Error('Team slug is required')
      return await unwrapResponse(teamMembersApi.getMembers(slug, page, size))
    },
    enabled: !!slug,
    staleTime: 1000 * 60 * 2,
  })
}

export function useCreateTeam() {
  const queryClient = useQueryClient()
  const navigate = useNavigate()

  return useMutation({
    mutationFn: async (data: TeamRequest) => {
      return await unwrapResponse(teamsApi.createTeam(data))
    },
    onSuccess: (team) => {
      queryClient.invalidateQueries({ queryKey: ['teams'] })
      queryClient.invalidateQueries({ queryKey: ['myTeams'] })

      // Show success notification
      useNotificationStore.getState().addNotification({
        message: '',
        type: 'success',
        duration: 4000,
        translationKey: 'notifications.teamCreated',
      })

      if (team) {
        navigate(`/teams/${team.slug}`)
      }
    },
  })
}

export function useUpdateTeam(slug: string) {
  const queryClient = useQueryClient()
  const navigate = useNavigate()

  return useMutation({
    mutationFn: async (data: TeamRequest) => {
      return await unwrapResponse(teamsApi.updateTeam(slug, data))
    },
    onSuccess: (team) => {
      queryClient.invalidateQueries({ queryKey: ['team', slug] })
      queryClient.invalidateQueries({ queryKey: ['teams'] })
      queryClient.invalidateQueries({ queryKey: ['myTeams'] })

      // Show success notification
      useNotificationStore.getState().addNotification({
        message: '',
        type: 'success',
        duration: 4000,
        translationKey: 'notifications.teamUpdated',
      })

      if (team) {
        queryClient.setQueryData(['team', team.slug], team)
        navigate(`/teams/${team.slug}`)
      }
    },
  })
}

export function useDeleteTeam(slug: string) {
  const queryClient = useQueryClient()
  const navigate = useNavigate()

  return useMutation({
    mutationFn: async () => {
      await unwrapResponse(teamsApi.deleteTeam(slug))
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['teams'] })
      queryClient.invalidateQueries({ queryKey: ['myTeams'] })
      queryClient.removeQueries({ queryKey: ['team', slug] })

      // Show success notification
      useNotificationStore.getState().addNotification({
        message: '',
        type: 'success',
        duration: 4000,
        translationKey: 'notifications.teamDeleted',
      })

      navigate('/teams')
    },
  })
}

export function useJoinTeam(slug: string) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: async () => {
      return await unwrapResponse(teamMembersApi.joinTeam(slug))
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['team', slug] })
      queryClient.invalidateQueries({ queryKey: ['teamMembers', slug] })
      queryClient.invalidateQueries({ queryKey: ['myTeams'] })
    },
  })
}

export function useLeaveTeam(slug: string) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: async () => {
      await unwrapResponse(teamMembersApi.leaveTeam(slug))
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['team', slug] })
      queryClient.invalidateQueries({ queryKey: ['teamMembers', slug] })
      queryClient.invalidateQueries({ queryKey: ['myTeams'] })
    },
  })
}

export function useAddMember(slug: string) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: async ({ userId, role }: { userId: string; role?: TeamRole }) => {
      return await unwrapResponse(teamMembersApi.addMember(slug, { userId, role }))
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['team', slug] })
      queryClient.invalidateQueries({ queryKey: ['teamMembers', slug] })

      // Show success notification
      useNotificationStore.getState().addNotification({
        message: '',
        type: 'success',
        duration: 4000,
        translationKey: 'notifications.memberAdded',
      })
    },
  })
}

export function useUpdateMemberRole(slug: string) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: async ({ memberId, role }: { memberId: string; role: TeamRole }) => {
      return await unwrapResponse(teamMembersApi.updateMemberRole(slug, memberId, { role }))
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['teamMembers', slug] })
    },
  })
}

export function useRemoveMember(slug: string) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: async (memberId: string) => {
      await unwrapResponse(teamMembersApi.removeMember(slug, memberId))
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['team', slug] })
      queryClient.invalidateQueries({ queryKey: ['teamMembers', slug] })

      // Show success notification
      useNotificationStore.getState().addNotification({
        message: '',
        type: 'success',
        duration: 4000,
        translationKey: 'notifications.memberRemoved',
      })
    },
  })
}
