import { describe, it, expect } from 'vitest'
import { readUrlFilters } from '@/hooks/useUrlFilters'
import { agendaApiParams, agendaFiltersAlias, agendaFiltersSchema } from './agendaFilters'

const read = (query: string) =>
  readUrlFilters(new URLSearchParams(query), {
    schema: agendaFiltersSchema,
    alias: agendaFiltersAlias,
  })

describe('agenda filters (WEB-68)', () => {
  it('opens on « À venir »: when=UPCOMING, no clock in the key', () => {
    const params = agendaApiParams(read(''))
    expect(params).toMatchObject({ when: 'UPCOMING', view: 'COMPACT', page: 0 })
    expect(params).not.toHaveProperty('from')
    expect(params).not.toHaveProperty('participating')
    expect(params.type).toBeUndefined()
  })

  it('« Je participe » is the upcoming ones the reader is registered to', () => {
    expect(agendaApiParams(read('w=me'))).toMatchObject({
      when: 'UPCOMING',
      participating: true,
    })
  })

  it('« Passées » asks for what is over, the server sorting it latest first', () => {
    const params = agendaApiParams(read('w=past'))
    expect(params).toMatchObject({ when: 'PAST' })
    expect(params).not.toHaveProperty('participating')
  })

  it('reads the former feed scopes and kinds without failing', () => {
    expect(read('w=all').scope).toBe('upcoming')
    expect(read('w=upcoming').scope).toBe('upcoming')
    expect(read('type=post').filter).toBe('all')
    expect(agendaApiParams(read('type=trip')).type).toBe('TRIP')
  })

  it('keeps the view out of the API params', () => {
    const filters = read('view=row')
    expect(filters.density).toBe('row')
    expect(agendaApiParams(filters)).not.toHaveProperty('density')
  })
})
