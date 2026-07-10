import { lazy } from 'react'
import type { RoutesConfig } from './routes.types'
import { pathVariants } from './paths'
import { tRegister } from '@/lib/i18nUtils'

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
const ForgotPasswordPage = lazy(() =>
  import('../pages/auth/ForgotPasswordPage').then((m) => ({ default: m.ForgotPasswordPage }))
)
const ResetPasswordPage = lazy(() =>
  import('../pages/auth/ResetPasswordPage').then((m) => ({ default: m.ResetPasswordPage }))
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
const PrivacyPolicyPage = lazy(() =>
  import('../pages/legal/PrivacyPolicyPage').then((m) => ({ default: m.PrivacyPolicyPage }))
)
const TermsOfServicePage = lazy(() =>
  import('../pages/legal/TermsOfServicePage').then((m) => ({ default: m.TermsOfServicePage }))
)

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
  },
  {
    id: 'all-routes',
    paths: pathVariants.allRoutes(),
    component: AllRoutesPage,
    auth: 'public',
    parentId: null,
    navGroup: 'home',
    breadcrumb: { type: 'static', i18nKey: tRegister('nav.routes') },
  },
  {
    id: 'all-routes-map',
    paths: pathVariants.allRoutesMap(),
    component: AllRoutesMapPage,
    auth: 'public',
    parentId: 'all-routes',
    navGroup: 'home',
    breadcrumb: { type: 'static', i18nKey: tRegister('routes.view.map') },
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
    id: 'profile',
    paths: pathVariants.profile(),
    component: UserProfilePage,
    auth: 'authenticated',
    parentId: null,
    breadcrumb: { type: 'static', i18nKey: tRegister('nav.profile') },
  },
  {
    id: 'calendar',
    paths: pathVariants.calendar(),
    component: CalendarPage,
    auth: 'authenticated',
    parentId: null,
    navGroup: 'home',
    breadcrumb: { type: 'static', i18nKey: tRegister('calendar.title') },
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
  },
  {
    id: 'team-about',
    paths: pathVariants.teamAbout(':teamSlug'),
    component: TeamAboutPage,
    auth: 'public',
    parentId: 'team-detail',
    breadcrumb: { type: 'static', i18nKey: tRegister('teams.detail.tabs.about') },
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
  },
  // === Team Admin Routes ===
  {
    id: 'team-admin',
    paths: pathVariants.teamAdmin(':teamSlug'),
    component: TeamAdminPage,
    auth: 'authenticated',
    parentId: 'team-detail',
    breadcrumb: { type: 'static', i18nKey: tRegister('teams.admin.title') },
  },
  {
    id: 'team-admin-places',
    paths: pathVariants.teamAdminPlaces(':teamSlug'),
    component: TeamPlacesPage,
    auth: 'authenticated',
    parentId: 'team-admin',
    breadcrumb: { type: 'static', i18nKey: tRegister('teams.admin.tabs.places') },
  },
  {
    id: 'team-admin-pages',
    paths: pathVariants.teamAdminPages(':teamSlug'),
    component: TeamPagesAdminPage,
    auth: 'authenticated',
    parentId: 'team-admin',
    breadcrumb: { type: 'static', i18nKey: tRegister('teams.admin.tabs.pages') },
  },
  {
    id: 'team-admin-page-new',
    paths: pathVariants.teamAdminPageNew(':teamSlug'),
    component: CreateTeamPagePage,
    auth: 'authenticated',
    parentId: 'team-admin-pages',
    breadcrumb: { type: 'static', i18nKey: tRegister('actions.new') },
    showBackLink: true,
  },
  {
    id: 'team-admin-page-edit',
    paths: pathVariants.teamAdminPageEdit(':teamSlug', ':pageSlug'),
    component: EditTeamPagePage,
    auth: 'authenticated',
    parentId: 'team-admin-pages',
    breadcrumb: { type: 'dynamic', entity: 'teamPage' },
    showBackLink: true,
  },
  {
    id: 'team-members',
    paths: pathVariants.teamMembers(':teamSlug'),
    component: TeamMembersPage,
    auth: 'authenticated',
    parentId: 'team-admin',
    breadcrumb: { type: 'static', i18nKey: tRegister('teams.admin.tabs.members') },
  },
  {
    id: 'team-settings',
    paths: pathVariants.teamSettings(':teamSlug'),
    component: TeamSettingsPage,
    auth: 'authenticated',
    parentId: 'team-admin',
    breadcrumb: { type: 'static', i18nKey: tRegister('teams.admin.tabs.settings') },
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
    showBackLink: true,
  },
  {
    id: 'ride-detail',
    paths: pathVariants.ride(':teamSlug', ':rideSlug'),
    component: RideDetailPage,
    auth: 'public',
    parentId: 'team-detail',
    breadcrumb: { type: 'dynamic', entity: 'ride' },
  },
  {
    id: 'ride-edit',
    paths: pathVariants.rideEdit(':teamSlug', ':rideSlug'),
    component: EditRidePage,
    auth: 'authenticated',
    parentId: 'ride-detail',
    breadcrumb: { type: 'static', i18nKey: tRegister('actions.edit') },
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
  },
  {
    id: 'ride-template-new',
    paths: pathVariants.rideTemplateNew(':teamSlug'),
    component: CreateRideTemplatePage,
    auth: 'authenticated',
    parentId: 'ride-templates',
    breadcrumb: { type: 'static', i18nKey: tRegister('actions.new') },
    showBackLink: true,
  },
  {
    id: 'ride-template-edit',
    paths: pathVariants.rideTemplateEdit(':teamSlug', ':templateSlug'),
    component: EditRideTemplatePage,
    auth: 'authenticated',
    parentId: 'ride-templates',
    breadcrumb: { type: 'dynamic', entity: 'rideTemplate' },
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
    showBackLink: true,
  },
  {
    id: 'trip-detail',
    paths: pathVariants.trip(':teamSlug', ':tripSlug'),
    component: TripDetailPage,
    auth: 'public',
    parentId: 'team-detail',
    breadcrumb: { type: 'dynamic', entity: 'trip' },
  },
  {
    id: 'trip-edit',
    paths: pathVariants.tripEdit(':teamSlug', ':tripSlug'),
    component: EditTripPage,
    auth: 'authenticated',
    parentId: 'trip-detail',
    breadcrumb: { type: 'static', i18nKey: tRegister('actions.edit') },
    showBackLink: true,
  },
  {
    id: 'stage-detail',
    paths: pathVariants.stage(':teamSlug', ':tripSlug', ':stageSlug'),
    component: StageDetailPage,
    auth: 'public',
    parentId: 'trip-detail',
    breadcrumb: { type: 'dynamic', entity: 'stage' },
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
    showBackLink: true,
  },
  {
    id: 'post-detail',
    paths: pathVariants.post(':teamSlug', ':postSlug'),
    component: PostDetailPage,
    auth: 'public',
    parentId: 'team-detail',
    breadcrumb: { type: 'dynamic', entity: 'post' },
  },
  {
    id: 'post-edit',
    paths: pathVariants.postEdit(':teamSlug', ':postSlug'),
    component: EditPostPage,
    auth: 'authenticated',
    parentId: 'post-detail',
    breadcrumb: { type: 'static', i18nKey: tRegister('actions.edit') },
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
    showBackLink: true,
  },
  {
    id: 'route-detail',
    paths: pathVariants.route(':teamSlug', ':routeSlug'),
    component: RouteDetailPage,
    auth: 'public',
    parentId: 'routes',
    breadcrumb: { type: 'dynamic', entity: 'route' },
  },
  {
    id: 'route-edit',
    paths: pathVariants.routeEdit(':teamSlug', ':routeSlug'),
    component: EditRoutePage,
    auth: 'authenticated',
    parentId: 'route-detail',
    breadcrumb: { type: 'static', i18nKey: tRegister('actions.edit') },
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
  },
  {
    id: 'ad-new',
    paths: pathVariants.adNew(':teamSlug'),
    component: CreateAdPage,
    auth: 'authenticated',
    parentId: 'ads',
    breadcrumb: { type: 'static', i18nKey: tRegister('ads.create.title') },
    showBackLink: true,
  },
  {
    id: 'ad-detail',
    paths: pathVariants.ad(':teamSlug', ':adSlug'),
    component: AdDetailPage,
    auth: 'public',
    parentId: 'ads',
    breadcrumb: { type: 'dynamic', entity: 'ad' },
  },
  {
    id: 'ad-edit',
    paths: pathVariants.adEdit(':teamSlug', ':adSlug'),
    component: EditAdPage,
    auth: 'authenticated',
    parentId: 'ad-detail',
    breadcrumb: { type: 'static', i18nKey: tRegister('actions.edit') },
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
]
