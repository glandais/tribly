import { useTranslation } from 'react-i18next'
import {
  Badge,
  Box,
  Button,
  Flex,
  Group,
  List,
  Paper,
  Stack,
  Text,
  ThemeIcon,
  Title,
} from '@mantine/core'
import {
  IconArrowRight,
  IconCalendarEvent,
  IconDeviceWatch,
  IconRoute,
  IconUsersGroup,
} from '@tabler/icons-react'
import { LoginForm, type LoginFormMode } from '@/components/auth/LoginForm'
import { PrefetchLink } from '@/components/common/PrefetchLink'
import { isSingleTeam } from '@/config/appConfig'
import { paths } from '@/config/paths'
import { PUBLICATION_TYPE_COLORS } from '@/lib/badgeColors.generated'
import { useAppName } from '@/hooks/useAppName'

/** Anchor of the hero's sign-in form, for the links that bring the visitor back to it. */
export const LOGIN_ANCHOR = 'connexion'

interface VisitorHeroProps {
  loginMode: LoginFormMode
  onLoginModeChange: (mode: LoginFormMode) => void
}

/**
 * Top of the visitor home: the product pitch beside the sign-in form (`LoginForm`, the login
 * page's own). On a phone the form comes right after the headline, and « Découvrir les
 * fonctionnalités » right after the form.
 */
export function VisitorHero({ loginMode, onLoginModeChange }: VisitorHeroProps) {
  const { t } = useTranslation()
  const appName = useAppName()
  const singleTeam = isSingleTeam()

  const discover = (
    <Button
      component={PrefetchLink}
      to={paths.features()}
      size="md"
      rightSection={<IconArrowRight size={18} />}
    >
      {t('home.visitor.discover')}
    </Button>
  )

  return (
    <Paper
      radius="lg"
      p={{ base: 'md', sm: 'xl' }}
      bg="var(--mantine-primary-color-light)"
      component="section"
      aria-labelledby="home-hero-title"
    >
      <Flex direction={{ base: 'column', md: 'row' }} gap={{ base: 'lg', md: 48 }} align="center">
        <Stack gap="md" style={{ flex: 1, minWidth: 0 }}>
          <Badge variant="light" color="primary" style={{ alignSelf: 'flex-start' }}>
            {t('home.visitor.eyebrow')}
          </Badge>
          <Title id="home-hero-title" order={1} fz={{ base: 28, sm: 40 }} lh={1.15}>
            {t('home.visitor.title')}
          </Title>
          <Text size="lg" c="dimmed" maw={540}>
            {t('home.visitor.pitch', { appName })}
          </Text>
          <List spacing="sm" center visibleFrom="sm">
            <List.Item
              icon={
                <ThemeIcon
                  variant="light"
                  color={PUBLICATION_TYPE_COLORS.RIDE}
                  radius="md"
                  size={28}
                >
                  <IconCalendarEvent size={16} />
                </ThemeIcon>
              }
            >
              {t('home.visitor.points.rides')}
            </List.Item>
            <List.Item
              icon={
                <ThemeIcon variant="light" color="orange" radius="md" size={28}>
                  <IconRoute size={16} />
                </ThemeIcon>
              }
            >
              {t('home.visitor.points.routes')}
            </List.Item>
            <List.Item
              icon={
                <ThemeIcon variant="light" color="gray" radius="md" size={28}>
                  <IconDeviceWatch size={16} />
                </ThemeIcon>
              }
            >
              {t('home.visitor.points.devices')}
            </List.Item>
          </List>
          <Group gap="sm" visibleFrom="md">
            {discover}
            {!singleTeam && (
              <Button
                component={PrefetchLink}
                to={paths.teams()}
                size="md"
                variant="default"
                leftSection={<IconUsersGroup size={18} />}
              >
                {t('home.visitor.browseTeams')}
              </Button>
            )}
          </Group>
        </Stack>

        <Box w="100%" maw={420} style={{ flexShrink: 0 }}>
          <Paper id={LOGIN_ANCHOR} shadow="lg" radius="lg" p="xl" style={{ scrollMarginTop: 80 }}>
            <LoginForm
              title={t('home.visitor.login.title')}
              subtitle={t('home.visitor.login.subtitle')}
              titleOrder={2}
              afterSignIn="stay"
              mode={loginMode}
              onModeChange={onLoginModeChange}
            />
          </Paper>
          <Stack hiddenFrom="md" mt="md">
            {discover}
          </Stack>
        </Box>
      </Flex>
    </Paper>
  )
}
