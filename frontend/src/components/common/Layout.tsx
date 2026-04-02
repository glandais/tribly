import { useEffect } from 'react'
import { Outlet, Link, useLocation } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import {
  AppShell,
  Group,
  Burger,
  Text,
  Button,
  Avatar,
  Menu,
  Container,
  Anchor,
  Stack,
  Divider,
  Box,
  UnstyledButton,
} from '@mantine/core'
import { useDisclosure, useHeadroom } from '@mantine/hooks'
import { IconUser, IconLogout, IconShield } from '@tabler/icons-react'
import { useAuth } from '../../hooks/useAuth'
import { useAppName } from '../../hooks/useAppName'
import { useAuthStore, selectIsPlatformAdmin } from '@/store/authStore'
import { useBreadcrumb } from '../../hooks/useBreadcrumb'
import { Breadcrumb } from './Breadcrumb'
import { ColorSchemeSwitcher } from './ColorSchemeSwitcher'
import { LanguageSwitcher } from './LanguageSwitcher'
import { paths } from '@/config/paths'

export function Layout() {
  const { t } = useTranslation()
  const { user, isAuthenticated, logout } = useAuth()
  const appName = useAppName()
  const isPlatformAdmin = useAuthStore(selectIsPlatformAdmin)
  const { items: breadcrumbItems, showBackLink } = useBreadcrumb()
  const [opened, { toggle, close }] = useDisclosure(false)
  const { pathname } = useLocation()

  const pinned = useHeadroom({ fixedAt: 120 })
  // Scroll to top on route change
  useEffect(() => {
    window.scrollTo(0, 0)
  }, [pathname])

  // Close mobile menu on Escape key
  useEffect(() => {
    const handleEscape = (event: KeyboardEvent) => {
      if (event.key === 'Escape' && opened) {
        close()
      }
    }

    document.addEventListener('keydown', handleEscape)
    return () => document.removeEventListener('keydown', handleEscape)
  }, [opened, close])

  return (
    <AppShell
      header={{ height: { base: 56, sm: 60 }, collapsed: !pinned }}
      navbar={{ width: 300, breakpoint: 'sm', collapsed: { desktop: true, mobile: !opened } }}
      padding={{ base: 'xs', sm: 'md' }}
    >
      <AppShell.Header>
        <Container size="lg" h="100%">
          <Group h="100%" justify="space-between">
            <Anchor component={Link} to="/" underline="never">
              <Text size="xl" fw={700} c="primary">
                {appName}
              </Text>
            </Anchor>

            {/* Desktop Navigation */}
            <Group gap="sm" visibleFrom="sm">
              <ColorSchemeSwitcher />
              <LanguageSwitcher />
              {isAuthenticated ? (
                <Menu shadow="md" width={200}>
                  <Menu.Target>
                    <UnstyledButton>
                      <Group gap="xs">
                        <Avatar
                          src={user?.avatarUrl}
                          alt={user?.displayName}
                          radius="xl"
                          size="sm"
                          color="primary"
                        >
                          {user?.displayName?.charAt(0).toUpperCase()}
                        </Avatar>
                        <Text size="sm" visibleFrom="md">
                          {user?.displayName}
                        </Text>
                      </Group>
                    </UnstyledButton>
                  </Menu.Target>
                  <Menu.Dropdown>
                    <Menu.Item
                      leftSection={<IconUser size={14} />}
                      component={Link}
                      to={paths.profile()}
                    >
                      {t('nav.profile')}
                    </Menu.Item>
                    {isPlatformAdmin && (
                      <Menu.Item
                        leftSection={<IconShield size={14} />}
                        component={Link}
                        to={paths.admin()}
                      >
                        {t('nav.admin')}
                      </Menu.Item>
                    )}
                    <Menu.Divider />
                    <Menu.Item
                      leftSection={<IconLogout size={14} />}
                      onClick={logout}
                      color="danger"
                    >
                      {t('nav.signOut')}
                    </Menu.Item>
                  </Menu.Dropdown>
                </Menu>
              ) : (
                <Button component={Link} to="/login">
                  {t('nav.signIn')}
                </Button>
              )}
            </Group>

            {/* Mobile burger */}
            <Burger opened={opened} onClick={toggle} hiddenFrom="sm" size="sm" />
          </Group>
        </Container>
      </AppShell.Header>

      {/* Mobile Navigation */}
      <AppShell.Navbar p="md">
        <Stack>
          <ColorSchemeSwitcher />
          <LanguageSwitcher />
          <Divider />
          {isAuthenticated ? (
            <>
              <UnstyledButton component={Link} to={paths.profile()} onClick={close}>
                <Group>
                  <Avatar
                    src={user?.avatarUrl}
                    alt={user?.displayName}
                    radius="xl"
                    size="sm"
                    color="primary"
                  >
                    {user?.displayName?.charAt(0).toUpperCase()}
                  </Avatar>
                  <Text size="sm">{user?.displayName}</Text>
                </Group>
              </UnstyledButton>
              {isPlatformAdmin && (
                <Button
                  variant="subtle"
                  leftSection={<IconShield size={16} />}
                  component={Link}
                  to={paths.admin()}
                  onClick={close}
                >
                  {t('nav.admin')}
                </Button>
              )}
              <Button
                variant="subtle"
                color="danger"
                leftSection={<IconLogout size={16} />}
                onClick={() => {
                  logout()
                  close()
                }}
              >
                {t('nav.signOut')}
              </Button>
            </>
          ) : (
            <Button component={Link} to="/login" onClick={close}>
              {t('nav.signIn')}
            </Button>
          )}
        </Stack>
      </AppShell.Navbar>

      <AppShell.Main>
        <Container size="lg" px={0}>
          <Breadcrumb items={breadcrumbItems} showBackLink={showBackLink} />
          <Outlet />
        </Container>
      </AppShell.Main>

      <Box
        component="footer"
        py="xl"
        style={{ borderTop: '1px solid var(--mantine-color-default-border)' }}
      >
        <Container size="lg">
          <Group justify="center" gap="xs" wrap="wrap">
            <Text c="dimmed" size="sm">
              {t('footer.copyright', { year: new Date().getFullYear(), appName })}
            </Text>
            <Text c="dimmed" size="sm">
              ·
            </Text>
            <Anchor component={Link} to={paths.privacy()} c="dimmed" size="sm">
              {t('footer.privacy')}
            </Anchor>
            <Text c="dimmed" size="sm">
              ·
            </Text>
            <Anchor component={Link} to={paths.terms()} c="dimmed" size="sm">
              {t('footer.terms')}
            </Anchor>
          </Group>
        </Container>
      </Box>
    </AppShell>
  )
}
