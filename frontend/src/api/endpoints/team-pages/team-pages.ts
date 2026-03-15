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
  ReorderPagesRequest,
  SlugChangeRequest,
  TeamPageDto,
  TeamPageRequest,
  TeamPageSummaryDto,
} from '../../dto'

import { axiosMutator } from '../../../lib/axiosInstance'
import type { ErrorType, BodyType } from '../../../lib/axiosInstance'

type SecondParameter<T extends (...args: never) => unknown> = Parameters<T>[1]

/**
 * Get list of additional pages for a team
 * @summary List team pages
 */
export const listPages = (
  teamSlug: string,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<TeamPageSummaryDto[]>(
    { url: `/api/teams/${teamSlug}/pages`, method: 'GET', signal },
    options
  )
}

export const getListPagesQueryKey = (teamSlug: string) => {
  return [`/api/teams/${teamSlug}/pages`] as const
}

export const getListPagesQueryOptions = <
  TData = Awaited<ReturnType<typeof listPages>>,
  TError = ErrorType<ErrorResponse>,
>(
  teamSlug: string,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof listPages>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  }
) => {
  const { query: queryOptions, request: requestOptions } = options ?? {}

  const queryKey = queryOptions?.queryKey ?? getListPagesQueryKey(teamSlug)

  const queryFn: QueryFunction<Awaited<ReturnType<typeof listPages>>> = ({ signal }) =>
    listPages(teamSlug, requestOptions, signal)

  return { queryKey, queryFn, enabled: !!teamSlug, ...queryOptions } as UseQueryOptions<
    Awaited<ReturnType<typeof listPages>>,
    TError,
    TData
  > & { queryKey: DataTag<QueryKey, TData, TError> }
}

export type ListPagesQueryResult = NonNullable<Awaited<ReturnType<typeof listPages>>>
export type ListPagesQueryError = ErrorType<ErrorResponse>

export function useListPages<
  TData = Awaited<ReturnType<typeof listPages>>,
  TError = ErrorType<ErrorResponse>,
