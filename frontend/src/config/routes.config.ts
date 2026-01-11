import { lazy } from 'react'
import type { RoutesConfig } from './routes.types'
import { paths } from './paths'
import { tRegister } from '@/lib/i18nUtils'

// Lazy load page components for code splitting
const HomePage = lazy(() => import('../pages/home/HomePage').then((m) => ({ default: m.HomePage })))
const LoginPage = lazy(() =>
  import('../pages/auth/LoginPage').then((m) => ({ default: m.LoginPage }))
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
const AllRoutesPage = lazy(() =>
  import('../pages/route/AllRoutesPage').then((m) => ({ default: m.AllRoutesPage }))
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

export const routesConfig: RoutesConfig = [
  // === Home ===
  {
    id: 'home',
    path: paths.home(),
    component: HomePage,
    auth: 'public',
    parentId: null,
    index: true,
    subRouteIds: ['home', 'all-routes', 'teams'],
    breadcrumb: { type: 'static', i18nKey: tRegister('home.tabs.feed') },
  },
  {
    id: 'all-routes',
    path: paths.allRoutes(),
    component: AllRoutesPage,
    auth: 'public',
    parentId: null,
    subRouteIds: ['home', 'all-routes', 'teams'],
    breadcrumb: { type: 'static', i18nKey: tRegister('nav.routes') },
  },

  // === Auth Routes ===
  {
    id: 'login',
    path: paths.login(),
    component: LoginPage,
    auth: 'unauthenticated',
    parentId: null,
    breadcrumb: { type: 'static', i18nKey: tRegister('auth.login.title') },
  },
  {
    id: 'profile',
    path: paths.profile(),
    component: UserProfilePage,
    auth: 'authenticated',
    parentId: null,
    breadcrumb: { type: 'static', i18nKey: tRegister('nav.profile') },
  },

  // === Team Routes ===
  {
    id: 'teams',
    path: paths.teams(),
    component: TeamListPage,
    auth: 'public',
    parentId: null,
    subRouteIds: ['home', 'all-routes', 'teams'],
    breadcrumb: { type: 'static', i18nKey: tRegister('teams.title') },
  },
  {
    id: 'teams-new',
    path: paths.teamsNew(),
    component: CreateTeamPage,
    auth: 'authenticated',
    parentId: 'teams',
    breadcrumb: { type: 'static', i18nKey: tRegister('teams.create.title') },
    showBackLink: true,
  },
  {
    id: 'team-detail',
    path: paths.team(':teamSlug'),
    component: PublicationListPage,
    auth: 'public',
    parentId: 'teams',
    subRouteIds: ['team-detail', 'routes', 'ads'],
    breadcrumb: { type: 'dynamic', entity: 'team' },
  },
  {
    id: 'team-about',
    path: paths.teamAbout(':teamSlug'),
    component: TeamAboutPage,
    auth: 'public',
    parentId: 'team-detail',
    breadcrumb: { type: 'static', i18nKey: tRegister('teams.detail.tabs.about') },
  },
  {
    id: 'team-page',
    path: paths.teamPage(':teamSlug', ':pageSlug'),
    component: TeamPageDetailPage,
    auth: 'public',
    parentId: 'team-detail',
    breadcrumb: { type: 'dynamic', entity: 'teamPage' },
  },
  // === Team Admin Routes ===
  {
    id: 'team-admin',
    path: paths.teamAdmin(':teamSlug'),
    component: TeamAdminPage,
    auth: 'authenticated',
    parentId: 'team-detail',
    breadcrumb: { type: 'static', i18nKey: tRegister('teams.admin.title') },
  },
  {
    id: 'team-admin-places',
    path: paths.teamAdminPlaces(':teamSlug'),
    component: TeamPlacesPage,
    auth: 'authenticated',
    parentId: 'team-admin',
    breadcrumb: { type: 'static', i18nKey: tRegister('teams.admin.tabs.places') },
  },
  {
    id: 'team-admin-pages',
    path: paths.teamAdminPages(':teamSlug'),
    component: TeamPagesAdminPage,
    auth: 'authenticated',
    parentId: 'team-admin',
    breadcrumb: { type: 'static', i18nKey: tRegister('teams.admin.tabs.pages') },
  },
  {
    id: 'team-admin-page-new',
    path: paths.teamAdminPageNew(':teamSlug'),
    component: CreateTeamPagePage,
    auth: 'authenticated',
    parentId: 'team-admin-pages',
    breadcrumb: { type: 'static', i18nKey: tRegister('actions.new') },
    showBackLink: true,
  },
  {
    id: 'team-admin-page-edit',
    path: paths.teamAdminPageEdit(':teamSlug', ':pageSlug'),
    component: EditTeamPagePage,
    auth: 'authenticated',
    parentId: 'team-admin-pages',
    breadcrumb: { type: 'dynamic', entity: 'teamPage' },
    showBackLink: true,
  },
  {
    id: 'team-members',
    path: paths.teamMembers(':teamSlug'),
    component: TeamMembersPage,
    auth: 'authenticated',
    parentId: 'team-admin',
    breadcrumb: { type: 'static', i18nKey: tRegister('teams.admin.tabs.members') },
  },
  {
    id: 'team-settings',
    path: paths.teamSettings(':teamSlug'),
    component: TeamSettingsPage,
    auth: 'authenticated',
    parentId: 'team-admin',
    breadcrumb: { type: 'static', i18nKey: tRegister('teams.admin.tabs.settings') },
  },

  // === Ride Routes ===
  // Note: rides have parent team-detail (no rides list page)
  {
    id: 'ride-new',
    path: paths.rideNew(':teamSlug'),
    component: CreateRidePage,
    auth: 'authenticated',
    parentId: 'team-detail',
    breadcrumb: { type: 'static', i18nKey: tRegister('rides.create.title') },
    showBackLink: true,
  },
  {
    id: 'ride-detail',
    path: paths.ride(':teamSlug', ':rideSlug'),
    component: RideDetailPage,
    auth: 'public',
    parentId: 'team-detail',
    breadcrumb: { type: 'dynamic', entity: 'ride' },
  },
  {
    id: 'ride-edit',
    path: paths.rideEdit(':teamSlug', ':rideSlug'),
    component: EditRidePage,
    auth: 'authenticated',
    parentId: 'ride-detail',
    breadcrumb: { type: 'static', i18nKey: tRegister('actions.edit') },
    showBackLink: true,
  },

  // === Ride Template Routes ===
  {
    id: 'ride-templates',
    path: paths.rideTemplates(':teamSlug'),
    component: RideTemplateListPage,
    auth: 'authenticated',
    parentId: 'team-admin',
    breadcrumb: { type: 'static', i18nKey: tRegister('teams.admin.tabs.rideTemplates') },
  },
  {
    id: 'ride-template-new',
    path: paths.rideTemplateNew(':teamSlug'),
    component: CreateRideTemplatePage,
    auth: 'authenticated',
    parentId: 'ride-templates',
    breadcrumb: { type: 'static', i18nKey: tRegister('actions.new') },
    showBackLink: true,
  },
  {
    id: 'ride-template-edit',
    path: paths.rideTemplateEdit(':teamSlug', ':templateSlug'),
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
    path: paths.tripNew(':teamSlug'),
    component: CreateTripPage,
    auth: 'authenticated',
    parentId: 'team-detail',
    breadcrumb: { type: 'static', i18nKey: tRegister('trips.create.title') },
    showBackLink: true,
  },
  {
    id: 'trip-detail',
    path: paths.trip(':teamSlug', ':tripSlug'),
    component: TripDetailPage,
    auth: 'public',
    parentId: 'team-detail',
    breadcrumb: { type: 'dynamic', entity: 'trip' },
  },
  {
    id: 'trip-edit',
    path: paths.tripEdit(':teamSlug', ':tripSlug'),
    component: EditTripPage,
    auth: 'authenticated',
    parentId: 'trip-detail',
    breadcrumb: { type: 'static', i18nKey: tRegister('actions.edit') },
    showBackLink: true,
  },
  {
    id: 'stage-detail',
    path: paths.stage(':teamSlug', ':tripSlug', ':stageSlug'),
    component: StageDetailPage,
    auth: 'public',
    parentId: 'trip-detail',
    breadcrumb: { type: 'dynamic', entity: 'stage' },
  },

  // === Post Routes ===
  // Note: posts have parent team-detail (no posts list page)
  {
    id: 'post-new',
    path: paths.postNew(':teamSlug'),
    component: CreatePostPage,
    auth: 'authenticated',
    parentId: 'team-detail',
    breadcrumb: { type: 'static', i18nKey: tRegister('posts.create.title') },
    showBackLink: true,
  },
  {
    id: 'post-detail',
    path: paths.post(':teamSlug', ':postSlug'),
    component: PostDetailPage,
    auth: 'public',
    parentId: 'team-detail',
    breadcrumb: { type: 'dynamic', entity: 'post' },
  },
  {
    id: 'post-edit',
    path: paths.postEdit(':teamSlug', ':postSlug'),
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
    path: paths.routes(':teamSlug'),
    component: RouteListPage,
    auth: 'public',
    parentId: 'team-detail',
    breadcrumb: { type: 'static', i18nKey: tRegister('nav.routes') },
  },
  {
    id: 'route-new',
    path: paths.routeNew(':teamSlug'),
    component: CreateRoutePage,
    auth: 'authenticated',
    parentId: 'routes',
    breadcrumb: { type: 'static', i18nKey: tRegister('actions.new') },
    showBackLink: true,
  },
  {
    id: 'route-detail',
    path: paths.route(':teamSlug', ':routeSlug'),
    component: RouteDetailPage,
    auth: 'public',
    parentId: 'routes',
    breadcrumb: { type: 'dynamic', entity: 'route' },
  },
  {
    id: 'route-edit',
    path: paths.routeEdit(':teamSlug', ':routeSlug'),
    component: EditRoutePage,
    auth: 'authenticated',
    parentId: 'route-detail',
    breadcrumb: { type: 'static', i18nKey: tRegister('actions.edit') },
    showBackLink: true,
  },

  // === Ad Routes ===
  {
    id: 'ads',
    path: paths.ads(':teamSlug'),
    component: AdListPage,
    auth: 'public',
    parentId: 'team-detail',
    breadcrumb: { type: 'static', i18nKey: tRegister('ads.title') },
  },
  {
    id: 'ad-new',
    path: paths.adNew(':teamSlug'),
    component: CreateAdPage,
    auth: 'authenticated',
    parentId: 'ads',
    breadcrumb: { type: 'static', i18nKey: tRegister('ads.create.title') },
    showBackLink: true,
  },
  {
    id: 'ad-detail',
    path: paths.ad(':teamSlug', ':adSlug'),
    component: AdDetailPage,
    auth: 'public',
    parentId: 'ads',
    breadcrumb: { type: 'dynamic', entity: 'ad' },
  },
  {
    id: 'ad-edit',
    path: paths.adEdit(':teamSlug', ':adSlug'),
    component: EditAdPage,
    auth: 'authenticated',
    parentId: 'ad-detail',
    breadcrumb: { type: 'static', i18nKey: tRegister('actions.edit') },
    showBackLink: true,
  },
]
