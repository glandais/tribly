import type { ReactNode } from 'react'
import { useTranslation } from 'react-i18next'
import { Anchor, Group, Paper, Stack, Text, Title } from '@mantine/core'
import { PrefetchLink } from '@/components/common/PrefetchLink'
import { ErrorBoundary } from '@/components/common/ErrorBoundary'

interface DashboardSectionProps {
  /** Id of the heading; the section is labelled by it. */
  id: string
  title: string
  /** « Voir tout »: the full list this section previews. */
  seeAllTo?: string
  /** Shown instead of the children when the section has nothing to list. */
  empty?: string
  isEmpty?: boolean
  /** Side panels are framed and use a smaller heading, like the mockup's aside. */
  framed?: boolean
  /** Next to the heading, on the right (a settings link). */
  aside?: ReactNode
  children?: ReactNode
}

/**
 * One block of the team dashboard: a heading, an optional « Voir tout » link and its rows. Each
 * section has its own error boundary, so one broken card does not blank the dashboard.
 */
export function DashboardSection({
  id,
  title,
  seeAllTo,
  empty,
  isEmpty,
  framed,
  aside,
  children,
}: DashboardSectionProps) {
  const { t } = useTranslation()

  const body = (
    <Stack gap="sm">
      <Group justify="space-between" align="center" wrap="nowrap" gap="xs">
        <Title order={2} id={id} size={framed ? 'h4' : 'h3'}>
          {title}
        </Title>
        {aside}
        {seeAllTo && !isEmpty && (
          <Anchor component={PrefetchLink} to={seeAllTo} size="sm" fw={500}>
            {t('teams.dashboard.seeAll')}
          </Anchor>
        )}
      </Group>
      <ErrorBoundary variant="inline">
        {isEmpty ? (
          <Text size="sm" c="dimmed">
            {empty}
          </Text>
        ) : (
          children
        )}
      </ErrorBoundary>
    </Stack>
  )

  return (
    <section aria-labelledby={id}>
      {framed ? (
        <Paper withBorder radius="md" p="md">
          {body}
        </Paper>
      ) : (
        body
      )}
    </section>
  )
}
