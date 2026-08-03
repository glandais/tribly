import { lazy } from 'react'
import type { RoutesConfig, RouteParams } from './routes.types'
import { pathVariants } from './paths'
import { tRegister } from '@/lib/i18nUtils'
import {
  prefetchListAllPublicationsQuery,
  prefetchListPublicationsQuery,
} from '@/api/endpoints/publications/publications'
import {
  prefetchListTeamsQuery,
  prefetchGetTeamQuery,
  getListTeamsQueryKey,
  getGetTeamQueryKey,
} from '@/api/endpoints/teams/teams'
import { prefetchListMyParticipationsQuery } from '@/api/endpoints/users/users'
import { prefetchGetEventsQuery } from '@/api/endpoints/calendar/calendar'
import { PARTICIPATION_COUNT_PARAMS } from '@/components/profile/participationCountParams'
import { getInitialCalendarRange } from '@/hooks/useCalendarDateRange'
import { prefetchListMyInvitationsQuery } from '@/api/endpoints/invitations/invitations'
import { prefetchGetRideQuery, getGetRideQueryKey } from '@/api/endpoints/rides/rides'
import { prefetchGetTripQuery, getGetTripQueryKey } from '@/api/endpoints/trips/trips'
import {
  listRideComments,
  getListRideCommentsQueryKey,
} from '@/api/endpoints/ride-comments/ride-comments'
import {
  listTripComments,
  getListTripCommentsQueryKey,
} from '@/api/endpoints/trip-comments/trip-comments'
import { prefetchGetPostQuery } from '@/api/endpoints/posts/posts'
import {
  listPostComments,
  getListPostCommentsQueryKey,
} from '@/api/endpoints/post-comments/post-comments'
import {
  prefetchGetRouteQuery,
  prefetchGetRouteUsagesQuery,
  prefetchGetRoutesBulkQuery,
} from '@/api/endpoints/routes/routes'
import { ROUTES_BULK_MAX_SLUGS } from '@/hooks/useRoutesBulk'
import { prefetchGetPageQuery, prefetchListPagesQuery } from '@/api/endpoints/team-pages/team-pages'
import { prefetchListPlacesQuery } from '@/api/endpoints/places/places'
import { prefetchGetMembersQuery } from '@/api/endpoints/team-members/team-members'
import { prefetchListInvitationsQuery } from '@/api/endpoints/team-invitations/team-invitations'
import { prefetchListTemplatesQuery } from '@/api/endpoints/ride-templates/ride-templates'
import { placeFiltersSchema } from '@/hooks/filters/placeFilters'
import { teamMemberFiltersSchema } from '@/hooks/filters/teamMemberFilters'
import { rideTemplateFiltersSchema } from '@/hooks/filters/rideTemplateFilters'
import { placeAutocompleteParams } from '@/components/common/placeAutocompleteParams'
import { InvitationStatus } from '@/api/dto'
import { prefetchGetAdQuery, prefetchListAdsQuery } from '@/api/endpoints/ads/ads'
import { prefetchGetPreviewQuery } from '@/api/endpoints/gpx-previews/gpx-previews'
import {
  prefetchListAllRoutesQuery,
  prefetchListRoutesQuery,
  prefetchGetAllRoutesBoundsQuery,
} from '@/api/endpoints/routes/routes'
import { prefetchGetAvailableServicesQuery } from '@/api/endpoints/gps-services/gps-services'
import {
  listRouteComments,
  getListRouteCommentsQueryKey,
} from '@/api/endpoints/route-comments/route-comments'
import { COMMENT_LIST_OPTIONS } from '@/hooks/useComments'
import {
  homeMeta,
  teamsMeta,
  teamDetailMeta,
  teamAboutMeta,
  teamPageMeta,
  rideMeta,
  tripMeta,
  stageMeta,
  postMeta,
  routeMeta,
  adMeta,
  gpxPreviewMeta,
  appsMeta,
} from './routeMeta'
import {
  publicationApiParams,
  publicationFiltersSchema,
  publicationFiltersAlias,
} from '@/hooks/filters/publicationFilters'
import { readUrlFilters } from '@/hooks/useUrlFilters'
import { makeHomeFiltersSchema, homeFiltersAlias } from '@/hooks/filters/homeFilters'
import { makeTeamFiltersSchema, teamFiltersAlias, teamApiParams } from '@/hooks/filters/teamFilters'
import {
  routeFiltersSchema,
  routeFiltersAlias,
  routeApiParams,
  makeAllRouteFiltersSchema,
  allRouteFiltersAlias,
  allRouteApiParams,
} from '@/hooks/filters/routeFilters'
import { adFiltersSchema, adFiltersAlias, adApiParams } from '@/hooks/filters/adFilters'
import { membershipToMinRole, type MembershipFilterValue } from '@/hooks/filters/membership'
import { useAuthStore } from '@/store/authStore'
import {
  MinRole,
  SortDirection,
  type TeamListResponse,
  type TeamDetailDto,
  type TripDto,
  type RideDto,
  type GetRoutesBulkParams,
} from '@/api/dto'
import { hourAlignedNowIso } from '@/utils/nowIso'
import { NEXT_RIDE_PARAMS } from '@/pages/home/nextRideParams'
import type { QueryClient } from '@tanstack/react-query'

/**
 * Comments are member-only on rides/trips/posts/routes (each detail page's own `isMember`, from
 * the team's `role`) — resolved from the team query prefetched just before calling this, same
 * shape `useComments` builds. Skipped entirely for a non-member, matching what the page renders.
 */
async function prefetchMemberComments(
  queryClient: QueryClient,
  teamSlug: string,
  entitySlug: string,
  listComments: (
    teamSlug: string,
    entitySlug: string,
    params: { page: number; size: number; sort: SortDirection }
  ) => Promise<unknown>,
  getQueryKey: (teamSlug: string, entitySlug: string) => readonly unknown[]
) {
  const team = queryClient.getQueryData<TeamDetailDto>(getGetTeamQueryKey(teamSlug))
  if (!team?.role) return

  await queryClient.prefetchInfiniteQuery({
    queryKey: [...getQueryKey(teamSlug, entitySlug), COMMENT_LIST_OPTIONS],
    queryFn: ({ pageParam }: { pageParam: number }) =>
      listComments(teamSlug, entitySlug, { page: pageParam, ...COMMENT_LIST_OPTIONS }),
    initialPageParam: 0,
  })
}

/** Mirrors `useRoutesBulk`'s own chunking against `ROUTES_BULK_MAX_SLUGS` so the query keys match. */
async function prefetchRoutesBulkChunked(
  queryClient: QueryClient,
  teamSlug: string,
  slugs: string[],
  params?: Omit<GetRoutesBulkParams, 'slug'>
) {
  const dedupedSlugs = Array.from(new Set(slugs)).sort()
  if (dedupedSlugs.length === 0) return

  await Promise.all(
    Array.from({ length: Math.ceil(dedupedSlugs.length / ROUTES_BULK_MAX_SLUGS) }, (_, i) =>
      dedupedSlugs.slice(i * ROUTES_BULK_MAX_SLUGS, (i + 1) * ROUTES_BULK_MAX_SLUGS)
    ).map((slugChunk) =>
      prefetchGetRoutesBulkQuery(queryClient, teamSlug, { ...params, slug: slugChunk })
    )
  )
}

