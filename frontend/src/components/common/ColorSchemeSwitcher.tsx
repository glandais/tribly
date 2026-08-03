import { useSyncExternalStore } from 'react'
import { useTranslation } from 'react-i18next'
import { ActionIcon, useMantineColorScheme, useComputedColorScheme } from '@mantine/core'
import { IconSun, IconMoon } from '@tabler/icons-react'
import { useUpdateMyPreferences } from '@/api/endpoints/users/users'
import { useAuthStore, selectIsAuthenticated } from '@/store/authStore'

const emptySubscribe = () => () => {}

export function ColorSchemeSwitcher() {
  const { t } = useTranslation()
  const { colorScheme, setColorScheme } = useMantineColorScheme()
  const computedColorScheme = useComputedColorScheme('light')
  const isAuthenticated = useAuthStore(selectIsAuthenticated)
  const mutation = useUpdateMyPreferences()
  // Only 'auto' (anonymous visitors, or a SYSTEM preference) risks a hydration mismatch: its
  // resolution depends on localStorage/matchMedia, which the server can't see, so the first
  // client render must match the server's always-light guess and only pick up the real scheme
  // once mounted. An explicit LIGHT/DARK preference is already known server-side (AppProviders'
  // defaultColorScheme) and matches from the very first render — gating it the same way would
  // just flash the wrong icon for every signed-in visitor on every page load.
  const hydrated = useSyncExternalStore(
    emptySubscribe,
    () => true,
    () => false
  )
  const needsHydrationGuard = colorScheme === 'auto'

  const toggleColorScheme = () => {
    const colorScheme = computedColorScheme === 'dark' ? 'light' : 'dark'
    setColorScheme(colorScheme)
    document.documentElement.setAttribute('data-color-scheme', colorScheme)
    // Anonymous visitors have nothing to persist theme to; partial PATCH sends only the changed
    // field, never the whole preference set.
    if (isAuthenticated) {
      mutation.mutate({ data: { theme: colorScheme === 'dark' ? 'DARK' : 'LIGHT' } })
    }
  }

  return (
    <ActionIcon
      onClick={toggleColorScheme}
      variant="default"
      size="md"
      aria-label={t('nav.colorScheme')}
    >
      {(hydrated || !needsHydrationGuard) && computedColorScheme === 'dark' ? (
        <IconSun size={18} />
      ) : (
        <IconMoon size={18} />
      )}
    </ActionIcon>
  )
}
