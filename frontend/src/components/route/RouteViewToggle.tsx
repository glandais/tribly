import { useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { Center, SegmentedControl } from '@mantine/core'
import { IconList, IconMap } from '@tabler/icons-react'
import { paths } from '@/config/paths'

export type RouteView = 'list' | 'map'

export interface RouteViewToggleProps {
  current: RouteView
  /** Omit for the cross-team routes pages. */
  teamSlug?: string
}

export function RouteViewToggle({ current, teamSlug }: RouteViewToggleProps) {
  const { t } = useTranslation()
  const navigate = useNavigate()

  const targets: Record<RouteView, string> = teamSlug
    ? { list: paths.routes(teamSlug), map: paths.routesMap(teamSlug) }
    : { list: paths.allRoutes(), map: paths.allRoutesMap() }

  return (
    <SegmentedControl
      value={current}
      onChange={(value) => navigate(targets[value as RouteView])}
      data={[
        {
          value: 'list',
          label: (
            <Center style={{ gap: 6 }}>
              <IconList size={16} />
              {t('routes.view.list')}
            </Center>
          ),
        },
        {
          value: 'map',
          label: (
            <Center style={{ gap: 6 }}>
              <IconMap size={16} />
              {t('routes.view.map')}
            </Center>
          ),
        },
      ]}
    />
  )
}
