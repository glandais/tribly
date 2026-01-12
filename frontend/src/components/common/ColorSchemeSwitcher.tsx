import { useTranslation } from 'react-i18next'
import { ActionIcon, useMantineColorScheme, useComputedColorScheme } from '@mantine/core'
import { IconSun, IconMoon } from '@tabler/icons-react'

export function ColorSchemeSwitcher() {
  const { t } = useTranslation()
  const { setColorScheme } = useMantineColorScheme()
  const computedColorScheme = useComputedColorScheme('light')

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
      {computedColorScheme === 'dark' ? <IconSun size={18} /> : <IconMoon size={18} />}
    </ActionIcon>
  )
}
