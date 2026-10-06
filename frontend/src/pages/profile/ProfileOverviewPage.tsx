import { useTranslation } from 'react-i18next'
import {
  Anchor,
  Avatar,
  Badge,
  Box,
  Button,
  Center,
  Group,
  Loader,
  NavLink,
  Paper,
  SimpleGrid,
  Skeleton,
  Stack,
  Text,
  ThemeIcon,
  Title,
} from '@mantine/core'
import { IconBike, IconChevronRight } from '@tabler/icons-react'
import { useGetMyProfileSummary } from '@/api/endpoints/users/users'
import type { ProfileSummaryDto, PublicationDto, TeamRole } from '@/api/dto'
import { PrefetchLink } from '@/components/common/PrefetchLink'
import { Rendezvous } from '@/components/common/Rendezvous'
import { UserAvatar } from '@/components/common/UserAvatar'
import {
  PROFILE_SIDEBAR_FROM,
  ProfileShell,
  ProfileSignOutButton,
} from '@/components/profile/ProfileShell'
import {
  PROFILE_NAV_GROUPS,
  type ProfileNavItem,
  type ProfileSection,
} from '@/components/profile/profileNav'
import { profileStatusLines } from '@/components/profile/profileStatus'
import { getColorFromName, getInitials } from '@/components/team/teamInitials'
import { paths } from '@/config/paths'
import { useAuth } from '@/hooks/useAuth'

/** The shortcuts a desktop shows as cards: the settings, without « Mon compte » (the identity
 * card leads there) and without the activity, which has its own two cards above. */
const DESKTOP_CARDS: ProfileSection[] = [
  'preferences',
  'notifications',
  'devices',
  'security',
  'privacy',
  'help',
]

const NAV_ITEMS: ProfileNavItem[] = PROFILE_NAV_GROUPS.flatMap((group) => group.items)

function publicationPath(publication: PublicationDto): string {
  return publication.type === 'TRIP'
    ? paths.trip(publication.team.slug, publication.slug)
    : paths.ride(publication.team.slug, publication.slug)
}

function IdentityCard() {
  const { t } = useTranslation()
  const { user } = useAuth()
  if (!user) return null
  return (
    <Paper withBorder radius="md" p="lg">
      <Group wrap="wrap" gap="lg">
        <UserAvatar user={user} size="xl" />
        <Box style={{ flex: '1 1 200px', minWidth: 0 }}>
          <Text fw={700} size="xl" truncate>
            {user.displayName}
          </Text>
          <Text size="sm" c="dimmed" truncate>
            {user.email}
          </Text>
        </Box>
        <Button component={PrefetchLink} to={paths.profileAccount()} variant="default">
          {t('profile.nav.account')}
        </Button>
      </Group>
    </Paper>
  )
}

function RidesCard({ summary }: { summary: ProfileSummaryDto | undefined }) {
  const { t } = useTranslation()
  const next = summary?.participations.next[0]
  return (
    <Paper withBorder radius="md" p="lg">
      <Stack gap="sm">
        <Group justify="space-between" align="baseline">
          <Title order={3} size="h5">
            {t('profile.nav.rides')}
          </Title>
          <Anchor component={PrefetchLink} to={paths.myParticipations()} size="sm" fw={600}>
            {t('profile.rides.viewAll')}
          </Anchor>
        </Group>
        {!summary ? (
          <Skeleton height={64} radius="md" />
        ) : next ? (
          <Paper bg="var(--mantine-color-default-hover)" radius="md" p="sm">
            <Group wrap="nowrap" gap="sm">
              <ThemeIcon variant="light" size={40} radius="md">
                <IconBike size={22} />
              </ThemeIcon>
              <Box style={{ minWidth: 0 }}>
                <Text size="xs" fw={600} c="dimmed">
                  {t('profile.rides.next')} ·{' '}
                  <Rendezvous date={next.dateTime} zone={next.timezone} />
                </Text>
                <Anchor
                  component={PrefetchLink}
                  to={publicationPath(next)}
                  fw={600}
                  c="inherit"
                  lineClamp={1}
                >
                  {next.name}
                </Anchor>
                <Text size="sm" c="dimmed" truncate>
                  {next.team.name}
                </Text>
              </Box>
            </Group>
          </Paper>
        ) : (
          <Text size="sm" c="dimmed">
            {t('profile.rides.noNext')}
          </Text>
        )}
        {summary && (
          <Group gap="xs">
            <Badge variant="light" color="primary">
              {t('profile.status.upcoming', { count: summary.participations.upcomingCount })}
            </Badge>
            <Badge variant="light" color="gray">
              {t('profile.status.history', { count: summary.participations.pastCount })}
            </Badge>
          </Group>
        )}
      </Stack>
    </Paper>
  )
}

