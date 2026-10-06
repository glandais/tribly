import { describe, it, expect, vi } from 'vitest'
import { renderHook } from '@testing-library/react'
import type { TeamDetailDto, TeamRole } from '@/api/dto'

vi.mock('@/config/appConfig', () => ({ isSingleTeam: () => false }))
vi.mock('react-i18next', () => ({
  useTranslation: () => ({ t: (key: string) => key, i18n: { language: 'fr' } }),
}))

import { useTeamNavItems } from './useNavItems'

function team(role?: TeamRole): TeamDetailDto {
  return {
    id: 't1',
    name: 'VC Craponne',
    slug: 'vc-craponne',
    visibility: 'PUBLIC',
    enableRides: true,
    enableRoutes: true,
    enableTrips: false,
    enablePosts: true,
    enableAds: false,
    pages: [],
    role,
  } as unknown as TeamDetailDto
}

describe('team tabs', () => {
  it('a visitor keeps the feed on the team URL, with no dashboard', () => {
    const { result } = renderHook(() => useTeamNavItems(team()))
    const ids = result.current.map((i) => i.id)
    expect(ids).not.toContain('dashboard')
    expect(result.current.find((i) => i.id === 'publications')?.path).not.toContain('tab=')
  })

  it('a member opens on the dashboard, the feed moves to ?tab=publications', () => {
    const { result } = renderHook(() => useTeamNavItems(team('MEMBER')))
    expect(result.current[0].id).toBe('dashboard')
    expect(result.current.find((i) => i.id === 'publications')?.path).toContain('?tab=publications')
    expect(result.current.map((i) => i.id)).not.toContain('members')
  })

  it('an administrator also gets the « Membres » tab', () => {
    const { result } = renderHook(() => useTeamNavItems(team('ADMIN')))
    expect(result.current.map((i) => i.id)).toContain('members')
  })
})
