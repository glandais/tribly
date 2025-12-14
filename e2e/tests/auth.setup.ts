import { test as setup, expect } from '@playwright/test';
import path from 'path';
import { fileURLToPath } from 'url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const authFile = path.join(__dirname, '../.auth/user.json');

setup('authenticate', async ({ page }) => {
  // Navigate to login page
  await page.goto('/login');

  // Wait for the page to load and click Sign In
  await page.waitForSelector('button:has-text("Sign In")', { timeout: 10000 });
  await page.getByRole('button', { name: 'Sign In' }).click();

  // Wait for Keycloak login page
  await page.waitForURL(/localhost:8180.*\/auth/, { timeout: 15000 });

  // Fill in Keycloak credentials
  await page.getByRole('textbox', { name: 'Username or email' }).fill('user1');
  await page.getByRole('textbox', { name: 'Password' }).fill('user1');

  // Click sign in
  await page.getByRole('button', { name: 'Sign In' }).click();

  // Wait for redirect back to app and authentication to complete
  await page.waitForURL('http://localhost:5173/**', { timeout: 15000 });

  // Wait for auth to be fully initialized (user menu visible)
  await expect(page.getByRole('button', { name: /sign out/i })).toBeVisible({
    timeout: 15000,
  });

  // Save storage state
  await page.context().storageState({ path: authFile });
});
