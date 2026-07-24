import fs from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'
import express from 'express'
import compression from 'compression'
import { createProxyMiddleware } from 'http-proxy-middleware'

const __dirname = path.dirname(fileURLToPath(import.meta.url))
const isProduction = process.env.NODE_ENV === 'production'
const port = process.env.PORT || 3000
const apiTarget = process.env.API_BASE_URL || 'http://localhost:8080'

// apple-app-site-association lives under public/ in dev, and is copied into
// dist/client/ by the build. It must be served with a JSON content type (nginx
// did this via nginx-spa.conf); express.static would serve it as octet-stream.
const wellKnownDir = isProduction
  ? path.resolve(__dirname, 'dist/client/.well-known')
  : path.resolve(__dirname, 'public/.well-known')

async function createServer() {
  const app = express()

  // Behind traefik (prod) / http-proxy-middleware (dev) — trust X-Forwarded-* so
  // req.protocol and req.ip reflect the original client.
  app.set('trust proxy', true)

  // API proxy — must be before Vite middleware. In prod, traefik routes /api
  // straight to the backend and this never runs.
  app.use(
    '/api',
    createProxyMiddleware({
      target: apiTarget,
      changeOrigin: true,
      on: {
        proxyReq: (proxyReq, req) => {
          const host = req.headers.host || req.headers[':authority'] || 'localhost'
          proxyReq.setHeader('X-Forwarded-Host', host)
          proxyReq.setHeader('X-Forwarded-Proto', req.protocol)
        },
        error: (err, req, res) => {
          console.error('[proxy] API proxy error:', err.message)
          if (res.writeHead && !res.headersSent) {
            res.writeHead(502, { 'Content-Type': 'application/json' })
            res.end(JSON.stringify({ error: 'API unavailable' }))
          } else {
            res.end()
          }
        },
      },
    })
  )

  // Serve apple-app-site-association as JSON (parity with nginx-spa.conf). Must be
  // registered before the static/Vite middleware so it wins the content type.
  // `dotfiles: 'allow'` is required: send (express 5) 404s any path with a segment
  // starting with a dot, so /.well-known/* is invisible to sendFile without it.
  app.get('/.well-known/apple-app-site-association', (_req, res) => {
    res.type('application/json')
    res.sendFile(
      path.join(wellKnownDir, 'apple-app-site-association'),
      { dotfiles: 'allow' },
      (err) => {
        if (err && !res.headersSent) {
          res.status(404).end()
        }
      }
    )
  })

  // Everything else under /.well-known (assetlinks.json for Android App Links and
  // passkey origins). Mounted on its own path so the dot segment is stripped before
  // send sees it; extensions still drive the content type.
  app.use('/.well-known', express.static(wellKnownDir, { index: false }))

  let vite
  let template
  let render

  if (isProduction) {
    app.use(compression())
    app.use(
      express.static(path.resolve(__dirname, 'dist/client'), {
        index: false,
        setHeaders: (res, filePath) => {
          // Hashed build assets are immutable and safe to cache for a long time.
          if (filePath.includes(`${path.sep}assets${path.sep}`)) {
            res.setHeader('Cache-Control', 'public, max-age=2678400, immutable')
          } else {
            res.setHeader('Cache-Control', 'public, max-age=2678400')
          }
        },
      })
    )
    template = fs.readFileSync(path.resolve(__dirname, 'dist/client/index.html'), 'utf-8')
    const serverModule = await import(path.resolve(__dirname, 'dist/server/entry-server.js'))
    render = serverModule.render
  } else {
    const { createServer: createViteServer } = await import('vite')
    vite = await createViteServer({
      server: { middlewareMode: true },
      appType: 'custom',
    })
    app.use(vite.middlewares)
  }

  app.get('/health', (_, res) => res.send('ok'))

  // Express 5 / path-to-regexp v8: bare '*' throws — the catch-all wildcard must
  // be named ('*splat').
  app.use('*splat', async (req, res) => {
    const url = req.originalUrl

    try {
      let currentTemplate, currentRender

      if (isProduction) {
        currentTemplate = template
        currentRender = render
      } else {
        currentTemplate = fs.readFileSync(path.resolve(__dirname, 'index.html'), 'utf-8')
        currentTemplate = await vite.transformIndexHtml(url, currentTemplate)
        const module = await vite.ssrLoadModule('/src/entry-server.tsx')
        currentRender = module.render
      }

      // Filter to single-value headers — render() types headers as Record<string, string>, but Express req.headers can contain string[] values (e.g. set-cookie)
      const result = await currentRender(url, Object.fromEntries(
        Object.entries(req.headers).filter(([, v]) => typeof v === 'string')
      ))

      // Handle redirects
      if (result.redirect) {
        res.redirect(result.statusCode || 302, result.redirect)
        return
      }

      const { html: appHtml, dehydratedState, lang, head } = result

      // Escape '<' to prevent XSS via </script> injection in inline JSON
      let stateScript = ''
      try {
        stateScript = dehydratedState
          ? `<script>window.__REACT_QUERY_STATE__=${JSON.stringify(dehydratedState).replace(/</g, '\\u003c')}</script>`
          : ''
      } catch (serErr) {
        console.error(`[SSR] Failed to serialize dehydrated state for "${url}":`, serErr)
        // Continue without SSR state — client will refetch
      }

      let finalHtml = currentTemplate
        .replace('<!--ssr-outlet-->', appHtml)
        .replace('<!--ssr-state-->', stateScript)
        .replace('<!--ssr-head-->', head || '')

      // The SSR-built link-preview block (injected above) owns the dynamic <title>. index.html also
      // ships a static fallback <title> for the JS-less dev SPA tab; once the block is present there
      // are two, so drop the LAST one (the static fallback) — leaving exactly one, dynamic title.
      if (head) {
        finalHtml = finalHtml.replace(/\n?\s*<title>[^<]*<\/title>(?![\s\S]*<title>)/, '')
      }

      // Reflect the request-resolved language on the root <html> element.
      if (lang) {
        finalHtml = finalHtml.replace('<html lang="en">', `<html lang="${lang}">`)
      }

      res
        .status(result.statusCode || 200)
        .set({ 'Content-Type': 'text/html', 'Cache-Control': 'no-store' })
        .send(finalHtml)
    } catch (e) {
      if (!isProduction && vite) {
        vite.ssrFixStacktrace(e)
      }
      console.error(e.stack || e)
      res
        .status(500)
        .set({ 'Content-Type': 'text/html' })
        .send(isProduction ? '<h1>Internal Server Error</h1>' : `<pre>${String(e.stack || e.message || e).replace(/</g, '&lt;').replace(/>/g, '&gt;')}</pre>`)
    }
  })

  const server = app.listen(port, () => {
    console.log(`Server running at http://localhost:${port}`)
  })
  server.on('error', (err) => {
    console.error(`Fatal: failed to bind to port ${port}:`, err.message)
    process.exit(1)
  })
}

process.on('unhandledRejection', (reason) => {
  console.error('[SSR] Unhandled promise rejection:', reason)
})

process.on('uncaughtException', (err) => {
  console.error('[SSR] Uncaught exception — server is in an unknown state:', err)
  process.exit(1)
})

createServer().catch((err) => {
  console.error('Fatal: failed to start SSR server', err)
  process.exit(1)
})
