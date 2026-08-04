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

The **4 remaining gaps were filed as a false positive and were not one** — see the entry below:
they were the calendar throwing its own prefetch away on mount, on `calendar` (×3 users) and
`teamCalendar` (×1). **Fixed, and re-crawled at 09:38Z on the two routes as `user1`: both `RAS
(prefetch: covered)`.** Every other prefetch gap is closed — `adEdit`'s two ad shapes, the form
pickers, `rideEdit`'s current selections, `tripEdit`'s stage routes.

One caveat on that re-crawl, because it is the trap this file exists to name: it ran on the **4th**
of the month, and the window/grid containment it verifies also held on the 4th with the *rolling*
window that preceded the fix. What it measures is that the bail-out works at all; that it works on
the 27th is `useCalendarDateRange.test.ts`'s job, not the crawler's. A green crawl dates from the
day it ran.

Two things this run changed about the open list: the old §2 (`TripEditor`'s suspected gap) is
**deleted**, fixed and verified; and the old §1 (the calendar's time-text mismatch) was rewritten
once it turned out the fix had already landed before the run — see §1 for what is actually left.

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
The only queries still fetched after hydration anywhere were the two calendars', and the 09:38Z
re-crawl of those two routes reports them `covered`.

A production React build minifies hydration errors (`#418` = mismatch, with `args[]` naming what
mismatched; `#185` = update loop) and carries no component diff. That's the expected trade: run the
build for the inventory, re-run a failing route against `pnpm dev:ssr` when you need the tree.

## Open

### 1. A calendar without a `timezone` preference renders the wrong *day*, then corrects itself

What is left of the old "`CalendarView` formats event times outside `useFormattedDate`" entry. The
two **hydration mismatches** it described are closed; a **visible correction** is not.

**Closed — do not re-investigate:**

- *The time-text mismatch (React #418, `args[]=text`, `user1`, both calendars).* `f925468f` moved
  `useEffectiveTimezone()` behind `useSyncExternalStore` with a `getServerSnapshot` of `UTC`, so
  the hydration render reads UTC on **both** sides and `dayjs(...).tz(tz)` matches by construction.
  That commit landed at 06:34Z, *before* the 07:28Z run — so "it did not reproduce" was the fix
  working, not the environmental luck the previous version of this entry guessed at. Note that
  `CalendarView` still formats with dayjs rather than `useFormattedDate`, and that is correct here:
  `@mantine/schedule` wants `'YYYY-MM-DD HH:mm:ss'` *positioning* strings, not display text.
- *The month-grid mismatch.* `CalendarView` seeded its current date from a raw `dayjs()`, which is
  the server process's zone during SSR and the browser's on the client: between midnight and the
  local offset on the 1st of a month, the server painted the previous month's grid and the client
  the next one. Now seeded from `hourAlignedNow()` in the effective zone, which both sides agree on.

**Open, and by design rather than by accident:** for a visitor with no `timezone` preference the
server has no way to know their zone, so it commits to UTC — an event at 00:30 Paris time is
rendered by the server in the *previous day's* cell, and moves one cell when the post-hydration
re-render supplies the real zone. Not an error React can report, and not fixable server-side
without a zone hint; setting a `timezone` preference removes it entirely. The honest fixes are
either to accept it (current choice) or to stop rendering the grid server-side for visitors whose
zone is guessed — which costs the whole point of prefetching it.

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
- **`MyParticipations`' paged queries on `profile`** — they fire only when a section is opened, so
  they cannot be prefetched and will be reported on every run.
- **~~`calendar`'s second `calendar/events` window, and `teamCalendar`'s~~ — this was not a false
  positive, and the entry stays as a warning about how it was misread.** It was filed as
  "FullCalendar re-queries its own visible grid over a range that depends on the viewport, so it is
  not computable server-side". Every clause was wrong: FullCalendar had already been replaced by
  `@mantine/schedule`, nothing was re-querying *itself*, and the range came from
  `CalendarView`'s own mount effect — `getVisibleRange(date, view)`, a pure function of two pieces
  of state, with no viewport input at all. The effect reported that range unconditionally, so the
  prefetched window was replaced one render after hydration: the server ran a seven-month query,
  the grid it painted blanked under the loading overlay, and the refetch showed up here. Fixed by
  having `useCalendarDateRange` bail out when the newly visible range is already inside the loaded
  one — which also required snapping that window to month boundaries, since a rolling
  `now - 1 month` starts *after* the grid's first Monday for the last days of every month (40 days
  a year) and would have left the bail-out missing there (`useCalendarDateRange.test.ts` is the
  guard, and the reason that number is measured rather than guessed). **The lesson worth keeping: "depends on the viewport" is a claim to verify against the
  component, not a category to file a gap under** — a range that a mount effect computes from state
  is always reproducible server-side.
