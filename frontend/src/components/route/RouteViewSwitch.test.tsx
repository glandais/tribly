import { describe, it, expect, vi, afterEach } from 'vitest'
import { render, screen, fireEvent, cleanup } from '@testing-library/react'
import { MemoryRouter, useLocation } from 'react-router-dom'
import { MantineProvider } from '@mantine/core'

vi.mock('react-i18next', () => ({
  useTranslation: () => ({ t: (key: string) => key, i18n: { language: 'fr' } }),
}))
vi.mock('@/config/paths', () => ({
  paths: {
    routes: (slug: string) => `/equipes/${slug}/parcours`,
    routesMap: (slug: string) => `/equipes/${slug}/parcours/carte`,
    allRoutes: () => '/parcours',
    allRoutesMap: () => '/parcours/carte',
  },
}))

import { RouteViewSwitch, type RouteViewSwitchProps } from './RouteViewSwitch'

function Where() {
  const { pathname, search } = useLocation()
  return <output data-testid="where">{pathname + search}</output>
}

function renderAt(url: string, props: RouteViewSwitchProps) {
  render(
    <MantineProvider>
      <MemoryRouter initialEntries={[url]}>
        <RouteViewSwitch {...props} />
        <Where />
      </MemoryRouter>
    </MantineProvider>
  )
}

const where = () => screen.getByTestId('where').textContent
const pick = (name: string) => fireEvent.click(screen.getByRole('radio', { name }))

describe('RouteViewSwitch', () => {
  afterEach(cleanup)

  it('changes the density in place on the list, without navigating', () => {
    const onDensityChange = vi.fn()
    renderAt('/equipes/np/parcours?surf=GRAVEL', {
      current: 'card',
      teamSlug: 'np',
      onDensityChange,
    })
    pick('listView.row')
    expect(onDensityChange).toHaveBeenCalledWith('row')
    expect(where()).toBe('/equipes/np/parcours?surf=GRAVEL')
  })

  it('navigates to the map path, keeping the filters and dropping the density and the page', () => {
    renderAt('/equipes/np/parcours?surf=GRAVEL&view=row&p=3&tags=1', {
      current: 'row',
      teamSlug: 'np',
      onDensityChange: vi.fn(),
    })
    pick('listView.map')
    expect(where()).toBe('/equipes/np/parcours/carte?surf=GRAVEL&tags=1')
  })

  it('goes back from the map to the list in the picked density, filters kept', () => {
    renderAt('/parcours/carte?q=col&role=all', { current: 'map' })
    pick('listView.row')
    expect(where()).toBe('/parcours?q=col&role=all&view=row')
  })

  it('does nothing when the current view is picked again', () => {
    renderAt('/parcours/carte?q=col', { current: 'map' })
    pick('listView.map')
    expect(where()).toBe('/parcours/carte?q=col')
  })
})
