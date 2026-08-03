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

## Last run

2026-08-03, dev SSR server on :3000 — 67 checks, 15 with issues.

**Coverage hole**: `user1` and `admin` failed to log in (the form never redirected within 10s), so
every team-admin route and all of `/platform/*` went unvisited. Only `anonymous` (30 checks) and
`user2` (37) actually ran. The list below is therefore a floor, not a total.

## Open

### 1. Routes with no `prefetch` at all

Each of these refetches on the client what SSR could have embedded:

| Route | Fetched after hydration |
| --- | --- |
| `profile` | `users/me/participations` (×2), `auth/passkeys`, `gps/available`, `users/me/export` |
| `calendar` | `calendar/token`, `calendar/events` (×2) |
| `deviceVerifyGarmin`, `deviceVerifyKaroo` | `gps/available` |
| `gpxToolsView` | `gps/available` (the preview itself *is* prefetched) |
| `gpxToolsList` | `gpx-previews?page=0&size=12` |
| `teamsNew` | `teams?page=0&size=1` |

`gps/available` accounts for three of them and `route-detail`/`ride-detail` already prefetch it
behind `isAuthenticated` — copy that, don't invent a second pattern.

### 2. Ads are declared public but the API requires membership

`ads` and `ad-detail` are `auth: 'public'` in `routes.config.ts`, yet an anonymous visitor takes
6 × 401 on `/teams/{slug}/classifieds` and 3 × 401 on the detail. The knock-on effect is the one
the crawler flags: the server-side `prefetchGetAdQuery` gets a 401, caches nothing, and the client
refetches the ad it was supposed to receive pre-filled. Either the endpoint should serve anonymous
readers, or the route shouldn't claim to be public — the current pair is the worst of both.

## Known false positives

- **`apps` failing for `user2`** — collateral from the `CompleteAccountPage` render loop (since
  fixed). It is the route crawled right after `completeAccount`, whose `setState` loop was still
  spinning when the next `goto` fired, so it inherited 227 pageerrors and a navigation timeout.
  `apps` is clean for `anonymous`. The crawler
  only recycles its page when `goto` itself throws, not when a page merely floods pageerrors —
  until that changes, distrust any single failure that directly follows a runaway-loop page.
- **`user2`'s 403s on `teamCalendar`, `ads`, `ad`** — `user2` is not a member of `n-peloton`, so
  those are the API refusing correctly, not a defect. Worth pointing those routes at a team
  `user2` belongs to, otherwise the crawl audits an error page.
