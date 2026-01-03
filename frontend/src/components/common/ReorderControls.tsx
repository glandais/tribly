import { ChevronUpIcon, ChevronDownIcon } from '@heroicons/react/24/outline'
import { useTranslation } from 'react-i18next'

export interface ReorderControlsProps {
  /** Current item index (0-based) */
  index: number
  /** Total number of items in the list */
  total: number
  /** Callback when move is requested */
  onMove: (direction: 'up' | 'down') => void
  /** Whether to show the index badge (displays 1-based number) */
  showIndex?: boolean
  /** Disables all controls */
  disabled?: boolean
}

export function ReorderControls({
  index,
  total,
  onMove,
  showIndex = true,
  disabled = false,
}: ReorderControlsProps) {
  const { t } = useTranslation('common')
  const isFirst = index === 0
  const isLast = index >= total - 1

  return (
    <div className="flex items-center gap-1">
      {showIndex && (
        <span className="inline-flex items-center justify-center w-6 h-6 rounded-full bg-indigo-100 text-indigo-700 text-xs font-semibold">
          {index + 1}
        </span>
      )}
      <button
        type="button"
        onClick={() => onMove('up')}
        disabled={disabled || isFirst}
        className="p-1 text-gray-400 hover:text-gray-600 disabled:opacity-30 disabled:cursor-not-allowed"
        aria-label={t('aria.moveUp')}
      >
        <ChevronUpIcon className="w-4 h-4" />
      </button>
      <button
        type="button"
        onClick={() => onMove('down')}
        disabled={disabled || isLast}
        className="p-1 text-gray-400 hover:text-gray-600 disabled:opacity-30 disabled:cursor-not-allowed"
        aria-label={t('aria.moveDown')}
      >
        <ChevronDownIcon className="w-4 h-4" />
      </button>
    </div>
  )
}
