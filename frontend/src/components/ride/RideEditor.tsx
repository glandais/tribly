import { useState, useEffect } from 'react'
import { useForm } from '@mantine/form'
import { zod4Resolver } from 'mantine-form-zod-resolver'
import { useTranslation } from 'react-i18next'
import {
  TextInput,
  Radio,
  Stack,
  Group,
  Button,
  Text,
  Paper,
  Box,
  NumberInput,
  ActionIcon,
  SimpleGrid,
} from '@mantine/core'
import { TimeInput, DateTimePicker } from '@mantine/dates'
import { IconX } from '@tabler/icons-react'
import { SlugEditor } from '../common/SlugEditor'
import { ReorderControls } from '../common/ReorderControls'
import { RoutePickerModal } from '../route/RoutePickerModal'
import { CreateRouteModal } from '../route/CreateRouteModal'
import { RoutePreview } from '../route/RoutePreview'
import { RoutePreviewCompact } from '../route/RoutePreviewCompact'
import { MediaEditor } from '../common/MediaEditor'
import { PlaceAutocomplete } from '../common/PlaceAutocomplete'
import type { RouteDto, TeamDetailDto, RideRequest } from '@/api/dto'
import { Status } from '@/api/dto'
import { createRideBody } from '@/api/zod/rides/rides.zod'

const rideSchema = createRideBody.refine(
  (data) => {
    if (data.status === Status.DRAFT && data.publishAt) {
      return new Date(data.publishAt) > new Date()
    }
    return true
  },
  {
    message: 'Publish date must be in the future for draft posts',
    path: ['publishAt'],
  }
)

type Target = { type: 'group'; index: number } | { type: 'ride' }

interface RideEditorProps {
  team: TeamDetailDto
  teamSlug: string
  initialValues: RideRequest
  onSubmit: (data: RideRequest) => void | Promise<void>
  onCancel: () => void
  isPending: boolean
  submitButtonText?: string
  cancelButtonText?: string
  currentSlug?: string
  onSlugChange?: (newSlug: string) => Promise<void>
  canEditSlug?: boolean
}

