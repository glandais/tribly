import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { isStaleChunkError, reloadForStaleChunk } from './staleChunk'

describe('staleChunk', () => {
  const reload = vi.fn()

  beforeEach(() => {
    sessionStorage.clear()
    reload.mockClear()
    vi.stubGlobal('location', { ...window.location, reload })
  })

  afterEach(() => {
    vi.unstubAllGlobals()
    vi.useRealTimers()
  })

  it('recognises a lazy chunk the deploy removed, in each browser wording', () => {
    expect(
      isStaleChunkError(
        new TypeError(
          'Failed to fetch dynamically imported module: https://www.pedalons.fr/assets/UserProfilePage-DXThapHx.js'
        )
      )
    ).toBe(true)
    expect(isStaleChunkError(new TypeError('Importing a module script failed.'))).toBe(true)
    expect(isStaleChunkError(new TypeError('error loading dynamically imported module'))).toBe(true)
    expect(isStaleChunkError(new TypeError('x is undefined'))).toBe(false)
  })

  it('reloads once, then lets the error show if the chunk still fails', () => {
    vi.useFakeTimers()
    expect(reloadForStaleChunk()).toBe(true)
    expect(reloadForStaleChunk()).toBe(false)
    expect(reload).toHaveBeenCalledTimes(1)

    vi.advanceTimersByTime(31_000)
    expect(reloadForStaleChunk()).toBe(true)
    expect(reload).toHaveBeenCalledTimes(2)
  })
})
