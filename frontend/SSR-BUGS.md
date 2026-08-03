# SSR — open defects found by the crawler

What `scripts/ssr-audit.mjs` found and what is left to fix. The architecture and the *why*
behind the invariants live in [SSR.md](SSR.md); this file is the running list of what is
currently broken, what was ruled a false positive, and why.

Keep an entry here until the fix is merged, then delete it — a fixed bug belongs in the git
history, not in a checklist nobody re-reads. A **false positive keeps its entry forever**: the
next crawl will surface it again, and the point of writing it down is that nobody re-investigates
it a third time.

## How this list is produced

```bash
# a built SSR server on :3000 (or :8090), started with FRONTEND_PREFETCH_AUDIT=true
USER1_PASSWORD=… USER2_PASSWORD=… ADMIN_PASSWORD=… \
  node scripts/ssr-audit.mjs --url http://localhost:3000
```

The crawler visits every web route of `contracts/routes.yaml` as each user declared in
`scripts/routes-ssr.yml` and reports three things: **hydration mismatches** (`[hydration]`, or an
uncaught `pageerror`), **prefetch gaps** (`[prefetch-audit]` — a query the page fetched after load
that the route's `prefetch` didn't cover) and any other console error. `--list` shows what it
would visit without launching a browser.

**Read the `Prefetch` column, not just `RAS`.** Each check ends in one of four verdicts:

| Verdict | Meaning |
| --- | --- |
| `covered` | measured, everything the page fetched was already in the SSR cache |
| `gaps` | measured, the listed queries were not |
| `discarded` | **not measured** — the route redirected before the page's audit could settle |
| `timeout` | **not measured** — no verdict within the crawler's settle window |

`RAS` only ever means "nothing crashed". A `discarded`/`timeout` check says nothing at all about
prefetch coverage, and the report groups those under "Not measured" for that reason. This is also
why `routes-ssr.yml` pins `locale: fr`: crawling the `en` path of a route that has a `fr` one
makes the app redirect to its canonical URL on load, which discards the verdict.

## Last run

2026-08-03 18:51Z, **staging** (production build) — 167 checks, **0 login failures**, 62 with
issues (62 prefetch, 7 console-error, 1 hydration). Verdicts: **98 covered, 62 gaps, 7 not
measured**.

First trustworthy run. The two previous ones understated the problem badly:

- `admin` never logged in, so `/platform/*` was invisible. That was never a credential problem —
  the crawler clicked submit before React had hydrated and the click went nowhere, which is why a
  *different* user failed in each run.
- the crawl requested the **English** paths while the app serves French, so every route with a
  `fr` variant redirected on load, the page's own audit discarded its verdict, and the check was
  filed as `RAS` **without having measured anything**. `locale: fr` in `routes-ssr.yml` fixes it;
  the crawler now separates `covered` / `gaps` / `discarded` / `timeout` and prints a "Not
  measured" section, so this can't silently happen again.

That is where the jump from 33 to 62 comes from: not a regression, previously-blind routes.

The 7 still not measured redirect legitimately on load: `completeAccount` (×3 — the redirect *is*
the known bug), `gpxToolsNew` (×3), `teamAdmin/user1`.

**The SEO-critical surface is clean**: `home`, `teams`, `team`, `teamAbout`, `teamPage`, `ride`,
`trip`, `stage`, `post`, `routes`, `route`, `routeMap`, `allRoutes`, `allRoutesMap`, `apps`,
`privacy`, `terms`, `gpxToolsMap` all report `covered` for all four users. Everything below is
either an authenticated screen or a public page no crawler ranks.

Staging runs a production React build, so hydration errors are minified (`#418` = mismatch, with
`args[]` naming what mismatched; `#185` = update loop) and carry no component diff. That's the
expected trade: run the build for the inventory, re-run a failing route against `pnpm dev:ssr`
when you need the tree.

The two fixes committed before this run were **not deployed** to staging — nothing about
`completeAccount` or `profile`'s passkey mismatch in this list is a regression.

