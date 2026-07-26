export type GetRouteElevationProfileParams = {
  /**
   * Number of profile points wanted. Clamped server-side to 2..1000, and further reduced to the number of points actually stored for the route.
   */
  samples?: number
}
