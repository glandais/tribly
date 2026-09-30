import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { render, screen, fireEvent, cleanup, waitFor } from '@testing-library/react'
import { MantineProvider } from '@mantine/core'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { MemoryRouter } from 'react-router-dom'
import type { AdDto, PublicationDto, RideDto } from '@/api/dto'

vi.mock('react-i18next', () => ({ useTranslation: () => ({ t: (k: string) => k }) }))
vi.mock('@mantine/notifications', () => ({ notifications: { show: vi.fn() } }))

const changeRideStatus = vi.fn()
const updateRide = vi.fn()
vi.mock('@/api/endpoints/rides/rides', async (importOriginal) => ({
  ...(await importOriginal<object>()),
  changeRideStatus: (...a: unknown[]) => changeRideStatus(...a),
  updateRide: (...a: unknown[]) => updateRide(...a),
}))

let currentUserId: string | undefined
vi.mock('@/store/authStore', () => ({
  useAuthStore: (select: (s: unknown) => unknown) => select({}),
  selectUser: () => (currentUserId ? { id: currentUserId } : null),
}))

import { AdCardActions, PublicationCardActions } from './CardActions'

// The Menu's positioning builds a ResizeObserver with `new`, which the arrow-function mock of
// test/setup.ts cannot be under vitest 4.
globalThis.ResizeObserver = class {
  observe() {}
  unobserve() {}
  disconnect() {}
}

function ride(overrides: Partial<RideDto> = {}): PublicationDto {
  return {
    type: 'RIDE',
    id: 'r1',
    slug: 'dimanche',
    name: 'Sortie du dimanche',
    team: { id: 't1', name: 'N-Peloton', slug: 'np', visibility: 'PUBLIC' },
    status: 'PUBLISHED',
    finished: false,
    deleted: false,
    ...overrides,
  } as unknown as PublicationDto
}

function renderInApp(ui: React.ReactElement) {
  render(
    <MantineProvider>
      <QueryClientProvider client={new QueryClient()}>
        <MemoryRouter>{ui}</MemoryRouter>
      </QueryClientProvider>
    </MantineProvider>
  )
}

async function openMenu() {
  fireEvent.click(screen.getByRole('button', { name: 'aria.manageActions' }))
  // The dropdown lives in a portal jsdom does not lay out: query it with `hidden`.
  await screen.findByRole('menu', { hidden: true })
}

describe('PublicationCardActions', () => {
  beforeEach(() => {
    changeRideStatus.mockReset().mockResolvedValue({})
    updateRide.mockReset()
    currentUserId = undefined
  })
  afterEach(cleanup)

  it('publishes a draft through the status endpoint, never the full update', async () => {
    // A list row has no groups: sending it back through the update would delete them.
    renderInApp(<PublicationCardActions publication={ride({ status: 'DRAFT' })} canManage />)
    await openMenu()
    fireEvent.click(screen.getByRole('menuitem', { name: 'actions.publish', hidden: true }))
    await waitFor(() => expect(changeRideStatus).toHaveBeenCalledTimes(1))
    expect(changeRideStatus).toHaveBeenCalledWith('np', 'dimanche', { status: 'PUBLISHED' })
    expect(updateRide).not.toHaveBeenCalled()
  })

  it('gives a member the calendar file of an upcoming ride, and nothing to manage', async () => {
    renderInApp(<PublicationCardActions publication={ride()} canManage={false} />)
    await openMenu()
    const calendar = screen.getByRole('menuitem', {
      name: 'cards.actions.addToCalendar',
      hidden: true,
    })
    expect(calendar).toHaveAttribute('href', '/api/teams/np/rides/dimanche/ics')
    expect(screen.queryByRole('menuitem', { name: 'actions.edit', hidden: true })).toBeNull()
    expect(screen.queryByRole('menuitem', { name: 'actions.delete', hidden: true })).toBeNull()
  })

  it('offers no calendar file for a finished ride, hence no menu at all for a member', () => {
    renderInApp(<PublicationCardActions publication={ride({ finished: true })} canManage={false} />)
    expect(screen.queryByRole('button', { name: 'aria.manageActions' })).toBeNull()
  })

  it('offers « Publier » on a draft only', async () => {
    renderInApp(<PublicationCardActions publication={ride()} canManage />)
    await openMenu()
    expect(screen.queryByRole('menuitem', { name: 'actions.publish', hidden: true })).toBeNull()
    expect(screen.getByRole('menuitem', { name: 'actions.edit', hidden: true })).toHaveAttribute(
      'href',
      expect.stringContaining('dimanche')
    )
  })

  it('asks before deleting', async () => {
    renderInApp(<PublicationCardActions publication={ride()} canManage />)
    await openMenu()
    fireEvent.click(screen.getByRole('menuitem', { name: 'actions.delete', hidden: true }))
    expect(await screen.findByText('rides.detail.confirmations.delete')).toBeInTheDocument()
  })
})

describe('AdCardActions', () => {
  afterEach(cleanup)

  const ad = {
    slug: 'velo',
    team: { slug: 'np' },
    status: 'PUBLISHED',
    createdById: 'u1',
    deleted: false,
  } as unknown as AdDto

  it('lets the author manage their ad', async () => {
    currentUserId = 'u1'
    renderInApp(<AdCardActions ad={ad} isTeamAdmin={false} />)
    await openMenu()
    expect(
      screen.getByRole('menuitem', { name: 'actions.delete', hidden: true })
    ).toBeInTheDocument()
  })

  it('gives another member — an organizer included — no menu', () => {
    currentUserId = 'u2'
    renderInApp(<AdCardActions ad={ad} isTeamAdmin={false} />)
    expect(screen.queryByRole('button', { name: 'aria.manageActions' })).toBeNull()
  })
})
