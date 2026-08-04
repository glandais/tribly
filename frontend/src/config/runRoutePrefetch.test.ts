import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import type { QueryClient } from '@tanstack/react-query'
import { runRoutePrefetch } from './runRoutePrefetch'
import type { RouteConfig } from './routes.types'

const queryClient = {} as QueryClient

function route(prefetch?: RouteConfig['prefetch']): RouteConfig {
  return {
    id: 'test-route',
    paths: { fr: '/test', en: '/test' },
    component: () => null,
    auth: 'public',
    parentId: null,
    prefetch,
  }
}

function axiosError(status: number) {
  return { isAxiosError: true, response: { status }, config: {}, name: 'AxiosError', message: 'x' }
}

describe('runRoutePrefetch', () => {
  beforeEach(() => {
    vi.spyOn(console, 'warn').mockImplementation(() => {})
    vi.spyOn(console, 'error').mockImplementation(() => {})
  })
  afterEach(() => vi.restoreAllMocks())

  it('passes the params and the full URL, query string included', async () => {
    const prefetch = vi.fn().mockResolvedValue(undefined)
    await runRoutePrefetch(
      route(prefetch),
      queryClient,
      { teamSlug: 'np', rideSlug: 'x' },
      new URL('https://pedalons.fr/equipes/np/sorties?p=2'),
      'link'
    )
    expect(prefetch).toHaveBeenCalledTimes(1)
    const [, params, url] = prefetch.mock.calls[0]
    expect(params).toEqual({ teamSlug: 'np', rideSlug: 'x' })
    expect(url.searchParams.get('p')).toBe('2')
  })

  it('drops undefined params so prefetch functions get guaranteed strings', async () => {
    const prefetch = vi.fn().mockResolvedValue(undefined)
    await runRoutePrefetch(
      route(prefetch),
      queryClient,
      { teamSlug: 'np', rideSlug: undefined },
      new URL('https://pedalons.fr/x'),
      'loader'
    )
    expect(prefetch.mock.calls[0][1]).toEqual({ teamSlug: 'np' })
  })

  it('does nothing when the route declares no prefetch', async () => {
    await expect(
      runRoutePrefetch(route(), queryClient, {}, new URL('https://pedalons.fr/x'), 'link')
    ).resolves.toBeUndefined()
  })

  it.each([401, 403, 404])('warns but does not throw on an expected %i', async (status) => {
    const prefetch = vi.fn().mockRejectedValue(axiosError(status))
    await expect(
      runRoutePrefetch(route(prefetch), queryClient, {}, new URL('https://pedalons.fr/x'), 'link')
    ).resolves.toBeUndefined()
    expect(console.warn).toHaveBeenCalled()
    expect(console.error).not.toHaveBeenCalled()
  })

  it('logs an unexpected failure as an error, still without throwing', async () => {
    const prefetch = vi.fn().mockRejectedValue(new Error('boom'))
    await expect(
      runRoutePrefetch(route(prefetch), queryClient, {}, new URL('https://pedalons.fr/x'), 'loader')
    ).resolves.toBeUndefined()
    expect(console.error).toHaveBeenCalled()
    expect(console.warn).not.toHaveBeenCalled()
  })
})
