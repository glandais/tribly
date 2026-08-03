#!/usr/bin/env node
// Crawls every web route as a set of configured users (Playwright) and reports two SSR defects:
// missing prefetch (frontend/src/lib/prefetchAudit.ts's `[prefetch-audit]` console.warn) and
// hydration mismatches (entry-client.tsx's `[hydration]` console.error, or any uncaught pageerror).
// See scripts/routes-ssr.yml for the input format.
//
// Usage:
//   node scripts/ssr-audit.mjs --verify                # check routes-ssr.yml vs contracts/routes.yaml
//   node scripts/ssr-audit.mjs --sync                   # add missing route ids/params, drop stale ones
//   node scripts/ssr-audit.mjs --list                   # print the route/user/URL list, visit nothing
//   node scripts/ssr-audit.mjs [--url http://host] [--config path] [--out path] [--no-skip]
//                               [--screenshots dir]
//
// Every route/user check saves a full-page screenshot to <screenshots>/<userId>-<routeId>.png
// (default screenshots dir: <out>-screenshots/).
//
// One-time setup: from frontend/, `pnpm install` then `npx playwright install chromium`.
// The target server must be built/run with FRONTEND_PREFETCH_AUDIT=true (see vite.config.ts) —
// otherwise no [prefetch-audit] settle signal is ever emitted and every route falls back to the
// timeout below (reported once, not per route).

import { createRequire } from 'node:module'
import { fileURLToPath } from 'node:url'
import { readFileSync, writeFileSync, mkdirSync } from 'node:fs'
import path from 'node:path'

const scriptDir = path.dirname(fileURLToPath(import.meta.url))
const repoRoot = path.resolve(scriptDir, '..')
const frontendReq = createRequire(path.join(repoRoot, 'frontend', 'package.json'))
const YAML = frontendReq('yaml')

/** A problem in routes-ssr.yml — printed as-is, with no stack trace to bury it. */
class ConfigError extends Error {}

const CONTRACT_ROUTES_PATH = path.join(repoRoot, 'contracts', 'routes.yaml')
const DEFAULT_CONFIG_PATH = path.join(scriptDir, 'routes-ssr.yml')
const SETTLE_TIMEOUT_MS = 8000

// ---------- CLI args ----------

const argv = process.argv.slice(2)
const flag = (name) => argv.includes(`--${name}`)
const opt = (name, fallback) => {
  const i = argv.indexOf(`--${name}`)
  return i === -1 ? fallback : argv[i + 1]
}

const configPath = path.resolve(opt('config', DEFAULT_CONFIG_PATH))
const baseUrl = (opt('url', process.env.SSR_AUDIT_URL || 'http://localhost:8090') ?? '').replace(
  /\/$/,
  ''
)
const noSkip = flag('no-skip')

// ---------- contracts/routes.yaml ----------

/** The web routes of the contract, in declaration order, keyed by id. */
function loadContractRoutes() {
  const raw = YAML.parse(readFileSync(CONTRACT_ROUTES_PATH, 'utf8'))
  if (!raw || !Array.isArray(raw.routes)) {
    throw new Error('contracts/routes.yaml must define a top-level `routes:` array')
  }
  return raw.routes.filter((r) => r.web !== false)
}

function loadContractRouteIds() {
  return loadContractRoutes().map((r) => r.id)
}

/** Every `{param}` name any web route template uses, deduped, in first-seen order. */
function contractParamNames(contractRoutes) {
  const names = []
  for (const route of contractRoutes) {
    for (const name of route.params ?? []) {
      if (!names.includes(name)) names.push(name)
    }
  }
  return names
}

function templateFor(contractRoute, locale) {
  const p = contractRoute.path
  if (typeof p === 'string') return p
  return p?.[locale] ?? p?.en ?? null
}

// ---------- routes-ssr.yml ----------

