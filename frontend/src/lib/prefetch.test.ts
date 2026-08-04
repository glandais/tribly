import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import type { QueryClient } from '@tanstack/react-query'

const authState = { isAuthenticated: false }

vi.mock('@/store/authStore', () => ({
  useAuthStore: { getState: () => authState },
}))
vi.mock('@/config/appConfig', () => ({
  isSingleTeam: () => false,
  getPinnedTeamSlug: () => null,
}))

import { resolvePrefetchTarget, prefetchUrl } from './prefetch'
import { routeById } from '@/config/routeUtils'

const queryClient = {} as QueryClient

describe('resolvePrefetchTarget', () => {
  beforeEach(() => {
    authState.isAuthenticated = false
  })

  it('resolves a ride URL to its route, params and query string', () => {
    const target = resolvePrefetchTarget('/equipes/np/sorties/la-sortie?p=2')
    expect(target?.route.id).toBe('ride-detail')
    expect(target?.params).toMatchObject({ teamSlug: 'np', rideSlug: 'la-sortie' })
    expect(target?.url.searchParams.get('p')).toBe('2')
  })

  it('resolves the English variant of the same route', () => {
    expect(resolvePrefetchTarget('/teams/np/rides/la-sortie')?.route.id).toBe('ride-detail')
  })

  it('ranks by specificity, not by declaration order', () => {
    // `/equipes/nouvelle` is the team-creation page. A first-match-wins scan would just as happily
    // call it team-detail with teamSlug="nouvelle" and prefetch a team that does not exist — it
    // agrees today only because routes.config.ts happens to declare the creation route first.
    authState.isAuthenticated = true
    expect(resolvePrefetchTarget('/equipes/nouvelle')?.route.id).toBe('teams-new')
    expect(resolvePrefetchTarget('/equipes/np')?.route.id).toBe('team-detail')
  })

  it.each([
    ['https://example.com/equipes/np', 'an absolute URL'],
    ['//example.com/equipes/np', 'a protocol-relative URL'],
    ['mailto:someone@example.com', 'a mailto link'],
    ['#section', 'a bare anchor'],
    ['/pas/une/route/connue', 'an unknown path'],
  ])('returns null for %s (%s)', (href) => {
    expect(resolvePrefetchTarget(href)).toBeNull()
  })

  it('skips an authenticated route for an anonymous visitor', () => {
    expect(resolvePrefetchTarget('/equipes/np/admin')).toBeNull()
    authState.isAuthenticated = true
    expect(resolvePrefetchTarget('/equipes/np/admin')?.route.id).toBe('team-admin')
  })

  it('skips an unauthenticated-only route for a signed-in visitor', () => {
    expect(resolvePrefetchTarget('/connexion')?.route.auth).toBe('unauthenticated')
    authState.isAuthenticated = true
    expect(resolvePrefetchTarget('/connexion')).toBeNull()
  })
})

describe('prefetchUrl', () => {
  const rideDetail = routeById.get('ride-detail')!
  let prefetchSpy: ReturnType<typeof vi.spyOn>

  beforeEach(() => {
    authState.isAuthenticated = false
    prefetchSpy = vi.spyOn(rideDetail, 'prefetch').mockResolvedValue(undefined)
  })
  afterEach(() => vi.restoreAllMocks())

  it('runs the route prefetch declared in routes.config.ts', () => {
    prefetchUrl(queryClient, '/equipes/np/sorties/la-sortie')
    expect(prefetchSpy).toHaveBeenCalledTimes(1)
    expect(prefetchSpy.mock.calls[0][1]).toMatchObject({ teamSlug: 'np', rideSlug: 'la-sortie' })
  })

  it('fetches no data in chunkOnly mode — what viewport prefetching uses', () => {
    prefetchUrl(queryClient, '/equipes/np/sorties/la-sortie', { chunkOnly: true })
    expect(prefetchSpy).not.toHaveBeenCalled()
  })

  it('does nothing for a URL that matches no route', () => {
    prefetchUrl(queryClient, '/pas/une/route/connue')
    expect(prefetchSpy).not.toHaveBeenCalled()
  })

  it('does nothing when the visitor asked to save data', () => {
    Object.defineProperty(navigator, 'connection', {
      value: { saveData: true },
      configurable: true,
    })
    prefetchUrl(queryClient, '/equipes/np/sorties/la-sortie')
    expect(prefetchSpy).not.toHaveBeenCalled()
    Reflect.deleteProperty(navigator, 'connection')
  })
})
