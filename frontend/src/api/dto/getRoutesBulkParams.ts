export type GetRoutesBulkParams = {
  /**
   * Whether to attach each route's sampled elevation profile.
   */
  elevation?: boolean
  /**
   * Resolution of the elevation profile when 'elevation' is true. Same clamping as the single-route elevation-profile endpoint.
   */
  elevationSamples?: number
  /**
   * Maximum number of track points per route, applied to every route of the batch — same semantics as on the single-route endpoint. Once the batch resolves to more than one route, this is capped at 1000 per route regardless of the value passed here (or of 'simplify'), so a request naming many slugs cannot be used to pull the full stored geometry of all of them at once.
   */
  points?: number
  /**
   * Douglas-Peucker tolerance in meters, applied to every route of the batch — same semantics as on the single-route endpoint.
   */
  simplify?: number
  /**
   * Route slug to include, repeatable. Capped at 50; unknown slugs and slugs the caller may not read are silently omitted from the response rather than erroring. Unlike GET /{routeSlug}, a slug that was renamed is also omitted rather than followed to the route's current slug: the single-route endpoint falls back to the rename history, this one does not. Same 'omit, never fail' contract as an unknown or unreadable slug, just for a different reason.
   */
  slug?: string[]
}
