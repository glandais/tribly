# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

Pedalons: multi-tenant cycling team platform (rides, routes with GPX/maps, posts). Contract-first API development.

Each module has its own `CLAUDE.md` with commands, architecture, and gotchas — it loads automatically when you work with files in that directory:
[backend/](backend/CLAUDE.md) · [frontend/](frontend/CLAUDE.md) · [mobile/](mobile/CLAUDE.md) · [karoo/](karoo/CLAUDE.md) · [garmin-app/](garmin-app/CLAUDE.md)

See [docs/BRANDING.md](docs/BRANDING.md) for the brand charter — logo and icon assets, palette,
typography, components, French lexicon. **Start there**: its header maps which of the two brand
sources is authoritative over what (that file for assets, the web theme and the written charter,
`mobile/lib/core/theme/` for Flutter and the derived dark mode). The business colour code is semantic and shared by
both clients: changing it in one place only makes them diverge silently.

## Where things are written down

| Question | Read |
|---|---|
| What's left to do, and what was deliberately ruled out | **[docs/LEDGER_NEXT.md](docs/LEDGER_NEXT.md)** — start here |
| What was delivered, and the decisions not to undo | [docs/LEDGER_DONE.md](docs/LEDGER_DONE.md) |
| Product roadmap (P0 → Icebox) | [docs/BACKLOG.md](docs/BACKLOG.md) |
| Notifications (event pipeline, channels, what's left) | [docs/plans/2026-09-18-notifications.md](docs/plans/2026-09-18-notifications.md) + its ledger |
| Why the mobile app / the site / the API look the way they do | [docs/plans/archive/](docs/plans/archive/) — executed plans, kept for their arbitrations |
| Security audit (September 2026): vulnerabilities and their status | [docs/SECURITY_AUDIT.md](docs/SECURITY_AUDIT.md) |
| Infrastructure, CI/CD and code-quality audit (February 2026, statuses partly refreshed on 2026-09-29) — some rows still open; not the security reference | [docs/plans/2026-02-14-project-audit.md](docs/plans/2026-02-14-project-audit.md) |
| Deployment, backups, restore (the runbook) | [docs/OPERATIONS.md](docs/OPERATIONS.md) |
| What the product does, for whom | [docs/PRODUCT_SHEET.md](docs/PRODUCT_SHEET.md) |
| Biketeam → Pédalons migration, team by team, server to server over HTTPS (contract with biketeam, operations) | [docs/plans/2026-09-22-biketeam-live-migration.md](docs/plans/2026-09-22-biketeam-live-migration.md) + [docs/MIGRATE_BIKETEAM.md](docs/MIGRATE_BIKETEAM.md) |
| The design brief the v2 came from (state *before* v2) | [docs/plans/archive/audit-ux/](docs/plans/archive/audit-ux/) |

### Task workflow: `LEDGER_NEXT` → `LEDGER_DONE`

The two ledgers share **the same section numbering**, so a `§x.y` reference names the same topic in
both — code comments and plans cite them (`docs/LEDGER_DONE.md §1.2` in the e2e specs, for instance).

- **Creating a task**: add it to [docs/LEDGER_NEXT.md](docs/LEDGER_NEXT.md), in the section it
  belongs to (recette, reprises, portage, API infrastructure, API gaps, September follow-ups…), as a
  `- [ ]` item or a bullet that names the degradation it removes, where it lives in the code, and a
  size when known. A large task gets its own plan in `docs/plans/` and a one-paragraph entry here that
  links to it. Something deliberately **not** done goes to §6 with its reason, never silently dropped.
- **Resolving a task**: move the entry, in the same commit as the work, to
  [docs/LEDGER_DONE.md](docs/LEDGER_DONE.md) under the same section number — rewritten in the past
  tense, with the date, the API version if the contract changed, the test that covers it, and the
  decisions that must not be undone. Leave nothing behind in `LEDGER_NEXT.md` but a one-line pointer
  when the section still has open items (or when a stub keeps the numbering), and keep whatever part
  is still open in `LEDGER_NEXT.md`.
- **Never renumber** a section of either ledger: add new ones at the end. When a reference in code or
  in a plan points at an item that moved, repoint it to `LEDGER_DONE.md`.
- A plan in `docs/plans/` that is fully executed moves to `docs/plans/archive/` (with its row in the
  archive README); its leftovers go to `LEDGER_NEXT.md`.

Three invariants that cut across modules, each of which a plausible-looking change would break:

- **A ride group's leader is `RideGroupDto.leader`, nullable, and null is the common case** — render
  nothing, and never fall back to `createdBy` (the *ride's* creator, identical across all its
  groups). Guarded by the backend test `groupLeader_isNotTheRideCreator`.
- **An ad's position is blurred to ~1 km and the proximity probe is quantised on the same grid** —
  every client renders a **sector, never a pin**. `AdDto` carries no contact field either: messages
  go through an e-mail relay so no address ever appears in the API.
- **List lookups resolve per *page*, never per row** — `ParticipationLookup`, `CommentCountLookup`
  and `ThumbnailLookup` each take a fixed number of queries for a whole page. The
  `…QueryCountTest` classes fail if someone reintroduces a per-row query; don't disable them to make
  a build pass.

## Tech Stack

| Layer | Technologies |
|-------|-------------|
| Backend | Java 25, Quarkus 3.39.x, PostgreSQL 17 + PostGIS, Hibernate/Panache, Flyway |
| Frontend | TypeScript 7 (tsgo native compiler), React 19, Vite 8, Mantine UI, Zustand, React Query |
| Mobile | Flutter, Dart (see `mobile/RULES.md` for detailed guidelines) |
| Karoo | Kotlin, Jetpack Compose, karoo-ext SDK, ktor-client-karoo |
| Auth | Database auth with JWT (password, OTP, passkeys/WebAuthn) |
| IDs | TSID via hypersistence-utils (Long internally, lowercase string in API) |
| API | OpenAPI 3.1 contract-first with code generation |

## Infrastructure

**One** stack serves both workflows on a workstation — there is no separate dev compose file.

```bash
# Backing services only, for `mvn quarkus:dev` + `pnpm dev`.
docker compose up -d

# The whole application, from the images built by ./build.sh.
docker compose --profile app up -d
```

`docker-compose.yml` **is the deployment file** — keep dev tooling out of it; what a workstation
needs on top lives in `docker-compose.local.yml`, picked up through `COMPOSE_FILE` in the local
`.env`. The overlay, its ports and the `.env` keys are described in the [README](README.md#quick-start)
([Running the full stack locally](README.md#running-the-full-stack-locally)); deployment, the shared
stack, backups and restore in [docs/OPERATIONS.md](docs/OPERATIONS.md) — read its
[Deployment](docs/OPERATIONS.md#deployment) section before touching networks or the compose files.

Four rules hold whatever the change:

- **The dev backend needs the stack's credentials**: `source scripts/dev-env.sh` before
  `mvn quarkus:dev`. It exports the postgres/MinIO values from `.env` and nothing else — Quarkus reads
  env vars above `application.properties`, so the full file would override the `%dev` bootstrap
  domain (`localhost`, the WebAuthn origin of dev passkeys).
- **End-to-end tests run on a stack of their own**: `scripts/e2e.sh` starts `tribly-e2e` (empty
  database, mail to mailpit only, ports offset so it runs beside `tribly-local`). Never point the suite at the workstation stack — see
  [frontend/e2e/README.md](frontend/e2e/README.md).
- **`ENV_NAME` names the stack** — containers, network, image tags, and the `${ENV_NAME}-minio` the
  backup scripts inspect. Keep it `tribly-local` on a workstation: a local stack called `…-prod` is
  indistinguishable from the real one in `docker ps` and to `scripts/restore.sh`.
- **A local stack must not be able to send mail.** The containers run the `%prod` Quarkus profile,
  whose only way out for mail is the SMTP relay named by `QUARKUS_MAILER_*`. A local `.env`
  therefore points it at `mailpit:1025`, with TLS and login `DISABLED`. This is not cosmetic: after a
  biketeam migration the local database holds thousands of real member addresses, and one OTP or
  team invitation is enough to reach them.

## Multi-Tenancy

- **Domain-based isolation**: Each HTTP domain has its own teams/users. Same email can exist on different domains.
- **Domain resolution**: `DomainResolver` extracts domain from `X-Forwarded-Host` → `Host` header → finds `Domain` entity
- **Domain entity**: Stores `domain` (hostname), `name`, `baseUrl`. WebAuthn/email settings derived from Domain.
- **All queries must filter by domainId**: Use `pedalonsQueryContext.getDomainId()` in query builders
- **Tests**: Call `dataService.getOrCreateDefaultDomain()` + `domainResolver.setDomainForTest(domain)` in setUp()

## Contract-First Workflow

**API contract**: use the `contract-first-api` skill after modifying backend REST resources or DTOs. Bump `pedalons.api.version` in `backend/src/main/resources/application.properties` with every contract change — it drives both `info.version` in the contract and `GET /api/version`.

**UI routes contract**: `contracts/routes.yaml` is the single source of truth (multi-locale path templates, deeplink/mobile flags). Edit it, then run `pnpm generate-routes` in frontend/ to regenerate `paths.generated.ts`, `paths.generated.dart`, the apple-app-site-association file, and the deeplink section of `AndroidManifest.xml`. Never hand-edit those. See [docs/APP_LINKS.md](docs/APP_LINKS.md).

## Formatting

```bash
./format.sh                        # format every module
./format.sh mobile                 # or one module: backend|frontend|mobile|karoo|garmin-app
```

Every module has an auto-formatter (Spotless/google-java-format, Prettier, `dart format`, Spotless/ktfmt,
prettier-plugin-monkeyc). `format.sh` fails loudly if a toolchain is missing rather than skipping a module.

- **Don't spend effort on formatting** — never hand-align code, reflow lines, or reorder imports for style, and
  don't raise formatting nits in reviews. Write code naturally and let the formatters decide.
- **Always run `./format.sh` before any commit**, and include its output in that commit.

## Critical Prohibitions

- **Never run backend tests yourself** — give instructions to the user instead. You're bad at fixing tests from test outcomes.

## Dev URLs

| Service | URL |
|---------|-----|
| Backend API | http://localhost:8080/api |
| Swagger UI | http://localhost:8080/q/swagger-ui |
| Frontend | https://localhost:5173 (needs the [mkcert certificates](README.md#2-install-and-configure-mkcert) — without them it falls back to http and dev passkeys fail) |