>(
  teamSlug: string,
  options: {
    query: Partial<UseQueryOptions<Awaited<ReturnType<typeof listPages>>, TError, TData>> &
      Pick<
        DefinedInitialDataOptions<
          Awaited<ReturnType<typeof listPages>>,
          TError,
          Awaited<ReturnType<typeof listPages>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): DefinedUseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useListPages<
  TData = Awaited<ReturnType<typeof listPages>>,
  TError = ErrorType<ErrorResponse>,
>(
  teamSlug: string,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof listPages>>, TError, TData>> &
      Pick<
        UndefinedInitialDataOptions<
          Awaited<ReturnType<typeof listPages>>,
          TError,
          Awaited<ReturnType<typeof listPages>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useListPages<
  TData = Awaited<ReturnType<typeof listPages>>,
  TError = ErrorType<ErrorResponse>,
>(
  teamSlug: string,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof listPages>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
/**
 * @summary List team pages
 */

export function useListPages<
  TData = Awaited<ReturnType<typeof listPages>>,
  TError = ErrorType<ErrorResponse>,
>(
  teamSlug: string,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof listPages>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {
  const queryOptions = getListPagesQueryOptions(teamSlug, options)

  const query = useQuery(queryOptions, queryClient) as UseQueryResult<TData, TError> & {
    queryKey: DataTag<QueryKey, TData, TError>
  }

  return { ...query, queryKey: queryOptions.queryKey }
}

/**
 * Create a new team page. Requires admin permissions. Maximum 3 additional pages per team.
 * @summary Create page
 */
export const createPage = (
  teamSlug: string,
  teamPageRequest: BodyType<TeamPageRequest>,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<TeamPageDto>(
    {
      url: `/api/teams/${teamSlug}/pages`,
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      data: teamPageRequest,
      signal,
    },
    options
  )
}

export const getCreatePageMutationOptions = <
  TError = ErrorType<ErrorResponse>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof createPage>>,
    TError,
    { teamSlug: string; data: BodyType<TeamPageRequest> },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof createPage>>,
  TError,
  { teamSlug: string; data: BodyType<TeamPageRequest> },
  TContext
> => {
  const mutationKey = ['createPage']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof createPage>>,
    { teamSlug: string; data: BodyType<TeamPageRequest> }
  > = (props) => {
    const { teamSlug, data } = props ?? {}

    return createPage(teamSlug, data, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type CreatePageMutationResult = NonNullable<Awaited<ReturnType<typeof createPage>>>
export type CreatePageMutationBody = BodyType<TeamPageRequest>
export type CreatePageMutationError = ErrorType<ErrorResponse>

/**
 * @summary Create page
 */
export const useCreatePage = <TError = ErrorType<ErrorResponse>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof createPage>>,
      TError,
      { teamSlug: string; data: BodyType<TeamPageRequest> },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof createPage>>,
  TError,
  { teamSlug: string; data: BodyType<TeamPageRequest> },
  TContext
> => {
  return useMutation(getCreatePageMutationOptions(options), queryClient)
}
/**
 * Reorder team pages. Requires admin permissions.
 * @summary Reorder pages
 */
export const reorderPages = (
  teamSlug: string,
  reorderPagesRequest: BodyType<ReorderPagesRequest>,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<TeamPageSummaryDto[]>(
    {
      url: `/api/teams/${teamSlug}/pages/reorder`,
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      data: reorderPagesRequest,
      signal,
    },
    options
  )
}

export const getReorderPagesMutationOptions = <
  TError = ErrorType<ErrorResponse>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof reorderPages>>,
    TError,
    { teamSlug: string; data: BodyType<ReorderPagesRequest> },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof reorderPages>>,
  TError,
  { teamSlug: string; data: BodyType<ReorderPagesRequest> },
  TContext
> => {
  const mutationKey = ['reorderPages']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof reorderPages>>,
    { teamSlug: string; data: BodyType<ReorderPagesRequest> }
  > = (props) => {
    const { teamSlug, data } = props ?? {}

    return reorderPages(teamSlug, data, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type ReorderPagesMutationResult = NonNullable<Awaited<ReturnType<typeof reorderPages>>>
export type ReorderPagesMutationBody = BodyType<ReorderPagesRequest>
export type ReorderPagesMutationError = ErrorType<ErrorResponse>

/**
 * @summary Reorder pages
 */
export const useReorderPages = <TError = ErrorType<ErrorResponse>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof reorderPages>>,
      TError,
      { teamSlug: string; data: BodyType<ReorderPagesRequest> },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof reorderPages>>,
  TError,
  { teamSlug: string; data: BodyType<ReorderPagesRequest> },
  TContext
> => {
  return useMutation(getReorderPagesMutationOptions(options), queryClient)
}
/**
 * Update page information. Requires admin permissions.
 * @summary Update page
 */
export const updatePage = (
  teamSlug: string,
  pageSlug: string,
  teamPageRequest: BodyType<TeamPageRequest>,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<TeamPageDto>(
    {
      url: `/api/teams/${teamSlug}/pages/${pageSlug}`,
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      data: teamPageRequest,
      signal,
    },
    options
  )
}

export const getUpdatePageMutationOptions = <
  TError = ErrorType<ErrorResponse>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof updatePage>>,
    TError,
    { teamSlug: string; pageSlug: string; data: BodyType<TeamPageRequest> },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof updatePage>>,
  TError,
  { teamSlug: string; pageSlug: string; data: BodyType<TeamPageRequest> },
  TContext
> => {
  const mutationKey = ['updatePage']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof updatePage>>,
    { teamSlug: string; pageSlug: string; data: BodyType<TeamPageRequest> }
  > = (props) => {
    const { teamSlug, pageSlug, data } = props ?? {}

    return updatePage(teamSlug, pageSlug, data, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type UpdatePageMutationResult = NonNullable<Awaited<ReturnType<typeof updatePage>>>
export type UpdatePageMutationBody = BodyType<TeamPageRequest>
export type UpdatePageMutationError = ErrorType<ErrorResponse>

/**
 * @summary Update page
 */
export const useUpdatePage = <TError = ErrorType<ErrorResponse>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof updatePage>>,
      TError,
      { teamSlug: string; pageSlug: string; data: BodyType<TeamPageRequest> },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof updatePage>>,
  TError,
  { teamSlug: string; pageSlug: string; data: BodyType<TeamPageRequest> },
  TContext
> => {
  return useMutation(getUpdatePageMutationOptions(options), queryClient)
}
/**
 * Get detailed page information
 * @summary Get page details
 */
export const getPage = (
  teamSlug: string,
  pageSlug: string,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<TeamPageDto>(
    { url: `/api/teams/${teamSlug}/pages/${pageSlug}`, method: 'GET', signal },
    options
  )
}

export const getGetPageQueryKey = (teamSlug: string, pageSlug: string) => {
  return [`/api/teams/${teamSlug}/pages/${pageSlug}`] as const
}

export const getGetPageQueryOptions = <
  TData = Awaited<ReturnType<typeof getPage>>,
  TError = ErrorType<ErrorResponse>,
>(
  teamSlug: string,
  pageSlug: string,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getPage>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  }
) => {
  const { query: queryOptions, request: requestOptions } = options ?? {}

  const queryKey = queryOptions?.queryKey ?? getGetPageQueryKey(teamSlug, pageSlug)

  const queryFn: QueryFunction<Awaited<ReturnType<typeof getPage>>> = ({ signal }) =>
    getPage(teamSlug, pageSlug, requestOptions, signal)

  return {
    queryKey,
    queryFn,
    enabled: !!(teamSlug && pageSlug),
    ...queryOptions,
  } as UseQueryOptions<Awaited<ReturnType<typeof getPage>>, TError, TData> & {
    queryKey: DataTag<QueryKey, TData, TError>
  }
}

export type GetPageQueryResult = NonNullable<Awaited<ReturnType<typeof getPage>>>
export type GetPageQueryError = ErrorType<ErrorResponse>

export function useGetPage<
  TData = Awaited<ReturnType<typeof getPage>>,
  TError = ErrorType<ErrorResponse>,
>(
  teamSlug: string,
  pageSlug: string,
  options: {
    query: Partial<UseQueryOptions<Awaited<ReturnType<typeof getPage>>, TError, TData>> &
      Pick<
        DefinedInitialDataOptions<
          Awaited<ReturnType<typeof getPage>>,
          TError,
          Awaited<ReturnType<typeof getPage>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): DefinedUseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useGetPage<
  TData = Awaited<ReturnType<typeof getPage>>,
  TError = ErrorType<ErrorResponse>,
>(
  teamSlug: string,
  pageSlug: string,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getPage>>, TError, TData>> &
      Pick<
        UndefinedInitialDataOptions<
          Awaited<ReturnType<typeof getPage>>,
          TError,
          Awaited<ReturnType<typeof getPage>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useGetPage<
  TData = Awaited<ReturnType<typeof getPage>>,
  TError = ErrorType<ErrorResponse>,
>(
  teamSlug: string,
  pageSlug: string,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getPage>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
/**
 * @summary Get page details
 */

export function useGetPage<
  TData = Awaited<ReturnType<typeof getPage>>,
  TError = ErrorType<ErrorResponse>,
>(
  teamSlug: string,
  pageSlug: string,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getPage>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {
  const queryOptions = getGetPageQueryOptions(teamSlug, pageSlug, options)

  const query = useQuery(queryOptions, queryClient) as UseQueryResult<TData, TError> & {
    queryKey: DataTag<QueryKey, TData, TError>
  }

  return { ...query, queryKey: queryOptions.queryKey }
}

/**
 * Delete a team page. Requires admin permissions.
 * @summary Delete page
 */
export const deletePage = (
  teamSlug: string,
  pageSlug: string,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<void>(
    { url: `/api/teams/${teamSlug}/pages/${pageSlug}`, method: 'DELETE', signal },
    options
  )
}

export const getDeletePageMutationOptions = <
  TError = ErrorType<ErrorResponse>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof deletePage>>,
    TError,
    { teamSlug: string; pageSlug: string },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof deletePage>>,
  TError,
  { teamSlug: string; pageSlug: string },
  TContext
> => {
  const mutationKey = ['deletePage']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof deletePage>>,
    { teamSlug: string; pageSlug: string }
  > = (props) => {
    const { teamSlug, pageSlug } = props ?? {}

    return deletePage(teamSlug, pageSlug, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type DeletePageMutationResult = NonNullable<Awaited<ReturnType<typeof deletePage>>>

export type DeletePageMutationError = ErrorType<ErrorResponse>

/**
 * @summary Delete page
 */
export const useDeletePage = <TError = ErrorType<ErrorResponse>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof deletePage>>,
      TError,
      { teamSlug: string; pageSlug: string },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof deletePage>>,
  TError,
  { teamSlug: string; pageSlug: string },
  TContext
> => {
  return useMutation(getDeletePageMutationOptions(options), queryClient)
}
/**
 * Change page URL slug. Requires admin permissions.
 * @summary Change page slug
 */
export const changePageSlug = (
  teamSlug: string,
  pageSlug: string,
  slugChangeRequest: BodyType<SlugChangeRequest>,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<TeamPageDto>(
    {
      url: `/api/teams/${teamSlug}/pages/${pageSlug}/slug`,
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      data: slugChangeRequest,
      signal,
    },
    options
  )
}

export const getChangePageSlugMutationOptions = <
  TError = ErrorType<ErrorResponse>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof changePageSlug>>,
    TError,
    { teamSlug: string; pageSlug: string; data: BodyType<SlugChangeRequest> },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof changePageSlug>>,
  TError,
  { teamSlug: string; pageSlug: string; data: BodyType<SlugChangeRequest> },
  TContext
> => {
  const mutationKey = ['changePageSlug']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof changePageSlug>>,
    { teamSlug: string; pageSlug: string; data: BodyType<SlugChangeRequest> }
  > = (props) => {
    const { teamSlug, pageSlug, data } = props ?? {}

    return changePageSlug(teamSlug, pageSlug, data, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type ChangePageSlugMutationResult = NonNullable<Awaited<ReturnType<typeof changePageSlug>>>
export type ChangePageSlugMutationBody = BodyType<SlugChangeRequest>
export type ChangePageSlugMutationError = ErrorType<ErrorResponse>

/**
 * @summary Change page slug
 */
export const useChangePageSlug = <TError = ErrorType<ErrorResponse>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof changePageSlug>>,
      TError,
      { teamSlug: string; pageSlug: string; data: BodyType<SlugChangeRequest> },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof changePageSlug>>,
  TError,
  { teamSlug: string; pageSlug: string; data: BodyType<SlugChangeRequest> },
  TContext
> => {
  return useMutation(getChangePageSlugMutationOptions(options), queryClient)
}
