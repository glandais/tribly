/**
 * Platform admin request to update team governance attributes
 */
export interface AdminTeamAttributesRequest {
  /** Whether team admins can change visibility */
  visibilityEditable: boolean
  /** Whether any domain user can join this public team */
  joinable: boolean
  /** Whether team admins can add members */
  addMemberAllowed: boolean
  /** Whether the interactive route planner is open to this team */
  enableRoutePlanner: boolean
}
