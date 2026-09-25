import { defineConfig, devices } from '@playwright/test'
import { stack } from './e2e/support/stack'

/**
 * End-to-end suite, against the full stack scripts/e2e.sh runs (SSR frontend + backend behind
 * traefik, empty database). See e2e/README.md.
 *
 * Test files are `*.e2e.ts`, not `*.spec.ts`/`*.test.ts`, so Vitest's default glob never picks them.
 */
export default defineConfig({
  testDir: './e2e',
  testMatch: '**/*.e2e.ts',
  globalSetup: './e2e/global-setup.ts',
  // Wiped at the start of every run: parallel runs need one each (E2E_OUTPUT=/tmp/e2e-mine).
  outputDir: process.env.E2E_OUTPUT ?? './e2e/.results',
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 1 : 0,
  reporter: [['list'], ['html', { outputFolder: './e2e/.report', open: 'never' }]],
  use: {
    baseURL: stack.baseURL,
    // French is the app's default language, and its audience's.
    locale: 'fr-FR',
    timezoneId: 'Europe/Paris',
    trace: 'retain-on-failure',
    screenshot: 'only-on-failure',
    video: 'retain-on-failure',
  },
  projects: [
    { name: 'desktop', use: { ...devices['Desktop Chrome'] } },
    { name: 'mobile', use: { ...devices['Pixel 7'] } },
  ],
})
