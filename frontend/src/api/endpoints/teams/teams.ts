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
  ListTeamsParams,
  SlugChangeRequest,
  TeamDetailDto,
  TeamListResponse,
  TeamRequest,
} from '../../dto'

import { axiosMutator } from '../../../lib/axiosInstance'
import type { ErrorType, BodyType } from '../../../lib/axiosInstance'

type SecondParameter<T extends (...args: never) => unknown> = Parameters<T>[1]

/**
 * Get a paginated list of public teams with optional search
 * @summary List public teams
 */
export const listTeams = (
  params?: ListTeamsParams,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<TeamListResponse>(
    { url: `/api/teams`, method: 'GET', params, signal },
    options
  )
}

export const getListTeamsQueryKey = (params?: ListTeamsParams) => {
  return [`/api/teams`, ...(params ? [params] : [])] as const
}

export const getListTeamsQueryOptions = <
  TData = Awaited<ReturnType<typeof listTeams>>,
  TError = ErrorType<unknown>,
>(
  params?: ListTeamsParams,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof listTeams>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  }
) => {
  const { query: queryOptions, request: requestOptions } = options ?? {}

  const queryKey = queryOptions?.queryKey ?? getListTeamsQueryKey(params)

  const queryFn: QueryFunction<Awaited<ReturnType<typeof listTeams>>> = ({ signal }) =>
    listTeams(params, requestOptions, signal)

  return { queryKey, queryFn, ...queryOptions } as UseQueryOptions<
    Awaited<ReturnType<typeof listTeams>>,
    TError,
    TData
  > & { queryKey: DataTag<QueryKey, TData, TError> }
}

export type ListTeamsQueryResult = NonNullable<Awaited<ReturnType<typeof listTeams>>>
export type ListTeamsQueryError = ErrorType<unknown>

export function useListTeams<
  TData = Awaited<ReturnType<typeof listTeams>>,
  TError = ErrorType<unknown>,
