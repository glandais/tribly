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
  AuthResponse,
  ErrorResponse,
  StravaAuthUrlResponse,
  StravaCallbackParams,
  StravaSessionRequest,
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
 * Handles the Strava OAuth redirect and hands off to the frontend
 * @summary Strava OAuth callback
 */
export const stravaCallback = (
  params?: StravaCallbackParams,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<unknown>(
    { url: `/api/auth/strava/callback`, method: 'GET', params, signal },
    options
  )
}

export const getStravaCallbackQueryKey = (params?: StravaCallbackParams) => {
  return [`/api/auth/strava/callback`, ...(params ? [params] : [])] as const
}

export const getStravaCallbackQueryOptions = <
  TData = Awaited<ReturnType<typeof stravaCallback>>,
  TError = ErrorType<void>,
>(
  params?: StravaCallbackParams,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof stravaCallback>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  }
) => {
  const { query: queryOptions, request: requestOptions } = options ?? {}

  const queryKey = queryOptions?.queryKey ?? getStravaCallbackQueryKey(params)

  const queryFn: QueryFunction<Awaited<ReturnType<typeof stravaCallback>>> = ({ signal }) =>
    stravaCallback(params, requestOptions, signal)

  return { queryKey, queryFn, ...queryOptions } as UseQueryOptions<
    Awaited<ReturnType<typeof stravaCallback>>,
    TError,
    TData
  > & { queryKey: DataTag<QueryKey, TData, TError> }
}

export type StravaCallbackQueryResult = NonNullable<Awaited<ReturnType<typeof stravaCallback>>>
export type StravaCallbackQueryError = ErrorType<void>

export function useStravaCallback<
  TData = Awaited<ReturnType<typeof stravaCallback>>,
  TError = ErrorType<void>,
