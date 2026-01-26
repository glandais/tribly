import { useCallback, useEffect, useState } from 'react'
import { useSearchParams, useLocation } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { notifications } from '@mantine/notifications'
import { IconDeviceDesktop, IconCheck, IconX, IconArrowLeft, IconLink } from '@tabler/icons-react'
import {
  Center,
  Paper,
  Stack,
  Title,
  Text,
  Button,
  Alert,
  ThemeIcon,
  Loader,
  PinInput,
} from '@mantine/core'
import { useAuth } from '../../hooks/useAuth'
import { useGpsConnections } from '../../hooks/useGpsConnections'
import { GpsServiceType } from '../../api/dto'

export function DeviceVerifyPage() {
  const { t } = useTranslation()
  const [searchParams, setSearchParams] = useSearchParams()
  const location = useLocation()
  const { user } = useAuth()
  const { isConnected, isServiceAvailable, initiateConnect } = useGpsConnections()

  const isKarooPage = location.pathname === '/karoo'

  const [isVerifying, setIsVerifying] = useState(true)
  const [codeValid, setCodeValid] = useState(false)
  const [isCompleting, setIsCompleting] = useState(false)
  const [completed, setCompleted] = useState(false)
  const [manualCode, setManualCode] = useState('')

  const userCode = searchParams.get('code')?.toUpperCase()

  // Verify the user code exists
  useEffect(() => {
    if (!userCode) {
      setIsVerifying(false)
      return
    }

    // Reset state when code changes (important for manual entry)
    setIsVerifying(true)
    setCodeValid(false)

    const verifyCode = async () => {
      try {
        const response = await fetch(`/api/device/oauth/verify?code=${userCode}`)
        if (response.ok) {
          const data = await response.json()
          setCodeValid(true)
          if (data.authorized) {
            setCompleted(true)
          }
        }
      } catch {
        // Code not valid
      } finally {
        setIsVerifying(false)
      }
    }

    verifyCode()
  }, [userCode])

  const completeAuthorization = useCallback(
    async (userId: string) => {
      if (!userCode) return

      setIsCompleting(true)

      try {
        const response = await fetch('/api/device/oauth/complete', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            userCode,
            userId,
          }),
        })

        if (response.ok) {
          setCompleted(true)
        } else {
          notifications.show({
            message: t('device.errors.authorizationFailed'),
            color: 'red',
          })
        }
      } catch {
        notifications.show({
          message: t('device.errors.authorizationFailed'),
          color: 'red',
        })
      } finally {
        setIsCompleting(false)
      }
    },
    [userCode, t]
  )

  // Auto-complete authorization when code is valid (user is already authenticated)
  useEffect(() => {
    if (user && codeValid && !completed && !isCompleting) {
      completeAuthorization(user.id)
    }
  }, [user, codeValid, completed, isCompleting, completeAuthorization])

  // Show loading while verifying code
  if (isVerifying) {
    return (
      <Center mih="70vh">
        <Paper shadow="lg" radius="lg" p="xl" w="100%" maw={420}>
          <Stack align="center" gap="md">
            <Loader size="lg" />
            <Text c="dimmed">{t('device.verifying')}</Text>
          </Stack>
        </Paper>
      </Center>
    )
  }

  // No code provided - show manual entry form
  if (!userCode) {
    return (
      <Center mih="70vh">
        <Paper shadow="lg" radius="lg" p="xl" w="100%" maw={420}>
          <Stack align="center" gap="md">
            <ThemeIcon size={64} radius="xl" color="blue">
              <IconDeviceDesktop size={32} />
            </ThemeIcon>
            <Title order={2} ta="center">
              {t('device.manualEntry.title')}
            </Title>
            <Text c="dimmed" ta="center">
              {t('device.manualEntry.description')}
            </Text>
            <PinInput
              length={6}
              type="alphanumeric"
              value={manualCode}
              onChange={(value) => {
                const upperValue = value.toUpperCase()
                setManualCode(upperValue)
                if (upperValue.length === 6) {
                  setSearchParams({ code: upperValue })
                }
              }}
              size="xl"
              style={{ justifyContent: 'center' }}
            />
          </Stack>
        </Paper>
      </Center>
    )
  }

  // Invalid code (code provided but not valid)
  if (!codeValid) {
    return (
      <Center mih="70vh">
        <Paper shadow="lg" radius="lg" p="xl" w="100%" maw={420}>
          <Stack align="center" gap="md">
            <ThemeIcon size={64} radius="xl" color="red">
              <IconX size={32} />
            </ThemeIcon>
            <Title order={2} ta="center">
              {t('device.errors.title')}
            </Title>
            <Alert color="red" title={t('device.errors.invalidCode')}>
              {t('device.errors.invalidCodeMessage')}
            </Alert>
            <Button
              variant="light"
              leftSection={<IconArrowLeft size={16} />}
              onClick={() => {
                setManualCode('')
                setSearchParams({})
              }}
            >
              {t('device.manualEntry.tryAgain')}
            </Button>
          </Stack>
        </Paper>
      </Center>
    )
  }

  // Authorization completed
  if (completed) {
    const hammerheadAvailable = isServiceAvailable(GpsServiceType.HAMMERHEAD)
    const hammerheadConnected = isConnected(GpsServiceType.HAMMERHEAD)
    const needsHammerheadConnection = isKarooPage && hammerheadAvailable && !hammerheadConnected

    return (
      <Center mih="70vh">
        <Paper shadow="lg" radius="lg" p="xl" w="100%" maw={420}>
          <Stack align="center" gap="md">
            <ThemeIcon size={64} radius="xl" color="green">
              <IconCheck size={32} />
            </ThemeIcon>
            <Title order={2} ta="center">
              {t('device.success.title')}
            </Title>
            <Text c="dimmed" ta="center">
              {t('device.success.message')}
            </Text>
            {needsHammerheadConnection ? (
              <>
                <Alert color="orange" variant="light">
                  <Text size="sm">{t('device.success.hammerheadRequired')}</Text>
                </Alert>
                <Button
                  leftSection={<IconLink size={16} />}
                  onClick={() => initiateConnect(GpsServiceType.HAMMERHEAD)}
                >
                  {t('device.success.connectHammerhead')}
                </Button>
              </>
            ) : (
              <Alert color="blue" variant="light">
                <Text size="sm">{t('device.success.returnToDevice')}</Text>
              </Alert>
            )}
          </Stack>
        </Paper>
      </Center>
    )
  }

  // Show loading while completing authorization
  return (
    <Center mih="70vh">
      <Paper shadow="lg" radius="lg" p="xl" w="100%" maw={420}>
        <Stack align="center" gap="md">
          <Loader size="lg" />
          <Text c="dimmed">{t('device.completing')}</Text>
        </Stack>
      </Paper>
    </Center>
  )
}
