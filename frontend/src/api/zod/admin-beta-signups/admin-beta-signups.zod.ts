import * as zod from 'zod'

/**
 * Get a paginated list of everyone who signed up to be notified about a beta
 * @summary List beta sign-ups
 */
export const listBetaSignupsQueryPageDefault = 0
export const listBetaSignupsQuerySizeDefault = 20

export const ListBetaSignupsQueryParams = zod.object({
  page: zod.number().default(listBetaSignupsQueryPageDefault).describe('Page number (0-indexed)'),
  size: zod.number().default(listBetaSignupsQuerySizeDefault).describe('Page size'),
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
    total: zod.number().describe('Total number of sign-ups'),
    page: zod.number().describe('Current page number'),
    size: zod.number().describe('Page size'),
  })
  .describe('Paginated beta sign-up list response')
