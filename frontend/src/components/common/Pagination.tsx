import { useTranslation } from 'react-i18next'
import { ChevronLeftIcon, ChevronRightIcon } from '@heroicons/react/24/outline'

export interface PaginationProps {
  currentPage: number
  totalPages: number
  onPageChange: (page: number) => void
  variant?: 'default' | 'compact'
  className?: string
}

export function Pagination({
  currentPage,
  totalPages,
  onPageChange,
  variant = 'default',
  className = '',
}: PaginationProps) {
  const { t } = useTranslation('common')

  // Don't render if only one page or less
  if (totalPages <= 1) return null

  const isFirstPage = currentPage === 0
  const isLastPage = currentPage >= totalPages - 1

  const handlePrevious = () => {
    if (!isFirstPage) {
      onPageChange(Math.max(0, currentPage - 1))
    }
  }

  const handleNext = () => {
    if (!isLastPage) {
      onPageChange(Math.min(totalPages - 1, currentPage + 1))
    }
  }

  const buttonBaseClasses =
    'inline-flex items-center gap-1 px-3 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:bg-white transition-colors'

  const compactButtonClasses =
    'inline-flex items-center gap-1 px-2 py-1 text-xs font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:bg-white transition-colors'

  const buttonClasses = variant === 'compact' ? compactButtonClasses : buttonBaseClasses
  const iconSize = variant === 'compact' ? 'h-3 w-3' : 'h-4 w-4'
  const textSize = variant === 'compact' ? 'text-xs' : 'text-sm'

  return (
    <nav
      role="navigation"
      aria-label={t('pagination.label')}
      className={`flex items-center justify-center gap-2 ${className}`}
    >
      <button
        onClick={handlePrevious}
        disabled={isFirstPage}
        aria-label={t('pagination.previousPage')}
        className={buttonClasses}
        type="button"
      >
        <ChevronLeftIcon className={iconSize} />
        <span className={variant === 'compact' ? 'sr-only' : ''}>{t('buttons.previous')}</span>
      </button>

      <span
        className={`text-gray-700 font-medium ${textSize}`}
        aria-live="polite"
        aria-atomic="true"
      >
        {t('pagination.page', { current: currentPage + 1, total: totalPages })}
      </span>

      <button
        onClick={handleNext}
        disabled={isLastPage}
        aria-label={t('pagination.nextPage')}
        className={buttonClasses}
        type="button"
      >
        <span className={variant === 'compact' ? 'sr-only' : ''}>{t('buttons.next')}</span>
        <ChevronRightIcon className={iconSize} />
      </button>
    </nav>
  )
}
