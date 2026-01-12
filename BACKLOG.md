# Tribly Roadmap

## P0 — Launch Blockers
Must-have for public launch. Focus on first impressions and core UX.

### UX Polish
- [X] Responsive website — Mobile-first is non-negotiable for cyclists
  - [ ] Still issues
- [X] Dark mode — Expected by modern users
- [X] Appealing cards (icons, route previews)
  - [ ] Still some polish to do ...
- [X] Pagination / infinite scrolling — Performance at scale

### Discoverability
- [x] SEO/robots.txt — Phase 1 complete (static meta)
- [ ] SSR/Dynamic meta — Future: migrate to Next.js for full SEO
- [ ] Dynamic sitemap.xml — Requires backend endpoint
- [ ] Share URL (Social + Strava) — Viral loop

### Core Features (In Progress)
- [X] Slug changes with redirects — Already started per git status

---

## P1 — Post-Launch (Month 1-2)
Drive engagement and reduce friction for organizers.

### Organizer Productivity
- [X] Multi-GPX upload (one route per file) — Huge time saver
- [X] Team location (init route planner) — Better defaults
- [ ] Card CTAs (modify, publish, delete, add to calendar)
- [ ] Team dashboard (drafts count, what's next, activity feed)

### Member Engagement
- [X] Calendar view (rides, trips) + sync URL export
- [ ] User unit system toggle (metric/imperial) — Respect preferences
- [ ] Ride/trip "Terminated" status — Clarity on past events

### Content System
- [ ] Markdown image improvements:
  - Use image asset endpoint in display
  - Allow any image format (heic, ...)
  - Drag/drop image support
- [ ] Tags on Ride, Post, Trip, Route, Ad — Filtering/discovery

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
- [ ] Router profile selection (Brouter profiles from gpx.studio)
- [ ] Custom cycling map style (Maplibre)

### Visibility Controls
- [ ] PUBLIC_UNLISTED visibility — Shareable but not indexed

### Trip Enhancements
- [ ] Trip stats (save in DB)
- [ ] Trip stage alternative routes
- [ ] Trip view redesign + progress indicator

---

## P3 — Platform Scale
Requires significant architecture work. Spike before committing.

### Notifications
- [ ] Versatile notification system
  - Event types
  - Team/user preferences
  - Dispatchers: webhook, email, in-app

### Administration
- [ ] System admin panel
  - Manage all users/teams
  - Promote/demote admins
  - Recover deleted items
  - Configure legal pages

### Multi-Tenancy
- [ ] Team custom domains
  - Own Keycloak realm per domain
  - SQL-level domain filtering
  - SPIKE NEEDED: Estimate 2-4 weeks

---

## Icebox — Needs Discovery
Validated interest required before prioritization.

### Device Integrations
- [ ] Garmin Connect upload (one-click route sync)
- [ ] Hammerhead Karoo upload
- [ ] Garmin GPS (iq store) app (route download for current ride)
- [ ] Karoo app (route download)
- [ ] Weather for ride/trip

### Mobile
- [ ] Mobile application (iOS/Android)
  - Consider PWA first
  - Public app vs dedicated per-domain app

### Other
- [ ] User dedicated team (personal workspace)
- [ ] Places improvements (currently limited to 50 items)

---

## Tech Debt / Hygiene
Run alongside feature work.

- [ ] Schedule orphan asset deletion (>24h without entity)
- [ ] Markdown asset reference cleanup
