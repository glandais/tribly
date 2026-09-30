import { describe, it, expect } from 'vitest'
import type { TeamDetailDto } from '@/api/dto'
import { buildLlmsTxt } from './llmsTxt'

function team(slug: string, name: string, excerpt?: string): TeamDetailDto {
  return { slug, name, excerpt } as TeamDetailDto
}

const origin = 'https://www.pedalons.fr'

describe('buildLlmsTxt', () => {
  it('opens on the site name and a summary, as llms.txt wants', () => {
    const txt = buildLlmsTxt({ appName: 'Pédalons', origin, teams: [], totalTeams: 0 })
    const lines = txt.split('\n')
    expect(lines[0]).toBe('# Pédalons')
    expect(lines[2]).toMatch(/^> .*https:\/\/www\.pedalons\.fr/)
  })

  it('links each public team on this host, in both locales, with its excerpt', () => {
    const txt = buildLlmsTxt({
      appName: 'Pédalons',
      origin,
      teams: [team('np', 'Nantes [Pédale]', 'Sorties\n le dimanche.')],
      totalTeams: 1,
    })
    expect(txt).toContain(
      '- [Nantes \\[Pédale\\]](https://www.pedalons.fr/equipes/np): Sorties le dimanche. — en anglais : https://www.pedalons.fr/teams/np'
    )
    expect(txt).not.toContain('autre')
  })

  it('points at the sitemap for the teams it does not list', () => {
    const txt = buildLlmsTxt({ appName: 'P', origin, teams: [team('a', 'A')], totalTeams: 3 })
    expect(txt).toContain('2 autres équipes publiques : voir le plan du site.')
    expect(txt).toContain('- [sitemap.xml](https://www.pedalons.fr/sitemap.xml)')
  })

  it('gives a pinned host one link to its root', () => {
    const txt = buildLlmsTxt({
      appName: 'NP',
      origin: 'https://np.example',
      teams: [team('np', 'NP')],
      totalTeams: 1,
      toBrowser: (path) => path.replace(/^\/(equipes|teams)\/np/, '') || '/',
    })
    expect(txt).toContain('- [NP](https://np.example/)\n')
  })

  it('never names an ad or a route as content to read', () => {
    const txt = buildLlmsTxt({ appName: 'P', origin, teams: [team('a', 'A')], totalTeams: 1 })
    expect(txt).not.toMatch(/\/(annonces|ads|parcours|routes)\b/)
  })
})
