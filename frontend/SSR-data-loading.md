# Data-loading companion modules

How a screen's data gets declared **once** and read twice: by the page as hooks, by
`routes.config.ts` as a prefetch. Read this before adding or editing a route's `prefetch`.

## The problem this solves

A server-rendered page needs its data described in two places that must agree byte-for-byte:

- the page's own hooks (`useGetRide`, `useListRoutes`, …), which build the query key at render
  time from the router's params and the URL's filters;
- the route's `prefetch` in `routes.config.ts`, which must land the *same* keys in the
  per-request `QueryClient` before the render.

Nothing enforces the agreement, and a divergence is **silent**: the page still works, the data
still arrives — only after hydration instead of before it. No error, no failing test. It shows up
as a gap in `scripts/ssr-audit.mjs`, or as a `[prefetch-audit]` console warning, and nowhere else.

The failure modes are all "looks fine, reads different":

| Divergence | Symptom |
|---|---|
| A param spelled out by hand (`{ page: 0, size: 12 }`) that the page later changed | key mismatch, full refetch |
| A slug array built in a different **order** (query keys are structural — `['a','b'] ≠ ['b','a']`) | key mismatch, duplicate request |
| The URL's filters read through a different schema/alias than the page uses | server renders one list, client refetches another |
| A derivation copied and then edited on one side only | one of the two is dead weight in the cache |

## The pattern

One module per screen, named after it, sitting next to the page: `pages/<domain>/<screen>Data.ts`.
It exports up to three things:

1. **The shared derivation(s)** — whatever turns route params or the URL into query params:
   a slug set, a filter schema/alias pair, a params builder.
2. **`use<Screen>Data(...)`** — every query the page itself owns, returned as the **raw query
   results** (`{ data, isLoading, error, refetch, … }`) so the page keeps reading them directly.
3. **`prefetch<Screen>(queryClient, …)`** — the same data server-side, as one `Promise.all`
   (or two, when a second wave depends on the first), reusing the exact same derivations.

The route entry then reduces to a single call:

```ts
prefetch: (queryClient, params, url) => prefetchRouteList(queryClient, params.teamSlug!, url),
```

### Rules

- **Its own module, never an export of the page.** `routes.config.ts` is imported eagerly by both
  entries; importing the page would drag its lazy chunk into the main bundle. (Same reason
  `pages/home/nextRideParams.ts` and `components/common/placeAutocompleteParams.ts` exist.)
  Check after a build: the page must still have its own chunk.
- **Derive, never copy.** If both sides call the same function, they cannot drift. That is the
  whole point — a comment saying "keep in sync with…" is the thing being replaced.
- **Canonical order for anything array-shaped in a key** — dedupe *and* sort, in the shared
  function, once.
- **Auth stays out of the shared hook.** The page reads it via `useAuth()`, the prefetch via
  `useAuthStore.getState()` (the server has no hook, and the store is a per-request-forbidden
  singleton — see [SSR.md](SSR.md#session-aware-ssr)). Keep auth-conditional branches inside
  `prefetch<Screen>`.
- **The prefetch may legitimately cover more than the hook** — data fetched by children the page
  mounts (comments, GPS export options). Say so in the docblock, so the asymmetry reads as
  deliberate rather than as an oversight.
- **Shared prefetch primitives live in `config/prefetchHelpers.ts`** (`prefetchPageWindow`,
  `prefetchMemberComments`, `prefetchRoutesBulkChunked`), not in `routes.config.ts` — a companion
  importing them back from `routes.config.ts` would be a cycle.

## The two shapes, worked

### A detail page — `pages/ride/rideDetailData.ts`

Dependent data: the routes bulk needs the ride's groups, so the prefetch runs in two phases.
The derivation is the interesting part — it is what had already drifted:

```ts
/** The ride's own route plus every group's, deduped and sorted (the array goes into the key). */
export function rideRouteSlugs(ride: RideDto | undefined): string[]
```

The page, `RoutesMapView` and the prefetch now share that one set, so all three hit a single
`/routes/bulk` cache entry. Before, the page derived `group.routeSlug || ride.routeSlug` while the
map derived the ride's route *plus* the groups' — two keys, two requests, a prefetch covering one.

### A list page — `pages/route/routeListData.ts`

The key is derived from the **query string**, so the page (via `useRouteFilters`) and the prefetch
(via `readUrlFilters(url.searchParams, …)`) must read it through the same schema *and* the same
alias, then project it the same way:

```ts
export const routeListFilterOptions = { schema: routeFiltersSchema, alias: routeFiltersAlias } as const
```

The hook returns the filter state as `useRouteFilters` gives it (the panel needs the setters) plus
the query results; `prefetchRouteList` prefetches `prefetchPageWindow(routeApiParams(filters), …)`
— the page the URL asks for **plus the neighbours** `usePaginatedQuery` fetches ahead on the
client. A filtered link (`?q=gravel&p=2&sort=DISTANCE`) must server-render *that* list.

## When not to reach for it

A route whose prefetch is one call with no derivation (`teamScopedPrefetch()`,
`prefetchGetPostQuery`) does not need a module — the generated `prefetchXxxQuery` already *is* the
single source of truth. Add a companion when there is something to share: a derivation, a filter
schema/alias pair, a params builder, or a multi-phase sequence.

## Verifying

The audit is the check, not the network tab — it names the exact keys that were fetched late:

```bash
API_BASE_URL=https://staging.pedalons.fr PORT=3111 pnpm dev:ssr

# 1. the right keys are in the dehydrated state (filters included)
curl -s 'http://localhost:3111/equipes/<team>/parcours?q=gravel&p=2' \
  | grep -o '__REACT_QUERY_STATE__.*' | head -c 2000

# 2. nothing is fetched after hydration
node ../scripts/ssr-audit.mjs --url http://localhost:3111      # or one page in a browser:
# console → [prefetch-audit] route "…": all queries were covered by route prefetch
```

`[prefetch-audit]` only flushes after 5 s with no fetch activity (`SETTLE_DEBOUNCE_MS` in
`lib/prefetchAudit.ts`) — a scripted check must wait that long before concluding anything, and the
build/server must have `FRONTEND_PREFETCH_AUDIT=true`.
