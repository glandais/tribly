import { describe, it, expect, vi, afterEach } from 'vitest'
import { render, screen, cleanup, within } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { MantineProvider } from '@mantine/core'
import type { AccountDeletionImpactDto, TeamPublicationDto } from '@/api/dto'

vi.mock('@/lib/prefetch', () => ({ prefetchUrl: vi.fn() }))

// Keys, not wording: the assertions read which message is shown, not how it is phrased.
vi.mock('react-i18next', () => ({
  useTranslation: () => ({
    t: (key: string, options?: { count?: number }) =>
      options?.count === undefined ? key : `${key}#${options.count}`,
    i18n: { language: 'fr' },
  }),
}))

import { AccountDeletionImpact } from './AccountDeletionImpact'

function team(name: string, slug: string): TeamPublicationDto {
  return { id: slug, name, slug, visibility: 'PUBLIC' } as TeamPublicationDto
}

function impact(overrides: Partial<AccountDeletionImpactDto> = {}): AccountDeletionImpactDto {
  return {
    blocked: false,
    blockingTeams: [],
    deletedTeams: [],
    migratedTeams: [],
    ...overrides,
  }
}

function renderImpact(value: AccountDeletionImpactDto) {
  return render(
    <MantineProvider>
      <QueryClientProvider client={new QueryClient()}>
        <MemoryRouter>
          <AccountDeletionImpact impact={value} isLoading={false} />
        </MemoryRouter>
      </QueryClientProvider>
    </MantineProvider>
  )
}

describe('AccountDeletionImpact', () => {
  afterEach(cleanup)

  it('names a team migrated from biketeam, with its slug, and says what to do', () => {
    renderImpact(impact({ blocked: true, migratedTeams: [team('Club migré', 'club-migre')] }))

    const migrated = screen.getByTestId('deletion-migrated-teams')
    expect(within(migrated).getByText('profile.account.dangerZone.blockedMigrated#1')).toBeTruthy()
    expect(within(migrated).getByText('Club migré')).toBeTruthy()
    expect(within(migrated).getByText('(club-migre)')).toBeTruthy()
    expect(
      within(migrated).getByText('profile.account.dangerZone.blockedMigratedHint')
    ).toBeTruthy()
    expect(screen.getByText('profile.account.dangerZone.blockedTitle')).toBeTruthy()
    // Nothing about the other kind of block, nor the plain confirmation.
    expect(screen.queryByTestId('deletion-blocking-teams')).toBeNull()
    expect(screen.queryByText('profile.account.dangerZone.confirmMessage')).toBeNull()
  })

  it('links a migrated team to its members, where another admin is named', () => {
    renderImpact(impact({ blocked: true, migratedTeams: [team('Club migré', 'club-migre')] }))

    const link = screen.getByText('Club migré').closest('a')
    expect(link?.getAttribute('href')).toContain('club-migre')
  })

  it('shows both blocks when both apply, each team once', () => {
    renderImpact(
      impact({
        blocked: true,
        migratedTeams: [team('Club migré', 'club-migre')],
        blockingTeams: [team('Les Rouleurs', 'les-rouleurs')],
      })
    )

    const migrated = screen.getByTestId('deletion-migrated-teams')
    const blocking = screen.getByTestId('deletion-blocking-teams')
    expect(within(migrated).queryByText('Les Rouleurs')).toBeNull()
    expect(within(blocking).getByText('Les Rouleurs')).toBeTruthy()
    expect(within(blocking).getByText('profile.account.dangerZone.blocked#1')).toBeTruthy()
    expect(within(blocking).queryByText('Club migré')).toBeNull()
  })

  it('without a migrated team, keeps the plain block', () => {
    renderImpact(impact({ blocked: true, blockingTeams: [team('Les Rouleurs', 'les-rouleurs')] }))

    expect(screen.queryByTestId('deletion-migrated-teams')).toBeNull()
    expect(screen.getByText('Les Rouleurs')).toBeTruthy()
  })

  it('not blocked, names the teams deleted with the account and asks to confirm', () => {
    renderImpact(impact({ deletedTeams: [team('Mon équipe', 'mon-equipe')] }))

    expect(screen.getByText('Mon équipe')).toBeTruthy()
    expect(screen.getByText('profile.account.dangerZone.confirmMessage')).toBeTruthy()
    expect(screen.queryByText('profile.account.dangerZone.blockedTitle')).toBeNull()
  })
})
