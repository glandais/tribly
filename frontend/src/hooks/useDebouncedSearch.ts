import { useEffect, useRef, useState } from 'react'
import { useDebouncedValue } from '@mantine/hooks'

/**
 * Drives a search box whose committed value lives in the URL.
 *
 * The returned value is a local mirror, so typing never lags behind the
 * debounced URL write. `lastSynced` tells apart "the URL changed because we
 * just pushed to it" (ignore) from "the URL changed externally" — back/forward
 * navigation, a canonical redirect — which must be reflected in the input.
 * Without it, our own URL commit would reset the input to a stale value
 * mid-typing.
 *
 * @param urlValue committed value, e.g. `filters.search ?? ''`
 * @param commit writes to the URL, e.g. `(v) => setFilters({ search: v || undefined })`
 */
export function useDebouncedSearch(
  urlValue: string,
  commit: (value: string) => void,
  delay = 300
): readonly [string, (value: string) => void] {
  const [local, setLocal] = useState(urlValue)
  const [debounced] = useDebouncedValue(local, delay)
  const lastSynced = useRef(urlValue)

  useEffect(() => {
    if (debounced === lastSynced.current) return
    lastSynced.current = debounced
    commit(debounced)
  }, [debounced, commit])

  useEffect(() => {
    if (urlValue === lastSynced.current) return
    lastSynced.current = urlValue
    setLocal(urlValue)
  }, [urlValue])

  return [local, setLocal] as const
}
