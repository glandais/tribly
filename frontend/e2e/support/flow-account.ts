import { expect, type BrowserContext, type Locator, type Page } from '@playwright/test'
import type { CalendarTokenDto, PasskeyDto, UserDto } from '../../src/api/dto'
import { apiGet, refresh, withApi, type AuthResponse } from './api'
import { escapeRegExp, hydrated } from './ui'

/**
 * Helpers for the account journeys (flow-account.e2e.ts): the header's account controls on both
 * layouts, the links of the auth mails, the session cookie and the account as the API sees it, and
 * the personal ICS feed read the way a calendar application reads it.
 */

/**
 * The link to `path` (`/verify-email`, `/reset-password`) in a mail, as the backend wrote it — the
 * whole URL, so the test follows exactly what a reader would click.
 */
export function mailLinkTo(text: string, path: string): string {
  const match = text.match(
    new RegExp(`https?://[^\\s"<>]+${escapeRegExp(path)}\\?token=[^\\s"<>&]+`)
  )
  if (!match) throw new Error(`no ${path}?token= link in mail:\n${text}`)
  return match[0]
}

/**
 * Where the header's global controls (theme, language, account) are on screen: the header itself on
 * desktop, the burger's drawer on mobile — which this opens. On desktop the collapsed drawer still
 * holds a second copy of every control, hence the scoping.
 */
export async function headerControls(page: Page, isMobile: boolean): Promise<Locator> {
  if (!isMobile) return page.getByRole('banner')
  const drawer = page.getByRole('navigation').filter({ has: page.getByRole('combobox') })
  // The burger has no accessible name (Layout.tsx) — it is the header's only visible button on
  // this layout, the desktop group being display:none.
  const burger = page.getByRole('banner').getByRole('button')
  await hydrated(burger)
  await burger.click()
  await expect(drawer.getByRole('combobox')).toBeInViewport()
  return drawer
}

/** Signs out through the header: the account menu on desktop, the drawer's button on mobile. */
export async function signOutFromHeader(page: Page, isMobile: boolean, displayName: string) {
  if (isMobile) {
    const drawer = await headerControls(page, true)
    await drawer.getByRole('button', { name: 'Se déconnecter' }).click()
    return
  }
  const account = page.getByRole('banner').getByRole('button', { name: displayName })
  await hydrated(account)
  await account.click()
  await page.getByRole('menuitem', { name: 'Se déconnecter' }).click()
}

/** The refresh_token cookie the browser holds, if any. */
export async function sessionCookie(context: BrowserContext): Promise<string | undefined> {
  return (await context.cookies()).find((c) => c.name === 'refresh_token')?.value
}

/** Whether a refresh token still opens a session on the backend. */
export async function sessionIsAlive(refreshToken: string): Promise<boolean> {
  return (await refresh(refreshToken)) !== null
}

/** The account behind a refresh token, as GET /api/users/me returns it. */
export async function meFromSession(refreshToken: string): Promise<UserDto> {
  const auth = await refresh(refreshToken)
  if (!auth) throw new Error('the session no longer refreshes')
  return apiGet<UserDto>(auth, '/api/users/me')
}

/** The account's passkeys, as GET /api/auth/passkeys returns them. */
export const passkeysOf = (accessToken: string) =>
  apiGet<PasskeyDto[]>({ accessToken }, '/api/auth/passkeys')

/**
 * A CTAP2 virtual authenticator on `page`, through the Chrome DevTools Protocol (both projects run
 * Chromium): resident keys, user verification always granted, presence simulated — so the
 * browser's WebAuthn prompts resolve without a human. http://localhost is a secure context, so
 * WebAuthn is available there.
 */
export async function virtualAuthenticator(page: Page) {
  const cdp = await page.context().newCDPSession(page)
  await cdp.send('WebAuthn.enable')
  const { authenticatorId } = await cdp.send('WebAuthn.addVirtualAuthenticator', {
    options: {
      protocol: 'ctap2',
      transport: 'internal',
      hasResidentKey: true,
      hasUserVerification: true,
      isUserVerified: true,
      automaticPresenceSimulation: true,
    },
  })
  return {
    credentials: async () =>
      (await cdp.send('WebAuthn.getCredentials', { authenticatorId })).credentials,
  }
}

/** The account's calendar token and feed URLs, as GET /api/calendar/token returns them. */
export const calendarTokenOf = (who: AuthResponse) =>
  apiGet<CalendarTokenDto>(who, '/api/calendar/token')

/** What a calendar application gets from a feed URL: no session, no cookie, just the URL. */
export async function fetchFeed(url: string) {
  return withApi(undefined, async (api) => {
    const response = await api.get(url)
    return {
      status: response.status(),
      contentType: response.headers()['content-type'] ?? '',
      body: await response.text(),
    }
  })
}

/** One VEVENT of a feed: its properties by name (parameters dropped: `DTSTART;VALUE=DATE` → `DTSTART`). */
export type IcsEvent = Record<string, string>

/**
 * Parses an iCalendar body strictly enough to call it valid (RFC 5545): CRLF line endings, one
 * VCALENDAR wrapping everything, VERSION 2.0 and a PRODID, every VEVENT closed and carrying the
 * UID, DTSTAMP and DTSTART the RFC requires. Throws on the first violation.
 */
export function parseIcs(body: string): { name: string | undefined; events: IcsEvent[] } {
  if (!body.endsWith('\r\n')) throw new Error('an iCalendar body ends with CRLF')
  if (/[^\r]\n/.test(body)) throw new Error('an iCalendar line ends with a bare LF')
  // Unfold continuation lines (CRLF followed by a space or a tab).
  const lines = body
    .replace(/\r\n[ \t]/g, '')
    .split('\r\n')
    .slice(0, -1)
  if (lines[0] !== 'BEGIN:VCALENDAR' || lines.at(-1) !== 'END:VCALENDAR')
    throw new Error(`not one VCALENDAR: ${lines[0]} … ${lines.at(-1)}`)
  const calendar: Record<string, string> = {}
  const events: IcsEvent[] = []
  let event: IcsEvent | undefined
  for (const line of lines.slice(1, -1)) {
    const colon = line.indexOf(':')
    if (colon < 1) throw new Error(`not a content line: ${line}`)
    const name = line.slice(0, colon).split(';')[0]
    const value = line.slice(colon + 1)
    if (name === 'BEGIN' && value === 'VEVENT') {
      if (event) throw new Error('a VEVENT opened inside another')
      event = {}
    } else if (name === 'END' && value === 'VEVENT') {
      if (!event) throw new Error('END:VEVENT without BEGIN')
      for (const required of ['UID', 'DTSTAMP', 'DTSTART'])
        if (!event[required]) throw new Error(`a VEVENT has no ${required}`)
      events.push(event)
      event = undefined
    } else if (event) {
      event[name] = value
    } else {
      calendar[name] = value
    }
  }
  if (event) throw new Error('a VEVENT is never closed')
  if (calendar.VERSION !== '2.0') throw new Error(`VERSION is ${calendar.VERSION}`)
  if (!calendar.PRODID) throw new Error('no PRODID')
  return { name: calendar['X-WR-CALNAME'], events }
}

/** An instant as an iCalendar UTC date-time: 2026-09-27T07:29:20.218Z → 20260927T072920Z. */
export const icsDateTime = (instant: string) =>
  new Date(instant)
    .toISOString()
    .replace(/\.\d{3}Z$/, 'Z')
    .replace(/[-:]/g, '')
