import type { ReactNode } from 'react'
import { Button, Center, Stack, Text, Title } from '@mantine/core'
import { IconMoodEmpty } from '@tabler/icons-react'
import { useTranslation } from 'react-i18next'
import { Link } from 'react-router-dom'
import { isAxiosError } from 'axios'
import { ApiClientError } from '@/lib/apiError'
import { ErrorMessage } from './ErrorBoundary'

interface NotFoundConfig {
  title: string
  message?: string
  /** Where to go back to — e.g. the team feed. */
  backTo?: string
  backLabel?: string
}

interface QueryStateBoundaryProps {
  isLoading: boolean
  isError?: boolean
  error?: unknown
  /** The query succeeded but returned nothing — rendered as `notFound` too. */
  isNotFound?: boolean
  /** The query succeeded and returned an empty collection. */
  isEmpty?: boolean
  onRetry?: () => void
  skeleton?: ReactNode
  empty?: ReactNode
  notFound?: NotFoundConfig
  children: ReactNode
}

/**
 * `axiosMutator` only wraps an error in `ApiClientError` when the body carries a `code`;
 * a bare 404 or a non-JSON 5xx arrives as the raw AxiosError. Read both shapes, so a 404
 * is recognised either way, and fall through to the recoverable branch when neither says.
 */
function statusOf(error: unknown): number | undefined {
  if (error instanceof ApiClientError) return error.status
  if (isAxiosError(error)) return error.response?.status
  return undefined
}

/**
 * Discriminates the four outcomes of a detail/list query: loading, *recoverable*
 * error (5xx, network — offers a retry), 404 (the resource genuinely does not
 * exist), and empty. Before this component, pages collapsed error and 404 into
 * a single "introuvable", which lied on a 500.
 */
export function QueryStateBoundary({
  isLoading,
  isError,
  error,
  isNotFound,
  isEmpty,
  onRetry,
  skeleton,
  empty,
  notFound,
  children,
}: QueryStateBoundaryProps) {
  const { t } = useTranslation()

  if (isLoading) {
    return <>{skeleton}</>
  }

  // An error wins over `isNotFound`: callers derive the latter from `!data`, which is also
  // true on a 500 — treating that as "not found" is exactly the lie this component exists
  // to stop. Only a genuine 404, or a successful-but-empty response, is "not found".
  const status = statusOf(error)
  const missing = isError ? status === 404 : isNotFound

  if (isError && !missing) {
    return (
      <ErrorMessage
        title={t('query.error.title')}
        message={t('query.error.message')}
        onRetry={onRetry}
      />
    )
  }

  if (missing) {
    return (
      <Center py="xl">
        <Stack align="center" gap="md">
          <IconMoodEmpty size={48} color="var(--mantine-color-dimmed)" />
          <Title order={3} ta="center">
            {notFound?.title ?? t('query.notFound.title')}
          </Title>
          <Text c="dimmed" ta="center" maw={400}>
            {notFound?.message ?? t('query.notFound.message')}
          </Text>
          {notFound?.backTo && (
            <Button component={Link} to={notFound.backTo} variant="light">
              {notFound.backLabel ?? t('errors.notFound.backHome')}
            </Button>
          )}
        </Stack>
      </Center>
    )
  }

  if (isEmpty && empty) {
    return <>{empty}</>
  }

  return <>{children}</>
}