// Doc.commentBefore text — no leading `#` (the yaml lib prepends it verbatim on stringify), only
// stamped onto a brand-new file. An existing file already carries its header as parsed comment
// nodes, which round-trip through --sync untouched; prepending this again would duplicate it.
const CONFIG_HEADER_COMMENT = ` SSR audit input — who to crawl every web route as, and what to look for.
 Verified/synced against contracts/routes.yaml (entries with web !== false) by
 \`node scripts/ssr-audit.mjs --verify\` / \`--sync\`. Never hand-edit paths.generated.ts from
 this file, or the other way around — the two are unrelated.

 URLs are NOT written here: each route's path template comes from contracts/routes.yaml and the
 script interpolates its \`{param}\` placeholders from \`params\` below. A route left with an
 unresolved placeholder fails the run at startup, before the browser opens — use \`skip:\` for a
 route that genuinely can't be covered.

 loginPath: path to the login form (default /login)
 locale:    which locale's template to take from contracts/routes.yaml (default en)

 params:  { name: value } — every \`{name}\` used by any web route template. \`--sync\` adds new
          ones as null; a null left in place fails the run.

 users:
   id:          short id, reused by routes[].users
   login:       email — omit for an anonymous user (no login step)
   passwordEnv: name of the env var holding the password (required when \`login\` is set)

 routes:
   id:     must match an id in contracts/routes.yaml
   users:  list of user ids to crawl this route as. Omit (or null) for every user — use it to
           restrict an authenticated-only or admin-only route.
   params: optional { name: value } overriding \`params\` for this route only (e.g. a team the
           test user actually administers).
   locale: optional per-route override of the top-level \`locale\`.
   path:   optional escape hatch — a literal path used verbatim, bypassing the template (query
           strings, one-off cases). Prefer \`params\`.
   skip:   optional — true, or a string explaining why, to exclude the route without deleting it.
           A skipped route still counts as covered for --verify/--sync. --no-skip overrides this
           and visits it anyway.`

function loadConfig() {
  let raw = {}
  try {
    raw = YAML.parse(readFileSync(configPath, 'utf8')) ?? {}
  } catch (err) {
    if (err.code !== 'ENOENT') throw err
  }
  return {
    loginPath: raw.loginPath || '/login',
    locale: raw.locale || 'en',
    params: raw.params && typeof raw.params === 'object' ? raw.params : {},
    users: Array.isArray(raw.users) ? raw.users : [],
    routes: Array.isArray(raw.routes) ? raw.routes : [],
  }
}

/**
 * A live YAML.Document, not a plain object — `--sync` edits its `routes` sequence node in place
 * (splice out stale items, `.add()` new ones) so untouched entries, including hand-written
 * per-route comments, survive a round trip. `YAML.stringify(plainObject)` would silently drop
 * every comment on every sync.
 */
function loadConfigDoc() {
  let text = ''
  try {
    text = readFileSync(configPath, 'utf8')
  } catch (err) {
    if (err.code !== 'ENOENT') throw err
  }
  const isNewFile = text.trim() === ''
  const doc = YAML.parseDocument(text)
  if (isNewFile) doc.commentBefore = CONFIG_HEADER_COMMENT
  if (doc.get('loginPath') === undefined) doc.set('loginPath', '/login')
  if (doc.get('locale') === undefined) doc.set('locale', 'en')
  if (doc.get('params') === undefined) doc.set('params', doc.createNode({}))
  if (doc.get('users') === undefined) doc.set('users', doc.createNode([]))
  if (doc.get('routes') === undefined) doc.set('routes', doc.createNode([]))
  return doc
}

function writeConfigDoc(doc) {
  writeFileSync(configPath, doc.toString({ lineWidth: 0 }))
}

// ---------- --verify / --sync ----------

function diffRouteIds(contractIds, configRouteIds) {
  const contractSet = new Set(contractIds)
  const configSet = new Set(configRouteIds)
  const missing = contractIds.filter((id) => !configSet.has(id))
  const stale = configRouteIds.filter((id) => !contractSet.has(id))
  return { missing, stale }
}

