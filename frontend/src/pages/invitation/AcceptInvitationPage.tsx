import { useEffect, useRef, useState } from 'react'
import { useSearchParams, useNavigate, useLocation, Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { IconCheck, IconX, IconUsers, IconLogin } from '@tabler/icons-react'
import { Alert, Button, Center, Group, Loader, Paper, Stack, Text, Title } from '@mantine/core'
import { paths } from '@/config/paths'
import { useAuthStore } from '@/store/authStore'
import { preview, accept } from '@/api/endpoints/invitations/invitations'
import { ApiClientError } from '@/lib/apiError'
import type { InvitationPreviewDto } from '@/api/dto'

/**
 * Where an invitation e-mail lands.
 *
 * One page for three states — signed in, signed out with an account, signed out without one —
 * which is why both e-mail templates point here. The preview call is public precisely so that a
 * signed-out visitor is told who is inviting them and to what, instead of being dropped on a bare
 * login form with no explanation.
 *
 * The token is kept in sessionStorage across the login or sign-up detour, so coming back does not
 * depend on the browser having preserved the query string.
 */
const TOKEN_STORAGE_KEY = 'pendingInvitationToken'

type PageState = 'loading' | 'ready' | 'accepted' | 'error'

export function AcceptInvitationPage() {
  const { t } = useTranslation()
  const navigate = useNavigate()
  const location = useLocation()
  const [searchParams] = useSearchParams()
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated)
  const logout = useAuthStore((state) => state.logout)

  const token = searchParams.get('token')

  const [state, setState] = useState<PageState>(() => (token ? 'loading' : 'error'))
  const [invitation, setInvitation] = useState<InvitationPreviewDto | null>(null)
  const [failureCode, setFailureCode] = useState<string | null>(null)
  const [isAccepting, setIsAccepting] = useState(false)
  const hasLoaded = useRef(false)

  useEffect(() => {
    if (!token || hasLoaded.current) {
      return
    }
    hasLoaded.current = true
    // Survives the trip through login or sign-up.
    sessionStorage.setItem(TOKEN_STORAGE_KEY, token)

    preview({ token })
      .then((data) => {
        setInvitation(data)
        setState('ready')
      })
      .catch((error) => {
        setFailureCode(error instanceof ApiClientError ? error.error.code : null)
        setState('error')
      })
  }, [token])

  const handleAccept = () => {
    if (!token) return
    setIsAccepting(true)
    setFailureCode(null)
    accept({ token })
      .then(() => {
        sessionStorage.removeItem(TOKEN_STORAGE_KEY)
        setState('accepted')
        if (invitation) {
          navigate(paths.team(invitation.teamSlug), { replace: true })
        }
      })
      .catch((error) => {
        setFailureCode(error instanceof ApiClientError ? error.error.code : null)
        setState('error')
      })
      .finally(() => setIsAccepting(false))
  }

  // Whoever is signed in is not who the invitation is for. Signing out and coming back to this very
  // URL is the way through, so it is offered as a button rather than left as an exercise.
  const handleSwitchAccount = async () => {
    await logout()
    navigate(`${location.pathname}${location.search}`, { replace: true })
  }

  if (state === 'loading') {
    return (
      <Center mih="60vh">
        <Loader />
      </Center>
    )
  }

  if (state === 'error' || !invitation) {
    return (
      <Center mih="60vh">
        <Paper p="xl" withBorder maw={480} w="100%">
          <Stack align="center">
            <IconX size={40} color="var(--mantine-color-danger-6)" />
            <Title order={3} ta="center">
              {t('invitations.accept.unavailableTitle')}
            </Title>
            <Text c="dimmed" ta="center">
              {failureCode
                ? t('errors.api.' + failureCode)
                : t('invitations.accept.unavailableBody')}
            </Text>
            <Button component={Link} to={paths.teams()} variant="default">
              {t('invitations.accept.backToTeams')}
            </Button>
          </Stack>
        </Paper>
      </Center>
    )
  }

  if (state === 'accepted') {
    return (
      <Center mih="60vh">
        <Paper p="xl" withBorder maw={480} w="100%">
          <Stack align="center">
            <IconCheck size={40} color="var(--mantine-color-success-6)" />
            <Title order={3} ta="center">
              {t('invitations.accept.joined', { team: invitation.teamName })}
            </Title>
          </Stack>
        </Paper>
      </Center>
    )
  }

  return (
    <Center mih="60vh">
      <Paper p="xl" withBorder maw={480} w="100%">
        <Stack>
          <Center>
            <IconUsers size={40} />
          </Center>
          <Title order={3} ta="center">
            {t('invitations.accept.title', {
              inviter: invitation.inviterName,
              team: invitation.teamName,
            })}
          </Title>
          <Text c="dimmed" ta="center">
            {t('invitations.accept.subtitle', {
              role: t(`roles.${invitation.role}`),
              email: invitation.maskedEmail,
            })}
          </Text>

          {!invitation.redeemable && (
            <Alert color="orange">{t('invitations.accept.notRedeemable')}</Alert>
          )}

          {invitation.redeemable && isAuthenticated && (
            <Stack gap="xs">
              <Button onClick={handleAccept} loading={isAccepting} fullWidth>
                {t('invitations.accept.join')}
              </Button>
              <Button variant="subtle" size="compact-sm" onClick={handleSwitchAccount}>
                {t('invitations.accept.switchAccount')}
              </Button>
            </Stack>
          )}

          {invitation.redeemable && !isAuthenticated && (
            <Stack gap="xs">
              <Text size="sm" ta="center">
                {t('invitations.accept.signInPrompt')}
              </Text>
              <Group justify="center">
                <Button
                  component={Link}
                  to={paths.login()}
                  state={{ from: location }}
                  leftSection={<IconLogin size={16} />}
                >
                  {t('invitations.accept.signIn')}
                </Button>
              </Group>
            </Stack>
          )}
        </Stack>
      </Paper>
    </Center>
  )
}
