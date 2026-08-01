import { useQuery } from '@tanstack/react-query'
import type {
  DataTag,
  DefinedInitialDataOptions,
  DefinedUseQueryResult,
  QueryClient,
  QueryFunction,
  QueryKey,
  UndefinedInitialDataOptions,
  UseQueryOptions,
  UseQueryResult,
} from '@tanstack/react-query'

import type { BetaSignupListResponse, ListBetaSignupsParams } from '../../dto'

import { axiosMutator } from '../../../lib/axiosInstance.ts'
import type { ErrorType } from '../../../lib/axiosInstance.ts'

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
 * Get a paginated list of everyone who signed up to be notified about a beta
 * @summary List beta sign-ups
 */
export const listBetaSignups = (
  params?: ListBetaSignupsParams,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<BetaSignupListResponse>(
    { url: `/api/admin/beta-signups`, method: 'GET', params, signal },
    options
  )
}

export const getListBetaSignupsQueryKey = (params?: ListBetaSignupsParams) => {
  return [`/api/admin/beta-signups`, ...(params ? [params] : [])] as const
}

export const getListBetaSignupsQueryOptions = <
  TData = Awaited<ReturnType<typeof listBetaSignups>>,
  TError = ErrorType<void>,
>(
  params?: ListBetaSignupsParams,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof listBetaSignups>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  }
) => {
  const { query: queryOptions, request: requestOptions } = options ?? {}

  const queryKey = queryOptions?.queryKey ?? getListBetaSignupsQueryKey(params)

  const queryFn: QueryFunction<Awaited<ReturnType<typeof listBetaSignups>>> = ({ signal }) =>
    listBetaSignups(params, requestOptions, signal)

  return { queryKey, queryFn, ...queryOptions } as UseQueryOptions<
    Awaited<ReturnType<typeof listBetaSignups>>,
    TError,
    TData
  > & { queryKey: DataTag<QueryKey, TData, TError> }
}

export type ListBetaSignupsQueryResult = NonNullable<Awaited<ReturnType<typeof listBetaSignups>>>
export type ListBetaSignupsQueryError = ErrorType<void>

export function useListBetaSignups<
  TData = Awaited<ReturnType<typeof listBetaSignups>>,
  TError = ErrorType<void>,
>(
  params: undefined | ListBetaSignupsParams,
  options: {
    query: Partial<UseQueryOptions<Awaited<ReturnType<typeof listBetaSignups>>, TError, TData>> &
      Pick<
        DefinedInitialDataOptions<
          Awaited<ReturnType<typeof listBetaSignups>>,
          TError,
          Awaited<ReturnType<typeof listBetaSignups>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): DefinedUseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useListBetaSignups<
  TData = Awaited<ReturnType<typeof listBetaSignups>>,
  TError = ErrorType<void>,
>(
  params?: ListBetaSignupsParams,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof listBetaSignups>>, TError, TData>> &
      Pick<
        UndefinedInitialDataOptions<
          Awaited<ReturnType<typeof listBetaSignups>>,
          TError,
          Awaited<ReturnType<typeof listBetaSignups>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useListBetaSignups<
  TData = Awaited<ReturnType<typeof listBetaSignups>>,
  TError = ErrorType<void>,
>(
  params?: ListBetaSignupsParams,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof listBetaSignups>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
/**
 * @summary List beta sign-ups
 */

export function useListBetaSignups<
  TData = Awaited<ReturnType<typeof listBetaSignups>>,
  TError = ErrorType<void>,
>(
  params?: ListBetaSignupsParams,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof listBetaSignups>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {
  const queryOptions = getListBetaSignupsQueryOptions(params, options)

  const query = useQuery(queryOptions, queryClient) as UseQueryResult<TData, TError> & {
    queryKey: DataTag<QueryKey, TData, TError>
  }

  return withQueryKey(query, queryOptions.queryKey)
}

/**
 * @summary List beta sign-ups
 */
export const prefetchListBetaSignupsQuery = async <
  TData = Awaited<ReturnType<typeof listBetaSignups>>,
  TError = ErrorType<void>,
>(
  queryClient: QueryClient,
  params?: ListBetaSignupsParams,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof listBetaSignups>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  }
): Promise<QueryClient> => {
  const queryOptions = getListBetaSignupsQueryOptions(params, options)

  await queryClient.prefetchQuery(queryOptions)

  return queryClient
}
