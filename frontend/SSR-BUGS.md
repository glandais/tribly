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
node scripts/ssr-audit.mjs --url http://localhost:3000
```

The crawl accounts' passwords come from `scripts/.env.users` (`USER1_PASSWORD=…`, one per line),
which is **gitignored and must stay that way**. Exported environment variables still take
precedence, so CI and one-off runs can pass them in the old way; the file only saves putting three
secrets on a command line, where shell history keeps them.

`--config` takes an alternative route list, which is how you re-check a single route in seconds
instead of re-running all 158 — copy `scripts/routes-ssr.yml`'s header and give it one `routes:`
entry.

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

2026-08-04 07:28Z, **local production build on :3000 against the staging API** — 158 checks,
**0 login failures**, **6 with issues** (5 prefetch, 1 console error). Verdicts: **146 covered,
5 gaps, 7 not measured**.

62 → 42 → 9 → 4 → **5** across five runs, and the count went *up* for a good reason: pointing
`routes-ssr.yml`'s `tripSlug` at a trip whose stages actually have routes finally made `tripEdit`
fire the query §2 suspected. That gap is now closed (`prefetchEditTripForm` prefetches the stage
routes bulk), **re-crawled and confirmed `covered`**.

The **4 remaining gaps are the documented false positive**: FullCalendar's visible grid, whose
range depends on the viewport and cannot be computed server-side, on `calendar` (×3 users) and
`teamCalendar` (×1). Every real prefetch gap is closed — `adEdit`'s two ad shapes, the form
pickers, `rideEdit`'s current selections, `tripEdit`'s stage routes.

Two things this run changed about the open list: the old §2 (`TripEditor`'s suspected gap) is
**deleted**, fixed and verified; and §1 did **not** reproduce, but stays anyway — see its entry for
why that is not evidence of a fix. §1 is once again the only open defect.

The run's sixth issue, `gpxToolsMap`'s two `console-error: Tn`, was investigated and could not be
reproduced or identified — see the entry below, which records what the dead end cost and why
grepping the bundle for a minified class name is not the way out of it.

**Structural change worth knowing before fixing anything below**: every route with a `prefetch` now
has a **companion module** next to its page (`pages/<domain>/<screen>Data.ts`), read as hooks by the
page and as a `Promise.all` by `routes.config.ts` — see
[SSR-data-loading.md](SSR-data-loading.md). Closing a gap means editing that module; a new route
means writing one, never adding a `prefetchXxxQuery` call to `routes.config.ts`.

The 7 still not measured redirect legitimately on load: `completeAccount` (×3 — the redirect *is*
the known bug), `gpxToolsNew` (×3), `teamAdmin/user1`.

**Every measured route reports `covered`** — public, authenticated, admin and form screens alike.
The only queries still fetched after hydration anywhere are the two calendars' viewport grids.

A production React build minifies hydration errors (`#418` = mismatch, with `args[]` naming what
mismatched; `#185` = update loop) and carries no component diff. That's the expected trade: run the
build for the inventory, re-run a failing route against `pnpm dev:ssr` when you need the tree.

## Open

### 1. `CalendarView` formats event times outside `useFormattedDate` — text mismatch on `/calendar`

React #418 with `args[]=text` (a text-content mismatch) on `/calendar` and on
`/equipes/{slug}/calendrier`, both for `user1`.

**The 2026-08-04 run did not reproduce it — the entry stays anyway.** Nothing was fixed:
`CalendarView.tsx` still formats straight to a text node with dayjs, unchanged. The likely reason
it went quiet is environmental, not structural — the mismatch needs the server and the browser to
*disagree* about the zone, so it disappears the moment the crawled account has a `timezone`
preference set (`15a4d7b4` added that preference; the staging accounts had NULL when this was
written). A null timezone on any account brings it straight back. This is the last paragraph of
this entry turned on itself.

The spread to `teamCalendar` was a **consequence of fixing its prefetch**: the page renders its
events server-side now, so it finally has time text to disagree about. One defect, two screens.

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

- **`gpxToolsMap`'s two `console-error: Tn`** (2026-08-04) — **not reproducible, and the run that
  saw it could not identify it.** Not a prefetch gap (the check was `covered`) and not visibly
  broken: the run's own screenshot shows the page fully rendered — track, tiles, elevation profile,
  stats. It did not reproduce in 5 `pnpm dev:ssr` attempts, standalone and replaying `user1`'s exact
  gpx sequence (including the redirecting `gpxToolsNew` before it, to rule out the run-order trap
  below), nor as `anonymous`, `user2` or `admin`.
  **Do not try to identify a minified class name from the bundle** — that was tried and it doesn't
  work. Minified names are **chunk-local**: `map-vendor` and the maplibre worker chunk each define
  their own `Tn`, so the name identifies nothing on its own. It is tempting to land on `map-vendor`'s
  `Tn=class extends Error{…super(\`AJAXError: ${t} (${e}): ${n}\`)…}` and call it a tile fetch
  failure; that inference is **wrong on its own evidence**, because Playwright renders an Error as
  `Name: message` — `AJAXError: …` would have been printed. A *bare* `Tn` means an Error subclass
  whose **message was empty**, which that class can never produce.
  `scripts/ssr-audit.mjs` has since been fixed to reach into the error's own properties, so a repeat
  will report `Tn: (empty message) {"status":…,"url":…}` with stack frames instead of a dead end.
  **Re-open this as a real defect if it comes back with that detail** — until then there is nothing
  actionable, only a page that renders correctly.
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
