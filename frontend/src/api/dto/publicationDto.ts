import type { PostDto } from './postDto.ts'
import type { PublicationType } from './publicationType.ts'
import type { RideDto } from './rideDto.ts'
import type { TripDto } from './tripDto.ts'
import type { Visibility } from './visibility.ts'

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