function runVerify() {
  const contractRoutes = loadContractRoutes()
  const contractIds = contractRoutes.map((r) => r.id)
  const config = loadConfig()
  const { missing, stale } = diffRouteIds(
    contractIds,
    config.routes.map((r) => r.id)
  )
  const missingParams = contractParamNames(contractRoutes).filter(
    (name) => !(name in config.params)
  )
  const rel = path.relative(repoRoot, configPath)
  if (missing.length === 0 && stale.length === 0 && missingParams.length === 0) {
    console.log(`${rel} is in sync with contracts/routes.yaml (${contractIds.length} routes).`)
    return
  }
  if (missingParams.length > 0) {
    console.error(`Missing from ${rel} \`params\` (${missingParams.length}):`)
    for (const name of missingParams) console.error(`  - ${name}`)
  }
  if (missing.length > 0) {
    console.error(`Missing from ${rel} (${missing.length}):`)
    for (const id of missing) console.error(`  - ${id}`)
  }
  if (stale.length > 0) {
    console.error(`Stale in ${rel}, no longer a web route (${stale.length}):`)
    for (const id of stale) console.error(`  - ${id}`)
  }
  console.error('\nRun with --sync to fix automatically.')
  process.exitCode = 1
}

function runSync() {
  const contractRoutes = loadContractRoutes()
  const contractIds = contractRoutes.map((r) => r.id)
  const doc = loadConfigDoc()
  const routesSeq = doc.get('routes')
  const paramsMap = doc.get('params')
  const configRouteIds = routesSeq.items.map((item) => item.get('id'))
  const { missing, stale } = diffRouteIds(contractIds, configRouteIds)

  const staleSet = new Set(stale)
  for (let i = routesSeq.items.length - 1; i >= 0; i--) {
    if (staleSet.has(routesSeq.items[i].get('id'))) routesSeq.items.splice(i, 1)
  }
  for (const id of missing) {
    routesSeq.add(doc.createNode({ id }))
  }

  // Params are only ever added, never removed: a value that no template uses today costs nothing,
  // and dropping it would silently lose a hand-picked slug the next contract change needs again.
  const addedParams = contractParamNames(contractRoutes).filter((name) => !paramsMap.has(name))
  for (const name of addedParams) paramsMap.set(name, null)

  writeConfigDoc(doc)
  const rel = path.relative(repoRoot, configPath)
  console.log(
    `Added ${missing.length}, removed ${stale.length}. ${rel} now has ${routesSeq.items.length} routes.`
  )
  if (addedParams.length > 0) {
    console.log(`Added ${addedParams.length} param(s) as null: ${addedParams.join(', ')}`)
  }
  if (missing.length > 0 || addedParams.length > 0) {
    console.log(
      'Fill in every null `params` value (and restrict `users:` where a route needs auth) before running the audit.'
    )
  }
}

// ---------- audit run ----------

/**
 * The concrete path for a route: its contract template with every `{param}` interpolated from
 * `params` (route-level values winning over the global ones). Reports `{ url: null, reason }`
 * for the caller to turn into a startup failure — an unresolved path is a config error, not an
 * audit result: crawling the other 60 routes and mentioning it in the report would let it rot
 * unnoticed. A route that genuinely can't be covered gets `skip:`, which never resolves at all.
 */
function resolveRouteUrl(route, contractRoute, config) {
  if (route.path) return { url: route.path }
  if (!contractRoute) return { url: null, reason: 'no such id in contracts/routes.yaml' }

  const locale = route.locale || config.locale
  const template = templateFor(contractRoute, locale)
  if (!template) return { url: null, reason: `no "${locale}" path in contracts/routes.yaml` }

  const params = { ...config.params, ...(route.params ?? {}) }
  const missing = []
  const url = template.replace(/\{(\w+)\}/g, (_, name) => {
    const value = params[name]
    if (value === undefined || value === null || value === '') {
      missing.push(name)
      return `{${name}}`
    }
    return String(value)
  })
  if (missing.length > 0) {
    return { url: null, reason: `unresolved param(s): ${missing.join(', ')}` }
  }
  return { url }
}

