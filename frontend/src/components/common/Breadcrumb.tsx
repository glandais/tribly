import { Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { ChevronLeftIcon } from '@heroicons/react/24/outline'
import {
  Breadcrumb as BreadcrumbRoot,
  BreadcrumbItem,
  BreadcrumbLink,
  BreadcrumbList,
  BreadcrumbPage,
  BreadcrumbSeparator,
} from '@/components/ui/breadcrumb'

export interface BreadcrumbItemType {
  label: string
  path?: string
}

interface BreadcrumbProps {
  items: BreadcrumbItemType[]
}

export function Breadcrumb({ items }: BreadcrumbProps) {
  const { t } = useTranslation('common')

  if (items.length === 0) {
    return null
  }

  // For mobile: show simple back button to previous item
  const previousItem = items.length > 1 ? items[items.length - 2] : null

  return (
    <nav className="mb-6" aria-label={t('aria.breadcrumb')}>
      {/* Mobile: Back button */}
      {previousItem && previousItem.path && (
        <Link
          to={previousItem.path}
          className="sm:hidden inline-flex items-center text-sm text-muted-foreground hover:text-foreground"
        >
          <ChevronLeftIcon className="size-4 mr-1" aria-hidden="true" />
          {t('buttons.back')}
        </Link>
      )}

      {/* Desktop: Full breadcrumb path */}
      <BreadcrumbRoot className="hidden sm:block">
        <BreadcrumbList>
          {items.map((item, index) => {
            const isLast = index === items.length - 1

            return (
              <BreadcrumbItem key={index}>
                {index > 0 && <BreadcrumbSeparator />}
                {item.path && !isLast ? (
                  <BreadcrumbLink asChild>
                    <Link to={item.path}>{item.label}</Link>
                  </BreadcrumbLink>
                ) : (
                  <BreadcrumbPage>{item.label}</BreadcrumbPage>
                )}
              </BreadcrumbItem>
            )
          })}
        </BreadcrumbList>
      </BreadcrumbRoot>
    </nav>
  )
}
