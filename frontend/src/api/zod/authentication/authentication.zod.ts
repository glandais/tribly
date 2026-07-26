import * as zod from 'zod'

/**
 * Set/change the account's real email (e.g. recover a migrated Strava account). Sends a verification link to the new address.
 * @summary Request email change
 */
export const requestEmailChangeBodyEmailMax = 250

export const requestEmailChangeBodyEmailRegExp = new RegExp('\\S')

export const RequestEmailChangeBody = zod
  .object({
    email: zod
      .string()
      .max(requestEmailChangeBodyEmailMax)
      .regex(requestEmailChangeBodyEmailRegExp)
      .describe('New email address'),
  })
  .describe("Request to set\/change the account's real email address")

export const RequestEmailChangeResponse = zod
  .object({
    message: zod.string().optional().describe('Response message'),
  })
  .describe('Simple message response')

/**
 * Send a 6-digit code to the user's email to reset their password
 * @summary Request password reset
 */
export const forgotPasswordBodyEmailMax = 250

export const forgotPasswordBodyEmailRegExp = new RegExp('\\S')

export const ForgotPasswordBody = zod
  .object({
    email: zod
      .string()
      .max(forgotPasswordBodyEmailMax)
      .regex(forgotPasswordBodyEmailRegExp)
      .describe('Email address'),
  })
  .describe('Forgot password request')

export const ForgotPasswordResponse = zod
  .object({
    message: zod.string().optional().describe('Response message'),
  })
  .describe('Simple message response')

/**
 * Authenticate using email and password
 * @summary Login with password
 */
export const LoginWithPasswordHeader = zod.object({
  'X-Forwarded-For': zod.string().optional(),
  'X-Real-IP': zod.string().optional(),
})

export const loginWithPasswordBodyEmailMax = 250

export const loginWithPasswordBodyEmailRegExp = new RegExp('\\S')
export const loginWithPasswordBodyPasswordMax = 100

export const loginWithPasswordBodyPasswordRegExp = new RegExp('\\S')

export const LoginWithPasswordBody = zod
  .object({
    email: zod
      .string()
      .max(loginWithPasswordBodyEmailMax)
      .regex(loginWithPasswordBodyEmailRegExp)
      .describe('Email address'),
    password: zod
      .string()
      .max(loginWithPasswordBodyPasswordMax)
      .regex(loginWithPasswordBodyPasswordRegExp)
      .describe('Password'),
  })
  .describe('Password login request')

export const LoginWithPasswordResponse = zod
  .object({
    accessToken: zod.string().optional().describe('JWT access token'),
    expiresIn: zod.number().optional().describe('Token expiry in seconds'),
    user: zod
      .object({
        id: zod.string().describe('User ID (TSID)'),
        email: zod.string().describe('User email address'),
        displayName: zod.string().describe('User display name'),
        avatarUrl: zod.string().optional().describe('User avatar URL'),
        createdAt: zod.iso
          .datetime({ offset: true })
          .optional()
          .describe('Account creation timestamp'),
        unitSystem: zod
          .enum(['METRIC', 'IMPERIAL'])
          .optional()
          .describe('Preferred unit system (metric or imperial)'),
        theme: zod
          .enum(['SYSTEM', 'LIGHT', 'DARK'])
          .optional()
          .describe(
            'Preferred colour scheme. Null means the user never chose one — distinct from SYSTEM, which they did choose — so a client is free to follow the device.'
          ),
        language: zod
          .string()
          .optional()
          .describe(
            'Preferred language as a BCP-47 tag. Null means the user never chose one; the client then follows the device or the domain.'
          ),
        platformRole: zod
          .enum(['PLATFORM_ADMIN'])
          .optional()
          .describe('Platform role (null if regular user)'),
        emailVerified: zod.boolean().describe("Whether the account's email has been verified"),
        requiresEmail: zod
          .boolean()
          .describe(
            'True when the account still needs a real, verified email (e.g. a migrated Strava account with a placeholder address)'
          ),
        connectedServices: zod
          .array(
            zod
              .object({
                serviceType: zod.enum(['HAMMERHEAD', 'GARMIN']).describe('Service type identifier'),
                displayName: zod.string().describe('Display name of the service'),
                connectedAt: zod.iso
                  .datetime({ offset: true })
                  .describe('When the service was connected'),
              })
              .describe('GPS service connection information')
          )
          .optional()
          .describe('Connected GPS services'),
        socialIdentities: zod
          .array(
            zod
              .object({
                provider: zod.enum(['STRAVA']).describe('Provider identifier'),
                displayName: zod.string().describe('Display name of the provider'),
                linkedAt: zod.iso
                  .datetime({ offset: true })
                  .describe('When the identity was linked'),
              })
              .describe('A linked external identity (e.g. Strava)')
          )
          .optional()
          .describe('Linked external identities (e.g. Strava)'),
      })
      .optional()
      .describe('Authenticated user'),
    refreshToken: zod.string().optional().describe('Refresh token (for mobile clients)'),
  })
  .describe('Authentication response')