>(
  params: undefined | StravaCallbackParams,
  options: {
    query: Partial<UseQueryOptions<Awaited<ReturnType<typeof stravaCallback>>, TError, TData>> &
      Pick<
        DefinedInitialDataOptions<
          Awaited<ReturnType<typeof stravaCallback>>,
          TError,
          Awaited<ReturnType<typeof stravaCallback>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): DefinedUseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useStravaCallback<
  TData = Awaited<ReturnType<typeof stravaCallback>>,
  TError = ErrorType<void>,
>(
  params?: StravaCallbackParams,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof stravaCallback>>, TError, TData>> &
      Pick<
        UndefinedInitialDataOptions<
          Awaited<ReturnType<typeof stravaCallback>>,
          TError,
          Awaited<ReturnType<typeof stravaCallback>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useStravaCallback<
  TData = Awaited<ReturnType<typeof stravaCallback>>,
  TError = ErrorType<void>,
>(
  params?: StravaCallbackParams,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof stravaCallback>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
/**
 * @summary Strava OAuth callback
 */

export function useStravaCallback<
  TData = Awaited<ReturnType<typeof stravaCallback>>,
  TError = ErrorType<void>,
>(
  params?: StravaCallbackParams,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof stravaCallback>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {
  const queryOptions = getStravaCallbackQueryOptions(params, options)

  const query = useQuery(queryOptions, queryClient) as UseQueryResult<TData, TError> & {
    queryKey: DataTag<QueryKey, TData, TError>
  }

  return withQueryKey(query, queryOptions.queryKey)
}

/**
 * Get the Strava OAuth authorization URL to link Strava to the current account
 * @summary Get Strava link URL
 */
export const getStravaConnectUrl = (
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<StravaAuthUrlResponse>(
    { url: `/api/auth/strava/connect`, method: 'GET', signal },
    options
  )
}

export const getGetStravaConnectUrlQueryKey = () => {
  return [`/api/auth/strava/connect`] as const
}

export const getGetStravaConnectUrlQueryOptions = <
  TData = Awaited<ReturnType<typeof getStravaConnectUrl>>,
  TError = ErrorType<ErrorResponse | void>,
>(options?: {
  query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getStravaConnectUrl>>, TError, TData>>
  request?: SecondParameter<typeof axiosMutator>
}) => {
  const { query: queryOptions, request: requestOptions } = options ?? {}

  const queryKey = queryOptions?.queryKey ?? getGetStravaConnectUrlQueryKey()

  const queryFn: QueryFunction<Awaited<ReturnType<typeof getStravaConnectUrl>>> = ({ signal }) =>
    getStravaConnectUrl(requestOptions, signal)

  return { queryKey, queryFn, ...queryOptions } as UseQueryOptions<
    Awaited<ReturnType<typeof getStravaConnectUrl>>,
    TError,
    TData
  > & { queryKey: DataTag<QueryKey, TData, TError> }
}

export type GetStravaConnectUrlQueryResult = NonNullable<
  Awaited<ReturnType<typeof getStravaConnectUrl>>
>
export type GetStravaConnectUrlQueryError = ErrorType<ErrorResponse | void>

export function useGetStravaConnectUrl<
  TData = Awaited<ReturnType<typeof getStravaConnectUrl>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  options: {
    query: Partial<
      UseQueryOptions<Awaited<ReturnType<typeof getStravaConnectUrl>>, TError, TData>
    > &
      Pick<
        DefinedInitialDataOptions<
          Awaited<ReturnType<typeof getStravaConnectUrl>>,
          TError,
          Awaited<ReturnType<typeof getStravaConnectUrl>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): DefinedUseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useGetStravaConnectUrl<
  TData = Awaited<ReturnType<typeof getStravaConnectUrl>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  options?: {
    query?: Partial<
      UseQueryOptions<Awaited<ReturnType<typeof getStravaConnectUrl>>, TError, TData>
    > &
      Pick<
        UndefinedInitialDataOptions<
          Awaited<ReturnType<typeof getStravaConnectUrl>>,
          TError,
          Awaited<ReturnType<typeof getStravaConnectUrl>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useGetStravaConnectUrl<
  TData = Awaited<ReturnType<typeof getStravaConnectUrl>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getStravaConnectUrl>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
/**
 * @summary Get Strava link URL
 */

export function useGetStravaConnectUrl<
  TData = Awaited<ReturnType<typeof getStravaConnectUrl>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getStravaConnectUrl>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {
  const queryOptions = getGetStravaConnectUrlQueryOptions(options)

  const query = useQuery(queryOptions, queryClient) as UseQueryResult<TData, TError> & {
    queryKey: DataTag<QueryKey, TData, TError>
  }

  return withQueryKey(query, queryOptions.queryKey)
}

/**
 * Remove the Strava identity from the account
 * @summary Unlink Strava
 */
export const unlinkStrava = (
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<void>({ url: `/api/auth/strava/identity`, method: 'DELETE', signal }, options)
}

export const getUnlinkStravaMutationOptions = <
  TError = ErrorType<ErrorResponse | void>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<Awaited<ReturnType<typeof unlinkStrava>>, TError, void, TContext>
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<Awaited<ReturnType<typeof unlinkStrava>>, TError, void, TContext> => {
  const mutationKey = ['unlinkStrava']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<Awaited<ReturnType<typeof unlinkStrava>>, void> = () => {
    return unlinkStrava(requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type UnlinkStravaMutationResult = NonNullable<Awaited<ReturnType<typeof unlinkStrava>>>

export type UnlinkStravaMutationError = ErrorType<ErrorResponse | void>

/**
 * @summary Unlink Strava
 */
export const useUnlinkStrava = <TError = ErrorType<ErrorResponse | void>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<Awaited<ReturnType<typeof unlinkStrava>>, TError, void, TContext>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<Awaited<ReturnType<typeof unlinkStrava>>, TError, void, TContext> => {
  return useMutation(getUnlinkStravaMutationOptions(options), queryClient)
}
/**
 * Get the Strava OAuth authorization URL to log in / recover an account
 * @summary Get Strava login URL
 */
export const getStravaLoginUrl = (
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<StravaAuthUrlResponse>(
    { url: `/api/auth/strava/login-url`, method: 'GET', signal },
    options
  )
}

export const getGetStravaLoginUrlQueryKey = () => {
  return [`/api/auth/strava/login-url`] as const
}

export const getGetStravaLoginUrlQueryOptions = <
  TData = Awaited<ReturnType<typeof getStravaLoginUrl>>,
  TError = ErrorType<ErrorResponse>,
>(options?: {
  query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getStravaLoginUrl>>, TError, TData>>
  request?: SecondParameter<typeof axiosMutator>
}) => {
  const { query: queryOptions, request: requestOptions } = options ?? {}

  const queryKey = queryOptions?.queryKey ?? getGetStravaLoginUrlQueryKey()

  const queryFn: QueryFunction<Awaited<ReturnType<typeof getStravaLoginUrl>>> = ({ signal }) =>
    getStravaLoginUrl(requestOptions, signal)

  return { queryKey, queryFn, ...queryOptions } as UseQueryOptions<
    Awaited<ReturnType<typeof getStravaLoginUrl>>,
    TError,
    TData
  > & { queryKey: DataTag<QueryKey, TData, TError> }
}

export type GetStravaLoginUrlQueryResult = NonNullable<
  Awaited<ReturnType<typeof getStravaLoginUrl>>
>
export type GetStravaLoginUrlQueryError = ErrorType<ErrorResponse>

export function useGetStravaLoginUrl<
  TData = Awaited<ReturnType<typeof getStravaLoginUrl>>,
  TError = ErrorType<ErrorResponse>,
>(
  options: {
    query: Partial<UseQueryOptions<Awaited<ReturnType<typeof getStravaLoginUrl>>, TError, TData>> &
      Pick<
        DefinedInitialDataOptions<
          Awaited<ReturnType<typeof getStravaLoginUrl>>,
          TError,
          Awaited<ReturnType<typeof getStravaLoginUrl>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): DefinedUseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useGetStravaLoginUrl<
  TData = Awaited<ReturnType<typeof getStravaLoginUrl>>,
  TError = ErrorType<ErrorResponse>,
>(
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getStravaLoginUrl>>, TError, TData>> &
      Pick<
        UndefinedInitialDataOptions<
          Awaited<ReturnType<typeof getStravaLoginUrl>>,
          TError,
          Awaited<ReturnType<typeof getStravaLoginUrl>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useGetStravaLoginUrl<
  TData = Awaited<ReturnType<typeof getStravaLoginUrl>>,
  TError = ErrorType<ErrorResponse>,
>(
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getStravaLoginUrl>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
/**
 * @summary Get Strava login URL
 */

export function useGetStravaLoginUrl<
  TData = Awaited<ReturnType<typeof getStravaLoginUrl>>,
  TError = ErrorType<ErrorResponse>,
>(
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getStravaLoginUrl>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {
  const queryOptions = getGetStravaLoginUrlQueryOptions(options)

  const query = useQuery(queryOptions, queryClient) as UseQueryResult<TData, TError> & {
    queryKey: DataTag<QueryKey, TData, TError>
  }

  return withQueryKey(query, queryOptions.queryKey)
}

/**
 * Exchange the one-time code from the Strava callback for an authenticated session
 * @summary Exchange Strava login code for a session
 */
export const createStravaSession = (
  stravaSessionRequest: BodyType<StravaSessionRequest>,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<AuthResponse>(
    {
      url: `/api/auth/strava/session`,
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      data: stravaSessionRequest,
      signal,
    },
    options
  )
}

export const getCreateStravaSessionMutationOptions = <
  TError = ErrorType<ErrorResponse>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof createStravaSession>>,
    TError,
    { data: BodyType<StravaSessionRequest> },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof createStravaSession>>,
  TError,
  { data: BodyType<StravaSessionRequest> },
  TContext
> => {
  const mutationKey = ['createStravaSession']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof createStravaSession>>,
    { data: BodyType<StravaSessionRequest> }
  > = (props) => {
    const { data } = props ?? {}

    return createStravaSession(data, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type CreateStravaSessionMutationResult = NonNullable<
  Awaited<ReturnType<typeof createStravaSession>>
>
export type CreateStravaSessionMutationBody = BodyType<StravaSessionRequest>
export type CreateStravaSessionMutationError = ErrorType<ErrorResponse>

/**
 * @summary Exchange Strava login code for a session
 */
export const useCreateStravaSession = <TError = ErrorType<ErrorResponse>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof createStravaSession>>,
      TError,
      { data: BodyType<StravaSessionRequest> },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof createStravaSession>>,
  TError,
  { data: BodyType<StravaSessionRequest> },
  TContext
> => {
  return useMutation(getCreateStravaSessionMutationOptions(options), queryClient)
}
