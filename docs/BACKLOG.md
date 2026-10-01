# Pédalons Roadmap

This is the **product** roadmap. The engineering follow-ups from the July 2026 v2 — API gaps that
each remove a named degradation, the three uncommitted infrastructure workstreams (push has shipped), and the live-app
test checklist — are in [LEDGER_NEXT.md](LEDGER_NEXT.md). Two entries below overlap with it and are
noted where they appear.

## P0 — Launch Blockers
Must-have for public launch. Focus on first impressions and core UX.

### UX Polish
- [X] Responsive website — Mobile-first is non-negotiable for cyclists
  - [ ] Still issues
- [X] Dark mode — Expected by modern users
- [X] Appealing cards (icons, route previews)
  - [ ] Still some polish to do ...
- [X] Pagination — Performance at scale
  - Offset pagination everywhere; infinite scroll on mobile only, deliberately not on the web, and
    cursor pagination still to do — see ledger `API-21` and `WEB-8`

### Discoverability
- [x] SEO/robots.txt — Phase 1 complete (static meta); `robots.txt` realigned on
      `contracts/routes.yaml` on 2026-09-29 (both locales, no route that no longer exists)
  - [x] llms.txt — `/llms.txt` per host, public teams and a pointer to the sitemap (ledger `WEB-34`)
- [X] SSR/Dynamic meta — shipped without Next.js: Express server-side rendering of the React app
      (`frontend/docs/SSR.md`) and per-page Open Graph/Twitter tags (`frontend/docs/LINK_PREVIEW.md`)
- [x] Dynamic sitemap.xml — `/sitemap.xml` per host, public content of public teams, no ads,
      no routes, no map pages (ledger `WEB-31`)
- [x] Share URL (Social) — Viral loop — share button on the web detail pages and team header
      (ledger `WEB-30`); the mobile app already shared links

### Core Features (In Progress)
- [X] Slug changes with redirects

---

## P1 — Post-Launch (Month 1-2)
Drive engagement and reduce friction for organizers.

### Organizer Productivity
- [X] Multi-GPX upload (one route per file) — Huge time saver
- [X] Team location (init route planner) — Better defaults
- [X] Card CTAs (modify, publish, delete, add to calendar) — ledger `WEB-33`
- [ ] Team dashboard (drafts count, what's next, activity feed)

### Member Engagement
- [X] Calendar view (rides, trips) + sync URL export
- [X] User unit system toggle (metric/imperial) — Respect preferences
- [X] Ride/trip "Terminated" status — Clarity on past events
  - The server says it: a computed `finished` boolean on rides, trips and calendar events, next to
    `Status` rather than in it (a past cancelled ride is both). See ledger `API-16`

### Content System
- [ ] Markdown image improvements:
  - [X] Use image asset endpoint in display
  - [X] Allow any image format (heic, ...)
  - [x] Drag/drop image support — drop or paste into the editor (ledger `WEB-32`)
- [ ] Tags on Ride, Post, Trip, Route, Ad — Filtering/discovery (one tag set per type) — ledger `API-59`

---

## P2 — Growth Phase (Month 3+)
Features that differentiate and deepen engagement.

### Discovery & Search
- [ ] Global full-text search (priority: my teams → public)
- [ ] All trips view with search filters
- [ ] User favorite routes + dedicated tab

### Route Features
- [ ] Route basket (collect routes, display on single map)
- [ ] Team/global route heatmap
- [ ] Router profile selection
- [ ] Custom cycling map style (Maplibre)

### Visibility Controls
- [x] PUBLIC_UNLISTED visibility — Shareable but not indexed
  - The enum value exists, both editors offer it, and both clients badge it orange. The "not
    indexed" half shipped with ledger `WEB-4`: the SSR head carries a per-page
    `<meta name="robots" content="noindex">` for unlisted content and for every page of an
    unlisted team, and `frontend/index.html` no longer ships a static robots tag.

### Trip Enhancements
- [ ] Trip stats (save in DB)
- [ ] Trip stage alternative routes
- [ ] Trip view redesign + progress indicator

---

## P3 — Platform Scale
Requires significant architecture work. Spike before committing.

### Notifications
- [x] Versatile notification system
  - **Phases 1 to 5 in production since 2026-09-21** — design in
    docs/plans/archive/2026-09-18-notifications.md, what shipped in ledger `NOTIF-9`
  - Event types, team/user preferences, in-app inbox, team webhook, daily digest
  - Mobile push: **live in production since 2026-09-21** (FCM, Android + iOS)
  - Web push: **live in production since 2026-09-29** (installable site, same FCM, platform `WEB`)
  - E-mail channel: built, **off in production** by product decision (2026-09-21)
  - What's left is ledger `NOTIF-1` to `NOTIF-4`

### Administration
- [X] System admin panel
  - Manage all users/teams
  - Promote/demote admins
  - Recover deleted items
  - [ ] Configure legal pages
  - [ ] Manage system images

### Multi-Tenancy
- [X] Team custom domains
  - [X] User linked to a domain
  - [X] SQL-level domain filtering
  - [X] Domain alias pinned to a team (V20, `AdminDomainAliasService`): a platform admin serves one
        team on its own hostname

---

## Icebox — Needs Discovery
Validated interest required before prioritization.

### Device Integrations
- [X] Garmin Connect upload (one-click route sync)
- [X] Hammerhead Karoo upload
- [X] Wahoo Cloud upload (cloud-only — no companion app, the ELEMNT syncs from the account)
- [X] Garmin GPS (iq store) app (route download for current ride)
- [X] Karoo app (route download)
- [ ] Weather for ride/trip

### Mobile
- [X] Mobile application (iOS/Android) — Flutter app with auth, teams, rides, routes, calendar
- [X] Mobile v2 (July 2026) — `core/pdl` design system, five-tab shell, light/dark, metric/imperial,
      twelve reworked screens. Consultation and participation only: creating and editing content
      stays out of scope

### Other
- [ ] Dedicated mobile/Garmin/Karoo app for a team with its own domain (one store listing per
      customer — the web side is covered by domain aliases)
- [ ] User dedicated team (personal workspace)
- [ ] Places improvements (currently limited to 50 items)

---

## Tech Debt / Hygiene
Run alongside feature work.

- [X] Schedule orphan asset deletion (>24h without entity) — `AssetCleanupScheduler`, daily
- [x] Markdown asset reference cleanup — `AssetService.updateAssets` keeps only the images a
      `::asset{…}` directive still references; orphan removal deletes the rest with their S3 files
