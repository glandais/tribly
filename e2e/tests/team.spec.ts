import { test, expect } from '@playwright/test';

test.describe('Team Management', () => {
  test.beforeEach(async ({ page, context }) => {
    await context.addCookies([
      {
        name: 'auth_token',
        value: 'mock-token-for-testing',
        domain: 'localhost',
        path: '/',
      },
    ]);

    await page.addInitScript(() => {
      localStorage.setItem(
        'auth-storage',
        JSON.stringify({
          state: {
            user: {
              id: 1,
              email: 'test@example.com',
              displayName: 'Test User',
              avatarUrl: null,
              stravaId: 'strava-123',
              locale: 'en',
              timezone: 'UTC',
            },
            token: 'mock-token-for-testing',
            isAuthenticated: true,
          },
        })
      );
    });
  });

  test('should display teams list page', async ({ page }) => {
    await page.goto('/teams');
    await expect(page.getByRole('heading', { level: 1 })).toBeVisible();
  });

  test('should navigate to create team page', async ({ page }) => {
    await page.goto('/teams');
    await page.getByRole('link', { name: /create team/i }).click();
    await expect(page).toHaveURL('/teams/new');
  });

  test('should display create team form', async ({ page }) => {
    await page.goto('/teams/new');
    await expect(page.getByLabel(/team name/i)).toBeVisible();
    await expect(page.getByLabel(/description/i)).toBeVisible();
    await expect(page.getByRole('button', { name: /create/i })).toBeVisible();
  });

  test('should validate team name is required', async ({ page }) => {
    await page.goto('/teams/new');
    await page.getByRole('button', { name: /create/i }).click();
    await expect(page.getByText(/name is required|please enter/i)).toBeVisible();
  });
});

test.describe('Team Detail Page', () => {
  test.beforeEach(async ({ page, context }) => {
    await context.addCookies([
      {
        name: 'auth_token',
        value: 'mock-token-for-testing',
        domain: 'localhost',
        path: '/',
      },
    ]);

    await page.addInitScript(() => {
      localStorage.setItem(
        'auth-storage',
        JSON.stringify({
          state: {
            user: {
              id: 1,
              email: 'test@example.com',
              displayName: 'Test User',
              avatarUrl: null,
              stravaId: 'strava-123',
              locale: 'en',
              timezone: 'UTC',
            },
            token: 'mock-token-for-testing',
            isAuthenticated: true,
          },
        })
      );
    });
  });

  test('should show 404 for non-existent team', async ({ page }) => {
    await page.goto('/teams/non-existent-team-slug');
    await expect(page.getByText(/not found|404/i)).toBeVisible();
  });
});

test.describe('Public Teams', () => {
  test('should allow browsing public teams without auth', async ({ page }) => {
    await page.goto('/teams');
    await expect(page).toHaveURL('/teams');
  });

  test('should require auth to create team', async ({ page }) => {
    await page.goto('/teams/new');
    await expect(page).toHaveURL(/\/login/);
  });
});
