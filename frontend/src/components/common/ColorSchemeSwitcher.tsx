import { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { ActionIcon, useMantineColorScheme, useComputedColorScheme } from '@mantine/core'
import { IconSun, IconMoon } from '@tabler/icons-react'

export function ColorSchemeSwitcher() {
  const { t } = useTranslation()
  const { setColorScheme } = useMantineColorScheme()
  const computedColorScheme = useComputedColorScheme('light')
  // The server doesn't know the user's scheme (localStorage), so it always renders the moon
  // icon. Mirror that on the first client render and swap to the real icon after mount —
  // otherwise hydration mismatches for every user with a stored dark scheme.
  const [mounted, setMounted] = useState(false)
  useEffect(() => setMounted(true), [])

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
      {mounted && computedColorScheme === 'dark' ? <IconSun size={18} /> : <IconMoon size={18} />}
    </ActionIcon>
  )
}
