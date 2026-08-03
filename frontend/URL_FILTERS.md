# URL-backed list filters

The query string is the **single source of truth** for every list page's search, filters and pagination. Nothing lives in `useState`. This is what makes filters survive back-navigation and makes a filtered list shareable as a link.

Three hooks in `src/hooks/`, one schema module per page in `src/hooks/filters/`.

## Adding filters to a list page

1. **Declare a schema** in `src/hooks/filters/`. Keys are the **API param names** (what Orval's `ListXxxParams` expects). Reuse the field builders from `filters/common.ts`.

```ts
// src/hooks/filters/adFilters.ts
export const AD_PAGE_SIZE = 12

export const adFiltersSchema = z.object({
  search: searchField,
  adType: z.enum(AdType).optional().catch(undefined),
  page: pageField,
  size: sizeField(AD_PAGE_SIZE),
})

export const adFiltersAlias = { ...COMMON_ALIAS, adType: 'type' } as const
```

2. **Wire the page** with `useUrlFilters`. The returned `filters` object is directly assignable to `ListXxxParams` — pass it straight to the generated query hook and to `prefetchPage`.

```tsx
const { filters, setFilters } = useUrlFilters({ schema: adFiltersSchema, alias: adFiltersAlias })
const { data } = useListAds(teamSlug!, filters, { query: { placeholderData: keepPreviousData } })

<Pagination currentPage={filters.page} totalPages={totalPages}
            onPageChange={(page) => setFilters({ page })} />
```

3. **Wire the search box** with `useDebouncedSearch`, never `useState`.

```tsx
const commitSearch = useCallback((v: string) => setFilters({ search: v || undefined }), [setFilters])
const [search, setSearch] = useDebouncedSearch(filters.search ?? '', commitSearch)

<SearchInput value={search} onChange={setSearch} ... />
```

## Rules

- **Schema keys are API param names; the URL uses short aliases.** `search` → `q`, `page` → `p`, `minDistance` → `dmin`. `COMMON_ALIAS` covers `q`/`p`. A key with no alias keeps its own name.
- **Every field needs `.default()` or `.optional()`**, so `schema.parse({})` yields the defaults. That call is how the hook learns them.
- **`.catch()` goes last.** A malformed URL (`?p=abc&sort=NONSENSE`) must fall back to defaults, never throw. Verified: garbage params render the page fine and are dropped on the next write.
- **Never `z.coerce.boolean()`** — `Boolean('false')` is `true`. Use `z.stringbool()`.
- **`z.coerce.number()` turns `''` into `0`**, which is why the hook treats an empty param as an absent one.
- **`size` belongs in the schema** with a `.default(PAGE_SIZE)`. It always equals its default, so it never reaches the URL, yet it is present in `filters` for the API call.
- **Values equal to their default are omitted from the URL.** Sorting by `DATE_TIME`/`DESC` writes nothing. Use `alwaysSerialize` only when a default is context-dependent — `TeamListPage`'s role filter defaults differently for signed-in and anonymous visitors, so a shared link must spell it out.
- **`setFilters(patch)` merges and resets `page`** unless the patch sets `page` itself. **`replaceFilters(next)` replaces everything**, omitted keys falling back to their default. A "clear filters" button that omits the keys it wants cleared needs `replaceFilters` — with `setFilters` those keys would be merged back in.
- **Every write is `{ replace: true }`.** Push is reserved for navigating to a detail page. Otherwise eight keystrokes would bury the previous page under eight history entries.
- **A filter value the page maps to something else stays in its own form in the URL.** `PublicationListPage` keeps `all|ride|post|trip` in the schema and converts to `PublicationType` in the page, so URLs stay readable.
- **The mapping from filters to API params lives next to the schema, not in the page.** `routeApiParams`, `adApiParams`, `teamApiParams`, `publicationApiParams` — because the route's `prefetch` has to produce the *same* params, and a copy in `routes.config.ts` drifts the day someone edits the page. Same reason `readUrlFilters` is exported from the hook: the alias mapping and the empty-param rule must be applied identically on both sides.

## SSR: the prefetch reads the query string too

A list route's `prefetch` receives the request URL as its third argument and resolves the filters
with `readUrlFilters(url.searchParams, { schema, alias })`, then prefetches the window
`usePaginatedQuery` reads (the requested page, the next, and the previous when there is one).

This is not an optimisation, it is what makes a filtered link work at all. Before it, `?p=5` or
`?q=col` server-rendered an **empty list**: the prefetch always filled the default variant, the
page read a different query key, and the content only appeared after hydration — on links whose
entire purpose is being shared. Verify with `scripts/ssr-audit.mjs` (a `path:` entry in
`routes-ssr.yml` may carry a query string) or with
`curl -s '…/parcours?p=5' | grep -c 'href="/equipes/…/parcours/'`.

Two consequences when adding a filter:

- a **context-dependent default** must be resolved the same way in the prefetch as in the page.
  `resolveMembershipDefault` in `routes.config.ts` mirrors `useMembershipDefault`, probe included,
  and feeds the schema factory — otherwise the URL's `role=all` would be read against a `member`
  default and produce a different key.
- **presentation-only fields must not reach the API** on either side (`density`), which is exactly
  what the shared `…ApiParams` builder guarantees.

## The membership filter

The home feed, the cross-team route list and the team list can restrict themselves to the teams the
user belongs to. `hooks/filters/membership.ts` holds the shared value (`all | member | organizer |
admin`), its mapping to the API's `MinRole`, and the URL alias `role`.

- The schema key is `membership`, mapped to `MinRole` in the page — never store the API enum in the
  URL, the page's own value reads better.
- Its default comes from `useMembershipDefault()`: `member` when signed in, but `all` for an
  anonymous visitor and for a signed-in user who belongs to no team, who would otherwise land on an
  empty page.
- Because that default is context-dependent, `membership` is in `alwaysSerialize` — a shared link
  must spell out the role it was built with.
- The backend returns nothing when `minRole` is set for an anonymous visitor, so the control is
  hidden rather than shown and broken.

`RouteFilterPanel`'s "clear filters" resets each clearable key by name instead of omitting it,
precisely so the membership filter (and the search) survive.

## Scroll restoration (`useScrollRestoration`)

Mounted once, in `Layout`. It cooperates with the scroll-to-top effect there by keying off mutually exclusive navigation types: **PUSH** scrolls to top, **POP** restores, **REPLACE** (a filter edit) leaves the scroll alone. Do not remove the `navigationType === 'PUSH'` guard on `Layout`'s `window.scrollTo(0, 0)` — without it, going back scrolls to the top before the position can be restored.

Two non-obvious constraints, both learned the hard way:

- **Never read `window.scrollY` when leaving a route.** `useEffect` cleanups run after the DOM already holds the incoming — usually shorter — page, so the browser has clamped `scrollY` to that page's height (500 silently becomes 85). A ref fed only by `scroll` events keeps the real value, because clamping does not dispatch a scroll event. The position is banked in a `useLayoutEffect` cleanup.
- **A hidden tab never fires `requestAnimationFrame`.** The restore waits for the list to reach full height before scrolling — otherwise the browser clamps again — so that wait is scheduled with `rAF` when visible and `setTimeout` when not.

## Bringing the list back into view on a page change (`useScrollToListTop`)

Paginating from the bottom of a list would otherwise leave you at the bottom of the next one. Attach
`listTopRef` to the element that starts the list and call `scrollToListTop()` from `onPageChange`,
right after `setFilters({ page })`.

- **Call it from the handler, never from an effect keyed on the page number.** Such an effect also
  fires on mount, and on a back-navigation it would run just after `useScrollRestoration` restored
  the position, throwing it away.
- The offset clears the sticky `AppShell` header; Mantine's default 1250ms duration is cut to 300ms.
- Inside a modal, use `useScrollToListTopWithinContainer`. Mantine's `Modal.content` carries the
  `overflow-y`, so `useScrollIntoView` would animate the window and do nothing; the native
  `scrollIntoView` walks up to the nearest scrollable ancestor instead.

## Gotchas elsewhere

- **`useCanonicalPath` must preserve the query string.** It redirects with `navigate(path + search + hash)`. Passing only `path` silently wipes every filter on a canonical redirect.
- **`RouteViewToggle` carries `location.search`** so the list ↔ map toggle keeps the filters.
- **`usePaginatedQuery` never owns the page.** It receives `page` and returns `totalPages`. Nothing to change when adding filters.
