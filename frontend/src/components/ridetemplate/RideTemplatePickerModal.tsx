import { useState, useEffect } from 'react'
import { useTranslation } from 'react-i18next'
import { keepPreviousData } from '@tanstack/react-query'
import { IconCopy, IconUsers } from '@tabler/icons-react'
import { Badge, Box, Button, Center, Group, Stack, Text, UnstyledButton } from '@mantine/core'
import { useListTemplates } from '@/api/endpoints/ride-templates/ride-templates'
import type { RideTemplateDto } from '@/api/dto'
import { MarkdownDisplay } from '../common/MarkdownDisplay'
import { LoadingSpinner } from '../common/LoadingSpinner'
import { Pagination } from '../common/Pagination'
import { usePagination } from '../../hooks/usePagination'
import { SearchInput } from '../common/SearchInput'
import { Modal } from '../common/Modal'

interface RideTemplatePickerModalProps {
  isOpen: boolean
  onClose: () => void
  onSelect: (template: RideTemplateDto) => void
  teamSlug: string
  title?: string
}

export function RideTemplatePickerModal({
  isOpen,
  onClose,
  onSelect,
  teamSlug,
  title,
}: RideTemplatePickerModalProps) {
  const { t } = useTranslation()
  const [page, setPage] = useState(0)
  const [search, setSearch] = useState('')
  const [debouncedSearch, setDebouncedSearch] = useState('')

  // Debounce search input
  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearch(search)
      setPage(0)
    }, 300)

    return () => clearTimeout(timer)
  }, [search])

  const pageSize = 20

  const {
    data: templatesResponse,
    isLoading,
    error,
  } = useListTemplates(
    teamSlug,
    {
      search: debouncedSearch || undefined,
      page,
      size: pageSize,
    },
    { query: { placeholderData: keepPreviousData } }
  )

  const { totalPages } = usePagination({
    pageSize,
    totalItems: templatesResponse?.total ?? 0,
  })

  const handleClose = () => {
    setSearch('')
    setPage(0)
    onClose()
  }

  const handleSelect = (template: RideTemplateDto) => {
    onSelect(template)
    handleClose()
  }

  const templates = templatesResponse?.templates || []

  const searchBar = (
    <SearchInput
      value={search}
      onChange={setSearch}
      placeholder={t('rideTemplates.picker.search')}
      fullWidth
    />
  )

  const footerContent = (
    <Button variant="default" onClick={handleClose}>
      {t('actions.cancelAction')}
    </Button>
  )

  return (
    <Modal
      isOpen={isOpen}
      onClose={handleClose}
      title={title || t('rideTemplates.picker.title')}
      size="2xl"
      subheader={searchBar}
      footer={footerContent}
    >
      {isLoading ? (
        <Center py="xl">
          <Stack align="center" gap="xs">
            <LoadingSpinner />
            <Text c="dimmed">{t('loading')}</Text>
          </Stack>
        </Center>
      ) : error ? (
        <Center py="xl">
          <Text c="red">{t('error.loading')}</Text>
        </Center>
      ) : templates.length === 0 ? (
        <Center py="xl">
          <Stack align="center" gap="xs">
            <IconCopy size={48} color="var(--mantine-color-dimmed)" />
            <Text c="dimmed">{t('rideTemplates.list.noResults')}</Text>
          </Stack>
        </Center>
      ) : (
        <>
          <Stack gap="sm">
            {templates.map((template) => (
              <UnstyledButton
                key={template.id}
                onClick={() => handleSelect(template)}
                p="md"
                style={{
                  border: '1px solid var(--mantine-color-default-border)',
                  borderRadius: 'var(--mantine-radius-md)',
                  textAlign: 'left',
                  width: '100%',
                }}
              >
                <Group justify="space-between" align="flex-start" wrap="nowrap">
                  <Box style={{ flex: 1, minWidth: 0 }}>
                    <Text fw={500} truncate>
                      {template.name}
                    </Text>
                    {template.markdown && (
                      <MarkdownDisplay
                        markdown={template.markdown}
                        preview={true}
                        maxLength={120}
                      />
                    )}
                  </Box>
                  <Group gap="xs" ml="md">
                    <IconUsers size={16} color="var(--mantine-color-dimmed)" />
                    <Text size="sm" c="dimmed">
                      {t('groups.groupCount', { count: template.groupCount })}
                    </Text>
                  </Group>
                </Group>
                {template.groups && template.groups.length > 0 && (
                  <Group gap="xs" mt="sm" wrap="wrap">
                    {template.groups.map((group) => (
                      <Badge key={group.id} variant="light" color="gray" size="sm">
                        {group.name}
                        {group.averageSpeed && (
                          <Text component="span" ml={4} c="dimmed">
                            {t('speed', { speed: group.averageSpeed })}
                          </Text>
                        )}
                      </Badge>
                    ))}
                  </Group>
                )}
              </UnstyledButton>
            ))}
          </Stack>

          <Pagination
            currentPage={page}
            totalPages={totalPages}
            onPageChange={setPage}
            variant="compact"
          />
        </>
      )}
    </Modal>
  )
}
