import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { render, screen, fireEvent, cleanup } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { MantineProvider, Paper } from '@mantine/core'
import type { ReactNode } from 'react'

const prefetchUrl = vi.fn()
vi.mock('@/lib/prefetch', () => ({ prefetchUrl: (...args: unknown[]) => prefetchUrl(...args) }))

import { PrefetchLink } from './PrefetchLink'

function renderIn(ui: ReactNode, initialPath = '/equipes/np') {
  return render(
    <MantineProvider>
      <QueryClientProvider client={new QueryClient()}>
        <MemoryRouter initialEntries={[initialPath]}>{ui}</MemoryRouter>
      </QueryClientProvider>
    </MantineProvider>
  )
}

describe('PrefetchLink', () => {
  beforeEach(() => {
    vi.useFakeTimers({ shouldAdvanceTime: true })
    prefetchUrl.mockClear()
  })
  afterEach(() => {
    cleanup()
    vi.useRealTimers()
  })

  it('prefetches shortly after the pointer settles on the link', () => {
    renderIn(<PrefetchLink to="/equipes/np/sorties/x">Sortie</PrefetchLink>)
    fireEvent.mouseEnter(screen.getByText('Sortie'))
    expect(prefetchUrl).not.toHaveBeenCalled()
    vi.advanceTimersByTime(100)
    expect(prefetchUrl).toHaveBeenCalledTimes(1)
    expect(prefetchUrl.mock.calls[0][1]).toBe('/equipes/np/sorties/x')
    expect(prefetchUrl.mock.calls[0][2]).toEqual({ chunkOnly: false })
  })

  it('prefetches nothing when the pointer only sweeps across', () => {
    renderIn(<PrefetchLink to="/equipes/np/sorties/x">Sortie</PrefetchLink>)
    const link = screen.getByText('Sortie')
    fireEvent.mouseEnter(link)
    vi.advanceTimersByTime(50)
    fireEvent.mouseLeave(link)
    vi.advanceTimersByTime(500)
    expect(prefetchUrl).not.toHaveBeenCalled()
  })

  it('prefetches on focus too, so keyboard navigation gets the same head start', () => {
    renderIn(<PrefetchLink to="/equipes/np/sorties/x">Sortie</PrefetchLink>)
    fireEvent.focus(screen.getByText('Sortie'))
    vi.advanceTimersByTime(100)
    expect(prefetchUrl).toHaveBeenCalledTimes(1)
  })

  it('prefetches immediately on touch, where there is no hover to read', () => {
    renderIn(<PrefetchLink to="/equipes/np/sorties/x">Sortie</PrefetchLink>)
    fireEvent.touchStart(screen.getByText('Sortie'))
    expect(prefetchUrl).toHaveBeenCalledTimes(1)
  })

  it('does nothing at all with prefetch="none"', () => {
    renderIn(
      <PrefetchLink to="/equipes/np/sorties/x" prefetch="none">
        Sortie
      </PrefetchLink>
    )
    fireEvent.mouseEnter(screen.getByText('Sortie'))
    vi.advanceTimersByTime(500)
    expect(prefetchUrl).not.toHaveBeenCalled()
  })

  it('keeps the caller’s own handlers', () => {
    const onMouseEnter = vi.fn()
    renderIn(
      <PrefetchLink to="/equipes/np/sorties/x" onMouseEnter={onMouseEnter}>
        Sortie
      </PrefetchLink>
    )
    fireEvent.mouseEnter(screen.getByText('Sortie'))
    expect(onMouseEnter).toHaveBeenCalledTimes(1)
  })

  it('keeps the query string — a filtered list is a different prefetch', () => {
    renderIn(
      <PrefetchLink to={{ pathname: '/equipes/np/parcours', search: '?p=2' }}>
        Parcours
      </PrefetchLink>
    )
    fireEvent.mouseEnter(screen.getByText('Parcours'))
    vi.advanceTimersByTime(100)
    expect(prefetchUrl.mock.calls[0][1]).toBe('/equipes/np/parcours?p=2')
  })

  it('works as a Mantine polymorphic component, the way this app links', () => {
    renderIn(
      <Paper component={PrefetchLink} to="/equipes/np/sorties/x">
        Carte
      </Paper>
    )
    const card = screen.getByText('Carte')
    expect(card.tagName).toBe('A')
    expect(card.getAttribute('href')).toBe('/equipes/np/sorties/x')
    fireEvent.mouseEnter(card)
    vi.advanceTimersByTime(100)
    expect(prefetchUrl).toHaveBeenCalledTimes(1)
  })
})
