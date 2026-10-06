import { describe, it, expect } from 'vitest'
import type { PostDto } from '@/api/dto'
import { postStatusRequest, postToRequest } from './postFormData'

// docs/LEDGER_*.md API-60, plan §6: a post's dates load as wall times of `PostDto.timezone`, not of
// the browser's zone (Vitest runs in Europe/Paris).
const POST = {
  name: 'Compte rendu',
  media: { markdown: '', assets: { images: [], attachments: [] } },
  dateTime: '2026-10-10T23:00:00Z',
  publishAt: '2026-10-11T03:00:00Z',
  timezone: 'Asia/Tokyo',
  status: 'PUBLISHED',
  visibility: 'PUBLIC',
  signedAsTeam: true,
  tags: [{ id: 'tag1' }],
} as unknown as PostDto

describe('postToRequest', () => {
  it('reads the dates in the post’s zone', () => {
    const request = postToRequest(POST)
    expect(request.dateTime).toBe('2026-10-11T08:00:00')
    expect(request.publishAt).toBe('2026-10-11T12:00:00')
    expect(request.tagIds).toEqual(['tag1'])
  })
})

describe('postStatusRequest', () => {
  it('keeps the wall times in the post’s zone and leaves the tags unchanged', () => {
    const request = postStatusRequest(POST, 'DRAFT')
    expect(request.status).toBe('DRAFT')
    expect(request.dateTime).toBe('2026-10-11T08:00:00')
    expect(request.publishAt).toBe('2026-10-11T12:00:00')
    expect(request.tagIds).toBeUndefined()
  })
})
