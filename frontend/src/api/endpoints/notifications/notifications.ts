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
  ListMyNotificationsParams,
  NotificationListResponse,
  NotificationPreferencesDto,
  NotificationPreferencesRequest,
  UnreadCountDto,
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
 * The current user's notifications, newest first. Each carries a type and structured fields, not rendered text: the client words it in its own language.
 * @summary List my notifications
 */
export const listMyNotifications = (
  params?: ListMyNotificationsParams,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<NotificationListResponse>(
    { url: `/api/notifications`, method: 'GET', params, signal },
    options
  )
}

export const getListMyNotificationsQueryKey = (params?: ListMyNotificationsParams) => {
  return [`/api/notifications`, ...(params ? [params] : [])] as const
}

export const getListMyNotificationsQueryOptions = <
  TData = Awaited<ReturnType<typeof listMyNotifications>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  params?: ListMyNotificationsParams,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof listMyNotifications>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  }
) => {
  const { query: queryOptions, request: requestOptions } = options ?? {}

  const queryKey = queryOptions?.queryKey ?? getListMyNotificationsQueryKey(params)

  const queryFn: QueryFunction<Awaited<ReturnType<typeof listMyNotifications>>> = ({ signal }) =>
    listMyNotifications(params, requestOptions, signal)

  return { queryKey, queryFn, ...queryOptions } as UseQueryOptions<
    Awaited<ReturnType<typeof listMyNotifications>>,
    TError,
    TData
  > & { queryKey: DataTag<QueryKey, TData, TError> }
}

export type ListMyNotificationsQueryResult = NonNullable<
  Awaited<ReturnType<typeof listMyNotifications>>
>
export type ListMyNotificationsQueryError = ErrorType<ErrorResponse | void>

export function useListMyNotifications<
  TData = Awaited<ReturnType<typeof listMyNotifications>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  params: undefined | ListMyNotificationsParams,
  options: {
    query: Partial<
      UseQueryOptions<Awaited<ReturnType<typeof listMyNotifications>>, TError, TData>
    > &
      Pick<
        DefinedInitialDataOptions<
          Awaited<ReturnType<typeof listMyNotifications>>,
          TError,
          Awaited<ReturnType<typeof listMyNotifications>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): DefinedUseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useListMyNotifications<
  TData = Awaited<ReturnType<typeof listMyNotifications>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  params?: ListMyNotificationsParams,
  options?: {
    query?: Partial<
      UseQueryOptions<Awaited<ReturnType<typeof listMyNotifications>>, TError, TData>
    > &
      Pick<
        UndefinedInitialDataOptions<
          Awaited<ReturnType<typeof listMyNotifications>>,
          TError,
          Awaited<ReturnType<typeof listMyNotifications>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useListMyNotifications<
  TData = Awaited<ReturnType<typeof listMyNotifications>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  params?: ListMyNotificationsParams,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof listMyNotifications>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
/**
 * @summary List my notifications
 */

export function useListMyNotifications<
  TData = Awaited<ReturnType<typeof listMyNotifications>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  params?: ListMyNotificationsParams,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof listMyNotifications>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {
  const queryOptions = getListMyNotificationsQueryOptions(params, options)

  const query = useQuery(queryOptions, queryClient) as UseQueryResult<TData, TError> & {
    queryKey: DataTag<QueryKey, TData, TError>
  }

  return withQueryKey(query, queryOptions.queryKey)
}

/**
 * @summary List my notifications
 */
export const prefetchListMyNotificationsQuery = async <
  TData = Awaited<ReturnType<typeof listMyNotifications>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  queryClient: QueryClient,
  params?: ListMyNotificationsParams,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof listMyNotifications>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  }
): Promise<QueryClient> => {
  const queryOptions = getListMyNotificationsQueryOptions(params, options)

  await queryClient.prefetchQuery(queryOptions)

  return queryClient
}

/**
 * A partial update: only the cells sent change. The full matrix is returned.
 * @summary Update my notification preferences
 */
export const updateMyNotificationPreferences = (
  notificationPreferencesRequest: BodyType<NotificationPreferencesRequest>,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<NotificationPreferencesDto>(
    {
      url: `/api/notifications/preferences`,
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      data: notificationPreferencesRequest,
      signal,
    },
    options
  )
}

export const getUpdateMyNotificationPreferencesMutationKey = () =>
  ['updateMyNotificationPreferences'] as const

