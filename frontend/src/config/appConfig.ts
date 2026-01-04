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
