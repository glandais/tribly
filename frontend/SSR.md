# SSR — architecture and hard-won findings

How the frontend is server-rendered, and the non-obvious problems hit while building it
(2026-07). Read this before touching `server.js`, the entry files, or anything they eagerly
import. The short invariants live in [CLAUDE.md](CLAUDE.md#ssr-server-side-rendering); this
file explains the *why* behind them.

## Architecture in one paragraph

`server.js` (Express 5) serves static assets and calls `render(url, headers)` from
`src/entry-server.tsx` for every page request. The render runs React Router in **library
mode** (`createStaticHandler` → `handler.query()` → `StaticRouterProvider`) inside a
per-request `AsyncLocalStorage` scope, prefetches route data into a per-request
`QueryClient` (Orval-generated `prefetchXxxQuery` declared as `prefetch` in
`routes.config.ts`), renders with React's **static prerender API**, and returns
`{ html, dehydratedState, statusCode, lang }`. `server.js` injects those into
`index.html` (`<!--ssr-outlet-->`, `<!--ssr-state-->`, `<html lang>`).
`src/entry-client.tsx` hydrates the query cache, seeds the app config, and calls
`hydrateRoot`. SSR is **anonymous**: no cookies or `Authorization` ever reach the backend
from the server; authenticated content renders client-side after hydration.

Deliberate non-choices: React Router framework mode (no retrofit path for a declarative
SPA), TanStack Start (full router migration), Vike (replaces a working ~150-line server),
streaming (complicates React Query dehydration for no SEO gain), Vite Environment API
(still experimental; we use `ssrLoadModule`).

## Finding 1 — `renderToString` renders every lazy page as its fallback

All route pages are `React.lazy`. `renderToString` flushes synchronously: a lazy component
is always pending on that first (and only) pass, so **every page SSR'd as the route-level
`<Loader/>`** — HTML looked plausible, dehydrated state was correct, but no content.
Nothing failed loudly; only `curl | grep` for actual content caught it.

The escalation path that ended at the right API:

1. `renderToPipeableStream` + `onAllReady` waits for lazy chunks and suspended data — but
   completed Suspense boundaries **larger than `progressiveChunkSize` (~12 KB) are
   "outlined"**: emitted as a `<template>` placeholder + hidden segment + inline `$RC`
   relocation script. Content is in the document but not inline — fine for JS-executing
   crawlers, wrong for anything reading raw HTML.
2. Final: `prerenderToNodeStream` from **`react-dom/static`** (React 19's prerender API,
   built for exactly this) with `progressiveChunkSize: Number.MAX_SAFE_INTEGER` so
   everything is inlined. A 10 s `AbortController` guard degrades still-pending boundaries
   to their fallbacks instead of hanging the request.

See `renderAppToString()` in `entry-server.tsx`.

## Finding 2 — a Suspense boundary can swallow a crash and render as "loading"

While diagnosing Finding 1, pages *still* rendered the fallback after the prerender switch.
Root cause was unrelated: `utils/dateFormat.ts` reads its date pattern via the **global**
i18next instance (`i18n.t('dateFormats.dateTime', …)`), which was uninitialized on the
server — `t()` returned `undefined`, `date-fns.format()` threw, and the error surfaced as…
the same Suspense fallback (boundary error → client-render marker `<!--$!-->`), with
**nothing in the server log** under the production React build. Two different root causes,
one identical symptom.

Fixes and rules that came out of this:

- ~15 modules legitimately call global `t()` at render time, so the **global i18next
  instance is now initialized on the server** (`i18n/index.ts`): pinned `lng: 'fr'`,
  `initAsync: false` (synchronous init — note the option is `initAsync` in i18next ≥ 24,
  not `initImmediate`), and **never mutated**. Per-request language lives only in the
  `createServerI18n()` instances passed through `I18nextProvider`. Call sites that need the
  request language must pass `{ lng }` explicitly (as `useFormattedDate` does).
- When a page SSRs as a spinner, **test in dev mode** (`node server.js` without
  `NODE_ENV=production`): the development React build prints the real error with a
  component stack; the production build stays silent.
- The distinct boundary markers in SSR output are diagnostic gold: `<!--$-->` complete,
  `<!--$?-->` pending/outlined, `<!--$!-->` errored (will client-render).

## Finding 3 — server and client must render the *same component tree*, not just the same HTML

Hydration produced page-wide mismatches on every Mantine `id` (`mantine-_R_7_` vs
`mantine-_R_3b_`). React's `useId` derives ids from **tree position**, so any server-only
or client-only wrapper — even a context provider or a component that renders `null` —
shifts every generated id below it. We had three such asymmetries: `I18nextProvider`
(server only), `<Notifications/>` (client only), `ErrorBoundary`/`AuthEffects` (client
only).

Fix: both entries render the exact same structure through shared components —
`src/AppProviders.tsx` (I18next → Mantine → Notifications → QueryClient) and `AppFrame` in
`src/App.tsx` (ErrorBoundary → AuthEffects → router provider). **Any structural change to
the provider stack must go through these two components.** The client router is also
created with `window.__staticRouterHydrationData` (injected by `StaticRouterProvider`) so
it starts initialized instead of rendering a pre-init state that differs from the server.

## Finding 4 — client-persisted state must not influence the first client render

The color-scheme toggle rendered sun vs moon from `useComputedColorScheme`. The server
doesn't know the user's stored scheme, so any user with `mantine-color-scheme-value: dark`
in localStorage got a hydration mismatch. Two traps inside the trap:

- `useComputedColorScheme('light', { getInitialValueInEffect: true })` does **not** help
  when a concrete scheme is stored: the option only defers `matchMedia` resolution of
  `'auto'`; a stored `'dark'` is read synchronously on the first render.
- The classic `const [mounted, setMounted] = useState(false); useEffect(() => setMounted(true))`
  gate is rejected by our lint (`react-hooks/set-state-in-effect`).

The pattern that works and lints clean (see `ColorSchemeSwitcher.tsx`):

```tsx
const hydrated = useSyncExternalStore(
  () => () => {},   // never notifies
  () => true,       // client snapshot
  () => false       // server snapshot — also used for the hydration render
)
// render the server's default until `hydrated`, then the real value
```

Apply this to any render output derived from localStorage / matchMedia / other
client-persisted state. (Accepted cosmetic consequence of anonymous SSR: a brief
post-hydration swap for users whose stored preference differs from the server default —
same deal for a stored language vs `Accept-Language`.)

## Finding 5 — assorted sharp edges

- **Express 5 catch-all**: `app.use('*', …)` throws (`path-to-regexp` v8). Use a named
  wildcard (`'*splat'` / `'/{*splat}'`).
- **404s**: the `'*'` catch-all *matches*, so the static handler reports 200 for unknown
  URLs. `entry-server` maps a leaf match of `path: '*'` to `statusCode: 404`.
- **Server `QueryClient` `gcTime` must not be 0** (TanStack-documented dehydration
  footgun) — we use 2000 ms (`lib/queryClient.ts`).
- **Prefetch params must byte-match the page hook's query key** (e.g. HomePage's
  `{ page: 0, size: 12 }`), or the dehydrated entry is dead weight and the page refetches.
  Verify: load the page, check the network tab for an immediate duplicate request.
