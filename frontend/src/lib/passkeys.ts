import { startRegistration, startAuthentication } from '@simplewebauthn/browser'
import type {
  PublicKeyCredentialCreationOptionsJSON,
  PublicKeyCredentialRequestOptionsJSON,
} from '@simplewebauthn/browser'
import {
  getRegistrationOptions,
  registerPasskey,
  getAuthenticationOptions,
  authenticate,
} from '@/api/endpoints/passkeys/passkeys'
import type { RegisterPasskeyBody, AuthenticateBody } from '@/api/dto'

/**
 * Register a new passkey for the current authenticated user.
 * This requires the user to be logged in.
 */
export async function registerNewPasskey(deviceName?: string) {
  // 1. Get registration options from server
  const optionsResponse = await getRegistrationOptions()
  const options = optionsResponse as PublicKeyCredentialCreationOptionsJSON

  // 2. Create credential using browser WebAuthn API
  const attestation = await startRegistration({ optionsJSON: options })

  // 3. Send attestation to server for verification
  // Cast to RegisterPasskeyBody as simplewebauthn types are compatible but don't have index signature
  const passkey = await registerPasskey(attestation as unknown as RegisterPasskeyBody, {
    deviceName,
  })

  return passkey
}

/**
 * Authenticate using a passkey.
 * Can be called without being logged in.
 */
export async function authenticateWithPasskey(email?: string) {
  // 1. Get authentication options from server
  const optionsResponse = await getAuthenticationOptions({ email })
  const options = optionsResponse as PublicKeyCredentialRequestOptionsJSON

  // 2. Get assertion using browser WebAuthn API
  const assertion = await startAuthentication({ optionsJSON: options })

  // 3. Verify assertion on server and get tokens
  // Cast to AuthenticateBody as simplewebauthn types are compatible but don't have index signature
  const authResponse = await authenticate(assertion as unknown as AuthenticateBody)

  return authResponse
}

/**
 * Check if WebAuthn is supported in the current browser.
 */
export function isPasskeySupported(): boolean {
  return (
    typeof window !== 'undefined' &&
    typeof window.PublicKeyCredential !== 'undefined' &&
    typeof window.PublicKeyCredential.isUserVerifyingPlatformAuthenticatorAvailable === 'function'
  )
}

/**
 * Check if a platform authenticator (Touch ID, Face ID, Windows Hello) is available.
 */
export async function isPlatformAuthenticatorAvailable(): Promise<boolean> {
  if (!isPasskeySupported()) {
    return false
  }
  try {
    return await window.PublicKeyCredential.isUserVerifyingPlatformAuthenticatorAvailable()
  } catch {
    return false
  }
}
