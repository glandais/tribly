/**
 * Type-safe path builders for navigation
 * Single source for constructing URLs throughout the app
 */
export const paths = {
  home: () => '/',
  login: () => '/login',
  profile: () => '/profile',

  // Teams
  teams: () => '/teams',
  teamsNew: () => '/teams/new',
  team: (teamSlug: string) => `/teams/${teamSlug}`,
  teamAbout: (teamSlug: string) => `/teams/${teamSlug}/about`,

  // Team Admin
  teamAdmin: (teamSlug: string) => `/teams/${teamSlug}/admin`,
  teamAdminPlaces: (teamSlug: string) => `/teams/${teamSlug}/admin/places`,
  teamMembers: (teamSlug: string) => `/teams/${teamSlug}/admin/members`,
  teamSettings: (teamSlug: string) => `/teams/${teamSlug}/admin/settings`,

  // Rides
  rideNew: (teamSlug: string) => `/teams/${teamSlug}/rides/new`,
  ride: (teamSlug: string, rideSlug: string) => `/teams/${teamSlug}/rides/${rideSlug}`,
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

  // Posts
  postNew: (teamSlug: string) => `/teams/${teamSlug}/posts/new`,
  post: (teamSlug: string, postSlug: string) => `/teams/${teamSlug}/posts/${postSlug}`,
  postEdit: (teamSlug: string, postSlug: string) => `/teams/${teamSlug}/posts/${postSlug}/edit`,

  // Routes
  routes: (teamSlug: string) => `/teams/${teamSlug}/routes`,
  routeNew: (teamSlug: string) => `/teams/${teamSlug}/routes/new`,
  route: (teamSlug: string, routeSlug: string) => `/teams/${teamSlug}/routes/${routeSlug}`,
  routeEdit: (teamSlug: string, routeSlug: string) => `/teams/${teamSlug}/routes/${routeSlug}/edit`,
} as const
