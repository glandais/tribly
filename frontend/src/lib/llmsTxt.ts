import type { TeamDetailDto } from '@/api/dto'
import { pathVariants } from '@/config/paths'

/** What `llms.txt` says about one site: the same public content `sitemap.xml` lists, as prose. */
export interface LlmsTxtInput {
  appName: string
  /** e.g. `https://www.pedalons.fr` */
  origin: string
  /** Public teams, first page of the anonymous listing. */
  teams: TeamDetailDto[]
  /** How many public teams exist in all, to say when `teams` is not all of them. */
  totalTeams: number
  /** The router → address-bar mapping of a host pinned to one team; identity elsewhere. */
  toBrowser?: (path: string) => string
}

/** Link text and notes must stay on their line and not close the link early. */
function inline(value: string): string {
  return value
    .replace(/\s+/g, ' ')
    .trim()
    .replace(/([\\[\]])/g, '\\$1')
}

/**
 * The `llms.txt` document of a site (https://llmstxt.org): a title, a one-line summary, then the
 * public teams and the sitemap, with absolute links on this host (docs/LEDGER_DONE.md WEB-34).
 *
 * It exposes nothing `robots.txt` and `sitemap.xml` do not already: public teams only, and it
 * names what neither lists — ads, routes, personal areas — so that a reader does not look for them.
 */
export function buildLlmsTxt({
  appName,
  origin,
  teams,
  totalTeams,
  toBrowser = (path) => path,
}: LlmsTxtInput): string {
  const url = (path: string) => origin + toBrowser(path)
  const lines: string[] = [
    `# ${inline(appName)}`,
    '',
    `> Site d'équipes cyclistes : sorties de groupe, articles, voyages et pages des équipes. Ce fichier décrit le contenu public de ${origin}, lisible sans compte.`,
    '',
    "Le site existe en français et en anglais. Chaque page a un chemin par langue (`/equipes/…` et `/teams/…`) et les deux affichent la même page ; la langue de l'interface suit l'en-tête `Accept-Language`. Le texte écrit par les équipes n'est pas traduit.",
    '',
    "N'apparaissent ni ici ni dans le plan du site : les annonces (lues entre membres seulement), les parcours et leurs cartes, les espaces personnels.",
  ]

  if (teams.length > 0) {
    lines.push('', '## Équipes', '')
    for (const team of teams) {
      const fr = url(pathVariants.team(team.slug).fr)
      const en = url(pathVariants.team(team.slug).en)
      // On a pinned host both variants are the site root: one link says it all.
      const notes = [
        team.excerpt ? inline(team.excerpt) : '',
        en !== fr ? `en anglais : ${en}` : '',
      ]
        .filter(Boolean)
        .join(' — ')
      lines.push(`- [${inline(team.name)}](${fr})${notes ? `: ${notes}` : ''}`)
    }
    const others = totalTeams - teams.length
    if (others > 0) {
      lines.push(
        '',
        `${others} autre${others > 1 ? 's' : ''} équipe${others > 1 ? 's' : ''} publique${others > 1 ? 's' : ''} : voir le plan du site.`
      )
    }
  }

  lines.push(
    '',
    '## Plan du site',
    '',
    `- [Fonctionnalités](${url(pathVariants.features().fr)}): ce que le site propose aux équipes et à leurs membres`,
    `- [sitemap.xml](${origin}/sitemap.xml): toutes les pages publiques indexables (équipes, pages, sorties, articles, voyages, étapes), avec leur date de mise à jour`
  )

  return lines.join('\n') + '\n'
}
