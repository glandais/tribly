import { describe, it, expect, vi } from 'vitest'
import type { ReactNode } from 'react'
import { renderToString } from 'react-dom/server'
import {
  Link,
  StaticRouterProvider,
  createStaticHandler,
  createStaticRouter,
} from 'react-router-dom'

vi.mock('./appConfig', () => ({
  getPinnedTeamSlug: () => 'n-peloton',
}))
vi.mock('./locale-context', () => ({
  getCurrentLocale: () => 'fr',
}))

import { PinnedHrefs } from './PinnedHrefs'

/** What the server renders for one link, the root wrapped or not. */
async function serverHref(wrap: (children: ReactNode) => ReactNode): Promise<string> {
  const handler = createStaticHandler([
    { path: '*', element: wrap(<Link to="/equipes/n-peloton/sorties/x">x</Link>) },
  ])
  const context = await handler.query(new Request('http://np.localhost/sorties/x'))
  if (context instanceof Response) throw new Error('unexpected redirect')
  const router = createStaticRouter(handler.dataRoutes, context)
  const html = renderToString(<StaticRouterProvider router={router} context={context} />)
  return /href="([^"]*)"/.exec(html)![1]
}

describe('PinnedHrefs on the server', () => {
  it('writes a link in browser space', async () => {
    expect(await serverHref((children) => <PinnedHrefs>{children}</PinnedHrefs>)).toBe('/sorties/x')
  })

  it('is what makes the difference: StaticRouterProvider alone keeps the prefix', async () => {
    expect(await serverHref((children) => children)).toBe('/equipes/n-peloton/sorties/x')
  })
})
