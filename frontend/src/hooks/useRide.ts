import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import apiClient from '../api/client';

export type RideStatus = 'DRAFT' | 'PUBLISHED' | 'CANCELLED' | 'COMPLETED';
export type Visibility = 'PUBLIC' | 'TEAM' | 'PRIVATE';

export interface Ride {
  id: number;
  title: string;
  description: string | null;
  date: string;
  startTime: string | null;
  status: RideStatus;
  visibility: Visibility;
  participantCount: number;
  groupCount: number;
  createdAt: string | null;
}

export interface RideGroup {
  id: number;
  name: string;
  description: string | null;
  averageSpeed: number | null;
  maxParticipants: number | null;
  currentParticipants: number;
  sortOrder: number;
}

export interface RideDetail extends Ride {
  groups: RideGroup[];
}

export interface RideParticipation {
  id: number;
  userId: number;
  status: 'REGISTERED' | 'CONFIRMED' | 'CANCELLED' | 'COMPLETED';
  registeredAt: string | null;
  notes: string | null;
}

export interface RideListResponse {
  rides: Ride[];
  total: number;
  page: number;
  size: number;
}

export interface RideGroupListResponse {
  data: RideGroup[];
}

export interface CreateRideRequest {
  title: string;
  description?: string;
  date: string;
  startTime?: string;
  visibility?: Visibility;
  groups?: CreateGroupRequest[];
}

export interface CreateGroupRequest {
  name: string;
  description?: string;
  averageSpeed?: number;
  maxParticipants?: number;
}

export interface UpdateRideRequest {
  title?: string;
  description?: string;
  date?: string;
  startTime?: string;
  status?: RideStatus;
  visibility?: Visibility;
}

interface UseRidesOptions {
  from?: string;
  to?: string;
  status?: RideStatus;
  page?: number;
  size?: number;
}

export function useRides(teamId: number | undefined, options: UseRidesOptions = {}) {
  const { from, to, status, page = 0, size = 20 } = options;

  return useQuery({
    queryKey: ['rides', teamId, { from, to, status, page, size }],
    queryFn: async () => {
      const params: Record<string, string | number> = { page, size };
      if (from) params.from = from;
      if (to) params.to = to;
      if (status) params.status = status;
      return apiClient.get<RideListResponse>(`/teams/${teamId}/rides`, { params });
    },
    enabled: !!teamId,
    staleTime: 1000 * 60 * 2,
  });
}

export function useRide(teamId: number | undefined, rideId: number | undefined) {
  return useQuery({
    queryKey: ['ride', teamId, rideId],
    queryFn: async () => {
      return apiClient.get<RideDetail>(`/teams/${teamId}/rides/${rideId}`);
    },
    enabled: !!teamId && !!rideId,
    staleTime: 1000 * 60 * 2,
  });
}

export function useRideGroups(teamId: number | undefined, rideId: number | undefined) {
  return useQuery({
    queryKey: ['rideGroups', teamId, rideId],
    queryFn: async () => {
      return apiClient.get<RideGroupListResponse>(`/teams/${teamId}/rides/${rideId}/groups`);
    },
    enabled: !!teamId && !!rideId,
    staleTime: 1000 * 60 * 2,
  });
}

export function useCreateRide(teamId: number | undefined, teamSlug: string) {
  const queryClient = useQueryClient();
  const navigate = useNavigate();

  return useMutation({
    mutationFn: async (data: CreateRideRequest) => {
      return apiClient.post<Ride>(`/teams/${teamId}/rides`, data);
    },
    onSuccess: (ride) => {
      queryClient.invalidateQueries({ queryKey: ['rides', teamId] });
      navigate(`/teams/${teamSlug}/rides/${ride.id}`);
    },
  });
}

export function useUpdateRide(teamId: number | undefined, rideId: number) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (data: UpdateRideRequest) => {
      return apiClient.patch<Ride>(`/teams/${teamId}/rides/${rideId}`, data);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['ride', teamId, rideId] });
      queryClient.invalidateQueries({ queryKey: ['rides', teamId] });
    },
  });
}

export function useDeleteRide(teamId: number | undefined, teamSlug: string) {
  const queryClient = useQueryClient();
  const navigate = useNavigate();

  return useMutation({
    mutationFn: async (rideId: number) => {
      return apiClient.delete(`/teams/${teamId}/rides/${rideId}`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['rides', teamId] });
      navigate(`/teams/${teamSlug}/rides`);
    },
  });
}

export function useCreateGroup(teamId: number | undefined, rideId: number) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (data: CreateGroupRequest) => {
      return apiClient.post<RideGroup>(`/teams/${teamId}/rides/${rideId}/groups`, data);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['ride', teamId, rideId] });
      queryClient.invalidateQueries({ queryKey: ['rideGroups', teamId, rideId] });
    },
  });
}

export function useJoinRide(teamId: number | undefined, rideId: number) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ groupId, notes }: { groupId: number; notes?: string }) => {
      return apiClient.post<RideParticipation>(
        `/teams/${teamId}/rides/${rideId}/groups/${groupId}/join`,
        notes ? { notes } : undefined
      );
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['ride', teamId, rideId] });
      queryClient.invalidateQueries({ queryKey: ['rideGroups', teamId, rideId] });
      queryClient.invalidateQueries({ queryKey: ['rides', teamId] });
    },
  });
}

export function useLeaveRide(teamId: number | undefined, rideId: number) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (groupId: number) => {
      return apiClient.post(`/teams/${teamId}/rides/${rideId}/groups/${groupId}/leave`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['ride', teamId, rideId] });
      queryClient.invalidateQueries({ queryKey: ['rideGroups', teamId, rideId] });
      queryClient.invalidateQueries({ queryKey: ['rides', teamId] });
    },
  });
}
