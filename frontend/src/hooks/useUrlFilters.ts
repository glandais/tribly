import { useCallback, useMemo } from 'react'
import { useSearchParams } from 'react-router-dom'
import type { z } from 'zod'

type AnyFiltersSchema = z.ZodObject<z.ZodRawShape>

export interface UseUrlFiltersOptions<S extends AnyFiltersSchema> {
  /** Keys are API param names; every field needs a `.default()` or `.optional()`. */
  schema: S
  /** API param name -> short URL key. Unlisted keys keep their own name. */
  alias?: Partial<Record<keyof z.infer<S> & string, string>>
  /**
   * Keys written to the URL even when they equal their default. Needed when the
   * default itself is context-dependent (e.g. derived from auth), so a shared
   * link resolves the same way for everyone.
   */
  alwaysSerialize?: ReadonlyArray<keyof z.infer<S> & string>
}

export interface UseUrlFiltersResult<S extends AnyFiltersSchema> {
  filters: z.infer<S>
  /** Merge into the current filters. Resets `page` unless the patch sets it. */
  setFilters: (patch: Partial<z.infer<S>>) => void
  /** Replace every filter. Omitted keys fall back to their default. */
  replaceFilters: (next: Partial<z.infer<S>>) => void
  defaults: z.infer<S>
}

/**
 * Binds a page's filters to the query string, making them survive navigation
 * and shareable as a link.
 *
 * Values equal to their default are omitted from the URL, which also keeps the
 * per-page `size` constant out of it while still returning it in `filters`.
 * Every write is a history `replace`, so typing in a search box does not bury
 * the previous page under one history entry per keystroke.
 */
export function useUrlFilters<S extends AnyFiltersSchema>({
  schema,
  alias,
  alwaysSerialize,
}: UseUrlFiltersOptions<S>): UseUrlFiltersResult<S> {
  const [searchParams, setSearchParams] = useSearchParams()

  const keys = useMemo(() => Object.keys(schema.shape), [schema])
  const defaults = useMemo(() => schema.parse({}) as z.infer<S>, [schema])
  const forced = useMemo(() => new Set<string>(alwaysSerialize ?? []), [alwaysSerialize])

  const toUrlKey = useCallback(
    (apiKey: string) => (alias as Record<string, string> | undefined)?.[apiKey] ?? apiKey,
    [alias]
  )

  const read = useCallback(
    (params: URLSearchParams): z.infer<S> => {
      const raw: Record<string, string> = {}
      for (const apiKey of keys) {
        const value = params.get(toUrlKey(apiKey))
        // An empty param is an absent param: `z.coerce.number()` turns '' into 0.
        if (value !== null && value !== '') raw[apiKey] = value
      }
      return schema.parse(raw) as z.infer<S>
    },
    [keys, schema, toUrlKey]
  )

  const serialize = useCallback(
    (next: Record<string, unknown>): URLSearchParams => {
      const defaultValues = defaults as Record<string, unknown>
      const params = new URLSearchParams()
      for (const apiKey of keys) {
        const value = next[apiKey]
        if (value === undefined || value === null || value === '') continue
        if (value === defaultValues[apiKey] && !forced.has(apiKey)) continue
        params.set(toUrlKey(apiKey), String(value))
      }
      return params
    },
    [keys, defaults, forced, toUrlKey]
  )

  const filters = useMemo(() => read(searchParams), [read, searchParams])

  const setFilters = useCallback(
    (patch: Partial<z.infer<S>>) => {
      setSearchParams(
        (previous) => {
          // Rebuild from `previous` rather than a captured `filters`, so a
          // debounced search commit racing a page change never clobbers it.
          const next = { ...read(previous), ...patch } as Record<string, unknown>
          if (!('page' in patch) && 'page' in next) {
            next.page = (defaults as Record<string, unknown>).page
          }
          return serialize(next)
        },
        { replace: true }
      )
    },
    [setSearchParams, read, serialize, defaults]
  )

  const replaceFilters = useCallback(
    (next: Partial<z.infer<S>>) => {
      setSearchParams(serialize(next as Record<string, unknown>), { replace: true })
    },
    [setSearchParams, serialize]
  )

  return { filters, setFilters, replaceFilters, defaults }
}
