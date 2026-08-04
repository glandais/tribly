import { useTranslation } from 'react-i18next'
import { IconArrowBackUp, IconArrowForwardUp } from '@tabler/icons-react'
import { MapControlButton, MapControlGroup } from '@/components/map/MapControl'

interface UndoRedoControlProps {
  canUndo: boolean
  canRedo: boolean
  onUndo: () => void
  onRedo: () => void
}

/** Planner history, in the map's control column alongside the zoom and basemap buttons. */
export function UndoRedoControl({ canUndo, canRedo, onUndo, onRedo }: UndoRedoControlProps) {
  const { t } = useTranslation()

  return (
    <MapControlGroup>
      <MapControlButton
        label={t('planner.undo')}
        icon={<IconArrowBackUp size={20} />}
        onClick={onUndo}
        disabled={!canUndo}
      />
      <MapControlButton
        label={t('planner.redo')}
        icon={<IconArrowForwardUp size={20} />}
        onClick={onRedo}
        disabled={!canRedo}
      />
    </MapControlGroup>
  )
}
