import { defineConfig, devices } from '@playwright/test';
import path from 'path';
import { fileURLToPath } from 'url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const authFile = path.join(__dirname, '.auth/user.json');

export default defineConfig({
  testDir: './tests',
  // Disable full parallelism to prevent storage state race conditions
  fullyParallel: false,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 1,
  workers: process.env.CI ? 1 : undefined,
  reporter: 'html',
  timeout: 60000,
  expect: {
    timeout: 10000,
  },
  use: {
    baseURL: 'http://localhost:5173',
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
  },
  projects: [
    // Setup project - runs first to authenticate
    {
      name: 'setup',
      testMatch: /.*\.setup\.ts/,
    },
    // Authenticated tests - run sequentially to preserve auth state
    {
      name: 'chromium',
      use: {
        ...devices['Desktop Chrome'],
        storageState: authFile,
      },
      dependencies: ['setup'],
      testIgnore: /.*\.setup\.ts/,
    },
    // Unauthenticated tests (no dependencies, explicitly no storage state)
    {
      name: 'chromium-unauthenticated',
      use: {
        ...devices['Desktop Chrome'],
        // Explicitly clear storage state to ensure no auth state leaks
        storageState: { cookies: [], origins: [] },
      },
      testMatch: /.*\.unauth\.spec\.ts/,
    },
  ],
  webServer: {
    command: 'cd ../frontend && pnpm dev',
    url: 'http://localhost:5173',
    reuseExistingServer: !process.env.CI,
    timeout: 120000,
  },
});
