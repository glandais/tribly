import { ConfigDto } from '@/api/dto'

let cachedConfig: ConfigDto | null = null
let configPromise: Promise<ConfigDto> | null = null

export async function fetchAppConfig(): Promise<ConfigDto> {
  if (cachedConfig) {
    return cachedConfig
  }

  if (configPromise) {
    return configPromise
  }

  configPromise = (async () => {
    const response = await fetch('/api/config')
    if (!response.ok) {
      throw new Error(`Failed to fetch app config: ${response.status}`)
    }
    cachedConfig = await response.json()
    return cachedConfig!
  })()

  return configPromise
}

export function getAppConfig(): ConfigDto | null {
  return cachedConfig
}

/**
 * When the site is served on a dedicated hostname (domain alias) pinned to a single team, returns
 * that team's slug. The app then roots on this team instead of showing the multi-team landing.
 * Returns null on a regular multi-team domain.
 */
export function getPinnedTeamSlug(): string | null {
  return cachedConfig?.pinnedTeamSlug ?? null
}
