import { z } from 'zod'

/** Malformed input must fall back to the default, never throw — `.catch()` last. */
export const searchField = z.string().optional().catch(undefined)

export const pageField = z.coerce.number().int().min(0).default(0).catch(0)

/** `size` is a per-page constant: it always equals its default, so it never reaches the URL. */
export const sizeField = (pageSize: number) => z.coerce.number().int().default(pageSize)

export const optionalNumberField = z.coerce.number().optional().catch(undefined)

export const COMMON_ALIAS = { search: 'q', page: 'p' } as const

/** At most this many tag ids are read from a URL — the API caps its own input further up. */
const MAX_TAG_IDS = 20

/**
 * `?tags=<id>,<id>` (plan D18): tag ids, never labels, so a renamed tag keeps its links. The API
 * param is a `string[]`; the URL keeps the comma-separated form, which `String(array)` writes back.
 * Anything that is not an id is dropped here, an id the team does not have is ignored by the API.
 * An empty selection is `undefined`, so it leaves both the URL and the query key.
 */
export const tagIdsField = z
  .string()
  .transform((raw) => {
    const ids = [
      ...new Set(
        raw
          .split(',')
          .map((id) => id.trim().toLowerCase())
          .filter((id) => /^[0-9a-z]{1,32}$/.test(id))
      ),
    ].slice(0, MAX_TAG_IDS)
    return ids.length > 0 ? ids : undefined
  })
  .optional()
  .catch(undefined)
