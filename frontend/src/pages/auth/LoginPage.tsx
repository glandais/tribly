import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useTranslation, Trans } from 'react-i18next'
import { useForm } from '@mantine/form'
import { notifications } from '@mantine/notifications'
import {
  IconFingerprint,
  IconMail,
  IconUserPlus,
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
  Anchor,
  TextInput,
  Stepper,
  Alert,
  Group,
  PinInput,
} from '@mantine/core'
import { startAuthentication, browserSupportsWebAuthn } from '@simplewebauthn/browser'
import { useAuth } from '../../hooks/useAuth'
import { useAppName } from '../../hooks/useAppName'
import { useAuthStore } from '../../store/authStore'
import { paths } from '@/config/paths'

type AuthMethod = 'otp' | 'register' | null

export function LoginPage() {
  const { t } = useTranslation()
  const navigate = useNavigate()
  const { isAuthenticated } = useAuth()
  const appName = useAppName()
  const { setAccessToken, setUser } = useAuthStore()

  const [activeStep, setActiveStep] = useState(0)
  const [selectedMethod, setSelectedMethod] = useState<AuthMethod>(null)
  const [isLoading, setIsLoading] = useState(false)
  const [otpEmail, setOtpEmail] = useState<string | null>(null)
  const [otpCode, setOtpCode] = useState('')
  const [canResend, setCanResend] = useState(false)
  const [resendCountdown, setResendCountdown] = useState(0)
  const [passkeySupported] = useState(() => browserSupportsWebAuthn())

  const otpForm = useForm({
    initialValues: { email: '' },
    validate: {
      email: (v) =>
        !v || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v) ? t('auth.validation.email') : null,
    },
  })

  const registerForm = useForm({
    initialValues: { email: '', displayName: '' },
    validate: {
      email: (v) =>
        !v || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v) ? t('auth.validation.email') : null,
      displayName: (v) =>
        !v || v.length < 2
          ? t('auth.validation.displayNameMin')
          : v.length > 100
            ? t('auth.validation.displayNameMax')
            : null,
    },
  })

  useEffect(() => {
    if (isAuthenticated) {
      navigate(paths.home())
    }
  }, [isAuthenticated, navigate])

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
        navigate(paths.home())
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
        setResendCountdown(30) // 30 seconds before allowing resend
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
        navigate(paths.home())
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

  const handleRegister = async (values: { email: string; displayName: string }) => {
    setIsLoading(true)
    try {
      const response = await fetch('/api/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(values),
      })

      if (response.ok) {
        setOtpEmail(values.email)
        setActiveStep(2)
      } else {
        const error = await response.json()
        notifications.show({
          message: t(`auth.errors.${error.code || 'registrationFailed'}`),
          color: 'red',
        })
      }
    } catch {
      notifications.show({ message: t('auth.errors.registrationFailed'), color: 'red' })
    } finally {
      setIsLoading(false)
    }
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

  const renderMethodSelection = () => (
    <Stack>
      <Stack gap="xs" ta="center">
        <Title order={1}>{t('welcome', { appName })}</Title>
        <Text c="dimmed">{t('auth.login.subtitle')}</Text>
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
        <Button
          variant="subtle"
          size="lg"
          leftSection={<IconUserPlus size={24} />}
          onClick={() => selectMethod('register')}
        >
          {t('auth.login.methods.register')}
        </Button>
      </Stack>

      <Text size="sm" c="dimmed" ta="center">
        <Trans
          i18nKey="auth.login.termsText"
          components={{
            termsLink: <Anchor href="/terms" />,
            privacyLink: <Anchor href="/privacy" />,
          }}
        />
      </Text>
    </Stack>
  )

  const renderForm = () => {
    switch (selectedMethod) {
      case 'otp':
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

      case 'register':
        return (
          <form onSubmit={registerForm.onSubmit(handleRegister)}>
            <Stack>
              <TextInput
                label={t('auth.form.email')}
                placeholder="email@example.com"
                autoComplete="email"
                {...registerForm.getInputProps('email')}
              />
              <TextInput
                label={t('auth.form.displayName')}
                placeholder={t('auth.form.displayNamePlaceholder')}
                autoComplete="name"
                {...registerForm.getInputProps('displayName')}
              />
              <Button
                type="submit"
                fullWidth
                loading={isLoading}
                leftSection={<IconUserPlus size={20} />}
              >
                {t('auth.register.button')}
              </Button>
            </Stack>
          </form>
        )

      default:
        return null
    }
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
    </Stack>
  )

  const renderRegistrationSuccess = () => (
    <Stack ta="center">
      <IconMail size={48} style={{ margin: '0 auto' }} color="var(--mantine-color-green-6)" />
      <Title order={2}>{t('auth.register.success.title')}</Title>
      {otpEmail && (
        <Text c="dimmed">
          <Trans
            i18nKey="auth.register.success.message"
            values={{ email: otpEmail }}
            components={{ strong: <Text span fw={500} /> }}
          />
        </Text>
      )}
      <Alert color="blue" variant="light">
        <Text size="sm">{t('auth.register.success.checkEmail')}</Text>
      </Alert>
    </Stack>
  )

  const renderCompleted = () => {
    if (selectedMethod === 'register') {
      return renderRegistrationSuccess()
    }
    return renderOtpVerification()
  }

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
                {selectedMethod === 'register' ? t('auth.register.title') : t('auth.otp.title')}
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
