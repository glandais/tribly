import fs from 'fs'
import path from 'path'
import { fileURLToPath } from 'url'

const __dirname = path.dirname(fileURLToPath(import.meta.url))
const LOCALES_FR_DIR = path.join(__dirname, '../src/locales/fr')
const LOCALES_EN_DIR = path.join(__dirname, '../src/locales/en')
const SRC_DIR = path.join(__dirname, '../src')

type NestedObject = { [key: string]: string | NestedObject }

// Define consolidation mappings: oldKey -> newKey
const CONSOLIDATIONS: Record<string, string> = {
  // Status - consolidate to common:status.*
  'ads:status.DRAFT': 'common:status.DRAFT',
  'ads:status.PUBLISHED': 'common:status.PUBLISHED',
  'ads:status.CANCELLED': 'common:status.CANCELLED',
  'posts:status.DRAFT': 'common:status.DRAFT',
  'posts:status.PUBLISHED': 'common:status.PUBLISHED',
  'posts:status.CANCELLED': 'common:status.CANCELLED',
  'rides:status.DRAFT': 'common:status.DRAFT',
  'rides:status.PUBLISHED': 'common:status.PUBLISHED',
  'rides:status.CANCELLED': 'common:status.CANCELLED',
  'trips:status.DRAFT': 'common:status.DRAFT',
  'trips:status.PUBLISHED': 'common:status.PUBLISHED',
  'trips:status.CANCELLED': 'common:status.CANCELLED',

  // Actions - consolidate to common:actions.*
  'ads:detail.actions.delete': 'common:actions.delete',
  'ads:detail.actions.edit': 'common:actions.edit',
  'ads:detail.actions.publish': 'common:actions.publish',
  'ads:detail.actions.unpublish': 'common:actions.unpublish',
  'ads:detail.actions.cancel': 'common:actions.cancelAction',
  'posts:detail.actions.delete': 'common:actions.delete',
  'posts:detail.actions.edit': 'common:actions.edit',
  'posts:detail.actions.publish': 'common:actions.publish',
  'posts:detail.actions.unpublish': 'common:actions.unpublish',
  'posts:detail.actions.cancel': 'common:actions.cancelAction',
  'rides:detail.actions.delete': 'common:actions.delete',
  'rides:detail.actions.edit': 'common:actions.edit',
  'rides:detail.actions.publish': 'common:actions.publish',
  'rides:detail.actions.unpublish': 'common:actions.unpublish',
  'trips:detail.actions.delete': 'common:actions.delete',
  'trips:detail.actions.edit': 'common:actions.edit',
  'trips:detail.actions.publish': 'common:actions.publish',
  'trips:detail.actions.unpublish': 'common:actions.unpublish',
  'rideTemplates:list.delete': 'common:actions.delete',
  'rideTemplates:list.edit': 'common:actions.edit',
  'comments:actions.delete': 'common:actions.delete',

  // Form labels - consolidate to common:form.*
  'ads:create.descriptionLabel': 'common:form.description',
  'ads:create.statusLabel': 'common:form.status',
  'ads:create.nameLabel': 'common:form.title',
  'posts:create.descriptionLabel': 'common:form.description',
  'posts:create.statusLabel': 'common:form.status',
  'posts:create.nameLabel': 'common:form.title',
  'rides:create.form.description.label': 'common:form.description',
  'rides:create.form.status.label': 'common:form.status',
  'trips:create.form.description.label': 'common:form.description',
  'trips:create.form.status.label': 'common:form.status',
  'routes:create.form.description': 'common:form.description',
  'rideTemplates:form.description.label': 'common:form.description',
  'rideTemplates:form.visibility.label': 'common:visibility.label',

  // Save changes variants
  'ads:edit.submit': 'common:actions.save',
  'posts:edit.submit': 'common:actions.save',
  'rides:edit.button': 'common:actions.save',
  'routes:edit.submit': 'common:actions.save',
  'trips:edit.button': 'common:actions.save',
  'teams:settings.saveChanges': 'common:actions.save',
  'teams:pages.form.save': 'common:actions.save',
}

