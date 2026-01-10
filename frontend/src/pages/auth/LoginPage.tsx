import { useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useTranslation, Trans } from 'react-i18next'
import { IconLogin } from '@tabler/icons-react'
import { Center, Paper, Stack, Title, Text, Button, Anchor } from '@mantine/core'
import { useAuth } from '../../hooks/useAuth'
import { paths } from '@/config/paths'

export function LoginPage() {
  const { t } = useTranslation()
  const navigate = useNavigate()
  const { isAuthenticated, login } = useAuth()

  useEffect(() => {
    if (isAuthenticated) {
      navigate(paths.home())
    }
  }, [isAuthenticated, navigate])

  return (
    <Center mih="70vh">
      <Paper shadow="lg" radius="lg" p="xl" w="100%" maw={400}>
        <Stack gap="lg">
          <Stack gap="xs" ta="center">
            <Title order={1}>{t('welcome')}</Title>
            <Text c="dimmed">{t('auth.login.subtitle')}</Text>
          </Stack>

          <Button onClick={() => login()} size="lg" fullWidth leftSection={<IconLogin size={20} />}>
            {t('auth.login.button')}
          </Button>

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
      </Paper>
    </Center>
  )
}
