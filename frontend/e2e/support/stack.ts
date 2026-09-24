import { readFileSync } from 'node:fs'
import { fileURLToPath } from 'node:url'
import { parseEnv } from 'node:util'

/**
 * Where the e2e stack answers, read from the same `.env.e2e` scripts/e2e.sh starts it from, so the
 * ports are written down once. E2E_BASE_URL / E2E_MAILHOG_URL override them (e.g. `pnpm dev`).
 */
const env = parseEnv(
  readFileSync(fileURLToPath(new URL('../../../.env.e2e', import.meta.url)), 'utf8')
)

function required(key: string): string {
  const value = env[key]
  if (!value) throw new Error(`.env.e2e: ${key} is not set`)
  return value
}

export const stack = {
  baseURL: process.env.E2E_BASE_URL ?? `http://localhost:${required('HTTP_PORT')}`,
  mailhogURL: process.env.E2E_MAILHOG_URL ?? `http://localhost:${required('E2E_MAILHOG_PORT')}`,
  adminEmail: required('PEDALONS_BOOTSTRAP_ADMIN_EMAIL'),
}

const authDir = fileURLToPath(new URL('../.auth/', import.meta.url))

export type Role = 'admin' | 'rider'

/** Saved browser session of a role: the refresh_token cookie, written by global-setup. */
export const storageStatePath = (role: Role) => `${authDir}${role}.json`

/** What global-setup seeded, for the tests to find it. */
export const seedPath = `${authDir}seed.json`
export { authDir }
