import { useState } from 'react'
import { Link, useSearchParams, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useForm } from '@mantine/form'
import { notifications } from '@mantine/notifications'
import { IconArrowLeft, IconLock } from '@tabler/icons-react'
import {
  Center,
  Paper,
  Stack,
  Title,
  Text,
  Button,
  Anchor,
  PasswordInput,
  PinInput,
  Alert,
} from '@mantine/core'
import { useAuthStore } from '../../store/authStore'
import { paths } from '@/config/paths'
import { resetPassword as resetPasswordApi } from '@/api/endpoints/authentication/authentication'

export function ResetPasswordPage() {
  const { t } = useTranslation()
  const navigate = useNavigate()
  const [searchParams] = useSearchParams()
  const emailFromUrl = searchParams.get('email') ?? ''
  const { setAccessToken, setUser } = useAuthStore()

  const [isLoading, setIsLoading] = useState(false)
  const [step, setStep] = useState<'code' | 'password'>('code')
  const [verifiedCode, setVerifiedCode] = useState('')
  const [pinValue, setPinValue] = useState('')

  const passwordForm = useForm({
    initialValues: { newPassword: '', confirmPassword: '' },
    validate: {
      newPassword: (v) => (!v || v.length < 8 ? t('auth.validation.passwordMin') : null),
      confirmPassword: (v, values) =>
        v !== values.newPassword ? t('auth.validation.passwordMismatch') : null,
    },
  })

  const handleCodeComplete = (code: string) => {
    if (code.length === 6) {
      setVerifiedCode(code)
      setStep('password')
    }
  }

  const handleResetPassword = async (values: { newPassword: string }) => {
    setIsLoading(true)
    try {
      const data = await resetPasswordApi({
        email: emailFromUrl,
        code: verifiedCode,
        newPassword: values.newPassword,
      })
      if (data.accessToken) setAccessToken(data.accessToken)
      if (data.user) setUser(data.user)
      navigate(paths.home())
    } catch (error: unknown) {
      console.error('Reset password failed', error)
      const code = (error as { response?: { data?: { code?: string } } })?.response?.data?.code
      if (code === 'TOKEN_INVALID') {
        setStep('code')
        setPinValue('')
        setVerifiedCode('')
        notifications.show({ message: t('auth.errors.otpInvalid'), color: 'red' })
      } else {
        notifications.show({ message: t('auth.errors.resetPasswordFailed'), color: 'red' })
      }
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <Center mih="70vh">
      <Paper shadow="lg" radius="lg" p="xl" w="100%" maw={420}>
        <Stack>
          <Stack gap="xs" ta="center">
            <Title order={2}>{t('auth.resetPassword.title')}</Title>
            <Text c="dimmed">{t('auth.resetPassword.subtitle')}</Text>
          </Stack>

          {step === 'code' && (
            <Stack ta="center">
              <Alert color="blue" variant="light">
                <Text size="sm">{t('auth.otp.verify.instruction')}</Text>
              </Alert>
              <PinInput
                length={6}
                type="number"
                value={pinValue}
                onChange={(v) => {
                  setPinValue(v)
                  handleCodeComplete(v)
                }}
                disabled={isLoading}
                size="xl"
                style={{ justifyContent: 'center' }}
              />
            </Stack>
          )}

          {step === 'password' && (
            <form onSubmit={passwordForm.onSubmit(handleResetPassword)}>
              <Stack>
                <PasswordInput
                  label={t('auth.form.newPassword')}
                  placeholder={t('auth.form.passwordPlaceholder')}
                  autoComplete="new-password"
                  leftSection={<IconLock size={16} />}
                  {...passwordForm.getInputProps('newPassword')}
                />
                <PasswordInput
                  label={t('auth.form.confirmPassword')}
                  placeholder={t('auth.form.confirmPasswordPlaceholder')}
                  autoComplete="new-password"
                  leftSection={<IconLock size={16} />}
                  {...passwordForm.getInputProps('confirmPassword')}
                />
                <Button type="submit" fullWidth loading={isLoading}>
                  {t('auth.resetPassword.submit')}
                </Button>
              </Stack>
            </form>
          )}

          <Anchor component={Link} to={paths.login()} size="sm" ta="center">
            <IconArrowLeft size={14} style={{ verticalAlign: 'middle', marginRight: 4 }} />
            {t('auth.forgotPassword.backToLogin')}
          </Anchor>
        </Stack>
      </Paper>
    </Center>
  )
}
