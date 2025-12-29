import { useTranslation } from 'react-i18next'
import { PaperClipIcon, ArrowDownTrayIcon } from '@heroicons/react/24/outline'
import { MediaDto, AssetDto } from '../../api/api'
import { MarkdownDisplay } from './MarkdownDisplay'

export interface MediaDisplayProps {
  media: MediaDto
  className?: string
}

/**
 * MediaDisplay component - renders media content in display mode.
 * Extracts markdown from MediaDto and passes to MarkdownDisplay.
 * Shows attachments with download links when present.
 */
export function MediaDisplay({ media, className = '' }: MediaDisplayProps) {
  const { t } = useTranslation('common')
  const attachments = media.assets.attachments
  const images = media.assets.images

  return (
    <div className={className}>
      <MarkdownDisplay markdown={media.markdown || ''} preview={false} images={images} />

      {/* Attachments section */}
      {attachments.length > 0 && (
        <div className="mt-4 border border-gray-200 rounded-lg p-3 bg-gray-50">
          <h4 className="text-sm font-medium text-gray-700 flex items-center gap-1 mb-2">
            <PaperClipIcon className="h-4 w-4" />
            {t('attachments.title')}
          </h4>
          <ul className="space-y-1">
            {attachments.map((attachment: AssetDto) => (
              <li key={attachment.id}>
                <a
                  href={attachment.url}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="flex items-center gap-2 text-sm text-indigo-600 hover:text-indigo-800 hover:underline py-1"
                >
                  <PaperClipIcon className="h-4 w-4 text-gray-400 shrink-0" />
                  <span className="truncate">{attachment.fileName}</span>
                  <ArrowDownTrayIcon className="h-4 w-4 shrink-0" />
                </a>
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  )
}
