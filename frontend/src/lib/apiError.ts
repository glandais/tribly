import { ErrorResponse } from '@/api/dto'

export class ApiClientError extends Error {
  constructor(
    public status: number,
    public error: ErrorResponse,
    /**
     * Seconds to wait, read from the `Retry-After` response header when the server sent one.
     * Only the quota responses declare it, so treat its absence as normal and fall back on a
     * message without a figure.
     */
    public retryAfterSeconds?: number
  ) {
    super(error.code)
    this.name = 'ApiClientError'
  }
}

/** Parses a `Retry-After` header holding a delta-seconds value. HTTP-date form is not used here. */
export function parseRetryAfter(value: unknown): number | undefined {
  const seconds = Number(value)
  return Number.isFinite(seconds) && seconds >= 0 ? seconds : undefined
}
