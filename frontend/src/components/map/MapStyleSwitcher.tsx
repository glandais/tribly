import { useState, useCallback } from 'react'
import { useTranslation } from 'react-i18next'
import { MapIcon, XMarkIcon } from '@heroicons/react/24/outline'
import { MAP_STYLES, STYLE_IDS, type MapStyleId } from './mapStyles'

export interface MapStyleSwitcherProps {
  position?: 'top-left' | 'top-right' | 'bottom-left' | 'bottom-right'
  currentStyleId: MapStyleId
  onStyleChange: (styleId: MapStyleId) => void
}

const POSITION_CLASSES: Record<string, string> = {
  'top-left': 'top-2 left-12',
  'top-right': 'top-2 right-2',
  'bottom-left': 'bottom-2 left-2',
  'bottom-right': 'bottom-2 right-2',
}

export function MapStyleSwitcher({
  position = 'bottom-left',
  currentStyleId,
  onStyleChange,
}: MapStyleSwitcherProps) {
  const { t } = useTranslation('common')
  const [isExpanded, setIsExpanded] = useState(false)

  const toggleExpanded = useCallback(() => {
    setIsExpanded((prev) => !prev)
  }, [])

  const handleStyleSelect = useCallback(
    (styleId: MapStyleId) => {
      onStyleChange(styleId)
      setIsExpanded(false)
    },
    [onStyleChange]
  )

  return (
    <div className={`absolute ${POSITION_CLASSES[position]} z-10`}>
      {isExpanded ? (
        <div className="bg-white rounded-lg shadow-lg p-2 min-w-[160px]">
          <div className="flex justify-between items-center mb-2 pb-2 border-b">
            <span className="text-sm font-medium text-gray-700">{t('map.styles.title')}</span>
            <button
              onClick={toggleExpanded}
              className="p-1 hover:bg-gray-100 rounded"
              aria-label={t('buttons.cancel')}
            >
              <XMarkIcon className="w-4 h-4 text-gray-500" />
            </button>
          </div>
          <div className="flex flex-col gap-1">
            {STYLE_IDS.map((styleId) => {
              const style = MAP_STYLES[styleId]
              const isSelected = styleId === currentStyleId
              return (
                <button
                  key={styleId}
                  onClick={() => handleStyleSelect(styleId)}
                  className={`
                    px-3 py-2 text-sm text-left rounded transition-colors
                    ${
                      isSelected
                        ? 'bg-indigo-100 text-indigo-700 font-medium'
                        : 'hover:bg-gray-100 text-gray-700'
                    }
                  `}
                >
                  {style.name}
                </button>
              )
            })}
          </div>
        </div>
      ) : (
        <button
          onClick={toggleExpanded}
          className="bg-white p-2 rounded-lg shadow-lg hover:bg-gray-50 transition-colors"
          aria-label={t('map.styles.switch')}
          title={t('map.styles.switch')}
        >
          <MapIcon className="w-5 h-5 text-gray-700" />
        </button>
      )}
    </div>
  )
}
