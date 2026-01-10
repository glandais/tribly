import { useState } from 'react'
import { Link, Navigate, useParams } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { keepPreviousData } from '@tanstack/react-query'
import { IconPlus, IconSearch } from '@tabler/icons-react'
import {
  Button,
  TextInput,
  Select,
  Stack,
  Group,
  Title,
  Text,
  SimpleGrid,
  Box,
  Paper,
  Center,
} from '@mantine/core'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import { useListAds } from '../../api/endpoints/ads/ads'
import { AdCard, AdCardSkeleton } from '../../components/ad'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { Pagination } from '../../components/common/Pagination'
import { TeamLayout } from '../../components/team/TeamLayout'
import { paths } from '@/config/paths'
import { AdType } from '../../api/dto'
import { useCanonicalPath } from '../../hooks/useCanonicalPath'

const PAGE_SIZE = 20

export function AdListPage() {
  const { t } = useTranslation()
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const [search, setSearch] = useState('')
  const [adType, setAdType] = useState<AdType | undefined>(undefined)
  const [page, setPage] = useState(0)

  const { data: team, isLoading: isLoadingTeam } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })
  const {
    data: adsResponse,
    isLoading: isLoadingAds,
    isFetching,
  } = useListAds(
    teamSlug!,
    {
      search: search || undefined,
      adType,
      page,
      size: PAGE_SIZE,
    },
    { query: { enabled: !!teamSlug, placeholderData: keepPreviousData } }
  )

  useCanonicalPath(team ? paths.ads(team.slug) : undefined)

  if (isLoadingTeam) {
    return <LoadingPage message={t('loading')} />
  }

  if (!team) {
    return <Navigate to={paths.teams()} replace />
  }

  const isMember = !!team.role
  const ads = adsResponse?.ads || []
  const totalPages = adsResponse ? Math.ceil(adsResponse.total / PAGE_SIZE) : 0

  const handleSearchChange = (value: string) => {
    setSearch(value)
    setPage(0)
  }

  const handleAdTypeChange = (value: string) => {
    setAdType(value === 'ALL' ? undefined : (value as AdType))
    setPage(0)
  }

  return (
    <TeamLayout team={team} currentTab="ads">
      <Stack gap="lg">
        {/* Header */}
        <Group justify="space-between" align="center" wrap="wrap">
          <Title order={2}>{t('ads.title')}</Title>
          {isMember && (
            <Button
              component={Link}
              to={paths.adNew(teamSlug!)}
              leftSection={<IconPlus size={20} />}
            >
              {t('ads.list.createAd')}
            </Button>
          )}
        </Group>

        {/* Filters */}
        <Group gap="md" align="flex-end" wrap="wrap">
          <TextInput
            style={{ flex: 1, minWidth: 200 }}
            placeholder={t('ads.list.search.placeholder')}
            value={search}
            onChange={(e: React.ChangeEvent<HTMLInputElement>) =>
              handleSearchChange(e.target.value)
            }
            leftSection={<IconSearch size={20} />}
            aria-label={t('ads.list.search.label')}
          />
          <Select
            w={{ base: '100%', sm: 180 }}
            value={adType || 'ALL'}
            onChange={(value: string | null) => handleAdTypeChange(value || 'ALL')}
            placeholder={t('ads.list.filterByType')}
            data={[
              { value: 'ALL', label: t('ads.list.allTypes') },
              { value: AdType.SALE, label: t('ads.adType.SALE') },
              { value: AdType.RENTAL, label: t('ads.adType.RENTAL') },
              { value: AdType.WANTED, label: t('ads.adType.WANTED') },
            ]}
          />
        </Group>

        {/* Content */}
        {isLoadingAds ? (
          <SimpleGrid cols={{ base: 1, md: 2 }} spacing="md">
            {Array.from({ length: 4 }).map((_, i) => (
              <AdCardSkeleton key={i} />
            ))}
          </SimpleGrid>
        ) : ads.length === 0 ? (
          <Paper withBorder p="xl" radius="md">
            <Center>
              <Stack align="center" gap="sm">
                <Title order={3}>{t('ads.list.empty.title')}</Title>
                <Text c="dimmed">
                  {search || adType ? t('ads.list.noResults') : t('ads.list.empty.member')}
                </Text>
              </Stack>
            </Center>
          </Paper>
        ) : (
          <Stack gap="lg">
            <SimpleGrid
              cols={{ base: 1, md: 2 }}
              spacing="md"
              style={{ opacity: isFetching ? 0.5 : 1 }}
            >
              {ads.map((ad) => (
                <AdCard key={ad.id} ad={ad} />
              ))}
            </SimpleGrid>

            {totalPages > 1 && (
              <Box mt="md">
                <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />
              </Box>
            )}
          </Stack>
        )}
      </Stack>
    </TeamLayout>
  )
}
