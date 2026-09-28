import { describe, it, expect, vi } from 'vitest'
import { QueryObserver } from '@tanstack/react-query'
import { makeQueryClient } from '@/lib/queryClient'
import { onlyIfNotFailed } from './useBreadcrumbData'

/**
 * The breadcrumb's observers outlive a navigation and only change key (`setOptions`), a path that
 * ignores `retryOnMount`: pointed at an entity the loader just failed to read, they read it again.
 */
describe('breadcrumb observer moving onto a failed entity', () => {
  async function readsAfterSwitch(enabled: (wanted: boolean) => unknown): Promise<number> {
    const queryClient = makeQueryClient()
    const queryFn = vi.fn(() => Promise.reject(Object.assign(new Error('404'), { status: 404 })))
    // The loader's read, failed.
    await queryClient.prefetchQuery({ queryKey: ['ride', 'gone'], queryFn, retry: false })
    expect(queryFn).toHaveBeenCalledTimes(1)

    // Mounted on the home page, on no entity; the navigation then points it at the ride.
    const observer = new QueryObserver(queryClient, {
      queryKey: ['ride', undefined],
      queryFn,
      retry: false,
      enabled: enabled(false) as boolean,
    })
    const unsubscribe = observer.subscribe(() => {})
    observer.setOptions({
      queryKey: ['ride', 'gone'],
      queryFn,
      retry: false,
      enabled: enabled(true) as boolean,
    })
    await new Promise((resolve) => setTimeout(resolve, 0))
    unsubscribe()
    return queryFn.mock.calls.length
  }

  it('reads it again with a plain enabled flag', async () => {
    expect(await readsAfterSwitch((wanted) => wanted)).toBe(2)
  })

  it('leaves it alone with onlyIfNotFailed', async () => {
    expect(await readsAfterSwitch(onlyIfNotFailed)).toBe(1)
  })
})
