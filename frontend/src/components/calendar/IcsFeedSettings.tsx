import { useState, useMemo } from 'react'
import { useTranslation } from 'react-i18next'
import {
  Button,
  TextInput,
  Stack,
  CopyButton,
  ActionIcon,
  Group,
  Paper,
  Alert,
  Skeleton,
} from '@mantine/core'
import {
  IconCopy,
  IconCheck,
  IconRefresh,
  IconInfoCircle,
  IconCalendarPlus,
} from '@tabler/icons-react'
import { useGetToken, useRegenerateToken } from '@/api/endpoints/calendar/calendar'
import { ConfirmDialog } from '@/components/common/ConfirmDialog'
import type { CalendarTokenDto } from '@/api/dto'

interface IcsFeedSettingsProps {
  teamSlug?: string
}

function buildFeedUrl(tokenData: CalendarTokenDto | undefined, teamSlug?: string): string {
  if (!tokenData) {
    return ''
  }
  if (teamSlug) {
    return tokenData.teamFeedUrlTemplate.replace('{teamSlug}', teamSlug)
  }
  return tokenData.globalFeedUrl
}

function toWebcalUrl(feedUrl: string): string {
  return feedUrl.replace(/^https?:\/\//, 'webcal://')
}

export function IcsFeedSettings({ teamSlug }: IcsFeedSettingsProps): React.ReactElement {
  const { t } = useTranslation()
  const [confirmOpen, setConfirmOpen] = useState(false)

  const { data: tokenData, isLoading } = useGetToken()
  const { mutate: regenerate, isPending } = useRegenerateToken()

  const feedUrl = useMemo(() => buildFeedUrl(tokenData, teamSlug), [tokenData, teamSlug])
  const webcalUrl = useMemo(() => toWebcalUrl(feedUrl), [feedUrl])

  const descriptionKey = teamSlug ? 'calendar.ics.teamDescription' : 'calendar.ics.description'
  const labelKey = teamSlug ? 'calendar.ics.teamFeedLabel' : 'calendar.ics.globalFeedLabel'

  function handleRegenerate(): void {
    regenerate(undefined, {
      onSuccess: () => {
        setConfirmOpen(false)
      },
    })
  }

  if (isLoading) {
    return (
      <Paper withBorder p="lg" radius="md">
        <Stack gap="md">
          <Skeleton height={20} width="60%" />
          <Skeleton height={36} />
          <Skeleton height={36} width={150} />
        </Stack>
      </Paper>
    )
  }

  return (
    <Paper withBorder p="lg" radius="md">
      <Stack gap="md">
        <Alert icon={<IconInfoCircle size={16} />} color="blue" variant="light">
          {t(descriptionKey)}
        </Alert>

        {tokenData && (
          <>
            <Group gap="xs" align="flex-end">
              <TextInput value={feedUrl} readOnly style={{ flex: 1 }} label={t(labelKey)} />
              <CopyButton value={feedUrl}>
                {({ copied, copy }) => (
                  <ActionIcon
                    onClick={copy}
                    variant="light"
                    size="lg"
                    color={copied ? 'teal' : 'blue'}
                  >
                    {copied ? <IconCheck size={18} /> : <IconCopy size={18} />}
                  </ActionIcon>
                )}
              </CopyButton>
            </Group>

            <Group gap="xs">
              <Button
                component="a"
                href={webcalUrl}
                target="_blank"
                leftSection={<IconCalendarPlus size={16} />}
              >
                {t('calendar.ics.subscribe')}
              </Button>

              <Button
                leftSection={<IconRefresh size={16} />}
                variant="outline"
                color="orange"
                onClick={() => setConfirmOpen(true)}
                loading={isPending}
              >
                {t('calendar.ics.regenerate')}
              </Button>
            </Group>
          </>
        )}

        <ConfirmDialog
          isOpen={confirmOpen}
          onClose={() => setConfirmOpen(false)}
          onConfirm={handleRegenerate}
          title={t('calendar.ics.regenerateConfirmTitle')}
          message={t('calendar.ics.regenerateConfirmMessage')}
          variant="warning"
          isLoading={isPending}
        />
      </Stack>
    </Paper>
  )
}
