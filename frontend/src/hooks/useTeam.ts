import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import apiClient from '../api/client';
import { useAuthStore } from '../store/authStore';

export interface Team {
  id: string;
  name: string;
  slug: string;
  description: string | null;
  logoUrl: string | null;
  coverImageUrl: string | null;
  isPublic: boolean;
  memberCount: number;
}

export interface TeamWithRole extends Team {
  role: 'ADMIN' | 'ORGANIZER' | 'MEMBER';
}

export interface TeamDetail extends Team {
  maxMembers: number | null;
  userRole: 'ADMIN' | 'ORGANIZER' | 'MEMBER' | null;
  createdAt: string | null;
}

export interface TeamMember {
  id: string;
  userId: string;
  displayName: string;
  avatarUrl: string | null;
  email: string;
  role: 'ADMIN' | 'ORGANIZER' | 'MEMBER';
  joinedAt: string | null;
}

export interface TeamListResponse {
  teams: Team[];
  total: number;
  page: number;
  size: number;
}

export interface MemberListResponse {
  members: TeamMember[];
  total: number;
  page: number;
  size: number;
}

export interface CreateTeamRequest {
  name: string;
  description?: string;
  isPublic?: boolean;
  maxMembers?: number;
}

export interface UpdateTeamRequest {
  name?: string;
  description?: string;
  isPublic?: boolean;
  logoUrl?: string;
  coverImageUrl?: string;
  maxMembers?: number;
}

interface UseTeamsOptions {
  search?: string;
  page?: number;
  size?: number;
}

export function useTeams(options: UseTeamsOptions = {}) {
  const { search, page = 0, size = 20 } = options;

  return useQuery({
    queryKey: ['teams', { search, page, size }],
    queryFn: async () => {
      const params: Record<string, string | number> = { page, size };
      if (search) params.search = search;
      return apiClient.get<TeamListResponse>('/teams', { params });
    },
    staleTime: 1000 * 60 * 2,
  });
}

export function useMyTeams() {
  return useQuery({
    queryKey: ['myTeams'],
    queryFn: async () => {
      return apiClient.get<TeamWithRole[]>('/teams/my');
    },
    staleTime: 1000 * 60 * 2,
  });
}

export function useTeam(slug: string | undefined) {
  // Include isAuthenticated in query key to refetch when auth state changes
  // This ensures userRole is correctly populated after login
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);

  return useQuery({
    queryKey: ['team', slug, { isAuthenticated }],
    queryFn: async () => {
      return apiClient.get<TeamDetail>(`/teams/${slug}`);
    },
    enabled: !!slug,
    staleTime: 1000 * 60 * 2,
  });
}

export function useTeamMembers(slug: string | undefined, page = 0, size = 50) {
  return useQuery({
    queryKey: ['teamMembers', slug, page, size],
    queryFn: async () => {
      return apiClient.get<MemberListResponse>(`/teams/${slug}/members`, {
        params: { page, size },
      });
    },
    enabled: !!slug,
    staleTime: 1000 * 60 * 2,
  });
}

export function useCreateTeam() {
  const queryClient = useQueryClient();
  const navigate = useNavigate();

  return useMutation({
    mutationFn: async (data: CreateTeamRequest) => {
      return apiClient.post<Team>('/teams', data);
    },
    onSuccess: (team) => {
      queryClient.invalidateQueries({ queryKey: ['teams'] });
      queryClient.invalidateQueries({ queryKey: ['myTeams'] });
      navigate(`/teams/${team.slug}`);
    },
  });
}

export function useUpdateTeam(slug: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (data: UpdateTeamRequest) => {
      return apiClient.put<Team>(`/teams/${slug}`, data);
    },
    onSuccess: (team) => {
      queryClient.invalidateQueries({ queryKey: ['team', slug] });
      queryClient.invalidateQueries({ queryKey: ['teams'] });
      queryClient.invalidateQueries({ queryKey: ['myTeams'] });
      queryClient.setQueryData(['team', team.slug], team);
    },
  });
}

export function useDeleteTeam(slug: string) {
  const queryClient = useQueryClient();
  const navigate = useNavigate();

  return useMutation({
    mutationFn: async () => {
      return apiClient.delete(`/teams/${slug}`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['teams'] });
      queryClient.invalidateQueries({ queryKey: ['myTeams'] });
      queryClient.removeQueries({ queryKey: ['team', slug] });
      navigate('/teams');
    },
  });
}

export function useJoinTeam(slug: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async () => {
      return apiClient.post<TeamMember>(`/teams/${slug}/members/join`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['team', slug] });
      queryClient.invalidateQueries({ queryKey: ['teamMembers', slug] });
      queryClient.invalidateQueries({ queryKey: ['myTeams'] });
    },
  });
}

export function useLeaveTeam(slug: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async () => {
      return apiClient.post(`/teams/${slug}/members/leave`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['team', slug] });
      queryClient.invalidateQueries({ queryKey: ['teamMembers', slug] });
      queryClient.invalidateQueries({ queryKey: ['myTeams'] });
    },
  });
}

export function useAddMember(slug: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({
      userId,
      role = 'MEMBER',
    }: {
      userId: string;
      role?: 'ADMIN' | 'ORGANIZER' | 'MEMBER';
    }) => {
      return apiClient.post<TeamMember>(`/teams/${slug}/members`, {
        userId,
        role,
      });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['team', slug] });
      queryClient.invalidateQueries({ queryKey: ['teamMembers', slug] });
    },
  });
}

export function useUpdateMemberRole(slug: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({
      memberId,
      role,
    }: {
      memberId: string;
      role: 'ADMIN' | 'ORGANIZER' | 'MEMBER';
    }) => {
      return apiClient.put<TeamMember>(`/teams/${slug}/members/${memberId}`, {
        role,
      });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['teamMembers', slug] });
    },
  });
}

export function useRemoveMember(slug: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (memberId: string) => {
      return apiClient.delete(`/teams/${slug}/members/${memberId}`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['team', slug] });
      queryClient.invalidateQueries({ queryKey: ['teamMembers', slug] });
    },
  });
}
