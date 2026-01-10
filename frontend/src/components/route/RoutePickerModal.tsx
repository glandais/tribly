import { useState, useEffect } from 'react'
import { useTranslation } from 'react-i18next'
import { keepPreviousData } from '@tanstack/react-query'
import { IconMap, IconArrowsMaximize, IconArrowUp } from '@tabler/icons-react'
import { Stack, Group, Button, Text, SimpleGrid, Image, Center, Loader } from '@mantine/core'
import { useListRoutes } from '@/api/endpoints/routes/routes'
import type { RouteDto } from '@/api/dto'
import { MarkdownDisplay } from '../../components/common/MarkdownDisplay'
import { Pagination } from '../common/Pagination'
import { usePagination } from '../../hooks/usePagination'
import { SearchInput } from '../common/SearchInput'
import { Modal } from '../common/Modal'

interface RoutePickerModalProps {
  isOpen: boolean
  onClose: () => void
  onSelect: (route: RouteDto | null) => void
  teamSlug: string
  selectedRouteSlug?: string | null
  title?: string
  onCreateNew?: () => void
}

export function RoutePickerModal({
  isOpen,
  onClose,
  onSelect,
  teamSlug,
  selectedRouteSlug,
  title,
  onCreateNew,
}: RoutePickerModalProps) {
  const { t } = useTranslation()
  const [page, setPage] = useState(0)
  const [search, setSearch] = useState('')
  const [debouncedSearch, setDebouncedSearch] = useState('')

  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearch(search)
      setPage(0)
    }, 300)

    return () => clearTimeout(timer)
  }, [search])

  const pageSize = 20

  const {
    data: routesResponse,
    isLoading,
    error,
  } = useListRoutes(
    teamSlug,
    { page, size: pageSize, search: debouncedSearch || undefined },
    {
      query: { placeholderData: keepPreviousData },
    }
  )

  const { totalPages } = usePagination({
    pageSize,
    totalItems: routesResponse?.total ?? 0,
  })

  const handleClose = () => {
    setSearch('')
    setPage(0)
    onClose()
  }

  const routes = routesResponse?.routes || []

  const searchBar = (
    <Group gap="md">
      <SearchInput
        value={search}
        onChange={setSearch}
        placeholder={t('routes.picker.search')}
        fullWidth
        style={{ flex: 1 }}
      />
      {onCreateNew && <Button onClick={onCreateNew}>{t('routes.create.title')}</Button>}
    </Group>
  )

  const footerContent = (
    <>
      {selectedRouteSlug && (
        <Button variant="subtle" color="red" onClick={() => onSelect(null)}>
          {t('routes.picker.clearSelection')}
        </Button>
      )}
      <Button variant="default" onClick={handleClose}>
        {t('actions.cancelAction')}
      </Button>
    </>
  )

  return (
    <Modal
      isOpen={isOpen}
      onClose={handleClose}
      title={title || t('routes.picker.title')}
      size="4xl"
      subheader={searchBar}
      footer={footerContent}
    >
      {isLoading ? (
        <Center py="xl">
          <Stack align="center">
            <Loader />
            <Text c="dimmed">{t('loading')}</Text>
          </Stack>
        </Center>
      ) : error ? (
        <Center py="xl">
          <Text c="red">{t('error.loading')}</Text>
        </Center>
      ) : routes.length === 0 ? (
        <Center py="xl">
          <Stack align="center">
            <IconMap size={48} color="var(--mantine-color-gray-5)" />
            <Text c="dimmed">{t('routes.picker.noResults')}</Text>
            {selectedRouteSlug && (
              <Button variant="subtle" onClick={() => onSelect(null)}>
                {t('routes.picker.clearSelection')}
              </Button>
            )}
          </Stack>
        </Center>
      ) : (
        <Stack gap="md">
          <SimpleGrid cols={{ base: 1, md: 2 }} spacing="md">
            {routes.map((route) => (
              <Button
                key={route.id}
                variant="default"
                h="auto"
                p="md"
                onClick={() => onSelect(route)}
                style={{
                  textAlign: 'left',
                  border:
                    route.slug === selectedRouteSlug
                      ? '2px solid var(--mantine-color-indigo-5)'
                      : undefined,
                  backgroundColor:
                    route.slug === selectedRouteSlug ? 'var(--mantine-color-indigo-0)' : undefined,
                }}
              >
                <Stack gap="xs" w="100%">
                  <Image
                    src={route.media.assets.thumbnail?.url}
                    alt={route.name}
                    h={128}
                    fit="cover"
                    radius="sm"
                  />
                  <Text fw={500} truncate>
                    {route.name}
                  </Text>
                  <MarkdownDisplay markdown={route.media.markdown} preview={true} maxLength={120} />
                  <Group gap="md">
                    <Group gap={4}>
                      <IconArrowsMaximize size={14} />
                      <Text size="xs" c="dimmed">
                        {t('distance', { distance: (route.distance / 1000).toFixed(1) })}
                      </Text>
                    </Group>
                    <Group gap={4}>
                      <IconArrowUp size={14} />
                      <Text size="xs" c="dimmed">
                        {t('elevation', { elevation: route.elevationGain })}
                      </Text>
                    </Group>
                  </Group>
                </Stack>
              </Button>
            ))}
          </SimpleGrid>

          <Pagination
            currentPage={page}
            totalPages={totalPages}
            onPageChange={setPage}
            variant="compact"
          />
        </Stack>
      )}
    </Modal>
  )
}
