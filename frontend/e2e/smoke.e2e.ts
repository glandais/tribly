import { as, expect, test } from './support/fixtures'
import { hydrated, pageHydrated, watchHydration } from './support/ui'

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

test.describe('an unknown URL', () => {
  // The router's '*' catch-all renders NotFoundPage, and entry-server.tsx turns that match into a
  // real 404 status — a crawler must not index a « Page non trouvée » as a page.
  for (const path of ['/inconnu', '/equipes/{team}/inconnu', '/plateforme/inconnu'])
    test(`${path} answers 404 with a usable page that leads home`, async ({ page, seed }) => {
      const url = path.replace('{team}', seed.team.slug)
      const { pageErrors, hydrationErrors } = await watchHydration(page)

      const response = await page.goto(url)
      expect(response?.status(), `${url}: document status`).toBe(404)
      await pageHydrated(page)
      const main = page.getByRole('main')
      await expect(main.getByRole('heading', { level: 1, name: '404', exact: true })).toBeVisible()
      await expect(main.getByText('Page non trouvée', { exact: true })).toBeVisible()
      expect(new URL(page.url()).pathname, 'the URL stays as typed').toBe(url)

      // The app is live around it: the link home works in place.
      const back = main.getByRole('link', { name: "Retour à l'accueil" })
      await expect(back).toHaveAttribute('href', '/')
      await hydrated(back)
      await back.click()
      await expect(page).toHaveURL(/\/$/)
      await expect(main.getByRole('heading', { name: 'Dernières publications' })).toBeVisible()

      expect(pageErrors, `${url}: uncaught errors`).toEqual([])
      expect(hydrationErrors, `${url}: hydration errors`).toEqual([])
    })
})
