import { mkdirSync, writeFileSync } from 'node:fs'
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

/** OTP login. Rate-limited to 3 requests per 5 minutes per address — don't call it per test. */
export async function loginWithOtp(email: string): Promise<AuthResponse> {
  const api = await apiContext()
  try {
    const seen = await mailbox(email)
    await expectOk(await api.post('/api/auth/otp', { data: { email } }))
    const code = otpCodeIn(await waitForNewMail(email, seen))
    return await expectOk<AuthResponse>(
      await api.post('/api/auth/otp/verify', { data: { email, code } })
    )
  } finally {
    await api.dispose()
  }
}

export async function loginWithPassword(email: string, password: string): Promise<AuthResponse> {
  const api = await apiContext()
  try {
    return await expectOk<AuthResponse>(
      await api.post('/api/auth/login', { data: { email, password } })
    )
  } finally {
    await api.dispose()
  }
}

/** Sign-up as the app does it: register, then follow the verification link from the mail. */
export async function register(account: {
  email: string
  displayName: string
  password: string
}): Promise<AuthResponse> {
  const api = await apiContext()
  try {
    const seen = await mailbox(account.email)
    await expectOk(
      await api.post('/api/auth/register', { data: { ...account, acceptTerms: true } })
    )
    const token = linkTokenIn(await waitForNewMail(account.email, seen))
    return await expectOk<AuthResponse>(
      await api.post('/api/auth/verify-email', { data: { token } })
    )
  } finally {
    await api.dispose()
  }
}

/** A fresh access token for a saved session, or null once the session is gone (stack reset). */
export async function refresh(refreshToken: string): Promise<AuthResponse | null> {
  const api = await apiContext()
  try {
    const response = await api.post('/api/auth/refresh', {
      headers: { 'X-Refresh-Token': refreshToken },
    })
    if (!response.ok()) return null
    return { ...(await response.json()), refreshToken }
  } finally {
    await api.dispose()
  }
}

/**
 * Writes a Playwright storageState holding the session cookie, exactly as the backend sets it
 * (RefreshTokenCookieFactory): path=/, httpOnly, SameSite=Lax, Secure — Chromium sends Secure
 * cookies to http://localhost, which counts as a secure context.
 */
export function writeStorageState(path: string, refreshToken: string) {
  const { hostname } = new URL(stack.baseURL)
  mkdirSync(dirname(path), { recursive: true })
  writeFileSync(
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
