import { z } from 'zod'

/** Malformed input must fall back to the default, never throw — `.catch()` last. */
export const searchField = z.string().optional().catch(undefined)

export const pageField = z.coerce.number().int().min(0).default(0).catch(0)

/** `size` is a per-page constant: it always equals its default, so it never reaches the URL. */
export const sizeField = (pageSize: number) => z.coerce.number().int().default(pageSize)

export const optionalNumberField = z.coerce.number().optional().catch(undefined)

export const COMMON_ALIAS = { search: 'q', page: 'p' } as const