export function RideEditor({
  team,
  teamSlug,
  initialValues,
  onSubmit,
  onCancel,
  isPending,
  submitButtonText,
  cancelButtonText,
  currentSlug,
  onSlugChange,
  canEditSlug = false,
}: RideEditorProps) {
  const { t } = useTranslation()
  const [showRoutePickerModal, setShowRoutePickerModal] = useState(false)
  const [showCreateRouteModal, setShowCreateRouteModal] = useState(false)
  const [pickerTarget, setPickerTarget] = useState<Target | null>(null)

  const form = useForm<RideRequest>({
    validate: zod4Resolver(rideSchema) as any,
    initialValues,
    validateInputOnChange: true,
  })

  const status = form.values.status
  const groups = form.values.groups
  const routeSlug = form.values.routeSlug

  useEffect(() => {
    form.validateField('publishAt')
  }, [status, form])

  const handleAddGroup = () => {
    form.insertListItem('groups', { name: `Groupe ${groups.length + 1}` })
  }

  const handleRemoveGroup = (index: number) => {
    form.removeListItem('groups', index)
  }

  const handleMoveGroup = (index: number, direction: 'up' | 'down') => {
    const newIndex = direction === 'up' ? index - 1 : index + 1
    if (newIndex >= 0 && newIndex < groups.length) {
      form.reorderListItem('groups', { from: index, to: newIndex })
    }
  }

  const handleSubmit = (values: RideRequest) => {
    onSubmit(values)
  }

  return (
    <form onSubmit={form.onSubmit(handleSubmit)}>
      <Stack gap="lg">
        <TextInput
          label={
            <>
              {t('rides.create.form.title.label')}{' '}
              <Text span c="red">
                *
              </Text>
            </>
          }
          placeholder={t('rides.create.form.title.placeholder')}
          {...form.getInputProps('name')}
        />

        {currentSlug && onSlugChange && (
          <SlugEditor
            currentSlug={currentSlug}
            baseUrl={`/teams/${teamSlug}/rides/`}
            onSlugChange={onSlugChange}
            disabled={!canEditSlug}
          />
        )}

        <Stack gap="xs">
          <Text size="sm" fw={500}>
            {t('form.description')}
          </Text>
          <MediaEditor
            value={form.values.media}
            onChange={(val) => form.setFieldValue('media', val)}
            placeholder={t('rides.create.form.description.placeholder')}
            minHeight="150px"
            maxHeight="300px"
            disabled={isPending}
            ariaLabel={t('form.description')}
            teamSlug={teamSlug}
          />
        </Stack>

        <DateTimePicker
          label={
            <>
              {t('startPlace')}{' '}
              <Text span c="red">
                *
              </Text>
            </>
          }
          value={form.values.dateTime ? new Date(form.values.dateTime) : null}
          onChange={(date) => {
            if (date) form.setFieldValue('dateTime', new Date(date).toISOString())
          }}
          error={form.errors.dateTime}
        />

        <SimpleGrid cols={{ base: 1, sm: 2 }}>
          <Stack gap="xs">
            <Text size="sm" fw={500}>
              {t('rides.create.form.startPlace.label')}
            </Text>
            <PlaceAutocomplete
              teamSlug={teamSlug}
              value={form.values.startPlaceId}
              onChange={(placeId) => form.setFieldValue('startPlaceId', placeId)}
              filterStart={true}
              placeholder={t('rides.create.form.startPlace.placeholder')}
            />
          </Stack>
          <Stack gap="xs">
            <Text size="sm" fw={500}>
              {t('rides.create.form.endPlace.label')}
            </Text>
            <PlaceAutocomplete
              teamSlug={teamSlug}
              value={form.values.endPlaceId}
              onChange={(placeId) => form.setFieldValue('endPlaceId', placeId)}
              filterEnd={true}
              placeholder={t('rides.create.form.endPlace.placeholder')}
            />
          </Stack>
        </SimpleGrid>

        {team.visibility !== 'TEAM' && (
          <Radio.Group label={t('visibility.label')} {...form.getInputProps('visibility')}>
            <Stack gap="xs" mt="xs">
              <Radio value="TEAM" label={t('visibility.team')} />
              <Radio value="PUBLIC" label={t('visibility.public')} />
            </Stack>
          </Radio.Group>
        )}

        <Radio.Group label={t('form.status')} {...form.getInputProps('status')}>
          <Stack gap="xs" mt="xs">
            <Radio value="DRAFT" label={t('status.DRAFT')} />
            <Radio value="PUBLISHED" label={t('status.PUBLISHED')} />
            <Radio value="CANCELLED" label={t('status.CANCELLED')} />
          </Stack>
        </Radio.Group>

        {status === Status.DRAFT && (
          <DateTimePicker
            label={t('rides.create.form.publishAt.label')}
            description={t('form.publishAtHint')}
            value={form.values.publishAt ? new Date(form.values.publishAt) : null}
            onChange={(date) =>
              form.setFieldValue('publishAt', date ? new Date(date).toISOString() : undefined)
            }
            error={form.errors.publishAt}
            clearable
          />
        )}

        {/* Route Selection */}
        <Stack gap="xs">
          <Group justify="space-between">
            <Text size="sm" fw={500}>
              {t('rides.create.form.route.label')}
            </Text>
            <Group gap="xs">
              <Button
                variant="subtle"
                size="xs"
                onClick={() => {
                  setPickerTarget({ type: 'ride' })
                  setShowRoutePickerModal(true)
                }}
              >
                {routeSlug ? t('actions.edit') : t('rides.create.form.route.select')}
              </Button>
              {routeSlug && (
                <Button
                  variant="subtle"
                  size="xs"
                  color="danger"
                  onClick={() => form.setFieldValue('routeSlug', undefined)}
                >
                  {t('rides.create.form.route.clear')}
                </Button>
              )}
            </Group>
          </Group>
          {routeSlug ? (
            <RoutePreview routeSlug={routeSlug} teamSlug={teamSlug} />
          ) : (
            <Text size="sm" c="dimmed" fs="italic">
              {t('noRouteSelected')}
            </Text>
          )}
          <Text size="xs" c="dimmed">
            {t('rides.create.form.route.hint')}
          </Text>
        </Stack>

        {/* Groups */}
        <Stack gap="xs">
          <Group justify="space-between">
            <Text size="sm" fw={500}>
              {t('rides.create.form.groups.label')}
            </Text>
            <Button variant="subtle" size="xs" onClick={handleAddGroup}>
              {t('groups.add')}
            </Button>
          </Group>

          <Stack gap="sm">
            {groups.map((group, index) => (
              <Paper key={index} withBorder p="sm">
                <Group justify="space-between" mb="sm">
                  <Group gap="xs">
                    <ReorderControls
                      index={index}
                      total={groups.length}
                      onMove={(dir) => handleMoveGroup(index, dir)}
                    />
                    <Text size="sm" fw={500}>
                      {group.name ||
                        t('rides.create.form.groups.defaultName', { number: index + 1 })}
                    </Text>
                  </Group>
                  <Button
                    variant="subtle"
                    size="xs"
                    color="danger"
                    onClick={() => handleRemoveGroup(index)}
                  >
                    {t('rides.create.form.groups.remove')}
                  </Button>
                </Group>

                <SimpleGrid cols={{ base: 1, xs: 2, sm: 4 }} spacing={{ base: 'xs', sm: 'xs' }}>
                  <TextInput
                    placeholder={t('rides.create.form.groups.name.placeholder')}
                    {...form.getInputProps(`groups.${index}.name`)}
                  />
                  <Box pos="relative">
                    <TimeInput
                      value={form.values.groups[index]?.time || ''}
                      onChange={(e) =>
                        form.setFieldValue(
                          `groups.${index}.time`,
                          e.currentTarget.value || undefined
                        )
                      }
                    />
                    {group.time && (
                      <ActionIcon
                        variant="subtle"
                        size="sm"
                        pos="absolute"
                        right={30}
                        top="50%"
                        style={{ transform: 'translateY(-50%)' }}
                        onClick={() => form.setFieldValue(`groups.${index}.time`, undefined)}
                      >
                        <IconX size={14} />
                      </ActionIcon>
                    )}
                  </Box>
                  <NumberInput
                    placeholder={t('rides.create.form.groups.speed.placeholder')}
                    min={0}
                    value={form.values.groups[index]?.averageSpeed ?? ''}
                    onChange={(val) =>
                      form.setFieldValue(
                        `groups.${index}.averageSpeed`,
                        val === '' ? undefined : Number(val)
                      )
                    }
                  />
                  <NumberInput
                    placeholder={t('rides.create.form.groups.maxParticipants.placeholder')}
                    min={1}
                    value={form.values.groups[index]?.maxParticipants ?? ''}
                    onChange={(val) =>
                      form.setFieldValue(
                        `groups.${index}.maxParticipants`,
                        val === '' ? undefined : Number(val)
                      )
                    }
                  />
                </SimpleGrid>

                <Box
                  mt="sm"
                  pt="sm"
                  style={{ borderTop: '1px solid var(--mantine-color-default-border)' }}
                >
                  <Group justify="space-between" mb={4}>
                    <Text size="xs" c="dimmed">
                      {t('rides.create.form.groups.route.label')}
                    </Text>
                    <Group gap="xs">
                      <Button
                        variant="subtle"
                        size="xs"
                        onClick={() => {
                          setPickerTarget({ type: 'group', index })
                          setShowRoutePickerModal(true)
                        }}
                      >
                        {group.routeSlug ? t('actions.edit') : t('rides.create.form.route.select')}
                      </Button>
                      {group.routeSlug && (
                        <Button
                          variant="subtle"
                          size="xs"
                          color="danger"
                          onClick={() => form.setFieldValue(`groups.${index}.routeSlug`, undefined)}
                        >
                          {t('rides.create.form.route.clear')}
                        </Button>
                      )}
                    </Group>
                  </Group>
                  {group.routeSlug ? (
                    <RoutePreviewCompact routeSlug={group.routeSlug} teamSlug={teamSlug} />
                  ) : (
                    <Text size="xs" c="dimmed" fs="italic">
                      {t('noRouteSelected')}
                    </Text>
                  )}
                </Box>
              </Paper>
            ))}
            {groups.length === 0 && (
              <Text size="sm" c="dimmed" fs="italic">
                {t('rides.create.form.groups.empty')}
              </Text>
            )}
          </Stack>
          <Text size="xs" c="dimmed">
            {t('rides.create.form.groups.hint')}
          </Text>
        </Stack>

        <Group justify="flex-end" pt="md">
          <Button variant="default" onClick={onCancel}>
            {cancelButtonText || t('actions.cancelAction')}
          </Button>
          <Button type="submit" disabled={isPending || !form.isValid()} loading={isPending}>
            {submitButtonText || t('actions.save')}
          </Button>
        </Group>

        <RoutePickerModal
          isOpen={showRoutePickerModal}
          onClose={() => {
            setShowRoutePickerModal(false)
            setPickerTarget(null)
          }}
          onSelect={(route: RouteDto | null) => {
            if (pickerTarget) {
              if (pickerTarget.type === 'ride') {
                form.setFieldValue('routeSlug', route ? route.slug : undefined)
              } else if (pickerTarget.type === 'group') {
                form.setFieldValue(
                  `groups.${pickerTarget.index}.routeSlug`,
                  route ? route.slug : undefined
                )
              }
            }
            setShowRoutePickerModal(false)
            setPickerTarget(null)
          }}
          teamSlug={teamSlug}
          selectedRouteSlug={
            pickerTarget?.type === 'ride'
              ? routeSlug
              : pickerTarget?.type === 'group'
                ? groups[pickerTarget.index]?.routeSlug
                : null
          }
          title={
            pickerTarget?.type === 'ride'
              ? t('rides.create.form.route.selectForRide')
              : t('rides.create.form.route.selectForGroup')
          }
          onCreateNew={() => {
            setShowRoutePickerModal(false)
            setShowCreateRouteModal(true)
          }}
        />

        <CreateRouteModal
          isOpen={showCreateRouteModal}
          onClose={() => {
            setShowCreateRouteModal(false)
            setPickerTarget(null)
          }}
          onRouteCreated={(route: RouteDto) => {
            if (pickerTarget) {
              if (pickerTarget.type === 'ride') {
                form.setFieldValue('routeSlug', route.slug)
              } else if (pickerTarget.type === 'group') {
                form.setFieldValue(`groups.${pickerTarget.index}.routeSlug`, route.slug)
              }
            }
            setShowCreateRouteModal(false)
            setPickerTarget(null)
          }}
          team={team}
        />
      </Stack>
    </form>
  )
}
