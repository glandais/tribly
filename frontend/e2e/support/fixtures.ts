import { readFileSync } from 'node:fs'
import { test as base } from '@playwright/test'
import { seedPath, storageStatePath, type Role } from './stack'

/** What global-setup seeded — see e2e/global-setup.ts. */
export interface Seed {
  admin: { email: string; displayName: string }
  rider: { email: string; displayName: string; password: string }
  team: { slug: string; name: string }
}

export const test = base.extend<{ seed: Seed }>({
  // Playwright requires the destructuring; `provide` rather than its usual `use`, which the React
  // hooks lint rule takes for React's use().
  // oxlint-disable-next-line no-empty-pattern
  seed: async ({}, provide) => {
    await provide(JSON.parse(readFileSync(seedPath, 'utf8')) as Seed)
  },
})

export { expect } from '@playwright/test'

let counter = 0

/**
 * A name no other test — nor another run against the same stack — will produce: the suite is fully
 * parallel, over two projects, and the database outlives a run.
 */
export function unique(label: string): string {
  const { project, workerIndex } = base.info()
  counter += 1
  return `${label} ${project.name}-${workerIndex}-${counter}-${Date.now().toString(36)}`
}

/** `test.use(as('rider'))` — the tests of that block start signed in as that role. */
export const as = (role: Role) => ({ storageState: storageStatePath(role) })
