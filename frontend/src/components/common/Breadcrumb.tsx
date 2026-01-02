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
  showBackLink?: boolean
}

export function Breadcrumb({ items, showBackLink = false }: BreadcrumbProps) {
  const { t } = useTranslation('common')

  if (items.length === 0) {
    return null
  }

  // For back link: show link to previous item
  const previousItem = items.length > 1 ? items[items.length - 2] : null

  return (
    <nav className="mb-6" aria-label={t('aria.breadcrumb')}>
      {/* Back link - shown on mobile always, on desktop only when showBackLink is true */}
      {previousItem && previousItem.path && (
        <Link
          to={previousItem.path}
          className={`inline-flex items-center text-sm text-muted-foreground hover:text-foreground ${
            showBackLink ? 'mb-2' : 'sm:hidden'
          }`}
        >
          <ChevronLeftIcon className="size-4 mr-1" aria-hidden="true" />
          {previousItem.label}
        </Link>
      )}

      {/* Desktop: Full breadcrumb path (hidden when showBackLink is true to avoid redundancy) */}
      {!showBackLink && (
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
      )}
    </nav>
  )
}
