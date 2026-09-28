/*
 * The site's service worker: it exists to make the site installable and to show web push
 * notifications — nothing else.
 *
 * It caches NOTHING and has NO fetch handler, on purpose. The SSR HTML is rendered for whoever
 * sent the cookie and embeds their access token (server.js serves it no-store): a worker that
 * cached pages would hand one member's page, session included, to the next person on that browser.
 * Keep it that way — offline support is not worth that risk.
 *
 * Plain JavaScript, served as-is from public/ (server.js gives it no-cache so an update is picked
 * up at once). The payload is FCM's data-only web message, built by FcmClient.message() for the WEB
 * platform: { data: { title, body, path, notificationId, type, ... } }.
 */

self.addEventListener('install', () => {
  self.skipWaiting()
})

self.addEventListener('activate', (event) => {
  event.waitUntil(self.clients.claim())
})

self.addEventListener('push', (event) => {
  let data = {}
  try {
    const payload = event.data ? event.data.json() : {}
    data = payload.data || payload
  } catch {
    // An unreadable payload still has to show something: browsers penalise (Safari revokes) a
    // push that displays no notification.
  }
  const title = data.title || self.registration.scope
  event.waitUntil(
    self.registration.showNotification(title, {
      body: data.body || '',
      icon: '/pwa-192x192.png',
      badge: '/pwa-64x64.png',
      // One notification per inbox entry: a retried delivery replaces it instead of stacking.
      tag: data.notificationId || undefined,
      data: { path: data.path || '/' },
    })
  )
})

self.addEventListener('notificationclick', (event) => {
  event.notification.close()
  const target = new URL(event.notification.data?.path || '/', self.location.origin).href
  event.waitUntil(
    (async () => {
      const windows = await self.clients.matchAll({ type: 'window', includeUncontrolled: true })
      for (const client of windows) {
        if (new URL(client.url).origin === self.location.origin && 'focus' in client) {
          await client.focus()
          if ('navigate' in client) {
            await client.navigate(target)
          }
          return
        }
      }
      await self.clients.openWindow(target)
    })()
  )
})