// New keys to add to common.json
const NEW_COMMON_KEYS_FR: NestedObject = {
  status: {
    DRAFT: 'Brouillon',
    PUBLISHED: 'Publié',
    CANCELLED: 'Annulé',
  },
  actions: {
    delete: 'Supprimer',
    edit: 'Modifier',
    publish: 'Publier',
    unpublish: 'Dépublier',
    cancelAction: 'Annuler',
    save: 'Enregistrer'
  },
  form: {
    description: 'Description',
    status: 'Statut',
    title: 'Titre',
  },
}

const NEW_COMMON_KEYS_EN: NestedObject = {
  status: {
    DRAFT: 'Draft',
    PUBLISHED: 'Published',
    CANCELLED: 'Cancelled',
  },
  actions: {
    delete: 'Delete',
    edit: 'Edit',
    publish: 'Publish',
    unpublish: 'Unpublish',
    cancelAction: 'Cancel',
    save: 'Save',
    saveChanges: 'Save changes',
  },
  form: {
    description: 'Description',
    status: 'Status',
    title: 'Title',
  },
}

function deepMerge(target: NestedObject, source: NestedObject): NestedObject {
  const result = { ...target }
  for (const key of Object.keys(source)) {
    if (typeof source[key] === 'object' && source[key] !== null && !Array.isArray(source[key])) {
      result[key] = deepMerge((result[key] as NestedObject) || {}, source[key] as NestedObject)
    } else {
      result[key] = source[key]
    }
  }
  return result
}

function deleteNestedKey(obj: NestedObject, keyPath: string): boolean {
  const parts = keyPath.split('.')
  let current: NestedObject | undefined = obj

  for (let i = 0; i < parts.length - 1; i++) {
    current = current[parts[i]] as NestedObject | undefined
    if (!current) return false
  }

  const lastKey = parts[parts.length - 1]
  if (current && lastKey in current) {
    delete current[lastKey]
    return true
  }
  return false
}

function sortObjectKeys(obj: NestedObject): NestedObject {
  const sorted: NestedObject = {}
  for (const key of Object.keys(obj).sort((a, b) => a.localeCompare(b))) {
    const value = obj[key]
    if (typeof value === 'object' && value !== null) {
      sorted[key] = sortObjectKeys(value as NestedObject)
    } else {
      sorted[key] = value
    }
  }
  return sorted
}

function updateLocaleFiles() {
  console.log('📁 Updating locale files...\n')

  // Update French common.json
  const frCommonPath = path.join(LOCALES_FR_DIR, 'common.json')
  const frCommon = JSON.parse(fs.readFileSync(frCommonPath, 'utf-8')) as NestedObject
  const updatedFrCommon = sortObjectKeys(deepMerge(frCommon, NEW_COMMON_KEYS_FR))
  fs.writeFileSync(frCommonPath, JSON.stringify(updatedFrCommon, null, 2) + '\n')
  console.log('✅ Updated fr/common.json')

  // Update English common.json
  const enCommonPath = path.join(LOCALES_EN_DIR, 'common.json')
  const enCommon = JSON.parse(fs.readFileSync(enCommonPath, 'utf-8')) as NestedObject
  const updatedEnCommon = sortObjectKeys(deepMerge(enCommon, NEW_COMMON_KEYS_EN))
  fs.writeFileSync(enCommonPath, JSON.stringify(updatedEnCommon, null, 2) + '\n')
  console.log('✅ Updated en/common.json')

  // Remove old keys from domain-specific files
  const keysToRemove = new Map<string, string[]>()
  for (const oldKey of Object.keys(CONSOLIDATIONS)) {
    const [namespace, keyPath] = oldKey.split(':')
    if (namespace === 'common') continue
    if (!keysToRemove.has(namespace)) keysToRemove.set(namespace, [])
    keysToRemove.get(namespace)!.push(keyPath)
  }

  for (const [namespace, keys] of keysToRemove) {
    // French
    const frPath = path.join(LOCALES_FR_DIR, `${namespace}.json`)
    if (fs.existsSync(frPath)) {
      const content = JSON.parse(fs.readFileSync(frPath, 'utf-8')) as NestedObject
      let deleted = 0
      for (const key of keys) {
        if (deleteNestedKey(content, key)) deleted++
      }
      fs.writeFileSync(frPath, JSON.stringify(sortObjectKeys(content), null, 2) + '\n')
      console.log(`✅ Removed ${deleted} keys from fr/${namespace}.json`)
    }

    // English
    const enPath = path.join(LOCALES_EN_DIR, `${namespace}.json`)
    if (fs.existsSync(enPath)) {
      const content = JSON.parse(fs.readFileSync(enPath, 'utf-8')) as NestedObject
      let deleted = 0
      for (const key of keys) {
        if (deleteNestedKey(content, key)) deleted++
      }
      fs.writeFileSync(enPath, JSON.stringify(sortObjectKeys(content), null, 2) + '\n')
      console.log(`✅ Removed ${deleted} keys from en/${namespace}.json`)
    }
  }
}

