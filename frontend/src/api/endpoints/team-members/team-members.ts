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
  AddMemberRequest,
  ErrorResponse,
  GetMembersParams,
  MemberDto,
  MemberListResponse,
  UpdateMemberRoleRequest,
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
 * Paginated list of team members. Administrators always see it; so do organisers, who need a member list to designate a ride group's leader. Everyone else needs the team to have set enableMemberDirectory. What is returned is graded too: 'role' and 'joinedAt' are null unless the caller is an administrator or the directory is open, and 'search' only matches an e-mail address for an administrator.
 * @summary Get team members
 */
export const getMembers = (
  teamSlug: string,
  params?: GetMembersParams,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<MemberListResponse>(
    { url: `/api/teams/${teamSlug}/members`, method: 'GET', params, signal },
    options
  )
}

export const getGetMembersQueryKey = (teamSlug: string, params?: GetMembersParams) => {
  return [`/api/teams/${teamSlug}/members`, ...(params ? [params] : [])] as const
}

export const getGetMembersQueryOptions = <
  TData = Awaited<ReturnType<typeof getMembers>>,
  TError = ErrorType<ErrorResponse>,
>(
  teamSlug: string,
  params?: GetMembersParams,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getMembers>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  }
) => {
  const { query: queryOptions, request: requestOptions } = options ?? {}

  const queryKey = queryOptions?.queryKey ?? getGetMembersQueryKey(teamSlug, params)

  const queryFn: QueryFunction<Awaited<ReturnType<typeof getMembers>>> = ({ signal }) =>
    getMembers(teamSlug, params, requestOptions, signal)

  return {
    queryKey,
    queryFn,
    enabled: teamSlug !== null && teamSlug !== undefined,
    ...queryOptions,
  } as UseQueryOptions<Awaited<ReturnType<typeof getMembers>>, TError, TData> & {
    queryKey: DataTag<QueryKey, TData, TError>
  }
}

export type GetMembersQueryResult = NonNullable<Awaited<ReturnType<typeof getMembers>>>
export type GetMembersQueryError = ErrorType<ErrorResponse>

export function useGetMembers<
  TData = Awaited<ReturnType<typeof getMembers>>,
  TError = ErrorType<ErrorResponse>,
