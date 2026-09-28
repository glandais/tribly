import type { Page } from '@playwright/test'
import type {
  DeviceCodeResponse,
  DeviceRequest,
  DeviceTokenRequest,
  DeviceTokenResponse,
  DeviceUserStatusResponse,
  UserDto,
  VerifyResponse,
} from '../../src/api/dto'
import { ApiError, apiGet, apiGetOrNull, apiPost } from './api'

/**
 * The device side of the GPS pairing (RFC 8628, DeviceOAuthResource): what the Garmin and Karoo
 * apps call, from Node, while the browser plays the rider who opens the verification page.
 */

const DEVICE_CODE_GRANT: DeviceTokenRequest['grantType'] =
  'urn:ietf:params:oauth:grant-type:device_code'

/** What the device asks for first: the code it polls with, and the one it shows the rider. */
export const startDeviceFlow = (clientId: string) =>
  apiPost<DeviceCodeResponse>(undefined, '/api/device/oauth/device', {
    clientId,
  } satisfies DeviceRequest)

/**
 * One poll of `/token` with the device code: the tokens once the rider authorized it, or the
 * ApiError the backend answers meanwhile (`AUTHORIZATION_PENDING`, then `TOKEN_INVALID` once the code
 * was exchanged — it is single-use).
 */
export const pollToken = (deviceCode: string) =>
  apiPost<DeviceTokenResponse>(undefined, '/api/device/oauth/token', {
    grantType: DEVICE_CODE_GRANT,
    deviceCode,
  } satisfies DeviceTokenRequest)

/** The device's refresh: a new access token for the session the pairing opened. */
export const refreshDeviceToken = (refreshToken: string) =>
  apiPost<DeviceTokenResponse>(undefined, '/api/device/oauth/token', {
    grantType: 'refresh_token',
    refreshToken,
  } satisfies DeviceTokenRequest)

/** The error code `/token` answers with, or null when it delivered tokens. */
export async function pollError(deviceCode: string): Promise<string | null> {
  try {
    await pollToken(deviceCode)
    return null
  } catch (error) {
    if (error instanceof ApiError) return error.code ?? `HTTP ${error.status}`
    throw error
  }
}

/** What the verification page asks the backend about a user code: null when unknown or expired. */
export const verifyUserCode = (userCode: string) =>
  apiGetOrNull<VerifyResponse>(undefined, '/api/device/oauth/verify', { code: userCode })

/** GET /api/device/me with the device's access token — what the apps call once paired. */
export const deviceMe = (accessToken: string) =>
  apiGet<DeviceUserStatusResponse>({ accessToken }, '/api/device/me')

/** GET /api/users/me with the device's access token: whose account the device was paired with. */
export const deviceOwner = (accessToken: string) =>
  apiGet<UserDto>({ accessToken }, '/api/users/me')

/** The claims of a JWT (no signature check: the backend accepting it is the check). */
export function jwtClaims(token: string): Record<string, unknown> {
  const payload = token.split('.')[1]
  return JSON.parse(Buffer.from(payload, 'base64url').toString('utf8')) as Record<string, unknown>
}

/** A user code the backend can never issue: 0, 1, I and O are left out of its alphabet. */
export const NEVER_ISSUED_CODE = '0I1O0I'

/** Counts the page's POST /api/device/oauth/complete — call it before the first `goto`. */
export function countCompletions(page: Page): () => number {
  let count = 0
  page.on('request', (request) => {
    if (request.method() === 'POST' && request.url().endsWith('/api/device/oauth/complete'))
      count += 1
  })
  return () => count
}

/** The verification page's card headings, as the rider reads them. */
export const DEVICE_PAGE = {
  success: 'Connexion réussie !',
  error: 'Erreur',
  manualEntry: 'Entrez le code',
} as const
