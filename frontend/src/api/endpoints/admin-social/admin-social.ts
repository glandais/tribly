import { useMutation } from '@tanstack/react-query'
import type {
  MutationFunction,
  QueryClient,
  UseMutationOptions,
  UseMutationResult,
} from '@tanstack/react-query'

import type {
  BackfillStravaIdentitiesParams,
  ErrorResponse,
  SocialBackfillResponse,
} from '../../dto'

import { axiosMutator } from '../../../lib/axiosInstance.ts'
import type { ErrorType } from '../../../lib/axiosInstance.ts'

type SecondParameter<T extends (...args: never) => unknown> = Parameters<T>[1]

/**
 * Create social-identity rows for migrated placeholder Strava accounts so they can log in with Strava. Idempotent.
 * @summary Backfill Strava identities
 */
export const backfillStravaIdentities = (
  params?: BackfillStravaIdentitiesParams,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<SocialBackfillResponse>(
    { url: `/api/admin/social/backfill-strava`, method: 'POST', params, signal },
    options
  )
}

export const getBackfillStravaIdentitiesMutationOptions = <
  TError = ErrorType<void | ErrorResponse>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof backfillStravaIdentities>>,
    TError,
    { params?: BackfillStravaIdentitiesParams },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof backfillStravaIdentities>>,
  TError,
  { params?: BackfillStravaIdentitiesParams },
  TContext
> => {
  const mutationKey = ['backfillStravaIdentities']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof backfillStravaIdentities>>,
    { params?: BackfillStravaIdentitiesParams }
  > = (props) => {
    const { params } = props ?? {}

    return backfillStravaIdentities(params, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type BackfillStravaIdentitiesMutationResult = NonNullable<
  Awaited<ReturnType<typeof backfillStravaIdentities>>
>

export type BackfillStravaIdentitiesMutationError = ErrorType<void | ErrorResponse>

/**
 * @summary Backfill Strava identities
 */
export const useBackfillStravaIdentities = <
  TError = ErrorType<void | ErrorResponse>,
  TContext = unknown,
>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof backfillStravaIdentities>>,
      TError,
      { params?: BackfillStravaIdentitiesParams },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof backfillStravaIdentities>>,
  TError,
  { params?: BackfillStravaIdentitiesParams },
  TContext
> => {
  return useMutation(getBackfillStravaIdentitiesMutationOptions(options), queryClient)
}
