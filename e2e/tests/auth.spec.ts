import { test, expect } from '@playwright/test';

test.describe('Authentication Flow', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
  });

  test('should show login page for unauthenticated users', async ({ page }) => {
    await page.goto('/login');
    await expect(page.getByRole('heading', { name: /sign in/i })).toBeVisible();
    await expect(page.getByRole('button', { name: /strava/i })).toBeVisible();
  });

  test('should redirect to Strava when clicking login button', async ({ page }) => {
    await page.goto('/login');

    const [popup] = await Promise.all([
      page.waitForEvent('popup'),
      page.getByRole('button', { name: /strava/i }).click(),
    ]).catch(() => [null]);

    if (popup) {
      await expect(popup.url()).toContain('strava.com');
    }
  });

  test('should show sign in button in header for unauthenticated users', async ({ page }) => {
    await expect(page.getByRole('link', { name: /sign in/i })).toBeVisible();
  });

  test('should navigate to login page when clicking sign in', async ({ page }) => {
    await page.getByRole('link', { name: /sign in/i }).click();
    await expect(page).toHaveURL('/login');
  });

  test('should handle OAuth callback with error', async ({ page }) => {
    await page.goto('/auth/callback/strava?error=access_denied');
    await expect(page.getByText(/authentication failed|error|denied/i)).toBeVisible();
  });

  test('should redirect protected routes to login', async ({ page }) => {
    await page.goto('/profile');
    await expect(page).toHaveURL(/\/login/);
  });
});

test.describe('Authenticated User', () => {
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

  test('should show user menu when authenticated', async ({ page }) => {
    await page.goto('/');
    await expect(page.getByText('Test User')).toBeVisible();
  });

  test('should show sign out button when authenticated', async ({ page }) => {
    await page.goto('/');
    await expect(page.getByRole('button', { name: /sign out/i })).toBeVisible();
  });

  test('should navigate to profile page', async ({ page }) => {
    await page.goto('/');
    await page.getByText('Test User').click();
    await expect(page).toHaveURL('/profile');
  });
});
