import { useEffect, useRef, useState } from 'react'
import { useSearchParams, useNavigate } from 'react-router-dom'
import { PrefetchLink } from '@/components/common/PrefetchLink'
import { useTranslation } from 'react-i18next'
import { IconCheck, IconX, IconMail, IconFingerprint } from '@tabler/icons-react'
import { Center, Paper, Stack, Title, Text, Button, Loader } from '@mantine/core'
import { paths } from '@/config/paths'
import { useAuthStore } from '@/store/authStore'
import { isPasskeySupported, registerNewPasskey } from '@/lib/passkeys'

type VerificationState = 'loading' | 'success' | 'error' | 'expired'

export function VerifyEmailPage() {
  const { t } = useTranslation()
  const navigate = useNavigate()
  const [searchParams] = useSearchParams()
  const token = searchParams.get('token')
  const { setAccessToken, setUser } = useAuthStore()

  const [state, setState] = useState<VerificationState>(() => (token ? 'loading' : 'error'))
  const [isResending, setIsResending] = useState(false)
  const [email, setEmail] = useState<string | null>(null)
  const [showPasskeyPrompt, setShowPasskeyPrompt] = useState(false)
  const [isRegisteringPasskey, setIsRegisteringPasskey] = useState(false)
  const hasVerified = useRef(false)

  useEffect(() => {
    if (!token || hasVerified.current) {
      return
    }
    hasVerified.current = true

    const verifyEmail = async () => {
      try {
        const response = await fetch('/api/auth/verify-email', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          credentials: 'include',
          body: JSON.stringify({ token }),
        })

        if (response.ok) {
          const data = await response.json()
          setAccessToken(data.accessToken)
          setUser(data.user)
          setState('success')
          if (isPasskeySupported()) {
            setShowPasskeyPrompt(true)
          }
        } else {
          const error = await response.json()
          if (error.code === 'TOKEN_EXPIRED') {
            setEmail(error.errorDetails?.email || null)
            setState('expired')
          } else {
            setState('error')
          }
        }
      } catch {
        setState('error')
      }
    }

    verifyEmail()
  }, [token, setAccessToken, setUser])

  const handleResendVerification = async () => {
    if (!email) return

    setIsResending(true)
    try {
      const response = await fetch('/api/auth/resend-verification', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email }),
      })

      if (response.ok) {
        setState('success')
        setShowPasskeyPrompt(false)
      }
    } finally {
      setIsResending(false)
    }
  }

  const handleRegisterPasskey = async () => {
    setIsRegisteringPasskey(true)
    try {
      await registerNewPasskey(undefined)
      navigate(paths.home())
    } catch {
      // If passkey registration fails, just go home
      navigate(paths.home())
    }
  }

  const handleSkipPasskey = () => {
    navigate(paths.home())
  }

  const renderContent = () => {
    switch (state) {
      case 'loading':
        return (
          <Stack align="center" gap="md">
            <Loader size="lg" />
            <Text>{t('auth.verifyEmail.verifying')}</Text>
          </Stack>
        )

      case 'success':
        if (showPasskeyPrompt) {
          return (
            <Stack ta="center">
              <IconFingerprint
                size={48}
                style={{ margin: '0 auto' }}
                color="var(--mantine-color-blue-6)"
              />
              <Title order={2}>{t('auth.verifyEmail.passkey.title')}</Title>
              <Text c="dimmed">{t('auth.verifyEmail.passkey.message')}</Text>
              <Button
                onClick={handleRegisterPasskey}
                loading={isRegisteringPasskey}
                fullWidth
                leftSection={<IconFingerprint size={18} />}
              >
                {t('auth.verifyEmail.passkey.register')}
              </Button>
              <Button onClick={handleSkipPasskey} variant="subtle" fullWidth>
                {t('auth.verifyEmail.passkey.skip')}
              </Button>
            </Stack>
          )
        }
        return (
          <Stack ta="center">
            <IconCheck
              size={48}
              style={{ margin: '0 auto' }}
              color="var(--mantine-color-green-6)"
            />
            <Title order={2}>{t('auth.verifyEmail.success.title')}</Title>
            <Text c="dimmed">{t('auth.verifyEmail.success.message')}</Text>
            <Button component={PrefetchLink} to={paths.home()} fullWidth>
              {t('auth.verifyEmail.success.continue')}
            </Button>
          </Stack>
        )

      case 'expired':
        return (
          <Stack ta="center">
            <IconMail
              size={48}
              style={{ margin: '0 auto' }}
              color="var(--mantine-color-yellow-6)"
            />
            <Title order={2}>{t('auth.verifyEmail.expired.title')}</Title>
            <Text c="dimmed">{t('auth.verifyEmail.expired.message')}</Text>
            {email && (
              <Button onClick={handleResendVerification} loading={isResending} fullWidth>
                {t('auth.verifyEmail.expired.resend')}
              </Button>
            )}
            <Button component={PrefetchLink} to={paths.login()} variant="light" fullWidth>
              {t('auth.verifyEmail.success.login')}
            </Button>
          </Stack>
        )

      case 'error':
        return (
          <Stack ta="center">
            <IconX size={48} style={{ margin: '0 auto' }} color="var(--mantine-color-red-6)" />
            <Title order={2}>{t('auth.verifyEmail.error.title')}</Title>
            <Text c="dimmed">{t('auth.verifyEmail.error.message')}</Text>
            <Button component={PrefetchLink} to={paths.login()} fullWidth>
              {t('auth.verifyEmail.success.login')}
            </Button>
          </Stack>
        )
    }
  }

  return (
    <Center mih="70vh">
      <Paper shadow="lg" radius="lg" p="xl" w="100%" maw={420}>
        {renderContent()}
      </Paper>
    </Center>
  )
}
