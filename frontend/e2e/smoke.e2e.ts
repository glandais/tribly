import { as, expect, test } from './support/fixtures'

/**
 * What routes-render.e2e.ts, which opens every route as every role, does not cover: the server
 * rendering on its own, and a signed-in visitor kept out of the platform admin area. If these fail,
 * look at the stack before looking at the feature under test.
 */

test.describe('anonymous, without JavaScript', () => {
  test.use({ javaScriptEnabled: false })

  test('a public team page is rendered by the server', async ({ page, seed }) => {
    const response = await page.goto(`/equipes/${seed.team.slug}`)
    expect(response?.status()).toBe(200)
    await expect(page.getByRole('heading', { level: 1, name: seed.team.name })).toBeVisible()
  })
})

test.describe('rider', () => {
  test.use(as('rider'))

  test('the platform admin area is still off limits', async ({ page }) => {
    await page.goto('/plateforme')
    // Sent home, and the home page is up: the session is a real one, not an anonymous visit.
    await expect(page).toHaveURL(/\/$/)
    await expect(
      page.getByRole('main').getByRole('heading', { name: 'Dernières publications' })
    ).toBeVisible()
    await expect(page.getByRole('heading', { name: 'Administration plateforme' })).toHaveCount(0)
  })
})