function usersForRoute(route, config, userById) {
  if (route.users === undefined || route.users === null) return config.users
  if (!Array.isArray(route.users)) {
    throw new Error(`route "${route.id}": \`users\` must be a list of user ids (or null for all)`)
  }
  return route.users.map((id) => {
    const user = userById.get(id)
    if (!user) throw new Error(`route "${route.id}": unknown user "${id}"`)
    return user
  })
}

function resolvePassword(user) {
  if (!user.login) return null
  if (!user.passwordEnv) {
    throw new Error(`user "${user.id}": has \`login\` but no \`passwordEnv\``)
  }
  const password = process.env[user.passwordEnv]
  if (!password) {
    throw new Error(`user "${user.id}": env var ${user.passwordEnv} is not set`)
  }
  return password
}

async function login(page, loginPath, user, password) {
  await page.goto(`${baseUrl}${loginPath}`, { waitUntil: 'domcontentloaded' })
  await page.locator('input[name="email"]').fill(user.login)
  await page.locator('input[name="password"]').fill(password)
  const before = page.url()
  await page.locator('button[type="submit"]').first().click()
  try {
    await page.waitForFunction((prev) => window.location.href !== prev, before, {
      timeout: 10000,
    })
  } catch {
    throw new Error(`login as "${user.id}" did not redirect away from ${loginPath} within 10s`)
  }
}

/** Any of the three lines installPrefetchAudit() ever logs — the settle/disarm signal. */
function isPrefetchAuditSettleLine(text) {
  return text.startsWith('[prefetch-audit] route')
}

function classifyConsoleMessage(message) {
  const text = message.text()
  if (text.startsWith('[hydration]')) return { category: 'hydration', text }
  if (
    message.type() === 'warning' &&
    text.startsWith('[prefetch-audit]') &&
    text.includes('not covered by route prefetch')
  ) {
    return { category: 'prefetch', text }
  }
  if (message.type() === 'error') return { category: 'console-error', text }
  return null
}

const MAX_DISTINCT_ISSUES = 20

/**
 * A crashing hydration loop can fire the *same* pageerror hundreds of times (React error #185 is
 * the classic one) — collapse repeats into a count instead of flooding the console/report, and cap
 * the number of distinct messages so a route with many different errors doesn't blow up either.
 */
function makeIssueCollector() {
  const byKey = new Map()
  let dropped = 0
  return {
    add(category, text) {
      const key = `${category}:${text}`
      const existing = byKey.get(key)
      if (existing) {
        existing.count += 1
        return
      }
      if (byKey.size >= MAX_DISTINCT_ISSUES) {
        dropped += 1
        return
      }
      byKey.set(key, { category, text, count: 1 })
    },
    list() {
      const issues = [...byKey.values()]
      if (dropped > 0) {
        issues.push({
          category: 'console-error',
          text: `…and ${dropped} more distinct message(s) dropped (cap: ${MAX_DISTINCT_ISSUES})`,
          count: 1,
        })
      }
      return issues
    },
  }
}

/**
 * Never throws — a crashed/hung page (e.g. a hydration error loop) becomes a `navigation` issue
 * on this route instead of an uncaught rejection that would abort the whole run and lose every
 * result collected so far. `crashed: true` tells the caller the page itself may be unusable and
 * should be recycled before the next route.
 */
async function auditRoute(page, url, screenshotPath) {
  const collector = makeIssueCollector()
  let settled = false

  const onConsole = (message) => {
    if (isPrefetchAuditSettleLine(message.text())) settled = true
    const classified = classifyConsoleMessage(message)
    if (classified) collector.add(classified.category, classified.text)
  }
  const onPageError = (error) => {
    collector.add('hydration', `pageerror: ${error.message}`)
  }

  page.on('console', onConsole)
  page.on('pageerror', onPageError)
  let crashed = false
  try {
    await page.goto(url, { waitUntil: 'domcontentloaded' })
    const deadline = Date.now() + SETTLE_TIMEOUT_MS
    while (!settled && Date.now() < deadline) {
      await new Promise((resolve) => setTimeout(resolve, 200))
    }
    try {
      await page.screenshot({ path: screenshotPath, fullPage: true })
    } catch (err) {
      collector.add('console-error', `screenshot failed: ${err.message}`)
    }
  } catch (err) {
    crashed = true
    collector.add('navigation', `navigation failed: ${err.message}`)
  } finally {
    page.off('console', onConsole)
    page.off('pageerror', onPageError)
  }

  return { issues: collector.list(), settled, crashed }
}

