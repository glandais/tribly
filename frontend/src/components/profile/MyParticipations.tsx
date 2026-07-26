import { useMemo, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { Accordion, Badge, Group, SimpleGrid, Stack, Title } from '@mantine/core'
import { IconCalendarEvent, IconHistory } from '@tabler/icons-react'
import { PublicationCard, PublicationCardSkeleton } from '../card'
import { EmptyState } from '../common/EmptyState'
import { Pagination } from '../common/Pagination'
import { useAuth } from '../../hooks/useAuth'
import { useMyParticipations } from '../../hooks/useMyParticipations'
import { usePaginatedQuery } from '../../hooks/usePaginatedQuery'
import { ListViewMode } from '@/api/dto'
import type { ListMyParticipationsParams } from '@/api/dto'

const PAGE_SIZE = 6

type SectionId = 'upcoming' | 'history'

interface ParticipationsPanelProps {
  /** Date window of the section — `{ from }` for upcoming, `{ to }` for history. */
  dateWindow: Pick<ListMyParticipationsParams, 'from' | 'to'>
  /** The paged query only fires once the accordion item is open. */
  opened: boolean
  emptyTitle: string
  emptyDescription: string
  emptyIcon: React.ReactNode
}

function ParticipationsPanel({
  dateWindow,
  opened,
  emptyTitle,
  emptyDescription,
  emptyIcon,
}: ParticipationsPanelProps) {
  // The page lives in local state on purpose: this is an accordion embedded in the
  // profile page, not a dedicated list route. The "filters and pagination belong in
  // the query string via useUrlFilters" rule applies to list PAGES — the plan makes
  // the URL-filter requirement conditional on choosing a dedicated page, which we
  // deliberately did not (no new entry in contracts/routes.yaml for a secondary view).
  const [page, setPage] = useState(0)

  const { data, isLoading } = useMyParticipations(
    { ...dateWindow, page, size: PAGE_SIZE, view: ListViewMode.COMPACT },
    opened
  )

  const publications = data?.publications ?? []
  const { totalPages } = usePaginatedQuery({
    page,
    pageSize: PAGE_SIZE,
    totalItems: data?.total ?? 0,
  })

  if (isLoading || !data) {
    return (
      <SimpleGrid cols={{ base: 1, sm: 2 }} spacing="md">
        {[...Array(2)].map((_, i) => (
          <PublicationCardSkeleton key={i} />
        ))}
      </SimpleGrid>
    )
  }

  if (publications.length === 0) {
    return <EmptyState icon={emptyIcon} title={emptyTitle} description={emptyDescription} />
  }

  return (
    <Stack gap="md">
      <SimpleGrid cols={{ base: 1, sm: 2 }} spacing="md">
        {publications.map((publication) => (
          <PublicationCard key={publication.id} publication={publication} showTeam />
        ))}
      </SimpleGrid>
      <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />
    </Stack>
  )
}

/**
 * "My participations" on the profile page: two accordion sections (upcoming rides
 * and history), each labelled with its exact total.
 *
 * The totals sit on the *closed* controls, so they cannot wait for the panel to be
 * expanded: each section runs a cheap `size: 1` count query up front, and the full
 * page of cards is only fetched once its item opens.
 */
export function MyParticipations() {
  const { t } = useTranslation()
  const { isAuthenticated } = useAuth()
  const [opened, setOpened] = useState<SectionId | null>(null)

  // Frozen at mount so the two windows stay complementary and the query keys stable.
  const now = useMemo(() => new Date().toISOString(), [])

  const countParams = { size: 1, view: ListViewMode.COMPACT } as const
  const { data: upcomingCount } = useMyParticipations({ from: now, ...countParams })
  const { data: historyCount } = useMyParticipations({ to: now, ...countParams })

  // Anonymous visitors get no shell at all — the hook is disabled for them anyway.
  if (!isAuthenticated) return null

  return (
    <Stack>
      <Title order={3} size="h5">
        {t('profile.participations.title')}
      </Title>

      <Accordion
        variant="separated"
        value={opened}
        onChange={(value) => setOpened(value as SectionId | null)}
      >
        <Accordion.Item value="upcoming">
          <Accordion.Control icon={<IconCalendarEvent size={18} />}>
            <Group gap="xs">
              {t('profile.participations.upcoming')}
              <Badge color="primary" variant="light">
                {upcomingCount?.total ?? 0}
              </Badge>
            </Group>
          </Accordion.Control>
          <Accordion.Panel>
            <ParticipationsPanel
              dateWindow={{ from: now }}
              opened={opened === 'upcoming'}
              emptyIcon={<IconCalendarEvent size={48} />}
              emptyTitle={t('profile.participations.empty.upcoming.title')}
              emptyDescription={t('profile.participations.empty.upcoming.description')}
            />
          </Accordion.Panel>
        </Accordion.Item>

        <Accordion.Item value="history">
          <Accordion.Control icon={<IconHistory size={18} />}>
            <Group gap="xs">
              {t('profile.participations.history')}
              <Badge variant="default">{historyCount?.total ?? 0}</Badge>
            </Group>
          </Accordion.Control>
          <Accordion.Panel>
            <ParticipationsPanel
              dateWindow={{ to: now }}
              opened={opened === 'history'}
              emptyIcon={<IconHistory size={48} />}
              emptyTitle={t('profile.participations.empty.history.title')}
              emptyDescription={t('profile.participations.empty.history.description')}
            />
          </Accordion.Panel>
        </Accordion.Item>
      </Accordion>
    </Stack>
  )
}
