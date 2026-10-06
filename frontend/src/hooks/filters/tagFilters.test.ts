import { describe, it, expect } from 'vitest'
import { readUrlFilters } from '@/hooks/useUrlFilters'
import { publicationFiltersSchema } from './publicationFilters'
import { agendaApiParams, agendaFiltersAlias, agendaFiltersSchema } from './agendaFilters'
import { teamPostApiParams, teamPostFiltersSchema } from './teamPostFilters'
import { adFiltersSchema, isAdFiltered } from './adFilters'
import { teamRouteFiltersSchema, routeApiParams } from './routeFilters'

const readFeed = (query: string) =>
  readUrlFilters(new URLSearchParams(query), {
    schema: agendaFiltersSchema,
    alias: agendaFiltersAlias,
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

  it('sends the agenda tags only once a kind is picked (D13)', () => {
    expect(agendaApiParams(readFeed('type=ride&tags=0abc'))).toMatchObject({
      type: 'RIDE',
      tags: ['0abc'],
    })
    expect(agendaApiParams(readFeed('tags=0abc'))).not.toHaveProperty('tags')
  })

  it('sends the post tags with the posts list, always narrowed to posts', () => {
    expect(teamPostApiParams(teamPostFiltersSchema.parse({ tags: '0abc' }))).toMatchObject({
      type: 'POST',
      tags: ['0abc'],
    })
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
