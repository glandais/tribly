import { useTranslation } from 'react-i18next'
import { CalendarIcon, MapPinIcon } from '@heroicons/react/24/outline'
import type { TripStageDto } from '@/api/dto'
import { useFormattedDate } from '../../utils/dateFormat'
import { MediaDisplay } from '../common/MediaDisplay'
import { paths } from '@/config/paths'

interface TripStageCardProps {
  stage: TripStageDto
  index: number
  teamSlug: string
  onHover?: (stageId: string | null) => void
  isHighlighted?: boolean
}

export function TripStageCard({
  stage,
  index,
  teamSlug,
  onHover,
  isHighlighted = false,
}: TripStageCardProps) {
  const { t } = useTranslation('trips')
  const { t: tCommon } = useTranslation('common')
  const { formatDateTime } = useFormattedDate()

  return (
    <div
      className={`border rounded-lg p-4 transition-all ${
        isHighlighted
          ? 'border-indigo-500 bg-indigo-50 shadow-md'
          : 'border-gray-200 bg-white hover:border-gray-300'
      }`}
      onMouseEnter={() => onHover?.(stage.id)}
      onMouseLeave={() => onHover?.(null)}
    >
      <div className="flex items-center justify-between gap-4 mb-2">
        <div className="flex items-center gap-2 min-w-0">
          <span className="inline-flex items-center justify-center w-6 h-6 rounded-full bg-indigo-100 text-indigo-700 text-xs font-semibold shrink-0">
            {index + 1}
          </span>
          <h3 className="font-semibold text-gray-900 truncate">{stage.name}</h3>
        </div>
        <div className="flex items-center text-sm text-gray-500 shrink-0">
          <CalendarIcon className="w-4 h-4 mr-1" />
          {formatDateTime(stage.dateTime)}
        </div>
      </div>

      {/* Stage description */}
      {stage.media.markdown && (
        <div className="mb-3 line-clamp-2">
          <MediaDisplay media={stage.media} className="text-sm text-gray-600" />
        </div>
      )}

      {/* Start and End Places */}
      {(stage.startPlace || stage.endPlace) && (
        <div className="flex flex-wrap items-center gap-4 text-sm text-gray-600">
          {stage.startPlace && (
            <span className="flex items-center">
              <MapPinIcon className="w-4 h-4 mr-1 text-green-600" />
              <span className="font-medium text-green-700">{tCommon('startPlace')}:</span>
              <span className="ml-1">{stage.startPlace.name}</span>
            </span>
          )}
          {stage.endPlace && (
            <span className="flex items-center">
              <MapPinIcon className="w-4 h-4 mr-1 text-red-600" />
              <span className="font-medium text-red-700">{tCommon('endPlace')}:</span>
              <span className="ml-1">{stage.endPlace.name}</span>
            </span>
          )}
        </div>
      )}

      {/* Route link */}
      {stage.routeSlug && (
        <div className="mt-2 text-sm">
          <a
            href={paths.route(teamSlug, stage.routeSlug)}
            className="text-indigo-600 hover:text-indigo-700 font-medium"
          >
            {t('stage.viewRoute')}
          </a>
        </div>
      )}
    </div>
  )
}
