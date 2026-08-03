import { useState } from 'react'
import { Navigate, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useForm } from '@mantine/form'
import { notifications } from '@mantine/notifications'
import { IconMail, IconMailCheck } from '@tabler/icons-react'
import { Center, Paper, Stack, Title, Text, Button, TextInput } from '@mantine/core'
import { paths } from '@/config/paths'
import { useAuth } from '@/hooks/useAuth'
import { requestEmailChange } from '@/api/endpoints/authentication/authentication'

/**
 * Prompts a recovered account (e.g. migrated Strava user with a placeholder email) to provide and
 * verify their real email. The verification itself completes on {@code VerifyEmailPage} via the
 * emailed link.
 */
export function CompleteAccountPage() {
  const { t } = useTranslation()
  const navigate = useNavigate()
  const { user } = useAuth()

  const [isLoading, setIsLoading] = useState(false)
  const [sentTo, setSentTo] = useState<string | null>(null)

  const form = useForm({
    initialValues: { email: '' },
    validate: {
      email: (v) =>
        !v || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v) ? t('auth.validation.email') : null,
    },
  })

  // Already has a real email — nothing to complete. `<Navigate>` and not a bare `navigate()` call:
  // navigating from the render body updates the router *while rendering this component*, which
  // React answers with "Cannot update a component while rendering a different component" and then
  // an endless re-render loop (each render re-navigates). Caught by scripts/ssr-audit.mjs.
  if (user && !user.requiresEmail) {
    return <Navigate to={paths.home()} replace />
  }

  const handleSubmit = async (values: { email: string }) => {
    setIsLoading(true)
    try {
      await requestEmailChange({ email: values.email })
      setSentTo(values.email)
    } catch (error: unknown) {
      const code = (error as { response?: { data?: { code?: string } } })?.response?.data?.code
      notifications.show({
        message:
          code === 'EMAIL_ALREADY_EXISTS'
            ? t('auth.completeAccount.errors.emailExists')
            : t('auth.completeAccount.errors.failed'),
        color: 'red',
      })
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <Center mih="70vh">
      <Paper shadow="lg" radius="lg" p="xl" w="100%" maw={460}>
        {sentTo ? (
          <Stack ta="center">
            <IconMailCheck
              size={48}
              style={{ margin: '0 auto' }}
              color="var(--mantine-color-green-6)"
            />
            <Title order={2}>{t('auth.completeAccount.sent.title')}</Title>
            <Text c="dimmed">{t('auth.completeAccount.sent.message', { email: sentTo })}</Text>
            <Button variant="light" fullWidth onClick={() => navigate(paths.home())}>
              {t('auth.completeAccount.sent.continue')}
            </Button>
          </Stack>
        ) : (
          <Stack>
            <Stack gap="xs" ta="center">
              <Title order={2}>{t('auth.completeAccount.title')}</Title>
              <Text c="dimmed">{t('auth.completeAccount.subtitle')}</Text>
            </Stack>

            <form onSubmit={form.onSubmit(handleSubmit)}>
              <Stack>
                <TextInput
                  label={t('auth.form.email')}
                  placeholder="email@example.com"
                  name="email"
                  autoComplete="email"
                  leftSection={<IconMail size={16} />}
                  {...form.getInputProps('email')}
                />
                <Button type="submit" fullWidth loading={isLoading}>
                  {t('auth.completeAccount.submit')}
                </Button>
              </Stack>
            </form>
          </Stack>
        )}
      </Paper>
    </Center>
  )
}
