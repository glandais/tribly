export type PreviewTeamTimezoneChangeParams = {
  /**
   * IANA zone the team would move to
   * @maxLength 64
   * @pattern \S
   */
  timezone: string
}
