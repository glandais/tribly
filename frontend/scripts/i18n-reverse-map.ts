import fs from 'fs'
import path from 'path'
import { fileURLToPath } from 'url'

const __dirname = path.dirname(fileURLToPath(import.meta.url))
const LOCALES_DIR = path.join(__dirname, '../src/locales/fr')

type NestedObject = { [key: string]: string | NestedObject }

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

  // Sort by French value and sort arrays
  const sorted = Object.fromEntries(
    [...reverseMap.entries()]
      .sort(([a], [b]) => a.localeCompare(b, 'fr'))
      .map(([key, values]) => [key, values.sort()])
  )

  console.log(JSON.stringify(sorted, null, 2))
}

main()
