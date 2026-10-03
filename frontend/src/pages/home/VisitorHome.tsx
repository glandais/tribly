import { useCallback, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { Stack } from '@mantine/core'
import { isSingleTeam } from '../../config/appConfig'
import type { LoginFormMode } from '../../components/auth/LoginForm'
import { HomeLayout } from '../../components/home/HomeLayout'
import { HomeFeedSection } from '../../components/home/HomeFeedSection'
import { VisitorHero, LOGIN_ANCHOR } from '../../components/home/visitor/VisitorHero'
import { FeatureTeaser } from '../../components/home/visitor/FeatureTeaser'
import { DevicesStrip } from '../../components/home/visitor/DevicesStrip'
import { TeamCtaBand } from '../../components/home/visitor/TeamCtaBand'
import { useHomeFeedData } from './homeFeedData'

/**
 * The home of a visitor who is not signed in: the pitch and the sign-in form first, a teaser of
 * the features page, the public feed, the bike computers, and a call to bring a team over.
 */
export function VisitorHome() {
  const { t } = useTranslation()
  const feed = useHomeFeedData()
  const singleTeam = isSingleTeam()
  const [loginMode, setLoginMode] = useState<LoginFormMode>('login')

  const openRegister = useCallback(() => {
    setLoginMode('register')
    document.getElementById(LOGIN_ANCHOR)?.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }, [])

  return (
    <HomeLayout
      currentTab="feed"
      header={<VisitorHero loginMode={loginMode} onLoginModeChange={setLoginMode} />}
    >
      <Stack gap={48}>
        <FeatureTeaser />
        <HomeFeedSection
          feed={feed}
          title={t('home.visitor.feed.title')}
          subtitle={t('home.visitor.feed.subtitle')}
        />
        <DevicesStrip />
        {!singleTeam && <TeamCtaBand onCreateAccount={openRegister} />}
      </Stack>
    </HomeLayout>
  )
}