export const getUpdateMyNotificationPreferencesMutationOptions = <
  TError = ErrorType<ErrorResponse | void>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof updateMyNotificationPreferences>>,
    TError,
    UpdateMyNotificationPreferencesMutationVariables,
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof updateMyNotificationPreferences>>,
  TError,
  UpdateMyNotificationPreferencesMutationVariables,
  TContext
> => {
  const mutationKey = getUpdateMyNotificationPreferencesMutationKey()
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof updateMyNotificationPreferences>>,
    UpdateMyNotificationPreferencesMutationVariables
  > = (props) => {
    const { data } = props ?? {}

    return updateMyNotificationPreferences(data, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type UpdateMyNotificationPreferencesMutationResult = NonNullable<
  Awaited<ReturnType<typeof updateMyNotificationPreferences>>
>
export type UpdateMyNotificationPreferencesMutationBody = BodyType<NotificationPreferencesRequest>
export type UpdateMyNotificationPreferencesMutationError = ErrorType<ErrorResponse | void>
export type UpdateMyNotificationPreferencesMutationVariables = {
  data: BodyType<NotificationPreferencesRequest>
}

/**
 * @summary Update my notification preferences
 */
export const useUpdateMyNotificationPreferences = <
  TError = ErrorType<ErrorResponse | void>,
  TContext = unknown,
>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof updateMyNotificationPreferences>>,
      TError,
      UpdateMyNotificationPreferencesMutationVariables,
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof updateMyNotificationPreferences>>,
  TError,
  UpdateMyNotificationPreferencesMutationVariables,
  TContext
> => {
  return useMutation(getUpdateMyNotificationPreferencesMutationOptions(options), queryClient)
}
/**
 * Every notification type on every channel this server can deliver on. The inbox is not listed: it always receives everything.
 * @summary Get my notification preferences
 */
export const getMyNotificationPreferences = (
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<NotificationPreferencesDto>(
    { url: `/api/notifications/preferences`, method: 'GET', signal },
    options
  )
}

export const getGetMyNotificationPreferencesQueryKey = () => {
  return [`/api/notifications/preferences`] as const
}

export const getGetMyNotificationPreferencesQueryOptions = <
  TData = Awaited<ReturnType<typeof getMyNotificationPreferences>>,
  TError = ErrorType<ErrorResponse | void>,
>(options?: {
  query?: Partial<
    UseQueryOptions<Awaited<ReturnType<typeof getMyNotificationPreferences>>, TError, TData>
  >
  request?: SecondParameter<typeof axiosMutator>
}) => {
  const { query: queryOptions, request: requestOptions } = options ?? {}

  const queryKey = queryOptions?.queryKey ?? getGetMyNotificationPreferencesQueryKey()

  const queryFn: QueryFunction<Awaited<ReturnType<typeof getMyNotificationPreferences>>> = ({
    signal,
  }) => getMyNotificationPreferences(requestOptions, signal)

  return { queryKey, queryFn, ...queryOptions } as UseQueryOptions<
    Awaited<ReturnType<typeof getMyNotificationPreferences>>,
    TError,
    TData
  > & { queryKey: DataTag<QueryKey, TData, TError> }
}

export type GetMyNotificationPreferencesQueryResult = NonNullable<
  Awaited<ReturnType<typeof getMyNotificationPreferences>>
>
export type GetMyNotificationPreferencesQueryError = ErrorType<ErrorResponse | void>

export function useGetMyNotificationPreferences<
  TData = Awaited<ReturnType<typeof getMyNotificationPreferences>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  options: {
    query: Partial<
      UseQueryOptions<Awaited<ReturnType<typeof getMyNotificationPreferences>>, TError, TData>
    > &
      Pick<
        DefinedInitialDataOptions<
          Awaited<ReturnType<typeof getMyNotificationPreferences>>,
          TError,
          Awaited<ReturnType<typeof getMyNotificationPreferences>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): DefinedUseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useGetMyNotificationPreferences<
  TData = Awaited<ReturnType<typeof getMyNotificationPreferences>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  options?: {
    query?: Partial<
      UseQueryOptions<Awaited<ReturnType<typeof getMyNotificationPreferences>>, TError, TData>
    > &
      Pick<
        UndefinedInitialDataOptions<
          Awaited<ReturnType<typeof getMyNotificationPreferences>>,
          TError,
          Awaited<ReturnType<typeof getMyNotificationPreferences>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useGetMyNotificationPreferences<
  TData = Awaited<ReturnType<typeof getMyNotificationPreferences>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  options?: {
    query?: Partial<
      UseQueryOptions<Awaited<ReturnType<typeof getMyNotificationPreferences>>, TError, TData>
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
/**
 * @summary Get my notification preferences
 */

export function useGetMyNotificationPreferences<
  TData = Awaited<ReturnType<typeof getMyNotificationPreferences>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  options?: {
    query?: Partial<
      UseQueryOptions<Awaited<ReturnType<typeof getMyNotificationPreferences>>, TError, TData>
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {
  const queryOptions = getGetMyNotificationPreferencesQueryOptions(options)

  const query = useQuery(queryOptions, queryClient) as UseQueryResult<TData, TError> & {
    queryKey: DataTag<QueryKey, TData, TError>
  }

  return withQueryKey(query, queryOptions.queryKey)
}

/**
 * @summary Get my notification preferences
 */
export const prefetchGetMyNotificationPreferencesQuery = async <
  TData = Awaited<ReturnType<typeof getMyNotificationPreferences>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  queryClient: QueryClient,
  options?: {
    query?: Partial<
      UseQueryOptions<Awaited<ReturnType<typeof getMyNotificationPreferences>>, TError, TData>
    >
    request?: SecondParameter<typeof axiosMutator>
  }
): Promise<QueryClient> => {
  const queryOptions = getGetMyNotificationPreferencesQueryOptions(options)

  await queryClient.prefetchQuery(queryOptions)

  return queryClient
}

/**
 * @summary Mark all my notifications read
 */
export const markAllNotificationsRead = (
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<void>({ url: `/api/notifications/read-all`, method: 'POST', signal }, options)
}

export const getMarkAllNotificationsReadMutationKey = () => ['markAllNotificationsRead'] as const

export const getMarkAllNotificationsReadMutationOptions = <
  TError = ErrorType<ErrorResponse | void>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof markAllNotificationsRead>>,
    TError,
    void,
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof markAllNotificationsRead>>,
  TError,
  void,
  TContext
> => {
  const mutationKey = getMarkAllNotificationsReadMutationKey()
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof markAllNotificationsRead>>,
    void
  > = () => {
    return markAllNotificationsRead(requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type MarkAllNotificationsReadMutationResult = NonNullable<
  Awaited<ReturnType<typeof markAllNotificationsRead>>
>

export type MarkAllNotificationsReadMutationError = ErrorType<ErrorResponse | void>

/**
 * @summary Mark all my notifications read
 */
export const useMarkAllNotificationsRead = <
  TError = ErrorType<ErrorResponse | void>,
  TContext = unknown,
>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof markAllNotificationsRead>>,
      TError,
      void,
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof markAllNotificationsRead>>,
  TError,
  void,
  TContext
> => {
  return useMutation(getMarkAllNotificationsReadMutationOptions(options), queryClient)
}
/**
 * The badge on the bell. Cheap by design: clients poll it (on focus, at most once a minute) rather than reloading the list.
 * @summary Count my unread notifications
 */
export const countMyUnreadNotifications = (
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<UnreadCountDto>(
    { url: `/api/notifications/unread-count`, method: 'GET', signal },
    options
  )
}

export const getCountMyUnreadNotificationsQueryKey = () => {
  return [`/api/notifications/unread-count`] as const
}

export const getCountMyUnreadNotificationsQueryOptions = <
  TData = Awaited<ReturnType<typeof countMyUnreadNotifications>>,
  TError = ErrorType<ErrorResponse | void>,
>(options?: {
  query?: Partial<
    UseQueryOptions<Awaited<ReturnType<typeof countMyUnreadNotifications>>, TError, TData>
  >
  request?: SecondParameter<typeof axiosMutator>
}) => {
  const { query: queryOptions, request: requestOptions } = options ?? {}

  const queryKey = queryOptions?.queryKey ?? getCountMyUnreadNotificationsQueryKey()

  const queryFn: QueryFunction<Awaited<ReturnType<typeof countMyUnreadNotifications>>> = ({
    signal,
  }) => countMyUnreadNotifications(requestOptions, signal)

  return { queryKey, queryFn, ...queryOptions } as UseQueryOptions<
    Awaited<ReturnType<typeof countMyUnreadNotifications>>,
    TError,
    TData
  > & { queryKey: DataTag<QueryKey, TData, TError> }
}

export type CountMyUnreadNotificationsQueryResult = NonNullable<
  Awaited<ReturnType<typeof countMyUnreadNotifications>>
>
export type CountMyUnreadNotificationsQueryError = ErrorType<ErrorResponse | void>

export function useCountMyUnreadNotifications<
  TData = Awaited<ReturnType<typeof countMyUnreadNotifications>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  options: {
    query: Partial<
      UseQueryOptions<Awaited<ReturnType<typeof countMyUnreadNotifications>>, TError, TData>
    > &
      Pick<
        DefinedInitialDataOptions<
          Awaited<ReturnType<typeof countMyUnreadNotifications>>,
          TError,
          Awaited<ReturnType<typeof countMyUnreadNotifications>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): DefinedUseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useCountMyUnreadNotifications<
  TData = Awaited<ReturnType<typeof countMyUnreadNotifications>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  options?: {
    query?: Partial<
      UseQueryOptions<Awaited<ReturnType<typeof countMyUnreadNotifications>>, TError, TData>
    > &
      Pick<
        UndefinedInitialDataOptions<
          Awaited<ReturnType<typeof countMyUnreadNotifications>>,
          TError,
          Awaited<ReturnType<typeof countMyUnreadNotifications>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useCountMyUnreadNotifications<
  TData = Awaited<ReturnType<typeof countMyUnreadNotifications>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  options?: {
    query?: Partial<
      UseQueryOptions<Awaited<ReturnType<typeof countMyUnreadNotifications>>, TError, TData>
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
/**
 * @summary Count my unread notifications
 */

export function useCountMyUnreadNotifications<
  TData = Awaited<ReturnType<typeof countMyUnreadNotifications>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  options?: {
    query?: Partial<
      UseQueryOptions<Awaited<ReturnType<typeof countMyUnreadNotifications>>, TError, TData>
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {
  const queryOptions = getCountMyUnreadNotificationsQueryOptions(options)

  const query = useQuery(queryOptions, queryClient) as UseQueryResult<TData, TError> & {
    queryKey: DataTag<QueryKey, TData, TError>
  }

  return withQueryKey(query, queryOptions.queryKey)
}

/**
 * @summary Count my unread notifications
 */
export const prefetchCountMyUnreadNotificationsQuery = async <
  TData = Awaited<ReturnType<typeof countMyUnreadNotifications>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  queryClient: QueryClient,
  options?: {
    query?: Partial<
      UseQueryOptions<Awaited<ReturnType<typeof countMyUnreadNotifications>>, TError, TData>
    >
    request?: SecondParameter<typeof axiosMutator>
  }
): Promise<QueryClient> => {
  const queryOptions = getCountMyUnreadNotificationsQueryOptions(options)

  await queryClient.prefetchQuery(queryOptions)

  return queryClient
}

/**
 * Idempotent: marking an already-read notification read succeeds.
 * @summary Mark a notification read
 */
export const markNotificationRead = (
  notificationId: string,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<void>(
    { url: `/api/notifications/${notificationId}/read`, method: 'POST', signal },
    options
  )
}

export const getMarkNotificationReadMutationKey = () => ['markNotificationRead'] as const

export const getMarkNotificationReadMutationOptions = <
  TError = ErrorType<ErrorResponse | void>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof markNotificationRead>>,
    TError,
    MarkNotificationReadMutationVariables,
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof markNotificationRead>>,
  TError,
  MarkNotificationReadMutationVariables,
  TContext
> => {
  const mutationKey = getMarkNotificationReadMutationKey()
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof markNotificationRead>>,
    MarkNotificationReadMutationVariables
  > = (props) => {
    const { notificationId } = props ?? {}

    return markNotificationRead(notificationId, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type MarkNotificationReadMutationResult = NonNullable<
  Awaited<ReturnType<typeof markNotificationRead>>
>

export type MarkNotificationReadMutationError = ErrorType<ErrorResponse | void>
export type MarkNotificationReadMutationVariables = { notificationId: string }

/**
 * @summary Mark a notification read
 */
export const useMarkNotificationRead = <
  TError = ErrorType<ErrorResponse | void>,
  TContext = unknown,
>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof markNotificationRead>>,
      TError,
      MarkNotificationReadMutationVariables,
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof markNotificationRead>>,
  TError,
  MarkNotificationReadMutationVariables,
  TContext
> => {
  return useMutation(getMarkNotificationReadMutationOptions(options), queryClient)
}
