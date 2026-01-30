# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

See also the root `../CLAUDE.md` for full-stack context (backend, mobile, karoo, garmin, multi-tenancy, contract-first workflow).

## Commands

```bash
pnpm dev                           # Dev server (localhost:5173, proxies /api to :8080)
pnpm build                         # TypeScript check + Vite build
pnpm generate-api                  # Regenerate API client from ../contracts/openapi.json
pnpm lint                          # ESLint (includes i18next/no-literal-string)
pnpm lint:fix                      # ESLint with auto-fix
pnpm format                        # Prettier
pnpm test                          # Vitest (watch mode)
pnpm i18n:lint                     # Validate i18n keys (checks templated keys have type annotations)
pnpm i18n:extract                  # Extract new keys from t() and tRegister() calls
```

## Stack

TypeScript 5, React 19, Vite, Mantine UI 8, React Router 7, TanStack React Query 5, Zustand 5, Zod 4, Orval (API codegen), i18next, MapLibre GL, Tiptap (rich text), Chart.js, FullCalendar.

## Architecture

### API Layer (generated — do not edit `src/api/`)

Orval generates from `../contracts/openapi.json`:
- `src/api/endpoints/` — React Query hooks per OpenAPI tag (`useGetRide`, `useCreateRide`, etc.)
- `src/api/dto/` — TypeScript types
- `src/api/zod/` — Zod schemas for form validation

Custom axios mutator in `lib/axiosInstance.ts` handles: JWT bearer tokens from authStore, `Accept-Language` header, 401 token refresh with request queuing, error notifications via Mantine.

### Routing (config-driven)

- Routes declared in `config/routes.config.ts` with metadata (auth requirements, breadcrumbs, `parentId` hierarchy, `subRouteIds` for tab nav)
- `RouteGenerator.tsx` converts config to `<Route>` elements with lazy loading
- Auth guards: `<AuthenticatedRoute>`, `<UnauthenticatedRoute>`, or public
- Breadcrumbs auto-built from `parentId` chain

### State Management

- **Server state**: React Query (generated hooks from Orval)
- **Global client state**: Zustand — only `authStore` (JWT, user) and `preferencesStore` (unit system, persisted to localStorage)
- Auth initializes by calling `/api/auth/refresh` on mount; `useAuth` hook fetches `/api/users/me`

### Path Management

`config/paths.ts` exports type-safe path builders: `paths.team(slug)`, `paths.ride(teamSlug, rideSlug)`, etc.

### i18n

- i18next with browser language detection. **French is the default/fallback language.**
- Single `common` namespace per language: `locales/fr/common.json`, `locales/en/common.json`
- `tRegister(key)` used in route config for static key tracking

### Forms

- Mantine `useForm` + Zod validation via `zodFormValidator` wrapper using generated Zod schemas
- Pattern: `XxxForm` reusable component (with `create` prop) + separate `CreateXxxPage`/`EditXxxPage` pages
- Submit via generated React Query mutation hooks → invalidate queries → show notification → navigate

### Build

- Path alias: `@/` → `src/`
- Manual chunk splitting in `vite.config.ts` (map-vendor, editor-vendor, mantine-vendor, etc.)
- Dev proxy: `/api` → `http://localhost:8080` with `X-Forwarded-Host` for multi-tenancy

## Key Rules

- **Never edit `src/api/`** — it's generated. Run `pnpm generate-api` after backend OpenAPI changes.
- **Never hard-code links** — use `paths.xxx()` from `config/paths.ts`.
- **Never use `confirm()` or custom modals for confirmations** — use `ConfirmDialog`.
- **Never use SVG for icons** — use `@tabler/icons-react`.
- **Mantine UI exclusively** — check https://mantine.dev/llms.txt for docs.
- **Templated i18n keys require type annotations**: `t(\`status.\${x satisfies 'DRAFT' | 'PUBLISHED'}\`)` (validated by `pnpm i18n:lint`).
- **Frontend config comes from `/api/config` endpoint**, not `.env` files.
- **Logos**: `TeamAvatar` (with initials fallback) vs `EntityLogo` (no fallback).
- **`MediaEditor` needs `teamSlug` prop** for uploads (hidden during team creation).
