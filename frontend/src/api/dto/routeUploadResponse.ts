/**
 * Result of uploading a route to a GPS service
 */
export interface RouteUploadResponse {
  /** Whether the upload was successful */
  success: boolean
  /** Error message if upload failed */
  message?: string
  /** External route ID on the GPS service */
  externalRouteId?: string
}
