import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { notifications } from '@mantine/notifications'
import {
  Alert,
  Badge,
  Box,
  Button,
  Group,
  Select,
  Stack,
  Switch,
  Text,
  TextInput,
  Title,
} from '@mantine/core'
import { IconCheck, IconSend, IconTrash, IconX } from '@tabler/icons-react'
import {
  useGetTeamWebhook,
  useSaveTeamWebhook,
  useDeleteTeamWebhook,
  useTestTeamWebhook,
  getGetTeamWebhookQueryKey,
} from '@/api/endpoints/team-webhook/team-webhook'
import { NotificationDeliveryStatus, TeamWebhookKind } from '@/api/dto'
import type { TeamWebhookDto, TeamWebhookTestDto } from '@/api/dto'
import { languageNames, supportedLanguages, type SupportedLanguage } from '@/i18n'
import { ConfirmDialog } from '@/components/common/ConfirmDialog'
import { FormattedDateTime } from '@/components/common/FormattedDate'

/** Mantine palette name per delivery outcome — `success`/`danger` as BRANDING.md uses them. */
const STATUS_COLORS: Record<NotificationDeliveryStatus, string> = {
  [NotificationDeliveryStatus.PENDING]: 'gray',
  [NotificationDeliveryStatus.SENDING]: 'gray',
  [NotificationDeliveryStatus.SENT]: 'success',
  [NotificationDeliveryStatus.SKIPPED]: 'gray',
  [NotificationDeliveryStatus.FAILED]: 'danger',
}

function isSupportedLanguage(value: string | null | undefined): value is SupportedLanguage {
  return supportedLanguages.includes(value as SupportedLanguage)
}

interface TeamWebhookSettingsProps {
  teamSlug: string
}

/**
 * The team's outgoing webhook (`/api/teams/{teamSlug}/webhook`, team admins only): the team's
 * announcements posted to Slack, Discord or any https endpoint — see
 * `docs/plans/2026-09-18-notifications.md` §12.
 *
 * **The URL is a secret** (a Slack or Discord webhook URL is enough to post to the channel): the API
 * only ever returns it masked, so the input starts empty, shows the masked URL as its placeholder,
 * and an empty input on save keeps the current URL — the request simply omits it.
 */
export function TeamWebhookSettings({ teamSlug }: TeamWebhookSettingsProps) {
  const { t } = useTranslation()
  const { data } = useGetTeamWebhook(teamSlug)

  return (
    <Box mt="xl" pt="xl" style={{ borderTop: '1px solid var(--mantine-color-default-border)' }}>
      <Title order={2} size="lg">
        {t('teams.settings.webhook.title')}
      </Title>
      <Text size="sm" c="dimmed" mt="xs">
        {t('teams.settings.webhook.description')}
      </Text>

      {/* Keyed on the saved state so the form restarts from it after a save or a removal. */}
      {data && (
        <WebhookForm
          key={`${data.configured}-${data.language}-${data.enabled}`}
          teamSlug={teamSlug}
          webhook={data}
        />
      )}
    </Box>
  )
}

