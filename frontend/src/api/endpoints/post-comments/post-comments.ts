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

import type { CommentDto, CommentListResponse, CommentRequest, ErrorResponse } from '../../dto'

import { axiosMutator } from '../../../lib/axiosInstance'
import type { ErrorType, BodyType } from '../../../lib/axiosInstance'

type SecondParameter<T extends (...args: never) => unknown> = Parameters<T>[1]

/**
 * @summary List post comments
 */
export const listPostComments = (
  teamSlug: string,
  entitySlug: string,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<CommentListResponse>(
    { url: `/api/teams/${teamSlug}/posts/${entitySlug}/comments`, method: 'GET', signal },
    options
  )
}

export const getListPostCommentsQueryKey = (teamSlug: string, entitySlug: string) => {
  return [`/api/teams/${teamSlug}/posts/${entitySlug}/comments`] as const
}

export const getListPostCommentsQueryOptions = <
  TData = Awaited<ReturnType<typeof listPostComments>>,
  TError = ErrorType<void | ErrorResponse>,
>(
  teamSlug: string,
  entitySlug: string,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof listPostComments>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  }
) => {
  const { query: queryOptions, request: requestOptions } = options ?? {}

  const queryKey = queryOptions?.queryKey ?? getListPostCommentsQueryKey(teamSlug, entitySlug)

  const queryFn: QueryFunction<Awaited<ReturnType<typeof listPostComments>>> = ({ signal }) =>
    listPostComments(teamSlug, entitySlug, requestOptions, signal)

  return {
    queryKey,
    queryFn,
    enabled:
      teamSlug !== null &&
      teamSlug !== undefined &&
      entitySlug !== null &&
      entitySlug !== undefined,
    ...queryOptions,
  } as UseQueryOptions<Awaited<ReturnType<typeof listPostComments>>, TError, TData> & {
    queryKey: DataTag<QueryKey, TData, TError>
  }
}

export type ListPostCommentsQueryResult = NonNullable<Awaited<ReturnType<typeof listPostComments>>>
export type ListPostCommentsQueryError = ErrorType<void | ErrorResponse>

export function useListPostComments<
  TData = Awaited<ReturnType<typeof listPostComments>>,
  TError = ErrorType<void | ErrorResponse>,
>(
  teamSlug: string,
  entitySlug: string,
  options: {
    query: Partial<UseQueryOptions<Awaited<ReturnType<typeof listPostComments>>, TError, TData>> &
      Pick<
        DefinedInitialDataOptions<
          Awaited<ReturnType<typeof listPostComments>>,
          TError,
          Awaited<ReturnType<typeof listPostComments>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): DefinedUseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useListPostComments<
  TData = Awaited<ReturnType<typeof listPostComments>>,
  TError = ErrorType<void | ErrorResponse>,
>(
  teamSlug: string,
  entitySlug: string,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof listPostComments>>, TError, TData>> &
      Pick<
        UndefinedInitialDataOptions<
          Awaited<ReturnType<typeof listPostComments>>,
          TError,
          Awaited<ReturnType<typeof listPostComments>>
        >,
        'initialData'
      >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
export function useListPostComments<
  TData = Awaited<ReturnType<typeof listPostComments>>,
  TError = ErrorType<void | ErrorResponse>,
>(
  teamSlug: string,
  entitySlug: string,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof listPostComments>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> }
/**
 * @summary List post comments
 */

export function useListPostComments<
  TData = Awaited<ReturnType<typeof listPostComments>>,
  TError = ErrorType<void | ErrorResponse>,
>(
  teamSlug: string,
  entitySlug: string,
  options?: {
    query?: Partial<UseQueryOptions<Awaited<ReturnType<typeof listPostComments>>, TError, TData>>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseQueryResult<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {
  const queryOptions = getListPostCommentsQueryOptions(teamSlug, entitySlug, options)

  const query = useQuery(queryOptions, queryClient) as UseQueryResult<TData, TError> & {
    queryKey: DataTag<QueryKey, TData, TError>
  }

  return { ...query, queryKey: queryOptions.queryKey }
}

/**
 * @summary Create post comment
 */
export const createPostComment = (
  teamSlug: string,
  entitySlug: string,
  commentRequest: BodyType<CommentRequest>,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<CommentDto>(
    {
      url: `/api/teams/${teamSlug}/posts/${entitySlug}/comments`,
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      data: commentRequest,
      signal,
    },
    options
  )
}

export const getCreatePostCommentMutationOptions = <
  TError = ErrorType<ErrorResponse | void>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof createPostComment>>,
    TError,
    { teamSlug: string; entitySlug: string; data: BodyType<CommentRequest> },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof createPostComment>>,
  TError,
  { teamSlug: string; entitySlug: string; data: BodyType<CommentRequest> },
  TContext
> => {
  const mutationKey = ['createPostComment']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof createPostComment>>,
    { teamSlug: string; entitySlug: string; data: BodyType<CommentRequest> }
  > = (props) => {
    const { teamSlug, entitySlug, data } = props ?? {}

    return createPostComment(teamSlug, entitySlug, data, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type CreatePostCommentMutationResult = NonNullable<
  Awaited<ReturnType<typeof createPostComment>>
>
export type CreatePostCommentMutationBody = BodyType<CommentRequest>
export type CreatePostCommentMutationError = ErrorType<ErrorResponse | void>

/**
 * @summary Create post comment
 */
export const useCreatePostComment = <TError = ErrorType<ErrorResponse | void>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof createPostComment>>,
      TError,
      { teamSlug: string; entitySlug: string; data: BodyType<CommentRequest> },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof createPostComment>>,
  TError,
  { teamSlug: string; entitySlug: string; data: BodyType<CommentRequest> },
  TContext
> => {
  return useMutation(getCreatePostCommentMutationOptions(options), queryClient)
}
/**
 * @summary Delete post comment
 */
export const deletePostComment = (
  teamSlug: string,
  entitySlug: string,
  commentId: string,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<void>(
    {
      url: `/api/teams/${teamSlug}/posts/${entitySlug}/comments/${commentId}`,
      method: 'DELETE',
      signal,
    },
    options
  )
}

export const getDeletePostCommentMutationOptions = <
  TError = ErrorType<void | ErrorResponse>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof deletePostComment>>,
    TError,
    { teamSlug: string; entitySlug: string; commentId: string },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof deletePostComment>>,
  TError,
  { teamSlug: string; entitySlug: string; commentId: string },
  TContext
> => {
  const mutationKey = ['deletePostComment']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof deletePostComment>>,
    { teamSlug: string; entitySlug: string; commentId: string }
  > = (props) => {
    const { teamSlug, entitySlug, commentId } = props ?? {}

    return deletePostComment(teamSlug, entitySlug, commentId, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type DeletePostCommentMutationResult = NonNullable<
  Awaited<ReturnType<typeof deletePostComment>>
>

export type DeletePostCommentMutationError = ErrorType<void | ErrorResponse>

/**
 * @summary Delete post comment
 */
export const useDeletePostComment = <TError = ErrorType<void | ErrorResponse>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof deletePostComment>>,
      TError,
      { teamSlug: string; entitySlug: string; commentId: string },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof deletePostComment>>,
  TError,
  { teamSlug: string; entitySlug: string; commentId: string },
  TContext
> => {
  return useMutation(getDeletePostCommentMutationOptions(options), queryClient)
}
