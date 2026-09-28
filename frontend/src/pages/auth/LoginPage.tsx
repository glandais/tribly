import { useCallback, useEffect, useRef, useState } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import { PrefetchLink } from '@/components/common/PrefetchLink'
import { useTranslation, Trans } from 'react-i18next'
import { useForm } from '@mantine/form'
import { notifications } from '@mantine/notifications'
import { IconFingerprint, IconUserPlus, IconLock, IconMail } from '@tabler/icons-react'
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
  Checkbox,
} from '@mantine/core'
import { startAuthentication, browserSupportsWebAuthn } from '@simplewebauthn/browser'
import { useAuth } from '../../hooks/useAuth'
import { useAppName } from '../../hooks/useAppName'
import { useAuthStore } from '../../store/authStore'
import { paths } from '@/config/paths'
import { apiErrorCode } from '@/lib/apiError'
import {
  loginWithPassword,
  register as registerUser,
} from '@/api/endpoints/authentication/authentication'
import type { AuthResponse } from '@/api/dto'
import { OtpLogin } from './OtpLogin'
import { safeNextPath } from '@/lib/safeNextPath'

type Mode = 'login' | 'register' | 'otp'

export function LoginPage() {
  const { t } = useTranslation()
  const navigate = useNavigate()
  const location = useLocation()
  const { isAuthenticated } = useAuth()
  const appName = useAppName()
  const { setAccessToken, setUser } = useAuthStore()

  const fromLocation = location.state?.from
  // `?next=` comes from outside the router: the backend sends a visitor without a session there
  // from a download link (a data export), the API client after a session ended mid-page. A browser
  // path — loaded as such, which also keeps it right on a pinned host. Same-origin paths only.
  const next = safeNextPath(new URLSearchParams(location.search).get('next'))
  const redirectTo =
    next ?? (fromLocation ? `${fromLocation.pathname}${fromLocation.search || ''}` : paths.home())
  const leaving = useRef(false)
  const goToRedirect = useCallback(() => {
    if (next) {
      if (!leaving.current) window.location.assign(next)
      leaving.current = true
    } else {
      navigate(redirectTo)
    }
  }, [navigate, next, redirectTo])

  const [mode, setMode] = useState<Mode>('login')
  const [isLoading, setIsLoading] = useState(false)
  const [passkeySupported, setPasskeySupported] = useState(false)

  useEffect(() => {
    setPasskeySupported(browserSupportsWebAuthn())
  }, [])

  const loginForm = useForm({
    initialValues: { email: '', password: '' },
    validate: {
      email: (v) =>
        !v || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v) ? t('auth.validation.email') : null,
      password: (v) => (!v ? t('auth.validation.required') : null),
    },
  })

  const registerForm = useForm({
    initialValues: {
      email: '',
      displayName: '',
      password: '',
      confirmPassword: '',
      acceptTerms: false,
    },
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
      acceptTerms: (v) => (v ? null : t('auth.validation.acceptTerms')),
    },
  })

  useEffect(() => {
    if (isAuthenticated) {
      goToRedirect()
    }
  }, [isAuthenticated, goToRedirect])

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
        goToRedirect()
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

  const completeLogin = (data: AuthResponse) => {
    if (data.accessToken) setAccessToken(data.accessToken)
    if (data.user) setUser(data.user)
    goToRedirect()
  }

  const handleLogin = async (values: { email: string; password: string }) => {
    setIsLoading(true)
    try {
      // One message, the page's own: the mutator's toast would come on top of it.
      completeLogin(
        await loginWithPassword(
          { email: values.email, password: values.password },
          { skipErrorToast: true }
        )
      )
    } catch (error: unknown) {
      console.error('Login failed', error)
      const code = apiErrorCode(error)
      notifications.show({
        message: t(`auth.errors.${code}` as Parameters<typeof t>[0], {
          defaultValue: t(`errors.api.${code}` as Parameters<typeof t>[0], {
            defaultValue: t('auth.errors.loginFailed'),
          }),
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
    acceptTerms: boolean
  }) => {
    setIsLoading(true)
    try {
      await registerUser(
        {
          email: values.email,
          displayName: values.displayName,
          password: values.password,
          acceptTerms: values.acceptTerms,
        },
        { skipErrorToast: true }
      )
      notifications.show({
        message: t('auth.register.success.checkEmail'),
        color: 'green',
      })
      setMode('login')
      loginForm.setFieldValue('email', values.email)
    } catch (error: unknown) {
      console.error('Registration failed', error)
      const code = apiErrorCode(error)
      notifications.show({
        message: t(`auth.errors.${code}` as Parameters<typeof t>[0], {
          defaultValue: t(`errors.api.${code}` as Parameters<typeof t>[0], {
            defaultValue: t('auth.errors.registrationFailed'),
          }),
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
                  name="email"
                  autoComplete="email"
                  {...loginForm.getInputProps('email')}
                />
                <PasswordInput
                  label={t('auth.form.password')}
                  placeholder={t('auth.form.passwordPlaceholder')}
                  name="password"
                  autoComplete="current-password"
                  leftSection={<IconLock size={16} />}
                  {...loginForm.getInputProps('password')}
                />
                <Anchor component={PrefetchLink} to={paths.forgotPassword()} size="sm" ta="right">
                  {t('auth.login.forgotPassword')}
                </Anchor>
                <Button type="submit" fullWidth loading={isLoading}>
                  {t('auth.login.button')}
                </Button>
              </Stack>
            </form>

            <Divider label={t('common.or')} labelPosition="center" />

            {passkeySupported && (
              <Button
                variant="default"
                fullWidth
                leftSection={<IconFingerprint size={20} />}
                onClick={handlePasskeyLogin}
                loading={isLoading}
              >
                {t('auth.login.methods.passkey')}
              </Button>
            )}

            <Button
              variant="default"
              fullWidth
              leftSection={<IconMail size={20} />}
              onClick={() => setMode('otp')}
              disabled={isLoading}
            >
              {t('auth.login.methods.otp')}
            </Button>

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
        ) : mode === 'otp' ? (
          <OtpLogin
            initialEmail={loginForm.values.email}
            onSuccess={completeLogin}
            onBack={() => setMode('login')}
          />
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
                  name="email"
                  autoComplete="email"
                  {...registerForm.getInputProps('email')}
                />
                <TextInput
                  label={t('auth.form.displayName')}
                  placeholder={t('auth.form.displayNamePlaceholder')}
                  name="name"
                  autoComplete="name"
                  {...registerForm.getInputProps('displayName')}
                />
                <PasswordInput
                  label={t('auth.form.password')}
                  placeholder={t('auth.form.passwordPlaceholder')}
                  name="new-password"
                  autoComplete="new-password"
                  leftSection={<IconLock size={16} />}
                  {...registerForm.getInputProps('password')}
                />
                <PasswordInput
                  label={t('auth.form.confirmPassword')}
                  placeholder={t('auth.form.confirmPasswordPlaceholder')}
                  name="new-password-confirm"
                  autoComplete="new-password"
                  leftSection={<IconLock size={16} />}
                  {...registerForm.getInputProps('confirmPassword')}
                />
                {/* Mandatory: the terms carry the zero-tolerance clause on abusive content. The links
                    open in a new tab so the half-filled form survives reading them. */}
                <Checkbox
                  name="accept-terms"
                  label={
                    <Trans
                      i18nKey="auth.register.acceptTerms"
                      components={{
                        termsLink: <Anchor href={paths.terms()} target="_blank" inherit />,
                        privacyLink: <Anchor href={paths.privacy()} target="_blank" inherit />,
                      }}
                    />
                  }
                  {...registerForm.getInputProps('acceptTerms', { type: 'checkbox' })}
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
