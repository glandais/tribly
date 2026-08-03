import { useTranslation } from 'react-i18next'
import { ActionIcon, Box, useMantineColorScheme } from '@mantine/core'
import { IconSun, IconMoon } from '@tabler/icons-react'
import { useUpdateMyPreferences } from '@/api/endpoints/users/users'
import { useAuthStore, selectIsAuthenticated } from '@/store/authStore'
import { useResolvedColorScheme } from '@/hooks/useResolvedColorScheme'

export function ColorSchemeSwitcher() {
  const { t } = useTranslation()
  const { setColorScheme } = useMantineColorScheme()
  const computedColorScheme = useResolvedColorScheme()
  const isAuthenticated = useAuthStore(selectIsAuthenticated)
  const mutation = useUpdateMyPreferences()

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
      {/* lightHidden/darkHidden are pure CSS (:root[data-mantine-color-scheme] selectors), shown
          or hidden the instant the attribute is set — before hydration, before any React state.
          A JS-computed icon (e.g. gated on a `hydrated` flag) would flash the wrong one for
          anonymous visitors, since the attribute is only known once the client resolves
          localStorage/matchMedia, which happens before React ever renders but after the server's
          always-light guess. */}
      <Box component={IconSun} size={18} lightHidden />
      <Box component={IconMoon} size={18} darkHidden />
    </ActionIcon>
  )
}
