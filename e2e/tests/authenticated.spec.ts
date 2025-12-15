import { test, expect } from '@playwright/test';

test.describe('Authenticated User - Navigation', () => {
  test('should show user menu in header', async ({ page }) => {
    await page.goto('/');
    // Wait for auth to be initialized
    await expect(page.getByRole('button', { name: /sign out/i })).toBeVisible({ timeout: 15000 });
    await expect(page.getByRole('link', { name: /teams/i })).toBeVisible();
  });

  test('should navigate to profile page', async ({ page }) => {
    await page.goto('/');
    await expect(page.getByRole('button', { name: /sign out/i })).toBeVisible({ timeout: 15000 });
    await page.getByRole('link', { name: /User One/i }).click();
    await expect(page).toHaveURL('/profile');
  });

  test('should navigate to teams page', async ({ page }) => {
    await page.goto('/');
    await expect(page.getByRole('button', { name: /sign out/i })).toBeVisible({ timeout: 15000 });
    await page.getByRole('link', { name: /teams/i }).click();
    await expect(page).toHaveURL('/teams');
  });
});

test.describe('Profile Page', () => {
  test('should display profile settings', async ({ page }) => {
    await page.goto('/profile');
    await expect(page.getByRole('heading', { name: 'Profile Settings' })).toBeVisible({ timeout: 15000 });
  });

  test('should show user email', async ({ page }) => {
    await page.goto('/profile');
    await expect(page.getByRole('heading', { name: 'Profile Settings' })).toBeVisible({ timeout: 15000 });
    // Wait for user data to load - email appears in multiple places
    await expect(page.getByText('user1@example.com').first()).toBeVisible({ timeout: 10000 });
  });

  test('should have edit profile button', async ({ page }) => {
    await page.goto('/profile');
    await expect(page.getByRole('heading', { name: 'Profile Settings' })).toBeVisible({ timeout: 15000 });
    await expect(page.getByRole('button', { name: 'Edit Profile' })).toBeVisible();
  });

  test('should open edit mode when clicking edit', async ({ page }) => {
    await page.goto('/profile');
    await expect(page.getByRole('heading', { name: 'Profile Settings' })).toBeVisible({ timeout: 15000 });
    await page.getByRole('button', { name: 'Edit Profile' }).click();
    await expect(page.getByRole('textbox', { name: 'Display Name' })).toBeVisible();
    await expect(page.getByRole('button', { name: 'Save' })).toBeVisible();
    await expect(page.getByRole('button', { name: 'Cancel' })).toBeVisible();
  });

  test('should cancel edit mode', async ({ page }) => {
    await page.goto('/profile');
    await expect(page.getByRole('heading', { name: 'Profile Settings' })).toBeVisible({ timeout: 15000 });
    await page.getByRole('button', { name: 'Edit Profile' }).click();
    await expect(page.getByRole('button', { name: 'Cancel' })).toBeVisible();
    await page.getByRole('button', { name: 'Cancel' }).click();
    await expect(page.getByRole('button', { name: 'Edit Profile' })).toBeVisible();
  });

  test('should have danger zone with delete account', async ({ page }) => {
    await page.goto('/profile');
    await expect(page.getByRole('heading', { name: 'Profile Settings' })).toBeVisible({ timeout: 15000 });
    await expect(page.getByRole('heading', { name: 'Danger Zone' })).toBeVisible();
    await expect(page.getByRole('button', { name: 'Delete Account' })).toBeVisible();
  });
});

test.describe('Teams Page', () => {
  test('should display teams list', async ({ page }) => {
    await page.goto('/teams');
    await expect(page.getByRole('heading', { name: 'Teams' })).toBeVisible({ timeout: 15000 });
  });

  test('should have create team button', async ({ page }) => {
    await page.goto('/teams');
    await expect(page.getByRole('heading', { name: 'Teams' })).toBeVisible({ timeout: 15000 });
    await expect(page.getByRole('link', { name: /create team/i })).toBeVisible();
  });

  test('should have public/my teams tabs', async ({ page }) => {
    await page.goto('/teams');
    await expect(page.getByRole('heading', { name: 'Teams' })).toBeVisible({ timeout: 15000 });
    await expect(page.getByRole('button', { name: 'Public Teams' })).toBeVisible();
    await expect(page.getByRole('button', { name: 'My Teams' })).toBeVisible();
  });

  test('should have search functionality', async ({ page }) => {
    await page.goto('/teams');
    await expect(page.getByRole('heading', { name: 'Teams' })).toBeVisible({ timeout: 15000 });
    await expect(page.getByPlaceholder(/search teams/i)).toBeVisible();
  });

  test('should navigate to create team page', async ({ page }) => {
    await page.goto('/teams');
    await expect(page.getByRole('heading', { name: 'Teams' })).toBeVisible({ timeout: 15000 });
    await page.getByRole('link', { name: /create team/i }).first().click();
    await expect(page).toHaveURL('/teams/new');
  });
});

