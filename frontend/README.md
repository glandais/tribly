# Pédalons Frontend

React SPA for the Pédalons cycling team management platform. Built with TypeScript, React 19, Vite, and Mantine UI.

## Prerequisites

- Node.js 20+
- pnpm 10+
- Backend running on `localhost:8080` (see root [README](../README.md))
- Docker infrastructure up (`docker compose up -d` from repo root)

## Getting Started

```bash
pnpm install
pnpm dev
```

The dev server starts at https://localhost:5173 and proxies `/api` requests to the backend.

### HTTPS Setup (required for WebAuthn/passkeys)

Generate local certificates with [mkcert](https://github.com/FiloSottile/mkcert):

```bash
mkcert -install              # one time: install local CA
mkcert localhost 127.0.0.1   # generates localhost+1.pem and localhost+1-key.pem
```

## Scripts

| Script | Description |
|--------|-------------|
| `pnpm dev` | Dev server with HMR |
| `pnpm build` | TypeScript check + production build |
| `pnpm preview` | Preview production build locally |
| `pnpm generate-api` | Regenerate API client from OpenAPI contract |
| `pnpm generate-routes` | Regenerate path builders + deeplinks from `../contracts/routes.yaml` |
| `pnpm lint` | ESLint |
| `pnpm format` | Prettier |
| `pnpm test` | Vitest (watch mode) |
| `pnpm test:coverage` | Vitest with coverage report |
| `pnpm i18n:lint` | Validate i18n key usage |
| `pnpm i18n:extract` | Extract new translation keys |

## API Client Generation

The API layer is generated from the backend's OpenAPI contract using [Orval](https://orval.dev/):

```bash
# 1. Generate OpenAPI spec (from backend/)
cd ../backend && mvn package -DskipTests

# 2. Generate TypeScript client (from frontend/)
pnpm generate-api
```

This produces React Query hooks, TypeScript DTOs, and Zod schemas in `src/api/`. **Do not edit generated files.**

## Stack

| Concern | Library |
|---------|---------|
| UI Components | [Mantine](https://mantine.dev/) 8 |
| Routing | React Router 7 |
| Server State | TanStack React Query 5 |
| Client State | Zustand 5 |
| Forms | Mantine Form + Zod validation |
| Rich Text | Tiptap 3 |
| Maps | MapLibre GL + react-map-gl |
| Charts | Chart.js + react-chartjs-2 |
| Calendar | FullCalendar 7 |
| i18n | i18next (French default) |
| Icons | @tabler/icons-react |

## Project Structure

```
src/
├── api/              # Generated (Orval) — do not edit
│   ├── dto/          # TypeScript types
│   ├── endpoints/    # React Query hooks per API tag
│   └── zod/          # Zod validation schemas
├── components/       # Organized by domain (common/, team/, ride/, route/, post/, trip/, ...)
├── config/           # paths.ts (re-export), paths.generated.ts (generated), locale-context.ts, routes.config.ts, appConfig.ts
├── hooks/            # Custom React Query wrappers (useAuth, useComments, usePaginatedQuery, ...)
├── lib/              # axiosInstance.ts (auth, error handling), apiUtils.ts
├── locales/          # fr/ (default), en/ — single "common" namespace per language
├── pages/            # Route-level page components
├── store/            # Zustand stores (authStore, preferencesStore)
├── types/            # Shared TypeScript types
└── utils/            # Utility functions
```

## Key Conventions

- **Navigation**: Always use `paths.xxx()` from `config/paths.ts` — never hard-code URLs. Returns the URL in the user's current locale (driven by i18next).
- **Adding a route**: Edit `../contracts/routes.yaml`, then `pnpm generate-routes`. Register the page in `config/routes.config.ts` via `pathVariants.xxx()`. See [../APP_LINKS.md](../APP_LINKS.md).
- **Confirmations**: Always use the `ConfirmDialog` component — never `window.confirm()` or custom modals.
- **Icons**: Always use `@tabler/icons-react` — never inline SVGs.
- **i18n**: French is the default language. Templated keys need type annotations: `` t(`status.${x satisfies 'DRAFT' | 'PUBLISHED'}`) ``.
- **Config**: Runtime config from `/api/config` endpoint — no `.env` files for app config.
- **Logos**: `TeamAvatar` (with initials fallback) vs `EntityLogo` (no fallback).
- **Forms**: Mantine `useForm` + Zod schemas from `api/zod/`. Separate `XxxForm` component + `CreateXxxPage`/`EditXxxPage` pages.
