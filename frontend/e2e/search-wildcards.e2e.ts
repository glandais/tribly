import type { PublicationListResponse } from '../src/api/dto'
import { apiGet } from './support/api'
import { newTeam, newUser } from './support/data'
import { expect, test, unique } from './support/fixtures'
import { newPost } from './support/posts'

/**
 * docs/LEDGER_*.md SEC-22 (audit L12): a search term is matched as typed. `%` and `_` went into
 * the LIKE pattern unescaped, so `%` listed everything and `_` matched any character — a search
 * box that answered « all of it » to a character nobody's name contains.
 */
test('% and _ typed in a search are characters to find, not wildcards', async () => {
  const owner = await newUser(unique('Joker'))
  const team = await newTeam(owner, unique('Équipe joker'))
  const plain = await newPost(owner, team.slug, unique('Sortie sans joker'))
  const percent = await newPost(owner, team.slug, unique('Remise 100% garantie'))

  const members = (search: string) =>
    apiGet<{ total: number }>(owner, `/api/teams/${team.slug}/members`, { search })
  const publications = (search: string) =>
    apiGet<PublicationListResponse>(owner, `/api/teams/${team.slug}/publications`, { search })

  // The owner's name holds neither character: nobody matches.
  expect((await members('%')).total).toBe(0)
  expect((await members('_')).total).toBe(0)
  // A literal % finds the one post that has it, and only that one.
  const found = await publications('100%')
  expect(found.publications.map((p) => p.slug)).toEqual([percent.slug])
  expect((await publications('_')).total).toBe(0)
  // The ordinary search still works.
  expect((await publications('sans joker')).publications.map((p) => p.slug)).toEqual([plain.slug])
})
