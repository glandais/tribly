import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import apiClient from '../api/client';

export type RideStatus = 'DRAFT' | 'PUBLISHED' | 'CANCELLED' | 'COMPLETED';
export type Visibility = 'PUBLIC' | 'TEAM' | 'PRIVATE';

export interface Ride {
  id: string;
  slug: string;
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

export function useRide(teamSlug: string | undefined, rideSlug: string | undefined) {
  return useQuery({
    queryKey: ['ride', teamSlug, rideSlug],
    queryFn: async () => {
      return apiClient.get<RideDetail>(`/teams/${teamSlug}/rides/${rideSlug}`);
    },
    enabled: !!teamSlug && !!rideSlug,
    staleTime: 1000 * 60 * 2,
  });
}

export function useRideGroups(teamSlug: string | undefined, rideSlug: string | undefined) {
  return useQuery({
    queryKey: ['rideGroups', teamSlug, rideSlug],
    queryFn: async () => {
      return apiClient.get<RideGroupListResponse>(`/teams/${teamSlug}/rides/${rideSlug}/groups`);
    },
    enabled: !!teamSlug && !!rideSlug,
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
      navigate(`/teams/${teamSlug}/rides/${ride.slug}`);
    },
  });
}

export function useUpdateRide(teamSlug: string | undefined, rideSlug: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (data: UpdateRideRequest) => {
      return apiClient.patch<Ride>(`/teams/${teamSlug}/rides/${rideSlug}`, data);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['ride', teamSlug, rideSlug] });
      queryClient.invalidateQueries({ queryKey: ['rides', teamSlug] });
    },
  });
}

export function useDeleteRide(teamSlug: string | undefined) {
  const queryClient = useQueryClient();
  const navigate = useNavigate();

  return useMutation({
    mutationFn: async (rideSlug: string) => {
      return apiClient.delete(`/teams/${teamSlug}/rides/${rideSlug}`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['rides', teamSlug] });
      navigate(`/teams/${teamSlug}/rides`);
    },
  });
}

export function useCreateGroup(teamSlug: string | undefined, rideSlug: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (data: CreateGroupRequest) => {
      return apiClient.post<RideGroup>(`/teams/${teamSlug}/rides/${rideSlug}/groups`, data);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['ride', teamSlug, rideSlug] });
      queryClient.invalidateQueries({ queryKey: ['rideGroups', teamSlug, rideSlug] });
    },
  });
}

export interface UpdateGroupRequest {
  name?: string;
  description?: string;
  averageSpeed?: number;
  maxParticipants?: number;
}

export function useUpdateGroup(teamSlug: string | undefined, rideSlug: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ groupId, data }: { groupId: string; data: UpdateGroupRequest }) => {
      return apiClient.patch<RideGroup>(`/teams/${teamSlug}/rides/${rideSlug}/groups/${groupId}`, data);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['ride', teamSlug, rideSlug] });
      queryClient.invalidateQueries({ queryKey: ['rideGroups', teamSlug, rideSlug] });
    },
  });
}

export function useDeleteGroup(teamSlug: string | undefined, rideSlug: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (groupId: string) => {
      return apiClient.delete(`/teams/${teamSlug}/rides/${rideSlug}/groups/${groupId}`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['ride', teamSlug, rideSlug] });
      queryClient.invalidateQueries({ queryKey: ['rideGroups', teamSlug, rideSlug] });
    },
  });
}

export function useJoinRide(teamSlug: string | undefined, rideSlug: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ groupId, notes }: { groupId: string; notes?: string }) => {
      return apiClient.post<RideParticipation>(
        `/teams/${teamSlug}/rides/${rideSlug}/groups/${groupId}/join`,
        notes ? { notes } : undefined
      );
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['ride', teamSlug, rideSlug] });
      queryClient.invalidateQueries({ queryKey: ['rideGroups', teamSlug, rideSlug] });
      queryClient.invalidateQueries({ queryKey: ['rides', teamSlug] });
    },
  });
}

export function useLeaveRide(teamSlug: string | undefined, rideSlug: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (groupId: string) => {
      return apiClient.post(`/teams/${teamSlug}/rides/${rideSlug}/groups/${groupId}/leave`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['ride', teamSlug, rideSlug] });
      queryClient.invalidateQueries({ queryKey: ['rideGroups', teamSlug, rideSlug] });
      queryClient.invalidateQueries({ queryKey: ['rides', teamSlug] });
    },
  });
}
