import type { ClientLogEntryDto, ClientLogLevel } from '@/api/dto'

/**
 * The browser's recent history, kept in memory for a bug report: console warnings and errors,
 * failed API calls, uncaught errors and navigations — the last {@link MAX_ENTRIES} of them.
 *
 * Nothing leaves the browser unless the member sends a report or an unhandled error is reported
 * (see errorReporter.ts). What goes in must already be safe to send: paths without their query
 * string (tokens travel there), no request or response bodies. The server redacts again anyway.
 */

export const MAX_ENTRIES = 200
/** The contract's limit on one entry. */
export const MAX_MESSAGE = 1000

const entries: ClientLogEntryDto[] = []

export function logEntry(level: ClientLogLevel, source: string, message: string): void {
  entries.push({
    ts: new Date().toISOString(),
    level,
    source,
    message: message.length > MAX_MESSAGE ? message.slice(0, MAX_MESSAGE - 1) + '…' : message,
  })
  if (entries.length > MAX_ENTRIES) {
    entries.splice(0, entries.length - MAX_ENTRIES)
  }
}

/** The buffer, oldest first — a copy. `limit` keeps the most recent ones. */
export function getLogEntries(limit = MAX_ENTRIES): ClientLogEntryDto[] {
  return entries.slice(-limit)
}

export function clearLogEntries(): void {
  entries.length = 0
}

/** A URL or path reduced to its path: query strings and fragments carry tokens. */
export function pathOnly(url: string): string {
  const cut = url.search(/[?#]/)
  const path = cut < 0 ? url : url.slice(0, cut)
  try {
    return new URL(path, 'http://x').pathname
  } catch {
    return path
  }
}

/** Renders console arguments the way a developer would read them, without object dumps. */
export function describe(value: unknown): string {
  if (value instanceof Error) return `${value.name}: ${value.message}`
  if (typeof value === 'string') return value
  if (value === null || value === undefined || typeof value !== 'object') return String(value)
  try {
    return JSON.stringify(value).slice(0, 200)
  } catch {
    return Object.prototype.toString.call(value)
  }
}

let installed = false

/**
 * Wraps `console.warn`/`console.error` so they also land in the buffer. Browser only — the SSR
 * server never calls it, and its process-wide console must not collect visitors' logs.
 */
export function installConsoleCapture(): void {
  if (installed || typeof window === 'undefined') return
  installed = true
  for (const level of ['warn', 'error'] as const) {
    const original = console[level].bind(console)
    console[level] = (...args: unknown[]) => {
      logEntry(level === 'warn' ? 'WARN' : 'ERROR', 'console', args.map(describe).join(' '))
      original(...args)
    }
  }
}
