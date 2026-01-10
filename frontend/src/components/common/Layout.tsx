import { useEffect } from 'react'
import { Outlet, Link } from 'react-router-dom'
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
import { useDisclosure } from '@mantine/hooks'
import { IconUser, IconLogout } from '@tabler/icons-react'
import { useAuth } from '../../hooks/useAuth'
import { useBreadcrumb } from '../../hooks/useBreadcrumb'
import { Breadcrumb } from './Breadcrumb'
import { LanguageSwitcher } from './LanguageSwitcher'
import { paths } from '@/config/paths'

export function Layout() {
  const { t } = useTranslation()
  const { user, isAuthenticated, logout } = useAuth()
  const { items: breadcrumbItems, showBackLink } = useBreadcrumb()
  const [opened, { toggle, close }] = useDisclosure(false)

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
      header={{ height: 60 }}
      navbar={{ width: 300, breakpoint: 'sm', collapsed: { desktop: true, mobile: !opened } }}
      padding="md"
    >
      <AppShell.Header>
        <Container size="lg" h="100%">
          <Group h="100%" justify="space-between">
            <Anchor component={Link} to="/" underline="never">
              <Text size="xl" fw={700} c="indigo">
                {t('appName')}
              </Text>
            </Anchor>

            {/* Desktop Navigation */}
            <Group gap="sm" visibleFrom="sm">
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
                          color="indigo"
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
                    <Menu.Divider />
                    <Menu.Item leftSection={<IconLogout size={14} />} onClick={logout} color="red">
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
                    color="indigo"
                  >
                    {user?.displayName?.charAt(0).toUpperCase()}
                  </Avatar>
                  <Text size="sm">{user?.displayName}</Text>
                </Group>
              </UnstyledButton>
              <Button
                variant="subtle"
                color="red"
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
        <Container size="lg">
          <Breadcrumb items={breadcrumbItems} showBackLink={showBackLink} />
          <Outlet />
        </Container>
      </AppShell.Main>

      <Box
        component="footer"
        py="xl"
        style={{ borderTop: '1px solid var(--mantine-color-gray-3)' }}
      >
        <Container size="lg">
          <Text ta="center" c="dimmed" size="sm">
            {t('footer.copyright', { year: new Date().getFullYear() })}
          </Text>
        </Container>
      </Box>
    </AppShell>
  )
}