## Open

### 1. Team-scoped form and admin pages — *fixed, awaiting a run*

The 20 routes under `/teams/{slug}/…` now go through `teamScopedPrefetch()` in
`routes.config.ts`: the team itself plus, where the page has one, its own entity (`ride-edit`,
`trip-edit`, `post-edit`, `route-edit`, `ad-edit`, `team-admin-page-edit`) or its list
(`team-admin-places`, `team-admin-pages`, `team-members` + pending invitations, `ride-templates`,
`ride-new`/`ride-edit`'s two place autocompletes).

Two things worth not undoing:

- list params come from the page's **own filter schema** (`placeFiltersSchema.parse({})` and
  friends) and place-autocomplete params from a shared `placeAutocompleteParams()`. Hand-copying
  the params out of a crawler report also produces a matching key — right up until someone changes
  a page size, at which point the prefetch silently becomes dead weight again.
- the helper returns early for an anonymous request: these routes render a redirect, not a page.

The existing prefetches were reviewed under the same angle and three hand-copied param sets became
shared derivations — see SSR.md Finding 5. They all matched at the time; the point was that they
matched *by coincidence of defaults*, which is not a property anyone maintains on purpose.

Still fetched after paint on these routes, and left alone on purpose: `tripNew`/`tripEdit`'s
`routes?page=0&size=20`. That query belongs to `RoutePickerModal`, which runs it even while the
modal is closed — gating it on `isOpen` removes the request outright, which beats prefetching a
list most visitors never open. Worth doing, but it's a component fix, not a prefetch one.

### 2. Filtered URLs server-rendered an empty list — *fixed, verified*

`makeLoader` had the request in hand but passed only the path params to `prefetch`, so every list
route prefetched its **default** variant whatever the URL asked for. A link carrying filters —
the one thing URL filters exist for — rendered with no content at all:

```
/equipes/n-peloton/parcours       12 cards in the HTML
/equipes/n-peloton/parcours?p=5    0        (now 12)
/equipes/n-peloton/parcours?q=ride 0        (now 12)
```

…and shipped a dehydrated cache entry the page never read. `prefetch` now takes the URL and each
list route resolves its filters from it (`readUrlFilters`), prefetching the page window
`usePaginatedQuery` reads. Verified with the crawler on nine filtered URLs across `home`, `teams`,
`routes`, `allRoutes`, `allRoutesMap`: all `covered`.

The one that stays `gaps` is `ads?p=1`, for the unrelated reason in entry 8 — the endpoint answers
401 to an anonymous SSR request, so nothing gets cached to begin with.

### 3. `TeamCalendarPage` repeats the unstable-date pattern

`teamCalendar` fetches `/api/teams/{slug}`, `calendar/events` **and** `calendar/token` after paint,
with the same `from`/`to` window `CalendarPage` had. It escaped the earlier fix only because the
crawler never measured it (it was in the redirect blind spot). Give it `getInitialCalendarRange()`
and a `prefetch`, exactly like `calendar`.

Two of its checks show `from: …T18:00:00Z` and `…T19:00:00Z` for different users — the hour
boundary crossed mid-run, i.e. the documented once-an-hour miss of `hourAlignedNow()`, not a
defect.

### 4. Fullscreen map pages have no `prefetch`

