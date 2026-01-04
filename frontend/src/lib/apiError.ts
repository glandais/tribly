import { ErrorResponse } from '@/api/dto'

export class ApiClientError extends Error {
  constructor(
    public status: number,
    public error: ErrorResponse
  ) {
    super(error?.message || 'API Client Error')
    this.name = 'ApiClientError'
  }
}