/**
 * `RoutesMapView` on the trip page loads every stage's route via `useRoutesBulk`, keyed off the
 * trip's own `routeSlug` (single-stage trips) or its stages' route slugs — data already sitting in
 * the trip query just prefetched above. Mirrors `useRoutesBulk`'s dedup/sort exactly so the query
 * key matches.
 */
async function prefetchTripRoutesBulk(
  queryClient: QueryClient,
  teamSlug: string,
  tripSlug: string
) {
  const trip = queryClient.getQueryData<TripDto>(getGetTripQueryKey(teamSlug, tripSlug))
  if (!trip) return

  const slugs = trip.routeSlug
    ? [trip.routeSlug]
    : (trip.stages ?? []).flatMap((stage) => (stage.route?.slug ? [stage.route.slug] : []))
  await prefetchRoutesBulkChunked(queryClient, teamSlug, slugs)
}

/**
 * RideDetailPage's own `useRoutesBulk` (group asset links) and `RoutesMapView`'s (the map) share
 * this exact slug set and params — the ride's own `routeSlug` plus every group's (falling back to
 * the ride's route when a group has none) — so a single prefetch covers both call sites' cache.
 */
async function prefetchRideRoutesBulk(
  queryClient: QueryClient,
  teamSlug: string,
  rideSlug: string
) {
  const ride = queryClient.getQueryData<RideDto>(getGetRideQueryKey(teamSlug, rideSlug))
  if (!ride) return

  const slugs = (ride.groups ?? [])
    .map((g) => g.routeSlug || ride.routeSlug)
    .filter((s): s is string => !!s)
  await prefetchRoutesBulkChunked(queryClient, teamSlug, slugs)
}

/**
 * Mirrors `useMembershipDefault` exactly, probe included: signed in with at least one team, the
 * cross-team listings default to `member`; anonymous, or signed in with no team, to `all`.
 *
 * Returns the page's own filter value rather than a `MinRole`, because that is what feeds the
 * filter schema — the default only applies when the URL doesn't spell `membership` out, and a
 * shared link generally does (`MEMBERSHIP_ALWAYS_SERIALIZE`).
 */
async function resolveMembershipDefault(queryClient: QueryClient): Promise<MembershipFilterValue> {
  if (!useAuthStore.getState().isAuthenticated) return 'all'
  const membershipParams = { minRole: MinRole.MEMBER, page: 0, size: 1 }
  await prefetchListTeamsQuery(queryClient, membershipParams)
  const teams = queryClient.getQueryData<TeamListResponse>(getListTeamsQueryKey(membershipParams))
  return teams && teams.total > 0 ? 'member' : 'all'
}

/**
 * A list route prefetches the window `usePaginatedQuery` reads: the page the URL asks for, plus
 * the neighbours it fetches ahead on the client (next, and previous when there is one). Leaving
 * one out doesn't lose the data, it just moves the round trip back after hydration — which is
 * exactly what the crawler reports as a gap.
 */
async function prefetchPageWindow<P extends { page: number }>(
  params: P,
  run: (pageParams: P) => Promise<unknown>
) {
  const pages = [params.page, params.page + 1, ...(params.page > 0 ? [params.page - 1] : [])]
  await Promise.all(pages.map((page) => run({ ...params, page })))
}

/**
 * The two `PlaceAutocomplete` fields a ride form mounts (start and end), each querying its own
 * filtered place list before the visitor touches anything.
 */
async function prefetchRideFormPlaces(queryClient: QueryClient, teamSlug: string) {
  await Promise.all([
    prefetchListPlacesQuery(queryClient, teamSlug, placeAutocompleteParams({ filterStart: true })),
    prefetchListPlacesQuery(queryClient, teamSlug, placeAutocompleteParams({ filterEnd: true })),
  ])
}

/**
 * `prefetch` for an authenticated, team-scoped screen — every form and admin page under
 * `/teams/{slug}/…`.
 *
 * They all render the team's name (breadcrumb, layout header) yet none of them used to prefetch
 * it, so `GET /api/teams/{slug}` was the single most repeated gap the crawler found: ~20 routes
 * fetching the same already-cacheable object after the first paint. `extra` adds whatever else the
 * page reads — pass the same `prefetchXxxQuery` its read-only sibling route uses, so the query key
 * matches by construction rather than by hand-copying params.
 *
 * Skipped entirely for an anonymous request: these routes render a redirect, not a page, so the
 * fetch would be wasted (and the API call pointless) before the guard sends the visitor away.
 */
function teamScopedPrefetch(
  extra?: (queryClient: QueryClient, params: RouteParams) => Promise<unknown>
): (queryClient: QueryClient, params: RouteParams) => Promise<void> {
  return async (queryClient, params) => {
    if (!useAuthStore.getState().isAuthenticated) return
    await Promise.all([
      prefetchGetTeamQuery(queryClient, params.teamSlug!),
      extra ? extra(queryClient, params) : Promise.resolve(),
    ])
  }
}

