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
  SlugChangeRequest,
  TripDto,
  TripParticipationDto,
  TripRequest,
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
 * Create a new trip with optional stages
 * @summary Create trip
 */
export const createTrip = (
  teamSlug: string,
  tripRequest: BodyType<TripRequest>,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<TripDto>(
    {
      url: `/api/teams/${teamSlug}/trips`,
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      data: tripRequest,
      signal,
    },
    options
  )
}

export const getCreateTripMutationOptions = <
  TError = ErrorType<ErrorResponse>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof createTrip>>,
    TError,
    { teamSlug: string; data: BodyType<TripRequest> },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof createTrip>>,
  TError,
  { teamSlug: string; data: BodyType<TripRequest> },
  TContext
> => {
  const mutationKey = ['createTrip']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof createTrip>>,
    { teamSlug: string; data: BodyType<TripRequest> }
  > = (props) => {
    const { teamSlug, data } = props ?? {}

    return createTrip(teamSlug, data, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type CreateTripMutationResult = NonNullable<Awaited<ReturnType<typeof createTrip>>>
export type CreateTripMutationBody = BodyType<TripRequest>
export type CreateTripMutationError = ErrorType<ErrorResponse>

/**
 * @summary Create trip
 */
export const useCreateTrip = <TError = ErrorType<ErrorResponse>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof createTrip>>,
      TError,
      { teamSlug: string; data: BodyType<TripRequest> },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof createTrip>>,
  TError,
  { teamSlug: string; data: BodyType<TripRequest> },
  TContext
> => {
  return useMutation(getCreateTripMutationOptions(options), queryClient)
}
/**
 * Update trip information. Requires organizer permissions.
 * @summary Update trip
 */
export const updateTrip = (
  teamSlug: string,
  tripSlug: string,
  tripRequest: BodyType<TripRequest>,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<TripDto>(
    {
      url: `/api/teams/${teamSlug}/trips/${tripSlug}`,
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      data: tripRequest,
      signal,
    },
    options
  )
}

export const getUpdateTripMutationOptions = <
  TError = ErrorType<ErrorResponse>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof updateTrip>>,
    TError,
    { teamSlug: string; tripSlug: string; data: BodyType<TripRequest> },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof updateTrip>>,
  TError,
  { teamSlug: string; tripSlug: string; data: BodyType<TripRequest> },
  TContext
> => {
  const mutationKey = ['updateTrip']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof updateTrip>>,
    { teamSlug: string; tripSlug: string; data: BodyType<TripRequest> }
  > = (props) => {
    const { teamSlug, tripSlug, data } = props ?? {}

    return updateTrip(teamSlug, tripSlug, data, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type UpdateTripMutationResult = NonNullable<Awaited<ReturnType<typeof updateTrip>>>
export type UpdateTripMutationBody = BodyType<TripRequest>
export type UpdateTripMutationError = ErrorType<ErrorResponse>

/**
 * @summary Update trip
 */
export const useUpdateTrip = <TError = ErrorType<ErrorResponse>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof updateTrip>>,
      TError,
      { teamSlug: string; tripSlug: string; data: BodyType<TripRequest> },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof updateTrip>>,
  TError,
  { teamSlug: string; tripSlug: string; data: BodyType<TripRequest> },
  TContext
> => {
  return useMutation(getUpdateTripMutationOptions(options), queryClient)
}
/**
 * Get detailed trip information including stages and participants
 * @summary Get trip details
 */
export const getTrip = (
  teamSlug: string,
  tripSlug: string,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<TripDto>(
    { url: `/api/teams/${teamSlug}/trips/${tripSlug}`, method: 'GET', signal },
    options
  )
}

export const getGetTripQueryKey = (teamSlug: string, tripSlug: string) => {
  return [`/api/teams/${teamSlug}/trips/${tripSlug}`] as const
}

export const getGetTripQueryOptions = <
  TData = Awaited<ReturnType<typeof getTrip>>,
  TError = ErrorType<ErrorResponse>,
>(
  teamSlug: string,
  tripSlug: string,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getTrip>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  }
) => {
  const { query: queryOptions, request: requestOptions } = options ?? {}

  const queryKey = queryOptions?.queryKey ?? getGetTripQueryKey(teamSlug, tripSlug)

  const queryFn: QueryFunction<Awaited<ReturnType<typeof getTrip>>> = ({ signal }) =>
    getTrip(teamSlug, tripSlug, requestOptions, signal)

  return {
    queryKey,
    queryFn,
    enabled:
      teamSlug !== null && teamSlug !== undefined && tripSlug !== null && tripSlug !== undefined,
    ...queryOptions,
  } as UseQueryOptions<Awaited<ReturnType<typeof getTrip>>, TError, TData> & {
    queryKey: DataTag<QueryKey, TData, TError>
  }
}

export type GetTripQueryResult = NonNullable<Awaited<ReturnType<typeof getTrip>>>
export type GetTripQueryError = ErrorType<ErrorResponse>

export function useGetTrip<
  TData = Awaited<ReturnType<typeof getTrip>>,
  TError = ErrorType<ErrorResponse>,
>(
  teamSlug: string,
  tripSlug: string,
  options: {
    query: Partial<UseQueryOptions<Awaited<ReturnType<typeof getTrip>>, TError, TData>> &
      Pick<
        DefinedInitialDataOptions<
          Awaited<ReturnType<typeof getTrip>>,
          TError,
          Awaited<ReturnType<typeof getTrip>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): DefinedUseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useGetTrip<
  TData = Awaited<ReturnType<typeof getTrip>>,
  TError = ErrorType<ErrorResponse>,
>(
  teamSlug: string,
  tripSlug: string,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getTrip>>, TError, TData>> &
      Pick<
        UndefinedInitialDataOptions<
          Awaited<ReturnType<typeof getTrip>>,
          TError,
          Awaited<ReturnType<typeof getTrip>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useGetTrip<
  TData = Awaited<ReturnType<typeof getTrip>>,
  TError = ErrorType<ErrorResponse>,
>(
  teamSlug: string,
  tripSlug: string,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getTrip>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
/**
 * @summary Get trip details
 */

export function useGetTrip<
  TData = Awaited<ReturnType<typeof getTrip>>,
  TError = ErrorType<ErrorResponse>,
>(
  teamSlug: string,
  tripSlug: string,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getTrip>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {
  const queryOptions = getGetTripQueryOptions(teamSlug, tripSlug, options)

  const query = useQuery(queryOptions, queryClient) as UseQueryResult<TData, TError> & {
    queryKey: DataTag<QueryKey, TData, TError>
  }

  return withQueryKey(query, queryOptions.queryKey)
}

/**
 * Soft delete a trip. Requires organizer permissions.
 * @summary Delete trip
 */
export const deleteTrip = (
  teamSlug: string,
  tripSlug: string,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<void>(
    { url: `/api/teams/${teamSlug}/trips/${tripSlug}`, method: 'DELETE', signal },
    options
  )
}

export const getDeleteTripMutationOptions = <
  TError = ErrorType<ErrorResponse>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof deleteTrip>>,
    TError,
    { teamSlug: string; tripSlug: string },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof deleteTrip>>,
  TError,
  { teamSlug: string; tripSlug: string },
  TContext
> => {
  const mutationKey = ['deleteTrip']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof deleteTrip>>,
    { teamSlug: string; tripSlug: string }
  > = (props) => {
    const { teamSlug, tripSlug } = props ?? {}

    return deleteTrip(teamSlug, tripSlug, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type DeleteTripMutationResult = NonNullable<Awaited<ReturnType<typeof deleteTrip>>>

export type DeleteTripMutationError = ErrorType<ErrorResponse>

/**
 * @summary Delete trip
 */
export const useDeleteTrip = <TError = ErrorType<ErrorResponse>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof deleteTrip>>,
      TError,
      { teamSlug: string; tripSlug: string },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof deleteTrip>>,
  TError,
  { teamSlug: string; tripSlug: string },
  TContext
> => {
  return useMutation(getDeleteTripMutationOptions(options), queryClient)
}
/**
 * Join a trip as a participant
 * @summary Join trip
 */
export const joinTrip = (
  teamSlug: string,
  tripSlug: string,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<TripParticipationDto>(
    { url: `/api/teams/${teamSlug}/trips/${tripSlug}/join`, method: 'POST', signal },
    options
  )
}

export const getJoinTripMutationOptions = <
  TError = ErrorType<ErrorResponse | void>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof joinTrip>>,
    TError,
    { teamSlug: string; tripSlug: string },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof joinTrip>>,
  TError,
  { teamSlug: string; tripSlug: string },
  TContext
> => {
  const mutationKey = ['joinTrip']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof joinTrip>>,
    { teamSlug: string; tripSlug: string }
  > = (props) => {
    const { teamSlug, tripSlug } = props ?? {}

    return joinTrip(teamSlug, tripSlug, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type JoinTripMutationResult = NonNullable<Awaited<ReturnType<typeof joinTrip>>>

export type JoinTripMutationError = ErrorType<ErrorResponse | void>

/**
 * @summary Join trip
 */
export const useJoinTrip = <TError = ErrorType<ErrorResponse | void>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof joinTrip>>,
      TError,
      { teamSlug: string; tripSlug: string },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof joinTrip>>,
  TError,
  { teamSlug: string; tripSlug: string },
  TContext
> => {
  return useMutation(getJoinTripMutationOptions(options), queryClient)
}
/**
 * Leave a trip as a participant
 * @summary Leave trip
 */
export const leaveTrip = (
  teamSlug: string,
  tripSlug: string,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<void>(
    { url: `/api/teams/${teamSlug}/trips/${tripSlug}/leave`, method: 'POST', signal },
    options
  )
}

export const getLeaveTripMutationOptions = <
  TError = ErrorType<ErrorResponse | void>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof leaveTrip>>,
    TError,
    { teamSlug: string; tripSlug: string },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof leaveTrip>>,
  TError,
  { teamSlug: string; tripSlug: string },
  TContext
> => {
  const mutationKey = ['leaveTrip']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof leaveTrip>>,
    { teamSlug: string; tripSlug: string }
  > = (props) => {
    const { teamSlug, tripSlug } = props ?? {}

    return leaveTrip(teamSlug, tripSlug, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type LeaveTripMutationResult = NonNullable<Awaited<ReturnType<typeof leaveTrip>>>

export type LeaveTripMutationError = ErrorType<ErrorResponse | void>

/**
 * @summary Leave trip
 */
export const useLeaveTrip = <TError = ErrorType<ErrorResponse | void>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof leaveTrip>>,
      TError,
      { teamSlug: string; tripSlug: string },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof leaveTrip>>,
  TError,
  { teamSlug: string; tripSlug: string },
  TContext
> => {
  return useMutation(getLeaveTripMutationOptions(options), queryClient)
}
/**
 * Change trip URL slug. Requires organizer permissions.
 * @summary Change trip slug
 */
export const changeTripSlug = (
  teamSlug: string,
  tripSlug: string,
  slugChangeRequest: BodyType<SlugChangeRequest>,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<TripDto>(
    {
      url: `/api/teams/${teamSlug}/trips/${tripSlug}/slug`,
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      data: slugChangeRequest,
      signal,
    },
    options
  )
}

export const getChangeTripSlugMutationOptions = <
  TError = ErrorType<ErrorResponse>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof changeTripSlug>>,
    TError,
    { teamSlug: string; tripSlug: string; data: BodyType<SlugChangeRequest> },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof changeTripSlug>>,
  TError,
  { teamSlug: string; tripSlug: string; data: BodyType<SlugChangeRequest> },
  TContext
> => {
  const mutationKey = ['changeTripSlug']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof changeTripSlug>>,
    { teamSlug: string; tripSlug: string; data: BodyType<SlugChangeRequest> }
  > = (props) => {
    const { teamSlug, tripSlug, data } = props ?? {}

    return changeTripSlug(teamSlug, tripSlug, data, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type ChangeTripSlugMutationResult = NonNullable<Awaited<ReturnType<typeof changeTripSlug>>>
export type ChangeTripSlugMutationBody = BodyType<SlugChangeRequest>
export type ChangeTripSlugMutationError = ErrorType<ErrorResponse>

/**
 * @summary Change trip slug
 */
export const useChangeTripSlug = <TError = ErrorType<ErrorResponse>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof changeTripSlug>>,
      TError,
      { teamSlug: string; tripSlug: string; data: BodyType<SlugChangeRequest> },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof changeTripSlug>>,
  TError,
  { teamSlug: string; tripSlug: string; data: BodyType<SlugChangeRequest> },
  TContext
> => {
  return useMutation(getChangeTripSlugMutationOptions(options), queryClient)
}
/**
 * Restore a soft-deleted trip. Requires organizer permissions.
 * @summary Restore trip
 */
export const undeleteTrip = (
  teamSlug: string,
  tripSlug: string,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<TripDto>(
    { url: `/api/teams/${teamSlug}/trips/${tripSlug}/undelete`, method: 'POST', signal },
    options
  )
}

export const getUndeleteTripMutationOptions = <
  TError = ErrorType<ErrorResponse>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof undeleteTrip>>,
    TError,
    { teamSlug: string; tripSlug: string },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof undeleteTrip>>,
  TError,
  { teamSlug: string; tripSlug: string },
  TContext
> => {
  const mutationKey = ['undeleteTrip']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof undeleteTrip>>,
    { teamSlug: string; tripSlug: string }
  > = (props) => {
    const { teamSlug, tripSlug } = props ?? {}

    return undeleteTrip(teamSlug, tripSlug, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type UndeleteTripMutationResult = NonNullable<Awaited<ReturnType<typeof undeleteTrip>>>

export type UndeleteTripMutationError = ErrorType<ErrorResponse>

/**
 * @summary Restore trip
 */
export const useUndeleteTrip = <TError = ErrorType<ErrorResponse>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof undeleteTrip>>,
      TError,
      { teamSlug: string; tripSlug: string },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof undeleteTrip>>,
  TError,
  { teamSlug: string; tripSlug: string },
  TContext
> => {
  return useMutation(getUndeleteTripMutationOptions(options), queryClient)
}
