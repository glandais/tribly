import { describe, expect, it } from 'vitest'
import {
  createAssetDirective,
  parseAssetDirective,
  parseDirectiveAttributes,
} from './assetMarkdown'

function roundTrip(alt: string) {
  const directive = createAssetDirective('abc123', alt, 'full')
  const body = /^::asset\{([^}]+)\}$/.exec(directive)?.[1]
  expect(body).toBeDefined()
  return parseAssetDirective(parseDirectiveAttributes(body!))
}

describe('createAssetDirective', () => {
  it('keeps plain alt text as is', () => {
    expect(createAssetDirective('abc123', 'My image')).toBe('::asset{id="abc123" alt="My image"}')
  })

  it('omits the default size and an empty alt', () => {
    expect(createAssetDirective('abc123', '', 'medium')).toBe('::asset{id="abc123"}')
  })

  it('survives a quote, braces, a backslash and a line break in the alt text', () => {
    expect(roundTrip('Le "col" {du} C:\\Tour\nde France')).toEqual({
      assetId: 'abc123',
      size: 'full',
      alt: "Le 'col' (du) C:\\Tour de France",
    })
  })
})
