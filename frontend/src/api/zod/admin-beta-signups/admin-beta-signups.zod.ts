import * as zod from 'zod'

/**
 * Get a paginated list of everyone who signed up to be notified about a beta
 * @summary List beta sign-ups
 */
export const listBetaSignupsQueryPageDefault = 0
export const listBetaSignupsQuerySizeDefault = 20

export const ListBetaSignupsQueryParams = zod.object({
  page: zod.int().default(listBetaSignupsQueryPageDefault).describe('Page number (0-indexed)'),
  size: zod.int().default(listBetaSignupsQuerySizeDefault).describe('Page size'),
})

export const ListBetaSignupsResponse = zod
  .object({
    signups: zod
      .array(
        zod
          .object({
            id: zod.string().describe('Sign-up ID'),
            email: zod.string().describe('Email'),
            createdAt: zod.iso
              .datetime({ offset: true })
              .describe('When the sign-up was submitted'),
          })
          .describe('A beta program sign-up, as seen by an admin')
      )
      .describe('List of sign-ups'),
    total: zod.int().describe('Total number of sign-ups'),
    page: zod.int().describe('Current page number'),
    size: zod.int().describe('Page size'),
  })
  .describe('Paginated beta sign-up list response')