function TeamsCard({ summary }: { summary: ProfileSummaryDto | undefined }) {
  const { t } = useTranslation()
  return (
    <Paper withBorder radius="md" p="lg">
      <Stack gap="sm">
        <Group justify="space-between" align="baseline">
          <Title order={3} size="h5">
            {t('profile.nav.teams')}
          </Title>
          <Anchor component={PrefetchLink} to={paths.teams()} size="sm" fw={600}>
            {t('teams.title')}
          </Anchor>
        </Group>
        {!summary ? (
          <Skeleton height={64} radius="md" />
        ) : summary.teams.length === 0 ? (
          <Text size="sm" c="dimmed">
            {t('profile.teams.empty')}
          </Text>
        ) : (
          <Stack gap={4}>
            {summary.teams.map((team) => (
              <NavLink
                key={team.slug}
                component={PrefetchLink}
                to={paths.team(team.slug)}
                label={team.name}
                leftSection={
                  <Avatar size="md" radius="md" color={getColorFromName(team.name)}>
                    {getInitials(team.name)}
                  </Avatar>
                }
                rightSection={
                  <Text size="xs" c="dimmed">
                    {t(`roles.${team.role satisfies TeamRole}`)}
                  </Text>
                }
                style={{ borderRadius: 'var(--mantine-radius-md)' }}
              />
            ))}
          </Stack>
        )}
      </Stack>
    </Paper>
  )
}

/** A desktop shortcut: the subject, its state line, and the way to its page. */
function StatusCard({ item, status }: { item: ProfileNavItem; status: string | undefined }) {
  const { t } = useTranslation()
  const Icon = item.icon
  return (
    <Paper
      withBorder
      radius="md"
      p="md"
      component={PrefetchLink}
      to={item.to()}
      style={{ color: 'inherit', textDecoration: 'none' }}
    >
      <Group wrap="nowrap" gap="md">
        <ThemeIcon variant="light" size={40} radius="md">
          <Icon size={20} stroke={1.5} />
        </ThemeIcon>
        <Box style={{ flex: 1, minWidth: 0 }}>
          <Text fw={600}>{t(item.labelKey)}</Text>
          <Text size="sm" c="dimmed" lineClamp={2}>
            {status ?? ' '}
          </Text>
        </Box>
        <IconChevronRight size={18} color="var(--mantine-color-dimmed)" />
      </Group>
    </Paper>
  )
}

/**
 * `/profil`: who is signed in, what they ride next, their teams, and one shortcut per subject with
 * its state line. Two calls in all — `/me`, which the server seeds, and the profile summary.
 *
 * A desktop has the sidebar (ProfileShell) and the subjects as cards; a phone has the grouped list,
 * which stands in for the sidebar, with « Se déconnecter » at its foot.
 */
export function ProfileOverviewPage() {
  const { t, i18n } = useTranslation()
  const { user, isLoading } = useAuth()
  const { data: summary } = useGetMyProfileSummary()

  if (isLoading || !user) {
    return (
      <Center py="xl">
        <Loader size="lg" />
      </Center>
    )
  }

  const status = profileStatusLines(t, user, summary, i18n.language)
  const itemOf = (section: ProfileSection) => NAV_ITEMS.find((item) => item.section === section)!

  return (
    <ProfileShell section="overview" title={t('profile.title')}>
      <IdentityCard />

      <SimpleGrid cols={{ base: 1, [PROFILE_SIDEBAR_FROM]: 2 }} spacing="md">
        <RidesCard summary={summary} />
        <Box visibleFrom={PROFILE_SIDEBAR_FROM}>
          <TeamsCard summary={summary} />
        </Box>
      </SimpleGrid>

      <Stack gap="sm" visibleFrom={PROFILE_SIDEBAR_FROM}>
        <Title order={3} size="h4">
          {t('profile.nav.group.settings')}
        </Title>
        <SimpleGrid cols={{ base: 1, lg: 2 }} spacing="sm">
          {DESKTOP_CARDS.map((section) => (
            <StatusCard key={section} item={itemOf(section)} status={status[section]} />
          ))}
        </SimpleGrid>
      </Stack>

      <Stack gap="lg" hiddenFrom={PROFILE_SIDEBAR_FROM}>
        {PROFILE_NAV_GROUPS.map((group) => (
          <Stack key={group.labelKey} gap={6}>
            <Text size="xs" fw={600} tt="uppercase" c="dimmed" px="xs">
              {t(group.labelKey)}
            </Text>
            <Paper withBorder radius="md" style={{ overflow: 'hidden' }}>
              {group.items.map((item) => {
                const Icon = item.icon
                return (
                  <NavLink
                    key={item.section}
                    component={PrefetchLink}
                    to={item.to()}
                    label={t(item.labelKey)}
                    description={status[item.section]}
                    leftSection={<Icon size={20} stroke={1.5} />}
                    rightSection={<IconChevronRight size={16} />}
                    py="sm"
                    mih={44}
                  />
                )
              })}
            </Paper>
          </Stack>
        ))}
        <Paper withBorder radius="md" p={4}>
          <ProfileSignOutButton />
        </Paper>
      </Stack>
    </ProfileShell>
  )
}
