import { as, expect, test } from './support/fixtures'

/**
 * The stack answers, and each role lands where it should. If these fail, look at the stack before
 * looking at the feature under test.
 */

test.describe('anonymous', () => {
  test('home page renders', async ({ page }) => {
    const response = await page.goto('/')
    expect(response?.status()).toBe(200)
    await expect(page.locator('body')).not.toBeEmpty()
  })

  test.describe('without JavaScript', () => {
    test.use({ javaScriptEnabled: false })

    test('a public team page is rendered by the server', async ({ page, seed }) => {
      const response = await page.goto(`/equipes/${seed.team.slug}`)
      expect(response?.status()).toBe(200)
      await expect(page.getByRole('heading', { level: 1, name: seed.team.name })).toBeVisible()
    })
  })

  test('the platform admin area sends a visitor away', async ({ page }) => {
    await page.goto('/plateforme')
    await expect(page).not.toHaveURL(/\/plateforme/)
  })

  // The counterpart of the rider's test below: without it, a login page that redirects everyone
  // would pass for a working session.
  test('the login page stays put for a visitor', async ({ page }) => {
    await page.goto('/connexion')
    await page.waitForLoadState('networkidle')
    await expect(page).toHaveURL(/\/connexion/)
  })
})

test.describe('rider', () => {
  test.use(as('rider'))

  test('the saved session signs in: the login page sends away', async ({ page }) => {
    await page.goto('/connexion')
    await expect(page).not.toHaveURL(/\/connexion/)
  })

  test('the platform admin area is still off limits', async ({ page }) => {
    await page.goto('/plateforme')
    await expect(page).not.toHaveURL(/\/plateforme/)
  })
})

test.describe('platform admin', () => {
  test.use(as('admin'))

  test('reaches the platform admin area', async ({ page }) => {
    await page.goto('/plateforme')
    await page.waitForLoadState('networkidle')
    await expect(page).toHaveURL(/\/plateforme/)
  })
})
