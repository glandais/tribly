import { describe, it, expect } from 'vitest'
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
