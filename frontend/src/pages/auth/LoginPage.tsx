import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useTranslation, Trans } from 'react-i18next'
import { useForm } from '@mantine/form'
import { notifications } from '@mantine/notifications'
import { IconFingerprint, IconMail, IconUserPlus, IconArrowLeft } from '@tabler/icons-react'
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
} from '@mantine/core'
import { startAuthentication, browserSupportsWebAuthn } from '@simplewebauthn/browser'
import { useAuth } from '../../hooks/useAuth'
import { useAuthStore } from '../../store/authStore'
import { paths } from '@/config/paths'

type AuthMethod = 'magic-link' | 'register' | null

export function LoginPage() {
  const { t } = useTranslation()
  const navigate = useNavigate()
  const { isAuthenticated } = useAuth()
  const { setAccessToken, setUser } = useAuthStore()

  const [activeStep, setActiveStep] = useState(0)
  const [selectedMethod, setSelectedMethod] = useState<AuthMethod>(null)
  const [isLoading, setIsLoading] = useState(false)
  const [completedMessage, setCompletedMessage] = useState<{
    title: string
    message: string
    email?: string
  } | null>(null)
  const [passkeySupported] = useState(() => browserSupportsWebAuthn())

  const magicLinkForm = useForm({
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
          message: t('auth.magicLink.sent.message'),
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

  const handleRegister = async (values: { email: string; displayName: string }) => {
    setIsLoading(true)
    try {
      const response = await fetch('/api/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(values),
      })

      if (response.ok) {
        setCompletedMessage({
          title: t('auth.register.success.title'),
          message: t('auth.register.success.checkEmail'),
          email: values.email,
        })
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
    setActiveStep(0)
    setSelectedMethod(null)
  }

  const renderMethodSelection = () => (
    <Stack>
      <Stack gap="xs" ta="center">
        <Title order={1}>{t('welcome')}</Title>
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
          onClick={() => selectMethod('magic-link')}
        >
          {t('auth.login.methods.magicLink')}
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
      case 'magic-link':
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

  const renderCompleted = () => (
    <Stack ta="center">
      <IconMail size={48} style={{ margin: '0 auto' }} color="var(--mantine-color-green-6)" />
      <Title order={2}>{completedMessage?.title}</Title>
      {completedMessage?.email && (
        <Text c="dimmed">
          <Trans
            i18nKey="auth.register.success.message"
            values={{ email: completedMessage.email }}
            components={{ strong: <Text span fw={500} /> }}
          />
        </Text>
      )}
      <Alert color="blue" variant="light">
        <Text size="sm">{completedMessage?.message}</Text>
      </Alert>
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
                {selectedMethod === 'register'
                  ? t('auth.register.title')
                  : t('auth.magicLink.title')}
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
