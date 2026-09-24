/**
 * Browser-side plumbing of the biketeam migration page, kept out of the component so tests can
 * replace it: the pending request survives the login or sign-up detour in sessionStorage, and the
 * page leaves for biketeam with a full navigation (the callback is another site).
 *
 * See docs/plans/2026-09-22-biketeam-live-migration.md §12.2.
 */

export const REQUEST_STORAGE_KEY = 'pendingBiketeamMigrationRequest'

// sessionStorage throws in some private modes and when site data is blocked; losing the detour
// only means coming back from biketeam's link again, so failures are swallowed.

export function storeRequestToken(token: string): void {
  try {
    sessionStorage.setItem(REQUEST_STORAGE_KEY, token)
  } catch {
    // see above
  }
}

export function readStoredRequestToken(): string | null {
  try {
    return sessionStorage.getItem(REQUEST_STORAGE_KEY)
  } catch {
    return null
  }
}

export function clearStoredRequestToken(): void {
  try {
    sessionStorage.removeItem(REQUEST_STORAGE_KEY)
  } catch {
    // see above
  }
}

/** Leaves the app for a biketeam URL the API handed back (redirectUrl, cancelUrl). */
export function leaveForBiketeam(url: string): void {
  window.location.assign(url)
}
