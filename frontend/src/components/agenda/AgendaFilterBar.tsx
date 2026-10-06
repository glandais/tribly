import type { ReactNode } from 'react'
import { useTranslation } from 'react-i18next'
import { Group, SegmentedControl } from '@mantine/core'
import type { TeamDetailDto } from '@/api/dto'
import type { AgendaTypeValue } from '@/hooks/filters/agendaFilters'
import { useAuth } from '@/hooks/useAuth'

/** The time scope the bar offers: the list's three, or the calendar's two (plan §2.2). */
export type AgendaBarScope = 'upcoming' | 'me' | 'past' | 'all'

interface AgendaFilterBarProps {
  team: Pick<TeamDetailDto, 'enableRides' | 'enableTrips' | 'enableRoutes'>
  /**
   * `list`: « À venir / Je participe / Passées ». `calendar`: « Tout / Je participe » — a calendar
   * is browsed by month, where « À venir » and « Passées » mean nothing.
   */
  mode: 'list' | 'calendar'
  scope: AgendaBarScope
  onScopeChange: (scope: AgendaBarScope) => void
  type: AgendaTypeValue
  onTypeChange: (type: AgendaTypeValue) => void
  /** After the two selectors: the list's « Filtres » button. */
  children?: ReactNode
}

/**
 * The agenda's filter bar, shared by its list (`TeamAgendaPage`) and its calendar view
 * (`TeamCalendarPage`): the period, then the kind — « Tout / Sorties / Voyages », each kind only
 * when the team has its module (a ride or a trip needs the routes too). « Je participe » is for a
 * signed-in reader only: `participating` yields nothing for an anonymous one.
 */
export function AgendaFilterBar({
  team,
  mode,
  scope,
  onScopeChange,
  type,
  onTypeChange,
  children,
}: AgendaFilterBarProps) {
  const { t } = useTranslation()
  const { isAuthenticated } = useAuth()

  const me = isAuthenticated ? [{ value: 'me', label: t('publications.scope.me') }] : []
  const scopes =
    mode === 'calendar'
      ? [{ value: 'all', label: t('agenda.scope.all') }, ...me]
      : [
          { value: 'upcoming', label: t('publications.scope.upcoming') },
          ...me,
          { value: 'past', label: t('agenda.scope.past') },
        ]

  const rides = !!team.enableRides && !!team.enableRoutes
  const trips = !!team.enableTrips && !!team.enableRoutes
  const types = [
    { value: 'all', label: t('agenda.type.all') },
    ...(rides ? [{ value: 'ride', label: t('agenda.type.ride') }] : []),
    ...(trips ? [{ value: 'trip', label: t('agenda.type.trip') }] : []),
  ]

  return (
    <Group gap="sm" wrap="wrap" align="center">
      <SegmentedControl
        value={scope}
        onChange={(next) => onScopeChange(next as AgendaBarScope)}
        data={scopes}
        aria-label={t('agenda.scope.label')}
      />
      {/* Shown with a single kind too: its tags are only offered once it is picked. */}
      {(rides || trips) && (
        <SegmentedControl
          value={type}
          onChange={(next) => onTypeChange(next as AgendaTypeValue)}
          data={types}
          aria-label={t('agenda.type.label')}
        />
      )}
      {children}
    </Group>
  )
}