- **`node:async_hooks` must not leak into the client bundle**: `lib/requestContext.ts`
  (SSR-only) is bridged through the client-safe `lib/ssrContext.ts` getters
  (`getSSRHeaders/getSSRLocale/getSSRConfig`); only `entry-server.tsx` may import
  `requestContext`.
- **i18next option rename**: synchronous init is `initAsync: false`, not
  `initImmediate: false` (typecheck catches it).

## Verifying SSR end-to-end

Static checks pass ≠ SSR works — every finding above survived typecheck, lint, tests and
build. The checks that actually catch regressions:

```bash
# against the local prod-replica stack (see repo docs; from a worktree: docker compose -p tribly)
curl -s -H 'Accept-Language: fr' http://localhost:8090/ | grep -c 'mantine-Loader-root'   # 0
curl -s http://localhost:8090/ | grep -c '<!--\$[?!]-->'                                   # 0 (no pending/errored boundaries)
curl -s http://localhost:8090/equipes/<slug> | grep '<title>'                              # real content, not fallback
curl -so /dev/null -w '%{http_code}' http://localhost:8090/nonexistent                     # 404
curl -s -H 'Cookie: refresh_token=X' http://localhost:8090/profil | grep -c 'X'           # 0 (no auth leakage)
```

Then load `/` in a browser **with a stored dark scheme and a logged-in session** and check
the console for `[hydration]` errors — the worst mismatches only reproduce with
client-persisted state present.
