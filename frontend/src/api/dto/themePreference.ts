export type ThemePreference = (typeof ThemePreference)[keyof typeof ThemePreference]

export const ThemePreference = {
  SYSTEM: 'SYSTEM',
  LIGHT: 'LIGHT',
  DARK: 'DARK',
} as const
