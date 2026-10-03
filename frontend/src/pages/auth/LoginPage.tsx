import { useTranslation } from 'react-i18next'
import { Center, Paper } from '@mantine/core'
import { LoginForm } from '@/components/auth/LoginForm'
import { useAppName } from '../../hooks/useAppName'

/**
 * The sign-in page. The form itself — password, passkey, e-mailed code, account creation and the
 * `?next=` redirect — is `LoginForm`, shared with the visitor home hero.
 */
export function LoginPage() {
  const { t } = useTranslation()
  const appName = useAppName()

  return (
    <Center mih="70vh">
      <Paper shadow="lg" radius="lg" p="xl" w="100%" maw={420}>
        <LoginForm
          title={t('welcome', { appName })}
          subtitle={t('auth.login.subtitle')}
          titleOrder={1}
          afterSignIn="redirect"
        />
      </Paper>
    </Center>
  )
}
