import { stack } from './stack'

/**
 * Reads the mail the backend sent through mailpit — the only way out for mail on the e2e stack.
 *
 * Messages are told apart by id, not by date: the caller snapshots the recipient's mailbox before
 * the request that sends mail, then waits for an id it has not seen. No clock is compared, so an old
 * (and already invalidated) OTP can never be picked up by mistake.
 */

interface Address {
  Address: string
}

/** A search hit: the envelope only, the content is fetched per message. */
interface MailpitSummary {
  ID: string
  To: Address[] | null
  Cc: Address[] | null
  Bcc: Address[] | null
}

/** A message as mailpit serves it — already MIME-decoded. */
interface MailpitMessage {
  Text: string
  HTML: string
}

async function get<T>(path: string): Promise<T> {
  const url = `${stack.mailpitURL}/api/v1${path}`
  const response = await fetch(url)
  if (!response.ok) throw new Error(`mailpit: ${url} answered ${response.status}`)
  return (await response.json()) as T
}

/** Newest first, like the mailbox itself. */
async function search(to: string): Promise<MailpitSummary[]> {
  const query = encodeURIComponent(`to:"${to}"`)
  const body = await get<{ messages: MailpitSummary[] | null }>(`/search?query=${query}&limit=250`)
  // `to:` matches substrings: a@x would also find ba@x. Keep the exact recipient only.
  const address = to.toLowerCase()
  return (body.messages ?? []).filter((m) =>
    [...(m.To ?? []), ...(m.Cc ?? []), ...(m.Bcc ?? [])].some(
      (a) => a.Address.toLowerCase() === address
    )
  )
}

const message = (id: string) => get<MailpitMessage>(`/message/${id}`)

/** Every non-empty body of the message: the plain text, then the HTML. */
const partsOf = (m: MailpitMessage) => [m.Text, m.HTML].filter(Boolean)

/** Ids of the mail already delivered to `to` — pass it to {@link waitForNewMail}. */
export async function mailbox(to: string): Promise<Set<string>> {
  return new Set((await search(to)).map((m) => m.ID))
}

/** The plain-text content of the first mail to `to` whose id is not in `seen`. */
export async function waitForNewMail(to: string, seen: Set<string>, timeoutMs = 20_000) {
  const deadline = Date.now() + timeoutMs
  while (Date.now() < deadline) {
    const fresh = (await search(to)).find((m) => !seen.has(m.ID))
    if (fresh) {
      const m = await message(fresh.ID)
      return m.Text || m.HTML
    }
    await new Promise((resolve) => setTimeout(resolve, 250))
  }
  throw new Error(`mailpit: no new mail for ${to} within ${timeoutMs} ms`)
}

export interface Mail {
  /** The top-level headers, names lowercased, first value of each (`reply-to`, `from`, …). */
  headers: Record<string, string>
  /** Every body of the message (text and HTML), decoded. */
  parts: string[]
}

/**
 * Every mail delivered to `to` so far, with its headers and all its parts — for what the text alone
 * does not show (a Reply-To, an address that must appear in no part).
 */
export async function mailsTo(to: string): Promise<Mail[]> {
  return Promise.all(
    (await search(to)).map(async ({ ID }) => {
      const [m, raw] = await Promise.all([
        message(ID),
        get<Record<string, string[]>>(`/message/${ID}/headers`),
      ])
      const headers: Record<string, string> = {}
      for (const [key, values] of Object.entries(raw)) headers[key.toLowerCase()] = values[0] ?? ''
      return { headers, parts: partsOf(m) }
    })
  )
}

export function otpCodeIn(text: string): string {
  const match = text.match(/\b(\d{6})\b/)
  if (!match) throw new Error(`no 6-digit code in mail:\n${text}`)
  return match[1]
}

/** The `token` query parameter of the first link in the mail. */
export function linkTokenIn(text: string): string {
  const match = text.match(/[?&]token=([^\s&"<>]+)/)
  if (!match) throw new Error(`no ?token= link in mail:\n${text}`)
  return match[1]
}
