import { useState, useEffect, useMemo } from 'react'
import { useForm } from '@mantine/form'
import { zodFormValidator } from '@/lib/formUtils'
import { useTranslation } from 'react-i18next'
import { useUnits } from '@/hooks/useUnits'
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
import { useRoutesBulk } from '@/hooks/useRoutesBulk'
import type { RouteDto, RouteDetailDto, TeamDetailDto, RideRequest, PublicUserDto } from '@/api/dto'
import { UserAutocomplete } from '../common/UserAutocomplete'
import { UserAvatar } from '../common/UserAvatar'
import { Status } from '@/api/dto'
import { CreateRideBody } from '@/api/zod/rides/rides.zod'

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
  /** Display names of the leaders already designated, keyed by user id, for the edit form. */
  initialLeaders?: Record<string, PublicUserDto>
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
  initialLeaders,
}: RideEditorProps) {
  const { t } = useTranslation()
  const { config, speedToDisplay, speedFromDisplay } = useUnits()
  const [showRoutePickerModal, setShowRoutePickerModal] = useState(false)
  const [showCreateRouteModal, setShowCreateRouteModal] = useState(false)
  const [pickerTarget, setPickerTarget] = useState<Target | null>(null)

  const rideSchema = useMemo(
    () =>
      CreateRideBody.refine(
        (data) => {
          if (data.status === Status.DRAFT && data.publishAt) {
            return new Date(data.publishAt) > new Date()
          }
          return true
        },
        {
          message: t('form.publishAt.futureRequired'),
          path: ['publishAt'],
        }
      ),
    [t]
  )

  const form = useForm<RideRequest>({
    validate: zodFormValidator<RideRequest>(rideSchema),
    initialValues,
    validateInputOnChange: true,
  })

  const status = form.values.status
  const groups = form.values.groups

  // The picker yields a whole user but the request only carries an id, so names are kept here
  // to render a chosen leader without a round trip. Seeded from the ride being edited.
  const [leaderNames, setLeaderNames] = useState<Record<string, PublicUserDto>>(
    () => initialLeaders ?? {}
  )
  const routeSlug = form.values.routeSlug

  // One bulk request for every group's route preview, instead of one `getRoute` per group —
  // groups frequently share the same route. The key string is memoized on the slugs' own
  // values (not the `groups` array reference, which mantine's `useForm` may recreate on
  // unrelated field edits) so the request stays stable across renders that don't touch routes.
  const groupRouteSlugsKey = groups
    .map((g) => g.routeSlug)
    .filter((s): s is string => !!s)
    .sort()
    .join(',')
  const groupRouteSlugs = useMemo(
    () => (groupRouteSlugsKey ? Array.from(new Set(groupRouteSlugsKey.split(','))) : []),
    [groupRouteSlugsKey]
  )
  const {
    data: groupRoutesBulk,
    isLoading: isLoadingGroupRoutes,
    isFetching: isFetchingGroupRoutes,
    failedSlugs: failedGroupRouteSlugs,
  } = useRoutesBulk(teamSlug, {
    // Only the name, distance and elevation gain are shown — ask for no geometry at all rather
    // than downloading each group's track to throw it away.
    slug: groupRouteSlugs,
    geometry: false,
  })
  const groupRoutesBySlug = useMemo(() => {
    const map = new Map<string, RouteDetailDto>()
    for (const route of groupRoutesBulk?.routes ?? []) {
      map.set(route.slug, route)
    }
    return map
  }, [groupRoutesBulk])
  // `useRoutesBulk` keeps previous data while the slug set changes, so `isLoadingGroupRoutes`
  // alone would only be true on first mount — picking a new route for one group must still show
  // a spinner for THAT row rather than briefly rendering nothing (no match in `groupRoutesBySlug`
  // yet) or stale data for a different route. Rows whose slug is already resolved keep rendering
  // from the previous data untouched. A slug whose chunk failed (`failedGroupRouteSlugs`) is never
  // "loading" — even while another chunk is still in flight — so its row falls through to
  // `RoutePreviewCompact`'s "not found" state instead of spinning forever.
  const isGroupRouteLoading = (slug: string | undefined) =>
    !!slug &&
    !groupRoutesBySlug.has(slug) &&
    !failedGroupRouteSlugs.has(slug) &&
    (isLoadingGroupRoutes || isFetchingGroupRoutes)

  useEffect(() => {
    form.validateField('publishAt')
  }, [status, form])

  const handleAddGroup = () => {
    form.insertListItem('groups', {
      name: t('rides.create.form.groups.defaultName', { number: groups.length + 1 }),
    })
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
      <Stack>
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
              <Radio value="PUBLIC_UNLISTED" label={t('visibility.public_unlisted')} />
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
                    placeholder={t('rides.create.form.groups.speed.placeholder', {
                      unit: config.speedUnit,
                    })}
                    min={0}
                    suffix={` ${config.speedUnit}`}
                    value={speedToDisplay(form.values.groups[index]?.averageSpeed) ?? ''}
                    onChange={(val) =>
                      form.setFieldValue(`groups.${index}.averageSpeed`, speedFromDisplay(val))
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
                  <Text size="xs" c="dimmed" mb={4}>
                    {t('rides.create.form.groups.leader.label')}
                  </Text>
                  {group.leaderId ? (
                    <Group gap="xs" wrap="nowrap">
                      {/* The name is cached on pick and seeded on edit; guard anyway, since
                          UserAvatar dereferences displayName and an id alone would crash it. */}
                      {leaderNames[group.leaderId] && (
                        <UserAvatar user={leaderNames[group.leaderId]} size="sm" />
                      )}
                      <Text size="sm" style={{ flex: 1, minWidth: 0 }} truncate>
                        {leaderNames[group.leaderId]?.displayName ??
                          t('rides.create.form.groups.leader.unknown')}
                      </Text>
                      <Button
                        variant="subtle"
                        size="xs"
                        color="danger"
                        onClick={() => form.setFieldValue(`groups.${index}.leaderId`, undefined)}
                      >
                        {t('rides.create.form.groups.leader.clear')}
                      </Button>
                    </Group>
                  ) : (
                    <UserAutocomplete
                      teamSlug={teamSlug}
                      placeholder={t('rides.create.form.groups.leader.placeholder')}
                      onSelect={(user) => {
                        setLeaderNames((previous) => ({ ...previous, [user.id]: user }))
                        form.setFieldValue(`groups.${index}.leaderId`, user.id)
                      }}
                    />
                  )}
                </Box>

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
                    <RoutePreviewCompact
                      route={groupRoutesBySlug.get(group.routeSlug)}
                      isLoading={isGroupRouteLoading(group.routeSlug)}
                    />
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
