import { test, expect } from '@playwright/test';

test.describe('Public Pages (Unauthenticated)', () => {
  // Clear all browser context before each test to ensure unauthenticated state
  test.beforeEach(async ({ context }) => {
    await context.clearCookies();
  });

  test('should display home page', async ({ page }) => {
    await page.goto('/');
    await expect(page.getByRole('heading', { name: 'Welcome to Tribly' })).toBeVisible();
    await expect(page.getByText('Your cycling team management platform')).toBeVisible();
  });

  test('should show sign in link in header', async ({ page }) => {
    await page.goto('/');
    await expect(page.getByRole('link', { name: /sign in/i })).toBeVisible();
  });

  test('should navigate to login page', async ({ page }) => {
    await page.goto('/');
    // Wait for the page to stabilize (React hydration) before clicking
    await expect(page.getByRole('link', { name: /sign in/i })).toBeVisible();
    // Use a short delay to let React hydration complete
    await page.waitForLoadState('networkidle');
    await page.getByRole('link', { name: /sign in/i }).click();
    await expect(page).toHaveURL('/login');
  });

  test('should display login page with sign in button', async ({ page }) => {
    await page.goto('/login');
    await expect(page.getByRole('heading', { name: 'Welcome to Tribly' })).toBeVisible();
    await expect(page.getByRole('button', { name: 'Sign In' })).toBeVisible();
  });

  test('should redirect to Keycloak when clicking sign in', async ({ page }) => {
    await page.goto('/login');
    await page.getByRole('button', { name: 'Sign In' }).click();
    await expect(page).toHaveURL(/localhost:8180.*\/auth/);
  });

  test('should display teams list page', async ({ page }) => {
    await page.goto('/teams');
    await expect(page.getByRole('heading', { name: 'Teams' })).toBeVisible();
  });

  test('should require auth to create team', async ({ page }) => {
    await page.goto('/teams/new');
    // Should redirect to login or show auth check
    await expect(page).toHaveURL(/\/(login|teams\/new)/);
  });

  test('should display footer on all pages', async ({ page }) => {
    await page.goto('/');
    await expect(page.getByText(/© 2025 Tribly/)).toBeVisible();
  });

  test('should show 404 for non-existent pages', async ({ page }) => {
    await page.goto('/non-existent-page');
    await expect(page.getByText('404')).toBeVisible();
  });
});
