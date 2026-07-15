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
  CompleteRequest,
  DeviceCodeResponse,
  DeviceRequest,
  DeviceTokenRequest,
  DeviceTokenResponse,
  ErrorResponse,
  VerifyParams,
  VerifyResponse,
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
 * Called by frontend after user authenticates via OTP
 * @summary Complete device authorization
 */
export const deviceComplete = (
  completeRequest: BodyType<CompleteRequest>,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<unknown>(
    {
      url: `/api/device/oauth/complete`,
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      data: completeRequest,
      signal,
    },
    options
  )
}

export const getDeviceCompleteMutationOptions = <
  TError = ErrorType<ErrorResponse | void>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof deviceComplete>>,
    TError,
    { data: BodyType<CompleteRequest> },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof deviceComplete>>,
  TError,
  { data: BodyType<CompleteRequest> },
  TContext
> => {
  const mutationKey = ['deviceComplete']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof deviceComplete>>,
    { data: BodyType<CompleteRequest> }
  > = (props) => {
    const { data } = props ?? {}

    return deviceComplete(data, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type DeviceCompleteMutationResult = NonNullable<Awaited<ReturnType<typeof deviceComplete>>>
export type DeviceCompleteMutationBody = BodyType<CompleteRequest>
export type DeviceCompleteMutationError = ErrorType<ErrorResponse | void>

/**
 * @summary Complete device authorization
 */
export const useDeviceComplete = <TError = ErrorType<ErrorResponse | void>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof deviceComplete>>,
      TError,
      { data: BodyType<CompleteRequest> },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof deviceComplete>>,
  TError,
  { data: BodyType<CompleteRequest> },
  TContext
> => {
  return useMutation(getDeviceCompleteMutationOptions(options), queryClient)
}
/**
 * Start device code flow - returns user code and verification URL
 * @summary Request device code
 */
export const device = (
  deviceRequest: BodyType<DeviceRequest>,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<DeviceCodeResponse>(
    {
      url: `/api/device/oauth/device`,
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      data: deviceRequest,
      signal,
    },
    options
  )
}

export const getDeviceMutationOptions = <
  TError = ErrorType<ErrorResponse>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof device>>,
    TError,
    { data: BodyType<DeviceRequest> },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof device>>,
  TError,
  { data: BodyType<DeviceRequest> },
  TContext
> => {
  const mutationKey = ['device']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof device>>,
    { data: BodyType<DeviceRequest> }
  > = (props) => {
    const { data } = props ?? {}

    return device(data, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type DeviceMutationResult = NonNullable<Awaited<ReturnType<typeof device>>>
export type DeviceMutationBody = BodyType<DeviceRequest>
export type DeviceMutationError = ErrorType<ErrorResponse>

/**
 * @summary Request device code
 */
export const useDevice = <TError = ErrorType<ErrorResponse>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof device>>,
      TError,
      { data: BodyType<DeviceRequest> },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof device>>,
  TError,
  { data: BodyType<DeviceRequest> },
  TContext
> => {
  return useMutation(getDeviceMutationOptions(options), queryClient)
}
/**
 * Exchange device code or refresh token for access tokens. Returns 'authorization_pending' error while waiting for user.
 * @summary Exchange code for tokens
 */
export const deviceToken = (
  deviceTokenRequest: BodyType<DeviceTokenRequest>,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<DeviceTokenResponse>(
    {
      url: `/api/device/oauth/token`,
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      data: deviceTokenRequest,
      signal,
    },
    options
  )
}

export const getDeviceTokenMutationOptions = <
  TError = ErrorType<ErrorResponse>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof deviceToken>>,
    TError,
    { data: BodyType<DeviceTokenRequest> },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof deviceToken>>,
  TError,
  { data: BodyType<DeviceTokenRequest> },
  TContext
> => {
  const mutationKey = ['deviceToken']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof deviceToken>>,
    { data: BodyType<DeviceTokenRequest> }
  > = (props) => {
    const { data } = props ?? {}

    return deviceToken(data, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type DeviceTokenMutationResult = NonNullable<Awaited<ReturnType<typeof deviceToken>>>
export type DeviceTokenMutationBody = BodyType<DeviceTokenRequest>
export type DeviceTokenMutationError = ErrorType<ErrorResponse>

/**
 * @summary Exchange code for tokens
 */
export const useDeviceToken = <TError = ErrorType<ErrorResponse>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof deviceToken>>,
      TError,
      { data: BodyType<DeviceTokenRequest> },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof deviceToken>>,
  TError,
  { data: BodyType<DeviceTokenRequest> },
  TContext
> => {
  return useMutation(getDeviceTokenMutationOptions(options), queryClient)
}
/**
 * Frontend uses this to verify user code before showing auth flow
 * @summary Check user code validity
 */
export const verify = (
  params?: VerifyParams,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<VerifyResponse>(
    { url: `/api/device/oauth/verify`, method: 'GET', params, signal },
    options
  )
}

export const getVerifyQueryKey = (params?: VerifyParams) => {
  return [`/api/device/oauth/verify`, ...(params ? [params] : [])] as const
}

export const getVerifyQueryOptions = <
  TData = Awaited<ReturnType<typeof verify>>,
  TError = ErrorType<ErrorResponse>,
>(
  params?: VerifyParams,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof verify>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  }
) => {
  const { query: queryOptions, request: requestOptions } = options ?? {}

  const queryKey = queryOptions?.queryKey ?? getVerifyQueryKey(params)

  const queryFn: QueryFunction<Awaited<ReturnType<typeof verify>>> = ({ signal }) =>
    verify(params, requestOptions, signal)

  return { queryKey, queryFn, ...queryOptions } as UseQueryOptions<
    Awaited<ReturnType<typeof verify>>,
    TError,
    TData
  > & { queryKey: DataTag<QueryKey, TData, TError> }
}

export type VerifyQueryResult = NonNullable<Awaited<ReturnType<typeof verify>>>
export type VerifyQueryError = ErrorType<ErrorResponse>

export function useVerify<
  TData = Awaited<ReturnType<typeof verify>>,
  TError = ErrorType<ErrorResponse>,
>(
  params: undefined | VerifyParams,
  options: {
    query: Partial<UseQueryOptions<Awaited<ReturnType<typeof verify>>, TError, TData>> &
      Pick<
        DefinedInitialDataOptions<
          Awaited<ReturnType<typeof verify>>,
          TError,
          Awaited<ReturnType<typeof verify>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): DefinedUseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useVerify<
  TData = Awaited<ReturnType<typeof verify>>,
  TError = ErrorType<ErrorResponse>,
>(
  params?: VerifyParams,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof verify>>, TError, TData>> &
      Pick<
        UndefinedInitialDataOptions<
          Awaited<ReturnType<typeof verify>>,
          TError,
          Awaited<ReturnType<typeof verify>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useVerify<
  TData = Awaited<ReturnType<typeof verify>>,
  TError = ErrorType<ErrorResponse>,
>(
  params?: VerifyParams,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof verify>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
/**
 * @summary Check user code validity
 */

export function useVerify<
  TData = Awaited<ReturnType<typeof verify>>,
  TError = ErrorType<ErrorResponse>,
>(
  params?: VerifyParams,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof verify>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {
  const queryOptions = getVerifyQueryOptions(params, options)

  const query = useQuery(queryOptions, queryClient) as UseQueryResult<TData, TError> & {
    queryKey: DataTag<QueryKey, TData, TError>
  }

  return withQueryKey(query, queryOptions.queryKey)
}

/**
 * @summary Check user code validity
 */
export const prefetchVerifyQuery = async <
  TData = Awaited<ReturnType<typeof verify>>,
  TError = ErrorType<ErrorResponse>,
>(
  queryClient: QueryClient,
  params?: VerifyParams,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof verify>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  }
): Promise<QueryClient> => {
  const queryOptions = getVerifyQueryOptions(params, options)

  await queryClient.prefetchQuery(queryOptions)

  return queryClient
}
