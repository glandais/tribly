import { beforeEach, describe, expect, it, vi } from 'vitest'

const reportClientError = vi.fn(() => Promise.resolve())
const authState = { isAuthenticated: true }

vi.mock('@/api/endpoints/feedback/feedback', () => ({
  reportClientError: (...args: unknown[]) => reportClientError(...(args as [])),
}))
vi.mock('@/store/authStore', () => ({ useAuthStore: { getState: () => authState } }))
vi.mock('./clientContext', () => ({
  buildClientContext: () => Promise.resolve({ platform: 'WEB', appVersion: 'test' }),
}))

import { ApiClientError } from '@/lib/apiError'
import { MAX_PER_SESSION, getLastError, reportError, resetErrorReporter } from './errorReporter'

const flush = () => new Promise((resolve) => setTimeout(resolve, 0))

describe('errorReporter', () => {
  beforeEach(() => {
    reportClientError.mockClear()
    resetErrorReporter()
    authState.isAuthenticated = true
  })

  it('reports an error once per page load', async () => {
    reportError(new TypeError('x is undefined'))
    reportError(new TypeError('x is undefined'))
    await flush()
    expect(reportClientError).toHaveBeenCalledTimes(1)
    expect(getLastError()?.type).toBe('TypeError')
  })

  it('caps the reports of a page load', async () => {
    for (let i = 0; i < MAX_PER_SESSION + 3; i++) reportError(new Error(`boom ${i}`))
    await flush()
    expect(reportClientError).toHaveBeenCalledTimes(MAX_PER_SESSION)
  })

  it('sends nothing for a visitor, or when the member opted out', async () => {
    authState.isAuthenticated = false
    reportError(new Error('anonymous'))
    authState.isAuthenticated = true
    // localStorage is a mock in the test setup: stub the stored opt-out.
    vi.mocked(localStorage.getItem).mockReturnValue('off')
    reportError(new Error('opted out'))
    vi.mocked(localStorage.getItem).mockReturnValue(null)
    await flush()
    expect(reportClientError).not.toHaveBeenCalled()
    // Still remembered, for a report written by hand.
    expect(getLastError()?.message).toBe('opted out')
  })

  it('ignores API errors and browser noise', async () => {
    reportError(new ApiClientError(500, { code: 'INTERNAL_ERROR' } as never))
    reportError(new Error('ResizeObserver loop completed with undelivered notifications.'))
    await flush()
    expect(reportClientError).not.toHaveBeenCalled()
  })
})
