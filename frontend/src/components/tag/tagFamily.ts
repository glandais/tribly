import { TagColor } from '@/api/dto'
import type { BadgeFamily } from '@/lib/badgeColors.generated'

/**
 * A tag's colour *is* a family of `contracts/brand-colors.yaml` (docs/BRANDING.md §3.6), spelled in
 * upper case by the API (`GREEN`) and in lower case by the charter and Mantine (`green`). No table:
 * lower-casing is the whole mapping, and the line below stops compiling the day the contract grows
 * a colour the charter does not have.
 */
const sameFamilies: BadgeFamily = '' as Lowercase<TagColor>
void sameFamilies

const FAMILIES: ReadonlySet<string> = new Set(
  Object.values(TagColor).map((color) => color.toLowerCase())
)

/** The family of a tag colour; a value unknown to this client (a newer API) renders gray. */
export function tagFamily(color: string): BadgeFamily {
  const family = color.toLowerCase()
  return FAMILIES.has(family) ? (family as BadgeFamily) : 'gray'
}
