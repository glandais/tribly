export interface AppConfig {
  keycloak: {
    url: string;
    realm: string;
    clientId: string;
  };
  map: {
    tileUrl: string;
    attribution: string;
  };
}

let cachedConfig: AppConfig | null = null;
let configPromise: Promise<AppConfig> | null = null;

export async function fetchAppConfig(): Promise<AppConfig> {
  if (cachedConfig) {
    return cachedConfig;
  }

  if (configPromise) {
    return configPromise;
  }

  configPromise = (async () => {
    const response = await fetch('/api/config');
    if (!response.ok) {
      throw new Error(`Failed to fetch app config: ${response.status}`);
    }
    cachedConfig = await response.json();
    return cachedConfig!;
  })();

  return configPromise;
}

export function getAppConfig(): AppConfig | null {
  return cachedConfig;
}
