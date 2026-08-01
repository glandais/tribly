import { useMutation } from '@tanstack/react-query'
import type {
  MutationFunction,
  QueryClient,
  UseMutationOptions,
  UseMutationResult,
} from '@tanstack/react-query'

import type { BetaSignupRequest } from '../../dto'

import { axiosMutator } from '../../../lib/axiosInstance.ts'
import type { ErrorType, BodyType } from '../../../lib/axiosInstance.ts'

type SecondParameter<T extends (...args: never) => unknown> = Parameters<T>[1]

/**
 * Record interest in being added to a beta program (TestFlight, Play Console, Garmin Connect IQ) once one opens. Submitting the same email again has no effect.
 * @summary Sign up for a beta program
 */
export const signUpForBeta = (
  betaSignupRequest: BodyType<BetaSignupRequest>,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<void>(
    {
      url: `/api/beta-signups`,
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      data: betaSignupRequest,
      signal,
    },
    options
  )
}

export const getSignUpForBetaMutationOptions = <
  TError = ErrorType<void>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof signUpForBeta>>,
    TError,
    { data: BodyType<BetaSignupRequest> },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof signUpForBeta>>,
  TError,
  { data: BodyType<BetaSignupRequest> },
  TContext
> => {
  const mutationKey = ['signUpForBeta']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof signUpForBeta>>,
    { data: BodyType<BetaSignupRequest> }
  > = (props) => {
    const { data } = props ?? {}

    return signUpForBeta(data, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type SignUpForBetaMutationResult = NonNullable<Awaited<ReturnType<typeof signUpForBeta>>>
export type SignUpForBetaMutationBody = BodyType<BetaSignupRequest>
export type SignUpForBetaMutationError = ErrorType<void>

/**
 * @summary Sign up for a beta program
 */
export const useSignUpForBeta = <TError = ErrorType<void>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof signUpForBeta>>,
      TError,
      { data: BodyType<BetaSignupRequest> },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof signUpForBeta>>,
  TError,
  { data: BodyType<BetaSignupRequest> },
  TContext
> => {
  return useMutation(getSignUpForBetaMutationOptions(options), queryClient)
}
