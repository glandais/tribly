import { useCallback, useEffect, useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import { useTranslation, Trans } from 'react-i18next'
import { useForm } from '@mantine/form'
import { notifications } from '@mantine/notifications'
import {
  IconFingerprint,
  IconMail,
  IconDeviceWatch,
  IconCheck,
  IconArrowLeft,
  IconRefresh,
} from '@tabler/icons-react'
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
  PinInput,
} from '@mantine/core'
import { startAuthentication, browserSupportsWebAuthn } from '@simplewebauthn/browser'
import { useAuth } from '../../hooks/useAuth'
import { useAppName } from '../../hooks/useAppName'
import { useAuthStore } from '../../store/authStore'

type AuthMethod = 'otp' | null

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
  const [otpEmail, setOtpEmail] = useState<string | null>(null)
  const [otpCode, setOtpCode] = useState('')
  const [canResend, setCanResend] = useState(false)
  const [resendCountdown, setResendCountdown] = useState(0)
  const [passkeySupported] = useState(() => browserSupportsWebAuthn())

  // OAuth params from URL
  const clientId = searchParams.get('client_id')
  const redirectUri = searchParams.get('redirect_uri')
  const state = searchParams.get('state')

  const otpForm = useForm({
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

  // Countdown timer for resend button
  useEffect(() => {
    if (resendCountdown > 0) {
      const timer = setTimeout(() => setResendCountdown(resendCountdown - 1), 1000)
      return () => clearTimeout(timer)
    } else if (otpEmail) {
      setCanResend(true)
    }
  }, [resendCountdown, otpEmail])

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

  const handleOtpRequest = async (values: { email: string }) => {
    setIsLoading(true)
    try {
      const response = await fetch('/api/auth/otp', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(values),
      })

      if (response.ok) {
        setOtpEmail(values.email)
        setOtpCode('')
        setCanResend(false)
        setResendCountdown(30)
        setActiveStep(2)
      } else {
        notifications.show({ message: t('auth.errors.otpFailed'), color: 'red' })
      }
    } catch {
      notifications.show({ message: t('auth.errors.otpFailed'), color: 'red' })
    } finally {
      setIsLoading(false)
    }
  }

  const handleOtpVerify = async (code: string) => {
    if (code.length !== 6 || !otpEmail) return

    setIsLoading(true)
    try {
      const response = await fetch('/api/auth/otp/verify', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ email: otpEmail, code }),
      })

      if (response.ok) {
        const data = await response.json()
        setAccessToken(data.accessToken)
        setUser(data.user)
        // OAuth flow will be completed by useEffect
      } else {
        notifications.show({ message: t('auth.errors.otpInvalid'), color: 'red' })
        setOtpCode('')
      }
    } catch {
      notifications.show({ message: t('auth.errors.otpVerifyFailed'), color: 'red' })
      setOtpCode('')
    } finally {
      setIsLoading(false)
    }
  }

  const handleResendOtp = async () => {
    if (!otpEmail || !canResend) return
    await handleOtpRequest({ email: otpEmail })
  }

  const selectMethod = (method: AuthMethod) => {
    setSelectedMethod(method)
    setActiveStep(1)
  }

  const goBack = () => {
    if (activeStep === 2) {
      setActiveStep(1)
      setOtpEmail(null)
      setOtpCode('')
    } else {
      setActiveStep(0)
      setSelectedMethod(null)
    }
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
          onClick={() => selectMethod('otp')}
        >
          {t('auth.login.methods.otp')}
        </Button>
      </Stack>

      <Text size="xs" c="dimmed" ta="center">
        {t('garmin.notice', { appName })}
      </Text>
    </Stack>
  )

  const renderForm = () => {
    if (selectedMethod === 'otp') {
      return (
        <form onSubmit={otpForm.onSubmit(handleOtpRequest)}>
          <Stack>
            <Text size="sm" c="dimmed">
              {t('auth.otp.description')}
            </Text>
            <TextInput
              label={t('auth.form.email')}
              placeholder="email@example.com"
              autoComplete="email"
              {...otpForm.getInputProps('email')}
            />
            <Button
              type="submit"
              fullWidth
              loading={isLoading}
              leftSection={<IconMail size={20} />}
            >
              {t('auth.otp.send')}
            </Button>
          </Stack>
        </form>
      )
    }
    return null
  }

  const renderOtpVerification = () => (
    <Stack ta="center">
      <IconMail size={48} style={{ margin: '0 auto' }} color="var(--mantine-color-blue-6)" />
      <Title order={2}>{t('auth.otp.verify.title')}</Title>
      {otpEmail && (
        <Text c="dimmed">
          <Trans
            i18nKey="auth.otp.verify.sentTo"
            values={{ email: otpEmail }}
            components={{ strong: <Text span fw={500} /> }}
          />
        </Text>
      )}
      <Alert color="blue" variant="light">
        <Text size="sm">{t('auth.otp.verify.instruction')}</Text>
      </Alert>
      <PinInput
        length={6}
        type="number"
        value={otpCode}
        onChange={(value) => {
          setOtpCode(value)
          if (value.length === 6) {
            handleOtpVerify(value)
          }
        }}
        disabled={isLoading}
        size="xl"
        style={{ justifyContent: 'center' }}
      />
      <Group justify="center" gap="xs">
        <Button
          variant="subtle"
          size="compact-sm"
          leftSection={<IconArrowLeft size={16} />}
          onClick={goBack}
        >
          {t('common.back')}
        </Button>
        <Button
          variant="subtle"
          size="compact-sm"
          leftSection={<IconRefresh size={16} />}
          onClick={handleResendOtp}
          disabled={!canResend || isLoading}
        >
          {canResend ? t('auth.otp.resend') : t('auth.otp.resendIn', { seconds: resendCountdown })}
        </Button>
      </Group>
      <Text size="sm" c="dimmed">
        {t('garmin.otp.afterVerify')}
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
                  leftSection={<IconArrowLeft size={16} />}
                  onClick={goBack}
                >
                  {t('common.back')}
                </Button>
              </Group>
              <Title order={2} ta="center">
                {t('auth.otp.title')}
              </Title>
              {renderForm()}
            </Stack>
          </Stepper.Step>
          <Stepper.Completed>{renderOtpVerification()}</Stepper.Completed>
        </Stepper>
      </Paper>
    </Center>
  )
}