/**
 * Logout and invalidate the refresh token
 * @summary Logout
 */
export const LogoutHeader = zod.object({
  'X-Refresh-Token': zod.string().optional(),
})

export const LogoutResponse = zod.void()

/**
 * Logout from all devices by invalidating all refresh tokens
 * @summary Logout all sessions
 */
export const LogoutAllResponse = zod.void()

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
        createdAt: zod.iso
          .datetime({ offset: true })
          .optional()
          .describe('Account creation timestamp'),
        unitSystem: zod
          .enum(['METRIC', 'IMPERIAL'])
          .optional()
          .describe('Preferred unit system (metric or imperial)'),
        theme: zod
          .enum(['SYSTEM', 'LIGHT', 'DARK'])
          .optional()
          .describe(
            'Preferred colour scheme. Null means the user never chose one — distinct from SYSTEM, which they did choose — so a client is free to follow the device.'
          ),
        language: zod
          .string()
          .optional()
          .describe(
            'Preferred language as a BCP-47 tag. Null means the user never chose one; the client then follows the device or the domain.'
          ),
        platformRole: zod
          .enum(['PLATFORM_ADMIN'])
          .optional()
          .describe('Platform role (null if regular user)'),
        emailVerified: zod.boolean().describe("Whether the account's email has been verified"),
        requiresEmail: zod
          .boolean()
          .describe(
            'True when the account still needs a real, verified email (e.g. a migrated Strava account with a placeholder address)'
          ),
        connectedServices: zod
          .array(
            zod
              .object({
                serviceType: zod.enum(['HAMMERHEAD', 'GARMIN']).describe('Service type identifier'),
                displayName: zod.string().describe('Display name of the service'),
                connectedAt: zod.iso
                  .datetime({ offset: true })
                  .describe('When the service was connected'),
              })
              .describe('GPS service connection information')
          )
          .optional()
          .describe('Connected GPS services'),
        socialIdentities: zod
          .array(
            zod
              .object({
                provider: zod.enum(['STRAVA']).describe('Provider identifier'),
                displayName: zod.string().describe('Display name of the provider'),
                linkedAt: zod.iso
                  .datetime({ offset: true })
                  .describe('When the identity was linked'),
              })
              .describe('A linked external identity (e.g. Strava)')
          )
          .optional()
          .describe('Linked external identities (e.g. Strava)'),
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
        createdAt: zod.iso
          .datetime({ offset: true })
          .optional()
          .describe('Account creation timestamp'),
        unitSystem: zod
          .enum(['METRIC', 'IMPERIAL'])
          .optional()
          .describe('Preferred unit system (metric or imperial)'),
        theme: zod
          .enum(['SYSTEM', 'LIGHT', 'DARK'])
          .optional()
          .describe(
            'Preferred colour scheme. Null means the user never chose one — distinct from SYSTEM, which they did choose — so a client is free to follow the device.'
          ),
        language: zod
          .string()
          .optional()
          .describe(
            'Preferred language as a BCP-47 tag. Null means the user never chose one; the client then follows the device or the domain.'
          ),
        platformRole: zod
          .enum(['PLATFORM_ADMIN'])
          .optional()
          .describe('Platform role (null if regular user)'),
        emailVerified: zod.boolean().describe("Whether the account's email has been verified"),
        requiresEmail: zod
          .boolean()
          .describe(
            'True when the account still needs a real, verified email (e.g. a migrated Strava account with a placeholder address)'
          ),
        connectedServices: zod
          .array(
            zod
              .object({
                serviceType: zod.enum(['HAMMERHEAD', 'GARMIN']).describe('Service type identifier'),
                displayName: zod.string().describe('Display name of the service'),
                connectedAt: zod.iso
                  .datetime({ offset: true })
                  .describe('When the service was connected'),
              })
              .describe('GPS service connection information')
          )
          .optional()
          .describe('Connected GPS services'),
        socialIdentities: zod
          .array(
            zod
              .object({
                provider: zod.enum(['STRAVA']).describe('Provider identifier'),
                displayName: zod.string().describe('Display name of the provider'),
                linkedAt: zod.iso
                  .datetime({ offset: true })
                  .describe('When the identity was linked'),
              })
              .describe('A linked external identity (e.g. Strava)')
          )
          .optional()
          .describe('Linked external identities (e.g. Strava)'),
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
export const registerBodyPasswordMin = 8
export const registerBodyPasswordMax = 100

