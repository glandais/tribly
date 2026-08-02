import * as zod from 'zod'

/**
 * Short-lived token authorizing the caller's own vector tile requests, to be passed as the 't' query parameter of the .mvt endpoints. Meant for clients whose map renderer has its own HTTP stack and can carry neither an Authorization header nor a session cookie. It grants nothing but tiles: no other endpoint accepts it.
 * @summary Mint a tile token
 */
export const MintResponse = zod
  .object({
    token: zod.string().describe("Token to pass as the 't' query parameter of the .mvt endpoints"),
    expiresAt: zod.iso
      .datetime({ offset: true })
      .describe('Absolute expiry instant (ISO 8601), for logs and diagnostics'),
    expiresIn: zod
      .int()
      .describe(
        'Seconds until expiry, measured at issuance. Schedule renewal on this, not on expiresAt: it is immune to a device clock that drifts.'
      ),
  })
  .describe('Short-lived token authorizing vector tile requests')
