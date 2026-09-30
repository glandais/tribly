import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { notifications } from '@mantine/notifications'
import { CardActionsMenu } from './common'
import { paths } from '@/config/paths'
import { useAuthStore, selectUser } from '@/store/authStore'
import { invalidateRouteQueries } from '@/lib/routeCacheInvalidation'
import { getListPublicationsQueryKey } from '@/api/endpoints/publications/publications'
import {
  changeRideStatus,
  deleteRide,
  getDownloadRideIcsQueryKey,
  getGetRideQueryKey,
} from '@/api/endpoints/rides/rides'
import {
  changeTripStatus,
  deleteTrip,
  getDownloadTripIcsQueryKey,
  getGetTripQueryKey,
} from '@/api/endpoints/trips/trips'
import { changePostStatus, deletePost, getGetPostQueryKey } from '@/api/endpoints/posts/posts'
import {
  changeAdStatus,
  deleteAd,
  getGetAdQueryKey,
  getListAdsQueryKey,
} from '@/api/endpoints/ads/ads'
import { deleteRoute } from '@/api/endpoints/routes/routes'
import type { AdDto, PublicationDto, RideDto, RouteDto, TripDto } from '@/api/dto'

/*
 * The per-type actions of a list card (docs/LEDGER_DONE.md WEB-33). Each wrapper decides which
 * actions apply and wires them to the API; `CardActionsMenu` only renders them.
 *
 * Publishing goes through `PATCH …/status`, never through the full update: a list row is a
 * compact projection without a ride's groups or a trip's stages, and sending it back as the whole
 * entity would delete them.
 */

interface PublicationCardActionsProps {
  publication: PublicationDto
  /** Team ADMIN or ORGANIZER — the backend's rule for editing, publishing and deleting. */
  canManage: boolean
}

export function PublicationCardActions({ publication, canManage }: PublicationCardActionsProps) {
  const { t } = useTranslation()
  const queryClient = useQueryClient()
  const teamSlug = publication.team.slug
  const slug = publication.slug

  // A soft-deleted row (shown to admins only) is restored from its page, not from the list.
  if (publication.deleted) {
    return null
  }

  const refresh = (detailKey: readonly unknown[]) => {
    queryClient.invalidateQueries({ queryKey: getListPublicationsQueryKey(teamSlug) })
    queryClient.invalidateQueries({ queryKey: detailKey })
  }

  const published = () => {
    refresh(detailKeyOf())
    notifications.show({ message: t(publishedMessageOf()), color: 'green' })
  }
  const deleted = () => {
    refresh(detailKeyOf())
    notifications.show({ message: t(deletedMessageOf()), color: 'green' })
  }

  function detailKeyOf() {
    switch (publication.type) {
      case 'RIDE':
        return getGetRideQueryKey(teamSlug, slug)
      case 'TRIP':
        return getGetTripQueryKey(teamSlug, slug)
      case 'POST':
        return getGetPostQueryKey(teamSlug, slug)
    }
  }
  function publishedMessageOf() {
    switch (publication.type) {
      case 'RIDE':
        return 'rides.notifications.published' as const
      case 'TRIP':
        return 'trips.notifications.published' as const
      case 'POST':
        return 'posts.notifications.published' as const
    }
  }
  function deletedMessageOf() {
    switch (publication.type) {
      case 'RIDE':
        return 'rides.notifications.deleted' as const
      case 'TRIP':
        return 'trips.notifications.deleted' as const
      case 'POST':
        return 'posts.notifications.deleted' as const
    }
  }

  const publish = { status: 'PUBLISHED' as const }
  let editPath: string
  let onPublish: () => Promise<unknown>
  let onDelete: () => Promise<unknown>
  let deleteMessage: string
  let icsUrl: string | undefined
  switch (publication.type) {
    case 'RIDE':
      editPath = paths.rideEdit(teamSlug, slug)
      onPublish = () => changeRideStatus(teamSlug, slug, publish).then(published)
      onDelete = () => deleteRide(teamSlug, slug).then(deleted)
      deleteMessage = t('rides.detail.confirmations.delete')
      icsUrl = upcoming(publication as RideDto)
        ? getDownloadRideIcsQueryKey(teamSlug, slug)[0]
        : undefined
      break
    case 'TRIP':
      editPath = paths.tripEdit(teamSlug, slug)
      onPublish = () => changeTripStatus(teamSlug, slug, publish).then(published)
      onDelete = () => deleteTrip(teamSlug, slug).then(deleted)
      deleteMessage = t('trips.detail.confirmations.delete')
      icsUrl = upcoming(publication as TripDto)
        ? getDownloadTripIcsQueryKey(teamSlug, slug)[0]
        : undefined
      break
    case 'POST':
      editPath = paths.postEdit(teamSlug, slug)
      onPublish = () => changePostStatus(teamSlug, slug, publish).then(published)
      onDelete = () => deletePost(teamSlug, slug).then(deleted)
      deleteMessage = t('posts.detail.confirmations.delete')
      break
  }

  return (
    <CardActionsMenu
      editPath={canManage ? editPath : undefined}
      onPublish={canManage && publication.status === 'DRAFT' ? onPublish : undefined}
      onDelete={canManage ? onDelete : undefined}
      deleteMessage={deleteMessage}
      icsUrl={icsUrl}
    />
  )
}