export const registerBodyPasswordRegExp = new RegExp('\\S')

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
    password: zod
      .string()
      .min(registerBodyPasswordMin)
      .max(registerBodyPasswordMax)
      .regex(registerBodyPasswordRegExp)
      .describe('Password (min 8 chars)'),
  })
  .describe('User registration request')

export const RegisterResponse = zod
  .object({
    message: zod.string().optional().describe('Response message'),
  })
  .describe('Simple message response')

/**
 * Verify the reset token and set a new password
 * @summary Reset password
 */
export const ResetPasswordHeader = zod.object({
  'X-Forwarded-For': zod.string().optional(),
  'X-Real-IP': zod.string().optional(),
})

export const resetPasswordBodyTokenMax = 100

export const resetPasswordBodyTokenRegExp = new RegExp('\\S')
export const resetPasswordBodyNewPasswordMin = 8
export const resetPasswordBodyNewPasswordMax = 100

export const resetPasswordBodyNewPasswordRegExp = new RegExp('\\S')

export const ResetPasswordBody = zod
  .object({
    token: zod
      .string()
      .max(resetPasswordBodyTokenMax)
      .regex(resetPasswordBodyTokenRegExp)
      .describe('Password reset token'),
    newPassword: zod
      .string()
      .min(resetPasswordBodyNewPasswordMin)
      .max(resetPasswordBodyNewPasswordMax)
      .regex(resetPasswordBodyNewPasswordRegExp)
      .describe('New password (min 8 chars)'),
  })
  .describe('Reset password request')

