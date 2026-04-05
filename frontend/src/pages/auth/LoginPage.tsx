import { useEffect, useState } from 'react'
import { Link, useNavigate, useLocation } from 'react-router-dom'
import { useTranslation, Trans } from 'react-i18next'
import { useForm } from '@mantine/form'
import { notifications } from '@mantine/notifications'
import { IconFingerprint, IconUserPlus, IconLock } from '@tabler/icons-react'
import {
  Center,
  Paper,
  Stack,
  Title,
  Text,
  Button,
  Anchor,
  TextInput,
  PasswordInput,
  Divider,
} from '@mantine/core'
import { startAuthentication, browserSupportsWebAuthn } from '@simplewebauthn/browser'
import { useAuth } from '../../hooks/useAuth'
import { useAppName } from '../../hooks/useAppName'
import { useAuthStore } from '../../store/authStore'
import { paths } from '@/config/paths'
import {
  loginWithPassword,
  register as registerUser,
} from '@/api/endpoints/authentication/authentication'

type Mode = 'login' | 'register'

export function LoginPage() {
  const { t } = useTranslation()
  const navigate = useNavigate()
  const location = useLocation()
  const { isAuthenticated } = useAuth()
  const appName = useAppName()
  const { setAccessToken, setUser } = useAuthStore()

  const fromLocation = location.state?.from
  const redirectTo = fromLocation
    ? `${fromLocation.pathname}${fromLocation.search || ''}`
    : paths.home()

  const [mode, setMode] = useState<Mode>('login')
  const [isLoading, setIsLoading] = useState(false)
  const [passkeySupported] = useState(() => browserSupportsWebAuthn())

  const loginForm = useForm({
    initialValues: { email: '', password: '' },
    validate: {
      email: (v) =>
        !v || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v) ? t('auth.validation.email') : null,
      password: (v) => (!v ? t('auth.validation.required') : null),
    },
  })

  const registerForm = useForm({
    initialValues: { email: '', displayName: '', password: '', confirmPassword: '' },
    validate: {
      email: (v) =>
        !v || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v) ? t('auth.validation.email') : null,
      displayName: (v) =>
        !v || v.length < 2
          ? t('auth.validation.displayNameMin')
          : v.length > 100
            ? t('auth.validation.displayNameMax')
            : null,
      password: (v) => (v.length < 8 ? t('auth.validation.passwordMin') : null),
      confirmPassword: (v, values) =>
        v !== values.password ? t('auth.validation.passwordMismatch') : null,
    },
  })

  useEffect(() => {
    if (isAuthenticated) {
      navigate(redirectTo)
    }
  }, [isAuthenticated, navigate, redirectTo])

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
        navigate(redirectTo)
      } else {
        notifications.show({ message: t('auth.errors.passkeyFailed'), color: 'red' })
      }
    } catch (error) {
      if (error instanceof Error && error.name === 'NotAllowedError') {
        return
      }
      notifications.show({ message: t('auth.errors.passkeyFailed'), color: 'red' })
    } finally {
      setIsLoading(false)
    }
  }

  const handleLogin = async (values: { email: string; password: string }) => {
    setIsLoading(true)
    try {
      const data = await loginWithPassword({ email: values.email, password: values.password })
      if (data.accessToken) setAccessToken(data.accessToken)
      if (data.user) setUser(data.user)
      navigate(redirectTo)
    } catch (error: unknown) {
      console.error('Login failed', error)
      const code = (error as { response?: { data?: { code?: string } } })?.response?.data?.code
      notifications.show({
        message: t(`auth.errors.${code}` as Parameters<typeof t>[0], {
          defaultValue: t('auth.errors.loginFailed'),
        }),
        color: 'red',
      })
    } finally {
      setIsLoading(false)
    }
  }

  const handleRegister = async (values: {
    email: string
    displayName: string
    password: string
  }) => {
    setIsLoading(true)
    try {
      await registerUser({
        email: values.email,
        displayName: values.displayName,
        password: values.password,
      })
      notifications.show({
        message: t('auth.register.success.checkEmail'),
        color: 'green',
      })
      setMode('login')
      loginForm.setFieldValue('email', values.email)
    } catch (error: unknown) {
      console.error('Registration failed', error)
      const code = (error as { response?: { data?: { code?: string } } })?.response?.data?.code
      notifications.show({
        message: t(`auth.errors.${code}` as Parameters<typeof t>[0], {
          defaultValue: t('auth.errors.registrationFailed'),
        }),
        color: 'red',
      })
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <Center mih="70vh">
      <Paper shadow="lg" radius="lg" p="xl" w="100%" maw={420}>
        {mode === 'login' ? (
          <Stack>
            <Stack gap="xs" ta="center">
              <Title order={1}>{t('welcome', { appName })}</Title>
              <Text c="dimmed">{t('auth.login.subtitle')}</Text>
            </Stack>

            <form onSubmit={loginForm.onSubmit(handleLogin)}>
              <Stack>
                <TextInput
                  label={t('auth.form.email')}
                  placeholder="email@example.com"
                  autoComplete="email"
                  {...loginForm.getInputProps('email')}
                />
                <PasswordInput
                  label={t('auth.form.password')}
                  placeholder={t('auth.form.passwordPlaceholder')}
                  autoComplete="current-password"
                  leftSection={<IconLock size={16} />}
                  {...loginForm.getInputProps('password')}
                />
                <Anchor component={Link} to={paths.forgotPassword()} size="sm" ta="right">
                  {t('auth.login.forgotPassword')}
                </Anchor>
                <Button type="submit" fullWidth loading={isLoading}>
                  {t('auth.login.button')}
                </Button>
              </Stack>
            </form>

            {passkeySupported && (
              <>
                <Divider label={t('common.or')} labelPosition="center" />
                <Button
                  variant="default"
                  fullWidth
                  leftSection={<IconFingerprint size={20} />}
                  onClick={handlePasskeyLogin}
                  loading={isLoading}
                >
                  {t('auth.login.methods.passkey')}
                </Button>
              </>
            )}

            <Text size="sm" ta="center">
              {t('auth.login.noAccount')}{' '}
              <Anchor component="button" onClick={() => setMode('register')}>
                {t('auth.login.methods.register')}
              </Anchor>
            </Text>

            <Text size="xs" c="dimmed" ta="center">
              <Trans
                i18nKey="auth.login.termsText"
                components={{
                  termsLink: <Anchor href={paths.terms()} />,
                  privacyLink: <Anchor href={paths.privacy()} />,
                }}
              />
            </Text>
          </Stack>
        ) : (
          <Stack>
            <Stack gap="xs" ta="center">
              <Title order={2}>{t('auth.register.title')}</Title>
              <Text c="dimmed">{t('auth.register.subtitle')}</Text>
            </Stack>

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
                <PasswordInput
                  label={t('auth.form.password')}
                  placeholder={t('auth.form.passwordPlaceholder')}
                  autoComplete="new-password"
                  leftSection={<IconLock size={16} />}
                  {...registerForm.getInputProps('password')}
                />
                <PasswordInput
                  label={t('auth.form.confirmPassword')}
                  placeholder={t('auth.form.confirmPasswordPlaceholder')}
                  autoComplete="new-password"
                  leftSection={<IconLock size={16} />}
                  {...registerForm.getInputProps('confirmPassword')}
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

            <Text size="sm" ta="center">
              {t('auth.register.haveAccount')}{' '}
              <Anchor component="button" onClick={() => setMode('login')}>
                {t('auth.login.title')}
              </Anchor>
            </Text>
          </Stack>
        )}
      </Paper>
    </Center>
  )
}
