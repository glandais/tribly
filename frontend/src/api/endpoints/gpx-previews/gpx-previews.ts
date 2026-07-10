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
  CreatePreviewBody,
  ErrorResponse,
  GpsServiceType,
  GpxPreviewDto,
  GpxPreviewListResponse,
  ListMyPreviewsParams,
  RouteDto,
  RouteRequest,
  RouteUploadResponse,
  UpdatePreviewBody,
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
 * Returns the current user's previews still within the 30-day retention window, most recent first
 * @summary List the current user's analysed GPX files
 */
export const listMyPreviews = (
  params?: ListMyPreviewsParams,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<GpxPreviewListResponse>(
    { url: `/api/gpx-previews`, method: 'GET', params, signal },
    options
  )
}

export const getListMyPreviewsQueryKey = (params?: ListMyPreviewsParams) => {
  return [`/api/gpx-previews`, ...(params ? [params] : [])] as const
}

export const getListMyPreviewsQueryOptions = <
  TData = Awaited<ReturnType<typeof listMyPreviews>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  params?: ListMyPreviewsParams,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof listMyPreviews>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  }
) => {
  const { query: queryOptions, request: requestOptions } = options ?? {}

  const queryKey = queryOptions?.queryKey ?? getListMyPreviewsQueryKey(params)

  const queryFn: QueryFunction<Awaited<ReturnType<typeof listMyPreviews>>> = ({ signal }) =>
    listMyPreviews(params, requestOptions, signal)

  return { queryKey, queryFn, ...queryOptions } as UseQueryOptions<
    Awaited<ReturnType<typeof listMyPreviews>>,
    TError,
    TData
  > & { queryKey: DataTag<QueryKey, TData, TError> }
}

export type ListMyPreviewsQueryResult = NonNullable<Awaited<ReturnType<typeof listMyPreviews>>>
export type ListMyPreviewsQueryError = ErrorType<ErrorResponse | void>

export function useListMyPreviews<
  TData = Awaited<ReturnType<typeof listMyPreviews>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  params: undefined | ListMyPreviewsParams,
  options: {
    query: Partial<UseQueryOptions<Awaited<ReturnType<typeof listMyPreviews>>, TError, TData>> &
      Pick<
        DefinedInitialDataOptions<
          Awaited<ReturnType<typeof listMyPreviews>>,
          TError,
          Awaited<ReturnType<typeof listMyPreviews>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): DefinedUseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useListMyPreviews<
  TData = Awaited<ReturnType<typeof listMyPreviews>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  params?: ListMyPreviewsParams,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof listMyPreviews>>, TError, TData>> &
      Pick<
        UndefinedInitialDataOptions<
          Awaited<ReturnType<typeof listMyPreviews>>,
          TError,
          Awaited<ReturnType<typeof listMyPreviews>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useListMyPreviews<
  TData = Awaited<ReturnType<typeof listMyPreviews>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  params?: ListMyPreviewsParams,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof listMyPreviews>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
/**
 * @summary List the current user's analysed GPX files
 */

export function useListMyPreviews<
  TData = Awaited<ReturnType<typeof listMyPreviews>>,
  TError = ErrorType<ErrorResponse | void>,
