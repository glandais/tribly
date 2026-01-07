import { useEffect, useRef } from 'react'
import { useNavigate } from 'react-router-dom'

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
  const checkedRef = useRef(false)

  useEffect(() => {
    if (checkedRef.current) return
    if (!canonicalPath) return

    checkedRef.current = true

    if (canonicalPath !== window.location.pathname) {
      navigate(canonicalPath, { replace: true })
    }
  }, [canonicalPath, navigate])
}
