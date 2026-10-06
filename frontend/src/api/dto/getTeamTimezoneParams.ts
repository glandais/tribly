export type GetTeamTimezoneParams = {
  /**
   * Latitude of the point; omitted with lon: the team's zone
   * @minimum -90
   * @maximum 90
   */
  lat?: number
  /**
   * Longitude of the point; omitted with lat: the team's zone
   * @minimum -180
   * @maximum 180
   */
  lon?: number
}
