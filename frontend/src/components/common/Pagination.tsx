import { useTranslation } from 'react-i18next'
import { Group, Button, Text } from '@mantine/core'
import { IconChevronLeft, IconChevronRight } from '@tabler/icons-react'

export interface PaginationProps {
  currentPage: number
  totalPages: number
  onPageChange: (page: number) => void
  variant?: 'default' | 'compact'
}

export function Pagination({
  currentPage,
  totalPages,
  onPageChange,
  variant = 'default',
}: PaginationProps) {
  const { t } = useTranslation()

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

  const isCompact = variant === 'compact'
  const buttonSize = isCompact ? 'xs' : 'sm'
  const iconSize = isCompact ? 12 : 16

  return (
    <Group justify="center" gap="sm" component="nav" aria-label={t('pagination.label')}>
      <Button
        variant="default"
        size={buttonSize}
        onClick={handlePrevious}
        disabled={isFirstPage}
        aria-label={t('pagination.previousPage')}
        leftSection={<IconChevronLeft size={iconSize} />}
      >
        {!isCompact && t('buttons.previous')}
      </Button>

      <Text size={isCompact ? 'xs' : 'sm'} fw={500} aria-live="polite" aria-atomic="true">
        {t('pagination.page', { current: currentPage + 1, total: totalPages })}
      </Text>

      <Button
        variant="default"
        size={buttonSize}
        onClick={handleNext}
        disabled={isLastPage}
        aria-label={t('pagination.nextPage')}
        rightSection={<IconChevronRight size={iconSize} />}
      >
        {!isCompact && t('buttons.next')}
      </Button>
    </Group>
  )
}
