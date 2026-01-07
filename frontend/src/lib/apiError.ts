import { ErrorResponse } from '@/api/dto'

export class ApiClientError extends Error {
  constructor(
    public status: number,
    public error: ErrorResponse
  ) {
    super(error.code)
    this.name = 'ApiClientError'
  }
}