// Lazy load page components for code splitting
const HomePage = lazy(() => import('../pages/home/HomePage').then((m) => ({ default: m.HomePage })))
const LoginPage = lazy(() =>
  import('../pages/auth/LoginPage').then((m) => ({ default: m.LoginPage }))
)
const DeviceVerifyPage = lazy(() =>
  import('../pages/device/DeviceVerifyPage').then((m) => ({ default: m.DeviceVerifyPage }))
)
const VerifyEmailPage = lazy(() =>
  import('../pages/auth/VerifyEmailPage').then((m) => ({ default: m.VerifyEmailPage }))
)
const AcceptInvitationPage = lazy(() =>
  import('../pages/invitation/AcceptInvitationPage').then((m) => ({
    default: m.AcceptInvitationPage,
  }))
)
const ForgotPasswordPage = lazy(() =>
  import('../pages/auth/ForgotPasswordPage').then((m) => ({ default: m.ForgotPasswordPage }))
)
const ResetPasswordPage = lazy(() =>
  import('../pages/auth/ResetPasswordPage').then((m) => ({ default: m.ResetPasswordPage }))
)
const StravaCallbackPage = lazy(() =>
  import('../pages/auth/StravaCallbackPage').then((m) => ({ default: m.StravaCallbackPage }))
)
const CompleteAccountPage = lazy(() =>
  import('../pages/auth/CompleteAccountPage').then((m) => ({ default: m.CompleteAccountPage }))
)
const UserProfilePage = lazy(() =>
  import('../pages/auth/UserProfilePage').then((m) => ({ default: m.UserProfilePage }))
)
const TeamListPage = lazy(() =>
  import('../pages/team/TeamListPage').then((m) => ({ default: m.TeamListPage }))
)
const CreateTeamPage = lazy(() =>
  import('../pages/team/CreateTeamPage').then((m) => ({ default: m.CreateTeamPage }))
)
const PublicationListPage = lazy(() =>
  import('../pages/publication/PublicationListPage').then((m) => ({
    default: m.PublicationListPage,
  }))
)
const TeamMembersPage = lazy(() =>
  import('../pages/team/TeamMembersPage').then((m) => ({ default: m.TeamMembersPage }))
)
const TeamSettingsPage = lazy(() =>
  import('../pages/team/TeamSettingsPage').then((m) => ({ default: m.TeamSettingsPage }))
)
const TeamAdminPage = lazy(() =>
  import('../pages/team/TeamAdminPage').then((m) => ({ default: m.TeamAdminPage }))
)
const TeamPlacesPage = lazy(() =>
  import('../pages/team/TeamPlacesPage').then((m) => ({ default: m.TeamPlacesPage }))
)
const TeamAboutPage = lazy(() =>
  import('../pages/team/TeamAboutPage').then((m) => ({ default: m.TeamAboutPage }))
)
const TeamPageDetailPage = lazy(() =>
  import('../pages/team/TeamPageDetailPage').then((m) => ({ default: m.TeamPageDetailPage }))
)
const TeamPagesAdminPage = lazy(() =>
  import('../pages/team/TeamPagesAdminPage').then((m) => ({ default: m.TeamPagesAdminPage }))
)
const CreateTeamPagePage = lazy(() =>
  import('../pages/team/CreateTeamPagePage').then((m) => ({ default: m.CreateTeamPagePage }))
)
const EditTeamPagePage = lazy(() =>
  import('../pages/team/EditTeamPagePage').then((m) => ({ default: m.EditTeamPagePage }))
)
const RideDetailPage = lazy(() =>
  import('../pages/ride/RideDetailPage').then((m) => ({ default: m.RideDetailPage }))
)
const CreateRidePage = lazy(() =>
  import('../pages/ride/CreateRidePage').then((m) => ({ default: m.CreateRidePage }))
)
const EditRidePage = lazy(() =>
  import('../pages/ride/EditRidePage').then((m) => ({ default: m.EditRidePage }))
)
const RideTemplateListPage = lazy(() =>
  import('../pages/ridetemplate/RideTemplateListPage').then((m) => ({
    default: m.RideTemplateListPage,
  }))
)
const CreateRideTemplatePage = lazy(() =>
  import('../pages/ridetemplate/CreateRideTemplatePage').then((m) => ({
    default: m.CreateRideTemplatePage,
  }))
)
const EditRideTemplatePage = lazy(() =>
  import('../pages/ridetemplate/EditRideTemplatePage').then((m) => ({
    default: m.EditRideTemplatePage,
  }))
)
const TripDetailPage = lazy(() =>
  import('../pages/trip/TripDetailPage').then((m) => ({ default: m.TripDetailPage }))
)
const CreateTripPage = lazy(() =>
  import('../pages/trip/CreateTripPage').then((m) => ({ default: m.CreateTripPage }))
)
const EditTripPage = lazy(() =>
  import('../pages/trip/EditTripPage').then((m) => ({ default: m.EditTripPage }))
)
const StageDetailPage = lazy(() =>
  import('../pages/trip/StageDetailPage').then((m) => ({ default: m.StageDetailPage }))
)
const StageFullscreenMapPage = lazy(() =>
  import('../pages/trip/StageFullscreenMapPage').then((m) => ({
    default: m.StageFullscreenMapPage,
  }))
)
const PostDetailPage = lazy(() =>
  import('../pages/post/PostDetailPage').then((m) => ({ default: m.PostDetailPage }))
)
const CreatePostPage = lazy(() =>
  import('../pages/post/CreatePostPage').then((m) => ({ default: m.CreatePostPage }))
)
const EditPostPage = lazy(() =>
  import('../pages/post/EditPostPage').then((m) => ({ default: m.EditPostPage }))
)
const RouteListPage = lazy(() =>
  import('../pages/route/RouteListPage').then((m) => ({ default: m.RouteListPage }))
)
const RouteDetailPage = lazy(() =>
  import('../pages/route/RouteDetailPage').then((m) => ({ default: m.RouteDetailPage }))
)
const RouteFullscreenMapPage = lazy(() =>
  import('../pages/route/RouteFullscreenMapPage').then((m) => ({
    default: m.RouteFullscreenMapPage,
  }))
)
const CreateRoutePage = lazy(() =>
  import('../pages/route/CreateRoutePage').then((m) => ({ default: m.CreateRoutePage }))
)
const EditRoutePage = lazy(() =>
  import('../pages/route/EditRoutePage').then((m) => ({ default: m.EditRoutePage }))
)
const AllRoutesMapPage = lazy(() =>
  import('../pages/route/AllRoutesMapPage').then((m) => ({ default: m.AllRoutesMapPage }))
)
const GpxToolsPage = lazy(() =>
  import('../pages/gpxtool/GpxToolsPage').then((m) => ({ default: m.GpxToolsPage }))
)
const CreateGpxPreviewPage = lazy(() =>
  import('../pages/gpxtool/CreateGpxPreviewPage').then((m) => ({ default: m.CreateGpxPreviewPage }))
)
const MyGpxPreviewsPage = lazy(() =>
  import('../pages/gpxtool/MyGpxPreviewsPage').then((m) => ({ default: m.MyGpxPreviewsPage }))
)
const GpxPreviewPage = lazy(() =>
  import('../pages/gpxtool/GpxPreviewPage').then((m) => ({ default: m.GpxPreviewPage }))
)
const GpxPreviewFullscreenMapPage = lazy(() =>
  import('../pages/gpxtool/GpxPreviewFullscreenMapPage').then((m) => ({
    default: m.GpxPreviewFullscreenMapPage,
  }))
)
const EditGpxPreviewPage = lazy(() =>
  import('../pages/gpxtool/EditGpxPreviewPage').then((m) => ({ default: m.EditGpxPreviewPage }))
)
const RoutesMapPage = lazy(() =>
  import('../pages/route/RoutesMapPage').then((m) => ({ default: m.RoutesMapPage }))
)
const AllRoutesPage = lazy(() =>
  import('../pages/route/AllRoutesPage').then((m) => ({ default: m.AllRoutesPage }))
)
const CalendarPage = lazy(() =>
  import('../pages/calendar/CalendarPage').then((m) => ({ default: m.CalendarPage }))
)
const TeamCalendarPage = lazy(() =>
  import('../pages/calendar/TeamCalendarPage').then((m) => ({ default: m.TeamCalendarPage }))
)
const AdListPage = lazy(() =>
  import('../pages/ad/AdListPage').then((m) => ({ default: m.AdListPage }))
)
const AdDetailPage = lazy(() =>
  import('../pages/ad/AdDetailPage').then((m) => ({ default: m.AdDetailPage }))
)
const CreateAdPage = lazy(() =>
  import('../pages/ad/CreateAdPage').then((m) => ({ default: m.CreateAdPage }))
)
const EditAdPage = lazy(() =>
  import('../pages/ad/EditAdPage').then((m) => ({ default: m.EditAdPage }))
)
const AdminDashboardPage = lazy(() =>
  import('../pages/admin/AdminDashboardPage').then((m) => ({ default: m.AdminDashboardPage }))
)
const AdminDomainsPage = lazy(() =>
  import('../pages/admin/AdminDomainsPage').then((m) => ({ default: m.AdminDomainsPage }))
)
const AdminTeamsPage = lazy(() =>
  import('../pages/admin/AdminTeamsPage').then((m) => ({ default: m.AdminTeamsPage }))
)
const AdminUsersPage = lazy(() =>
  import('../pages/admin/AdminUsersPage').then((m) => ({ default: m.AdminUsersPage }))
)
const AdminBetaSignupsPage = lazy(() =>
  import('../pages/admin/AdminBetaSignupsPage').then((m) => ({
    default: m.AdminBetaSignupsPage,
  }))
)
const PrivacyPolicyPage = lazy(() =>
  import('../pages/legal/PrivacyPolicyPage').then((m) => ({ default: m.PrivacyPolicyPage }))
)
const TermsOfServicePage = lazy(() =>
  import('../pages/legal/TermsOfServicePage').then((m) => ({ default: m.TermsOfServicePage }))
)
const AppsPage = lazy(() => import('../pages/apps/AppsPage').then((m) => ({ default: m.AppsPage })))