export const ResetPasswordResponse = zod
  .object({
    accessToken: zod.string().optional().describe('JWT access token'),
    expiresIn: zod.number().optional().describe('Token expiry in seconds'),
    user: zod
      .object({
        id: zod.string().describe('User ID (TSID)'),
        email: zod.string().describe('User email address'),
        displayName: zod.string().describe('User display name'),
        avatarUrl: zod.string().optional().describe('User avatar URL'),
        createdAt: zod.iso
          .datetime({ offset: true })
          .optional()
          .describe('Account creation timestamp'),
        unitSystem: zod
          .enum(['METRIC', 'IMPERIAL'])
          .optional()
          .describe('Preferred unit system (metric or imperial)'),
        theme: zod
          .enum(['SYSTEM', 'LIGHT', 'DARK'])
          .optional()
          .describe(
            'Preferred colour scheme. Null means the user never chose one — distinct from SYSTEM, which they did choose — so a client is free to follow the device.'
          ),
        language: zod
          .string()
          .optional()
          .describe(
            'Preferred language as a BCP-47 tag. Null means the user never chose one; the client then follows the device or the domain.'
          ),
        platformRole: zod
          .enum(['PLATFORM_ADMIN'])
          .optional()
          .describe('Platform role (null if regular user)'),
        emailVerified: zod.boolean().describe("Whether the account's email has been verified"),
        requiresEmail: zod
          .boolean()
          .describe(
            'True when the account still needs a real, verified email (e.g. a migrated Strava account with a placeholder address)'
          ),
        connectedServices: zod
          .array(
            zod
              .object({
                serviceType: zod.enum(['HAMMERHEAD', 'GARMIN']).describe('Service type identifier'),
                displayName: zod.string().describe('Display name of the service'),
                connectedAt: zod.iso
                  .datetime({ offset: true })
                  .describe('When the service was connected'),
              })
              .describe('GPS service connection information')
          )
          .optional()
          .describe('Connected GPS services'),
        socialIdentities: zod
          .array(
            zod
              .object({
                provider: zod.enum(['STRAVA']).describe('Provider identifier'),
                displayName: zod.string().describe('Display name of the provider'),
                linkedAt: zod.iso
                  .datetime({ offset: true })
                  .describe('When the identity was linked'),
              })
              .describe('A linked external identity (e.g. Strava)')
          )
          .optional()
          .describe('Linked external identities (e.g. Strava)'),
      })
      .optional()
      .describe('Authenticated user'),
    refreshToken: zod.string().optional().describe('Refresh token (for mobile clients)'),
  })
  .describe('Authentication response')

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
        createdAt: zod.iso
          .datetime({ offset: true })
          .optional()
          .describe('Account creation timestamp'),
        unitSystem: zod
          .enum(['METRIC', 'IMPERIAL'])
          .optional()
          .describe('Preferred unit system (metric or imperial)'),
        theme: zod
          .enum(['SYSTEM', 'LIGHT', 'DARK'])
          .optional()
          .describe(
            'Preferred colour scheme. Null means the user never chose one — distinct from SYSTEM, which they did choose — so a client is free to follow the device.'
          ),
        language: zod
          .string()
          .optional()
          .describe(
            'Preferred language as a BCP-47 tag. Null means the user never chose one; the client then follows the device or the domain.'
          ),
        platformRole: zod
          .enum(['PLATFORM_ADMIN'])
          .optional()
          .describe('Platform role (null if regular user)'),
        emailVerified: zod.boolean().describe("Whether the account's email has been verified"),
        requiresEmail: zod
          .boolean()
          .describe(
            'True when the account still needs a real, verified email (e.g. a migrated Strava account with a placeholder address)'
          ),
        connectedServices: zod
          .array(
            zod
              .object({
                serviceType: zod.enum(['HAMMERHEAD', 'GARMIN']).describe('Service type identifier'),
                displayName: zod.string().describe('Display name of the service'),
                connectedAt: zod.iso
                  .datetime({ offset: true })
                  .describe('When the service was connected'),
              })
              .describe('GPS service connection information')
          )
          .optional()
          .describe('Connected GPS services'),
        socialIdentities: zod
          .array(
            zod
              .object({
                provider: zod.enum(['STRAVA']).describe('Provider identifier'),
                displayName: zod.string().describe('Display name of the provider'),
                linkedAt: zod.iso
                  .datetime({ offset: true })
                  .describe('When the identity was linked'),
              })
              .describe('A linked external identity (e.g. Strava)')
          )
          .optional()
          .describe('Linked external identities (e.g. Strava)'),
      })
      .optional()
      .describe('Authenticated user'),
    refreshToken: zod.string().optional().describe('Refresh token (for mobile clients)'),
  })
  .describe('Authentication response')
