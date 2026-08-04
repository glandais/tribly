import { createPortal } from 'react-dom'
import { useTranslation } from 'react-i18next'
import { useControl } from 'react-map-gl/maplibre'
import type { ControlPosition } from 'react-map-gl/maplibre'
import type { IControl } from 'maplibre-gl'
import { Tooltip } from '@mantine/core'
import { IconRoute, IconRouteOff } from '@tabler/icons-react'

/**
 * A MapLibre control whose element React renders into, so the button joins the
 * native control stack (same group styling and position as the zoom controls)
 * while staying an ordinary React subtree.
 */
class PortalControl implements IControl {
  private readonly container = document.createElement('div')

  onAdd() {
    this.container.className = 'maplibregl-ctrl maplibregl-ctrl-group'
    return this.container
  }

  onRemove() {
    this.container.remove()
  }

  getElement() {
    return this.container
  }
}

interface HideTrackControlProps {
  hidden: boolean
  onToggle: () => void
  position?: ControlPosition
}

/**
 * Toggles the GPX trace and its markers from the map itself — the same state the
 * "h" shortcut drives (`useHideTrackKey`), which the tooltip advertises.
 */
export function HideTrackControl({
  hidden,
  onToggle,
  position = 'top-left',
}: HideTrackControlProps) {
  const { t } = useTranslation()
  const control = useControl(() => new PortalControl(), { position })

  const label = hidden ? t('map.hideTrack.show') : t('map.hideTrack.hide')

  return createPortal(
    <Tooltip label={label} position="right" withArrow>
      <button
        type="button"
        className="maplibregl-ctrl-icon"
        onClick={onToggle}
        aria-label={label}
        aria-pressed={hidden}
        style={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}
      >
        {hidden ? <IconRouteOff size={18} /> : <IconRoute size={18} />}
      </button>
    </Tooltip>,
    control.getElement()
  )
}
