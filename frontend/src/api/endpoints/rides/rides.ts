import { useMutation, useQuery } from '@tanstack/react-query'
import type {
  DataTag,
  DefinedInitialDataOptions,
  DefinedUseQueryResult,
  MutationFunction,
  QueryClient,
  QueryFunction,
  QueryKey,
  UndefinedInitialDataOptions,
  UseMutationOptions,
  UseMutationResult,
  UseQueryOptions,
  UseQueryResult,
} from '@tanstack/react-query'

import type {
  ErrorResponse,
  RideDto,
  RideParticipationDto,
  RideRequest,
  SlugChangeRequest,
} from '../../dto'

import { axiosMutator } from '../../../lib/axiosInstance.ts'
import type { ErrorType, BodyType } from '../../../lib/axiosInstance.ts'

type SecondParameter<T extends (...args: never) => unknown> = Parameters<T>[1]

const withQueryKey = <T extends object, K>(query: T, queryKey: K): T & { queryKey: K } => {
  const result = { queryKey } as T & { queryKey: K }
  for (const key of Object.keys(query)) {
    // The explicit queryKey always wins, matching the previous
    // `{ ...query, queryKey }` spread where it was set last.
    if (key === 'queryKey') continue
    Object.defineProperty(result, key, {
      enumerable: true,
      configurable: true,
      get: () => (query as Record<string, unknown>)[key],
    })
  }
  return result
}

/**
 * Create a new ride with optional groups
 * @summary Create ride
 */
export const createRide = (
  teamSlug: string,
  rideRequest: BodyType<RideRequest>,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<RideDto>(
    {
      url: `/api/teams/${teamSlug}/rides`,
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      data: rideRequest,
      signal,
    },
    options
  )
}

export const getCreateRideMutationKey = () => ['createRide'] as const

export const getCreateRideMutationOptions = <
  TError = ErrorType<ErrorResponse>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof createRide>>,
    TError,
    CreateRideMutationVariables,
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof createRide>>,
  TError,
  CreateRideMutationVariables,
  TContext
