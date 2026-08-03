import { useGetTeam } from '@/api/endpoints/teams/teams'
import { useGetTemplate } from '@/api/endpoints/ride-templates/ride-templates'

/**
 * `CreateRideTemplatePage` and `EditRideTemplatePage` read nothing beyond the team (and, for the
 * edit page, the template itself). The team query is already covered server-side by
 * `teamScopedPrefetch()` on both the `ride-template-new` and `ride-template-edit` routes in
 * `routes.config.ts`, which prefetches the same `getGetTeamQueryKey` entry — a `prefetch` here
 * would fetch it a second time under the same key. This module exists only so each page's fetch
 * and the route's prefetch stay discoverable in one place, per the project's "always a companion"
 * convention.
 *
 * `useEditRideTemplateFormData`'s template query is a known **prefetch gap**, not fixed here: the
 * `ride-template-edit` route only prefetches the team, never `GET /api/teams/{teamSlug}/ride-
 * templates/{templateSlug}`, so the template always refetches client-side after hydration. Adding
 * that prefetch is out of scope for this migration — flagged for the orchestrator rather than
 * silently fixed, since fixing it would change `routes.config.ts` behaviour beyond a mechanical
 * move.
 */

/** Every query `CreateRideTemplatePage` itself owns — just the team. */
export function useCreateRideTemplateFormData(teamSlug: string | undefined) {
  return useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })
}

/**
 * Every query `EditRideTemplatePage` itself owns, returned as the raw query results so the page
 * keeps reading `.data` / `.isLoading` directly. See the module docblock: the template query is
 * server-rendered nowhere today, a pre-existing gap, not one introduced by this move.
 */
export function useEditRideTemplateFormData(
  teamSlug: string | undefined,
  templateSlug: string | undefined
) {
  const team = useGetTeam(teamSlug!, { query: { enabled: !!teamSlug } })
  const template = useGetTemplate(teamSlug!, templateSlug!, {
    query: { enabled: !!teamSlug && !!templateSlug },
  })
  return { team, template }
}
