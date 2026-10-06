import type { RelativeWind } from './relativeWind.ts'

/**
 * The wind on the stretch between two checkpoints, as the rider meets it
 */
export interface WindSegmentDto {
  /** Start of the stretch, metres from the start of the leg */
  fromDistance: number
  /** End of the stretch, metres from the start of the leg */
  toDistance: number
  /** HEAD when the head component exceeds half the wind speed, TAIL below minus half, CROSS otherwise. Always shown with its label and an arrow, not by colour alone. */
  relativeWind: RelativeWind
  /** Mean head component, km/h, signed: positive against the rider */
  headwind: number
}