>(
  params?: ListMyPreviewsParams,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof listMyPreviews>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {
  const queryOptions = getListMyPreviewsQueryOptions(params, options)

  const query = useQuery(queryOptions, queryClient) as UseQueryResult<TData, TError> & {
    queryKey: DataTag<QueryKey, TData, TError>
  }

  return withQueryKey(query, queryOptions.queryKey)
}

/**
 * Uploads a GPX file, runs the elevation and climb pipeline, and stores the result under an unguessable identifier for 30 days
 * @summary Analyse a GPX file
 */
export const createPreview = (
  createPreviewBody: BodyType<CreatePreviewBody>,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  const formData = new FormData()
  if (createPreviewBody.gpxFile !== undefined) {
    formData.append(`gpxFile`, createPreviewBody.gpxFile)
  }

  return axiosMutator<GpxPreviewDto>(
    {
      url: `/api/gpx-previews`,
      method: 'POST',
      headers: { 'Content-Type': 'multipart/form-data' },
      data: formData,
      signal,
    },
    options
  )
}

export const getCreatePreviewMutationOptions = <
  TError = ErrorType<ErrorResponse | void>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof createPreview>>,
    TError,
    { data: BodyType<CreatePreviewBody> },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof createPreview>>,
  TError,
  { data: BodyType<CreatePreviewBody> },
  TContext
> => {
  const mutationKey = ['createPreview']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof createPreview>>,
    { data: BodyType<CreatePreviewBody> }
  > = (props) => {
    const { data } = props ?? {}

    return createPreview(data, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type CreatePreviewMutationResult = NonNullable<Awaited<ReturnType<typeof createPreview>>>
export type CreatePreviewMutationBody = BodyType<CreatePreviewBody>
export type CreatePreviewMutationError = ErrorType<ErrorResponse | void>

/**
 * @summary Analyse a GPX file
 */
export const useCreatePreview = <TError = ErrorType<ErrorResponse | void>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof createPreview>>,
      TError,
      { data: BodyType<CreatePreviewBody> },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof createPreview>>,
  TError,
  { data: BodyType<CreatePreviewBody> },
  TContext
> => {
  return useMutation(getCreatePreviewMutationOptions(options), queryClient)
}
/**
 * Rename the preview and, when a new GPX file or planner points are provided, replay the pipeline to replace its track. Only the creator may edit.
 * @summary Update an analysed GPX file
 */
export const updatePreview = (
  previewId: string,
  updatePreviewBody: BodyType<UpdatePreviewBody>,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  const formData = new FormData()
  if (updatePreviewBody.preview !== undefined) {
    formData.append(`preview`, JSON.stringify(updatePreviewBody.preview))
  }
  if (updatePreviewBody.gpxFile !== undefined) {
    formData.append(`gpxFile`, updatePreviewBody.gpxFile)
  }

  return axiosMutator<GpxPreviewDto>(
    {
      url: `/api/gpx-previews/${previewId}`,
      method: 'PUT',
      headers: { 'Content-Type': 'multipart/form-data' },
      data: formData,
      signal,
    },
    options
  )
}

export const getUpdatePreviewMutationOptions = <
  TError = ErrorType<ErrorResponse>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof updatePreview>>,
    TError,
    { previewId: string; data: BodyType<UpdatePreviewBody> },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof updatePreview>>,
  TError,
  { previewId: string; data: BodyType<UpdatePreviewBody> },
  TContext
> => {
  const mutationKey = ['updatePreview']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof updatePreview>>,
    { previewId: string; data: BodyType<UpdatePreviewBody> }
  > = (props) => {
    const { previewId, data } = props ?? {}

    return updatePreview(previewId, data, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type UpdatePreviewMutationResult = NonNullable<Awaited<ReturnType<typeof updatePreview>>>
export type UpdatePreviewMutationBody = BodyType<UpdatePreviewBody>
export type UpdatePreviewMutationError = ErrorType<ErrorResponse>

/**
 * @summary Update an analysed GPX file
 */
export const useUpdatePreview = <TError = ErrorType<ErrorResponse>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof updatePreview>>,
      TError,
      { previewId: string; data: BodyType<UpdatePreviewBody> },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof updatePreview>>,
  TError,
  { previewId: string; data: BodyType<UpdatePreviewBody> },
  TContext
> => {
  return useMutation(getUpdatePreviewMutationOptions(options), queryClient)
}
/**
 * Anyone holding the link may read the preview: the unguessable identifier is what grants access. Creating and deleting require an account.
 * @summary Get an analysed GPX file
 */
export const getPreview = (
  previewId: string,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<GpxPreviewDto>(
    { url: `/api/gpx-previews/${previewId}`, method: 'GET', signal },
    options
  )
}

export const getGetPreviewQueryKey = (previewId: string) => {
  return [`/api/gpx-previews/${previewId}`] as const
}

export const getGetPreviewQueryOptions = <
  TData = Awaited<ReturnType<typeof getPreview>>,
  TError = ErrorType<ErrorResponse>,
>(
  previewId: string,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getPreview>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  }
) => {
  const { query: queryOptions, request: requestOptions } = options ?? {}

  const queryKey = queryOptions?.queryKey ?? getGetPreviewQueryKey(previewId)

  const queryFn: QueryFunction<Awaited<ReturnType<typeof getPreview>>> = ({ signal }) =>
    getPreview(previewId, requestOptions, signal)

  return {
    queryKey,
    queryFn,
    enabled: previewId !== null && previewId !== undefined,
    ...queryOptions,
  } as UseQueryOptions<Awaited<ReturnType<typeof getPreview>>, TError, TData> & {
    queryKey: DataTag<QueryKey, TData, TError>
  }
}

export type GetPreviewQueryResult = NonNullable<Awaited<ReturnType<typeof getPreview>>>
export type GetPreviewQueryError = ErrorType<ErrorResponse>

export function useGetPreview<
  TData = Awaited<ReturnType<typeof getPreview>>,
  TError = ErrorType<ErrorResponse>,
>(
  previewId: string,
  options: {
    query: Partial<UseQueryOptions<Awaited<ReturnType<typeof getPreview>>, TError, TData>> &
      Pick<
        DefinedInitialDataOptions<
          Awaited<ReturnType<typeof getPreview>>,
          TError,
          Awaited<ReturnType<typeof getPreview>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): DefinedUseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useGetPreview<
  TData = Awaited<ReturnType<typeof getPreview>>,
  TError = ErrorType<ErrorResponse>,
>(
  previewId: string,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getPreview>>, TError, TData>> &
      Pick<
        UndefinedInitialDataOptions<
          Awaited<ReturnType<typeof getPreview>>,
          TError,
          Awaited<ReturnType<typeof getPreview>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useGetPreview<
  TData = Awaited<ReturnType<typeof getPreview>>,
  TError = ErrorType<ErrorResponse>,
>(
  previewId: string,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getPreview>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
/**
 * @summary Get an analysed GPX file
 */

export function useGetPreview<
  TData = Awaited<ReturnType<typeof getPreview>>,
  TError = ErrorType<ErrorResponse>,
>(
  previewId: string,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof getPreview>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {
  const queryOptions = getGetPreviewQueryOptions(previewId, options)

  const query = useQuery(queryOptions, queryClient) as UseQueryResult<TData, TError> & {
    queryKey: DataTag<QueryKey, TData, TError>
  }

  return withQueryKey(query, queryOptions.queryKey)
}

/**
 * Only the creator may delete
 * @summary Delete an analysed GPX file
 */
export const deletePreview = (
  previewId: string,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<void>(
    { url: `/api/gpx-previews/${previewId}`, method: 'DELETE', signal },
    options
  )
}

export const getDeletePreviewMutationOptions = <
  TError = ErrorType<ErrorResponse>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof deletePreview>>,
    TError,
    { previewId: string },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof deletePreview>>,
  TError,
  { previewId: string },
  TContext
> => {
  const mutationKey = ['deletePreview']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof deletePreview>>,
    { previewId: string }
  > = (props) => {
    const { previewId } = props ?? {}

    return deletePreview(previewId, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type DeletePreviewMutationResult = NonNullable<Awaited<ReturnType<typeof deletePreview>>>

export type DeletePreviewMutationError = ErrorType<ErrorResponse>

/**
 * @summary Delete an analysed GPX file
 */
export const useDeletePreview = <TError = ErrorType<ErrorResponse>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof deletePreview>>,
      TError,
      { previewId: string },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof deletePreview>>,
  TError,
  { previewId: string },
  TContext
> => {
  return useMutation(getDeletePreviewMutationOptions(options), queryClient)
}
/**
 * Uploads the preview to the current user's connected Garmin or Hammerhead
 * @summary Send an analysed GPX file to a GPS service
 */
export const uploadToGpsService = (
  previewId: string,
  serviceType: GpsServiceType,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<RouteUploadResponse>(
    { url: `/api/gpx-previews/${previewId}/gps/${serviceType}`, method: 'POST', signal },
    options
  )
}

export const getUploadToGpsServiceMutationOptions = <
  TError = ErrorType<ErrorResponse | void>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof uploadToGpsService>>,
    TError,
    { previewId: string; serviceType: GpsServiceType },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof uploadToGpsService>>,
  TError,
  { previewId: string; serviceType: GpsServiceType },
  TContext
> => {
  const mutationKey = ['uploadToGpsService']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof uploadToGpsService>>,
    { previewId: string; serviceType: GpsServiceType }
  > = (props) => {
    const { previewId, serviceType } = props ?? {}

    return uploadToGpsService(previewId, serviceType, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type UploadToGpsServiceMutationResult = NonNullable<
  Awaited<ReturnType<typeof uploadToGpsService>>
>

export type UploadToGpsServiceMutationError = ErrorType<ErrorResponse | void>

/**
 * @summary Send an analysed GPX file to a GPS service
 */
export const useUploadToGpsService = <TError = ErrorType<ErrorResponse | void>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof uploadToGpsService>>,
      TError,
      { previewId: string; serviceType: GpsServiceType },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof uploadToGpsService>>,
  TError,
  { previewId: string; serviceType: GpsServiceType },
  TContext
> => {
  return useMutation(getUploadToGpsServiceMutationOptions(options), queryClient)
}
/**
 * Creates a team route from the preview's original upload
 * @summary Save an analysed GPX file as a route
 */
export const createRouteFromPreview = (
  previewId: string,
  teamSlug: string,
  routeRequest: BodyType<RouteRequest>,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<RouteDto>(
    {
      url: `/api/gpx-previews/${previewId}/routes/${teamSlug}`,
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      data: routeRequest,
      signal,
    },
    options
  )
}

export const getCreateRouteFromPreviewMutationOptions = <
  TError = ErrorType<ErrorResponse>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof createRouteFromPreview>>,
    TError,
    { previewId: string; teamSlug: string; data: BodyType<RouteRequest> },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof createRouteFromPreview>>,
  TError,
  { previewId: string; teamSlug: string; data: BodyType<RouteRequest> },
  TContext
> => {
  const mutationKey = ['createRouteFromPreview']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof createRouteFromPreview>>,
    { previewId: string; teamSlug: string; data: BodyType<RouteRequest> }
  > = (props) => {
    const { previewId, teamSlug, data } = props ?? {}

    return createRouteFromPreview(previewId, teamSlug, data, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type CreateRouteFromPreviewMutationResult = NonNullable<
  Awaited<ReturnType<typeof createRouteFromPreview>>
>
export type CreateRouteFromPreviewMutationBody = BodyType<RouteRequest>
export type CreateRouteFromPreviewMutationError = ErrorType<ErrorResponse>

/**
 * @summary Save an analysed GPX file as a route
 */
export const useCreateRouteFromPreview = <TError = ErrorType<ErrorResponse>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof createRouteFromPreview>>,
      TError,
      { previewId: string; teamSlug: string; data: BodyType<RouteRequest> },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof createRouteFromPreview>>,
  TError,
  { previewId: string; teamSlug: string; data: BodyType<RouteRequest> },
  TContext
> => {
  return useMutation(getCreateRouteFromPreviewMutationOptions(options), queryClient)
}