> => {
  const mutationKey = getCreateRideMutationKey()
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof createRide>>,
    CreateRideMutationVariables
  > = (props) => {
    const { teamSlug, data } = props ?? {}

    return createRide(teamSlug, data, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type CreateRideMutationResult = NonNullable<Awaited<ReturnType<typeof createRide>>>
export type CreateRideMutationBody = BodyType<RideRequest>
export type CreateRideMutationError = ErrorType<ErrorResponse>
export type CreateRideMutationVariables = { teamSlug: string; data: BodyType<RideRequest> }

/**
 * @summary Create ride
 */
export const useCreateRide = <TError = ErrorType<ErrorResponse>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof createRide>>,
      TError,
      CreateRideMutationVariables,
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof createRide>>,
  TError,
  CreateRideMutationVariables,
  TContext
> => {
  return useMutation(getCreateRideMutationOptions(options), queryClient)
}
/**
 * Update ride information. Requires organizer permissions.
 * @summary Update ride
 */
export const updateRide = (
  teamSlug: string,
  rideSlug: string,
  rideRequest: BodyType<RideRequest>,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<RideDto>(
    {
      url: `/api/teams/${teamSlug}/rides/${rideSlug}`,
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      data: rideRequest,
      signal,
    },
    options
  )
}

export const getUpdateRideMutationKey = () => ['updateRide'] as const

export const getUpdateRideMutationOptions = <
  TError = ErrorType<ErrorResponse>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof updateRide>>,
    TError,
    UpdateRideMutationVariables,
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof updateRide>>,
  TError,
  UpdateRideMutationVariables,
  TContext
> => {
  const mutationKey = getUpdateRideMutationKey()
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof updateRide>>,
    UpdateRideMutationVariables
  > = (props) => {
    const { teamSlug, rideSlug, data } = props ?? {}

    return updateRide(teamSlug, rideSlug, data, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type UpdateRideMutationResult = NonNullable<Awaited<ReturnType<typeof updateRide>>>
export type UpdateRideMutationBody = BodyType<RideRequest>
export type UpdateRideMutationError = ErrorType<ErrorResponse>
export type UpdateRideMutationVariables = {
  teamSlug: string
  rideSlug: string
  data: BodyType<RideRequest>
}

/**
 * @summary Update ride
 */
export const useUpdateRide = <TError = ErrorType<ErrorResponse>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof updateRide>>,
      TError,
      UpdateRideMutationVariables,
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof updateRide>>,
  TError,
  UpdateRideMutationVariables,
  TContext
> => {
  return useMutation(getUpdateRideMutationOptions(options), queryClient)
}
/**
 * Get detailed ride information including groups
 * @summary Get ride details
 */
export const getRide = (
  teamSlug: string,
  rideSlug: string,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<RideDto>(
    { url: `/api/teams/${teamSlug}/rides/${rideSlug}`, method: 'GET', signal },
    options
  )
}

export const getGetRideQueryKey = (teamSlug: string, rideSlug: string) => {
  return [`/api/teams/${teamSlug}/rides/${rideSlug}`] as const
}

export const getGetRideQueryOptions = <
  TData = Awaited<ReturnType<typeof getRide>>,
  TError = ErrorType<ErrorResponse>,
>(
  teamSlug: string,
  rideSlug: string,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getRide>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  }
) => {
  const { query: queryOptions, request: requestOptions } = options ?? {}

  const queryKey = queryOptions?.queryKey ?? getGetRideQueryKey(teamSlug, rideSlug)

  const queryFn: QueryFunction<Awaited<ReturnType<typeof getRide>>> = ({ signal }) =>
    getRide(teamSlug, rideSlug, requestOptions, signal)

  return {
    queryKey,
    queryFn,
    enabled:
      teamSlug !== null && teamSlug !== undefined && rideSlug !== null && rideSlug !== undefined,
    ...queryOptions,
  } as UseQueryOptions<Awaited<ReturnType<typeof getRide>>, TError, TData> & {
    queryKey: DataTag<QueryKey, TData, TError>
  }
}

export type GetRideQueryResult = NonNullable<Awaited<ReturnType<typeof getRide>>>
export type GetRideQueryError = ErrorType<ErrorResponse>

export function useGetRide<
  TData = Awaited<ReturnType<typeof getRide>>,
  TError = ErrorType<ErrorResponse>,
>(
  teamSlug: string,
  rideSlug: string,
  options: {
    query: Partial<UseQueryOptions<Awaited<ReturnType<typeof getRide>>, TError, TData>> &
      Pick<
        DefinedInitialDataOptions<
          Awaited<ReturnType<typeof getRide>>,
          TError,
          Awaited<ReturnType<typeof getRide>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): DefinedUseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useGetRide<
  TData = Awaited<ReturnType<typeof getRide>>,
  TError = ErrorType<ErrorResponse>,
>(
  teamSlug: string,
  rideSlug: string,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getRide>>, TError, TData>> &
      Pick<
        UndefinedInitialDataOptions<
          Awaited<ReturnType<typeof getRide>>,
          TError,
          Awaited<ReturnType<typeof getRide>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useGetRide<
  TData = Awaited<ReturnType<typeof getRide>>,
  TError = ErrorType<ErrorResponse>,
>(
  teamSlug: string,
  rideSlug: string,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getRide>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
/**
 * @summary Get ride details
 */

export function useGetRide<
  TData = Awaited<ReturnType<typeof getRide>>,
  TError = ErrorType<ErrorResponse>,
>(
  teamSlug: string,
  rideSlug: string,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getRide>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {
  const queryOptions = getGetRideQueryOptions(teamSlug, rideSlug, options)

  const query = useQuery(queryOptions, queryClient) as UseQueryResult<TData, TError> & {
    queryKey: DataTag<QueryKey, TData, TError>
  }

  return withQueryKey(query, queryOptions.queryKey)
}

/**
 * @summary Get ride details
 */
export const prefetchGetRideQuery = async <
  TData = Awaited<ReturnType<typeof getRide>>,
  TError = ErrorType<ErrorResponse>,
>(
  queryClient: QueryClient,
  teamSlug: string,
  rideSlug: string,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getRide>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  }
): Promise<QueryClient> => {
  const queryOptions = getGetRideQueryOptions(teamSlug, rideSlug, options)

  await queryClient.prefetchQuery(queryOptions)

  return queryClient
}

/**
 * Soft delete a ride. Requires organizer permissions.
 * @summary Delete ride
 */
export const deleteRide = (
  teamSlug: string,
  rideSlug: string,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<void>(
    { url: `/api/teams/${teamSlug}/rides/${rideSlug}`, method: 'DELETE', signal },
    options
  )
}

export const getDeleteRideMutationKey = () => ['deleteRide'] as const

export const getDeleteRideMutationOptions = <
  TError = ErrorType<ErrorResponse>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof deleteRide>>,
    TError,
    DeleteRideMutationVariables,
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof deleteRide>>,
  TError,
  DeleteRideMutationVariables,
  TContext
> => {
  const mutationKey = getDeleteRideMutationKey()
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof deleteRide>>,
    DeleteRideMutationVariables
  > = (props) => {
    const { teamSlug, rideSlug } = props ?? {}

    return deleteRide(teamSlug, rideSlug, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type DeleteRideMutationResult = NonNullable<Awaited<ReturnType<typeof deleteRide>>>

export type DeleteRideMutationError = ErrorType<ErrorResponse>
export type DeleteRideMutationVariables = { teamSlug: string; rideSlug: string }

/**
 * @summary Delete ride
 */
export const useDeleteRide = <TError = ErrorType<ErrorResponse>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof deleteRide>>,
      TError,
      DeleteRideMutationVariables,
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof deleteRide>>,
  TError,
  DeleteRideMutationVariables,
  TContext
> => {
  return useMutation(getDeleteRideMutationOptions(options), queryClient)
}
/**
 * Join a ride group
 * @summary Join ride group
 */
export const joinGroup = (
  teamSlug: string,
  rideSlug: string,
  groupId: string,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<RideParticipationDto>(
    {
      url: `/api/teams/${teamSlug}/rides/${rideSlug}/groups/${groupId}/join`,
      method: 'POST',
      signal,
    },
    options
  )
}

export const getJoinGroupMutationKey = () => ['joinGroup'] as const

export const getJoinGroupMutationOptions = <
  TError = ErrorType<ErrorResponse | void>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof joinGroup>>,
    TError,
    JoinGroupMutationVariables,
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof joinGroup>>,
  TError,
  JoinGroupMutationVariables,
  TContext
> => {
  const mutationKey = getJoinGroupMutationKey()
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof joinGroup>>,
    JoinGroupMutationVariables
  > = (props) => {
    const { teamSlug, rideSlug, groupId } = props ?? {}

    return joinGroup(teamSlug, rideSlug, groupId, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type JoinGroupMutationResult = NonNullable<Awaited<ReturnType<typeof joinGroup>>>

export type JoinGroupMutationError = ErrorType<ErrorResponse | void>
export type JoinGroupMutationVariables = { teamSlug: string; rideSlug: string; groupId: string }

/**
 * @summary Join ride group
 */
export const useJoinGroup = <TError = ErrorType<ErrorResponse | void>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof joinGroup>>,
      TError,
      JoinGroupMutationVariables,
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof joinGroup>>,
  TError,
  JoinGroupMutationVariables,
  TContext
> => {
  return useMutation(getJoinGroupMutationOptions(options), queryClient)
}
/**
 * Leave a ride group
 * @summary Leave ride group
 */
export const leaveGroup = (
  teamSlug: string,
  rideSlug: string,
  groupId: string,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<void>(
    {
      url: `/api/teams/${teamSlug}/rides/${rideSlug}/groups/${groupId}/leave`,
      method: 'POST',
      signal,
    },
    options
  )
}

export const getLeaveGroupMutationKey = () => ['leaveGroup'] as const

export const getLeaveGroupMutationOptions = <
  TError = ErrorType<ErrorResponse | void>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof leaveGroup>>,
    TError,
    LeaveGroupMutationVariables,
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof leaveGroup>>,
  TError,
  LeaveGroupMutationVariables,
  TContext
> => {
  const mutationKey = getLeaveGroupMutationKey()
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof leaveGroup>>,
    LeaveGroupMutationVariables
  > = (props) => {
    const { teamSlug, rideSlug, groupId } = props ?? {}

    return leaveGroup(teamSlug, rideSlug, groupId, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type LeaveGroupMutationResult = NonNullable<Awaited<ReturnType<typeof leaveGroup>>>

export type LeaveGroupMutationError = ErrorType<ErrorResponse | void>
export type LeaveGroupMutationVariables = { teamSlug: string; rideSlug: string; groupId: string }

/**
 * @summary Leave ride group
 */
export const useLeaveGroup = <TError = ErrorType<ErrorResponse | void>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof leaveGroup>>,
      TError,
      LeaveGroupMutationVariables,
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof leaveGroup>>,
  TError,
  LeaveGroupMutationVariables,
  TContext
> => {
  return useMutation(getLeaveGroupMutationOptions(options), queryClient)
}
/**
 * Change ride URL slug. Requires organizer permissions.
 * @summary Change ride slug
 */
export const changeRideSlug = (
  teamSlug: string,
  rideSlug: string,
  slugChangeRequest: BodyType<SlugChangeRequest>,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<RideDto>(
    {
      url: `/api/teams/${teamSlug}/rides/${rideSlug}/slug`,
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      data: slugChangeRequest,
      signal,
    },
    options
  )
}

export const getChangeRideSlugMutationKey = () => ['changeRideSlug'] as const

export const getChangeRideSlugMutationOptions = <
  TError = ErrorType<ErrorResponse>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof changeRideSlug>>,
    TError,
    ChangeRideSlugMutationVariables,
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof changeRideSlug>>,
  TError,
  ChangeRideSlugMutationVariables,
  TContext
> => {
  const mutationKey = getChangeRideSlugMutationKey()
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof changeRideSlug>>,
    ChangeRideSlugMutationVariables
  > = (props) => {
    const { teamSlug, rideSlug, data } = props ?? {}

    return changeRideSlug(teamSlug, rideSlug, data, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type ChangeRideSlugMutationResult = NonNullable<Awaited<ReturnType<typeof changeRideSlug>>>
export type ChangeRideSlugMutationBody = BodyType<SlugChangeRequest>
export type ChangeRideSlugMutationError = ErrorType<ErrorResponse>
export type ChangeRideSlugMutationVariables = {
  teamSlug: string
  rideSlug: string
  data: BodyType<SlugChangeRequest>
}

/**
 * @summary Change ride slug
 */
export const useChangeRideSlug = <TError = ErrorType<ErrorResponse>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof changeRideSlug>>,
      TError,
      ChangeRideSlugMutationVariables,
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof changeRideSlug>>,
  TError,
  ChangeRideSlugMutationVariables,
  TContext
> => {
  return useMutation(getChangeRideSlugMutationOptions(options), queryClient)
}
/**
 * Restore a soft-deleted ride. Requires organizer permissions.
 * @summary Restore ride
 */
export const undeleteRide = (
  teamSlug: string,
  rideSlug: string,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<RideDto>(
    { url: `/api/teams/${teamSlug}/rides/${rideSlug}/undelete`, method: 'POST', signal },
    options
  )
}

export const getUndeleteRideMutationKey = () => ['undeleteRide'] as const

export const getUndeleteRideMutationOptions = <
  TError = ErrorType<ErrorResponse>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof undeleteRide>>,
    TError,
    UndeleteRideMutationVariables,
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof undeleteRide>>,
  TError,
  UndeleteRideMutationVariables,
  TContext
> => {
  const mutationKey = getUndeleteRideMutationKey()
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof undeleteRide>>,
    UndeleteRideMutationVariables
  > = (props) => {
    const { teamSlug, rideSlug } = props ?? {}

    return undeleteRide(teamSlug, rideSlug, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type UndeleteRideMutationResult = NonNullable<Awaited<ReturnType<typeof undeleteRide>>>

export type UndeleteRideMutationError = ErrorType<ErrorResponse>
export type UndeleteRideMutationVariables = { teamSlug: string; rideSlug: string }

/**
 * @summary Restore ride
 */
export const useUndeleteRide = <TError = ErrorType<ErrorResponse>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof undeleteRide>>,
      TError,
      UndeleteRideMutationVariables,
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof undeleteRide>>,
  TError,
  UndeleteRideMutationVariables,
  TContext
> => {
  return useMutation(getUndeleteRideMutationOptions(options), queryClient)
}
