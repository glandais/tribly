import { useCallback, useEffect, useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useForm } from '@mantine/form'
import { notifications } from '@mantine/notifications'
import { IconFingerprint, IconMail, IconDeviceWatch, IconCheck } from '@tabler/icons-react'
import {
  Center,
  Paper,
  Stack,
  Title,
  Text,
  Button,
  TextInput,
  Stepper,
  Alert,
  Group,
  ThemeIcon,
} from '@mantine/core'
import { startAuthentication, browserSupportsWebAuthn } from '@simplewebauthn/browser'
import { useAuth } from '../../hooks/useAuth'
import { useAppName } from '../../hooks/useAppName'
import { useAuthStore } from '../../store/authStore'

type AuthMethod = 'magic-link' | null

export function GarminLoginPage() {
  const { t } = useTranslation()
  const [searchParams] = useSearchParams()
  const { isAuthenticated } = useAuth()
  const appName = useAppName()
  const { accessToken, setAccessToken, setUser } = useAuthStore()

  const [activeStep, setActiveStep] = useState(0)
  const [selectedMethod, setSelectedMethod] = useState<AuthMethod>(null)
  const [isLoading, setIsLoading] = useState(false)
  const [isRedirecting, setIsRedirecting] = useState(false)
  const [completedMessage, setCompletedMessage] = useState<{
    title: string
    message: string
    email?: string
  } | null>(null)
  const [passkeySupported] = useState(() => browserSupportsWebAuthn())

  // OAuth params from URL
  const clientId = searchParams.get('client_id')
  const redirectUri = searchParams.get('redirect_uri')
  const state = searchParams.get('state')

  const magicLinkForm = useForm({
    initialValues: { email: '' },
    validate: {
      email: (v) =>
        !v || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v) ? t('auth.validation.email') : null,
    },
  })

  const completeOAuthFlow = useCallback(
    async (token: string) => {
      if (!clientId || !redirectUri) {
        notifications.show({
          message: t('garmin.errors.invalidRequest'),
          color: 'red',
        })
        return
      }

      setIsRedirecting(true)

      try {
        const response = await fetch('/api/garmin/oauth/complete', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            accessToken: token,
            redirectUri,
            state,
          }),
        })

        if (response.ok) {
          const data = await response.json()
          // Redirect to Garmin app with auth code
          window.location.href = data.redirectUrl
        } else {
          setIsRedirecting(false)
          notifications.show({
            message: t('garmin.errors.callbackFailed'),
            color: 'red',
          })
        }
      } catch {
        setIsRedirecting(false)
        notifications.show({
          message: t('garmin.errors.callbackFailed'),
          color: 'red',
        })
      }
    },
    [clientId, redirectUri, state, t]
  )

  // When user is already authenticated, complete the OAuth flow
  useEffect(() => {
    if (isAuthenticated && accessToken && !isRedirecting) {
      completeOAuthFlow(accessToken)
    }
  }, [isAuthenticated, accessToken, isRedirecting, completeOAuthFlow])

  const handlePasskeyLogin = async () => {
    setIsLoading(true)
    try {
      const optionsResponse = await fetch('/api/auth/passkeys/authentication-options', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({}),
      })

      if (!optionsResponse.ok) {
        notifications.show({ message: t('auth.errors.passkeyFailed'), color: 'red' })
        return
      }

      const options = await optionsResponse.json()
      const assertion = await startAuthentication({ optionsJSON: options })

      const authResponse = await fetch('/api/auth/passkeys/authenticate', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify(assertion),
      })

      if (authResponse.ok) {
        const data = await authResponse.json()
        setAccessToken(data.accessToken)
        setUser(data.user)
        // OAuth flow will be completed by useEffect
      } else {
        notifications.show({ message: t('auth.errors.passkeyFailed'), color: 'red' })
      }
    } catch (error) {
      if (error instanceof Error && error.name === 'NotAllowedError') {
        return // User cancelled
      }
      notifications.show({ message: t('auth.errors.passkeyFailed'), color: 'red' })
    } finally {
      setIsLoading(false)
    }
  }

  const handleMagicLinkRequest = async (values: { email: string }) => {
    setIsLoading(true)
    try {
      const response = await fetch('/api/auth/magic-link', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(values),
      })

      if (response.ok) {
        setCompletedMessage({
          title: t('auth.magicLink.sent.title'),
          message: t('garmin.magicLink.sent.message'),
          email: values.email,
        })
        setActiveStep(2)
      } else {
        notifications.show({ message: t('auth.errors.magicLinkFailed'), color: 'red' })
      }
    } catch {
      notifications.show({ message: t('auth.errors.magicLinkFailed'), color: 'red' })
    } finally {
      setIsLoading(false)
    }
  }

  const selectMethod = (method: AuthMethod) => {
    setSelectedMethod(method)
    setActiveStep(1)
  }

  // Show loading while redirecting
  if (isRedirecting) {
    return (
      <Center mih="70vh">
        <Paper shadow="lg" radius="lg" p="xl" w="100%" maw={420}>
          <Stack align="center" gap="md">
            <ThemeIcon size={64} radius="xl" color="green">
              <IconCheck size={32} />
            </ThemeIcon>
            <Title order={2} ta="center">
              {t('garmin.redirecting.title')}
            </Title>
            <Text c="dimmed" ta="center">
              {t('garmin.redirecting.message')}
            </Text>
          </Stack>
        </Paper>
      </Center>
    )
  }

  // Validate OAuth params
  if (!clientId || !redirectUri) {
    return (
      <Center mih="70vh">
        <Paper shadow="lg" radius="lg" p="xl" w="100%" maw={420}>
          <Alert color="red" title={t('garmin.errors.title')}>
            {t('garmin.errors.invalidRequest')}
          </Alert>
        </Paper>
      </Center>
    )
  }

  const renderMethodSelection = () => (
    <Stack>
      <Stack gap="xs" ta="center">
        <Group justify="center">
          <ThemeIcon size={48} radius="xl" color="blue">
            <IconDeviceWatch size={28} />
          </ThemeIcon>
        </Group>
        <Title order={2}>{t('garmin.title')}</Title>
        <Text c="dimmed">{t('garmin.subtitle')}</Text>
      </Stack>

      <Stack gap="sm">
        {passkeySupported && (
          <Button
            variant="filled"
            size="lg"
            leftSection={<IconFingerprint size={24} />}
            onClick={handlePasskeyLogin}
            loading={isLoading}
          >
            {t('auth.login.methods.passkey')}
          </Button>
        )}
        <Button
          variant="light"
          size="lg"
          leftSection={<IconMail size={24} />}
          onClick={() => selectMethod('magic-link')}
        >
          {t('auth.login.methods.magicLink')}
        </Button>
      </Stack>

      <Text size="xs" c="dimmed" ta="center">
        {t('garmin.notice', { appName })}
      </Text>
    </Stack>
  )

  const renderForm = () => {
    if (selectedMethod === 'magic-link') {
      return (
        <form onSubmit={magicLinkForm.onSubmit(handleMagicLinkRequest)}>
          <Stack>
            <Text size="sm" c="dimmed">
              {t('auth.magicLink.description')}
            </Text>
            <TextInput
              label={t('auth.form.email')}
              placeholder="email@example.com"
              autoComplete="email"
              {...magicLinkForm.getInputProps('email')}
            />
            <Button
              type="submit"
              fullWidth
              loading={isLoading}
              leftSection={<IconMail size={20} />}
            >
              {t('auth.magicLink.send')}
            </Button>
          </Stack>
        </form>
      )
    }
    return null
  }

  const renderCompleted = () => (
    <Stack ta="center">
      <IconMail size={48} style={{ margin: '0 auto' }} color="var(--mantine-color-green-6)" />
      <Title order={2}>{completedMessage?.title}</Title>
      <Alert color="blue" variant="light">
        <Text size="sm">{completedMessage?.message}</Text>
      </Alert>
      <Text size="sm" c="dimmed">
        {t('garmin.magicLink.afterVerify')}
      </Text>
    </Stack>
  )

  return (
    <Center mih="70vh">
      <Paper shadow="lg" radius="lg" p="xl" w="100%" maw={420}>
        <Stepper active={activeStep} allowNextStepsSelect={false}>
          <Stepper.Step label={t('auth.steps.method')}>{renderMethodSelection()}</Stepper.Step>
          <Stepper.Step label={t('auth.steps.credentials')}>
            <Stack>
              <Group>
                <Button
                  variant="subtle"
                  size="compact-sm"
                  onClick={() => {
                    setActiveStep(0)
                    setSelectedMethod(null)
                  }}
                >
                  {t('common.back')}
                </Button>
              </Group>
              <Title order={2} ta="center">
                {t('auth.magicLink.title')}
              </Title>
              {renderForm()}
            </Stack>
          </Stepper.Step>
          <Stepper.Completed>{renderCompleted()}</Stepper.Completed>
        </Stepper>
      </Paper>
    </Center>
  )
}
