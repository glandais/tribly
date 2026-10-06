import type { PublicationDto, RideDto, TripDto } from '@/api/dto'

/** A ride or a trip: the publications that have a start and an end (API-85). */
export type DatedPublication = RideDto | TripDto

export function isDated(publication: PublicationDto): publication is DatedPublication {
  return publication.type === 'RIDE' || publication.type === 'TRIP'
}

/**
 * « En cours »: a ride or a trip that has started and is not over yet — `dateTime <= now <
 * endDateTime`, the end the server stores (API-85). A state derived on the client, like
 * « Inscrit »: the API's `finished` only says the start has passed. A cancelled one is never under
 * way. Such a ride keeps its place in the agenda's « À venir » (`when=UPCOMING` reads the same
 * end).
 */
export function isUnderWay(publication: PublicationDto, now: number = Date.now()): boolean {
  if (!isDated(publication) || publication.status === 'CANCELLED') return false
  const start = Date.parse(publication.dateTime)
  const end = Date.parse(publication.endDateTime)
  return Number.isFinite(start) && Number.isFinite(end) && start <= now && now < end
}
