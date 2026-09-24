# End-to-end tests

Playwright, against the **real application**: the SSR frontend and the backend images behind
traefik, with postgres, MinIO, imgproxy and mailhog — the deployment's `docker-compose.yml` plus the
`docker-compose.e2e.yml` overlay. Nothing is mocked: logins read their OTP and verification links
from mailhog, data is seeded through the REST API.

## Running

From the repository root:

```bash
scripts/e2e.sh up          # build the tribly-e2e images if missing, start, wait until it answers
scripts/e2e.sh test        # run the suite (any Playwright argument passes through)
scripts/e2e.sh reset       # empty database, files and saved sessions, then up
scripts/e2e.sh build frontend   # after a code change: rebuild an image, then `up` again
scripts/e2e.sh down
```

The app is on http://localhost:8190, mailhog on http://localhost:18025 — every port is offset from
the workstation stack (`tribly-local`), so both run side by side. `valhalla` and `tileserver` are
borrowed from the workstation stack's network; when it is down the e2e stack still starts, but
routing and map tiles fail.

From `frontend/`: `pnpm e2e` (same as `scripts/e2e.sh test`), `pnpm e2e:ui` (Playwright's UI mode),
`pnpm e2e:typecheck`. After a failure: `pnpm exec playwright show-report e2e/.report`, or
`show-trace` on the `trace.zip` the failure printed.

**The tests run against images, not your working tree** — rebuild (`scripts/e2e.sh build frontend`
or `backend`, about a minute each) and `up` before testing a change. `E2E_BASE_URL` and
`E2E_MAILHOG_URL` override where the suite looks, but the stack behind them must be one whose
bootstrap admin is `admin@e2e.test` — not the workstation stack.

## Why a separate stack

The workstation database is a biketeam restore holding thousands of real member addresses, and a
test run sends OTPs and invitations. The e2e stack starts on an **empty** database, its mailer only
reaches mailhog, and `.env.e2e` holds nothing but throwaway values — which is why it is committed.

## Writing tests

- Files are `*.e2e.ts` (Vitest's glob would pick up `*.spec.ts`).
- Import `test`, `expect` and `as` from `./support/fixtures`. `test.use(as('rider'))` signs a block in
  as that role; the `seed` fixture says what `global-setup.ts` created (a public team with the
  platform admin as owner and the rider as member).
- **Never modify the seed from a test.** Create what the test needs, with a unique name, through
  `support/api.ts` — the suite runs fully parallel, on two projects (desktop and mobile).
- Type request bodies with the generated DTOs (`import type { … } from '../src/api/dto'`): a field
  the contract made required is then a type error instead of a 400 at run time.
- The app speaks French by default (`locale: 'fr-FR'`), so navigate with the French paths
  (`/connexion`, `/equipes/…`) — or with the English ones, which the router registers too.
- Prefer roles and accessible names (`getByRole('heading', { name })`) over text: the same text
  often appears in a breadcrumb that the mobile layout hides.

## How sessions work

`global-setup.ts` logs each role in once and writes a Playwright `storageState` holding the
`refresh_token` cookie, exactly as the backend sets it; the app and its SSR server turn that cookie
into a session on the first request. The admin logs in by OTP, which the backend rate-limits to
3 per 5 minutes — so on later runs global-setup refreshes the saved sessions instead of logging in
again, and only logs in when a session no longer refreshes (after `reset`).
