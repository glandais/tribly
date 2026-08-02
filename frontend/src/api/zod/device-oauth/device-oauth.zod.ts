import * as zod from 'zod'

/**
 * Called by frontend after user authenticates via OTP
 * @summary Complete device authorization
 */
export const DeviceCompleteBody = zod
  .object({
    userCode: zod.string().describe('User code from device display'),
  })
  .describe('Complete device authorization request')

export const DeviceCompleteResponse = zod.unknown()

/**
 * Start device code flow - returns user code and verification URL
 * @summary Request device code
 */
export const DeviceBody = zod
  .object({
    clientId: zod.string().optional().describe("Client ID (e.g., 'karoo', 'garmin')"),
  })
  .describe('Device auth request')

export const DeviceResponse = zod
  .object({
    deviceCode: zod.string().describe('Device code for polling'),
    userCode: zod.string().describe("User code to display (e.g., 'ABCD12')"),
    verificationUri: zod.string().describe('Verification URL for user to visit'),
    verificationUriComplete: zod.string().describe('Verification URL with user code embedded'),
    expiresIn: zod.int().describe('Code expiry in seconds'),
    interval: zod.int().describe('Minimum polling interval in seconds'),
  })
  .describe('Device code response (RFC 8628)')

/**
 * Exchange device code or refresh token for access tokens. Returns 'authorization_pending' error while waiting for user.
 * @summary Exchange code for tokens
 */
export const deviceTokenBodyGrantTypeRegExp = new RegExp('\\S')

export const DeviceTokenBody = zod
  .object({
    grantType: zod
      .string()
      .regex(deviceTokenBodyGrantTypeRegExp)
      .describe("Grant type: 'urn:ietf:params:oauth:grant-type:device_code' or 'refresh_token'"),
    deviceCode: zod.string().optional().describe('Device code (for device_code grant)'),
    refreshToken: zod.string().optional().describe('Refresh token (for refresh_token grant)'),
  })
  .describe('Device OAuth token request')

export const DeviceTokenResponse = zod
  .object({
    accessToken: zod.string().describe('Access token'),
    tokenType: zod.string().describe("Token type (always 'Bearer')"),
    expiresIn: zod.int().describe('Token expiry in seconds'),
    refreshToken: zod.string().optional().describe('Refresh token'),
  })
  .describe('Device OAuth token response')

/**
 * Frontend uses this to verify user code before showing auth flow
 * @summary Check user code validity
 */
export const VerifyQueryParams = zod.object({
  code: zod.string().optional(),
})

export const VerifyResponse = zod
  .object({
    userCode: zod.string().describe('User code'),
    authorized: zod.boolean().optional().describe('Whether authorization is already completed'),
  })
  .describe('User code verification response')
