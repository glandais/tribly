import type { ClientContextDto } from '@/api/dto'
import { getVersion } from '@/api/endpoints/server-version/server-version'
import i18n from '@/i18n'
import { pathOnly } from './clientLog'

/**
 * The site has no version of its own: it is built and deployed with the API, so the server's
 * commit (or API version) identifies the bundle. Fetched once; a failure falls back on "web".
 */
let versionPromise: Promise<string> | undefined

function appVersion(): Promise<string> {
  versionPromise ??= getVersion({ skipErrorToast: true })
    .then((v) => (v.commit || v.apiVersion).slice(0, 50))
    .catch(() => 'web')
  return versionPromise
}

export async function buildClientContext(): Promise<ClientContextDto> {
  return {
    platform: 'WEB',
    appVersion: await appVersion(),
    userAgent: navigator.userAgent.slice(0, 500),
    route: pathOnly(window.location.pathname).slice(0, 500),
    locale: i18n.language?.slice(0, 20),
    timezone: Intl.DateTimeFormat().resolvedOptions().timeZone?.slice(0, 64),
  }
}
