import { describe, it, expect, vi } from 'vitest'
import { renderToString } from 'react-dom/server'
import { QueryClientProvider, useQuery } from '@tanstack/react-query'
import { makeQueryClient } from './queryClient'

function Probe() {
  const { status } = useQuery({
    queryKey: ['ad', 'refused'],
    queryFn: () => Promise.reject(new Error('403')),
  })
  return <span>{status}</span>
}

/**
 * A query the loader failed to read is not dehydrated: the client starts without it, pending. The
 * server render has to agree, or React throws #418 on hydration.
 */
describe('server render of a query the loader failed to read', () => {
  it('renders it pending, as the client will', async () => {
    const queryClient = makeQueryClient({ isServer: true })
    await queryClient.prefetchQuery({
      queryKey: ['ad', 'refused'],
      queryFn: () => Promise.reject(new Error('403')),
    })
    expect(queryClient.getQueryState(['ad', 'refused'])?.status).toBe('error')

    const html = renderToString(
      <QueryClientProvider client={queryClient}>
        <Probe />
      </QueryClientProvider>
    )
    expect(html).toContain('pending')
  })
})

/**
 * entry-server reads the config and the version before the route's prefetch, and renders them only
 * after it: nothing observes them in between. Collected then, the page rendered without the site
 * name or the version, and did not dehydrate them — React #418 once the client had them.
 */
describe('a server query nothing observes yet', () => {
  it('outlives a slow prefetch, up to the render', async () => {
    vi.useFakeTimers()
    try {
      const queryClient = makeQueryClient({ isServer: true })
      await queryClient.fetchQuery({ queryKey: ['config'], queryFn: () => ({ appName: 'P' }) })

      await vi.advanceTimersByTimeAsync(30_000)

      expect(queryClient.getQueryData(['config'])).toEqual({ appName: 'P' })
    } finally {
      vi.useRealTimers()
    }
  })
})