export const routesConfig: RoutesConfig = [
  // === Home ===
  {
    id: 'home',
    paths: pathVariants.home(),
    component: HomePage,
    auth: 'public',
    parentId: null,
    index: true,
    navGroup: 'home',
    breadcrumb: { type: 'static', i18nKey: tRegister('home.tabs.feed') },
    // Resolves the feed the URL actually asks for, filters and page included, the same way
    // HomePage does: the membership probe of `useMembershipDefault` feeds the schema's default,
    // the query string overrides it, and `publicationApiParams` projects the result.
    prefetch: async (queryClient, _params, url) => {
      const membershipDefault = await resolveMembershipDefault(queryClient)
      if (useAuthStore.getState().isAuthenticated) {
        await prefetchListMyParticipationsQuery(queryClient, {
          from: hourAlignedNowIso(),
          ...NEXT_RIDE_PARAMS,
        })
      }
      const filters = readUrlFilters(url.searchParams, {
        schema: makeHomeFiltersSchema(membershipDefault),
        alias: homeFiltersAlias,
      })
      const feedParams = {
        ...publicationApiParams(filters, hourAlignedNowIso()),
        minRole: membershipToMinRole[filters.membership],
      }
      await prefetchPageWindow(feedParams, (p) => prefetchListAllPublicationsQuery(queryClient, p))
    },
    meta: homeMeta,
  },
  {
    id: 'all-routes',
    paths: pathVariants.allRoutes(),
    component: AllRoutesPage,
    auth: 'public',
    parentId: null,
    navGroup: 'home',
    breadcrumb: { type: 'static', i18nKey: tRegister('nav.routes') },
    // Matches AllRoutesPage's initial useListAllRoutes key when no URL filters are set. Signed
    // in, the list's `minRole` depends on team membership (see useMembershipDefault) —
    // replicated here so the resolved variant, not a guess, lands in the cache (same pattern as
    // the `home` and `teams` routes).
    prefetch: async (queryClient, _params, url) => {
      const filters = readUrlFilters(url.searchParams, {
        schema: makeAllRouteFiltersSchema(await resolveMembershipDefault(queryClient)),
        alias: allRouteFiltersAlias,
      })
      await prefetchPageWindow(allRouteApiParams(filters), (p) =>
        prefetchListAllRoutesQuery(queryClient, p)
      )
    },
  },
  {
    id: 'all-routes-map',
    paths: pathVariants.allRoutesMap(),
    component: AllRoutesMapPage,
    auth: 'public',
    parentId: 'all-routes',
    navGroup: 'home',
    breadcrumb: { type: 'static', i18nKey: tRegister('routes.view.map') },
    // Matches AllRoutesMapPage's initial useGetAllRoutesBounds key when no URL filters are set —
    // same minRole resolution as `all-routes` above.
    prefetch: async (queryClient, _params, url) => {
      const filters = readUrlFilters(url.searchParams, {
        schema: makeAllRouteFiltersSchema(await resolveMembershipDefault(queryClient)),
        alias: allRouteFiltersAlias,
      })
      await prefetchGetAllRoutesBoundsQuery(queryClient, {
        minRole: membershipToMinRole[filters.membership],
      })
    },
  },

  // === GPX Tools Routes ===
  {
    id: 'gpx-tools',
    paths: pathVariants.gpxTools(),
    component: GpxToolsPage,
    auth: 'authenticated',
    parentId: null,
    breadcrumb: { type: 'static', i18nKey: tRegister('gpxTools.title') },
  },
  {
    id: 'gpx-tools-list',
    paths: pathVariants.gpxToolsList(),
    component: MyGpxPreviewsPage,
    auth: 'authenticated',
    parentId: 'gpx-tools',
    breadcrumb: { type: 'static', i18nKey: tRegister('gpxTools.listFiles.title') },
  },
  {
    id: 'gpx-tools-new',
    paths: pathVariants.gpxToolsNew(),
    component: CreateGpxPreviewPage,
    auth: 'authenticated',
    parentId: 'gpx-tools',
    breadcrumb: { type: 'static', i18nKey: tRegister('gpxTools.createFromScratch.title') },
  },
  {
    id: 'gpx-tools-view',
    paths: pathVariants.gpxToolsView(':previewId'),
    component: GpxPreviewPage,
    // The link is shareable: holding the unguessable id is enough to view it.
    auth: 'public',
    parentId: 'gpx-tools',
    breadcrumb: { type: 'static', i18nKey: tRegister('gpxTools.preview.title') },
    // Public/anonymous endpoint — prefetches fine under stateless SSR, and feeds gpxPreviewMeta.
    prefetch: async (queryClient, params) => {
      await prefetchGetPreviewQuery(queryClient, params.previewId!)
    },
    meta: gpxPreviewMeta,
  },
  {
    id: 'gpx-tools-map',
    paths: pathVariants.gpxToolsMap(':previewId'),
    component: GpxPreviewFullscreenMapPage,
    // The link is shareable: holding the unguessable id is enough to view it.
    auth: 'public',
    layout: 'bare',
    parentId: 'gpx-tools-view',
    breadcrumb: { type: 'static', i18nKey: tRegister('map.fullscreen.title') },
    // Public/anonymous endpoint — prefetches fine under stateless SSR, and feeds gpxPreviewMeta.
    prefetch: async (queryClient, params) => {
      await prefetchGetPreviewQuery(queryClient, params.previewId!)
    },
    meta: gpxPreviewMeta,
  },
  {
    id: 'gpx-tools-edit',
    paths: pathVariants.gpxToolsEdit(':previewId'),
    component: EditGpxPreviewPage,
    // Editing is owner-only; the page itself redirects non-owners to the read view.
    auth: 'authenticated',
    parentId: 'gpx-tools-view',
    breadcrumb: { type: 'static', i18nKey: tRegister('gpxTools.edit.title') },
  },

  // === Apps ===
  {
    id: 'apps',
    paths: pathVariants.apps(),
    component: AppsPage,
    auth: 'public',
    parentId: null,
    breadcrumb: { type: 'static', i18nKey: tRegister('apps.title') },
    meta: appsMeta,
  },

  // === Legal Routes ===
  {
    id: 'privacy',
    paths: pathVariants.privacy(),
    component: PrivacyPolicyPage,
    auth: 'public',
    parentId: null,
    breadcrumb: { type: 'static', i18nKey: tRegister('legal.privacy.title') },
  },
  {
    id: 'terms',
    paths: pathVariants.terms(),
    component: TermsOfServicePage,
    auth: 'public',
    parentId: null,
    breadcrumb: { type: 'static', i18nKey: tRegister('legal.terms.title') },
  },

  // === Auth Routes ===
  {
    id: 'login',
    paths: pathVariants.login(),
    component: LoginPage,
    auth: 'unauthenticated',
    parentId: null,
    breadcrumb: { type: 'static', i18nKey: tRegister('auth.login.title') },
  },
  {
    id: 'device-verify-garmin',
    paths: pathVariants.deviceVerifyGarmin(),
    component: DeviceVerifyPage,
    auth: 'authenticated',
    parentId: null,
    breadcrumb: { type: 'static', i18nKey: tRegister('device.title') },
  },
  {
    id: 'device-verify-karoo',
    paths: pathVariants.deviceVerifyKaroo(),
    component: DeviceVerifyPage,
    auth: 'authenticated',
    parentId: null,
    breadcrumb: { type: 'static', i18nKey: tRegister('device.title') },
  },
  {
    id: 'verify-email',
    paths: pathVariants.verifyEmail(),
    component: VerifyEmailPage,
    auth: 'public',
    parentId: null,
    breadcrumb: { type: 'static', i18nKey: tRegister('auth.verifyEmail.title') },
  },
  {
    // Public, not authenticated: the page has to be able to say who is inviting and to what before
    // anyone signs in, or a signed-out invitee lands on a bare login form with no explanation.
    id: 'invitation',
    paths: pathVariants.invitation(),
    component: AcceptInvitationPage,
    auth: 'public',
    parentId: null,
    breadcrumb: { type: 'static', i18nKey: tRegister('invitations.accept.pageTitle') },
  },
  {
    id: 'forgot-password',
    paths: pathVariants.forgotPassword(),
    component: ForgotPasswordPage,
    auth: 'unauthenticated',
    parentId: null,
    breadcrumb: { type: 'static', i18nKey: tRegister('auth.forgotPassword.title') },
  },
  {
    id: 'reset-password',
    paths: pathVariants.resetPassword(),
    component: ResetPasswordPage,
    auth: 'public',
    parentId: null,
    breadcrumb: { type: 'static', i18nKey: tRegister('auth.resetPassword.title') },
  },
  {
    id: 'strava-callback',
    paths: pathVariants.stravaCallback(),
    component: StravaCallbackPage,
    auth: 'public',
    parentId: null,
    breadcrumb: { type: 'static', i18nKey: tRegister('auth.strava.title') },
  },
  {
    id: 'complete-account',
    paths: pathVariants.completeAccount(),
    component: CompleteAccountPage,
    auth: 'authenticated',
    parentId: null,
    breadcrumb: { type: 'static', i18nKey: tRegister('auth.completeAccount.title') },
  },
  {
    id: 'profile',
    paths: pathVariants.profile(),
    component: UserProfilePage,
    auth: 'authenticated',
    parentId: null,
    breadcrumb: { type: 'static', i18nKey: tRegister('nav.profile') },
    // The two counts `MyParticipations` puts on its closed accordion controls. They share the
    // component's own `hourAlignedNowIso()` boundary, which is the whole reason this can be
    // prefetched at all — the raw `new Date()` it used before never matched the SSR key.
    // The section's paged queries are deliberately left out: they only fire once opened.
    prefetch: async (queryClient) => {
      if (!useAuthStore.getState().isAuthenticated) return
      const now = hourAlignedNowIso()
      await Promise.all([
        prefetchListMyParticipationsQuery(queryClient, {
          from: now,
          ...PARTICIPATION_COUNT_PARAMS,
        }),
        prefetchListMyParticipationsQuery(queryClient, { to: now, ...PARTICIPATION_COUNT_PARAMS }),
      ])
    },
  },
  {
    id: 'calendar',
    paths: pathVariants.calendar(),
    component: CalendarPage,
    auth: 'authenticated',
    parentId: null,
    navGroup: 'home',
    breadcrumb: { type: 'static', i18nKey: tRegister('calendar.title') },
    // CalendarPage's first events query, keyed on the same `getInitialCalendarRange()` the hook
    // seeds its state with. FullCalendar re-queries its own visible grid right after mount; that
    // second range depends on the viewport and can't be known here, so it stays a client fetch.
    prefetch: async (queryClient) => {
      if (!useAuthStore.getState().isAuthenticated) return
      await prefetchGetEventsQuery(queryClient, getInitialCalendarRange())
    },
  },

  // === Team Routes ===
  {
    id: 'teams',
    paths: pathVariants.teams(),
    component: TeamListPage,
    auth: 'public',
    parentId: null,
    navGroup: 'home',
    breadcrumb: { type: 'static', i18nKey: tRegister('teams.title') },
    hideWhenSingleTeam: true,
    // Matches TeamListPage's initial useListTeams key when no URL filters are set (search
    // undefined, page 0). Signed in, the list's `minRole` depends on team membership (see
    // useMembershipDefault) — replicated here so the resolved variant, not a guess, lands in the
    // cache. Without this, a signed-in visitor's real first query misses the SSR cache and
    // refetches after hydration (member: wrong shape cached; non-member: the optimistic
    // "member" default used while the probe is loading fires one fetch, then correcting to
    // "all" fires a second) — hydrating with the resolved probe result upfront skips that
    // optimistic guess entirely.
    prefetch: async (queryClient, _params, url) => {
      if (useAuthStore.getState().isAuthenticated) {
        await prefetchListMyInvitationsQuery(queryClient)
      }
      const filters = readUrlFilters(url.searchParams, {
        schema: makeTeamFiltersSchema(await resolveMembershipDefault(queryClient)),
        alias: teamFiltersAlias,
      })
      await prefetchPageWindow(teamApiParams(filters), (p) =>
        prefetchListTeamsQuery(queryClient, p)
      )
    },
    meta: teamsMeta,
  },
  {
    id: 'teams-new',
    paths: pathVariants.teamsNew(),
    component: CreateTeamPage,
    auth: 'authenticated',
    parentId: 'teams',
    breadcrumb: { type: 'static', i18nKey: tRegister('teams.create.title') },
    showBackLink: true,
    hideWhenSingleTeam: true,
  },
  {
    id: 'team-detail',
    paths: pathVariants.team(':teamSlug'),
    component: PublicationListPage,
    auth: 'public',
    parentId: 'teams',
    navGroup: 'team',
    breadcrumb: { type: 'dynamic', entity: 'team' },
    // PublicationListPage reads the team plus its first two, unfiltered publications pages.
    // Matches PublicationListPage's query key exactly (including `view`) — a mismatch here (e.g.
    // a missing `view`) makes the real query miss the SSR cache and refetch after hydration.
    prefetch: async (queryClient, params, url) => {
      await Promise.all([
        prefetchGetTeamQuery(queryClient, params.teamSlug!),
        prefetchPageWindow(
          publicationApiParams(
            readUrlFilters(url.searchParams, {
              schema: publicationFiltersSchema,
              alias: publicationFiltersAlias,
            }),
            hourAlignedNowIso()
          ),
          (p) => prefetchListPublicationsQuery(queryClient, params.teamSlug!, p)
        ),
      ])
    },
    meta: teamDetailMeta,
  },
  {
    id: 'team-about',
    paths: pathVariants.teamAbout(':teamSlug'),
    component: TeamAboutPage,
    auth: 'public',
    parentId: 'team-detail',
    breadcrumb: { type: 'static', i18nKey: tRegister('teams.detail.tabs.about') },
    prefetch: async (queryClient, params) => {
      await prefetchGetTeamQuery(queryClient, params.teamSlug!)
    },
    meta: teamAboutMeta,
  },
  {
    id: 'team-calendar',
    paths: pathVariants.teamCalendar(':teamSlug'),
    component: TeamCalendarPage,
    auth: 'public',
    parentId: 'team-detail',
    breadcrumb: { type: 'static', i18nKey: tRegister('calendar.title') },
  },
  {
    id: 'team-page',
    paths: pathVariants.teamPage(':teamSlug', ':pageSlug'),
    component: TeamPageDetailPage,
    auth: 'public',
    parentId: 'team-detail',
    breadcrumb: { type: 'dynamic', entity: 'teamPage' },
    prefetch: async (queryClient, params) => {
      await Promise.all([
        prefetchGetTeamQuery(queryClient, params.teamSlug!),
        prefetchGetPageQuery(queryClient, params.teamSlug!, params.pageSlug!),
      ])
    },
    meta: teamPageMeta,
  },
  // === Team Admin Routes ===
  {
    id: 'team-admin',
    paths: pathVariants.teamAdmin(':teamSlug'),
    component: TeamAdminPage,
    auth: 'authenticated',
    parentId: 'team-detail',
    breadcrumb: { type: 'static', i18nKey: tRegister('teams.admin.title') },
    prefetch: teamScopedPrefetch(),
  },
  {
    id: 'team-admin-places',
    paths: pathVariants.teamAdminPlaces(':teamSlug'),
    component: TeamPlacesPage,
    auth: 'authenticated',
    parentId: 'team-admin',
    breadcrumb: { type: 'static', i18nKey: tRegister('teams.admin.tabs.places') },
    prefetch: teamScopedPrefetch((qc, p) =>
      prefetchListPlacesQuery(qc, p.teamSlug!, placeFiltersSchema.parse({}))
    ),
  },
  {
    id: 'team-admin-pages',
    paths: pathVariants.teamAdminPages(':teamSlug'),
    component: TeamPagesAdminPage,
    auth: 'authenticated',
    parentId: 'team-admin',
    breadcrumb: { type: 'static', i18nKey: tRegister('teams.admin.tabs.pages') },
    prefetch: teamScopedPrefetch((qc, p) => prefetchListPagesQuery(qc, p.teamSlug!)),
  },
  {
    id: 'team-admin-page-new',
    paths: pathVariants.teamAdminPageNew(':teamSlug'),
    component: CreateTeamPagePage,
    auth: 'authenticated',
    parentId: 'team-admin-pages',
    breadcrumb: { type: 'static', i18nKey: tRegister('actions.new') },
    prefetch: teamScopedPrefetch(),
    showBackLink: true,
  },
  {
    id: 'team-admin-page-edit',
    paths: pathVariants.teamAdminPageEdit(':teamSlug', ':pageSlug'),
    component: EditTeamPagePage,
    auth: 'authenticated',
    parentId: 'team-admin-pages',
    breadcrumb: { type: 'dynamic', entity: 'teamPage' },
    prefetch: teamScopedPrefetch((qc, p) => prefetchGetPageQuery(qc, p.teamSlug!, p.pageSlug!)),
    showBackLink: true,
  },
  {
    id: 'team-members',
    paths: pathVariants.teamAdminMembers(':teamSlug'),
    component: TeamMembersPage,
    auth: 'authenticated',
    parentId: 'team-admin',
    breadcrumb: { type: 'static', i18nKey: tRegister('teams.admin.tabs.members') },
    prefetch: teamScopedPrefetch((qc, p) =>
      Promise.all([
        prefetchGetMembersQuery(qc, p.teamSlug!, teamMemberFiltersSchema.parse({})),
        prefetchListInvitationsQuery(qc, p.teamSlug!, { status: InvitationStatus.PENDING }),
      ])
    ),
  },
  {
    id: 'team-settings',
    paths: pathVariants.teamSettings(':teamSlug'),
    component: TeamSettingsPage,
    auth: 'authenticated',
    parentId: 'team-admin',
    breadcrumb: { type: 'static', i18nKey: tRegister('teams.admin.tabs.settings') },
    prefetch: teamScopedPrefetch(),
  },

  // === Ride Routes ===
  // Note: rides have parent team-detail (no rides list page)
  {
    id: 'ride-new',
    paths: pathVariants.rideNew(':teamSlug'),
    component: CreateRidePage,
    auth: 'authenticated',
    parentId: 'team-detail',
    breadcrumb: { type: 'static', i18nKey: tRegister('rides.create.title') },
    prefetch: teamScopedPrefetch((qc, p) => prefetchRideFormPlaces(qc, p.teamSlug!)),
    showBackLink: true,
  },
  {
    id: 'ride-detail',
    paths: pathVariants.ride(':teamSlug', ':rideSlug'),
    component: RideDetailPage,
    auth: 'public',
    parentId: 'team-detail',
    breadcrumb: { type: 'dynamic', entity: 'ride' },
    prefetch: async (queryClient, params) => {
      const teamSlug = params.teamSlug!
      const rideSlug = params.rideSlug!
      await Promise.all([
        prefetchGetTeamQuery(queryClient, teamSlug),
        prefetchGetRideQuery(queryClient, teamSlug, rideSlug),
      ])
      await Promise.all([
        prefetchMemberComments(
          queryClient,
          teamSlug,
          rideSlug,
          listRideComments,
          getListRideCommentsQueryKey
        ),
        prefetchRideRoutesBulk(queryClient, teamSlug, rideSlug),
        useAuthStore.getState().isAuthenticated
          ? prefetchGetAvailableServicesQuery(queryClient)
          : Promise.resolve(queryClient),
      ])
    },
    meta: rideMeta,
  },
  {
    id: 'ride-edit',
    paths: pathVariants.rideEdit(':teamSlug', ':rideSlug'),
    component: EditRidePage,
    auth: 'authenticated',
    parentId: 'ride-detail',
    breadcrumb: { type: 'static', i18nKey: tRegister('actions.edit') },
    prefetch: teamScopedPrefetch((qc, p) =>
      Promise.all([
        prefetchGetRideQuery(qc, p.teamSlug!, p.rideSlug!),
        prefetchRideFormPlaces(qc, p.teamSlug!),
      ])
    ),
    showBackLink: true,
  },

  // === Ride Template Routes ===
  {
    id: 'ride-templates',
    paths: pathVariants.rideTemplates(':teamSlug'),
    component: RideTemplateListPage,
    auth: 'authenticated',
    parentId: 'team-admin',
    breadcrumb: { type: 'static', i18nKey: tRegister('teams.admin.tabs.rideTemplates') },
    prefetch: teamScopedPrefetch((qc, p) =>
      prefetchListTemplatesQuery(qc, p.teamSlug!, rideTemplateFiltersSchema.parse({}))
    ),
  },
  {
    id: 'ride-template-new',
    paths: pathVariants.rideTemplateNew(':teamSlug'),
    component: CreateRideTemplatePage,
    auth: 'authenticated',
    parentId: 'ride-templates',
    breadcrumb: { type: 'static', i18nKey: tRegister('actions.new') },
    prefetch: teamScopedPrefetch(),
    showBackLink: true,
  },
  {
    id: 'ride-template-edit',
    paths: pathVariants.rideTemplateEdit(':teamSlug', ':templateSlug'),
    component: EditRideTemplatePage,
    auth: 'authenticated',
    parentId: 'ride-templates',
    breadcrumb: { type: 'dynamic', entity: 'rideTemplate' },
    prefetch: teamScopedPrefetch(),
    showBackLink: true,
  },

  // === Trip Routes ===
  // Note: trips have parent team-detail (no trips list page)
  {
    id: 'trip-new',
    paths: pathVariants.tripNew(':teamSlug'),
    component: CreateTripPage,
    auth: 'authenticated',
    parentId: 'team-detail',
    breadcrumb: { type: 'static', i18nKey: tRegister('trips.create.title') },
    prefetch: teamScopedPrefetch(),
    showBackLink: true,
  },
  {
    id: 'trip-detail',
    paths: pathVariants.trip(':teamSlug', ':tripSlug'),
    component: TripDetailPage,
    auth: 'public',
    parentId: 'team-detail',
    breadcrumb: { type: 'dynamic', entity: 'trip' },
    prefetch: async (queryClient, params) => {
      const teamSlug = params.teamSlug!
      const tripSlug = params.tripSlug!
      await Promise.all([
        prefetchGetTeamQuery(queryClient, teamSlug),
        prefetchGetTripQuery(queryClient, teamSlug, tripSlug),
      ])
      await Promise.all([
        prefetchMemberComments(
          queryClient,
          teamSlug,
          tripSlug,
          listTripComments,
          getListTripCommentsQueryKey
        ),
        prefetchTripRoutesBulk(queryClient, teamSlug, tripSlug),
      ])
    },
    meta: tripMeta,
  },
  {
    id: 'trip-edit',
    paths: pathVariants.tripEdit(':teamSlug', ':tripSlug'),
    component: EditTripPage,
    auth: 'authenticated',
    parentId: 'trip-detail',
    breadcrumb: { type: 'static', i18nKey: tRegister('actions.edit') },
    prefetch: teamScopedPrefetch((qc, p) => prefetchGetTripQuery(qc, p.teamSlug!, p.tripSlug!)),
    showBackLink: true,
  },
  {
    id: 'stage-detail',
    paths: pathVariants.stage(':teamSlug', ':tripSlug', ':stageSlug'),
    component: StageDetailPage,
    auth: 'public',
    parentId: 'trip-detail',
    breadcrumb: { type: 'dynamic', entity: 'stage' },
    // StageDetailPage reads the team and the parent trip, then looks up the stage by
    // `stageSlug` within the trip's own stages to find its route — same lookup here, once the
    // trip is in cache. GPS export options are authenticated-only, resolved the same way as
    // route-detail's own prefetch above.
    prefetch: async (queryClient, params) => {
      const teamSlug = params.teamSlug!
      const tripSlug = params.tripSlug!
      await Promise.all([
        prefetchGetTeamQuery(queryClient, teamSlug),
        prefetchGetTripQuery(queryClient, teamSlug, tripSlug),
      ])
      const trip = queryClient.getQueryData<TripDto>(getGetTripQueryKey(teamSlug, tripSlug))
      const routeSlug = trip?.stages?.find((s) => s.slug === params.stageSlug)?.route?.slug
      await Promise.all([
        routeSlug
          ? prefetchGetRouteQuery(queryClient, teamSlug, routeSlug)
          : Promise.resolve(queryClient),
        useAuthStore.getState().isAuthenticated
          ? prefetchGetAvailableServicesQuery(queryClient)
          : Promise.resolve(queryClient),
      ])
    },
    meta: stageMeta,
  },
  {
    id: 'stage-map',
    paths: pathVariants.stageMap(':teamSlug', ':tripSlug', ':stageSlug'),
    component: StageFullscreenMapPage,
    auth: 'public',
    layout: 'bare',
    parentId: 'stage-detail',
    breadcrumb: { type: 'static', i18nKey: tRegister('map.fullscreen.title') },
  },

  // === Post Routes ===
  // Note: posts have parent team-detail (no posts list page)
  {
    id: 'post-new',
    paths: pathVariants.postNew(':teamSlug'),
    component: CreatePostPage,
    auth: 'authenticated',
    parentId: 'team-detail',
    breadcrumb: { type: 'static', i18nKey: tRegister('posts.create.title') },
    prefetch: teamScopedPrefetch(),
    showBackLink: true,
  },
  {
    id: 'post-detail',
    paths: pathVariants.post(':teamSlug', ':postSlug'),
    component: PostDetailPage,
    auth: 'public',
    parentId: 'team-detail',
    breadcrumb: { type: 'dynamic', entity: 'post' },
    prefetch: async (queryClient, params) => {
      const teamSlug = params.teamSlug!
      const postSlug = params.postSlug!
      await Promise.all([
        prefetchGetTeamQuery(queryClient, teamSlug),
        prefetchGetPostQuery(queryClient, teamSlug, postSlug),
      ])
      await prefetchMemberComments(
        queryClient,
        teamSlug,
        postSlug,
        listPostComments,
        getListPostCommentsQueryKey
      )
    },
    meta: postMeta,
  },
  {
    id: 'post-edit',
    paths: pathVariants.postEdit(':teamSlug', ':postSlug'),
    component: EditPostPage,
    auth: 'authenticated',
    parentId: 'post-detail',
    breadcrumb: { type: 'static', i18nKey: tRegister('actions.edit') },
    prefetch: teamScopedPrefetch((qc, p) => prefetchGetPostQuery(qc, p.teamSlug!, p.postSlug!)),
    showBackLink: true,
  },

  // === Route Routes ===
  // Note: routes have a list page, so route-detail has parent 'routes'
  {
    id: 'routes',
    paths: pathVariants.routes(':teamSlug'),
    component: RouteListPage,
    auth: 'public',
    parentId: 'team-detail',
    breadcrumb: { type: 'static', i18nKey: tRegister('nav.routes') },
    // RouteListPage reads the team plus its first two, unfiltered route pages — no
    // team-membership dependency here, unlike the cross-team route list.
    prefetch: async (queryClient, params, url) => {
      const filters = readUrlFilters(url.searchParams, {
        schema: routeFiltersSchema,
        alias: routeFiltersAlias,
      })
      await Promise.all([
        prefetchGetTeamQuery(queryClient, params.teamSlug!),
        prefetchPageWindow(routeApiParams(filters), (p) =>
          prefetchListRoutesQuery(queryClient, params.teamSlug!, p)
        ),
      ])
    },
  },
  {
    id: 'routes-map',
    paths: pathVariants.routesMap(':teamSlug'),
    component: RoutesMapPage,
    auth: 'public',
    parentId: 'routes',
    breadcrumb: { type: 'static', i18nKey: tRegister('routes.view.map') },
  },
  {
    id: 'route-new',
    paths: pathVariants.routeNew(':teamSlug'),
    component: CreateRoutePage,
    auth: 'authenticated',
    parentId: 'routes',
    breadcrumb: { type: 'static', i18nKey: tRegister('actions.new') },
    prefetch: teamScopedPrefetch(),
    showBackLink: true,
  },
  {
    id: 'route-detail',
    paths: pathVariants.route(':teamSlug', ':routeSlug'),
    component: RouteDetailPage,
    auth: 'public',
    parentId: 'routes',
    breadcrumb: { type: 'dynamic', entity: 'route' },
    // Comments are member-only (RouteDetailPage's `isMember`, from the team's `role`) and GPS
    // export options are authenticated-only — both resolved from data already being prefetched
    // here, so this replicates the page's own gating instead of guessing.
    prefetch: async (queryClient, params) => {
      const teamSlug = params.teamSlug!
      const routeSlug = params.routeSlug!
      await Promise.all([
        prefetchGetTeamQuery(queryClient, teamSlug),
        prefetchGetRouteQuery(queryClient, teamSlug, routeSlug),
        prefetchGetRouteUsagesQuery(queryClient, teamSlug, routeSlug),
        useAuthStore.getState().isAuthenticated
          ? prefetchGetAvailableServicesQuery(queryClient)
          : Promise.resolve(queryClient),
      ])
      await prefetchMemberComments(
        queryClient,
        teamSlug,
        routeSlug,
        listRouteComments,
        getListRouteCommentsQueryKey
      )
    },
    meta: routeMeta,
  },
  {
    id: 'route-map',
    paths: pathVariants.routeMap(':teamSlug', ':routeSlug'),
    component: RouteFullscreenMapPage,
    auth: 'public',
    layout: 'bare',
    parentId: 'route-detail',
    breadcrumb: { type: 'static', i18nKey: tRegister('map.fullscreen.title') },
    prefetch: async (queryClient, params) => {
      await Promise.all([
        prefetchGetTeamQuery(queryClient, params.teamSlug!),
        prefetchGetRouteQuery(queryClient, params.teamSlug!, params.routeSlug!),
      ])
    },
    meta: routeMeta,
  },
  {
    id: 'route-edit',
    paths: pathVariants.routeEdit(':teamSlug', ':routeSlug'),
    component: EditRoutePage,
    auth: 'authenticated',
    parentId: 'route-detail',
    breadcrumb: { type: 'static', i18nKey: tRegister('actions.edit') },
    prefetch: teamScopedPrefetch((qc, p) => prefetchGetRouteQuery(qc, p.teamSlug!, p.routeSlug!)),
    showBackLink: true,
  },

  // === Ad Routes ===
  {
    id: 'ads',
    paths: pathVariants.ads(':teamSlug'),
    component: AdListPage,
    auth: 'public',
    parentId: 'team-detail',
    breadcrumb: { type: 'static', i18nKey: tRegister('ads.title') },
    // AdListPage reads the team plus its first two, unfiltered ad pages — matches its query key
    // exactly (including `view`), same pattern as the publications and routes lists above.
    prefetch: async (queryClient, params, url) => {
      const filters = readUrlFilters(url.searchParams, {
        schema: adFiltersSchema,
        alias: adFiltersAlias,
      })
      await Promise.all([
        prefetchGetTeamQuery(queryClient, params.teamSlug!),
        prefetchPageWindow(adApiParams(filters), (p) =>
          prefetchListAdsQuery(queryClient, params.teamSlug!, p)
        ),
      ])
    },
  },
  {
    id: 'ad-new',
    paths: pathVariants.adNew(':teamSlug'),
    component: CreateAdPage,
    auth: 'authenticated',
    parentId: 'ads',
    breadcrumb: { type: 'static', i18nKey: tRegister('ads.create.title') },
    prefetch: teamScopedPrefetch(),
    showBackLink: true,
  },
  {
    id: 'ad-detail',
    paths: pathVariants.ad(':teamSlug', ':adSlug'),
    component: AdDetailPage,
    auth: 'public',
    parentId: 'ads',
    breadcrumb: { type: 'dynamic', entity: 'ad' },
    prefetch: async (queryClient, params) => {
      await Promise.all([
        prefetchGetTeamQuery(queryClient, params.teamSlug!),
        prefetchGetAdQuery(queryClient, params.teamSlug!, params.adSlug!),
      ])
    },
    meta: adMeta,
  },
  {
    id: 'ad-edit',
    paths: pathVariants.adEdit(':teamSlug', ':adSlug'),
    component: EditAdPage,
    auth: 'authenticated',
    parentId: 'ad-detail',
    breadcrumb: { type: 'static', i18nKey: tRegister('actions.edit') },
    prefetch: teamScopedPrefetch((qc, p) => prefetchGetAdQuery(qc, p.teamSlug!, p.adSlug!)),
    showBackLink: true,
  },

  // === Platform Admin Routes ===
  {
    id: 'admin',
    paths: pathVariants.admin(),
    component: AdminDashboardPage,
    auth: 'authenticated',
    parentId: null,
    breadcrumb: { type: 'static', i18nKey: tRegister('admin.title') },
  },
  {
    id: 'admin-domains',
    paths: pathVariants.adminDomains(),
    component: AdminDomainsPage,
    auth: 'authenticated',
    parentId: 'admin',
    breadcrumb: { type: 'static', i18nKey: tRegister('admin.tabs.domains') },
  },
  {
    id: 'admin-teams',
    paths: pathVariants.adminTeams(),
    component: AdminTeamsPage,
    auth: 'authenticated',
    parentId: 'admin',
    breadcrumb: { type: 'static', i18nKey: tRegister('admin.tabs.teams') },
  },
  {
    id: 'admin-users',
    paths: pathVariants.adminUsers(),
    component: AdminUsersPage,
    auth: 'authenticated',
    parentId: 'admin',
    breadcrumb: { type: 'static', i18nKey: tRegister('admin.tabs.users') },
  },
  {
    id: 'admin-beta-signups',
    paths: pathVariants.adminBetaSignups(),
    component: AdminBetaSignupsPage,
    auth: 'authenticated',
    parentId: 'admin',
    breadcrumb: { type: 'static', i18nKey: tRegister('admin.tabs.betaSignups') },
  },
]
