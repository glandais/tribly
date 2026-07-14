import { useMutation } from '@tanstack/react-query'
import type {
  MutationFunction,
  QueryClient,
  UseMutationOptions,
  UseMutationResult,
} from '@tanstack/react-query'

import type {
  AuthResponse,
  EmailChangeRequest,
  ErrorResponse,
  ForgotPasswordRequest,
  LoginRequest,
  MessageResponse,
  OtpRequest,
  RegisterRequest,
  ResetPasswordRequest,
  VerifyOtpRequest,
  VerifyTokenRequest,
} from '../../dto'

import { axiosMutator } from '../../../lib/axiosInstance.ts'
import type { ErrorType, BodyType } from '../../../lib/axiosInstance.ts'

type SecondParameter<T extends (...args: never) => unknown> = Parameters<T>[1]

/**
 * Set/change the account's real email (e.g. recover a migrated Strava account). Sends a verification link to the new address.
 * @summary Request email change
 */
export const requestEmailChange = (
  emailChangeRequest: BodyType<EmailChangeRequest>,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<MessageResponse>(
    {
      url: `/api/auth/email/change-request`,
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      data: emailChangeRequest,
      signal,
    },
    options
  )
}

export const getRequestEmailChangeMutationOptions = <
  TError = ErrorType<void | ErrorResponse>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof requestEmailChange>>,
    TError,
    { data: BodyType<EmailChangeRequest> },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof requestEmailChange>>,
  TError,
  { data: BodyType<EmailChangeRequest> },
  TContext