test.describe('Create Team Page', () => {
  test('should display create team form', async ({ page }) => {
    await page.goto('/teams/new');
    await expect(page.getByRole('heading', { name: 'Create a Team' })).toBeVisible({ timeout: 15000 });
  });

  test('should have required form fields', async ({ page }) => {
    await page.goto('/teams/new');
    await expect(page.getByRole('heading', { name: 'Create a Team' })).toBeVisible({ timeout: 15000 });
    await expect(page.getByRole('textbox', { name: /team name/i })).toBeVisible();
    await expect(page.getByRole('textbox', { name: /description/i })).toBeVisible();
    await expect(page.getByRole('checkbox', { name: /public team/i })).toBeVisible();
  });

  test('should have create button disabled initially', async ({ page }) => {
    await page.goto('/teams/new');
    await expect(page.getByRole('heading', { name: 'Create a Team' })).toBeVisible({ timeout: 15000 });
    await expect(page.getByRole('button', { name: 'Create Team' })).toBeDisabled();
  });

  test('should enable create button when name is filled', async ({ page }) => {
    await page.goto('/teams/new');
    await expect(page.getByRole('heading', { name: 'Create a Team' })).toBeVisible({ timeout: 15000 });
    await page.getByRole('textbox', { name: /team name/i }).fill('Test Team');
    await expect(page.getByRole('button', { name: 'Create Team' })).toBeEnabled();
  });

  test('should have cancel button', async ({ page }) => {
    await page.goto('/teams/new');
    await expect(page.getByRole('heading', { name: 'Create a Team' })).toBeVisible({ timeout: 15000 });
    await expect(page.getByRole('link', { name: 'Cancel' })).toBeVisible();
  });
});

test.describe('Team Creation and Detail Flow', () => {
  const uniqueTeamName = `E2E Test Team ${Date.now()}`;

  test('should create a new team and see details', async ({ page }) => {
    // First ensure auth is established
    await page.goto('/');
    await expect(page.getByRole('button', { name: /sign out/i })).toBeVisible({ timeout: 15000 });

    await page.goto('/teams/new');
    await expect(page.getByRole('heading', { name: 'Create a Team' })).toBeVisible({ timeout: 15000 });

    // Fill in team details
    await page.getByRole('textbox', { name: /team name/i }).fill(uniqueTeamName);
    await page.getByRole('textbox', { name: /description/i }).fill('An E2E test team for automated testing');

    // Submit form
    await page.getByRole('button', { name: 'Create Team' }).click();

    // Should redirect to team detail page
    await expect(page).toHaveURL(/\/teams\/e2e-test-team-/, { timeout: 15000 });

    // Verify basic team info is displayed
    await expect(page.getByRole('heading', { name: uniqueTeamName })).toBeVisible({ timeout: 15000 });

    // Verify user is shown as member (the Members section is always visible)
    await expect(page.getByText('Members')).toBeVisible();
    // Check for User One in the members list (using (you) marker to be specific)
    await expect(page.getByText('User One(you)')).toBeVisible();
  });

  test('should access team settings', async ({ page }) => {
    // First ensure auth is established
    await page.goto('/');
    await expect(page.getByRole('button', { name: /sign out/i })).toBeVisible({ timeout: 15000 });

    // Create a team
    await page.goto('/teams/new');
    await expect(page.getByRole('heading', { name: 'Create a Team' })).toBeVisible({ timeout: 15000 });
    await page.getByRole('textbox', { name: /team name/i }).fill(`Settings Test ${Date.now()}`);
    await page.getByRole('button', { name: 'Create Team' }).click();

    // Wait for redirect then reload to ensure fresh team data
    await expect(page).toHaveURL(/\/teams\/settings-test-/, { timeout: 15000 });
    await page.reload();
    await expect(page.getByRole('link', { name: 'Settings' })).toBeVisible({ timeout: 15000 });

    // Navigate to settings
    await page.getByRole('link', { name: 'Settings' }).click();
    await expect(page.getByRole('heading', { name: 'Team Settings' })).toBeVisible();

    // Check settings form
    await expect(page.getByRole('textbox', { name: 'Team Name' })).toBeVisible();
    await expect(page.getByRole('textbox', { name: 'Description' })).toBeVisible();
    await expect(page.getByRole('checkbox', { name: /public team/i })).toBeVisible();
    await expect(page.getByRole('button', { name: 'Save Changes' })).toBeVisible();

    // Check danger zone
    await expect(page.getByRole('heading', { name: 'Danger Zone' })).toBeVisible();
    await expect(page.getByRole('button', { name: 'Delete Team' })).toBeVisible();
  });
});

