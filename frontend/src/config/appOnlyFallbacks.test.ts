import { describe, it, expect, vi } from 'vitest'
import { createStaticHandler } from 'react-router-dom'

vi.mock('./locale-context', () => ({
  getCurrentLocale: () => 'fr',
}))
vi.mock('./appConfig', () => ({
  getPinnedTeamSlug: () => null,
  isSingleTeam: () => false,
}))

import { appOnlyFallbackRoutes } from './RouteGenerator'

/** Where the server sends a browser that opens `path`, or null when it renders a page. */
async function landsOn(path: string): Promise<string | null> {
  const handler = createStaticHandler([
    ...appOnlyFallbackRoutes(),
    // The team page next to them, as in the real table: a static link must win over it.
    { path: '/equipes/:teamSlug', element: null },
  ])
  const context = await handler.query(new Request(`http://pedalons.test${path}`))
  return context instanceof Response ? context.headers.get('Location') : null
}

describe('app-only deeplinks opened in a browser', () => {
  it.each([
    ['/inscription', '/connexion'],
    ['/register', '/connexion'],
    ['/profil/participations', '/profil'],
    ['/equipes/decouvrir', '/equipes'],
    ['/teams/discover', '/equipes'],
    ['/equipes/n-peloton/membres', '/equipes/n-peloton'],
  ])('%s lands on %s, not a 404', async (path, target) => {
    expect(await landsOn(path)).toBe(target)
  })

  it('leaves a team page to the team route', async () => {
    expect(await landsOn('/equipes/n-peloton')).toBeNull()
  })
})
