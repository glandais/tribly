import type { Page } from '@playwright/test'
import type {
  AdminGpsCredentialDto,
  CreateGpsCredentialRequest,
  DeviceCodeResponse,
  DeviceRequest,
  DeviceTokenRequest,
  DeviceTokenResponse,
  DeviceUserStatusResponse,
  UserDto,
  VerifyResponse,
} from '../../src/api/dto'
import { ApiError, apiGet, apiGetOrNull, apiPost } from './api'
import { roleSession } from './data'
import { adminDomain } from './platform-admin'

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
  return countPosts(page, '/api/device/oauth/complete')
}

/** Counts the page's POST /api/device/oauth/deny — call it before the first `goto`. */
export function countDenials(page: Page): () => number {
  return countPosts(page, '/api/device/oauth/deny')
}

function countPosts(page: Page, path: string): () => number {
  let count = 0
  page.on('request', (request) => {
    if (request.method() === 'POST' && request.url().endsWith(path)) count += 1
  })
  return () => count
}

/**
 * Hammerhead offered on `localhost`, as the platform admin configures it: what makes the Karoo page
 * chain its Hammerhead step (docs/LEDGER_*.md API-63). Get-or-create — it outlives a run like the
 * seed, and the desktop and mobile projects may race to add it. The client id is a dummy: no test
 * reaches Hammerhead, the browser's trip there is intercepted (see {@link HAMMERHEAD_AUTHORIZE}).
 */
export async function ensureHammerheadOffered(): Promise<void> {
  const admin = await roleSession('admin')
  const domain = await adminDomain('localhost')
  const path = `/api/admin/domains/${domain.id}/gps-credentials`
  const has = async () =>
    (await apiGet<AdminGpsCredentialDto[]>(admin, path)).some(
      (c) => c.serviceType === 'HAMMERHEAD' && c.active
    )
  if (await has()) return
  try {
    await apiPost<AdminGpsCredentialDto>(admin, path, {
      serviceType: 'HAMMERHEAD',
      clientId: 'e2e-hammerhead',
      clientSecret: 'e2e-hammerhead-secret',
      active: true,
    } satisfies CreateGpsCredentialRequest)
  } catch (error) {
    // The other project created it in between.
    if (!(await has())) throw error
  }
}

/** Hammerhead's authorization page (HammerheadClient.AUTH_URL), where « Associer » sends the rider. */
export const HAMMERHEAD_AUTHORIZE = 'https://api.hammerhead.io/v1/auth/oauth/authorize**'

/**
 * Plays Hammerhead for `page`: its authorization page answers at once by sending the browser back
 * to the `redirect_uri` it was given, with the `state` and `error=access_denied` — a rider who
 * refused. A successful exchange cannot be played (the backend would call Hammerhead itself).
 * Returns the authorization URLs the page was sent to.
 */
export async function refuseAtHammerhead(page: Page): Promise<URL[]> {
  const visits: URL[] = []
  await page.route(HAMMERHEAD_AUTHORIZE, async (route) => {
    const url = new URL(route.request().url())
    visits.push(url)
    const back = new URL(url.searchParams.get('redirect_uri')!)
    back.searchParams.set('state', url.searchParams.get('state')!)
    back.searchParams.set('error', 'access_denied')
    await route.fulfill({ status: 302, headers: { location: back.toString() } })
  })
  return visits
}

/** The verification page's card headings, as the rider reads them. */
export const DEVICE_PAGE = {
  confirm: 'Autoriser cet appareil ?',
  denied: 'Demande refusée',
  success: 'Connexion réussie !',
  hammerhead: 'Dernière étape : associer Hammerhead',
  ready: 'Votre Karoo est prêt',
  error: 'Erreur',
  manualEntry: 'Entrez le code',
} as const