/** Throws on the first config problem found, listing every one of them — fix them all in one pass. */
function buildWorkItems(config, contractById) {
  const items = []
  const errors = []
  const userById = new Map(config.users.map((u) => [u.id, u]))
  let skipped = 0

  for (const route of config.routes) {
    let users
    try {
      users = usersForRoute(route, config, userById)
    } catch (err) {
      errors.push(err.message)
      continue
    }
    if (users.length === 0) continue

    if (Boolean(route.skip) && !noSkip) {
      skipped += users.length
      continue
    }
    const { url, reason } = resolveRouteUrl(route, contractById.get(route.id), config)
    if (!url) {
      errors.push(`route "${route.id}": ${reason}`)
      continue
    }
    for (const user of users) items.push({ route, user, url })
  }

  if (errors.length > 0) {
    throw new ConfigError(
      `${path.relative(repoRoot, configPath)} has ${errors.length} unusable route(s):\n` +
        errors.map((e) => `  - ${e}`).join('\n') +
        '\n\nFill in the missing `params` values, or mark the route `skip:` with a reason.'
    )
  }
  return { items, skipped }
}

function buildMarkdownReport({
  generatedAt,
  baseUrl,
  results,
  loginFailures,
  skipped,
  everSettled,
}) {
  const lines = [
    `# SSR audit report`,
    ``,
    `Generated ${generatedAt} against ${baseUrl}.`,
    ``,
    `- ${results.length} route/user checks run`,
    `- ${skipped} route/user checks skipped (\`skip\`)`,
    `- ${loginFailures.length} login failure(s)`,
    `- ${results.filter((r) => r.issues.length > 0).length} check(s) with issues`,
    ``,
  ]
  if (!everSettled) {
    lines.push(
      '> No `[prefetch-audit]` settle signal was ever observed — is `FRONTEND_PREFETCH_AUDIT=true` ' +
        'set on the target? Every check below fell back to the 8s timeout, so prefetch gaps may be ' +
        'under-reported.',
      ''
    )
  }
  if (loginFailures.length > 0) {
    lines.push('## Login failures', '')
    for (const f of loginFailures) lines.push(`- **${f.userId}**: ${f.error}`)
    lines.push('')
  }

  const byRoute = new Map()
  for (const r of results) {
    if (r.issues.length === 0) continue
    if (!byRoute.has(r.routeId)) byRoute.set(r.routeId, [])
    byRoute.get(r.routeId).push(r)
  }

  if (byRoute.size > 0) {
    lines.push('## Issues by route', '')
    for (const [routeId, entries] of byRoute) {
      lines.push(`### ${routeId}`, '')
      for (const entry of entries) {
        lines.push(`- **${entry.userId}** — ${entry.url} (\`${entry.screenshot}\`)`)
        for (const issue of entry.issues) {
          const countSuffix = issue.count > 1 ? ` (x${issue.count})` : ''
          lines.push(`  - \`${issue.category}\`${countSuffix}: ${issue.text}`)
        }
      }
      lines.push('')
    }
  } else {
    lines.push('No issues found.', '')
  }

  lines.push('## All checks', '', '| Route | User | Status | Screenshot |', '| --- | --- | --- | --- |')
  for (const r of results) {
    const status = r.issues.length === 0 ? 'RAS' : `FAIL (${r.issues.length})`
    lines.push(`| ${r.routeId} | ${r.userId} | ${status} | \`${r.screenshot}\` |`)
  }
  lines.push('')

  return lines.join('\n')
}