> => {
  const mutationKey = ['requestEmailChange']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof requestEmailChange>>,
    { data: BodyType<EmailChangeRequest> }
  > = (props) => {
    const { data } = props ?? {}

    return requestEmailChange(data, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type RequestEmailChangeMutationResult = NonNullable<
  Awaited<ReturnType<typeof requestEmailChange>>
>
export type RequestEmailChangeMutationBody = BodyType<EmailChangeRequest>
export type RequestEmailChangeMutationError = ErrorType<void | ErrorResponse>

/**
 * @summary Request email change
 */
export const useRequestEmailChange = <TError = ErrorType<void | ErrorResponse>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof requestEmailChange>>,
      TError,
      { data: BodyType<EmailChangeRequest> },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof requestEmailChange>>,
  TError,
  { data: BodyType<EmailChangeRequest> },
  TContext
> => {
  return useMutation(getRequestEmailChangeMutationOptions(options), queryClient)
}
/**
 * Send a 6-digit code to the user's email to reset their password
 * @summary Request password reset
 */
export const forgotPassword = (
  forgotPasswordRequest: BodyType<ForgotPasswordRequest>,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<MessageResponse>(
    {
      url: `/api/auth/forgot-password`,
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      data: forgotPasswordRequest,
      signal,
    },
    options
  )
}

export const getForgotPasswordMutationOptions = <
  TError = ErrorType<void>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof forgotPassword>>,
    TError,
    { data: BodyType<ForgotPasswordRequest> },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof forgotPassword>>,
  TError,
  { data: BodyType<ForgotPasswordRequest> },
  TContext
> => {
  const mutationKey = ['forgotPassword']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof forgotPassword>>,
    { data: BodyType<ForgotPasswordRequest> }
  > = (props) => {
    const { data } = props ?? {}

    return forgotPassword(data, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type ForgotPasswordMutationResult = NonNullable<Awaited<ReturnType<typeof forgotPassword>>>
export type ForgotPasswordMutationBody = BodyType<ForgotPasswordRequest>
export type ForgotPasswordMutationError = ErrorType<void>

/**
 * @summary Request password reset
 */
export const useForgotPassword = <TError = ErrorType<void>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof forgotPassword>>,
      TError,
      { data: BodyType<ForgotPasswordRequest> },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof forgotPassword>>,
  TError,
  { data: BodyType<ForgotPasswordRequest> },
  TContext
> => {
  return useMutation(getForgotPasswordMutationOptions(options), queryClient)
}
/**
 * Authenticate using email and password
 * @summary Login with password
 */
export const loginWithPassword = (
  loginRequest: BodyType<LoginRequest>,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<AuthResponse>(
    {
      url: `/api/auth/login`,
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      data: loginRequest,
      signal,
    },
    options
  )
}

export const getLoginWithPasswordMutationOptions = <
  TError = ErrorType<ErrorResponse>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof loginWithPassword>>,
    TError,
    { data: BodyType<LoginRequest> },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof loginWithPassword>>,
  TError,
  { data: BodyType<LoginRequest> },
  TContext
> => {
  const mutationKey = ['loginWithPassword']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof loginWithPassword>>,
    { data: BodyType<LoginRequest> }
  > = (props) => {
    const { data } = props ?? {}

    return loginWithPassword(data, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type LoginWithPasswordMutationResult = NonNullable<
  Awaited<ReturnType<typeof loginWithPassword>>
>
export type LoginWithPasswordMutationBody = BodyType<LoginRequest>
export type LoginWithPasswordMutationError = ErrorType<ErrorResponse>

/**
 * @summary Login with password
 */
export const useLoginWithPassword = <TError = ErrorType<ErrorResponse>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof loginWithPassword>>,
      TError,
      { data: BodyType<LoginRequest> },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof loginWithPassword>>,
  TError,
  { data: BodyType<LoginRequest> },
  TContext
> => {
  return useMutation(getLoginWithPasswordMutationOptions(options), queryClient)
}
/**
 * Logout and invalidate the refresh token
 * @summary Logout
 */
export const logout = (options?: SecondParameter<typeof axiosMutator>, signal?: AbortSignal) => {
  return axiosMutator<void>({ url: `/api/auth/logout`, method: 'POST', signal }, options)
}

export const getLogoutMutationOptions = <
  TError = ErrorType<unknown>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<Awaited<ReturnType<typeof logout>>, TError, void, TContext>
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<Awaited<ReturnType<typeof logout>>, TError, void, TContext> => {
  const mutationKey = ['logout']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<Awaited<ReturnType<typeof logout>>, void> = () => {
    return logout(requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type LogoutMutationResult = NonNullable<Awaited<ReturnType<typeof logout>>>

export type LogoutMutationError = ErrorType<unknown>

/**
 * @summary Logout
 */
export const useLogout = <TError = ErrorType<unknown>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<Awaited<ReturnType<typeof logout>>, TError, void, TContext>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<Awaited<ReturnType<typeof logout>>, TError, void, TContext> => {
  return useMutation(getLogoutMutationOptions(options), queryClient)
}
/**
 * Logout from all devices by invalidating all refresh tokens
 * @summary Logout all sessions
 */
export const logoutAll = (options?: SecondParameter<typeof axiosMutator>, signal?: AbortSignal) => {
  return axiosMutator<void>({ url: `/api/auth/logout-all`, method: 'POST', signal }, options)
}

export const getLogoutAllMutationOptions = <
  TError = ErrorType<ErrorResponse | void>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<Awaited<ReturnType<typeof logoutAll>>, TError, void, TContext>
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<Awaited<ReturnType<typeof logoutAll>>, TError, void, TContext> => {
  const mutationKey = ['logoutAll']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<Awaited<ReturnType<typeof logoutAll>>, void> = () => {
    return logoutAll(requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type LogoutAllMutationResult = NonNullable<Awaited<ReturnType<typeof logoutAll>>>

export type LogoutAllMutationError = ErrorType<ErrorResponse | void>

/**
 * @summary Logout all sessions
 */
export const useLogoutAll = <TError = ErrorType<ErrorResponse | void>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<Awaited<ReturnType<typeof logoutAll>>, TError, void, TContext>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<Awaited<ReturnType<typeof logoutAll>>, TError, void, TContext> => {
  return useMutation(getLogoutAllMutationOptions(options), queryClient)
}
/**
 * Send a 6-digit OTP code to the user's email for passwordless login
 * @summary Request OTP
 */
export const requestOtp = (
  otpRequest: BodyType<OtpRequest>,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<MessageResponse>(
    {
      url: `/api/auth/otp`,
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      data: otpRequest,
      signal,
    },
    options
  )
}

export const getRequestOtpMutationOptions = <
  TError = ErrorType<void>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof requestOtp>>,
    TError,
    { data: BodyType<OtpRequest> },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof requestOtp>>,
  TError,
  { data: BodyType<OtpRequest> },
  TContext
> => {
  const mutationKey = ['requestOtp']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof requestOtp>>,
    { data: BodyType<OtpRequest> }
  > = (props) => {
    const { data } = props ?? {}

    return requestOtp(data, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type RequestOtpMutationResult = NonNullable<Awaited<ReturnType<typeof requestOtp>>>
export type RequestOtpMutationBody = BodyType<OtpRequest>
export type RequestOtpMutationError = ErrorType<void>

/**
 * @summary Request OTP
 */
export const useRequestOtp = <TError = ErrorType<void>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof requestOtp>>,
      TError,
      { data: BodyType<OtpRequest> },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof requestOtp>>,
  TError,
  { data: BodyType<OtpRequest> },
  TContext
> => {
  return useMutation(getRequestOtpMutationOptions(options), queryClient)
}
/**
 * Verify OTP code and authenticate
 * @summary Verify OTP
 */
export const verifyOtp = (
  verifyOtpRequest: BodyType<VerifyOtpRequest>,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<AuthResponse>(
    {
      url: `/api/auth/otp/verify`,
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      data: verifyOtpRequest,
      signal,
    },
    options
  )
}

export const getVerifyOtpMutationOptions = <
  TError = ErrorType<ErrorResponse>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof verifyOtp>>,
    TError,
    { data: BodyType<VerifyOtpRequest> },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof verifyOtp>>,
  TError,
  { data: BodyType<VerifyOtpRequest> },
  TContext
> => {
  const mutationKey = ['verifyOtp']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof verifyOtp>>,
    { data: BodyType<VerifyOtpRequest> }
  > = (props) => {
    const { data } = props ?? {}

    return verifyOtp(data, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type VerifyOtpMutationResult = NonNullable<Awaited<ReturnType<typeof verifyOtp>>>
export type VerifyOtpMutationBody = BodyType<VerifyOtpRequest>
export type VerifyOtpMutationError = ErrorType<ErrorResponse>

/**
 * @summary Verify OTP
 */
export const useVerifyOtp = <TError = ErrorType<ErrorResponse>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof verifyOtp>>,
      TError,
      { data: BodyType<VerifyOtpRequest> },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof verifyOtp>>,
  TError,
  { data: BodyType<VerifyOtpRequest> },
  TContext
> => {
  return useMutation(getVerifyOtpMutationOptions(options), queryClient)
}
/**
 * Get a new access token using the refresh token cookie
 * @summary Refresh access token
 */
export const refresh = (options?: SecondParameter<typeof axiosMutator>, signal?: AbortSignal) => {
  return axiosMutator<AuthResponse>({ url: `/api/auth/refresh`, method: 'POST', signal }, options)
}

export const getRefreshMutationOptions = <
  TError = ErrorType<ErrorResponse>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<Awaited<ReturnType<typeof refresh>>, TError, void, TContext>
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<Awaited<ReturnType<typeof refresh>>, TError, void, TContext> => {
  const mutationKey = ['refresh']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<Awaited<ReturnType<typeof refresh>>, void> = () => {
    return refresh(requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type RefreshMutationResult = NonNullable<Awaited<ReturnType<typeof refresh>>>

export type RefreshMutationError = ErrorType<ErrorResponse>

/**
 * @summary Refresh access token
 */
export const useRefresh = <TError = ErrorType<ErrorResponse>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<Awaited<ReturnType<typeof refresh>>, TError, void, TContext>
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<Awaited<ReturnType<typeof refresh>>, TError, void, TContext> => {
  return useMutation(getRefreshMutationOptions(options), queryClient)
}
/**
 * Register a new user. A verification email will be sent.
 * @summary Register new user
 */
export const register = (
  registerRequest: BodyType<RegisterRequest>,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<MessageResponse>(
    {
      url: `/api/auth/register`,
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      data: registerRequest,
      signal,
    },
    options
  )
}

export const getRegisterMutationOptions = <
  TError = ErrorType<ErrorResponse>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof register>>,
    TError,
    { data: BodyType<RegisterRequest> },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof register>>,
  TError,
  { data: BodyType<RegisterRequest> },
  TContext
> => {
  const mutationKey = ['register']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof register>>,
    { data: BodyType<RegisterRequest> }
  > = (props) => {
    const { data } = props ?? {}

    return register(data, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type RegisterMutationResult = NonNullable<Awaited<ReturnType<typeof register>>>
export type RegisterMutationBody = BodyType<RegisterRequest>
export type RegisterMutationError = ErrorType<ErrorResponse>

/**
 * @summary Register new user
 */
export const useRegister = <TError = ErrorType<ErrorResponse>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof register>>,
      TError,
      { data: BodyType<RegisterRequest> },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof register>>,
  TError,
  { data: BodyType<RegisterRequest> },
  TContext
> => {
  return useMutation(getRegisterMutationOptions(options), queryClient)
}
/**
 * Verify the reset token and set a new password
 * @summary Reset password
 */
export const resetPassword = (
  resetPasswordRequest: BodyType<ResetPasswordRequest>,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<AuthResponse>(
    {
      url: `/api/auth/reset-password`,
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      data: resetPasswordRequest,
      signal,
    },
    options
  )
}

export const getResetPasswordMutationOptions = <
  TError = ErrorType<ErrorResponse>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof resetPassword>>,
    TError,
    { data: BodyType<ResetPasswordRequest> },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof resetPassword>>,
  TError,
  { data: BodyType<ResetPasswordRequest> },
  TContext
> => {
  const mutationKey = ['resetPassword']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof resetPassword>>,
    { data: BodyType<ResetPasswordRequest> }
  > = (props) => {
    const { data } = props ?? {}

    return resetPassword(data, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type ResetPasswordMutationResult = NonNullable<Awaited<ReturnType<typeof resetPassword>>>
export type ResetPasswordMutationBody = BodyType<ResetPasswordRequest>
export type ResetPasswordMutationError = ErrorType<ErrorResponse>

/**
 * @summary Reset password
 */
export const useResetPassword = <TError = ErrorType<ErrorResponse>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof resetPassword>>,
      TError,
      { data: BodyType<ResetPasswordRequest> },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof resetPassword>>,
  TError,
  { data: BodyType<ResetPasswordRequest> },
  TContext
> => {
  return useMutation(getResetPasswordMutationOptions(options), queryClient)
}
/**
 * Verify email address and complete registration
 * @summary Verify email
 */
export const verifyEmail = (
  verifyTokenRequest: BodyType<VerifyTokenRequest>,
  options?: SecondParameter<typeof axiosMutator>,
  signal?: AbortSignal
) => {
  return axiosMutator<AuthResponse>(
    {
      url: `/api/auth/verify-email`,
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      data: verifyTokenRequest,
      signal,
    },
    options
  )
}

export const getVerifyEmailMutationOptions = <
  TError = ErrorType<ErrorResponse>,
  TContext = unknown,
>(options?: {
  mutation?: UseMutationOptions<
    Awaited<ReturnType<typeof verifyEmail>>,
    TError,
    { data: BodyType<VerifyTokenRequest> },
    TContext
  >
  request?: SecondParameter<typeof axiosMutator>
}): UseMutationOptions<
  Awaited<ReturnType<typeof verifyEmail>>,
  TError,
  { data: BodyType<VerifyTokenRequest> },
  TContext
> => {
  const mutationKey = ['verifyEmail']
  const { mutation: mutationOptions, request: requestOptions } = options
    ? options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey
      ? options
      : { ...options, mutation: { ...options.mutation, mutationKey } }
    : { mutation: { mutationKey }, request: undefined }

  const mutationFn: MutationFunction<
    Awaited<ReturnType<typeof verifyEmail>>,
    { data: BodyType<VerifyTokenRequest> }
  > = (props) => {
    const { data } = props ?? {}

    return verifyEmail(data, requestOptions)
  }

  return { mutationFn, ...mutationOptions }
}

export type VerifyEmailMutationResult = NonNullable<Awaited<ReturnType<typeof verifyEmail>>>
export type VerifyEmailMutationBody = BodyType<VerifyTokenRequest>
export type VerifyEmailMutationError = ErrorType<ErrorResponse>

/**
 * @summary Verify email
 */
export const useVerifyEmail = <TError = ErrorType<ErrorResponse>, TContext = unknown>(
  options?: {
    mutation?: UseMutationOptions<
      Awaited<ReturnType<typeof verifyEmail>>,
      TError,
      { data: BodyType<VerifyTokenRequest> },
      TContext
    >
    request?: SecondParameter<typeof axiosMutator>
  },
  queryClient?: QueryClient
): UseMutationResult<
  Awaited<ReturnType<typeof verifyEmail>>,
  TError,
  { data: BodyType<VerifyTokenRequest> },
  TContext
> => {
  return useMutation(getVerifyEmailMutationOptions(options), queryClient)
}
