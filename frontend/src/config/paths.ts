/**
 * Type-safe path builders for navigation
 * Single source for constructing URLs throughout the app
 */
export const paths = {
  home: () => '/', // mobile OK
  login: () => '/login', // mobile OK
  deviceVerifyGarmin: () => '/garmin',
  deviceVerifyKaroo: () => '/karoo',
  verifyEmail: () => '/verify-email', // mobile OK
  forgotPassword: () => '/forgot-password', // mobile OK
  resetPassword: () => '/reset-password', // mobile OK
  privacy: () => '/privacy', // mobile OK
  terms: () => '/terms', // mobile OK
  profile: () => '/profile', // mobile OK
  calendar: () => '/calendar', // mobile OK
  allRoutes: () => '/routes',

  // Teams
  teams: () => '/teams', // mobile OK
  teamsNew: () => '/teams/new',
  team: (teamSlug: string) => `/teams/${teamSlug}`, // mobile OK
  teamAbout: (teamSlug: string) => `/teams/${teamSlug}/about`, // mobile OK (redirects to team)
  teamCalendar: (teamSlug: string) => `/teams/${teamSlug}/calendar`, // mobile OK

  // Team Pages (displayed as tabs)
  teamPage: (teamSlug: string, pageSlug: string) => `/teams/${teamSlug}/pages/${pageSlug}`,

  // Team Admin
  teamAdmin: (teamSlug: string) => `/teams/${teamSlug}/admin`,
  teamAdminPlaces: (teamSlug: string) => `/teams/${teamSlug}/admin/places`,
  teamAdminPages: (teamSlug: string) => `/teams/${teamSlug}/admin/pages`,
  teamAdminPageNew: (teamSlug: string) => `/teams/${teamSlug}/admin/pages/new`,
  teamAdminPageEdit: (teamSlug: string, pageSlug: string) =>
    `/teams/${teamSlug}/admin/pages/${pageSlug}/edit`,
  teamMembers: (teamSlug: string) => `/teams/${teamSlug}/admin/members`,
  teamSettings: (teamSlug: string) => `/teams/${teamSlug}/admin/settings`,

  // Rides
  rideNew: (teamSlug: string) => `/teams/${teamSlug}/rides/new`,
  ride: (teamSlug: string, rideSlug: string) => `/teams/${teamSlug}/rides/${rideSlug}`, // mobile OK
  rideEdit: (teamSlug: string, rideSlug: string) => `/teams/${teamSlug}/rides/${rideSlug}/edit`,

  // Ride Templates
  rideTemplates: (teamSlug: string) => `/teams/${teamSlug}/admin/ride-templates`,
  rideTemplateNew: (teamSlug: string) => `/teams/${teamSlug}/admin/ride-templates/new`,
  rideTemplateEdit: (teamSlug: string, templateSlug: string) =>
    `/teams/${teamSlug}/admin/ride-templates/${templateSlug}/edit`,

  // Trips
  tripNew: (teamSlug: string) => `/teams/${teamSlug}/trips/new`,
  trip: (teamSlug: string, tripSlug: string) => `/teams/${teamSlug}/trips/${tripSlug}`,
  tripEdit: (teamSlug: string, tripSlug: string) => `/teams/${teamSlug}/trips/${tripSlug}/edit`,
  stage: (teamSlug: string, tripSlug: string, stageSlug: string) =>
    `/teams/${teamSlug}/trips/${tripSlug}/stages/${stageSlug}`,

  // Posts
  postNew: (teamSlug: string) => `/teams/${teamSlug}/posts/new`,
  post: (teamSlug: string, postSlug: string) => `/teams/${teamSlug}/posts/${postSlug}`,
  postEdit: (teamSlug: string, postSlug: string) => `/teams/${teamSlug}/posts/${postSlug}/edit`,

  // Routes
  routes: (teamSlug: string) => `/teams/${teamSlug}/routes`, // mobile OK
  routeNew: (teamSlug: string) => `/teams/${teamSlug}/routes/new`,
  route: (teamSlug: string, routeSlug: string) => `/teams/${teamSlug}/routes/${routeSlug}`, // mobile OK
  routeEdit: (teamSlug: string, routeSlug: string) => `/teams/${teamSlug}/routes/${routeSlug}/edit`,

  // Ads
  ads: (teamSlug: string) => `/teams/${teamSlug}/ads`,
  adNew: (teamSlug: string) => `/teams/${teamSlug}/ads/new`,
  ad: (teamSlug: string, adSlug: string) => `/teams/${teamSlug}/ads/${adSlug}`,
  adEdit: (teamSlug: string, adSlug: string) => `/teams/${teamSlug}/ads/${adSlug}/edit`,

  // Platform Admin
  admin: () => '/admin',
  adminDomains: () => '/admin/domains',
  adminTeams: () => '/admin/teams',
  adminUsers: () => '/admin/users',
} as const
