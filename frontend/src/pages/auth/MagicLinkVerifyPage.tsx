import { useEffect, useRef, useState } from 'react'
import { useNavigate, useSearchParams, Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { IconCheck, IconX } from '@tabler/icons-react'
import { Center, Paper, Stack, Title, Text, Button, Loader } from '@mantine/core'
import { useAuthStore } from '../../store/authStore'
import { paths } from '@/config/paths'

type VerificationState = 'loading' | 'success' | 'error'

export function MagicLinkVerifyPage() {
  const { t } = useTranslation()
  const navigate = useNavigate()
  const [searchParams] = useSearchParams()
  const token = searchParams.get('token')
  const { setAccessToken, setUser } = useAuthStore()

  const [state, setState] = useState<VerificationState>(() => (token ? 'loading' : 'error'))
  const hasVerified = useRef(false)

  useEffect(() => {
    if (!token || hasVerified.current) {
      return
    }
    hasVerified.current = true

    const verifyMagicLink = async () => {
      try {
        const response = await fetch('/api/auth/magic-link/verify', {
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
          // Redirect to home after a brief delay
          setTimeout(() => {
            navigate(paths.home())
          }, 2000)
        } else {
          setState('error')
        }
      } catch {
        setState('error')
      }
    }

    verifyMagicLink()
  }, [token, navigate, setAccessToken, setUser])

  const renderContent = () => {
    switch (state) {
      case 'loading':
        return (
          <Stack align="center" gap="md">
            <Loader size="lg" />
            <Text>{t('auth.magicLink.verify.verifying')}</Text>
          </Stack>
        )

      case 'success':
        return (
          <Stack ta="center">
            <IconCheck
              size={48}
              style={{ margin: '0 auto' }}
              color="var(--mantine-color-green-6)"
            />
            <Title order={2}>{t('auth.magicLink.verify.success.title')}</Title>
            <Text c="dimmed">{t('auth.magicLink.verify.success.message')}</Text>
            <Loader size="sm" />
          </Stack>
        )

      case 'error':
        return (
          <Stack ta="center">
            <IconX size={48} style={{ margin: '0 auto' }} color="var(--mantine-color-red-6)" />
            <Title order={2}>{t('auth.magicLink.verify.error.title')}</Title>
            <Text c="dimmed">{t('auth.magicLink.verify.error.message')}</Text>
            <Button component={Link} to={paths.login()} fullWidth>
              {t('auth.magicLink.verify.error.tryAgain')}
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