>(
  params: undefined | ListTeamsParams,
  options: {
    query: Partial<UseQueryOptions<Awaited<ReturnType<typeof listTeams>>, TError, TData>> &
      Pick<
        DefinedInitialDataOptions<
          Awaited<ReturnType<typeof listTeams>>,
          TError,
          Awaited<ReturnType<typeof listTeams>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): DefinedUseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useListTeams<
  TData = Awaited<ReturnType<typeof listTeams>>,
  TError = ErrorType<unknown>,
>(
  params?: ListTeamsParams,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof listTeams>>, TError, TData>> &
      Pick<
        UndefinedInitialDataOptions<
          Awaited<ReturnType<typeof listTeams>>,
          TError,
          Awaited<ReturnType<typeof listTeams>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useListTeams<
  TData = Awaited<ReturnType<typeof listTeams>>,
  TError = ErrorType<unknown>,
>(
  params?: ListTeamsParams,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof listTeams>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
/**
 * @summary List public teams
 */

export function useListTeams<
  TData = Awaited<ReturnType<typeof listTeams>>,
  TError = ErrorType<unknown>,
>(
  params?: ListTeamsParams,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof listTeams>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {
  const queryOptions = getListTeamsQueryOptions(params, options)

  const query = useQuery(queryOptions, queryClient) as UseQueryResult<TData, TError> & {
    queryKey: DataTag<QueryKey, TData, TError>
  }

  return { ...query, queryKey: queryOptions.queryKey }
}

/**
 * Create a new team. The current user will be set as the team owner.
 * @summary Create team
 */
export const createTeam = (
  teamRequest: BodyType<TeamRequest>,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<TeamDetailDto>(
    {
      url: `/api/teams`,
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      data: teamRequest,
      signal,
    },
    options
  )
}

export const getCreateTeamMutationOptions = <
  TError = ErrorType<ErrorResponse | void>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof createTeam>>,
    TError,
    { data: BodyType<TeamRequest> },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof createTeam>>,
  TError,
  { data: BodyType<TeamRequest> },
  TContext
> => {
  const mutationKey = ['createTeam']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof createTeam>>,
    { data: BodyType<TeamRequest> }
  > = (props) => {
    const { data } = props ?? {}

    return createTeam(data, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type CreateTeamMutationResult = NonNullable<Awaited<ReturnType<typeof createTeam>>>
export type CreateTeamMutationBody = BodyType<TeamRequest>
export type CreateTeamMutationError = ErrorType<ErrorResponse | void>

/**
 * @summary Create team
 */
export const useCreateTeam = <TError = ErrorType<ErrorResponse | void>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof createTeam>>,
      TError,
      { data: BodyType<TeamRequest> },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof createTeam>>,
  TError,
  { data: BodyType<TeamRequest> },
  TContext
> => {
  return useMutation(getCreateTeamMutationOptions(options), queryClient)
}
/**
 * Update team information. Requires ADMIN role.
 * @summary Update team
 */
export const updateTeam = (
  teamSlug: string,
  teamRequest: BodyType<TeamRequest>,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<TeamDetailDto>(
    {
      url: `/api/teams/${teamSlug}`,
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      data: teamRequest,
      signal,
    },
    options
  )
}

export const getUpdateTeamMutationOptions = <
  TError = ErrorType<ErrorResponse>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof updateTeam>>,
    TError,
    { teamSlug: string; data: BodyType<TeamRequest> },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof updateTeam>>,
  TError,
  { teamSlug: string; data: BodyType<TeamRequest> },
  TContext
> => {
  const mutationKey = ['updateTeam']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof updateTeam>>,
    { teamSlug: string; data: BodyType<TeamRequest> }
  > = (props) => {
    const { teamSlug, data } = props ?? {}

    return updateTeam(teamSlug, data, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type UpdateTeamMutationResult = NonNullable<Awaited<ReturnType<typeof updateTeam>>>
export type UpdateTeamMutationBody = BodyType<TeamRequest>
export type UpdateTeamMutationError = ErrorType<ErrorResponse>

/**
 * @summary Update team
 */
export const useUpdateTeam = <TError = ErrorType<ErrorResponse>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof updateTeam>>,
      TError,
      { teamSlug: string; data: BodyType<TeamRequest> },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof updateTeam>>,
  TError,
  { teamSlug: string; data: BodyType<TeamRequest> },
  TContext
> => {
  return useMutation(getUpdateTeamMutationOptions(options), queryClient)
}
/**
 * Get detailed team information by URL slug
 * @summary Get team by slug
 */
export const getTeam = (
  teamSlug: string,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<TeamDetailDto>(
    { url: `/api/teams/${teamSlug}`, method: 'GET', signal },
    options
  )
}

export const getGetTeamQueryKey = (teamSlug: string) => {
  return [`/api/teams/${teamSlug}`] as const
}

export const getGetTeamQueryOptions = <
  TData = Awaited<ReturnType<typeof getTeam>>,
  TError = ErrorType<ErrorResponse>,
>(
  teamSlug: string,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getTeam>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  }
) => {
  const { query: queryOptions, request: requestOptions } = options ?? {}

  const queryKey = queryOptions?.queryKey ?? getGetTeamQueryKey(teamSlug)

  const queryFn: QueryFunction<Awaited<ReturnType<typeof getTeam>>> = ({ signal }) =>
    getTeam(teamSlug, requestOptions, signal)

  return {
    queryKey,
    queryFn,
    enabled: teamSlug !== null && teamSlug !== undefined,
    ...queryOptions,
  } as UseQueryOptions<Awaited<ReturnType<typeof getTeam>>, TError, TData> & {
    queryKey: DataTag<QueryKey, TData, TError>
  }
}

export type GetTeamQueryResult = NonNullable<Awaited<ReturnType<typeof getTeam>>>
export type GetTeamQueryError = ErrorType<ErrorResponse>

export function useGetTeam<
  TData = Awaited<ReturnType<typeof getTeam>>,
  TError = ErrorType<ErrorResponse>,
>(
  teamSlug: string,
  options: {
    query: Partial<UseQueryOptions<Awaited<ReturnType<typeof getTeam>>, TError, TData>> &
      Pick<
        DefinedInitialDataOptions<
          Awaited<ReturnType<typeof getTeam>>,
          TError,
          Awaited<ReturnType<typeof getTeam>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): DefinedUseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useGetTeam<
  TData = Awaited<ReturnType<typeof getTeam>>,
  TError = ErrorType<ErrorResponse>,
>(
  teamSlug: string,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getTeam>>, TError, TData>> &
      Pick<
        UndefinedInitialDataOptions<
          Awaited<ReturnType<typeof getTeam>>,
          TError,
          Awaited<ReturnType<typeof getTeam>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useGetTeam<
  TData = Awaited<ReturnType<typeof getTeam>>,
  TError = ErrorType<ErrorResponse>,
>(
  teamSlug: string,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getTeam>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
/**
 * @summary Get team by slug
 */

export function useGetTeam<
  TData = Awaited<ReturnType<typeof getTeam>>,
  TError = ErrorType<ErrorResponse>,
>(
  teamSlug: string,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getTeam>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {
  const queryOptions = getGetTeamQueryOptions(teamSlug, options)

  const query = useQuery(queryOptions, queryClient) as UseQueryResult<TData, TError> & {
    queryKey: DataTag<QueryKey, TData, TError>
  }

  return { ...query, queryKey: queryOptions.queryKey }
}

/**
 * Soft delete a team. Requires OWNER role.
 * @summary Delete team
 */
export const deleteTeam = (
  teamSlug: string,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<void>({ url: `/api/teams/${teamSlug}`, method: 'DELETE', signal }, options)
}

export const getDeleteTeamMutationOptions = <
  TError = ErrorType<ErrorResponse>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof deleteTeam>>,
    TError,
    { teamSlug: string },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof deleteTeam>>,
  TError,
  { teamSlug: string },
  TContext
> => {
  const mutationKey = ['deleteTeam']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof deleteTeam>>,
    { teamSlug: string }
  > = (props) => {
    const { teamSlug } = props ?? {}

    return deleteTeam(teamSlug, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type DeleteTeamMutationResult = NonNullable<Awaited<ReturnType<typeof deleteTeam>>>

export type DeleteTeamMutationError = ErrorType<ErrorResponse>

/**
 * @summary Delete team
 */
export const useDeleteTeam = <TError = ErrorType<ErrorResponse>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof deleteTeam>>,
      TError,
      { teamSlug: string },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof deleteTeam>>,
  TError,
  { teamSlug: string },
  TContext
> => {
  return useMutation(getDeleteTeamMutationOptions(options), queryClient)
}
/**
 * Change team URL slug. Requires ADMIN role.
 * @summary Change team slug
 */
export const changeTeamSlug = (
  teamSlug: string,
  slugChangeRequest: BodyType<SlugChangeRequest>,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<TeamDetailDto>(
    {
      url: `/api/teams/${teamSlug}/slug`,
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      data: slugChangeRequest,
      signal,
    },
    options
  )
}

export const getChangeTeamSlugMutationOptions = <
  TError = ErrorType<ErrorResponse>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof changeTeamSlug>>,
    TError,
    { teamSlug: string; data: BodyType<SlugChangeRequest> },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof changeTeamSlug>>,
  TError,
  { teamSlug: string; data: BodyType<SlugChangeRequest> },
  TContext
> => {
  const mutationKey = ['changeTeamSlug']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof changeTeamSlug>>,
    { teamSlug: string; data: BodyType<SlugChangeRequest> }
  > = (props) => {
    const { teamSlug, data } = props ?? {}

    return changeTeamSlug(teamSlug, data, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type ChangeTeamSlugMutationResult = NonNullable<Awaited<ReturnType<typeof changeTeamSlug>>>
export type ChangeTeamSlugMutationBody = BodyType<SlugChangeRequest>
export type ChangeTeamSlugMutationError = ErrorType<ErrorResponse>

/**
 * @summary Change team slug
 */
export const useChangeTeamSlug = <TError = ErrorType<ErrorResponse>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof changeTeamSlug>>,
      TError,
      { teamSlug: string; data: BodyType<SlugChangeRequest> },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof changeTeamSlug>>,
  TError,
  { teamSlug: string; data: BodyType<SlugChangeRequest> },
  TContext
> => {
  return useMutation(getChangeTeamSlugMutationOptions(options), queryClient)
}
