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
  PublicUserDto,
  SearchUsersParams,
  UpdateUserRequest,
  UploadAvatarBody,
  UserDto,
  UserExportDto,
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
 * Download a prepared data export archive using the token from the notification email.
 * @summary Download a personal data export
 */
export const downloadDataExport = (
  token: string,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<Blob>(
    { url: `/api/export/download/${token}`, method: 'GET', responseType: 'blob', signal },
    options
  )
}

export const getDownloadDataExportQueryKey = (token: string) => {
  return [`/api/export/download/${token}`] as const
}

export const getDownloadDataExportQueryOptions = <
  TData = Awaited<ReturnType<typeof downloadDataExport>>,
  TError = ErrorType<Blob>,
>(
  token: string,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof downloadDataExport>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  }
) => {
  const { query: queryOptions, request: requestOptions } = options ?? {}

  const queryKey = queryOptions?.queryKey ?? getDownloadDataExportQueryKey(token)

  const queryFn: QueryFunction<Awaited<ReturnType<typeof downloadDataExport>>> = ({ signal }) =>
    downloadDataExport(token, requestOptions, signal)

  return {
    queryKey,
    queryFn,
    enabled: token !== null && token !== undefined,
    ...queryOptions,
  } as UseQueryOptions<Awaited<ReturnType<typeof downloadDataExport>>, TError, TData> & {
    queryKey: DataTag<QueryKey, TData, TError>
  }
}

export type DownloadDataExportQueryResult = NonNullable<
  Awaited<ReturnType<typeof downloadDataExport>>
>
export type DownloadDataExportQueryError = ErrorType<Blob>

export function useDownloadDataExport<
  TData = Awaited<ReturnType<typeof downloadDataExport>>,
  TError = ErrorType<Blob>,
