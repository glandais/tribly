import type { Instant } from './instant.ts'

/**
 * Which map thumbnails to redraw from the geometry already stored. The criteria combine: the date window and the size threshold narrow the thumbnails that exist, the 'missing' flag adds the entities that have none. At least one criterion is required.
 */
export interface ThumbnailRegenerationRequest {
  /** Only thumbnails drawn at or after this instant */
  renderedFrom?: Instant
  /** Only thumbnails drawn before this instant */
  renderedTo?: Instant
  /**
   * Only thumbnails whose stored file is smaller than this many bytes, or has no file. A map drawn without its background weighs a few kB, a real one tens.
   * @minimum 1
   */
  suspectBelowBytes?: number
  /** Also redraw the routes, rides and trips that have a route to draw but lack a light or dark thumbnail — what a failed render leaves behind */
  includeMissing?: boolean
  /** List what would be redrawn, without redrawing anything */
  dryRun: boolean
  /**
   * Redraw at most this many entities (default 100)
   * @minimum 1
   * @maximum 1000
   */
  limit?: number
}