/** Dry run: the exact route/user/URL list the audit would visit, without launching a browser. */
function runList() {
  const config = loadConfig()
  const contractById = new Map(loadContractRoutes().map((r) => [r.id, r]))
  const { items, skipped } = buildWorkItems(config, contractById)
  for (const item of items) {
    console.log(`${item.user.id.padEnd(12)} ${item.route.id.padEnd(22)} ${item.url}`)
  }
  console.log(`\n${items.length} check(s), ${skipped} skipped.`)
}

async function runAudit() {
  const config = loadConfig()
  if (config.users.length === 0) {
    throw new ConfigError(`${path.relative(repoRoot, configPath)} defines no users`)
  }
  const contractById = new Map(loadContractRoutes().map((r) => [r.id, r]))
  const { items, skipped } = buildWorkItems(config, contractById)

  const generatedAt = new Date().toISOString()
  const outBase = opt('out', `ssr-audit-report-${generatedAt.replace(/[:.]/g, '-')}`)
  const screenshotsDir = path.resolve(opt('screenshots', `${outBase}-screenshots`))
  mkdirSync(screenshotsDir, { recursive: true })

  const { chromium } = frontendReq('playwright')
  const browser = await chromium.launch()

  const results = []
  const loginFailures = []
  let everSettled = false

  try {
    for (const user of config.users) {
      const userItems = items.filter((i) => i.user.id === user.id)
      if (userItems.length === 0) continue

      const context = await browser.newContext()
      let page = await context.newPage()

      if (user.login) {
        try {
          console.log(`[${user.id}] login as ${user.login}…`)
          const password = resolvePassword(user)
          await login(page, config.loginPath, user, password)
        } catch (err) {
          console.error(`[login] ${user.id}: ${err.message}`)
          loginFailures.push({ userId: user.id, error: err.message })
          await context.close()
          continue
        }
      }

      for (const [index, { route, url }] of userItems.entries()) {
        const fullUrl = `${baseUrl}${url}`
        const screenshotPath = path.join(screenshotsDir, `${user.id}-${route.id}.png`)
        // Printed *before* the goto: a route that hangs or crashes the page is otherwise silent
        // until the 8s settle timeout, with nothing saying which one is stuck.
        console.log(`[${user.id} ${index + 1}/${userItems.length}] ${route.id} — ${fullUrl}`)
        const { issues, settled, crashed } = await auditRoute(page, fullUrl, screenshotPath)
        if (settled) everSettled = true
        if (issues.length === 0) {
          console.log(`      RAS`)
        } else {
          console.warn(`      FAIL — ${issues.length} distinct issue(s)`)
          for (const issue of issues) {
            const countSuffix = issue.count > 1 ? ` (x${issue.count})` : ''
            console.warn(`        [${issue.category}]${countSuffix} ${issue.text}`)
          }
        }
        results.push({
          routeId: route.id,
          userId: user.id,
          url,
          issues,
          screenshot: path.relative(repoRoot, screenshotPath),
        })

        // A crashed page (e.g. a hydration error loop) can stay unresponsive — every subsequent
        // goto() on it would then time out too. Recycle it so the rest of this user's routes get
        // a clean start instead of cascading into the same failure.
        if (crashed) {
          await page.close().catch(() => {})
          page = await context.newPage()
        }
      }

      await context.close()
    }
  } finally {
    await browser.close()
  }

  const report = { generatedAt, baseUrl, results, loginFailures, skipped, everSettled }
  const markdown = buildMarkdownReport(report)

  writeFileSync(`${outBase}.json`, JSON.stringify(report, null, 2))
  writeFileSync(`${outBase}.md`, markdown)
  console.log(`\nReport written to ${outBase}.json / ${outBase}.md`)
  console.log(`Screenshots written to ${path.relative(repoRoot, screenshotsDir)}/`)

  const failing = results.filter((r) => r.issues.length > 0)
  if (failing.length > 0 || loginFailures.length > 0) {
    process.exitCode = 1
  }
}

// ---------- main ----------

async function main() {
  if (flag('verify')) return runVerify()
  if (flag('sync')) return runSync()
  if (flag('list')) return runList()
  await runAudit()
}

main().catch((err) => {
  console.error(err instanceof ConfigError ? err.message : err.stack || err.message)
  process.exitCode = 1
})
