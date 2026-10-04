import type { QueryClient } from '@tanstack/react-query'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import {
  useGetTemplate,
  prefetchGetTemplateQuery,
} from '@/api/endpoints/ride-templates/ride-templates'
import { prefetchTeamTags } from '@/config/prefetchHelpers'

/**
 * `CreateRideTemplatePage` and `EditRideTemplatePage` read the team (and, for the edit page, the
 * template itself), and `RideTemplateEditor`'s `TagPicker` reads the team's ride tags. The team
 * query is covered server-side by the `teamScopedPrefetch` wrapper on both `ride-template-new` and
 * `ride-template-edit` in `routes.config.ts` — never prefetched here, it would go out twice under
 * the same key. {@link prefetchCreateRideTemplateForm} adds the tags, and
 * {@link prefetchEditRideTemplateForm} the tags and the template: it calls the same generated
 * `prefetchGetTemplateQuery` — same `teamSlug`/`templateSlug` params, same order —
 * `useEditRideTemplateFormData` builds via `useGetTemplate`, so the primed cache entry and the
 * client read share one key.
 */

/** Every query `CreateRideTemplatePage` itself owns — just the team. */
export function useCreateRideTemplateFormData(teamSlug: string | undefined) {
  return useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })
}

/**
 * Every query `EditRideTemplatePage` itself owns, returned as the raw query results so the page
 * keeps reading `.data` / `.isLoading` directly.
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

/** Server-side counterpart of what `RideTemplateEditor` reads on a new template: its tags. */
export async function prefetchCreateRideTemplateForm(queryClient: QueryClient, teamSlug: string) {
  await prefetchTeamTags(queryClient, teamSlug, 'RIDE')
}

/**
 * Primes the template query `EditRideTemplatePage` reads via `useEditRideTemplateFormData`, and the
 * editor's tags. Wrapped in `teamScopedPrefetch` in `routes.config.ts`, next to the team fetch that
 * wrapper already covers.
 */
export async function prefetchEditRideTemplateForm(
  queryClient: QueryClient,
  teamSlug: string,
  templateSlug: string
) {
  await Promise.all([
    prefetchGetTemplateQuery(queryClient, teamSlug, templateSlug),
    prefetchCreateRideTemplateForm(queryClient, teamSlug),
  ])
}
