import { readFileSync } from 'node:fs'
import { fileURLToPath } from 'node:url'
import { parse } from 'yaml'

/**
 * The UI routes as the repository declares them: contracts/routes.yaml (their paths) and
 * routes.config.ts (the guard in front of each one).
 */

const repoFile = (path: string) => fileURLToPath(new URL(`../../../${path}`, import.meta.url))

export interface ContractRoute {
  id: string
  /** The French template (the app's default locale), `{param}` placeholders included. */
  path: string
  params: string[]
}

/** The web routes of contracts/routes.yaml (`web` not false), as scripts/ssr-audit.mjs reads them. */
export function contractWebRoutes(): ContractRoute[] {
  const raw = parse(readFileSync(repoFile('contracts/routes.yaml'), 'utf8')) as {
    routes: {
      id: string
      path: string | Record<string, string>
      params?: string[]
      web?: boolean
    }[]
  }
  return raw.routes
    .filter((route) => route.web !== false)
    .map((route) => ({
      id: route.id,
      path: typeof route.path === 'string' ? route.path : (route.path.fr ?? route.path.en),
      params: route.params ?? [],
    }))
}

export type AuthRequirement = 'public' | 'authenticated' | 'unauthenticated'

/**
 * The `auth` of every entry of routes.config.ts, keyed by its contract id (the `pathVariants.<id>`
 * it registers). Read from the source rather than imported: the module pulls in every page
 * component, which a Node test runner cannot load.
 */
export function configuredAuth(): Map<string, AuthRequirement> {
  const source = readFileSync(repoFile('frontend/src/config/routes.config.ts'), 'utf8')
  const auth = new Map<string, AuthRequirement>()
  const entry = /paths:\s*pathVariants\.(\w+)\([^)]*\),[\s\S]*?auth:\s*'(\w+)'/g
  for (const [, id, requirement] of source.matchAll(entry))
    auth.set(id, requirement as AuthRequirement)
  return auth
}

/** `route`'s path with every `{param}` replaced from `params`. */
export function fillPath(route: ContractRoute, params: Record<string, string>): string {
  return route.path.replace(/\{(\w+)\}/g, (_, name: string) => {
    const value = params[name]
    if (!value) throw new Error(`route ${route.id}: no value for {${name}}`)
    return value
  })
}
