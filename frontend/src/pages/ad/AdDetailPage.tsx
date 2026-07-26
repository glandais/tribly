import { useState } from 'react'
import { Link, useParams, useNavigate, Navigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { notifications } from '@mantine/notifications'
import i18next from 'i18next'
import {
  IconCalendar,
  IconPencil,
  IconMail,
  IconChevronDown,
  IconMapPin,
  IconCurrencyEuro,
} from '@tabler/icons-react'
import {
  Box,
  Button,
  Menu,
  Container,
  Paper,
  Group,
  Stack,
  Title,
  Text,
  Badge,
  Loader,
} from '@mantine/core'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import {
  useGetAd,
  useUpdateAd,
  useDeleteAd,
  useUndeleteAd,
  getListAdsQueryKey,
  getGetAdQueryKey,
} from '../../api/endpoints/ads/ads'
import { QueryStateBoundary } from '../../components/common/QueryStateBoundary'
import { AdContactModal } from '../../components/ad/AdContactModal'
import { useAuth } from '../../hooks/useAuth'
import { DetailPageSkeleton } from '../../components/common/DetailPageSkeleton'
import { ConfirmDialog } from '../../components/common/ConfirmDialog'
import { MediaDisplay } from '../../components/common/MediaDisplay'
import { EntityLogo } from '../../components/common/EntityLogo'
import { TeamContextBanner } from '../../components/team/TeamContextBanner'
import { useFormattedDate } from '../../utils/dateFormat'
import { paths } from '@/config/paths'
import { AdType, RentalPeriod, Status } from '../../api/dto'
import { useCanonicalPath } from '../../hooks/useCanonicalPath'

const statusColors: Record<Status, 'gray' | 'green' | 'red'> = {
  [Status.DRAFT]: 'gray',
  [Status.PUBLISHED]: 'green',
  [Status.CANCELLED]: 'red',
}

const adTypeColors: Record<AdType, 'primary' | 'grape' | 'yellow'> = {
  [AdType.SALE]: 'primary',
  [AdType.RENTAL]: 'grape',
  [AdType.WANTED]: 'yellow',
}

export function AdDetailPage() {
  const { t } = useTranslation()
  const { formatDateTime } = useFormattedDate()
  const { teamSlug, adSlug } = useParams<{ teamSlug: string; adSlug: string }>()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const [showUnpublishConfirm, setShowUnpublishConfirm] = useState(false)
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false)
  const [contactOpen, setContactOpen] = useState(false)
  const { user } = useAuth()

  const { data: team, isLoading: isLoadingTeam } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })
  const {
    data: ad,
    isLoading: isLoadingAd,
    error,
    refetch,
  } = useGetAd(teamSlug!, adSlug!, { query: { enabled: !!teamSlug && !!adSlug } })

  useCanonicalPath(team && ad ? paths.ad(team.slug, ad.slug) : undefined)

  const updateMutation = useUpdateAd()
  const deleteMutation = useDeleteAd()
  const undeleteMutation = useUndeleteAd()

  if (!isLoadingTeam && !team) {
    return <Navigate to={paths.teams()} replace />
  }

  if (isLoadingTeam || isLoadingAd || error || !ad) {
    return (
      <Container size="xl" py="xl">
        <QueryStateBoundary
          isLoading={isLoadingTeam || isLoadingAd}
          isError={!!error}
          error={error}
          isNotFound={!ad}
          onRetry={() => void refetch()}
          skeleton={<DetailPageSkeleton withMap={false} />}
          notFound={{
            title: t('ads.detail.notFound.title'),
            message: t('ads.detail.notFound.message'),
            backTo: paths.ads(teamSlug!),
            backLabel: t('ads.title'),
          }}
        >
          {null}
        </QueryStateBoundary>
      </Container>
    )
  }

  const isAdmin = team?.role === 'ADMIN'
  // The relay is open to anyone who may read the ad, i.e. a team member — except its author,
  // who has no one to write to. `AD_CONTACT_SELF` therefore never needs a rendering.
  const canContactAuthor = !!team?.role && ad.createdById !== user?.id
  // Note: Full creator check would require comparing createdById with current user ID
  // For now, backend handles authorization, frontend shows edit for all members
  const canEdit = isAdmin || !!team?.role

  const formattedDate = formatDateTime(ad.createdAt)

  const formatPrice = (price: number | undefined, adType: AdType, rentalPeriod?: RentalPeriod) => {
    if (price === undefined || price === null) {
      return t('ads.detail.priceNegotiable')
    }
    const formattedPrice = new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: 'EUR',
    }).format(price)

    if (adType === AdType.RENTAL && rentalPeriod) {
      return `${formattedPrice} / ${t(`ads.rentalPeriod.${rentalPeriod satisfies 'DAY' | 'WEEK' | 'MONTH'}`).toLowerCase()}`
    }
    return formattedPrice
  }

  const invalidateAds = () => {
    queryClient.invalidateQueries({ queryKey: getListAdsQueryKey(teamSlug!) })
    queryClient.invalidateQueries({ queryKey: getGetAdQueryKey(teamSlug!, adSlug!) })
  }

  const handlePublish = () => {
    updateMutation.mutate(
      { teamSlug: teamSlug!, slug: adSlug!, data: { ...ad, status: Status.PUBLISHED } },
      {
        onSuccess: () => {
          invalidateAds()
          notifications.show({ message: i18next.t('ads.notifications.updated'), color: 'green' })
        },
      }
    )
  }

  const handleUnpublish = () => {
    updateMutation.mutate(
      { teamSlug: teamSlug!, slug: adSlug!, data: { ...ad, status: Status.DRAFT } },
      {
        onSuccess: () => {
          invalidateAds()
          notifications.show({ message: i18next.t('ads.notifications.updated'), color: 'green' })
          setShowUnpublishConfirm(false)
        },
      }
    )
  }

  const handleDelete = () => {
    deleteMutation.mutate(
      { teamSlug: teamSlug!, slug: adSlug! },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getListAdsQueryKey(teamSlug!) })
          notifications.show({ message: i18next.t('ads.notifications.deleted'), color: 'green' })
          setShowDeleteConfirm(false)
          navigate(paths.ads(teamSlug!))
        },
      }
    )
  }

  const handleRestore = () => {
    undeleteMutation.mutate(
      { teamSlug: teamSlug!, slug: adSlug! },
      {
        onSuccess: () => {
          invalidateAds()
          notifications.show({ message: i18next.t('ads.notifications.restored'), color: 'green' })
        },
      }
    )
  }

  return (
    <Container size="md" py="xl">
      {/* Way back to the team: this page is not mounted inside TeamLayout */}
      {team && <TeamContextBanner team={team} />}

      {/* Header */}
      <Paper withBorder p="lg" mb="lg">
        <Group justify="space-between" align="flex-start" wrap="wrap">
          <Group style={{ minWidth: 0 }}>
            <EntityLogo logo={ad.media.assets.logo} alt={ad.name} size="lg" />
            <Stack gap={4} style={{ minWidth: 0 }}>
              <Title order={2} lineClamp={1}>
                {ad.name}
              </Title>
              <Group gap="xs">
                <Badge color={adTypeColors[ad.adType]}>
                  {t(`ads.adType.${ad.adType satisfies 'SALE' | 'RENTAL' | 'WANTED'}`)}
                </Badge>
                <Badge color={statusColors[ad.status]}>
                  {t(`status.${ad.status satisfies 'DRAFT' | 'PUBLISHED' | 'CANCELLED'}`)}
                </Badge>
              </Group>
            </Stack>
          </Group>

          {canContactAuthor && (
            <Button
              leftSection={<IconMail size={16} />}
              onClick={() => setContactOpen(true)}
              mr="sm"
            >
              {t('ads.contact.button')}
            </Button>
          )}

          {canEdit && (
            <Button.Group>
              <Button
                component={Link}
                to={paths.adEdit(teamSlug!, adSlug!)}
                variant="default"
                leftSection={<IconPencil size={16} />}
              >
                {t('actions.edit')}
              </Button>
              <Menu position="bottom-end">
                <Menu.Target>
                  <Button variant="default" px="xs">
                    <IconChevronDown size={16} />
                  </Button>
                </Menu.Target>
                <Menu.Dropdown>
                  {ad.status === Status.DRAFT && (
                    <Menu.Item
                      onClick={handlePublish}
                      disabled={updateMutation.isPending}
                      color="success"
                      leftSection={updateMutation.isPending ? <Loader size="sm" /> : undefined}
                    >
                      {t('actions.publish')}
                    </Menu.Item>
                  )}
                  {ad.status === Status.PUBLISHED && (
                    <Menu.Item onClick={() => setShowUnpublishConfirm(true)} color="warning">
                      {t('actions.unpublish')}
                    </Menu.Item>
                  )}
                  {ad.deleted && (
                    <Menu.Item
                      onClick={handleRestore}
                      color="green"
                      disabled={undeleteMutation.isPending}
                    >
                      {t('actions.restore')}
                    </Menu.Item>
                  )}
                  <Menu.Divider />
                  <Menu.Item onClick={() => setShowDeleteConfirm(true)} color="danger">
                    {t('actions.delete')}
                  </Menu.Item>
                </Menu.Dropdown>
              </Menu>
            </Button.Group>
          )}
        </Group>

        {/* Price */}
        <Box
          mt="lg"
          p="md"
          bg="var(--mantine-color-body)"
          style={{ borderRadius: 'var(--mantine-radius-md)' }}
        >
          <Group gap="xs">
            <IconCurrencyEuro size={24} />
            <Text size="xl" fw={700}>
              {formatPrice(ad.price, ad.adType, ad.rentalPeriod)}
            </Text>
          </Group>
        </Box>

        {/* Description */}
        <Box mt="md">
          <MediaDisplay media={ad.media} />
        </Box>

        {/* Meta info */}
        <Group mt="md">
          <Group gap="xs">
            <IconCalendar size={16} />
            <Text size="sm" c="dimmed">
              {formattedDate}
            </Text>
          </Group>
          {ad.locationDescription && (
            <Group gap="xs">
              <IconMapPin size={16} />
              <Text size="sm" c="dimmed">
                {ad.locationDescription}
              </Text>
            </Group>
          )}
        </Group>
      </Paper>

      {/* Confirmation Dialogs */}
      <ConfirmDialog
        isOpen={showUnpublishConfirm}
        onClose={() => setShowUnpublishConfirm(false)}
        onConfirm={handleUnpublish}
        title={t('actions.unpublish')}
        message={t('ads.detail.confirmations.unpublish')}
        confirmText={t('actions.unpublish')}
        variant="warning"
        isLoading={updateMutation.isPending}
      />
      <AdContactModal
        opened={contactOpen}
        onClose={() => setContactOpen(false)}
        teamSlug={teamSlug!}
        adSlug={adSlug!}
      />

      <ConfirmDialog
        isOpen={showDeleteConfirm}
        onClose={() => setShowDeleteConfirm(false)}
        onConfirm={handleDelete}
        title={t('actions.delete')}
        message={t('ads.detail.confirmations.delete')}
        confirmText={t('actions.delete')}
        variant="danger"
        isLoading={deleteMutation.isPending}
      />
    </Container>
  )
}
