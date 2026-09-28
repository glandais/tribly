import {
  registerPushDevice,
  unregisterPushDevice,
} from '@/api/endpoints/notifications/notifications'
import { PushPlatform, type WebPushConfigDto } from '@/api/dto'
import { devicePlatform, isStandalone } from '@/lib/install/installStore'

/**
 * Push notifications in the browser, through the same FCM channel as the mobile app (platform
 * `WEB`). The site's service worker (`public/sw.js`) displays them; this module only subscribes
 * the browser and tells the server.
 *
 * Firebase is imported dynamically, when a subscription is actually made or refreshed: it is
 * never in the initial bundle nor in the SSR one. Nothing here runs at module scope.
 */

/** The FCM token this browser registered, so it can be unregistered at sign-out. */
const TOKEN_KEY = 'pedalons.webPush.token'

export type WebPushStatus =
  /** No push in this browser at all. */
  | 'unsupported'
  /** iOS: push exists only for the site added to the home screen. */
  | 'needsInstall'
  /** The member refused in the browser; only the browser's settings can undo it. */
  | 'denied'
  | 'disabled'
  | 'enabled'

function storedToken(): string | null {
  try {
    return localStorage.getItem(TOKEN_KEY)
  } catch {
    return null
  }
}

function storeToken(token: string | null): void {
  try {
    if (token) localStorage.setItem(TOKEN_KEY, token)
    else localStorage.removeItem(TOKEN_KEY)
  } catch {
    // Private mode: the subscription still works, it just won't be unregistered at sign-out.
  }
}

function isSupported(): boolean {
  return (
    typeof window !== 'undefined' &&
    'serviceWorker' in navigator &&
    'PushManager' in window &&
    'Notification' in window
  )
}

export function webPushStatus(): WebPushStatus {
  if (devicePlatform() === 'ios' && !isStandalone()) return 'needsInstall'
  if (!isSupported()) return 'unsupported'
  if (Notification.permission === 'denied') return 'denied'
  if (Notification.permission === 'granted' && storedToken()) return 'enabled'
  return 'disabled'
}

async function obtainToken(config: WebPushConfigDto): Promise<string> {
  const [{ getApps, initializeApp }, { getMessaging, getToken }] = await Promise.all([
    import('firebase/app'),
    import('firebase/messaging'),
  ])
  const app =
    getApps()[0] ??
    initializeApp({
      apiKey: config.apiKey,
      projectId: config.projectId,
      appId: config.appId,
      messagingSenderId: config.messagingSenderId,
    })
  // Our own worker, not Firebase's default firebase-messaging-sw.js: it is the one that shows
  // the notifications (and the one that makes the site installable).
  const registration = await navigator.serviceWorker.register('/sw.js')
  await navigator.serviceWorker.ready
  return getToken(getMessaging(app), {
    vapidKey: config.vapidKey,
    serviceWorkerRegistration: registration,
  })
}

/** "Firefox · Android", for the member's own device list. */
function deviceName(): string {
  const ua = navigator.userAgent
  const browser = /Edg\//.test(ua)
    ? 'Edge'
    : /Firefox\//.test(ua)
      ? 'Firefox'
      : /SamsungBrowser\//.test(ua)
        ? 'Samsung Internet'
        : /Chrome\//.test(ua)
          ? 'Chrome'
          : /Safari\//.test(ua)
            ? 'Safari'
            : 'Browser'
  const os = /iPhone|iPod/.test(ua)
    ? 'iPhone'
    : devicePlatform() === 'ios'
      ? 'iPad'
      : /Android/.test(ua)
        ? 'Android'
        : /Windows/.test(ua)
          ? 'Windows'
          : /Macintosh/.test(ua)
            ? 'macOS'
            : /Linux/.test(ua)
              ? 'Linux'
              : null
  return os ? `${browser} · ${os}` : browser
}

async function register(config: WebPushConfigDto): Promise<void> {
  const token = await obtainToken(config)
  const previous = storedToken()
  if (previous && previous !== token) {
    // The token rotated: drop the old row rather than wait for FCM to reject it.
    await unregisterPushDevice(previous).catch(() => undefined)
  }
  await registerPushDevice({ token, platform: PushPlatform.WEB, deviceName: deviceName() })
  storeToken(token)
}

/**
 * Asks for permission — call it from a click, browsers refuse otherwise — then subscribes this
 * browser and registers it for the signed-in member.
 */
export async function enableWebPush(config: WebPushConfigDto): Promise<WebPushStatus> {
  const permission = await Notification.requestPermission()
  if (permission === 'denied') return 'denied'
  if (permission !== 'granted') return 'disabled'
  await register(config)
  return 'enabled'
}

/**
 * At startup, for a browser the member already subscribed: registers the current token again.
 * FCM rotates tokens, and registering refreshes the device's last-seen date server-side.
 */
export async function refreshWebPush(config: WebPushConfigDto): Promise<void> {
  if (webPushStatus() !== 'enabled') return
  await register(config)
}

/**
 * Unsubscribes this browser. Called from the settings, and at sign-out — while the session is
 * still valid, since the unregistration is an authenticated call — so that a shared computer stops
 * receiving the previous member's notifications.
 */
export async function disableWebPush(): Promise<void> {
  const token = storedToken()
  if (!token) return
  storeToken(null)
  await unregisterPushDevice(token).catch(() => undefined)
  try {
    const registration = await navigator.serviceWorker?.getRegistration('/')
    const subscription = await registration?.pushManager.getSubscription()
    await subscription?.unsubscribe()
  } catch {
    // The server row is gone already: nothing will be sent to this browser any more.
  }
}
