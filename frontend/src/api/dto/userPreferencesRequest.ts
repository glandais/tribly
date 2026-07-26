import type { ThemePreference } from './themePreference.ts'
import type { UnitSystem } from './unitSystem.ts'

/**
 * Partial update of the current user's display preferences
 */
export interface UserPreferencesRequest {
  /** Preferred unit system. Omit or send null to leave it unchanged. */
  unitSystem?: UnitSystem
  /** Preferred colour scheme. Omit or send null to leave it unchanged. */
  theme?: ThemePreference
  /**
   * Preferred language as a BCP-47 tag ('fr', 'en', 'fr-CA'). Omit or send null to leave it unchanged. Not validated against the set of translations the app ships: a client asking for a language nobody has translated yet falls back on its own, which is better than a 400 the day a translation lands.
   * @minLength 2
   * @maxLength 10
   * @pattern ^[A-Za-z]{2,3}(-[A-Za-z0-9]{2,8})*$
   */
  language?: string
  /** Whether team members may reach you through the classified-ad relay. Omit or send null to leave it unchanged. Setting it to false stops the relay from delivering to you; your ads stay visible, they simply stop being answerable. */
  contactableByMembers?: boolean
}
