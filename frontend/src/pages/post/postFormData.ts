import type { QueryClient } from '@tanstack/react-query'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import { useGetPost, prefetchGetPostQuery } from '@/api/endpoints/posts/posts'
import { prefetchTeamTags } from '@/config/prefetchHelpers'

/**
 * `CreatePostPage` reads the team — covered server-side by the `teamScopedPrefetch` wrapper on the
 * `post-new` route in `routes.config.ts` — and `PostEditor`'s `TagPicker` reads the team's post
 * tags, which {@link prefetchCreatePostForm} adds (never the team again: same key, fetched twice).
 *
 * `EditPostPage` reads the team plus the post itself. The team is still the `teamScopedPrefetch`
 * wrapper's job, but the post is `EditPostPage`'s own data, so {@link prefetchEditPostForm} covers
 * it server-side via the generated `prefetchGetPostQuery` — wired as `teamScopedPrefetch((qc, p) =>
 * prefetchEditPostForm(qc, p.teamSlug!, p.postSlug!))` in `routes.config.ts`. Describing that query
 * twice (once here, once inline in the route table) is what this module exists to prevent: a
 * divergence doesn't break anything visibly, it just yields a different query key, so the client
 * refetches after hydration and only the prefetch audit of
 * `e2e/routes-render.e2e.ts` notices.
 */

/** Every query `CreatePostPage` itself owns, returned as the raw query result. */
export function useCreatePostFormData(teamSlug: string | undefined) {
  const team = useGetTeam(teamSlug!, { query: { enabled: !!teamSlug } })
  return { team }
}

/** Every query `EditPostPage` itself owns, returned as the raw query results. */
export function useEditPostFormData(teamSlug: string | undefined, postSlug: string | undefined) {
  const team = useGetTeam(teamSlug!, { query: { enabled: !!teamSlug } })
  const post = useGetPost(teamSlug!, postSlug!, {
    query: { enabled: !!teamSlug && !!postSlug },
  })
  return { team, post }
}

/** Server-side counterpart of what `PostEditor` reads on a new post: its `TagPicker`'s tags. */
export async function prefetchCreatePostForm(queryClient: QueryClient, teamSlug: string) {
  await prefetchTeamTags(queryClient, teamSlug, 'POST')
}

/**
 * Server-side counterpart of {@link useEditPostFormData}'s post query and the editor's tags (the
 * team itself comes from the `teamScopedPrefetch` wrapper).
 */
export async function prefetchEditPostForm(
  queryClient: QueryClient,
  teamSlug: string,
  postSlug: string
) {
  await Promise.all([
    prefetchGetPostQuery(queryClient, teamSlug, postSlug),
    prefetchCreatePostForm(queryClient, teamSlug),
  ])
}
