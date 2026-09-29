import { describe, it, expect } from 'vitest'
import { SitemapEntryType } from '@/api/dto'
import type { SitemapEntryDto } from '@/api/dto'
import { buildSitemapXml, sitemapPath } from './sitemap'

const at = '2026-09-30T08:00:00Z'

function entry(type: SitemapEntryType, slug?: string, tripSlug?: string): SitemapEntryDto {
  return { type, teamSlug: 'np', tripSlug, slug, lastModified: at }
}

function locs(xml: string): string[] {
  return [...xml.matchAll(/<loc>([^<]*)<\/loc>/g)].map((m) => m[1])
}

describe('sitemapPath', () => {
  it('gives every type its French detail path', () => {
    expect(sitemapPath(entry(SitemapEntryType.TEAM))).toBe('/equipes/np')
    expect(sitemapPath(entry(SitemapEntryType.TEAM_ABOUT))).toBe('/equipes/np/a-propos')
    expect(sitemapPath(entry(SitemapEntryType.TEAM_PAGE, 'charte'))).toBe(
      '/equipes/np/pages/charte'
    )
    expect(sitemapPath(entry(SitemapEntryType.RIDE, 'dimanche'))).toBe(
      '/equipes/np/sorties/dimanche'
    )
    expect(sitemapPath(entry(SitemapEntryType.POST, 'bilan'))).toBe('/equipes/np/articles/bilan')
    expect(sitemapPath(entry(SitemapEntryType.TRIP, 'alpes'))).toBe('/equipes/np/voyages/alpes')
    expect(sitemapPath(entry(SitemapEntryType.TRIP_STAGE, 'j1', 'alpes'))).toBe(
      '/equipes/np/voyages/alpes/etapes/j1'
    )
    expect(sitemapPath(entry(SitemapEntryType.ROUTE, 'boucle'))).toBe('/equipes/np/parcours/boucle')
  })

  it('never produces a map page', () => {
    for (const type of Object.values(SitemapEntryType)) {
      expect(sitemapPath(entry(type, 'x', 'y')) ?? '').not.toMatch(/\/(carte|map)$/)
    }
  })

  it('skips an entry it cannot address rather than guessing', () => {
    expect(sitemapPath(entry(SitemapEntryType.RIDE))).toBeNull()
    expect(sitemapPath(entry(SitemapEntryType.TRIP_STAGE, 'j1'))).toBeNull()
    expect(
      sitemapPath({ ...entry(SitemapEntryType.RIDE, 'x'), type: 'AD' as SitemapEntryType })
    ).toBeNull()
  })
})

describe('buildSitemapXml', () => {
  it('lists the home page and each entry as an absolute URL with its date', () => {
    const xml = buildSitemapXml(
      [entry(SitemapEntryType.TEAM), entry(SitemapEntryType.RIDE, 'dimanche')],
      'https://www.pedalons.fr'
    )
    expect(xml.startsWith('<?xml version="1.0" encoding="UTF-8"?>')).toBe(true)
    expect(locs(xml)).toEqual([
      'https://www.pedalons.fr/',
      'https://www.pedalons.fr/equipes/np',
      'https://www.pedalons.fr/equipes/np/sorties/dimanche',
    ])
    expect(xml).toContain(`<lastmod>${at}</lastmod>`)
  })

  it('writes the addresses a pinned host shows, once each', () => {
    const strip = (path: string) =>
      path === '/equipes/np' ? '/' : path.replace(/^\/equipes\/np(?=\/)/, '')
    const xml = buildSitemapXml(
      [entry(SitemapEntryType.TEAM), entry(SitemapEntryType.POST, 'bilan')],
      'https://club.example',
      strip
    )
    expect(locs(xml)).toEqual(['https://club.example/', 'https://club.example/articles/bilan'])
  })

  it('escapes what XML reserves', () => {
    const xml = buildSitemapXml([], 'https://a.example/?x=1&y=2')
    expect(locs(xml)).toEqual(['https://a.example/?x=1&amp;y=2/'])
  })
})