test.describe('Rides Flow', () => {
  test('should create team and navigate to rides', async ({ page }) => {
    // First ensure auth is established
    await page.goto('/');
    await expect(page.getByRole('button', { name: /sign out/i })).toBeVisible({ timeout: 15000 });

    // Create a team
    await page.goto('/teams/new');
    await expect(page.getByRole('heading', { name: 'Create a Team' })).toBeVisible({ timeout: 15000 });
    await page.getByRole('textbox', { name: /team name/i }).fill(`Rides Test ${Date.now()}`);
    await page.getByRole('button', { name: 'Create Team' }).click();

    // Wait for redirect then reload to ensure fresh team data
    await expect(page).toHaveURL(/\/teams\/rides-test-/, { timeout: 15000 });
    await page.reload();

    // Wait for team page and navigate to rides
    const navTabs = page.locator('nav').filter({ hasText: 'Overview' });
    await expect(navTabs.getByRole('link', { name: 'Rides' })).toBeVisible({ timeout: 15000 });
    await navTabs.getByRole('link', { name: 'Rides' }).click();

    // Check rides page
    await expect(page.getByRole('heading', { name: 'Rides' })).toBeVisible();
    await expect(page.getByRole('link', { name: /create ride/i })).toBeVisible();
  });

  test('should display create ride form', async ({ page }) => {
    // First ensure auth is established
    await page.goto('/');
    await expect(page.getByRole('button', { name: /sign out/i })).toBeVisible({ timeout: 15000 });

    // Create a team
    await page.goto('/teams/new');
    await expect(page.getByRole('heading', { name: 'Create a Team' })).toBeVisible({ timeout: 15000 });
    await page.getByRole('textbox', { name: /team name/i }).fill(`Create Ride Test ${Date.now()}`);
    await page.getByRole('button', { name: 'Create Team' }).click();

    // Wait for redirect - capture the team slug for direct navigation
    await expect(page).toHaveURL(/\/teams\/create-ride-test-/, { timeout: 15000 });
    const teamPageUrl = page.url();
    const ridesUrl = teamPageUrl + '/rides';
    const newRideUrl = teamPageUrl + '/rides/new';

    // Navigate directly to create ride page (avoid intermediate navigation issues)
    await page.goto(newRideUrl);
    await page.waitForLoadState('domcontentloaded');

    // Check form - wait for page to load
    await expect(page.getByRole('heading', { name: 'Create a Ride' })).toBeVisible({ timeout: 15000 });
    await expect(page.getByRole('textbox', { name: /ride title/i })).toBeVisible();
    await expect(page.getByRole('textbox', { name: /description/i })).toBeVisible();
    await expect(page.getByRole('textbox', { name: /date/i })).toBeVisible();
    await expect(page.getByRole('radio', { name: 'Team only' })).toBeVisible();
    await expect(page.getByRole('radio', { name: 'Public' })).toBeVisible();
    // Groups section is within the form
    await expect(page.getByText('Groups', { exact: true })).toBeVisible({ timeout: 5000 });
    await expect(page.getByRole('button', { name: 'Create Ride' })).toBeDisabled();
  });

  test('should create a new ride', async ({ page }) => {
    // First ensure auth is established
    await page.goto('/');
    await expect(page.getByRole('button', { name: /sign out/i })).toBeVisible({ timeout: 15000 });

    // Create a team
    await page.goto('/teams/new');
    await expect(page.getByRole('heading', { name: 'Create a Team' })).toBeVisible({ timeout: 15000 });
    await page.getByRole('textbox', { name: /team name/i }).fill(`New Ride Test ${Date.now()}`);
    await page.getByRole('button', { name: 'Create Team' }).click();

    // Wait for redirect then reload to ensure fresh team data
    await expect(page).toHaveURL(/\/teams\/new-ride-test-/, { timeout: 15000 });
    await page.reload();

    // Navigate to create ride - use locator within nav to target the tab link
    const navTabs = page.locator('nav').filter({ hasText: 'Overview' });
    await expect(navTabs.getByRole('link', { name: 'Rides' })).toBeVisible({ timeout: 15000 });
    await navTabs.getByRole('link', { name: 'Rides' }).click();
    // Wait for URL to change to rides page
    await expect(page).toHaveURL(/\/rides$/, { timeout: 10000 });
    await page.getByRole('link', { name: /create ride/i }).first().click();

    // Fill in ride details
    await expect(page.getByRole('heading', { name: 'Create a Ride' })).toBeVisible({ timeout: 10000 });
    await page.getByRole('textbox', { name: /ride title/i }).fill('E2E Test Ride');
    await page.getByRole('textbox', { name: /description/i }).fill('A test ride created by E2E tests');
    await page.getByRole('textbox', { name: /date/i }).fill('2025-12-25');
    await page.getByRole('textbox', { name: /start time/i }).fill('10:00');

    // Submit form
    await expect(page.getByRole('button', { name: 'Create Ride' })).toBeEnabled();
    await page.getByRole('button', { name: 'Create Ride' }).click();

    // Should redirect to ride detail page (TSID format - alphanumeric string)
    await expect(page).toHaveURL(/\/rides\/[a-z0-9]+/, { timeout: 15000 });
    await expect(page.getByRole('heading', { name: 'E2E Test Ride' })).toBeVisible();
    await expect(page.getByText(/participants/i)).toBeVisible();
  });
});

test.describe('Sign Out', () => {
  test('should sign out successfully', async ({ page }) => {
    await page.goto('/');
    await expect(page.getByRole('button', { name: /sign out/i })).toBeVisible({ timeout: 15000 });
    await page.getByRole('button', { name: /sign out/i }).click();

    // Should redirect to login or home page and show sign in link
    await expect(page.getByRole('link', { name: /sign in/i })).toBeVisible({ timeout: 15000 });
  });
});
