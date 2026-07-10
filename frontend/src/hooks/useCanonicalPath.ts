import { useEffect, useRef } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'

/**
 * Hook that redirects to the canonical path if it differs from the current URL.
 * Only runs once on mount to avoid race conditions with form submissions.
 *
 * @param canonicalPath - The expected path (e.g., paths.ad(team.slug, ad.slug))
 *                        Pass undefined/null while data is still loading
 *
 * @example
 * // In a detail page:
 * useCanonicalPath(team && ad ? paths.ad(team.slug, ad.slug) : undefined)
 *
 * @example
 * // In a create page (optional - can skip for create pages):
 * useCanonicalPath(team ? paths.adNew(team.slug) : undefined)
 */
export function useCanonicalPath(canonicalPath: string | undefined | null): void {
  const navigate = useNavigate()
  const location = useLocation()
  const checkedRef = useRef(false)

  useEffect(() => {
    if (checkedRef.current) return
    if (!canonicalPath) return

    checkedRef.current = true

    // Compare against the router pathname, not window.location: on a pinned single-team host the
    // address bar is the stripped path while canonicalPath (from paths.xxx()) is prefixed, so a raw
    // window.location comparison would always mismatch and force a spurious redirect.
    if (canonicalPath !== location.pathname) {
      // Keep the query string: it carries the page's filters.
      navigate(canonicalPath + location.search + location.hash, { replace: true })
    }
  }, [canonicalPath, navigate, location.pathname, location.search, location.hash])
}
