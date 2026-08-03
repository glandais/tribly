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

2026-08-03 21:19Z, **local production build on :3000** — 158 checks, **0 login failures**,
**42 with issues** (42 prefetch, 1 hydration). Verdicts: **109 covered, 42 gaps, 7 not measured**.

Down from 62 gaps in the 18:51Z staging run. What closed: every team-scoped list and admin screen
(`teamMembers`, `teamAdminPlaces`, `teamAdminPages`, `rideTemplates` (list), `teamAdminPageEdit`,
`ad`, `adNew`, `teamSettings`, `postNew`/`postEdit`, `routeNew`/`routeEdit`, `teamPage`,
`teamAbout`…), plus the filtered-URL fix confirmed. Nothing regressed.

**Structural change worth knowing before fixing anything below**: every route's `prefetch` now
lives in a **companion module** next to its page (`pages/<domain>/<screen>Data.ts`), read as hooks
by the page and as a `Promise.all` by `routes.config.ts` — see
[SSR-data-loading.md](SSR-data-loading.md). Closing a gap means editing that module, and a route
with no companion at all (`stageMap`, `routesMap`, `teamCalendar`, `gpxToolsList`, `gpxToolsEdit`,
`teamsNew`, `/platform/*`, `deviceVerify*`) means writing one — not adding a `prefetchXxxQuery`
call to `routes.config.ts`.

The 7 still not measured redirect legitimately on load: `completeAccount` (×3 — the redirect *is*
the known bug), `gpxToolsNew` (×3), `teamAdmin/user1`.

**The SEO-critical surface is clean**: `home`, `teams`, `team`, `teamAbout`, `teamPage`, `ride`,
`trip`, `stage`, `post`, `routes`, `route`, `routeMap`, `allRoutes`, `allRoutesMap`, `ads`, `ad`,
`apps`, `privacy`, `terms`, `gpxToolsMap` all report `covered` for all four users. Everything below
is either an authenticated screen or a public page no crawler ranks.

A production React build minifies hydration errors (`#418` = mismatch, with `args[]` naming what
mismatched; `#185` = update loop) and carries no component diff. That's the expected trade: run the
build for the inventory, re-run a failing route against `pnpm dev:ssr` when you need the tree.

## Open

### 1. Two screens fetch an entity their route never primes

Both were found while migrating to companion modules, left alone there because a refactor is not
the place to change what is fetched, and **confirmed by this run**:

- **`adEdit`** — the page reads `/classifieds/{slug}/edit` (`useGetAdEdit`) while its prefetch
  primes `/classifieds/{slug}` (`getAd`). Two different endpoints, so two different keys: the
  prefetched entry is dead weight and the edit form fetches on the client. Fix in
  `pages/ad/adFormData.ts` — make `prefetchEditAdForm` prime the same endpoint the hook reads, and
  check whether the plain `getAd` entry is worth keeping at all.
- **`rideTemplateEdit`** — renders the template but the route prefetches only the team (bare
  `teamScopedPrefetch()`). Add the template to `pages/ridetemplate/rideTemplateFormData.ts` and
  wire the route through it, which turns the screen out of the "team-only" special case.

### 2. Form pickers fetch lists nobody opened

`rideNew` (`routes?page=0&size=20`, `ride-templates?page=0&size=20`), `rideEdit` (the same routes
list, `places/{id}`, `routes/bulk?geometry=false`), `tripNew`/`tripEdit` (the routes list).

These belong to `RoutePickerModal` and its siblings, which query **while still closed**. Gating
them on `isOpen` removes the request outright, which beats prefetching a list most visitors never
open. Worth doing, but it is a component fix, not a prefetch one — do not "fix" it by adding these
to the form companions.

### 3. `TeamCalendarPage` repeats the unstable-date pattern

`teamCalendar` fetches `/api/teams/{slug}`, `calendar/events` (twice — its own wide range **and**
FullCalendar's visible grid) and `calendar/token` after paint. It escaped the earlier fix only
because the crawler never measured it (it was in the redirect blind spot). Give it a companion
next to `pages/calendar/calendarData.ts` keyed on `getInitialCalendarRange()`, exactly like
`calendar`.

Two of its checks show `from: …T18:00:00Z` and `…T19:00:00Z` for different users — the hour
boundary crossed mid-run, i.e. the documented once-an-hour miss of `hourAlignedNow()`, not a
defect.

### 4. Fullscreen map pages have no `prefetch`

`stageMap` (team + trip + the stage's route) and `routesMap` (team + `routes/bounds`) fetch
everything client-side, while their non-map siblings `stage` and `routesMap`'s parent are clean.
Public routes, unlike the two entries above.

### 5. Queries no route primes

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
behind `isAuthenticated` — copy that, don't invent a second pattern. `profile`, `calendar` and
`gpxToolsView` already have a companion to add it to; the other four need one.

Not visible in this run but the same class of gap: the **admin lists prime
`someFiltersSchema.parse({})`** — the default list, ignoring the URL's filters — where the public
lists go through `readUrlFilters(url.searchParams, …)`. The crawler only visits their default URL,
so it reports them `covered`. Each companion says so in its docblock (`teamMembersData`,
`teamPlacesData`, `rideTemplateListData`); fixing it means threading `url` into their prefetch the
way `routeListData` does.

The date-filtered queries of `profile` and `calendar` are **now prefetched**: they used a raw
`new Date()`, whose millisecond precision put a different value in the query key on the server and
in the browser, so no prefetch could ever have matched them. Both now derive their window from
`hourAlignedNow()` (`utils/nowIso.ts`). Two client-only fetches remain by design — `MyParticipations`'
paged queries fire only when a section is opened, and FullCalendar re-queries its visible grid,
a range that depends on the viewport and can't be computed server-side.

### 6. `CalendarView` formats event times outside `useFormattedDate` — text mismatch on `/calendar`

React #418 with `args[]=text` (a text-content mismatch) on `/calendar`, for `user1` only — the one
hydration issue in the whole run, unchanged since 18:51Z.

`useEffectiveTimezone()` (`utils/dateFormat.ts`) resolves to `UTC` on the server whenever the user
has no `timezone` preference — and it is NULL for all three staging accounts — while the browser
resolves to its own zone. `FormattedDate`/`FormattedDateTime` exist precisely to absorb that with
`suppressHydrationWarning`, but `CalendarView.tsx` formats straight to a text node with dayjs
(`dayjs(dto.start).tz(tz).format('HH:mm')`, line 149, and the `YYYY-MM-DD HH:mm:ss` event
start/end above it). Server writes UTC, client writes Europe/Paris, React reports a mismatch.

Only `user1` trips it because only `user1` has events on the first paint — the other two render an
empty calendar and so have no time text to disagree about. That's the general shape of this class
of bug: it shows up only for the account whose data reaches the failing branch, so a run where it
disappears proves nothing about the fix.

### 7. `/platform/*` has no `prefetch` at all

Five routes, every one of them fetching after paint (unchanged since the previous run):

| Route | Fetched after hydration |
| --- | --- |
| `admin` | `admin/domains/stats` |
| `adminDomains` | `admin/domains?page=0&size=20` |
| `adminTeams` | `admin/domains?page=0&size=100`, `admin/teams?page=0&size=20` |
| `adminUsers` | `admin/domains?page=0&size=100`, `admin/users?page=0&size=20` |
| `adminBetaSignups` | `admin/beta-signups?page=0&size=20` |

Lowest priority in this list: these are internal screens with a handful of users, and SEO doesn't
apply. Listed so the inventory is honest, not because it's urgent.

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
