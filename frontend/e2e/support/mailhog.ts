import { stack } from './stack'

/**
 * Reads the mail the backend sent through mailhog — the only way out for mail on the e2e stack.
 *
 * Messages are told apart by id, not by date: the caller snapshots the recipient's mailbox before
 * the request that sends mail, then waits for an id it has not seen. No clock is compared, so an old
 * (and already invalidated) OTP can never be picked up by mistake.
 */

interface MailhogPart {
  Headers: Record<string, string[]>
  Body: string
  MIME: { Parts: MailhogPart[] } | null
}

interface MailhogMessage {
  ID: string
  Content: MailhogPart
  MIME: { Parts: MailhogPart[] } | null
}

async function search(to: string): Promise<MailhogMessage[]> {
  const url = `${stack.mailhogURL}/api/v2/search?kind=to&query=${encodeURIComponent(to)}&limit=250`
  const response = await fetch(url)
  if (!response.ok) throw new Error(`mailhog: ${url} answered ${response.status}`)
  const body = (await response.json()) as { items: MailhogMessage[] | null }
  return body.items ?? []
}

/** Ids of the mail already delivered to `to` — pass it to {@link waitForNewMail}. */
export async function mailbox(to: string): Promise<Set<string>> {
  return new Set((await search(to)).map((m) => m.ID))
}

/** The plain-text content of the first mail to `to` whose id is not in `seen`. */
export async function waitForNewMail(to: string, seen: Set<string>, timeoutMs = 20_000) {
  const deadline = Date.now() + timeoutMs
  while (Date.now() < deadline) {
    const fresh = (await search(to)).find((m) => !seen.has(m.ID))
    if (fresh) return textOf(fresh)
    await new Promise((resolve) => setTimeout(resolve, 250))
  }
  throw new Error(`mailhog: no new mail for ${to} within ${timeoutMs} ms`)
}

function header(part: MailhogPart, name: string): string {
  const key = Object.keys(part.Headers).find((k) => k.toLowerCase() === name.toLowerCase())
  return key ? (part.Headers[key][0] ?? '') : ''
}

function decode(part: MailhogPart): string {
  const encoding = header(part, 'Content-Transfer-Encoding').toLowerCase()
  if (encoding === 'base64')
    return Buffer.from(part.Body.replace(/\s+/g, ''), 'base64').toString('utf8')
  if (encoding === 'quoted-printable') {
    const bytes = part.Body.replace(/=\r?\n/g, '').replace(/=([0-9A-F]{2})/gi, (_, hex: string) =>
      String.fromCharCode(parseInt(hex, 16))
    )
    return Buffer.from(bytes, 'latin1').toString('utf8')
  }
  return part.Body
}

/** Every text/plain leaf of the message, decoded; the raw body when it is not multipart. */
function textOf(message: MailhogMessage): string {
  const leaves: MailhogPart[] = []
  const walk = (part: MailhogPart) => {
    if (part.MIME?.Parts?.length) part.MIME.Parts.forEach(walk)
    else leaves.push(part)
  }
  if (message.MIME?.Parts?.length) message.MIME.Parts.forEach(walk)
  else leaves.push(message.Content)

  const plain = leaves.filter((p) => header(p, 'Content-Type').startsWith('text/plain'))
  return (plain.length ? plain : leaves).map(decode).join('\n')
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
