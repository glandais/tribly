import { setWorkerUrl } from 'maplibre-gl'
import workerUrl from 'maplibre-gl/dist/maplibre-gl-worker.mjs?worker&url'

// maplibre-gl resolves its worker via `new URL('./maplibre-gl-worker.mjs', import.meta.url)`
// inside its own bundled module — a dynamic expression that doesn't survive bundling: Rollup
// can't trace it to emit the file, and even if it were copied by hand, import.meta.url inside
// our map-vendor chunk no longer points next to it. `?worker&url` routes the worker (and the
// sibling maplibre-gl-shared.mjs it imports) through Vite's worker pipeline into a self-contained
// chunk with a real URL, which setWorkerUrl then points maplibre-gl at explicitly — the pattern
// documented at https://maplibre.org/maplibre-gl-js/docs/ for bundlers Vite/webpack/esbuild/Rollup.
setWorkerUrl(workerUrl)
