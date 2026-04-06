import { useState } from 'react'
import { useParams, Navigate, useNavigate } from 'react-router-dom'
import { useCanonicalPath } from '../../hooks/useCanonicalPath'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { notifications } from '@mantine/notifications'
import i18next from 'i18next'
import { IconCopy } from '@tabler/icons-react'
import { Container, Stack, Group, Title, Text, Button } from '@mantine/core'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import { useCreateRide } from '../../api/endpoints/rides/rides'
import { getListPublicationsQueryKey } from '../../api/endpoints/publications/publications'
import { Status } from '@/api/dto'
import type { RideRequest, RideTemplateDto } from '@/api/dto'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { RideEditor } from '../../components/ride/RideEditor'
import { RideTemplatePickerModal } from '../../components/ridetemplate/RideTemplatePickerModal'
import { defaultMedia } from '@/lib/apiUtils'
import { paths } from '@/config/paths'

export function CreateRidePage() {
  const { t } = useTranslation()
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const { data: team, isLoading: isLoadingTeam } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })

  const createMutation = useCreateRide()

  const [showTemplateModal, setShowTemplateModal] = useState(false)
  const [editorKey, setEditorKey] = useState(0)
  const [templateValues, setTemplateValues] = useState<RideTemplateDto | null>(null)

  useCanonicalPath(team ? paths.rideNew(team.slug) : undefined)

  if (isLoadingTeam) {
    return <LoadingPage message={t('loading')} />
  }

  if (!team) {
    return <Navigate to={paths.teams()} replace />
  }

  if (!team.enableRides || !team.enableRoutes) {
    return <Navigate to={paths.team(teamSlug!)} replace />
  }

  const canCreate = team.role === 'ADMIN' || team.role === 'ORGANIZER'

  if (!canCreate) {
    return <Navigate to={paths.team(teamSlug!)} replace />
  }

  // Calculate next Sunday at 8am
  const getNextSunday = () => {
    const today = new Date()
    const daysUntilSunday = (7 - today.getDay()) % 7 || 7
    const nextSunday = new Date(today)
    nextSunday.setDate(today.getDate() + daysUntilSunday)
    nextSunday.setHours(8, 0, 0, 0)
    return nextSunday.toISOString()
  }

  // Prepare initial values - use template values if available
  const initialValues = templateValues
    ? {
        ...templateValues,
        media: {
          markdown: templateValues.markdown,
          assets: defaultMedia().assets,
        },
        dateTime: getNextSunday(),
        publishAt: undefined,
        routeSlug: undefined,
      }
    : {
        name: '',
        media: defaultMedia(),
        dateTime: getNextSunday(),
        visibility: team.visibility,
        status: Status.DRAFT,
        publishAt: undefined,
        routeSlug: undefined,
        groups: [
          {
            name: t('rides.create.form.groups.defaultName', { number: 1 }),
            time: undefined,
            averageSpeed: undefined,
            maxParticipants: undefined,
            routeSlug: undefined,
            isNew: true,
          },
        ],
      }

  const handleTemplateSelect = (template: RideTemplateDto) => {
    setTemplateValues({
      ...template,
      groups: template.groups.map((g) => ({
        ...g,
        routeSlug: undefined,
        isNew: true,
      })),
    })
    setEditorKey((prev) => prev + 1)
    setShowTemplateModal(false)
  }

  const handleSubmit = (data: RideRequest) => {
    const filteredGroups = data.groups.filter((g) => g.name.trim())
    createMutation.mutate(
      {
        teamSlug: teamSlug!,
        data: {
          ...data,
          groups: filteredGroups,
        },
      },
      {
        onSuccess: (ride) => {
          queryClient.invalidateQueries({ queryKey: getListPublicationsQueryKey(teamSlug!) })
          notifications.show({ message: i18next.t('rides.notifications.created'), color: 'green' })
          navigate(paths.ride(teamSlug!, ride.slug))
        },
      }
    )
  }

  return (
    <Container size="sm" py="xl">
      <Stack>
        <Group justify="space-between" align="center">
          <Title order={1}>{t('rides.create.title')}</Title>
          <Button
            variant="light"
            leftSection={<IconCopy size={16} />}
            onClick={() => setShowTemplateModal(true)}
          >
            {t('rides.create.loadTemplate')}
          </Button>
        </Group>
        <Text c="dimmed">{t('rides.create.subtitle', { teamName: team.name })}</Text>
      </Stack>

      <RideEditor
        key={editorKey}
        team={team}
        teamSlug={teamSlug!}
        initialValues={initialValues}
        onSubmit={handleSubmit}
        onCancel={() => navigate(paths.team(teamSlug!))}
        isPending={createMutation.isPending}
        submitButtonText={t('rides.create.button')}
      />

      <RideTemplatePickerModal
        isOpen={showTemplateModal}
        onClose={() => setShowTemplateModal(false)}
        onSelect={handleTemplateSelect}
        teamSlug={teamSlug!}
      />
    </Container>
  )
}
