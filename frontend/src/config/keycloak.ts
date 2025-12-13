import Keycloak from 'keycloak-js';

const keycloakConfig = {
  url: import.meta.env.VITE_KEYCLOAK_URL || 'http://localhost:8180',
  realm: import.meta.env.VITE_KEYCLOAK_REALM || 'tribly',
  clientId: import.meta.env.VITE_KEYCLOAK_CLIENT_ID || 'tribly-frontend',
};

export const keycloak = new Keycloak(keycloakConfig);

let initialized = false;
let initPromise: Promise<boolean> | null = null;

export const initKeycloak = async (): Promise<boolean> => {
  if (initialized) {
    return keycloak.authenticated ?? false;
  }

  if (initPromise) {
    return initPromise;
  }

  initPromise = (async () => {
    try {
      const authenticated = await keycloak.init({
        onLoad: 'check-sso',
        silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html',
        pkceMethod: 'S256',
        checkLoginIframe: false,
      });

      initialized = true;

      // Setup token refresh
      if (authenticated) {
        setInterval(() => {
          keycloak.updateToken(70).catch(() => {
            console.warn('Failed to refresh token, logging out');
            keycloak.logout();
          });
        }, 60000);
      }

      return authenticated;
    } catch (error) {
      console.error('Keycloak initialization failed:', error);
      initialized = true;
      return false;
    }
  })();

  return initPromise;
};

export const getToken = (): string | undefined => {
  return keycloak.token;
};

export const isAuthenticated = (): boolean => {
  return !!keycloak.authenticated;
};

export interface KeycloakUserProfile {
  id: string;
  email: string;
  displayName: string;
  avatarUrl: string | null;
  stravaId: string | null;
}

export const getUserProfile = (): KeycloakUserProfile | null => {
  if (!keycloak.tokenParsed) return null;

  const tokenParsed = keycloak.tokenParsed as Record<string, unknown>;

  return {
    id: tokenParsed.sub as string,
    email: (tokenParsed.email as string) || '',
    displayName:
      (tokenParsed.name as string) ||
      (tokenParsed.preferred_username as string) ||
      '',
    avatarUrl: (tokenParsed.avatar_url as string) || null,
    stravaId: (tokenParsed.strava_id as string) || null,
  };
};

export default keycloak;
