import { describe, it, expect, vi } from 'vitest'

vi.mock('./appConfig', () => ({
  getPinnedTeamSlug: () => 'n-peloton',
}))
vi.mock('./locale-context', () => ({
  getCurrentLocale: () => 'fr',
}))

import { toBrowser, toRouter } from './pinnedHistory'

describe('pinnedHistory mapping (pinned to "n-peloton", fr locale)', () => {
  describe('toBrowser (router → browser, strips the prefix)', () => {
    it('maps the team root to /', () => {
      expect(toBrowser('/equipes/n-peloton')).toBe('/')
    })
    it('strips the fr prefix from team sub-paths', () => {
      expect(toBrowser('/equipes/n-peloton/sorties/x')).toBe('/sorties/x')
      expect(toBrowser('/equipes/n-peloton/parcours')).toBe('/parcours')
      expect(toBrowser('/equipes/n-peloton/admin')).toBe('/admin')
    })
    it('strips the en prefix too (cross-locale legacy links)', () => {
      expect(toBrowser('/teams/n-peloton/rides/x')).toBe('/rides/x')
    })
    it('leaves genuinely-global paths untouched', () => {
      expect(toBrowser('/connexion')).toBe('/connexion')
      expect(toBrowser('/plateforme')).toBe('/plateforme')
      expect(toBrowser('/')).toBe('/')
    })
    it('does not strip a lookalike that is not the prefix', () => {
      expect(toBrowser('/equipes')).toBe('/equipes')
      expect(toBrowser('/equipes/other-team/sorties')).toBe('/equipes/other-team/sorties')
    })
  })

  describe('toRouter (browser → router, adds the prefix for team paths)', () => {
    it('roots / on the team home', () => {
      expect(toRouter('/')).toBe('/equipes/n-peloton')
    })
    it('prefixes team sub-paths', () => {
      expect(toRouter('/sorties/x')).toBe('/equipes/n-peloton/sorties/x')
    })
    it('prefixes collision segments so the TEAM version wins', () => {
      expect(toRouter('/calendrier')).toBe('/equipes/n-peloton/calendrier')
      expect(toRouter('/parcours')).toBe('/equipes/n-peloton/parcours')
      expect(toRouter('/parcours/carte')).toBe('/equipes/n-peloton/parcours/carte')
      expect(toRouter('/admin')).toBe('/equipes/n-peloton/admin')
    })
    it('leaves genuinely-global paths unprefixed', () => {
      expect(toRouter('/connexion')).toBe('/connexion')
      expect(toRouter('/profil')).toBe('/profil')
      expect(toRouter('/confidentialite')).toBe('/confidentialite')
      expect(toRouter('/outils-gpx/abc')).toBe('/outils-gpx/abc')
      expect(toRouter('/garmin')).toBe('/garmin')
    })
    it('keeps the relocated platform admin reachable', () => {
      expect(toRouter('/plateforme')).toBe('/plateforme')
      expect(toRouter('/plateforme/domaines')).toBe('/plateforme/domaines')
    })
    it('is a no-op for already-prefixed (leaked legacy) paths', () => {
      expect(toRouter('/equipes/n-peloton/sorties/x')).toBe('/equipes/n-peloton/sorties/x')
      expect(toRouter('/equipes/n-peloton')).toBe('/equipes/n-peloton')
    })
  })

  it('round-trips team paths: toBrowser ∘ toRouter = identity for clean paths', () => {
    for (const clean of ['/', '/sorties/x', '/parcours', '/calendrier', '/admin']) {
      expect(toBrowser(toRouter(clean))).toBe(clean)
    }
  })
})
