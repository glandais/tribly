import Keycloak from 'keycloak-js';
import { fetchAppConfig } from './appConfig';

let keycloak: Keycloak | null = null;
let initialized = false;
let initPromise: Promise<boolean> | null = null;

export const initKeycloak = async (): Promise<boolean> => {
  if (initialized && keycloak) {
    return keycloak.authenticated ?? false;
  }

  if (initPromise) {
    return initPromise;
  }

  initPromise = (async () => {
    try {
      const config = await fetchAppConfig();

      keycloak = new Keycloak({
        url: config.keycloak.url,
        realm: config.keycloak.realm,
        clientId: config.keycloak.clientId,
      });

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
          keycloak!.updateToken(70).catch(() => {
            console.warn('Failed to refresh token, logging out');
            keycloak!.logout();
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

export const getKeycloak = (): Keycloak | null => {
  return keycloak;
};

export const getToken = (): string | undefined => {
  return keycloak?.token;
};

export const isAuthenticated = (): boolean => {
  return !!keycloak?.authenticated;
};

export interface KeycloakUserProfile {
  id: string;
  email: string;
  displayName: string;
}

export const getUserProfile = (): KeycloakUserProfile | null => {
  if (!keycloak?.tokenParsed) return null;

  const tokenParsed = keycloak.tokenParsed as Record<string, unknown>;

  return {
    id: tokenParsed.sub as string,
    email: (tokenParsed.email as string) || '',
    displayName:
      (tokenParsed.name as string) ||
      (tokenParsed.preferred_username as string) ||
      '',
  };
};

export default { getKeycloak, initKeycloak, getToken, isAuthenticated, getUserProfile };
