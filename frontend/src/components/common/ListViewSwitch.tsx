import { useId, type ComponentType } from 'react'
import { useTranslation } from 'react-i18next'
import { Center, Group, SegmentedControl, Text, Tooltip, VisuallyHidden } from '@mantine/core'
import {
  IconCalendar,
  IconLayoutGrid,
  IconList,
  IconMap,
  type IconProps,
} from '@tabler/icons-react'

/**
 * Every way a list page can show its results. A page offers a subset: Parcours « Vignettes ·
 * Lignes · Carte », the Agenda « Vignettes · Lignes · Calendrier » (plan 2026-10-06 §4).
 */
export type ListView = 'card' | 'row' | 'map' | 'calendar'

const VIEW_ICONS: Record<ListView, ComponentType<IconProps>> = {
  card: IconLayoutGrid,
  row: IconList,
  map: IconMap,
  calendar: IconCalendar,
}

export interface ListViewSwitchProps<V extends ListView> {
  /** The views offered, in display order. */
  views: readonly V[]
  value: V
  /**
   * Called with the picked view. The page decides what it means: a URL parameter for the
   * thumbnails and the rows, a navigation for the views with their own path (map, calendar).
   */
  onChange: (view: V) => void
}

/**
 * The single view selector of the list pages (ledger `WEB-69`), right of the line that carries the
 * result count. Icons only: each one has its tooltip and its accessible name (a visually hidden
 * label inside the radio's `<label>`, so a screen reader and `getByRole('radio', { name })` read
 * « Lignes »), and the group is labelled « Affichage » — visibly, and through `aria-labelledby`.
 *
 * Purely presentational on purpose: where the view lives (`?view=`, a path) belongs to the page,
 * so the same component serves Parcours and the Agenda.
 */
export function ListViewSwitch<V extends ListView>({
  views,
  value,
  onChange,
}: ListViewSwitchProps<V>) {
  const { t } = useTranslation()
  const labelId = useId()

  return (
    <Group gap="xs" wrap="nowrap">
      <Text id={labelId} size="sm" c="dimmed">
        {t('listView.label')}
      </Text>
      <SegmentedControl
        aria-labelledby={labelId}
        size="sm"
        value={value}
        onChange={(next) => onChange(next as V)}
        data={views.map((view) => {
          const Icon: ComponentType<IconProps> = VIEW_ICONS[view as ListView]
          const label = t(`listView.${view satisfies ListView}`)
          return {
            value: view,
            label: (
              <Tooltip label={label} withArrow openDelay={300}>
                <Center data-testid={`list-view-${view}`}>
                  <Icon size={18} aria-hidden />
                  <VisuallyHidden>{label}</VisuallyHidden>
                </Center>
              </Tooltip>
            ),
          }
        })}
      />
    </Group>
  )
}
