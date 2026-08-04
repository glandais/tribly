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

2026-08-03 21:57Z, **local production build on :3000** — 158 checks, **0 login failures**,
**9 with issues** (9 prefetch, 2 hydration). Verdicts: **142 covered, 9 gaps, 7 not measured**.

62 → 42 → **9** across three runs. This one measures the fixes for everything the 21:19Z run found:
the fullscreen maps, `teamCalendar`, `gps/available`, `profile`, the gpx tools, `teamsNew`, the five
`/platform/*` screens and `rideTemplateEdit` all report `covered`. Of the nine that remain, **six are
the two things this file says should stay** (four form-picker checks, two FullCalendar visible
grids), one is a fix that moved its own gap by one key (§1), and the two hydration issues are one
`CalendarView` defect on two screens (§4).

**Structural change worth knowing before fixing anything below**: every route with a `prefetch` now
has a **companion module** next to its page (`pages/<domain>/<screen>Data.ts`), read as hooks by the
page and as a `Promise.all` by `routes.config.ts` — see
[SSR-data-loading.md](SSR-data-loading.md). Closing a gap means editing that module; a new route
means writing one, never adding a `prefetchXxxQuery` call to `routes.config.ts`.

The 7 still not measured redirect legitimately on load: `completeAccount` (×3 — the redirect *is*
the known bug), `gpxToolsNew` (×3), `teamAdmin/user1`.

**The SEO-critical surface is clean**, and so is every authenticated screen except the entries
below: the public pages (`home`, `teams`, `team`, `teamAbout`, `teamPage`, `ride`, `trip`, `stage`,
`stageMap`, `post`, `routes`, `route`, `routeMap`, `routesMap`, `allRoutes`, `allRoutesMap`, `ads`,
`ad`, `apps`, `privacy`, `terms`, `gpxTools*`), `profile`, both calendars' own range, `/platform/*`,
and every team admin and form screen all report `covered`.

A production React build minifies hydration errors (`#418` = mismatch, with `args[]` naming what
mismatched; `#185` = update loop) and carries no component diff. That's the expected trade: run the
build for the inventory, re-run a failing route against `pnpm dev:ssr` when you need the tree.

## Open

### 1. `adEdit` needs **both** ad shapes — *fixed, awaiting a run*

A two-run story worth keeping, because the trap generalises.

The 21:19Z run showed the page reading `/classifieds/{slug}/edit` (`useGetAdEdit`) while the route
primed `/classifieds/{slug}` (`getAd`) — different endpoints, different keys, prefetched entry dead
weight. Swapping one for the other closed that gap and opened its mirror image: this run reports
`/classifieds/{slug}` fetched after paint.

Because the **breadcrumb trail renders the parent crumb**. `ad-edit`'s own crumb is static, but its
parent `ad-detail` carries `breadcrumb: { type: 'dynamic', entity: 'ad' }`, and `useBreadcrumbData`
fires `useGetAd` on *any* route whose params carry an `adSlug`. So an edit screen reads its parent's
entity even when its own form doesn't. `prefetchEditAdForm` now primes both.

Generalises to every `…-edit` route under a dynamic-breadcrumb parent — ride, post, trip, route,
team page, ride template all already prime the parent's entity because their form happens to read
the same shape. `ad` was the one where the two shapes differ, which is why only it broke.

### 2. Form pickers fetched lists nobody opened — *fixed, awaiting a run*

`RoutePickerModal` (`routes?page=0&size=20`, on `rideNew`/`rideEdit`/`tripNew`/`tripEdit`) and
`RideTemplatePickerModal` (`ride-templates?page=0&size=20`, on `rideNew`) query **while still
closed**: the editors mount them for the whole form session, and only `Modal`'s *rendering* is
conditional, not the hooks above it. Both queries are now `enabled: isOpen` — the request is gone,
not moved, which beats prefetching a list most visitors never open.

Watch the loading branch if you touch this again: with `enabled: false` the query is `pending` but
not `fetching`, so `isLoading` is **false** and the modal would flash its empty state for one frame
on opening. Both now branch on `isPending`, which stays true until the first data arrives.

### 3. `rideEdit` fetched the form's current selections — *fixed, awaiting a run*

Not the pickers: `places/{id}` (`PlaceAutocomplete`'s lookup of the place already chosen) and
`routes/bulk?geometry=false` (`RideEditor`'s summary of each group's route). Both render on the
first paint, so unlike the pickers they were genuine prefetch candidates. `prefetchEditRideForm`
now runs a second phase off the ride it just primed: `rideFormPlaceIds` and
`rideFormGroupRouteSlugs`, both exported from `pages/ride/rideFormData.ts`.

`rideFormGroupRouteSlugs` is **not** `rideRouteSlugs` from `rideDetailData.ts`, and the two must not
be merged: the editor lists each group's own route with no fallback to the ride's, and asks for
`geometry: false`. Either difference alone changes the key.

`TripEditor` has the same two shapes (a `PlaceAutocomplete`, a `useRoutesBulk` with
`geometry: false` over its stages) and `tripEdit` prefetches neither — but the crawler has **never
seen those queries fire** there, on any run, which means they are behind something the first paint
doesn't render (collapsed stage panels), or the crawled trip has no stage routes. Don't prefetch on
the strength of the symmetry: crawl a trip that has stage routes first, and only add what the report
then names.

### 4. `CalendarView` formats event times outside `useFormattedDate` — text mismatch on `/calendar`

React #418 with `args[]=text` (a text-content mismatch) on `/calendar`, and now on
`/equipes/{slug}/calendrier` as well — both for `user1`, the two hydration issues in this run. The
spread to `teamCalendar` is a **consequence of fixing its prefetch**: the page renders its events
server-side now, so it finally has time text to disagree about. One defect, two screens.

`useEffectiveTimezone()` (`utils/dateFormat.ts`) resolves to `UTC` on the server whenever the user
has no `timezone` preference — and it is NULL for all three staging accounts — while the browser
resolves to its own zone. `FormattedDate`/`FormattedDateTime` exist precisely to absorb that with
`suppressHydrationWarning`, but `CalendarView.tsx` formats straight to a text node with dayjs
(`dayjs(dto.start).tz(tz).format('HH:mm')`, line 149, and the `YYYY-MM-DD HH:mm:ss` event
start/end above it). Server writes UTC, client writes Europe/Paris, React reports a mismatch.

Only `user1` trips it, on either screen, because only `user1` has events on the first paint — the
other two render an empty calendar and so have no time text to disagree about. That's the general
shape of this class of bug: it shows up only for the account whose data reaches the failing branch,
so a run where it disappears proves nothing about the fix. It is also why fixing a *prefetch* can
surface it somewhere new, as `teamCalendar` just did.

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
  those were the API refusing correctly, not a defect. All three are member-only
  (`auth: 'authenticated'` plus a team-role check server-side) and are now crawled as `user1` only,
  the one crawl user who belongs to that team — otherwise the run audits an error page. Same trap
  for any future member-scoped route: restrict its `users:` or point `params.teamSlug` at a team
  the user belongs to.
- **`calendar`'s second `calendar/events` window, and `teamCalendar`'s** — FullCalendar re-queries
  its own visible grid right after mount, over a range that depends on the viewport. That second
  window is not computable server-side, so it will be reported as a gap on every run, forever. The
  *first* range (`getInitialCalendarRange()`) is the one that must stay `covered`. Same for
  `MyParticipations`' paged queries on `profile`: they fire only when a section is opened.
