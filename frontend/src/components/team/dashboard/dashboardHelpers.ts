import type { PublicationDto, RideDto, TripDto, PostDto } from '@/api/dto'
import { paths } from '@/config/paths'

/** Rows of a dashboard section, narrowed by their `type` discriminator. */
export function ridesOf(publications: PublicationDto[] | undefined): RideDto[] {
  return (publications ?? []).filter((p): p is RideDto => p.type === 'RIDE')
}

export function postsOf(publications: PublicationDto[] | undefined): PostDto[] {
  return (publications ?? []).filter((p): p is PostDto => p.type === 'POST')
}

export function isTrip(publication: PublicationDto): publication is TripDto {
  return publication.type === 'TRIP'
}

export function publicationPath(publication: PublicationDto): string {
  const teamSlug = publication.team.slug
  switch (publication.type) {
    case 'RIDE':
      return paths.ride(teamSlug, publication.slug)
    case 'TRIP':
      return paths.trip(teamSlug, publication.slug)
    case 'POST':
      return paths.post(teamSlug, publication.slug)
  }
}

export function publicationEditPath(publication: PublicationDto): string {
  const teamSlug = publication.team.slug
  switch (publication.type) {
    case 'RIDE':
      return paths.rideEdit(teamSlug, publication.slug)
    case 'TRIP':
      return paths.tripEdit(teamSlug, publication.slug)
    case 'POST':
      return paths.postEdit(teamSlug, publication.slug)
  }
}

/**
 * Whether a ride is routed nowhere: neither the ride nor any of its groups has a route. The
 * dashboard's « Sorties à venir » card says so instead of showing an empty distance.
 */
export function rideHasNoRoute(ride: RideDto): boolean {
  return !ride.routeSlug && ride.groupSummaries.every((g) => !g.routeSlug)
}

/** Router state that opens the invitation dialog of the members page on arrival. */
export const OPEN_INVITE_STATE = { openInvite: true } as const
