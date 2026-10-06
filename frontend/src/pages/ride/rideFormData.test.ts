import { describe, it, expect } from 'vitest'
import type { RideDto } from '@/api/dto'
import { rideStatusRequest, rideToRequest } from './rideFormData'

// docs/LEDGER_*.md API-60, plan §6: an edit form loads its dates as wall times of the ride's own zone,
// not of the browser's (Vitest runs in Europe/Paris). Read in Paris, the next save would move a
// Tokyo ride by seven hours.
const RIDE = {
  name: 'Boucle du mont Takao',
  media: { markdown: '', assets: { images: [], attachments: [] } },
  dateTime: '2026-10-10T23:00:00Z',
  publishAt: '2026-10-04T01:30:00Z',
  timezone: 'Asia/Tokyo',
  status: 'PUBLISHED',
  visibility: 'PUBLIC',
  groups: [{ id: 'g1', name: 'Groupe A', time: '08:00', leader: null }],
  tags: [{ id: 'tag1' }],
} as unknown as RideDto

describe('rideToRequest', () => {
  it('reads the dates in the ride’s zone', () => {
    const request = rideToRequest(RIDE)
    expect(request.dateTime).toBe('2026-10-11T08:00:00')
    expect(request.publishAt).toBe('2026-10-04T10:30:00')
    // A group's time already is a wall time of that zone.
    expect(request.groups?.[0].time).toBe('08:00')
  })

  it('leaves an absent publishAt absent', () => {
    expect(rideToRequest({ ...RIDE, publishAt: undefined }).publishAt).toBeUndefined()
  })
})

describe('rideStatusRequest', () => {
  it('keeps the wall times in the ride’s zone and drops the tags', () => {
    const request = rideStatusRequest(RIDE, 'CANCELLED')
    expect(request.status).toBe('CANCELLED')
    expect(request.dateTime).toBe('2026-10-11T08:00:00')
    expect(request.publishAt).toBe('2026-10-04T10:30:00')
    expect(request.tagIds).toBeUndefined()
  })
})
