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
  GpsOAuthUrlResponse,
  GpsServiceType,
  HandleCallbackParams,
  RouteUploadResponse,
} from '../../dto'

import { axiosMutator } from '../../../lib/axiosInstance'
import type { ErrorType } from '../../../lib/axiosInstance'

type SecondParameter<T extends (...args: never) => unknown> = Parameters<T>[1]

/**
 * Get list of GPS service types configured for this domain
 * @summary Get available GPS services
 */
export const getAvailableServices = (
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<GpsServiceType[]>(
    { url: `/api/gps/available`, method: 'GET', signal },
    options
  )
}

export const getGetAvailableServicesQueryKey = () => {
  return [`/api/gps/available`] as const
}

export const getGetAvailableServicesQueryOptions = <
  TData = Awaited<ReturnType<typeof getAvailableServices>>,
  TError = ErrorType<void>,
>(options?: {
  query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getAvailableServices>>, TError, TData>>
  request?: SecondParameter<typeof axiosMutator>
}) => {
  const { query: queryOptions, request: requestOptions } = options ?? {}

  const queryKey = queryOptions?.queryKey ?? getGetAvailableServicesQueryKey()

  const queryFn: QueryFunction<Awaited<ReturnType<typeof getAvailableServices>>> = ({ signal }) =>
    getAvailableServices(requestOptions, signal)

  return { queryKey, queryFn, ...queryOptions } as UseQueryOptions<
    Awaited<ReturnType<typeof getAvailableServices>>,
    TError,
    TData
  > & { queryKey: DataTag<QueryKey, TData, TError> }
}

export type GetAvailableServicesQueryResult = NonNullable<
  Awaited<ReturnType<typeof getAvailableServices>>
>
export type GetAvailableServicesQueryError = ErrorType<void>

export function useGetAvailableServices<
  TData = Awaited<ReturnType<typeof getAvailableServices>>,
  TError = ErrorType<void>,
