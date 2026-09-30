import { afterEach, describe, expect, it, vi } from 'vitest'
import { resolveSsrSession } from './ssrSession'

function answer(status: number, body: unknown, setCookies: string[] = []): Response {
  const headers = new Headers({ 'Content-Type': 'application/json' })
  for (const cookie of setCookies) headers.append('Set-Cookie', cookie)
  return new Response(JSON.stringify(body), { status, headers })
}

describe('resolveSsrSession', () => {
  afterEach(() => {
    vi.unstubAllGlobals()
  })

  it('hands over the rotated session cookie with the session', async () => {
    const rotated = [
      'refresh_token=; Path=/api; Max-Age=0',
      'refresh_token=next; Path=/; HttpOnly; Secure; SameSite=Lax',
    ]
    vi.stubGlobal(
      'fetch',
      vi.fn(async () => answer(200, { accessToken: 'at', user: null }, rotated))
    )
    const onSetCookie = vi.fn()

    const session = await resolveSsrSession({ cookie: 'refresh_token=previous' }, onSetCookie)

    expect(session?.accessToken).toBe('at')
    expect(onSetCookie).toHaveBeenCalledWith(rotated)
  })

  it('hands over nothing inside the grace of another rotation, where no cookie comes back', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn(async () => answer(200, { accessToken: 'at', user: null }))
    )
    const onSetCookie = vi.fn()

    await resolveSsrSession({ cookie: 'refresh_token=previous' }, onSetCookie)

    expect(onSetCookie).not.toHaveBeenCalled()
  })

  it('does not call the backend without a session cookie', async () => {
    const fetch = vi.fn()
    vi.stubGlobal('fetch', fetch)

    expect(await resolveSsrSession({ cookie: 'other=1' })).toBeUndefined()
    expect(fetch).not.toHaveBeenCalled()
  })
})