`stageMap` (team + trip + the stage's route) and `routesMap` (team + `routes/bounds`) fetch
everything client-side, while their non-map siblings `stage` and `routesMap`'s parent are clean.
Public routes, unlike the two entries above.

### 5. Routes with no `prefetch` at all

Each of these refetches on the client what SSR could have embedded:

| Route | Fetched after hydration |
| --- | --- |
| `profile` | `auth/passkeys`, `gps/available`, `users/me/export` |
| `calendar` | `calendar/token` |
| `deviceVerifyGarmin`, `deviceVerifyKaroo` | `gps/available` |
| `gpxToolsView` | `gps/available` (the preview itself *is* prefetched) |
| `gpxToolsList` | `gpx-previews?page=0&size=12` |
| `gpxToolsEdit` | `gpx-previews/{previewId}` |
| `teamsNew` | `teams?page=0&size=1` |

`gps/available` accounts for three of them and `route-detail`/`ride-detail` already prefetch it
behind `isAuthenticated` — copy that, don't invent a second pattern.

The date-filtered queries of `profile` and `calendar` are **now prefetched**: they used a raw
`new Date()`, whose millisecond precision put a different value in the query key on the server and
in the browser, so no prefetch could ever have matched them. Both now derive their window from
`hourAlignedNow()` (`utils/nowIso.ts`). Two client-only fetches remain by design — `MyParticipations`'
paged queries fire only when a section is opened, and FullCalendar re-queries its visible grid,
a range that depends on the viewport and can't be computed server-side.

### 6. `CalendarView` formats event times outside `useFormattedDate` — text mismatch on `/calendar`

React #418 with `args[]=text` (a text-content mismatch) on `/calendar`, for `user1` only.

`useEffectiveTimezone()` (`utils/dateFormat.ts`) resolves to `UTC` on the server whenever the user
has no `timezone` preference — and it is NULL for all three staging accounts — while the browser
resolves to its own zone. `FormattedDate`/`FormattedDateTime` exist precisely to absorb that with
`suppressHydrationWarning`, but `CalendarView.tsx` formats straight to a text node with dayjs
(`dayjs(dto.start).tz(tz).format('HH:mm')`, line 149, and the `YYYY-MM-DD HH:mm:ss` event
start/end above it). Server writes UTC, client writes Europe/Paris, React reports a mismatch.

Only `user1` trips it because only `user1` has events on the first paint — the other two render an
empty calendar and so have no time text to disagree about. That's the general shape of this class
of bug: it shows up only for the account whose data reaches the failing branch.

### 7. `/platform/*` has no `prefetch` at all

Newly reachable now that `admin` logs in — five routes, every one of them fetching after paint:

| Route | Fetched after hydration |
| --- | --- |
| `admin` | `admin/domains/stats` |
| `adminDomains` | `admin/domains?page=0&size=20` |
| `adminTeams` | `admin/domains?page=0&size=100`, `admin/teams?page=0&size=20` |
| `adminUsers` | `admin/domains?page=0&size=100`, `admin/users?page=0&size=20` |
| `adminBetaSignups` | `admin/beta-signups?page=0&size=20` |

Lowest priority of the three: these are internal screens with a handful of users, and SEO doesn't
apply. Listed so the inventory is honest, not because it's urgent.

### 8. Ads are declared public but the API requires membership

`ads` and `ad-detail` are `auth: 'public'` in `routes.config.ts`, yet an anonymous visitor takes
6 × 401 on `/teams/{slug}/classifieds` and 3 × 401 on the detail. The knock-on effect is the one
the crawler flags: the server-side `prefetchGetAdQuery` gets a 401, caches nothing, and the client
refetches the ad it was supposed to receive pre-filled. Either the endpoint should serve anonymous
readers, or the route shouldn't claim to be public — the current pair is the worst of both.

## Known false positives

- **`apps` failing for an authenticated user** — collateral from the `CompleteAccountPage` render
  loop (fixed locally, not yet deployed). It is the route crawled right after `completeAccount`,
  whose `setState` loop was still spinning when the next `goto` fired, so it inherits the
  pageerrors and a navigation timeout. Reproduced identically for `user1` and `user2` on staging,
  while `apps` is clean for `anonymous` — the failure follows the *previous route*, not the page.
  The crawler only recycles its page when `goto` itself throws, not when a page merely floods
  pageerrors — until that changes, distrust any single failure that directly follows a
  runaway-loop page.
- **`user2`'s 403s on `teamCalendar`, `ads`, `ad`** — `user2` is not a member of `n-peloton`, so
  those are the API refusing correctly, not a defect. Worth pointing those routes at a team
  `user2` belongs to, otherwise the crawl audits an error page.