/** Worth adding to a calendar: published, and not over yet (`finished`, computed by the server). */
function upcoming(publication: RideDto | TripDto): boolean {
  return publication.status === 'PUBLISHED' && !publication.finished
}

interface RouteCardActionsProps {
  route: RouteDto
  /** Team ADMIN or ORGANIZER. */
  canManage: boolean
}

/** A route has no status: edit and delete only. */
export function RouteCardActions({ route, canManage }: RouteCardActionsProps) {
  const { t } = useTranslation()
  const queryClient = useQueryClient()
  if (!canManage || route.deleted) {
    return null
  }
  const teamSlug = route.team.slug
  return (
    <CardActionsMenu
      editPath={paths.routeEdit(teamSlug, route.slug)}
      onDelete={() =>
        deleteRoute(teamSlug, route.slug).then(() => {
          invalidateRouteQueries(queryClient, teamSlug, route.slug)
          notifications.show({ message: t('routes.notifications.deleted'), color: 'green' })
        })
      }
      deleteMessage={t('routes.detail.deleteConfirm.message')}
    />
  )
}

interface AdCardActionsProps {
  ad: AdDto
  /** Team ADMIN. An ad is also managed by its author, which this component checks itself. */
  isTeamAdmin: boolean
}

/** An ad is managed by the team admin or its author — an organizer alone is not enough. */
export function AdCardActions({ ad, isTeamAdmin }: AdCardActionsProps) {
  const { t } = useTranslation()
  const queryClient = useQueryClient()
  const userId = useAuthStore(selectUser)?.id
  const canManage = isTeamAdmin || (!!userId && ad.createdById === userId)
  if (!canManage || ad.deleted) {
    return null
  }
  const teamSlug = ad.team.slug
  const refresh = () => {
    queryClient.invalidateQueries({ queryKey: getListAdsQueryKey(teamSlug) })
    queryClient.invalidateQueries({ queryKey: getGetAdQueryKey(teamSlug, ad.slug) })
  }
  return (
    <CardActionsMenu
      editPath={paths.adEdit(teamSlug, ad.slug)}
      onPublish={
        ad.status === 'DRAFT'
          ? () =>
              changeAdStatus(teamSlug, ad.slug, { status: 'PUBLISHED' }).then(() => {
                refresh()
                notifications.show({ message: t('ads.notifications.published'), color: 'green' })
              })
          : undefined
      }
      onDelete={() =>
        deleteAd(teamSlug, ad.slug).then(() => {
          refresh()
          notifications.show({ message: t('ads.notifications.deleted'), color: 'green' })
        })
      }
      deleteMessage={t('ads.detail.confirmations.delete')}
    />
  )
}