function applyCodeReplacements() {
  console.log('\n📝 Updating source files...\n')

  // Build a map of namespace -> keys that need to change
  const namespaceKeyMap = new Map<string, Map<string, string>>()
  for (const [oldKey, newKey] of Object.entries(CONSOLIDATIONS)) {
    const [oldNs, oldPath] = oldKey.split(':')
    const [, newPath] = newKey.split(':')
    if (!namespaceKeyMap.has(oldNs)) namespaceKeyMap.set(oldNs, new Map())
    namespaceKeyMap.get(oldNs)!.set(oldPath, newPath)
  }

  function walkDir(dir: string) {
    const files = fs.readdirSync(dir)
    for (const file of files) {
      const fullPath = path.join(dir, file)
      const stat = fs.statSync(fullPath)
      if (stat.isDirectory()) {
        if (!file.includes('node_modules') && !file.includes('.git')) {
          walkDir(fullPath)
        }
      } else if (file.endsWith('.tsx') || file.endsWith('.ts')) {
        let content = fs.readFileSync(fullPath, 'utf-8')
        let modified = false
        let needsTCommon = false

        // Find which namespace this file uses
        const nsMatch = content.match(/useTranslation\(['"](\w+)['"]\)/)
        if (!nsMatch) continue
        const fileNamespace = nsMatch[1]

        // Check if file already has tCommon
        const hasTCommon = content.includes('{ t: tCommon }') || content.includes('t: tCommon')

        // Get keys that need to change for this namespace
        const keysToChange = namespaceKeyMap.get(fileNamespace)
        if (!keysToChange) continue

        for (const [oldPath, newPath] of keysToChange) {
          const escapedOldPath = oldPath.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')

          // Match t('key') pattern (where t is bound to the file's namespace)
          const pattern = new RegExp(`\\bt\\(['"\`]${escapedOldPath}['"\`]\\)`, 'g')

          if (pattern.test(content)) {
            content = content.replace(pattern, `tCommon('${newPath}')`)
            modified = true
            if (!hasTCommon) needsTCommon = true
          }
        }

        // Add tCommon import if needed
        if (needsTCommon && !hasTCommon) {
          // Add after existing useTranslation line
          content = content.replace(
            /const \{ t \} = useTranslation\(['"](\w+)['"]\)/,
            `const { t } = useTranslation('$1')\n  const { t: tCommon } = useTranslation('common')`
          )
          // Also handle destructuring with other vars
          content = content.replace(
            /const \{ t, ([^}]+) \} = useTranslation\(['"](\w+)['"]\)/,
            `const { t, $1 } = useTranslation('$2')\n  const { t: tCommon } = useTranslation('common')`
          )
        }

        if (modified) {
          fs.writeFileSync(fullPath, content)
          console.log(`✅ Updated ${fullPath.replace(SRC_DIR + '/', '')}`)
        }
      }
    }
  }

  walkDir(SRC_DIR)
}

function main() {
  const args = process.argv.slice(2)
  const dryRun = args.includes('--dry-run')

  console.log('🔄 i18n Migration Script\n')
  console.log(`Mode: ${dryRun ? 'DRY RUN (no changes)' : 'APPLY CHANGES'}\n`)

  if (dryRun) {
    console.log('📊 Consolidation mappings:\n')
    for (const [oldKey, newKey] of Object.entries(CONSOLIDATIONS)) {
      console.log(`  ${oldKey} -> ${newKey}`)
    }
  } else {
    updateLocaleFiles()
    applyCodeReplacements()
    console.log('\n✨ Migration complete!')
    console.log('\nNext steps:')
    console.log('  1. Run: pnpm i18n:lint')
    console.log('  2. Run: pnpm exec tsc --noEmit')
    console.log('  3. Review changes and test the application')
  }
}

main()
