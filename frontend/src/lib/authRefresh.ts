/**
 * Whether a 401 on `url` is worth a refresh-and-retry.
 *
 * Everything outside `/api/auth/` is. Inside it, only the endpoints that require the access token
 * are: `logout-all`, the e-mail change request and the passkey management calls (list, delete,
 * registration). Excluded along with the public ones — login, refresh, passkey authentication —
 * an expired access token failed them with a 401 instead of being refreshed. Same table as the
 * mobile's `refreshesOn401` (docs/LEDGER_*.md MOB-32, WEB-28).
 */
export function refreshesOn401(url: string | undefined): boolean {
  if (!url) return true
  if (!url.includes('/api/auth/')) return true
  if (url.includes('/api/auth/logout-all') || url.includes('/api/auth/email/change-request')) {
    return true
  }
  // `authenticate` and `authentication-options` are public: they sign in.
  return url.includes('/api/auth/passkeys') && !url.includes('authenticat')
}
