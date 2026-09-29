import { describe, it, expect, vi, afterEach } from 'vitest'
import { render, screen, cleanup } from '@testing-library/react'
import { MantineProvider } from '@mantine/core'
import type { RideDto } from '@/api/dto'

// Keys and their arguments, not wording.
vi.mock('react-i18next', () => ({
  useTranslation: () => ({
    t: (key: string, options?: { current?: number; max?: number }) =>
      options?.max === undefined ? key : `${key}:${options.current}/${options.max}`,
  }),
}))

import { PublicationCardProgress } from './PublicationCardProgress'

/** A list row: no `groups`, the capacity is the server's (docs/LEDGER_*.md API-5). */
function listRow(overrides: Partial<RideDto>): RideDto {
  return { groups: [], participantCount: 12, full: false, ...overrides } as RideDto
}

function renderProgress(ride: RideDto) {
  return render(
    <MantineProvider>
      <PublicationCardProgress ride={ride} />
    </MantineProvider>
  )
}

describe('PublicationCardProgress', () => {
  afterEach(cleanup)

  it('renders participants against the ride capacity, though the row carries no groups', () => {
    renderProgress(listRow({ maxParticipants: 40 }))
    expect(screen.getByText('spotsOf:12/40')).toBeTruthy()
    expect(screen.getByRole('progressbar')).toBeTruthy()
  })

  it('says full when the server says so', () => {
    renderProgress(listRow({ maxParticipants: 12, full: true }))
    expect(screen.getByText('spotsFull')).toBeTruthy()
  })

  it('draws nothing for a ride without an overall limit', () => {
    renderProgress(listRow({ maxParticipants: undefined }))
    expect(screen.queryByText(/^spots/)).toBeNull()
    expect(screen.queryByRole('progressbar')).toBeNull()
  })
})
