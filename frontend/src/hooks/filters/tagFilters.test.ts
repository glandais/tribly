import { describe, it, expect } from 'vitest'
import { readUrlFilters } from '@/hooks/useUrlFilters'
import {
  teamPublicationApiParams,
  teamPublicationFiltersAlias,
  teamPublicationFiltersSchema,
  publicationFiltersSchema,
} from './publicationFilters'
import { adFiltersSchema, isAdFiltered } from './adFilters'
import { teamRouteFiltersSchema, routeApiParams } from './routeFilters'

const NOW = '2026-10-01T10:00:00Z'

const readFeed = (query: string) =>
  readUrlFilters(new URLSearchParams(query), {
    schema: teamPublicationFiltersSchema,
    alias: teamPublicationFiltersAlias,
  })

describe('tag filter in the URL', () => {
  it('reads ?tags=<id>,<id> as ids, deduplicated', () => {
    expect(readFeed('type=ride&tags=0abc,0DEF,0abc').tags).toEqual(['0abc', '0def'])
  })

  it('drops what is not an id, and an empty selection is no filter at all', () => {
    expect(readFeed('type=ride&tags=0abc,<script>,').tags).toEqual(['0abc'])
    expect(readFeed('type=ride&tags=,,').tags).toBeUndefined()
    expect(readFeed('type=ride').tags).toBeUndefined()
  })

  it('sends the tags only on a feed narrowed to one kind (D13)', () => {
    expect(teamPublicationApiParams(readFeed('type=ride&tags=0abc'), NOW)).toMatchObject({
      type: 'RIDE',
      tags: ['0abc'],
    })
    expect(teamPublicationApiParams(readFeed('tags=0abc'), NOW)).not.toHaveProperty('tags')
  })

  it('leaves the home feed schema without tags (D7)', () => {
    expect(Object.keys(publicationFiltersSchema.shape)).not.toContain('tags')
  })

  it('passes the tags through to the team route list and counts them as a filter on ads', () => {
    const routes = teamRouteFiltersSchema.parse({ tags: '0abc' })
    expect(routeApiParams(routes)).toMatchObject({ tags: ['0abc'] })
    expect(isAdFiltered(adFiltersSchema.parse({ tags: '0abc' }))).toBe(true)
    expect(isAdFiltered(adFiltersSchema.parse({}))).toBe(false)
  })
})
