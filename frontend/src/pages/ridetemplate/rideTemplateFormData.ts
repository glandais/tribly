import type { QueryClient } from '@tanstack/react-query'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import {
  useGetTemplate,
  prefetchGetTemplateQuery,
} from '@/api/endpoints/ride-templates/ride-templates'

/**
 * `CreateRideTemplatePage` and `EditRideTemplatePage` read nothing beyond the team (and, for the
 * edit page, the template itself). The team query is already covered server-side by
 * `teamScopedPrefetch()` on both the `ride-template-new` and `ride-template-edit` routes in
 * `routes.config.ts`, which prefetches the same `getGetTeamQueryKey` entry — a `prefetch` here
 * would fetch it a second time under the same key. This module exists only so each page's fetch
 * and the route's prefetch stay discoverable in one place, per the project's "always a companion"
 * convention.
 *
 * `EditRideTemplatePage` leaves that team-only special case: its template query is primed by
 * {@link prefetchEditRideTemplateForm}, wrapped in `teamScopedPrefetch` on `ride-template-edit` in
 * `routes.config.ts` next to the team fetch. It calls the same generated `prefetchGetTemplateQuery`
 * — same `teamSlug`/`templateSlug` params, same order — `useEditRideTemplateFormData` builds via
 * `useGetTemplate`, so the primed cache entry and the client read share one key.
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

/**
 * Primes the template query `EditRideTemplatePage` reads via `useEditRideTemplateFormData` — the
 * gap closed on `ride-template-edit`. Wrapped in `teamScopedPrefetch` in `routes.config.ts`, next
 * to the team fetch that wrapper already covers.
 */
export async function prefetchEditRideTemplateForm(
  queryClient: QueryClient,
  teamSlug: string,
  templateSlug: string
) {
  await prefetchGetTemplateQuery(queryClient, teamSlug, templateSlug)
}
