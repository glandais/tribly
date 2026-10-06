// docs/LEDGER_*.md WEB-4: `PUBLIC_UNLISTED` is shareable by link but must not be indexed. The robots
// directive is part of the SSR head block, so it is checked here on the string buildMetaTags emits.
import { describe, it, expect } from 'vitest'
import { QueryClient } from '@tanstack/react-query'
import type { TFunction } from 'i18next'
import type { RideDto, TeamDetailDto, Visibility } from '@/api/dto'
import { getGetTeamQueryKey } from '@/api/endpoints/teams/teams'
import { getGetRideQueryKey } from '@/api/endpoints/rides/rides'
import { buildMetaTags, type RouteMetaContext } from '@/lib/seo'
import { rideMeta, teamDetailMeta, withIndexing } from './routeMeta'

const t = ((key: string) => key) as unknown as TFunction

function context(teamVisibility: Visibility, rideVisibility?: Visibility): RouteMetaContext {
  const queryClient = new QueryClient()
  queryClient.setQueryData(getGetTeamQueryKey('np'), {
    slug: 'np',
    name: 'N Peloton',
    visibility: teamVisibility,
  } as TeamDetailDto)
  if (rideVisibility) {
    queryClient.setQueryData(getGetRideQueryKey('np', 'sortie'), {
      slug: 'sortie',
      name: 'Sortie du dimanche',
      visibility: rideVisibility,
      participantCount: 0,
    } as unknown as RideDto)
  }
  return {
    queryClient,
    params: { teamSlug: 'np', rideSlug: 'sortie' },
    origin: 'https://pedalons.fr',
    path: '/equipes/np/sorties/sortie',
    locale: 'fr',
    t,
  }
}

/** The whole robots story of a page, as the SSR head block tells it. */
function robotsTags(ctx: RouteMetaContext, meta: ReturnType<typeof rideMeta>): string[] {
  const head = buildMetaTags(withIndexing(meta, ctx), ctx)
  return head.match(/<meta name="robots"[^>]*>/g) ?? []
}

describe('robots directive (WEB-4)', () => {
  it('indexes public content of a public team, with a single robots tag', () => {
    const ctx = context('PUBLIC', 'PUBLIC')
    expect(robotsTags(ctx, rideMeta(ctx))).toEqual([
      '<meta name="robots" content="index, follow" />',
    ])
  })

  it('does not index unlisted content', () => {
    const ctx = context('PUBLIC', 'PUBLIC_UNLISTED')
    expect(robotsTags(ctx, rideMeta(ctx))).toEqual(['<meta name="robots" content="noindex" />'])
  })

  it('does not index public content of an unlisted team', () => {
    const ctx = context('PUBLIC_UNLISTED', 'PUBLIC')
    expect(robotsTags(ctx, rideMeta(ctx))).toEqual(['<meta name="robots" content="noindex" />'])
  })

  it('does not index an unlisted team page', () => {
    const ctx = context('PUBLIC_UNLISTED')
    expect(teamDetailMeta(ctx)?.noindex).toBe(true)
    expect(robotsTags(ctx, teamDetailMeta(ctx))).toEqual([
      '<meta name="robots" content="noindex" />',
    ])
  })

  it('does not index a page of an unlisted team that has no meta() of its own', () => {
    const ctx = context('PUBLIC_UNLISTED')
    expect(robotsTags(ctx, undefined)).toEqual(['<meta name="robots" content="noindex" />'])
  })

  it('indexes a page outside any team', () => {
    const ctx = { ...context('PUBLIC_UNLISTED'), params: {} }
    expect(robotsTags(ctx, undefined)).toEqual(['<meta name="robots" content="index, follow" />'])
  })
})

describe('ride date in the link preview (WEB-70)', () => {
  it('is the day in Paris, not the SSR server’s UTC day', () => {
    const ctx = context('PUBLIC', 'PUBLIC')
    const ride = ctx.queryClient.getQueryData<RideDto>(getGetRideQueryKey('np', 'sortie'))!
    // 00:30 in Paris on 11 October is still 10 October in UTC.
    ctx.queryClient.setQueryData(getGetRideQueryKey('np', 'sortie'), {
      ...ride,
      dateTime: '2026-10-10T22:30:00Z',
    })
    expect(rideMeta(ctx)?.description).toContain('11 octobre 2026')
  })
})
