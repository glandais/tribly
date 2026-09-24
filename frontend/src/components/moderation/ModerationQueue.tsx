import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { notifications } from '@mantine/notifications'
import {
  Anchor,
  Badge,
  Blockquote,
  Button,
  Group,
  Paper,
  SegmentedControl,
  Skeleton,
  Stack,
  Text,
} from '@mantine/core'
import { IconCheck, IconEyeOff, IconFlag, IconTrash } from '@tabler/icons-react'
import { ModerationAction, ReportQueueStatus, ReportStatus, ReportTargetType } from '@/api/dto'
import type {
  ModerationDecisionRequest,
  ModerationItemDto,
  ModerationQueueResponse,
  ReportReason,
} from '@/api/dto'
import { paths } from '@/config/paths'
import { useFormattedDate } from '@/utils/dateFormat'
import { PrefetchLink } from '../common/PrefetchLink'
import { UserAvatar } from '../common/UserAvatar'
import { ConfirmDialog } from '../common/ConfirmDialog'
import { EmptyState } from '../common/EmptyState'
import { QueryStateBoundary } from '../common/QueryStateBoundary'

interface ModerationQueueProps {
  /**
   * `team`: a team's organizers — reporters stay anonymous. `platform`: the domain's platform
   * admins — every team, with who reported.
   */
  scope: 'team' | 'platform'
  status: ReportQueueStatus
  onStatusChange: (status: ReportQueueStatus) => void
  data: ModerationQueueResponse | undefined
  isLoading: boolean
  error: unknown
  onRetry: () => void
  /** Applies a decision to every open report of one target. Rejects when the API refuses it. */
  resolve: (decision: ModerationDecisionRequest) => Promise<unknown>
}

const STATUS_COLORS: Record<ReportStatus, string> = {
  [ReportStatus.OPEN]: 'warning',
  [ReportStatus.REMOVED]: 'danger',
  [ReportStatus.DISMISSED]: 'gray',
}

const itemKey = (item: Pick<ModerationItemDto, 'targetType' | 'targetId'>) =>
  `${item.targetType}:${item.targetId}`

/** The page that shows the reported content: the publication itself, or the one a comment is on. */
function contentPath(item: ModerationItemDto): string | undefined {
  const { teamSlug, contentSlug, contentType } = item
  if (!contentSlug) return undefined
  switch (contentType) {
    case ReportTargetType.POST:
      return paths.post(teamSlug, contentSlug)
    case ReportTargetType.RIDE:
      return paths.ride(teamSlug, contentSlug)
    case ReportTargetType.TRIP:
      return paths.trip(teamSlug, contentSlug)
    case ReportTargetType.ROUTE:
      return paths.route(teamSlug, contentSlug)
    case ReportTargetType.AD:
      return paths.ad(teamSlug, contentSlug)
    default:
      return undefined
  }
}

interface ModerationItemCardProps {
  item: ModerationItemDto
  scope: 'team' | 'platform'
  isResolving: boolean
  onRemove: () => void
  onDismiss: () => void
}

function ModerationItemCard({
  item,
  scope,
  isResolving,
  onRemove,
  onDismiss,
}: ModerationItemCardProps) {
  const { t } = useTranslation()
  const { formatDateTime, isGuessedTimezone } = useFormattedDate()

  const isMember = item.targetType === ReportTargetType.MEMBER
  const isOpen = item.status === ReportStatus.OPEN
  const path = contentPath(item)
  const contentLabel = item.contentName
    ? item.targetType === ReportTargetType.COMMENT
      ? t('moderation.queue.commentOn', { name: item.contentName })
      : item.contentName
    : undefined

  return (
    <Paper withBorder p="md" radius="md">
      <Stack gap="sm">
        <Group justify="space-between" align="flex-start" wrap="wrap" gap="xs">
          <Group gap="xs">
            <Badge variant="light" color="gray">
              {t(`moderation.targetType.${item.targetType satisfies ReportTargetType}`)}
            </Badge>
            <Badge variant="light" color="danger" leftSection={<IconFlag size={12} />}>
              {t('moderation.queue.reportCount', { count: item.reportCount })}
            </Badge>
            {item.hidden && (
              <Badge variant="light" color="warning" leftSection={<IconEyeOff size={12} />}>
                {t('moderation.queue.hidden')}
              </Badge>
            )}
            {!isOpen && (
              <Badge variant="light" color={STATUS_COLORS[item.status]}>
                {t(`moderation.status.${item.status satisfies ReportStatus}`)}
              </Badge>
            )}
          </Group>
          <Text size="xs" c="dimmed" suppressHydrationWarning={isGuessedTimezone}>
            {t('moderation.queue.lastReported', { date: formatDateTime(item.lastReportedAt) })}
          </Text>
        </Group>

        {scope === 'platform' && (
          <Text size="sm" c="dimmed">
            {t('moderation.queue.team', { team: item.teamName })}
          </Text>
        )}

        {contentLabel &&
          (path ? (
            <Anchor component={PrefetchLink} to={path} fw={500}>
              {contentLabel}
            </Anchor>
          ) : (
            <Text fw={500}>{contentLabel}</Text>
          ))}
        {!isMember && !item.contentName && (
          <Text size="sm" c="dimmed" fs="italic">
            {t('moderation.queue.contentGone')}
          </Text>
        )}

        <Group gap="xs" wrap="nowrap">
          <UserAvatar user={item.targetUser} size="sm" />
          <Text size="sm">
            {isMember
              ? t('moderation.queue.member', { name: item.targetUser.displayName })
              : t('moderation.queue.author', { name: item.targetUser.displayName })}
          </Text>
        </Group>

        {item.excerpt && (
          <Blockquote color="gray" p="sm" radius="sm">
            <Text
              size="sm"
              lineClamp={6}
              style={{ whiteSpace: 'pre-wrap', wordBreak: 'break-word' }}
            >
              {item.excerpt}
            </Text>
          </Blockquote>
        )}

        <Group gap={6}>
          {item.reasons.map((reason) => (
            <Badge key={reason} variant="outline" color="gray" size="sm">
              {t(`moderation.reason.${reason satisfies ReportReason}`)}
            </Badge>
          ))}
        </Group>

        {item.messages.length > 0 && (
          <Stack gap={4}>
            <Text size="sm" fw={500}>
              {t('moderation.queue.messages')}
            </Text>
            {item.messages.map((message, index) => (
              <Text
                key={index}
                size="sm"
                c="dimmed"
                style={{ whiteSpace: 'pre-wrap', wordBreak: 'break-word' }}
              >
                {message}
              </Text>
            ))}
          </Stack>
        )}

        {scope === 'platform' && item.reporters && item.reporters.length > 0 && (
          <Stack gap={4}>
            <Text size="sm" fw={500}>
              {t('moderation.queue.reporters')}
            </Text>
            <Group gap="sm">
              {item.reporters.map((reporter) => (
                <Group key={reporter.id} gap={6} wrap="nowrap">
                  <UserAvatar user={reporter} size="xs" />
                  <Text size="sm">{reporter.displayName}</Text>
                </Group>
              ))}
            </Group>
          </Stack>
        )}

        {isOpen && (
          <Stack gap="xs">
            <Group gap="sm">
              {!isMember && (
                <Button
                  color="danger"
                  variant="light"
                  leftSection={<IconTrash size={16} />}
                  onClick={onRemove}
                  disabled={isResolving}
                >
                  {t('moderation.queue.actions.remove')}
                </Button>
              )}
              <Button
                variant="default"
                leftSection={<IconCheck size={16} />}
                onClick={onDismiss}
                loading={isResolving}
              >
                {t('moderation.queue.actions.dismiss')}
              </Button>
            </Group>
            {isMember && (
              <Text size="xs" c="dimmed">
                {t('moderation.queue.memberHint')}
              </Text>
            )}
          </Stack>
        )}
      </Stack>
    </Paper>
  )
}

