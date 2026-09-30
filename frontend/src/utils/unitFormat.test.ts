import { describe, expect, it } from 'vitest'
import { formatFileSize } from './unitFormat'

// Intl separates value and unit with a no-break space; compare on plain spaces.
const plain = (s: string) => s.replace(/[  ]/g, ' ')

describe('formatFileSize', () => {
  it('uses the French symbols', () => {
    expect(plain(formatFileSize(512, 'fr'))).toBe('512 o')
    expect(plain(formatFileSize(240_000, 'fr'))).toBe('240 ko')
    expect(plain(formatFileSize(2_400_000, 'fr'))).toBe('2,4 Mo')
  })

  it('uses the English symbols', () => {
    expect(formatFileSize(240_000, 'en')).toBe('240 kB')
    expect(formatFileSize(2_400_000, 'en')).toBe('2.4 MB')
  })

  it('moves to the next unit rather than showing 1000', () => {
    expect(formatFileSize(999_700, 'en')).toBe('1 MB')
    expect(formatFileSize(15_300_000, 'en')).toBe('15 MB')
  })
})
