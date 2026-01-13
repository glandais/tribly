import { useTranslation } from 'react-i18next'
import { Group, Text, Pagination as MantinePagination } from '@mantine/core'
import { useResponsive } from '@/hooks/useResponsive'

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
  const { sizeCompact, isMobile } = useResponsive()

  // Don't render if only one page or less
  if (totalPages <= 1) return null

  // Convert 0-indexed (internal) to 1-indexed (Mantine)
  const mantineValue = currentPage + 1

  const handleChange = (page: number) => {
    // Convert 1-indexed (Mantine) back to 0-indexed (internal)
    onPageChange(page - 1)
  }

  if (isMobile || variant === 'compact') {
    return (
      <Group justify="center" gap="xs" component="nav" aria-label={t('pagination.label')}>
        <MantinePagination.Root
          total={totalPages}
          value={mantineValue}
          onChange={handleChange}
          size="xs"
        >
          <Group gap="xs">
            <MantinePagination.Previous aria-label={t('pagination.previousPage')} />
            <Text size="xs" fw={500}>
              {t('pagination.page', { current: mantineValue, total: totalPages })}
            </Text>
            <MantinePagination.Next aria-label={t('pagination.nextPage')} />
          </Group>
        </MantinePagination.Root>
      </Group>
    )
  }

  return (
    <Group justify="center" component="nav" aria-label={t('pagination.label')}>
      <MantinePagination
        total={totalPages}
        value={mantineValue}
        onChange={handleChange}
        size={sizeCompact}
        withEdges
        siblings={1}
        boundaries={1}
        getItemProps={(page) => ({
          'aria-label': t('pagination.goToPage', { page }),
        })}
      />
    </Group>
  )
}