/**
 * A moderation queue — one card per reported target, all its reports grouped. Shared by a team's
 * admin tab and the platform admin tab; only the platform one shows the team and the reporters.
 */
export function ModerationQueue({
  scope,
  status,
  onStatusChange,
  data,
  isLoading,
  error,
  onRetry,
  resolve,
}: ModerationQueueProps) {
  const { t } = useTranslation()
  const [resolvingKey, setResolvingKey] = useState<string | null>(null)
  const [toRemove, setToRemove] = useState<ModerationItemDto | null>(null)

  const decide = async (item: ModerationItemDto, action: ModerationAction) => {
    setResolvingKey(itemKey(item))
    try {
      await resolve({ targetType: item.targetType, targetId: item.targetId, action })
      notifications.show({
        message:
          action === ModerationAction.REMOVE_CONTENT
            ? t('moderation.queue.removed')
            : t('moderation.queue.dismissed'),
        color: 'green',
      })
      setToRemove(null)
    } catch {
      // The API error is already shown by the axios mutator.
    } finally {
      setResolvingKey(null)
    }
  }

  const items = data?.items ?? []

  return (
    <Stack>
      <Text size="sm" c="dimmed">
        {scope === 'platform'
          ? t('moderation.queue.description.platform')
          : t('moderation.queue.description.team')}
      </Text>

      <SegmentedControl
        value={status}
        onChange={(value) => onStatusChange(value as ReportQueueStatus)}
        data={[
          { value: ReportQueueStatus.OPEN, label: t('moderation.queue.tabs.open') },
          { value: ReportQueueStatus.RESOLVED, label: t('moderation.queue.tabs.resolved') },
        ]}
        style={{ alignSelf: 'flex-start' }}
      />

      <QueryStateBoundary
        isLoading={isLoading}
        isError={!!error}
        error={error}
        onRetry={onRetry}
        isEmpty={items.length === 0}
        skeleton={
          <Stack>
            {Array.from({ length: 3 }).map((_, i) => (
              <Skeleton key={i} height={160} radius="md" />
            ))}
          </Stack>
        }
        empty={
          status === ReportQueueStatus.OPEN ? (
            <EmptyState
              icon={<IconFlag size={48} />}
              title={t('moderation.queue.empty.open.title')}
              description={t('moderation.queue.empty.open.description')}
            />
          ) : (
            <EmptyState
              icon={<IconFlag size={48} />}
              title={t('moderation.queue.empty.resolved.title')}
              description={t('moderation.queue.empty.resolved.description')}
            />
          )
        }
      >
        <Stack>
          {items.map((item) => (
            <ModerationItemCard
              key={itemKey(item)}
              item={item}
              scope={scope}
              isResolving={resolvingKey === itemKey(item)}
              onRemove={() => setToRemove(item)}
              onDismiss={() => void decide(item, ModerationAction.DISMISS)}
            />
          ))}
        </Stack>
      </QueryStateBoundary>

      <ConfirmDialog
        isOpen={!!toRemove}
        onClose={() => setToRemove(null)}
        onConfirm={() => {
          if (toRemove) void decide(toRemove, ModerationAction.REMOVE_CONTENT)
        }}
        title={t('moderation.queue.removeConfirm.title')}
        message={t('moderation.queue.removeConfirm.message')}
        confirmText={t('moderation.queue.actions.remove')}
        variant="danger"
        isLoading={!!toRemove && resolvingKey === itemKey(toRemove)}
      />
    </Stack>
  )
}