>(
  teamSlug: string,
  params: undefined | GetMembersParams,
  options: {
    query: Partial<UseQueryOptions<Awaited<ReturnType<typeof getMembers>>, TError, TData>> &
      Pick<
        DefinedInitialDataOptions<
          Awaited<ReturnType<typeof getMembers>>,
          TError,
          Awaited<ReturnType<typeof getMembers>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): DefinedUseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useGetMembers<
  TData = Awaited<ReturnType<typeof getMembers>>,
  TError = ErrorType<ErrorResponse>,
>(
  teamSlug: string,
  params?: GetMembersParams,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getMembers>>, TError, TData>> &
      Pick<
        UndefinedInitialDataOptions<
          Awaited<ReturnType<typeof getMembers>>,
          TError,
          Awaited<ReturnType<typeof getMembers>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useGetMembers<
  TData = Awaited<ReturnType<typeof getMembers>>,
  TError = ErrorType<ErrorResponse>,
>(
  teamSlug: string,
  params?: GetMembersParams,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getMembers>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
/**
 * @summary Get team members
 */

export function useGetMembers<
  TData = Awaited<ReturnType<typeof getMembers>>,
  TError = ErrorType<ErrorResponse>,
>(
  teamSlug: string,
  params?: GetMembersParams,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getMembers>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {
  const queryOptions = getGetMembersQueryOptions(teamSlug, params, options)

  const query = useQuery(queryOptions, queryClient) as UseQueryResult<TData, TError> & {
    queryKey: DataTag<QueryKey, TData, TError>
  }

  return withQueryKey(query, queryOptions.queryKey)
}

/**
 * @summary Get team members
 */
export const prefetchGetMembersQuery = async <
  TData = Awaited<ReturnType<typeof getMembers>>,
  TError = ErrorType<ErrorResponse>,
>(
  queryClient: QueryClient,
  teamSlug: string,
  params?: GetMembersParams,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getMembers>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  }
): Promise<QueryClient> => {
  const queryOptions = getGetMembersQueryOptions(teamSlug, params, options)

  await queryClient.prefetchQuery(queryOptions)

  return queryClient
}

/**
 * Add a member to the team. Requires ADMIN role on team.
 * @summary Add team member
 */
export const addMember = (
  teamSlug: string,
  addMemberRequest: BodyType<AddMemberRequest>,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<MemberDto>(
    {
      url: `/api/teams/${teamSlug}/members`,
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      data: addMemberRequest,
      signal,
    },
    options
  )
}

export const getAddMemberMutationOptions = <
  TError = ErrorType<ErrorResponse>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof addMember>>,
    TError,
    { teamSlug: string; data: BodyType<AddMemberRequest> },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof addMember>>,
  TError,
  { teamSlug: string; data: BodyType<AddMemberRequest> },
  TContext
> => {
  const mutationKey = ['addMember']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof addMember>>,
    { teamSlug: string; data: BodyType<AddMemberRequest> }
  > = (props) => {
    const { teamSlug, data } = props ?? {}

    return addMember(teamSlug, data, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type AddMemberMutationResult = NonNullable<Awaited<ReturnType<typeof addMember>>>
export type AddMemberMutationBody = BodyType<AddMemberRequest>
export type AddMemberMutationError = ErrorType<ErrorResponse>

/**
 * @summary Add team member
 */
export const useAddMember = <TError = ErrorType<ErrorResponse>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof addMember>>,
      TError,
      { teamSlug: string; data: BodyType<AddMemberRequest> },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof addMember>>,
  TError,
  { teamSlug: string; data: BodyType<AddMemberRequest> },
  TContext
> => {
  return useMutation(getAddMemberMutationOptions(options), queryClient)
}
/**
 * Request to join a team
 * @summary Join team
 */
export const joinTeam = (
  teamSlug: string,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<MemberDto>(
    { url: `/api/teams/${teamSlug}/members/join`, method: 'POST', signal },
    options
  )
}

export const getJoinTeamMutationOptions = <
  TError = ErrorType<ErrorResponse | void>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof joinTeam>>,
    TError,
    { teamSlug: string },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof joinTeam>>,
  TError,
  { teamSlug: string },
  TContext
> => {
  const mutationKey = ['joinTeam']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<Awaited<ReturnType<typeof joinTeam>>, { teamSlug: string }> = (
    props
  ) => {
    const { teamSlug } = props ?? {}

    return joinTeam(teamSlug, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type JoinTeamMutationResult = NonNullable<Awaited<ReturnType<typeof joinTeam>>>

export type JoinTeamMutationError = ErrorType<ErrorResponse | void>

/**
 * @summary Join team
 */
export const useJoinTeam = <TError = ErrorType<ErrorResponse | void>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof joinTeam>>,
      TError,
      { teamSlug: string },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof joinTeam>>,
  TError,
  { teamSlug: string },
  TContext
> => {
  return useMutation(getJoinTeamMutationOptions(options), queryClient)
}
/**
 * Leave a team
 * @summary Leave team
 */
export const leaveTeam = (
  teamSlug: string,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<void>(
    { url: `/api/teams/${teamSlug}/members/leave`, method: 'POST', signal },
    options
  )
}

export const getLeaveTeamMutationOptions = <
  TError = ErrorType<ErrorResponse | void>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof leaveTeam>>,
    TError,
    { teamSlug: string },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof leaveTeam>>,
  TError,
  { teamSlug: string },
  TContext
> => {
  const mutationKey = ['leaveTeam']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof leaveTeam>>,
    { teamSlug: string }
  > = (props) => {
    const { teamSlug } = props ?? {}

    return leaveTeam(teamSlug, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type LeaveTeamMutationResult = NonNullable<Awaited<ReturnType<typeof leaveTeam>>>

export type LeaveTeamMutationError = ErrorType<ErrorResponse | void>

/**
 * @summary Leave team
 */
export const useLeaveTeam = <TError = ErrorType<ErrorResponse | void>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof leaveTeam>>,
      TError,
      { teamSlug: string },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof leaveTeam>>,
  TError,
  { teamSlug: string },
  TContext
> => {
  return useMutation(getLeaveTeamMutationOptions(options), queryClient)
}
/**
 * Update a team member's role. Requires ADMIN role.
 * @summary Update member role
 */
export const updateMemberRole = (
  teamSlug: string,
  memberId: string,
  updateMemberRoleRequest: BodyType<UpdateMemberRoleRequest>,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<MemberDto>(
    {
      url: `/api/teams/${teamSlug}/members/${memberId}`,
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      data: updateMemberRoleRequest,
      signal,
    },
    options
  )
}

export const getUpdateMemberRoleMutationOptions = <
  TError = ErrorType<ErrorResponse>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof updateMemberRole>>,
    TError,
    { teamSlug: string; memberId: string; data: BodyType<UpdateMemberRoleRequest> },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof updateMemberRole>>,
  TError,
  { teamSlug: string; memberId: string; data: BodyType<UpdateMemberRoleRequest> },
  TContext
> => {
  const mutationKey = ['updateMemberRole']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof updateMemberRole>>,
    { teamSlug: string; memberId: string; data: BodyType<UpdateMemberRoleRequest> }
  > = (props) => {
    const { teamSlug, memberId, data } = props ?? {}

    return updateMemberRole(teamSlug, memberId, data, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type UpdateMemberRoleMutationResult = NonNullable<
  Awaited<ReturnType<typeof updateMemberRole>>
>
export type UpdateMemberRoleMutationBody = BodyType<UpdateMemberRoleRequest>
export type UpdateMemberRoleMutationError = ErrorType<ErrorResponse>

/**
 * @summary Update member role
 */
export const useUpdateMemberRole = <TError = ErrorType<ErrorResponse>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof updateMemberRole>>,
      TError,
      { teamSlug: string; memberId: string; data: BodyType<UpdateMemberRoleRequest> },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof updateMemberRole>>,
  TError,
  { teamSlug: string; memberId: string; data: BodyType<UpdateMemberRoleRequest> },
  TContext
> => {
  return useMutation(getUpdateMemberRoleMutationOptions(options), queryClient)
}
/**
 * Remove a member from the team. Requires ADMIN role.
 * @summary Remove team member
 */
export const removeMember = (
  teamSlug: string,
  memberId: string,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<void>(
    { url: `/api/teams/${teamSlug}/members/${memberId}`, method: 'DELETE', signal },
    options
  )
}

export const getRemoveMemberMutationOptions = <
  TError = ErrorType<ErrorResponse>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof removeMember>>,
    TError,
    { teamSlug: string; memberId: string },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof removeMember>>,
  TError,
  { teamSlug: string; memberId: string },
  TContext
> => {
  const mutationKey = ['removeMember']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof removeMember>>,
    { teamSlug: string; memberId: string }
  > = (props) => {
    const { teamSlug, memberId } = props ?? {}

    return removeMember(teamSlug, memberId, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type RemoveMemberMutationResult = NonNullable<Awaited<ReturnType<typeof removeMember>>>

export type RemoveMemberMutationError = ErrorType<ErrorResponse>

/**
 * @summary Remove team member
 */
export const useRemoveMember = <TError = ErrorType<ErrorResponse>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof removeMember>>,
      TError,
      { teamSlug: string; memberId: string },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof removeMember>>,
  TError,
  { teamSlug: string; memberId: string },
  TContext
> => {
  return useMutation(getRemoveMemberMutationOptions(options), queryClient)
}
