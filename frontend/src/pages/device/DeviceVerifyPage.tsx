import { useCallback, useEffect, useState } from 'react'
import { useSearchParams, useLocation } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { notifications } from '@mantine/notifications'
import {
  IconDeviceDesktop,
  IconCheck,
  IconX,
  IconArrowLeft,
  IconLink,
  IconAlertTriangle,
  IconBan,
} from '@tabler/icons-react'
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
  Group,
} from '@mantine/core'
import { useAuth } from '../../hooks/useAuth'
import { useGpsConnections } from '../../hooks/useGpsConnections'
import { GpsServiceType } from '../../api/dto'
import type { CompleteRequest, DenyRequest, VerifyResponse } from '../../api/dto'
import { useFormattedDate } from '../../utils/dateFormat'
import { AXIOS_INSTANCE } from '../../lib/axiosInstance'

export function DeviceVerifyPage() {
  const { t } = useTranslation()
  const [searchParams, setSearchParams] = useSearchParams()
  const location = useLocation()
  const { user } = useAuth()
  const { isConnected, isServiceAvailable, initiateConnect } = useGpsConnections()

  const isKarooPage = location.pathname === '/karoo'

  const userCode = searchParams.get('code')?.toUpperCase()

  const [isVerifying, setIsVerifying] = useState(() => Boolean(searchParams.get('code')))
  const [pending, setPending] = useState<VerifyResponse | null>(null)
  const [completed, setCompleted] = useState(false)
  const [denied, setDenied] = useState(false)
  const [submitting, setSubmitting] = useState<'confirm' | 'deny' | null>(null)
  const [manualCode, setManualCode] = useState('')
  const { formatRelative } = useFormattedDate()
  const codeValid = pending !== null

  // Verify the user code exists
  useEffect(() => {
    if (!userCode) return

    const verifyCode = async () => {
      // Reset state when code changes (important for manual entry)
      setIsVerifying(true)
      setPending(null)
      try {
        const response = await fetch(
          `/api/device/oauth/verify?code=${encodeURIComponent(userCode)}`
        )
        if (response.ok) {
          const data: VerifyResponse = await response.json()
          setPending(data)
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

  // Never on its own: a link carrying a code must not be enough to pair a stranger's device with
  // this account (docs/LEDGER_*.md SEC-2, audit H3). The user reads the code, compares it with the
  // device in front of them, and presses a button.
  const confirmAuthorization = useCallback(async () => {
    if (!userCode) return
    setSubmitting('confirm')
    try {
      const body: CompleteRequest = { userCode, confirmed: true }
      await AXIOS_INSTANCE.post('/api/device/oauth/complete', body)
      setCompleted(true)
    } catch {
      notifications.show({
        message: t('device.errors.authorizationFailed'),
        color: 'red',
      })
    } finally {
      setSubmitting(null)
    }
  }, [userCode, t])

  const denyAuthorization = useCallback(async () => {
    if (!userCode) return
    setSubmitting('deny')
    try {
      const body: DenyRequest = { userCode }
      await AXIOS_INSTANCE.post('/api/device/oauth/deny', body)
      setDenied(true)
    } catch {
      notifications.show({
        message: t('device.errors.denyFailed'),
        color: 'red',
      })
    } finally {
      setSubmitting(null)
    }
  }, [userCode, t])

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
                setPending(null)
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

  // Refused: the code is dead, the device will start over
  if (denied) {
    return (
      <Center mih="70vh">
        <Paper shadow="lg" radius="lg" p="xl" w="100%" maw={420}>
          <Stack align="center" gap="md">
            <ThemeIcon size={64} radius="xl" color="gray">
              <IconBan size={32} />
            </ThemeIcon>
            <Title order={2} ta="center">
              {t('device.denied.title')}
            </Title>
            <Text c="dimmed" ta="center">
              {t('device.denied.message')}
            </Text>
          </Stack>
        </Paper>
      </Center>
    )
  }

  // Explicit confirmation (RFC 8628 §5.4)
  const deviceName = t(
    pending?.clientId === 'karoo'
      ? 'device.client.karoo'
      : pending?.clientId === 'garmin'
        ? 'device.client.garmin'
        : 'device.client.other'
  )
  return (
    <Center mih="70vh">
      <Paper shadow="lg" radius="lg" p="xl" w="100%" maw={420}>
        <Stack align="center" gap="md">
          <ThemeIcon size={64} radius="xl" color="blue">
            <IconDeviceDesktop size={32} />
          </ThemeIcon>
          <Title order={2} ta="center">
            {t('device.confirm.title')}
          </Title>
          <Text ta="center">
            {t('device.confirm.question', { device: deviceName, name: user?.displayName ?? '' })}
          </Text>
          <Text fz={32} fw={700} ff="monospace" style={{ letterSpacing: '0.2em' }}>
            {userCode}
          </Text>
          {pending?.requestedAt && (
            <Text size="sm" c="dimmed" ta="center">
              {t('device.confirm.requestedAt', { when: formatRelative(pending.requestedAt) })}
            </Text>
          )}
          <Alert color="orange" variant="light" icon={<IconAlertTriangle size={16} />}>
            <Text size="sm">{t('device.confirm.warning')}</Text>
          </Alert>
          <Group grow w="100%">
            <Button
              variant="default"
              color="red"
              leftSection={<IconX size={16} />}
              loading={submitting === 'deny'}
              disabled={submitting !== null}
              onClick={denyAuthorization}
            >
              {t('device.confirm.deny')}
            </Button>
            <Button
              leftSection={<IconCheck size={16} />}
              loading={submitting === 'confirm'}
              disabled={submitting !== null}
              onClick={confirmAuthorization}
            >
              {t('device.confirm.authorize')}
            </Button>
          </Group>
        </Stack>
      </Paper>
    </Center>
  )
}
