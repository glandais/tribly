import { Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { ChevronLeftIcon } from '@heroicons/react/24/outline'

export interface BreadcrumbItem {
  label: string
  path?: string
}

interface BreadcrumbProps {
  items: BreadcrumbItem[]
}

export function Breadcrumb({ items }: BreadcrumbProps) {
  const { t } = useTranslation('common')
  if (items.length === 0) {
    return null
  }

  // For mobile: show simple back button to previous item
  // For desktop: show full breadcrumb path
  const previousItem = items.length > 1 ? items[items.length - 2] : null

  return (
    <nav className="mb-6" aria-label="Breadcrumb">
      {/* Mobile: Back button */}
      {previousItem && previousItem.path && (
        <Link
          to={previousItem.path}
          className="sm:hidden inline-flex items-center text-sm text-gray-700 hover:text-gray-900 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-indigo-500 focus-visible:ring-offset-2 rounded px-1"
        >
          <ChevronLeftIcon className="h-4 w-4 mr-1" aria-hidden="true" />
          {t('buttons.back')}
        </Link>
      )}

      {/* Desktop: Full breadcrumb path */}
      <div className="hidden sm:flex items-center space-x-2 text-sm text-gray-700">
        {items.map((item, index) => {
          const isLast = index === items.length - 1

          return (
            <div key={index} className="flex items-center">
              {index > 0 && (
                <span className="mx-2 text-gray-400" aria-hidden="true">
                  /
                </span>
              )}
              {item.path && !isLast ? (
                <Link
                  to={item.path}
                  className="text-gray-700 hover:text-gray-900 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-indigo-500 focus-visible:ring-offset-2 rounded px-1"
                >
                  {item.label}
                </Link>
              ) : (
                <span className={isLast ? 'text-gray-900 font-medium' : ''}>{item.label}</span>
              )}
            </div>
          )
        })}
      </div>
    </nav>
  )
}
