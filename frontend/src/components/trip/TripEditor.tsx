import { useState, useEffect } from 'react'
import { useForm } from '@mantine/form'
import { zod4Resolver } from 'mantine-form-zod-resolver'
import { useTranslation } from 'react-i18next'
import { TextInput, Radio, Stack, Group, Button, Text, Paper, SimpleGrid } from '@mantine/core'
import { DateTimePicker } from '@mantine/dates'
import { IconPlus } from '@tabler/icons-react'
import { SlugEditor } from '../common/SlugEditor'
import { ReorderControls } from '../common/ReorderControls'
import { RoutePickerModal } from '../route/RoutePickerModal'
import { CreateRouteModal } from '../route/CreateRouteModal'
import { RoutePreview } from '../route/RoutePreview'
import { RoutePreviewCompact } from '../route/RoutePreviewCompact'
import { MediaEditor } from '../common/MediaEditor'
import { PlaceAutocomplete } from '../common/PlaceAutocomplete'
import type { RouteDto, TeamDetailDto, TripRequest } from '@/api/dto'
import { Status } from '@/api/dto'
import { defaultMedia } from '@/lib/apiUtils'
import { createTripBody } from '@/api/zod/trips/trips.zod'

const tripSchema = createTripBody.refine(
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

interface TripEditorProps {
  team: TeamDetailDto
  teamSlug: string
  initialValues: TripRequest
  onSubmit: (data: TripRequest) => void | Promise<void>
  onCancel: () => void
  isPending: boolean
  submitButtonText?: string
  cancelButtonText?: string
  currentSlug?: string
  onSlugChange?: (newSlug: string) => Promise<void>
  canEditSlug?: boolean
}

type Target = { type: 'stage'; index: number } | { type: 'trip' }

export function TripEditor({
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
}: TripEditorProps) {
  const { t } = useTranslation()

  const [showRoutePickerModal, setShowRoutePickerModal] = useState(false)
  const [showCreateRouteModal, setShowCreateRouteModal] = useState(false)
  const [pickerTarget, setPickerTarget] = useState<Target | null>(null)

  const form = useForm<TripRequest>({
    validate: zod4Resolver(tripSchema) as any,
    initialValues,
    validateInputOnChange: true,
  })

  const status = form.values.status
  const dateTime = form.values.dateTime
  const stages = form.values.stages
  const routeSlug = form.values.routeSlug

  useEffect(() => {
    form.validateField('publishAt')
  }, [status, form])

  const handleAddStage = () => {
    const newStageDate = new Date(dateTime || new Date().toISOString())
    newStageDate.setDate(newStageDate.getDate() + stages.length)

    form.insertListItem('stages', {
      name: `Jour ${stages.length + 1}`,
      dateTime: newStageDate.toISOString(),
      media: defaultMedia(),
    })
  }

  const handleRemoveStage = (index: number) => {
    form.removeListItem('stages', index)
  }

  const handleMoveStage = (index: number, direction: 'up' | 'down') => {
    const newIndex = direction === 'up' ? index - 1 : index + 1
    if (newIndex >= 0 && newIndex < stages.length) {
      form.reorderListItem('stages', { from: index, to: newIndex })
    }
  }

  const handleSubmit = (values: TripRequest) => {
    onSubmit(values)
  }

  return (
    <form onSubmit={form.onSubmit(handleSubmit)}>
      <Stack gap="lg">
        <TextInput
          label={
            <>
              {t('trips.create.form.title.label')}{' '}
              <Text span c="red">
                *
              </Text>
            </>
          }
          placeholder={t('trips.create.form.title.placeholder')}
          {...form.getInputProps('name')}
        />

        {currentSlug && onSlugChange && (
          <SlugEditor
            currentSlug={currentSlug}
            baseUrl={`/teams/${teamSlug}/trips/`}
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
            placeholder={t('trips.create.form.description.placeholder')}
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
            label={t('trips.create.form.publishAt.label')}
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
              {t('trips.create.form.route.label')}
            </Text>
            <Group gap="xs">
              <Button
                variant="subtle"
                size="xs"
                onClick={() => {
                  setPickerTarget({ type: 'trip' })
                  setShowRoutePickerModal(true)
                }}
              >
                {routeSlug ? t('actions.edit') : t('trips.create.form.route.select')}
              </Button>
              {routeSlug && (
                <Button
                  variant="subtle"
                  size="xs"
                  color="red"
                  onClick={() => form.setFieldValue('routeSlug', undefined)}
                >
                  {t('trips.create.form.route.clear')}
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
            {t('trips.create.form.route.hint')}
          </Text>
        </Stack>

        {/* Stages */}
        <Stack gap="xs">
          <Group justify="space-between">
            <Text size="sm" fw={500}>
              {t('trips.detail.stages.title')}
            </Text>
            <Button
              variant="subtle"
              size="xs"
              leftSection={<IconPlus size={16} />}
              onClick={handleAddStage}
            >
              {t('trips.create.form.stages.add')}
            </Button>
          </Group>

          <Stack gap="sm">
            {stages.map((stage, index) => (
              <Paper key={index} withBorder p="sm">
                <Group justify="space-between" mb="sm">
                  <Group gap="xs">
                    <ReorderControls
                      index={index}
                      total={stages.length}
                      onMove={(dir) => handleMoveStage(index, dir)}
                    />
                    <Text size="sm" fw={500}>
                      {stage.name ||
                        t('trips.create.form.stages.defaultName', { number: index + 1 })}
                    </Text>
                  </Group>
                  <Button
                    variant="subtle"
                    size="xs"
                    color="red"
                    onClick={() => handleRemoveStage(index)}
                  >
                    {t('trips.create.form.stages.remove')}
                  </Button>
                </Group>

                <SimpleGrid cols={{ base: 1, sm: 2 }} spacing="xs" mb="sm">
                  <TextInput
                    placeholder={t('trips.create.form.stages.name.placeholder')}
                    {...form.getInputProps(`stages.${index}.name`)}
                  />
                  <DateTimePicker
                    value={
                      form.values.stages[index]?.dateTime
                        ? new Date(form.values.stages[index].dateTime)
                        : null
                    }
                    onChange={(date) => {
                      if (date)
                        form.setFieldValue(`stages.${index}.dateTime`, new Date(date).toISOString())
                    }}
                  />
                </SimpleGrid>

                <SimpleGrid cols={{ base: 1, sm: 2 }} spacing="xs" mb="sm">
                  <Stack gap={4}>
                    <Text size="xs" c="dimmed">
                      {t('trips.create.form.stages.startPlace.label')}
                    </Text>
                    <PlaceAutocomplete
                      teamSlug={teamSlug}
                      value={stage.startPlaceId}
                      onChange={(placeId) =>
                        form.setFieldValue(`stages.${index}.startPlaceId`, placeId)
                      }
                      filterStart={true}
                      placeholder={t('trips.create.form.stages.startPlace.placeholder')}
                    />
                  </Stack>
                  <Stack gap={4}>
                    <Text size="xs" c="dimmed">
                      {t('trips.create.form.stages.endPlace.label')}
                    </Text>
                    <PlaceAutocomplete
                      teamSlug={teamSlug}
                      value={stage.endPlaceId}
                      onChange={(placeId) =>
                        form.setFieldValue(`stages.${index}.endPlaceId`, placeId)
                      }
                      filterEnd={true}
                      placeholder={t('trips.create.form.stages.endPlace.placeholder')}
                    />
                  </Stack>
                </SimpleGrid>

                <Stack gap={4} mb="sm">
                  <Text size="xs" c="dimmed">
                    {t('form.description')}
                  </Text>
                  <MediaEditor
                    value={form.values.stages[index]?.media}
                    onChange={(val) => form.setFieldValue(`stages.${index}.media`, val)}
                    placeholder={t('trips.create.form.stages.description.placeholder')}
                    minHeight="80px"
                    maxHeight="150px"
                    disabled={isPending}
                    ariaLabel={t('form.description')}
                    teamSlug={teamSlug}
                  />
                </Stack>

                <Stack
                  gap={4}
                  pt="sm"
                  style={{ borderTop: '1px solid var(--mantine-color-default-border)' }}
                >
                  <Group justify="space-between">
                    <Text size="xs" c="dimmed">
                      {t('trips.create.form.stages.route.label')}
                    </Text>
                    <Group gap="xs">
                      <Button
                        variant="subtle"
                        size="xs"
                        onClick={() => {
                          setPickerTarget({ type: 'stage', index })
                          setShowRoutePickerModal(true)
                        }}
                      >
                        {stage.routeSlug ? t('actions.edit') : t('trips.create.form.route.select')}
                      </Button>
                      {stage.routeSlug && (
                        <Button
                          variant="subtle"
                          size="xs"
                          color="red"
                          onClick={() => form.setFieldValue(`stages.${index}.routeSlug`, undefined)}
                        >
                          {t('trips.create.form.route.clear')}
                        </Button>
                      )}
                    </Group>
                  </Group>
                  {stage.routeSlug ? (
                    <RoutePreviewCompact routeSlug={stage.routeSlug} teamSlug={teamSlug} />
                  ) : (
                    <Text size="xs" c="dimmed" fs="italic">
                      {t('noRouteSelected')}
                    </Text>
                  )}
                </Stack>
              </Paper>
            ))}
            {stages.length === 0 && (
              <Text size="sm" c="dimmed" fs="italic">
                {t('trips.create.form.stages.empty')}
              </Text>
            )}
          </Stack>
          <Text size="xs" c="dimmed">
            {t('trips.create.form.stages.hint')}
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
              if (pickerTarget.type === 'trip') {
                form.setFieldValue('routeSlug', route ? route.slug : undefined)
              } else if (pickerTarget.type === 'stage') {
                form.setFieldValue(
                  `stages.${pickerTarget.index}.routeSlug`,
                  route ? route.slug : undefined
                )
              }
            }
            setShowRoutePickerModal(false)
            setPickerTarget(null)
          }}
          teamSlug={teamSlug}
          selectedRouteSlug={
            pickerTarget?.type === 'trip'
              ? routeSlug
              : pickerTarget?.type === 'stage'
                ? stages[pickerTarget.index]?.routeSlug
                : null
          }
          title={
            pickerTarget?.type === 'trip'
              ? t('trips.create.form.route.selectForTrip')
              : t('trips.create.form.route.selectForStage')
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
              if (pickerTarget.type === 'trip') {
                form.setFieldValue('routeSlug', route.slug)
              } else if (pickerTarget.type === 'stage') {
                form.setFieldValue(`stages.${pickerTarget.index}.routeSlug`, route.slug)
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
