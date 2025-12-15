import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import apiClient from '../api/client';

export type RideStatus = 'DRAFT' | 'PUBLISHED' | 'CANCELLED' | 'COMPLETED';
export type Visibility = 'PUBLIC' | 'TEAM' | 'PRIVATE';

export interface Ride {
  id: string;
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
  id: string;
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
  id: string;
  userId: string;
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

export function useRides(teamSlug: string | undefined, options: UseRidesOptions = {}) {
  const { from, to, status, page = 0, size = 20 } = options;

  return useQuery({
    queryKey: ['rides', teamSlug, { from, to, status, page, size }],
    queryFn: async () => {
      const params: Record<string, string | number> = { page, size };
      if (from) params.from = from;
      if (to) params.to = to;
      if (status) params.status = status;
      return apiClient.get<RideListResponse>(`/teams/${teamSlug}/rides`, { params });
    },
    enabled: !!teamSlug,
    staleTime: 1000 * 60 * 2,
  });
}

export function useRide(teamSlug: string | undefined, rideId: string | undefined) {
  return useQuery({
    queryKey: ['ride', teamSlug, rideId],
    queryFn: async () => {
      return apiClient.get<RideDetail>(`/teams/${teamSlug}/rides/${rideId}`);
    },
    enabled: !!teamSlug && !!rideId,
    staleTime: 1000 * 60 * 2,
  });
}

export function useRideGroups(teamSlug: string | undefined, rideId: string | undefined) {
  return useQuery({
    queryKey: ['rideGroups', teamSlug, rideId],
    queryFn: async () => {
      return apiClient.get<RideGroupListResponse>(`/teams/${teamSlug}/rides/${rideId}/groups`);
    },
    enabled: !!teamSlug && !!rideId,
    staleTime: 1000 * 60 * 2,
  });
}

export function useCreateRide(teamSlug: string | undefined) {
  const queryClient = useQueryClient();
  const navigate = useNavigate();

  return useMutation({
    mutationFn: async (data: CreateRideRequest) => {
      return apiClient.post<Ride>(`/teams/${teamSlug}/rides`, data);
    },
    onSuccess: (ride) => {
      queryClient.invalidateQueries({ queryKey: ['rides', teamSlug] });
      navigate(`/teams/${teamSlug}/rides/${ride.id}`);
    },
  });
}

export function useUpdateRide(teamSlug: string | undefined, rideId: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (data: UpdateRideRequest) => {
      return apiClient.patch<Ride>(`/teams/${teamSlug}/rides/${rideId}`, data);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['ride', teamSlug, rideId] });
      queryClient.invalidateQueries({ queryKey: ['rides', teamSlug] });
    },
  });
}

export function useDeleteRide(teamSlug: string | undefined) {
  const queryClient = useQueryClient();
  const navigate = useNavigate();

  return useMutation({
    mutationFn: async (rideId: string) => {
      return apiClient.delete(`/teams/${teamSlug}/rides/${rideId}`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['rides', teamSlug] });
      navigate(`/teams/${teamSlug}/rides`);
    },
  });
}

export function useCreateGroup(teamSlug: string | undefined, rideId: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (data: CreateGroupRequest) => {
      return apiClient.post<RideGroup>(`/teams/${teamSlug}/rides/${rideId}/groups`, data);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['ride', teamSlug, rideId] });
      queryClient.invalidateQueries({ queryKey: ['rideGroups', teamSlug, rideId] });
    },
  });
}

export function useJoinRide(teamSlug: string | undefined, rideId: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ groupId, notes }: { groupId: string; notes?: string }) => {
      return apiClient.post<RideParticipation>(
        `/teams/${teamSlug}/rides/${rideId}/groups/${groupId}/join`,
        notes ? { notes } : undefined
      );
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['ride', teamSlug, rideId] });
      queryClient.invalidateQueries({ queryKey: ['rideGroups', teamSlug, rideId] });
      queryClient.invalidateQueries({ queryKey: ['rides', teamSlug] });
    },
  });
}

export function useLeaveRide(teamSlug: string | undefined, rideId: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (groupId: string) => {
      return apiClient.post(`/teams/${teamSlug}/rides/${rideId}/groups/${groupId}/leave`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['ride', teamSlug, rideId] });
      queryClient.invalidateQueries({ queryKey: ['rideGroups', teamSlug, rideId] });
      queryClient.invalidateQueries({ queryKey: ['rides', teamSlug] });
    },
  });
}