>(
  token: string,
  options: {
    query: Partial<UseQueryOptions<Awaited<ReturnType<typeof downloadDataExport>>, TError, TData>> &
      Pick<
        DefinedInitialDataOptions<
          Awaited<ReturnType<typeof downloadDataExport>>,
          TError,
          Awaited<ReturnType<typeof downloadDataExport>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): DefinedUseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useDownloadDataExport<
  TData = Awaited<ReturnType<typeof downloadDataExport>>,
  TError = ErrorType<Blob>,
>(
  token: string,
  options?: {
    query?: Partial<
      UseQueryOptions<Awaited<ReturnType<typeof downloadDataExport>>, TError, TData>
    > &
      Pick<
        UndefinedInitialDataOptions<
          Awaited<ReturnType<typeof downloadDataExport>>,
          TError,
          Awaited<ReturnType<typeof downloadDataExport>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useDownloadDataExport<
  TData = Awaited<ReturnType<typeof downloadDataExport>>,
  TError = ErrorType<Blob>,
>(
  token: string,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof downloadDataExport>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
/**
 * @summary Download a personal data export
 */

export function useDownloadDataExport<
  TData = Awaited<ReturnType<typeof downloadDataExport>>,
  TError = ErrorType<Blob>,
>(
  token: string,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof downloadDataExport>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {
  const queryOptions = getDownloadDataExportQueryOptions(token, options)

  const query = useQuery(queryOptions, queryClient) as UseQueryResult<TData, TError> & {
    queryKey: DataTag<QueryKey, TData, TError>
  }

  return withQueryKey(query, queryOptions.queryKey)
}

/**
 * @summary Download a personal data export
 */
export const prefetchDownloadDataExportQuery = async <
  TData = Awaited<ReturnType<typeof downloadDataExport>>,
  TError = ErrorType<Blob>,
>(
  queryClient: QueryClient,
  token: string,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof downloadDataExport>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  }
): Promise<QueryClient> => {
  const queryOptions = getDownloadDataExportQueryOptions(token, options)

  await queryClient.prefetchQuery(queryOptions)

  return queryClient
}

/**
 * Update the current user's profile
 * @summary Update current user
 */
export const updateMe = (
  updateUserRequest: BodyType<UpdateUserRequest>,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<UserDto>(
    {
      url: `/api/users/me`,
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      data: updateUserRequest,
      signal,
    },
    options
  )
}

export const getUpdateMeMutationOptions = <
  TError = ErrorType<ErrorResponse | void>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof updateMe>>,
    TError,
    { data: BodyType<UpdateUserRequest> },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof updateMe>>,
  TError,
  { data: BodyType<UpdateUserRequest> },
  TContext
> => {
  const mutationKey = ['updateMe']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof updateMe>>,
    { data: BodyType<UpdateUserRequest> }
  > = (props) => {
    const { data } = props ?? {}

    return updateMe(data, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type UpdateMeMutationResult = NonNullable<Awaited<ReturnType<typeof updateMe>>>
export type UpdateMeMutationBody = BodyType<UpdateUserRequest>
export type UpdateMeMutationError = ErrorType<ErrorResponse | void>

/**
 * @summary Update current user
 */
export const useUpdateMe = <TError = ErrorType<ErrorResponse | void>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof updateMe>>,
      TError,
      { data: BodyType<UpdateUserRequest> },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof updateMe>>,
  TError,
  { data: BodyType<UpdateUserRequest> },
  TContext
> => {
  return useMutation(getUpdateMeMutationOptions(options), queryClient)
}
/**
 * Get the current authenticated user's profile.
 * @summary Get current user
 */
export const getMe = (options?: SecondParameter<typeof axiosMutator>, signal?: AbortSignal) => {
  return axiosMutator<UserDto>({ url: `/api/users/me`, method: 'GET', signal }, options)
}

export const getGetMeQueryKey = () => {
  return [`/api/users/me`] as const
}

export const getGetMeQueryOptions = <
  TData = Awaited<ReturnType<typeof getMe>>,
  TError = ErrorType<ErrorResponse | void>,
>(options?: {
  query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getMe>>, TError, TData>>
  request?: SecondParameter<typeof axiosMutator>
}) => {
  const { query: queryOptions, request: requestOptions } = options ?? {}

  const queryKey = queryOptions?.queryKey ?? getGetMeQueryKey()

  const queryFn: QueryFunction<Awaited<ReturnType<typeof getMe>>> = ({ signal }) =>
    getMe(requestOptions, signal)

  return { queryKey, queryFn, ...queryOptions } as UseQueryOptions<
    Awaited<ReturnType<typeof getMe>>,
    TError,
    TData
  > & { queryKey: DataTag<QueryKey, TData, TError> }
}

export type GetMeQueryResult = NonNullable<Awaited<ReturnType<typeof getMe>>>
export type GetMeQueryError = ErrorType<ErrorResponse | void>

export function useGetMe<
  TData = Awaited<ReturnType<typeof getMe>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  options: {
    query: Partial<UseQueryOptions<Awaited<ReturnType<typeof getMe>>, TError, TData>> &
      Pick<
        DefinedInitialDataOptions<
          Awaited<ReturnType<typeof getMe>>,
          TError,
          Awaited<ReturnType<typeof getMe>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): DefinedUseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useGetMe<
  TData = Awaited<ReturnType<typeof getMe>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getMe>>, TError, TData>> &
      Pick<
        UndefinedInitialDataOptions<
          Awaited<ReturnType<typeof getMe>>,
          TError,
          Awaited<ReturnType<typeof getMe>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useGetMe<
  TData = Awaited<ReturnType<typeof getMe>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getMe>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
/**
 * @summary Get current user
 */

export function useGetMe<
  TData = Awaited<ReturnType<typeof getMe>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getMe>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {
  const queryOptions = getGetMeQueryOptions(options)

  const query = useQuery(queryOptions, queryClient) as UseQueryResult<TData, TError> & {
    queryKey: DataTag<QueryKey, TData, TError>
  }

  return withQueryKey(query, queryOptions.queryKey)
}

/**
 * @summary Get current user
 */
export const prefetchGetMeQuery = async <
  TData = Awaited<ReturnType<typeof getMe>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  queryClient: QueryClient,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getMe>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  }
): Promise<QueryClient> => {
  const queryOptions = getGetMeQueryOptions(options)

  await queryClient.prefetchQuery(queryOptions)

  return queryClient
}

/**
 * Delete the current user's account
 * @summary Delete current user
 */
export const deleteCurrentUser = (
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<void>({ url: `/api/users/me`, method: 'DELETE', signal }, options)
}

export const getDeleteCurrentUserMutationOptions = <
  TError = ErrorType<ErrorResponse | void>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof deleteCurrentUser>>,
    TError,
    void,
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<Awaited<ReturnType<typeof deleteCurrentUser>>, TError, void, TContext> => {
  const mutationKey = ['deleteCurrentUser']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<Awaited<ReturnType<typeof deleteCurrentUser>>, void> = () => {
    return deleteCurrentUser(requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type DeleteCurrentUserMutationResult = NonNullable<
  Awaited<ReturnType<typeof deleteCurrentUser>>
>

export type DeleteCurrentUserMutationError = ErrorType<ErrorResponse | void>

/**
 * @summary Delete current user
 */
export const useDeleteCurrentUser = <TError = ErrorType<ErrorResponse | void>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof deleteCurrentUser>>,
      TError,
      void,
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<Awaited<ReturnType<typeof deleteCurrentUser>>, TError, void, TContext> => {
  return useMutation(getDeleteCurrentUserMutationOptions(options), queryClient)
}
/**
 * Upload a new avatar image for the current user. Image will be resized to 256x256.
 * @summary Upload user avatar
 */
export const uploadAvatar = (
  uploadAvatarBody: BodyType<UploadAvatarBody>,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  const formData = new FormData()
  if (uploadAvatarBody.file !== undefined) {
    formData.append(`file`, uploadAvatarBody.file)
  }

  return axiosMutator<UserDto>(
    {
      url: `/api/users/me/avatar`,
      method: 'POST',
      headers: { 'Content-Type': 'multipart/form-data' },
      data: formData,
      signal,
    },
    options
  )
}

export const getUploadAvatarMutationOptions = <
  TError = ErrorType<ErrorResponse | void>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof uploadAvatar>>,
    TError,
    { data: BodyType<UploadAvatarBody> },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof uploadAvatar>>,
  TError,
  { data: BodyType<UploadAvatarBody> },
  TContext
> => {
  const mutationKey = ['uploadAvatar']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof uploadAvatar>>,
    { data: BodyType<UploadAvatarBody> }
  > = (props) => {
    const { data } = props ?? {}

    return uploadAvatar(data, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type UploadAvatarMutationResult = NonNullable<Awaited<ReturnType<typeof uploadAvatar>>>
export type UploadAvatarMutationBody = BodyType<UploadAvatarBody>
export type UploadAvatarMutationError = ErrorType<ErrorResponse | void>

/**
 * @summary Upload user avatar
 */
export const useUploadAvatar = <TError = ErrorType<ErrorResponse | void>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof uploadAvatar>>,
      TError,
      { data: BodyType<UploadAvatarBody> },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof uploadAvatar>>,
  TError,
  { data: BodyType<UploadAvatarBody> },
  TContext
> => {
  return useMutation(getUploadAvatarMutationOptions(options), queryClient)
}
/**
 * Remove the current user's avatar
 * @summary Delete user avatar
 */
export const deleteAvatar = (
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<UserDto>({ url: `/api/users/me/avatar`, method: 'DELETE', signal }, options)
}

export const getDeleteAvatarMutationOptions = <
  TError = ErrorType<ErrorResponse | void>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<Awaited<ReturnType<typeof deleteAvatar>>, TError, void, TContext>
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<Awaited<ReturnType<typeof deleteAvatar>>, TError, void, TContext> => {
  const mutationKey = ['deleteAvatar']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<Awaited<ReturnType<typeof deleteAvatar>>, void> = () => {
    return deleteAvatar(requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type DeleteAvatarMutationResult = NonNullable<Awaited<ReturnType<typeof deleteAvatar>>>

export type DeleteAvatarMutationError = ErrorType<ErrorResponse | void>

/**
 * @summary Delete user avatar
 */
export const useDeleteAvatar = <TError = ErrorType<ErrorResponse | void>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<Awaited<ReturnType<typeof deleteAvatar>>, TError, void, TContext>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<Awaited<ReturnType<typeof deleteAvatar>>, TError, void, TContext> => {
  return useMutation(getDeleteAvatarMutationOptions(options), queryClient)
}
/**
 * Queue a GDPR export of the current user's data. The archive is built in the background and a download link is emailed when it is ready. Limited to one export per hour.
 * @summary Request a personal data export
 */
export const requestExport = (
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<UserExportDto>(
    { url: `/api/users/me/export`, method: 'POST', signal },
    options
  )
}

export const getRequestExportMutationOptions = <
  TError = ErrorType<ErrorResponse | void>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<Awaited<ReturnType<typeof requestExport>>, TError, void, TContext>
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<Awaited<ReturnType<typeof requestExport>>, TError, void, TContext> => {
  const mutationKey = ['requestExport']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<Awaited<ReturnType<typeof requestExport>>, void> = () => {
    return requestExport(requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type RequestExportMutationResult = NonNullable<Awaited<ReturnType<typeof requestExport>>>

export type RequestExportMutationError = ErrorType<ErrorResponse | void>

/**
 * @summary Request a personal data export
 */
export const useRequestExport = <TError = ErrorType<ErrorResponse | void>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<Awaited<ReturnType<typeof requestExport>>, TError, void, TContext>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<Awaited<ReturnType<typeof requestExport>>, TError, void, TContext> => {
  return useMutation(getRequestExportMutationOptions(options), queryClient)
}
/**
 * Status of the current user's most recent export request, if any.
 * @summary Get the latest data export
 */
export const getLatestExport = (
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<UserExportDto | void>(
    { url: `/api/users/me/export`, method: 'GET', signal },
    options
  )
}

export const getGetLatestExportQueryKey = () => {
  return [`/api/users/me/export`] as const
}

export const getGetLatestExportQueryOptions = <
  TData = Awaited<ReturnType<typeof getLatestExport>>,
  TError = ErrorType<ErrorResponse | void>,
>(options?: {
  query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getLatestExport>>, TError, TData>>
  request?: SecondParameter<typeof axiosMutator>
}) => {
  const { query: queryOptions, request: requestOptions } = options ?? {}

  const queryKey = queryOptions?.queryKey ?? getGetLatestExportQueryKey()

  const queryFn: QueryFunction<Awaited<ReturnType<typeof getLatestExport>>> = ({ signal }) =>
    getLatestExport(requestOptions, signal)

  return { queryKey, queryFn, ...queryOptions } as UseQueryOptions<
    Awaited<ReturnType<typeof getLatestExport>>,
    TError,
    TData
  > & { queryKey: DataTag<QueryKey, TData, TError> }
}

export type GetLatestExportQueryResult = NonNullable<Awaited<ReturnType<typeof getLatestExport>>>
export type GetLatestExportQueryError = ErrorType<ErrorResponse | void>

export function useGetLatestExport<
  TData = Awaited<ReturnType<typeof getLatestExport>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  options: {
    query: Partial<UseQueryOptions<Awaited<ReturnType<typeof getLatestExport>>, TError, TData>> &
      Pick<
        DefinedInitialDataOptions<
          Awaited<ReturnType<typeof getLatestExport>>,
          TError,
          Awaited<ReturnType<typeof getLatestExport>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): DefinedUseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useGetLatestExport<
  TData = Awaited<ReturnType<typeof getLatestExport>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getLatestExport>>, TError, TData>> &
      Pick<
        UndefinedInitialDataOptions<
          Awaited<ReturnType<typeof getLatestExport>>,
          TError,
          Awaited<ReturnType<typeof getLatestExport>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useGetLatestExport<
  TData = Awaited<ReturnType<typeof getLatestExport>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getLatestExport>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
/**
 * @summary Get the latest data export
 */

export function useGetLatestExport<
  TData = Awaited<ReturnType<typeof getLatestExport>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getLatestExport>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {
  const queryOptions = getGetLatestExportQueryOptions(options)

  const query = useQuery(queryOptions, queryClient) as UseQueryResult<TData, TError> & {
    queryKey: DataTag<QueryKey, TData, TError>
  }

  return withQueryKey(query, queryOptions.queryKey)
}

/**
 * @summary Get the latest data export
 */
export const prefetchGetLatestExportQuery = async <
  TData = Awaited<ReturnType<typeof getLatestExport>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  queryClient: QueryClient,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getLatestExport>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  }
): Promise<QueryClient> => {
  const queryOptions = getGetLatestExportQueryOptions(options)

  await queryClient.prefetchQuery(queryOptions)

  return queryClient
}

/**
 * Status of one of the current user's export requests.
 * @summary Get a data export
 */
export const getExport = (
  exportId: string,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<UserExportDto>(
    { url: `/api/users/me/export/${exportId}`, method: 'GET', signal },
    options
  )
}

export const getGetExportQueryKey = (exportId: string) => {
  return [`/api/users/me/export/${exportId}`] as const
}

export const getGetExportQueryOptions = <
  TData = Awaited<ReturnType<typeof getExport>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  exportId: string,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getExport>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  }
) => {
  const { query: queryOptions, request: requestOptions } = options ?? {}

  const queryKey = queryOptions?.queryKey ?? getGetExportQueryKey(exportId)

  const queryFn: QueryFunction<Awaited<ReturnType<typeof getExport>>> = ({ signal }) =>
    getExport(exportId, requestOptions, signal)

  return {
    queryKey,
    queryFn,
    enabled: exportId !== null && exportId !== undefined,
    ...queryOptions,
  } as UseQueryOptions<Awaited<ReturnType<typeof getExport>>, TError, TData> & {
    queryKey: DataTag<QueryKey, TData, TError>
  }
}

export type GetExportQueryResult = NonNullable<Awaited<ReturnType<typeof getExport>>>
export type GetExportQueryError = ErrorType<ErrorResponse | void>

export function useGetExport<
  TData = Awaited<ReturnType<typeof getExport>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  exportId: string,
  options: {
    query: Partial<UseQueryOptions<Awaited<ReturnType<typeof getExport>>, TError, TData>> &
      Pick<
        DefinedInitialDataOptions<
          Awaited<ReturnType<typeof getExport>>,
          TError,
          Awaited<ReturnType<typeof getExport>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): DefinedUseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useGetExport<
  TData = Awaited<ReturnType<typeof getExport>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  exportId: string,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getExport>>, TError, TData>> &
      Pick<
        UndefinedInitialDataOptions<
          Awaited<ReturnType<typeof getExport>>,
          TError,
          Awaited<ReturnType<typeof getExport>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useGetExport<
  TData = Awaited<ReturnType<typeof getExport>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  exportId: string,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getExport>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
/**
 * @summary Get a data export
 */

export function useGetExport<
  TData = Awaited<ReturnType<typeof getExport>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  exportId: string,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getExport>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {
  const queryOptions = getGetExportQueryOptions(exportId, options)

  const query = useQuery(queryOptions, queryClient) as UseQueryResult<TData, TError> & {
    queryKey: DataTag<QueryKey, TData, TError>
  }

  return withQueryKey(query, queryOptions.queryKey)
}

/**
 * @summary Get a data export
 */
export const prefetchGetExportQuery = async <
  TData = Awaited<ReturnType<typeof getExport>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  queryClient: QueryClient,
  exportId: string,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getExport>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  }
): Promise<QueryClient> => {
  const queryOptions = getGetExportQueryOptions(exportId, options)

  await queryClient.prefetchQuery(queryOptions)

  return queryClient
}

/**
 * Search users by display name
 * @summary Search users
 */
export const searchUsers = (
  params?: SearchUsersParams,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<PublicUserDto[]>(
    { url: `/api/users/search`, method: 'GET', params, signal },
    options
  )
}

export const getSearchUsersQueryKey = (params?: SearchUsersParams) => {
  return [`/api/users/search`, ...(params ? [params] : [])] as const
}

export const getSearchUsersQueryOptions = <
  TData = Awaited<ReturnType<typeof searchUsers>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  params?: SearchUsersParams,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof searchUsers>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  }
) => {
  const { query: queryOptions, request: requestOptions } = options ?? {}

  const queryKey = queryOptions?.queryKey ?? getSearchUsersQueryKey(params)

  const queryFn: QueryFunction<Awaited<ReturnType<typeof searchUsers>>> = ({ signal }) =>
    searchUsers(params, requestOptions, signal)

  return { queryKey, queryFn, ...queryOptions } as UseQueryOptions<
    Awaited<ReturnType<typeof searchUsers>>,
    TError,
    TData
  > & { queryKey: DataTag<QueryKey, TData, TError> }
}

export type SearchUsersQueryResult = NonNullable<Awaited<ReturnType<typeof searchUsers>>>
export type SearchUsersQueryError = ErrorType<ErrorResponse | void>

export function useSearchUsers<
  TData = Awaited<ReturnType<typeof searchUsers>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  params: undefined | SearchUsersParams,
  options: {
    query: Partial<UseQueryOptions<Awaited<ReturnType<typeof searchUsers>>, TError, TData>> &
      Pick<
        DefinedInitialDataOptions<
          Awaited<ReturnType<typeof searchUsers>>,
          TError,
          Awaited<ReturnType<typeof searchUsers>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): DefinedUseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useSearchUsers<
  TData = Awaited<ReturnType<typeof searchUsers>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  params?: SearchUsersParams,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof searchUsers>>, TError, TData>> &
      Pick<
        UndefinedInitialDataOptions<
          Awaited<ReturnType<typeof searchUsers>>,
          TError,
          Awaited<ReturnType<typeof searchUsers>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useSearchUsers<
  TData = Awaited<ReturnType<typeof searchUsers>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  params?: SearchUsersParams,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof searchUsers>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
/**
 * @summary Search users
 */

export function useSearchUsers<
  TData = Awaited<ReturnType<typeof searchUsers>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  params?: SearchUsersParams,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof searchUsers>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {
  const queryOptions = getSearchUsersQueryOptions(params, options)

  const query = useQuery(queryOptions, queryClient) as UseQueryResult<TData, TError> & {
    queryKey: DataTag<QueryKey, TData, TError>
  }

  return withQueryKey(query, queryOptions.queryKey)
}

/**
 * @summary Search users
 */
export const prefetchSearchUsersQuery = async <
  TData = Awaited<ReturnType<typeof searchUsers>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  queryClient: QueryClient,
  params?: SearchUsersParams,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof searchUsers>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  }
): Promise<QueryClient> => {
  const queryOptions = getSearchUsersQueryOptions(params, options)

  await queryClient.prefetchQuery(queryOptions)

  return queryClient
}
