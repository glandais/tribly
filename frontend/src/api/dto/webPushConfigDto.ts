/**
 * Firebase web configuration, for push notifications in the browser
 */
export interface WebPushConfigDto {
  /** Firebase web API key */
  apiKey: string
  /** Firebase project id, the one the server sends through */
  projectId: string
  /** Firebase web app id */
  appId: string
  /** FCM sender id (the project number) */
  messagingSenderId: string
  /** Public VAPID key of the project's web push certificate */
  vapidKey: string
}
