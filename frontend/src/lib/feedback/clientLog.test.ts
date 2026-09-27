import { beforeEach, describe, expect, it } from 'vitest'
import {
  MAX_ENTRIES,
  MAX_MESSAGE,
  clearLogEntries,
  getLogEntries,
  logEntry,
  pathOnly,
} from './clientLog'

describe('clientLog', () => {
  beforeEach(() => clearLogEntries())

  it('keeps the most recent entries only', () => {
    for (let i = 0; i < MAX_ENTRIES + 5; i++) logEntry('INFO', 'navigation', `/page/${i}`)
    const entries = getLogEntries()
    expect(entries).toHaveLength(MAX_ENTRIES)
    expect(entries[0].message).toBe('/page/5')
    expect(getLogEntries(2).map((e) => e.message)).toEqual([
      `/page/${MAX_ENTRIES + 3}`,
      `/page/${MAX_ENTRIES + 4}`,
    ])
  })

  it('truncates an entry to the contract limit', () => {
    logEntry('ERROR', 'console', 'x'.repeat(MAX_MESSAGE * 2))
    expect(getLogEntries()[0].message).toHaveLength(MAX_MESSAGE)
  })

  it('reduces a URL to its path', () => {
    expect(pathOnly('/verify?token=secret')).toBe('/verify')
    expect(pathOnly('/api/teams/x#frag')).toBe('/api/teams/x')
    expect(pathOnly('https://pedalons.fr/a/b?c=d')).toBe('/a/b')
  })
})
