import { useEffect, useRef, useState } from 'react'
import { useSearchParams, Link, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { IconX, IconBrandStrava } from '@tabler/icons-react'
import { Center, Paper, Stack, Title, Text, Button, Loader } from '@mantine/core'
import { paths } from '@/config/paths'
import { useAuthStore } from '@/store/authStore'
import { createStravaSession } from '@/api/endpoints/strava-authentication/strava-authentication'

type CallbackState = 'loading' | 'error'

/**
 * Landing page for the Strava OAuth hand-off. Exchanges the one-time code (minted by the backend
 * callback) for a first-party session, then routes to the email-completion step for migrated
 * accounts or home otherwise.
 */
export function StravaCallbackPage() {
  const { t } = useTranslation()
  const navigate = useNavigate()
  const [searchParams] = useSearchParams()
  const code = searchParams.get('code')
  const needsEmail = searchParams.get('needs_email') === '1'
  const stravaError = searchParams.get('strava_error')
  const { setAccessToken, setUser } = useAuthStore()

  const [state, setState] = useState<CallbackState>(() =>
    !code || stravaError ? 'error' : 'loading'
  )
  const hasRun = useRef(false)

  useEffect(() => {
    if (!code || stravaError || hasRun.current) {
      return
    }
    hasRun.current = true

    const exchange = async () => {
      try {
        const data = await createStravaSession({ code })
        if (data.accessToken) setAccessToken(data.accessToken)
        if (data.user) setUser(data.user)
        navigate(needsEmail ? paths.completeAccount() : paths.home(), { replace: true })
      } catch {
        setState('error')
      }
    }

    exchange()
  }, [code, stravaError, needsEmail, navigate, setAccessToken, setUser])

  const errorMessage =
    stravaError === 'no_account'
      ? t('auth.strava.errors.noAccount')
      : t('auth.strava.errors.callbackFailed')

  return (
    <Center mih="70vh">
      <Paper shadow="lg" radius="lg" p="xl" w="100%" maw={420}>
        {state === 'loading' ? (
          <Stack align="center" gap="md">
            <IconBrandStrava size={48} color="var(--mantine-color-orange-6)" />
            <Loader size="lg" />
            <Text>{t('auth.strava.connecting')}</Text>
          </Stack>
        ) : (
          <Stack ta="center">
            <IconX size={48} style={{ margin: '0 auto' }} color="var(--mantine-color-red-6)" />
            <Title order={2}>{t('auth.strava.errors.title')}</Title>
            <Text c="dimmed">{errorMessage}</Text>
            <Button component={Link} to={paths.login()} fullWidth>
              {t('auth.strava.backToLogin')}
            </Button>
          </Stack>
        )}
      </Paper>
    </Center>
  )
}
