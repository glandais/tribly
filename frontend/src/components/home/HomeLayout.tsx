import { useTranslation } from 'react-i18next'
import { Stack } from '@mantine/core'
import { useHomeNavItems } from '@/hooks/useNavItems'
import { NavButtons } from '../common/NavButtons'

export type HomeTab = 'feed' | 'routes' | 'teams' | 'calendar'

interface HomeLayoutProps {
  header?: React.ReactNode
  currentTab: HomeTab
  children: React.ReactNode
}

export function HomeLayout({ header = <></>, currentTab, children }: HomeLayoutProps) {
  const { t } = useTranslation()
  const tabs = useHomeNavItems()

  return (
    <Stack>
      {header}

      {/* Home Navigation */}
      <NavButtons items={tabs} currentId={currentTab} label={t('nav.landmark.home')} />

      {/* Page Content */}
      <div>{children}</div>
    </Stack>
  )
}