>(
  options: {
    query: Partial<
      UseQueryOptions<Awaited<ReturnType<typeof getAvailableServices>>, TError, TData>
    > &
      Pick<
        DefinedInitialDataOptions<
          Awaited<ReturnType<typeof getAvailableServices>>,
          TError,
          Awaited<ReturnType<typeof getAvailableServices>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): DefinedUseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useGetAvailableServices<
  TData = Awaited<ReturnType<typeof getAvailableServices>>,
  TError = ErrorType<void>,
>(
  options?: {
    query?: Partial<
      UseQueryOptions<Awaited<ReturnType<typeof getAvailableServices>>, TError, TData>
    > &
      Pick<
        UndefinedInitialDataOptions<
          Awaited<ReturnType<typeof getAvailableServices>>,
          TError,
          Awaited<ReturnType<typeof getAvailableServices>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useGetAvailableServices<
  TData = Awaited<ReturnType<typeof getAvailableServices>>,
  TError = ErrorType<void>,
>(
  options?: {
    query?: Partial<
      UseQueryOptions<Awaited<ReturnType<typeof getAvailableServices>>, TError, TData>
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
/**
 * @summary Get available GPS services
 */

export function useGetAvailableServices<
  TData = Awaited<ReturnType<typeof getAvailableServices>>,
  TError = ErrorType<void>,
>(
  options?: {
    query?: Partial<
      UseQueryOptions<Awaited<ReturnType<typeof getAvailableServices>>, TError, TData>
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {
  const queryOptions = getGetAvailableServicesQueryOptions(options)

  const query = useQuery(queryOptions, queryClient) as UseQueryResult<TData, TError> & {
    queryKey: DataTag<QueryKey, TData, TError>
  }

  return { ...query, queryKey: queryOptions.queryKey }
}

/**
 * Handles OAuth callback from GPS service and redirects to frontend
 * @summary OAuth callback
 */
export const handleCallback = (
  serviceType: GpsServiceType,
  params?: HandleCallbackParams,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<unknown>(
    { url: `/api/gps/callback/${serviceType}`, method: 'GET', params, signal },
    options
  )
}

export const getHandleCallbackQueryKey = (
  serviceType: GpsServiceType,
  params?: HandleCallbackParams
) => {
  return [`/api/gps/callback/${serviceType}`, ...(params ? [params] : [])] as const
}

export const getHandleCallbackQueryOptions = <
  TData = Awaited<ReturnType<typeof handleCallback>>,
  TError = ErrorType<void | ErrorResponse>,
>(
  serviceType: GpsServiceType,
  params?: HandleCallbackParams,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof handleCallback>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  }
) => {
  const { query: queryOptions, request: requestOptions } = options ?? {}

  const queryKey = queryOptions?.queryKey ?? getHandleCallbackQueryKey(serviceType, params)

  const queryFn: QueryFunction<Awaited<ReturnType<typeof handleCallback>>> = ({ signal }) =>
    handleCallback(serviceType, params, requestOptions, signal)

  return { queryKey, queryFn, enabled: !!serviceType, ...queryOptions } as UseQueryOptions<
    Awaited<ReturnType<typeof handleCallback>>,
    TError,
    TData
  > & { queryKey: DataTag<QueryKey, TData, TError> }
}

export type HandleCallbackQueryResult = NonNullable<Awaited<ReturnType<typeof handleCallback>>>
export type HandleCallbackQueryError = ErrorType<void | ErrorResponse>

export function useHandleCallback<
  TData = Awaited<ReturnType<typeof handleCallback>>,
  TError = ErrorType<void | ErrorResponse>,
>(
  serviceType: GpsServiceType,
  params: undefined | HandleCallbackParams,
  options: {
    query: Partial<UseQueryOptions<Awaited<ReturnType<typeof handleCallback>>, TError, TData>> &
      Pick<
        DefinedInitialDataOptions<
          Awaited<ReturnType<typeof handleCallback>>,
          TError,
          Awaited<ReturnType<typeof handleCallback>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): DefinedUseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useHandleCallback<
  TData = Awaited<ReturnType<typeof handleCallback>>,
  TError = ErrorType<void | ErrorResponse>,
>(
  serviceType: GpsServiceType,
  params?: HandleCallbackParams,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof handleCallback>>, TError, TData>> &
      Pick<
        UndefinedInitialDataOptions<
          Awaited<ReturnType<typeof handleCallback>>,
          TError,
          Awaited<ReturnType<typeof handleCallback>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useHandleCallback<
  TData = Awaited<ReturnType<typeof handleCallback>>,
  TError = ErrorType<void | ErrorResponse>,
>(
  serviceType: GpsServiceType,
  params?: HandleCallbackParams,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof handleCallback>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
/**
 * @summary OAuth callback
 */

export function useHandleCallback<
  TData = Awaited<ReturnType<typeof handleCallback>>,
  TError = ErrorType<void | ErrorResponse>,
>(
  serviceType: GpsServiceType,
  params?: HandleCallbackParams,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof handleCallback>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {
  const queryOptions = getHandleCallbackQueryOptions(serviceType, params, options)

  const query = useQuery(queryOptions, queryClient) as UseQueryResult<TData, TError> & {
    queryKey: DataTag<QueryKey, TData, TError>
  }

  return { ...query, queryKey: queryOptions.queryKey }
}

/**
 * Get the OAuth authorization URL to connect a GPS service
 * @summary Get OAuth authorization URL
 */
export const getConnectUrl = (
  serviceType: GpsServiceType,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<GpsOAuthUrlResponse>(
    { url: `/api/gps/connect/${serviceType}`, method: 'GET', signal },
    options
  )
}

export const getGetConnectUrlQueryKey = (serviceType: GpsServiceType) => {
  return [`/api/gps/connect/${serviceType}`] as const
}

export const getGetConnectUrlQueryOptions = <
  TData = Awaited<ReturnType<typeof getConnectUrl>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  serviceType: GpsServiceType,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getConnectUrl>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  }
) => {
  const { query: queryOptions, request: requestOptions } = options ?? {}

  const queryKey = queryOptions?.queryKey ?? getGetConnectUrlQueryKey(serviceType)

  const queryFn: QueryFunction<Awaited<ReturnType<typeof getConnectUrl>>> = ({ signal }) =>
    getConnectUrl(serviceType, requestOptions, signal)

  return { queryKey, queryFn, enabled: !!serviceType, ...queryOptions } as UseQueryOptions<
    Awaited<ReturnType<typeof getConnectUrl>>,
    TError,
    TData
  > & { queryKey: DataTag<QueryKey, TData, TError> }
}

export type GetConnectUrlQueryResult = NonNullable<Awaited<ReturnType<typeof getConnectUrl>>>
export type GetConnectUrlQueryError = ErrorType<ErrorResponse | void>

export function useGetConnectUrl<
  TData = Awaited<ReturnType<typeof getConnectUrl>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  serviceType: GpsServiceType,
  options: {
    query: Partial<UseQueryOptions<Awaited<ReturnType<typeof getConnectUrl>>, TError, TData>> &
      Pick<
        DefinedInitialDataOptions<
          Awaited<ReturnType<typeof getConnectUrl>>,
          TError,
          Awaited<ReturnType<typeof getConnectUrl>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): DefinedUseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useGetConnectUrl<
  TData = Awaited<ReturnType<typeof getConnectUrl>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  serviceType: GpsServiceType,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getConnectUrl>>, TError, TData>> &
      Pick<
        UndefinedInitialDataOptions<
          Awaited<ReturnType<typeof getConnectUrl>>,
          TError,
          Awaited<ReturnType<typeof getConnectUrl>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useGetConnectUrl<
  TData = Awaited<ReturnType<typeof getConnectUrl>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  serviceType: GpsServiceType,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getConnectUrl>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
/**
 * @summary Get OAuth authorization URL
 */

export function useGetConnectUrl<
  TData = Awaited<ReturnType<typeof getConnectUrl>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  serviceType: GpsServiceType,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getConnectUrl>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {
  const queryOptions = getGetConnectUrlQueryOptions(serviceType, options)

  const query = useQuery(queryOptions, queryClient) as UseQueryResult<TData, TError> & {
    queryKey: DataTag<QueryKey, TData, TError>
  }

  return { ...query, queryKey: queryOptions.queryKey }
}

/**
 * Disconnect a connected GPS service
 * @summary Disconnect GPS service
 */
export const disconnect = (
  serviceType: GpsServiceType,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<void>(
    { url: `/api/gps/disconnect/${serviceType}`, method: 'DELETE', signal },
    options
  )
}

export const getDisconnectMutationOptions = <
  TError = ErrorType<ErrorResponse | void>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof disconnect>>,
    TError,
    { serviceType: GpsServiceType },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof disconnect>>,
  TError,
  { serviceType: GpsServiceType },
  TContext
> => {
  const mutationKey = ['disconnect']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof disconnect>>,
    { serviceType: GpsServiceType }
  > = (props) => {
    const { serviceType } = props ?? {}

    return disconnect(serviceType, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type DisconnectMutationResult = NonNullable<Awaited<ReturnType<typeof disconnect>>>

export type DisconnectMutationError = ErrorType<ErrorResponse | void>

/**
 * @summary Disconnect GPS service
 */
export const useDisconnect = <TError = ErrorType<ErrorResponse | void>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof disconnect>>,
      TError,
      { serviceType: GpsServiceType },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof disconnect>>,
  TError,
  { serviceType: GpsServiceType },
  TContext
> => {
  return useMutation(getDisconnectMutationOptions(options), queryClient)
}
/**
 * Upload a route to a connected GPS service
 * @summary Upload route to GPS service
 */
export const uploadRoute = (
  serviceType: GpsServiceType,
  teamSlug: string,
  routeSlug: string,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<RouteUploadResponse>(
    { url: `/api/gps/upload/${serviceType}/${teamSlug}/${routeSlug}`, method: 'POST', signal },
    options
  )
}

export const getUploadRouteMutationOptions = <
  TError = ErrorType<ErrorResponse | void>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof uploadRoute>>,
    TError,
    { serviceType: GpsServiceType; teamSlug: string; routeSlug: string },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof uploadRoute>>,
  TError,
  { serviceType: GpsServiceType; teamSlug: string; routeSlug: string },
  TContext
> => {
  const mutationKey = ['uploadRoute']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof uploadRoute>>,
    { serviceType: GpsServiceType; teamSlug: string; routeSlug: string }
  > = (props) => {
    const { serviceType, teamSlug, routeSlug } = props ?? {}

    return uploadRoute(serviceType, teamSlug, routeSlug, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type UploadRouteMutationResult = NonNullable<Awaited<ReturnType<typeof uploadRoute>>>

export type UploadRouteMutationError = ErrorType<ErrorResponse | void>

/**
 * @summary Upload route to GPS service
 */
export const useUploadRoute = <TError = ErrorType<ErrorResponse | void>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof uploadRoute>>,
      TError,
      { serviceType: GpsServiceType; teamSlug: string; routeSlug: string },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof uploadRoute>>,
  TError,
  { serviceType: GpsServiceType; teamSlug: string; routeSlug: string },
  TContext
> => {
  return useMutation(getUploadRouteMutationOptions(options), queryClient)
}
