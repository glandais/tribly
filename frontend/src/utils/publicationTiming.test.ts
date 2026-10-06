import { describe, it, expect } from 'vitest'
import type { PublicationDto } from '@/api/dto'
import { isUnderWay } from './publicationTiming'

const NOW = Date.parse('2026-10-11T10:00:00Z')

function pub(overrides: Record<string, unknown>): PublicationDto {
  return {
    type: 'RIDE',
    status: 'PUBLISHED',
    dateTime: '2026-10-11T08:30:00Z',
    endDateTime: '2026-10-11T12:10:00Z',
    ...overrides,
  } as unknown as PublicationDto
}

describe('isUnderWay', () => {
  it('is true between the start and the stored end', () => {
    expect(isUnderWay(pub({}), NOW)).toBe(true)
    expect(isUnderWay(pub({ type: 'TRIP' }), NOW)).toBe(true)
  })

  it('is false before the start and from the end on', () => {
    expect(isUnderWay(pub({ dateTime: '2026-10-11T10:00:01Z' }), NOW)).toBe(false)
    expect(isUnderWay(pub({ endDateTime: '2026-10-11T10:00:00Z' }), NOW)).toBe(false)
  })

  it('is never true for a cancelled ride, nor for a post', () => {
    expect(isUnderWay(pub({ status: 'CANCELLED' }), NOW)).toBe(false)
    expect(isUnderWay(pub({ type: 'POST' }), NOW)).toBe(false)
  })
})
