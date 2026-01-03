import fs from 'fs'
import path from 'path'
import { fileURLToPath } from 'url'

const __dirname = path.dirname(fileURLToPath(import.meta.url))
const LOCALES_DIR = path.join(__dirname, '../src/locales/fr')

type NestedObject = { [key: string]: string | NestedObject }

interface ConsolidationItem {
  value: string
  keys: string[]
  suggestedKey: string
  category: string
}

function flattenObject(obj: NestedObject, prefix = ''): Map<string, string> {
  const result = new Map<string, string>()
  for (const [key, value] of Object.entries(obj)) {
    const fullKey = prefix ? `${prefix}.${key}` : key
    if (typeof value === 'string') {
      result.set(fullKey, value)
    } else if (typeof value === 'object' && value !== null) {
      for (const [nestedKey, nestedValue] of flattenObject(value, fullKey)) {
        result.set(nestedKey, nestedValue)
      }
    }
  }
  return result
}

// Categories for consolidation
const CONSOLIDATION_RULES: { pattern: RegExp; category: string; suggestedPrefix: string }[] = [
  { pattern: /^status\.(DRAFT|PUBLISHED|CANCELLED)$/, category: 'status', suggestedPrefix: 'common:status' },
  { pattern: /\.status\.(DRAFT|PUBLISHED|CANCELLED)$/, category: 'status', suggestedPrefix: 'common:status' },
  { pattern: /\.actions\.(delete|edit|publish|unpublish|cancel)$/i, category: 'actions', suggestedPrefix: 'common:actions' },
  { pattern: /^(buttons|actions)\.(delete|edit|save|cancel)$/i, category: 'actions', suggestedPrefix: 'common:actions' },
  { pattern: /loading$/i, category: 'status', suggestedPrefix: 'common:status' },
  { pattern: /\.remove$/, category: 'actions', suggestedPrefix: 'common:actions' },
]

function suggestConsolidatedKey(keys: string[], _value: string): { key: string; category: string } {
  // Check if already in common
  const commonKey = keys.find((k) => k.startsWith('common:'))
  if (commonKey) {
    return { key: commonKey, category: 'existing' }
  }

  // Check patterns
  for (const rule of CONSOLIDATION_RULES) {
    for (const key of keys) {
      const keyPart = key.split(':')[1]
      if (rule.pattern.test(keyPart)) {
        const match = keyPart.match(rule.pattern)
        if (match) {
          const suffix = match[1] || keyPart.split('.').pop()
          return { key: `${rule.suggestedPrefix}.${suffix?.toLowerCase()}`, category: rule.category }
        }
      }
    }
  }

  // Default: create new common key based on most common pattern
  const keyParts = keys[0].split(':')[1].split('.')
  const lastPart = keyParts[keyParts.length - 1]
  return { key: `common:shared.${lastPart}`, category: 'shared' }
}

function main() {
  const reverseMap = new Map<string, string[]>()
  const files = fs.readdirSync(LOCALES_DIR).filter((f: string) => f.endsWith('.json'))

  for (const file of files) {
    const namespace = path.basename(file, '.json')
    const content = JSON.parse(fs.readFileSync(path.join(LOCALES_DIR, file), 'utf-8')) as NestedObject
    const flattened = flattenObject(content)

    for (const [key, value] of flattened) {
      const fullKey = `${namespace}:${key}`
      const existing = reverseMap.get(value) ?? []
      existing.push(fullKey)
      reverseMap.set(value, existing)
    }
  }

  // Find duplicates (2+ occurrences, not all in common)
  const consolidations: ConsolidationItem[] = []

  for (const [value, keys] of reverseMap.entries()) {
    if (keys.length < 2) continue

    // Skip if all keys are in common
    const nonCommonKeys = keys.filter((k) => !k.startsWith('common:'))
    if (nonCommonKeys.length < 2) continue

    // Skip interpolated values with different contexts
    if (value.includes('{{') && keys.some((k) => k.includes('_one') || k.includes('_many'))) {
      continue
    }

    const { key: suggestedKey, category } = suggestConsolidatedKey(keys, value)

    consolidations.push({
      value,
      keys: keys.sort(),
      suggestedKey,
      category,
    })
  }

  // Group by category
  const byCategory = consolidations.reduce(
    (acc, item) => {
      if (!acc[item.category]) acc[item.category] = []
      acc[item.category].push(item)
      return acc
    },
    {} as Record<string, ConsolidationItem[]>
  )

  // Output report
  const report = {
    summary: {
      totalDuplicates: consolidations.length,
      byCategory: Object.fromEntries(
        Object.entries(byCategory).map(([cat, items]) => [cat, items.length])
      ),
    },
    consolidations: byCategory,
  }

  console.log(JSON.stringify(report, null, 2))
}

main()