function WebhookForm({ teamSlug, webhook }: { teamSlug: string; webhook: TeamWebhookDto }) {
  const { t, i18n } = useTranslation()
  const queryClient = useQueryClient()
  const queryKey = getGetTeamWebhookQueryKey(teamSlug)

  const [url, setUrl] = useState('')
  const [language, setLanguage] = useState<SupportedLanguage>(
    isSupportedLanguage(webhook.language)
      ? webhook.language
      : isSupportedLanguage(i18n.language)
        ? i18n.language
        : 'fr'
  )
  // A new webhook starts enabled: nobody pastes a URL to leave it off.
  const [enabled, setEnabled] = useState(webhook.configured ? webhook.enabled : true)
  const [testResult, setTestResult] = useState<TeamWebhookTestDto | null>(null)
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false)

  const saveMutation = useSaveTeamWebhook()
  const deleteMutation = useDeleteTeamWebhook()
  const testMutation = useTestTeamWebhook()

  const trimmedUrl = url.trim()
  // The first save needs a URL; afterwards an empty field means "keep the current one".
  const canSave = webhook.configured || trimmedUrl.length > 0

  const handleSave = () => {
    saveMutation.mutate(
      {
        teamSlug,
        data: { url: trimmedUrl || undefined, language, enabled },
      },
      {
        onSuccess: (saved) => {
          queryClient.setQueryData(queryKey, saved)
          setUrl('')
          setTestResult(null)
          notifications.show({ message: t('teams.settings.webhook.saved'), color: 'green' })
        },
      }
    )
  }

  const handleTest = () => {
    setTestResult(null)
    testMutation.mutate(
      { teamSlug },
      {
        onSuccess: (result) => {
          setTestResult(result)
          queryClient.invalidateQueries({ queryKey })
        },
      }
    )
  }

  const handleDelete = () => {
    deleteMutation.mutate(
      { teamSlug },
      {
        onSuccess: () => {
          setShowDeleteConfirm(false)
          setTestResult(null)
          queryClient.invalidateQueries({ queryKey })
          notifications.show({ message: t('teams.settings.webhook.removed'), color: 'green' })
        },
      }
    )
  }

  return (
    <Stack mt="md">
      {webhook.configured && (
        <Group gap="xs">
          <Text size="sm">{t('teams.settings.webhook.current')}</Text>
          {/* Masked by the API — never anything more than this. */}
          <Text size="sm" ff="monospace" style={{ wordBreak: 'break-all' }}>
            {webhook.maskedUrl}
          </Text>
          {webhook.kind && (
            <Badge variant="light" color="gray">
              {t(`teams.settings.webhook.kind.${webhook.kind satisfies TeamWebhookKind}`)}
            </Badge>
          )}
        </Group>
      )}

      <TextInput
        type="url"
        label={t('teams.settings.webhook.urlLabel')}
        description={
          webhook.configured
            ? t('teams.settings.webhook.urlKeepHint')
            : t('teams.settings.webhook.urlHint')
        }
        placeholder={webhook.maskedUrl ?? 'https://hooks.slack.com/services/…'}
        value={url}
        onChange={(event) => setUrl(event.currentTarget.value)}
        autoComplete="off"
        spellCheck={false}
        maxLength={1000}
      />

      <Select
        label={t('teams.settings.webhook.languageLabel')}
        description={t('teams.settings.webhook.languageHint')}
        data={supportedLanguages.map((value) => ({ value, label: languageNames[value] }))}
        value={language}
        onChange={(value) => {
          if (isSupportedLanguage(value)) setLanguage(value)
        }}
        allowDeselect={false}
        maw={240}
      />

      <Switch
        checked={enabled}
        onChange={(event) => setEnabled(event.currentTarget.checked)}
        label={t('teams.settings.webhook.enabledLabel')}
        description={t('teams.settings.webhook.enabledHint')}
      />

      {webhook.configured && webhook.lastStatus && (
        <Stack gap={4}>
          <Group gap="xs">
            <Text size="sm">{t('teams.settings.webhook.lastAttempt')}</Text>
            <Badge variant="light" color={STATUS_COLORS[webhook.lastStatus]}>
              {t(
                `teams.settings.webhook.status.${webhook.lastStatus satisfies NotificationDeliveryStatus}`
              )}
            </Badge>
            {webhook.lastAttemptAt && (
              <Text size="sm" c="dimmed">
                <FormattedDateTime date={webhook.lastAttemptAt} />
              </Text>
            )}
          </Group>
          {webhook.lastError && (
            <Text size="xs" c="dimmed" style={{ wordBreak: 'break-word' }}>
              {webhook.lastError}
            </Text>
          )}
        </Stack>
      )}

      {testResult && (
        <Alert
          variant="light"
          color={testResult.success ? 'success' : 'danger'}
          icon={testResult.success ? <IconCheck size={16} /> : <IconX size={16} />}
          withCloseButton
          onClose={() => setTestResult(null)}
        >
          {testResult.success
            ? t('teams.settings.webhook.testSuccess', { status: testResult.statusCode ?? '2xx' })
            : testResult.statusCode
              ? t('teams.settings.webhook.testFailedStatus', {
                  status: testResult.statusCode,
                  error: testResult.error ?? '',
                })
              : t('teams.settings.webhook.testFailed', { error: testResult.error ?? '' })}
        </Alert>
      )}

      <Group>
        <Button onClick={handleSave} loading={saveMutation.isPending} disabled={!canSave}>
          {t('actions.save')}
        </Button>
        {webhook.configured && (
          <>
            {/* Tests the saved webhook, not what is typed: save first. */}
            <Button
              variant="default"
              leftSection={<IconSend size={16} />}
              onClick={handleTest}
              loading={testMutation.isPending}
            >
              {t('teams.settings.webhook.test')}
            </Button>
            <Button
              variant="subtle"
              color="danger"
              leftSection={<IconTrash size={16} />}
              onClick={() => setShowDeleteConfirm(true)}
            >
              {t('teams.settings.webhook.remove')}
            </Button>
          </>
        )}
      </Group>

      <ConfirmDialog
        isOpen={showDeleteConfirm}
        onClose={() => setShowDeleteConfirm(false)}
        onConfirm={handleDelete}
        title={t('teams.settings.webhook.removeTitle')}
        message={t('teams.settings.webhook.removeWarning')}
        confirmText={t('teams.settings.webhook.remove')}
        variant="danger"
        isLoading={deleteMutation.isPending}
      />
    </Stack>
  )
}
