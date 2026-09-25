import { mkdirSync, renameSync, writeFileSync } from 'node:fs'
import { dirname } from 'node:path'
import { request, type APIRequestContext, type APIResponse } from '@playwright/test'
import { linkTokenIn, mailbox, otpCodeIn, waitForNewMail } from './mailhog'
import { stack } from './stack'

/**
 * Talks to the real REST API from Node, to log in and seed data without going through the UI.
 *
 * Logins go through the same endpoints as the app — OTP read from mailhog, e-mail verification link
 * read from mailhog — so there is no test-only backdoor in the backend.
 */

export interface AuthResponse {
  accessToken: string
  refreshToken: string
  user: { id: string; email: string; displayName: string }
}

/**
 * A request context with no cookie jar shared with anyone: the backend reads the refresh_token
 * cookie before the X-Refresh-Token header, so a jar that saw another user's login would refresh as
 * that user.
 */
export async function apiContext(accessToken?: string): Promise<APIRequestContext> {
  return request.newContext({
    baseURL: stack.baseURL,
    extraHTTPHeaders: accessToken ? { Authorization: `Bearer ${accessToken}` } : {},
  })
}

export class ApiError extends Error {
  constructor(
    readonly status: number,
    readonly code: string | undefined,
    message: string
  ) {
    super(message)
  }
}

export async function expectOk<T>(response: APIResponse): Promise<T> {
  if (response.ok()) {
    const text = await response.text()
    return (text ? JSON.parse(text) : undefined) as T
  }
  const body = await response.text()
  let code: string | undefined
  try {
    code = (JSON.parse(body) as { code?: string }).code
  } catch {
    // not JSON — keep the raw body in the message
  }
  throw new ApiError(response.status(), code, `${response.url()} → ${response.status()} ${body}`)
}

/** Whoever an API call is made as: a session, or nobody (an anonymous visitor). */
export type Caller = Pick<AuthResponse, 'accessToken'> | undefined

/** Runs `run` with a fresh request context signed in as `who`, and disposes of it afterwards. */
export async function withApi<T>(who: Caller, run: (api: APIRequestContext) => Promise<T>) {
  const api = await apiContext(who?.accessToken)
  try {
    return await run(api)
  } finally {
    await api.dispose()
  }
}

type Params = Record<string, string | number | boolean>

/** GET as `who`; the parsed body, or an ApiError. */
export const apiGet = <T>(who: Caller, path: string, params?: Params) =>
  withApi(who, async (api) => expectOk<T>(await api.get(path, { params })))

/** GET as `who`; the parsed body, or null when the API answers 404 (a deleted or unknown entity). */
export const apiGetOrNull = <T>(who: Caller, path: string, params?: Params) =>
  withApi(who, async (api) => {
    const response = await api.get(path, { params })
    return response.status() === 404 ? null : expectOk<T>(response)
  })

export const apiPost = <T>(who: Caller, path: string, data?: unknown) =>
  withApi(who, async (api) => expectOk<T>(await api.post(path, { data })))

export const apiPut = <T>(who: Caller, path: string, data: unknown) =>
  withApi(who, async (api) => expectOk<T>(await api.put(path, { data })))

export const apiPatch = <T>(who: Caller, path: string, data: unknown) =>
  withApi(who, async (api) => expectOk<T>(await api.patch(path, { data })))

export const apiDelete = (who: Caller, path: string) =>
  withApi(who, async (api) => expectOk(await api.delete(path)))

/** OTP login. Rate-limited to 3 requests per 5 minutes per address — don't call it per test. */
export async function loginWithOtp(email: string): Promise<AuthResponse> {
  const seen = await mailbox(email)
  await apiPost(undefined, '/api/auth/otp', { email })
  const code = otpCodeIn(await waitForNewMail(email, seen))
  return apiPost<AuthResponse>(undefined, '/api/auth/otp/verify', { email, code })
}

export const loginWithPassword = (email: string, password: string) =>
  apiPost<AuthResponse>(undefined, '/api/auth/login', { email, password })

/** Sign-up as the app does it: register, then follow the verification link from the mail. */
export async function register(account: {
  email: string
  displayName: string
  password: string
}): Promise<AuthResponse> {
  const seen = await mailbox(account.email)
  await apiPost(undefined, '/api/auth/register', { ...account, acceptTerms: true })
  const token = linkTokenIn(await waitForNewMail(account.email, seen))
  return apiPost<AuthResponse>(undefined, '/api/auth/verify-email', { token })
}

/**
 * A fresh access token for a saved session, or null once the session is gone (stack reset: the
 * backend answers 4xx). Any other failure throws — concurrent refreshes of one session used to
 * answer 500 (optimistic lock on User, fixed 2026-09-25) and auth.e2e.ts pins that they no longer do,
 * so there is nothing to retry.
 */
export async function refresh(refreshToken: string): Promise<AuthResponse | null> {
  return withApi(undefined, async (api) => {
    const response = await api.post('/api/auth/refresh', {
      headers: { 'X-Refresh-Token': refreshToken },
    })
    if (response.ok()) return { ...(await response.json()), refreshToken }
    if (response.status() < 500) return null
    throw new Error(`POST /api/auth/refresh → ${response.status()}: ${await response.text()}`)
  })
}

/**
 * Writes a Playwright storageState holding the session cookie, exactly as the backend sets it
 * (RefreshTokenCookieFactory): path=/, httpOnly, SameSite=Lax, Secure — Chromium sends Secure
 * cookies to http://localhost, which counts as a secure context.
 */
export function writeStorageState(path: string, refreshToken: string) {
  const { hostname } = new URL(stack.baseURL)
  writeFileAtomic(
    path,
    JSON.stringify(
      {
        cookies: [
          {
            name: 'refresh_token',
            value: refreshToken,
            domain: hostname,
            path: '/',
            expires: Math.floor(Date.now() / 1000) + 30 * 24 * 3600,
            httpOnly: true,
            secure: true,
            sameSite: 'Lax',
          },
        ],
        origins: [],
      },
      null,
      2
    )
  )
}

/**
 * Several Playwright runs may share one stack, each running global-setup: a reader must never see a
 * half-written session or seed file.
 */
export function writeFileAtomic(path: string, content: string) {
  mkdirSync(dirname(path), { recursive: true })
  const tmp = `${path}.${process.pid}.tmp`
  writeFileSync(tmp, content)
  renameSync(tmp, path)
}
