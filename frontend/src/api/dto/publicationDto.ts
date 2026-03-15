import type { PostDto } from './postDto'
import type { PublicationType } from './publicationType'
import type { RideDto } from './rideDto'
import type { TripDto } from './tripDto'
import type { Visibility } from './visibility'

/**
 * Publication data
 */
export type PublicationDto =
  | (RideDto & {
      type?: PublicationType
      visibility?: Visibility
      name?: string
    })
  | (PostDto & {
      type?: PublicationType
      visibility?: Visibility
      name?: string
    })
  | (TripDto & {
      type?: PublicationType
      visibility?: Visibility
      name?: string
    })
