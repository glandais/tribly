import { useSyncExternalStore } from 'react'
import { useTranslation } from 'react-i18next'
import { ActionIcon, useMantineColorScheme, useComputedColorScheme } from '@mantine/core'
import { IconSun, IconMoon } from '@tabler/icons-react'

const emptySubscribe = () => () => {}

export function ColorSchemeSwitcher() {
  const { t } = useTranslation()
  const { setColorScheme } = useMantineColorScheme()
  const computedColorScheme = useComputedColorScheme('light')
  // The server doesn't know the user's scheme (localStorage), so it always renders the moon
  // icon. During hydration React uses the server snapshot (false), so the first client render
  // matches the markup; right after, it re-renders with the real scheme — no hydration mismatch
  // for users with a stored dark scheme.
  const hydrated = useSyncExternalStore(
    emptySubscribe,
    () => true,
    () => false
  )

  const toggleColorScheme = () => {
    const colorScheme = computedColorScheme === 'dark' ? 'light' : 'dark'
    setColorScheme(colorScheme)
    document.documentElement.setAttribute('data-color-scheme', colorScheme)
  }

  return (
    <ActionIcon
      onClick={toggleColorScheme}
      variant="default"
      size="md"
      aria-label={t('nav.colorScheme')}
    >
      {hydrated && computedColorScheme === 'dark' ? <IconSun size={18} /> : <IconMoon size={18} />}
    </ActionIcon>
  )
}
