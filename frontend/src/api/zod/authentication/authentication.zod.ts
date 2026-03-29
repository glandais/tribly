import * as zod from 'zod'

/**
 * Logout and invalidate the refresh token
 * @summary Logout
 */
export const LogoutHeader = zod.object({
  'X-Refresh-Token': zod.string().optional(),
})

/**
 * Send a 6-digit OTP code to the user's email for passwordless login
 * @summary Request OTP
 */
export const requestOtpBodyEmailMax = 250

export const requestOtpBodyEmailRegExp = new RegExp('\\S')

export const RequestOtpBody = zod
  .object({
    email: zod
      .string()
      .max(requestOtpBodyEmailMax)
      .regex(requestOtpBodyEmailRegExp)
      .describe('Email address'),
  })
  .describe('OTP request')

export const RequestOtpResponse = zod
  .object({
    message: zod.string().optional().describe('Response message'),
  })
  .describe('Simple message response')

/**
 * Verify OTP code and authenticate
 * @summary Verify OTP
 */
export const VerifyOtpHeader = zod.object({
  'X-Forwarded-For': zod.string().optional(),
  'X-Real-IP': zod.string().optional(),
})

export const verifyOtpBodyEmailMax = 250

export const verifyOtpBodyEmailRegExp = new RegExp('\\S')
export const verifyOtpBodyCodeRegExp = new RegExp('^\\d{6}$')

export const VerifyOtpBody = zod
  .object({
    email: zod
      .string()
      .max(verifyOtpBodyEmailMax)
      .regex(verifyOtpBodyEmailRegExp)
      .describe('Email address'),
    code: zod.string().regex(verifyOtpBodyCodeRegExp).describe('6-digit OTP code'),
  })
  .describe('OTP verification request')

export const VerifyOtpResponse = zod
  .object({
    accessToken: zod.string().optional().describe('JWT access token'),
    expiresIn: zod.number().optional().describe('Token expiry in seconds'),
    user: zod
      .object({
        id: zod.string().describe('User ID (TSID)'),
        email: zod.string().describe('User email address'),
        displayName: zod.string().describe('User display name'),
        avatarUrl: zod.string().optional().describe('User avatar URL'),
        createdAt: zod.iso.datetime({}).optional().describe('Account creation timestamp'),
        unitSystem: zod
          .enum(['METRIC', 'IMPERIAL'])
          .optional()
          .describe('Preferred unit system (metric or imperial)'),
        platformRole: zod
          .enum(['PLATFORM_ADMIN'])
          .optional()
          .describe('Platform role (null if regular user)'),
        connectedServices: zod
          .array(
            zod
              .object({
                serviceType: zod.enum(['HAMMERHEAD', 'GARMIN']).describe('Service type identifier'),
                displayName: zod.string().describe('Display name of the service'),
                connectedAt: zod.iso.datetime({}).describe('When the service was connected'),
              })
              .describe('GPS service connection information')
          )
          .optional()
          .describe('Connected GPS services'),
      })
      .optional()
      .describe('Authenticated user'),
    refreshToken: zod.string().optional().describe('Refresh token (for mobile clients)'),
  })
  .describe('Authentication response')

/**
 * Get a new access token using the refresh token cookie
 * @summary Refresh access token
 */
export const RefreshHeader = zod.object({
  'X-Forwarded-For': zod.string().optional(),
  'X-Real-IP': zod.string().optional(),
  'X-Refresh-Token': zod.string().optional(),
})

export const RefreshResponse = zod
  .object({
    accessToken: zod.string().optional().describe('JWT access token'),
    expiresIn: zod.number().optional().describe('Token expiry in seconds'),
    user: zod
      .object({
        id: zod.string().describe('User ID (TSID)'),
        email: zod.string().describe('User email address'),
        displayName: zod.string().describe('User display name'),
        avatarUrl: zod.string().optional().describe('User avatar URL'),
        createdAt: zod.iso.datetime({}).optional().describe('Account creation timestamp'),
        unitSystem: zod
          .enum(['METRIC', 'IMPERIAL'])
          .optional()
          .describe('Preferred unit system (metric or imperial)'),
        platformRole: zod
          .enum(['PLATFORM_ADMIN'])
          .optional()
          .describe('Platform role (null if regular user)'),
        connectedServices: zod
          .array(
            zod
              .object({
                serviceType: zod.enum(['HAMMERHEAD', 'GARMIN']).describe('Service type identifier'),
                displayName: zod.string().describe('Display name of the service'),
                connectedAt: zod.iso.datetime({}).describe('When the service was connected'),
              })
              .describe('GPS service connection information')
          )
          .optional()
          .describe('Connected GPS services'),
      })
      .optional()
      .describe('Authenticated user'),
    refreshToken: zod.string().optional().describe('Refresh token (for mobile clients)'),
  })
  .describe('Authentication response')

/**
 * Register a new user. A verification email will be sent.
 * @summary Register new user
 */
export const registerBodyEmailMax = 250

export const registerBodyEmailRegExp = new RegExp('\\S')
export const registerBodyDisplayNameMax = 200

export const registerBodyDisplayNameRegExp = new RegExp('\\S')

export const RegisterBody = zod
  .object({
    email: zod
      .string()
      .max(registerBodyEmailMax)
      .regex(registerBodyEmailRegExp)
      .describe('Email address'),
    displayName: zod
      .string()
      .min(1)
      .max(registerBodyDisplayNameMax)
      .regex(registerBodyDisplayNameRegExp)
      .describe('Display name'),
  })
  .describe('User registration request')

export const RegisterResponse = zod
  .object({
    message: zod.string().optional().describe('Response message'),
  })
  .describe('Simple message response')

/**
 * Verify email address and complete registration
 * @summary Verify email
 */
export const VerifyEmailHeader = zod.object({
  'X-Forwarded-For': zod.string().optional(),
  'X-Real-IP': zod.string().optional(),
})

export const verifyEmailBodyTokenMax = 100

export const verifyEmailBodyTokenRegExp = new RegExp('\\S')

export const VerifyEmailBody = zod
  .object({
    token: zod
      .string()
      .max(verifyEmailBodyTokenMax)
      .regex(verifyEmailBodyTokenRegExp)
      .describe('Verification token'),
  })
  .describe('Token verification request')

export const VerifyEmailResponse = zod
  .object({
    accessToken: zod.string().optional().describe('JWT access token'),
    expiresIn: zod.number().optional().describe('Token expiry in seconds'),
    user: zod
      .object({
        id: zod.string().describe('User ID (TSID)'),
        email: zod.string().describe('User email address'),
        displayName: zod.string().describe('User display name'),
        avatarUrl: zod.string().optional().describe('User avatar URL'),
        createdAt: zod.iso.datetime({}).optional().describe('Account creation timestamp'),
        unitSystem: zod
          .enum(['METRIC', 'IMPERIAL'])
          .optional()
          .describe('Preferred unit system (metric or imperial)'),
        platformRole: zod
          .enum(['PLATFORM_ADMIN'])
          .optional()
          .describe('Platform role (null if regular user)'),
        connectedServices: zod
          .array(
            zod
              .object({
                serviceType: zod.enum(['HAMMERHEAD', 'GARMIN']).describe('Service type identifier'),
                displayName: zod.string().describe('Display name of the service'),
                connectedAt: zod.iso.datetime({}).describe('When the service was connected'),
              })
              .describe('GPS service connection information')
          )
          .optional()
          .describe('Connected GPS services'),
      })
      .optional()
      .describe('Authenticated user'),
    refreshToken: zod.string().optional().describe('Refresh token (for mobile clients)'),
  })
  .describe('Authentication response')
