import { newUser } from './support/data'
import { expect, test } from './support/fixtures'
import { stack } from './support/stack'

/**
 * Session refresh, as two tabs — or the SSR server and the browser — do it: at the same moment, on
 * the same session. Every one of them used to fail but one, with a 500 (AuthService.refreshToken
 * updated the User row, whose @Version collided; fixed 2026-09-25).
 */
test('concurrent refreshes of one session all succeed', async () => {
  const user = await newUser('auth concurrent refresh')
  const statuses = await Promise.all(
    Array.from({ length: 8 }, () =>
      fetch(`${stack.baseURL}/api/auth/refresh`, {
        method: 'POST',
        headers: { 'X-Refresh-Token': user.refreshToken },
      }).then((response) => response.status)
    )
  )
  expect(statuses).toEqual(Array(8).fill(200))
})
