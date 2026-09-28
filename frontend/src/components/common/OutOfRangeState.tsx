import { Button } from '@mantine/core'
import { IconFileOff } from '@tabler/icons-react'
import { useTranslation } from 'react-i18next'
import { EmptyState } from './EmptyState'

/**
 * A list page past the last one (`?p=99`, an old link, a list that shrank). The list is not empty,
 * so the absolute empty state (« Aucune équipe n'a encore été créée ») would lie: this says the page
 * does not exist and offers the way back. Render it instead of the empty state when `page > 0`.
 */
export function OutOfRangeState({ onFirstPage }: { onFirstPage: () => void }) {
  const { t } = useTranslation()
  return (
    <EmptyState
      variant="filtered"
      icon={<IconFileOff size={48} />}
      title={t('pagination.outOfRange.title')}
      description={t('pagination.outOfRange.description')}
      actions={
        <Button variant="light" onClick={onFirstPage}>
          {t('pagination.outOfRange.firstPage')}
        </Button>
      }
    />
  )
}
