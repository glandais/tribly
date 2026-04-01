import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { notifications } from '@mantine/notifications'
import i18next from 'i18next'
import {
  Stack,
  Group,
  Title,
  Button,
  Text,
  Paper,
  Box,
  Badge,
  ActionIcon,
  Anchor,
  Center,
  Loader,
  Pagination,
} from '@mantine/core'
import { IconPencil, IconTrash, IconPlus, IconMapPin } from '@tabler/icons-react'
import {
  useListPlaces,
  useDeletePlace,
  getListPlacesQueryKey,
} from '../../api/endpoints/places/places'
import { ConfirmDialog } from '../common/ConfirmDialog'
import { PlaceForm } from './PlaceForm'
import type { PlaceDetailDto } from '../../api/dto'

interface PlaceListProps {
  teamSlug: string
  canManage: boolean
}

const PAGE_SIZE = 20

export function PlaceList({ teamSlug, canManage }: PlaceListProps) {
  const { t } = useTranslation()
  const queryClient = useQueryClient()
  const [page, setPage] = useState(1)
  const { data: placesData, isLoading } = useListPlaces(teamSlug, {
    page: page - 1,
    size: PAGE_SIZE,
  })
  const deleteMutation = useDeletePlace()

  const [editingPlace, setEditingPlace] = useState<PlaceDetailDto | null>(null)
  const [showCreateForm, setShowCreateForm] = useState(false)
  const [deleteConfirm, setDeleteConfirm] = useState<string | null>(null)

  if (isLoading) {
    return (
      <Center py="xl">
        <Loader />
      </Center>
    )
  }

  const places = placesData?.places ?? []

  const totalPages = placesData ? Math.ceil(placesData.total / PAGE_SIZE) : 0

  const handleDelete = (placeId: string) => {
    deleteMutation.mutate(
      { teamSlug: teamSlug, placeId },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getListPlacesQueryKey(teamSlug) })
          notifications.show({
            message: i18next.t('teams.notifications.placeDeleted'),
            color: 'green',
          })
          setDeleteConfirm(null)
        },
      }
    )
  }

  function newPlace(): PlaceDetailDto {
    return {
      id: '',
      name: '',
      address: '',
      link: '',
      startPlace: true,
      endPlace: true,
    }
  }

  return (
    <Stack>
      <Group justify="space-between">
        <Title order={4}>{t('places.title')}</Title>
        {canManage && (
          <Button leftSection={<IconPlus size={16} />} onClick={() => setShowCreateForm(true)}>
            {t('places.add')}
          </Button>
        )}
      </Group>

      {places.length === 0 ? (
        <Text size="sm" c="dimmed" py="md">
          {t('places.empty')}
        </Text>
      ) : (
        <Paper withBorder>
          <Stack gap={0}>
            {places.map((place, index) => (
              <Box
                key={place.id}
                py="md"
                px="md"
                style={{
                  borderBottom:
                    index < places.length - 1
                      ? '1px solid var(--mantine-color-default-border)'
                      : undefined,
                }}
              >
                <Group justify="space-between" wrap="nowrap">
                  <Group gap="sm" wrap="nowrap" align="flex-start">
                    <IconMapPin
                      size={20}
                      color="var(--mantine-color-dimmed)"
                      style={{ marginTop: 2, flexShrink: 0 }}
                    />
                    <Box>
                      <Text size="sm" fw={500}>
                        {place.name}
                      </Text>
                      {place.address && (
                        <Text size="sm" c="dimmed">
                          {place.address}
                        </Text>
                      )}
                      {place.link && (
                        <Anchor
                          href={place.link}
                          target="_blank"
                          rel="noopener noreferrer"
                          size="sm"
                        >
                          {t('places.viewLink')}
                        </Anchor>
                      )}
                      <Group gap="xs" mt="xs">
                        {place.startPlace && (
                          <Badge size="sm" color="green" variant="light">
                            {t('startPlace')}
                          </Badge>
                        )}
                        {place.endPlace && (
                          <Badge size="sm" color="blue" variant="light">
                            {t('endPlace')}
                          </Badge>
                        )}
                      </Group>
                    </Box>
                  </Group>
                  {canManage && (
                    <Group gap="xs">
                      <ActionIcon
                        variant="subtle"
                        color="gray"
                        onClick={() => setEditingPlace(place)}
                        title={t('actions.edit')}
                      >
                        <IconPencil size={16} />
                      </ActionIcon>
                      <ActionIcon
                        variant="subtle"
                        color="danger"
                        onClick={() => setDeleteConfirm(place.id)}
                        title={t('places.delete')}
                      >
                        <IconTrash size={16} />
                      </ActionIcon>
                    </Group>
                  )}
                </Group>
              </Box>
            ))}
          </Stack>
        </Paper>
      )}

      {totalPages > 1 && <Pagination value={page} onChange={setPage} total={totalPages} />}

      {/* Create Form Modal */}
      {showCreateForm && (
        <PlaceForm
          teamSlug={teamSlug}
          place={newPlace()}
          onClose={() => setShowCreateForm(false)}
        />
      )}

      {/* Edit Form Modal */}
      {editingPlace && (
        <PlaceForm teamSlug={teamSlug} place={editingPlace} onClose={() => setEditingPlace(null)} />
      )}

      {/* Delete Confirmation */}
      <ConfirmDialog
        isOpen={!!deleteConfirm}
        onClose={() => setDeleteConfirm(null)}
        onConfirm={() => deleteConfirm && handleDelete(deleteConfirm)}
        title={t('places.deleteConfirm.title')}
        message={t('places.deleteConfirm.message')}
        confirmText={t('places.deleteConfirm.confirm')}
        variant="danger"
        isLoading={deleteMutation.isPending}
      />
    </Stack>
  )
}
