import { describe, it, expect } from 'vitest'
import { safeNextPath } from './safeNextPath'

describe('safeNextPath', () => {
  it('keeps a same-origin path, with its query and hash', () => {
    expect(safeNextPath('/api/export/download?token=abc')).toBe('/api/export/download?token=abc')
    expect(safeNextPath('/sorties/x?p=2#groupes')).toBe('/sorties/x?p=2#groupes')
  })

  it('keeps an encoded slash as a path segment of this site', () => {
    expect(safeNextPath('/%2F/x')).toBe('/%2F/x')
  })

  it.each([
    ['/\t/evil.example'],
    ['/\n/evil.example'],
    ['/\r\n/evil.example'],
    ['/\\evil.example'],
    ['\\/evil.example'],
    ['//evil.example'],
    ['///evil.example'],
    ['https://evil.example'],
    ['javascript:alert(1)'],
    ['evil.example'],
    [''],
    [null],
  ])('refuses %j', (value) => {
    expect(safeNextPath(value)).toBeNull()
  })
})
