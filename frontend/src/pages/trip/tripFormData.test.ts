import { describe, it, expect } from 'vitest'
import type { TripDto } from '@/api/dto'
import { tripStatusRequest, tripToRequest } from './tripFormData'

// docs/LEDGER_*.md API-60, plan §6: a trip can cross zones — its own dates load in
// `TripDto.timezone`, each stage's in its own `TripStageDto.timezone`, never the browser's (Vitest
// runs in Europe/Paris).
const TRIP = {
  name: 'Tour du monde',
  media: { markdown: '', assets: { images: [], attachments: [] } },
  dateTime: '2026-10-10T06:00:00Z',
  publishAt: '2026-10-01T16:00:00Z',
  timezone: 'Europe/Paris',
  status: 'PUBLISHED',
  visibility: 'PUBLIC',
  stages: [
    {
      id: 's1',
      name: 'Tokyo',
      dateTime: '2026-10-10T23:00:00Z',
      timezone: 'Asia/Tokyo',
      media: { markdown: '', assets: { images: [], attachments: [] } },
    },
    {
      id: 's2',
      name: 'Montréal',
      dateTime: '2026-10-12T12:30:00Z',
      timezone: 'America/Montreal',
      media: { markdown: '', assets: { images: [], attachments: [] } },
    },
  ],
  tags: [{ id: 'tag1' }],
} as unknown as TripDto

describe('tripToRequest', () => {
  it('reads the trip’s dates in the trip’s zone', () => {
    const request = tripToRequest(TRIP)
    expect(request.dateTime).toBe('2026-10-10T08:00:00')
    expect(request.publishAt).toBe('2026-10-01T18:00:00')
  })

  it('reads each stage in its own zone', () => {
    const [tokyo, montreal] = tripToRequest(TRIP).stages ?? []
    expect(tokyo.dateTime).toBe('2026-10-11T08:00:00')
    expect(montreal.dateTime).toBe('2026-10-12T08:30:00')
  })
})

describe('tripStatusRequest', () => {
  it('keeps every stage in its own zone and drops the tags', () => {
    const request = tripStatusRequest(TRIP, 'DRAFT')
    expect(request.status).toBe('DRAFT')
    expect(request.stages?.map((stage) => stage.dateTime)).toEqual([
      '2026-10-11T08:00:00',
      '2026-10-12T08:30:00',
    ])
    expect(request.tagIds).toBeUndefined()
  })
})
