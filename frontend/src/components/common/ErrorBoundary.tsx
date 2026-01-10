import { Component, ErrorInfo, ReactNode } from 'react'
import { Translation } from 'react-i18next'
import {
  Alert,
  Button,
  Center,
  Paper,
  Stack,
  Text,
  Title,
  Code,
  Group,
  ThemeIcon,
} from '@mantine/core'
import { IconAlertTriangle, IconXboxX } from '@tabler/icons-react'

interface ErrorBoundaryProps {
  children: ReactNode
  fallback?: ReactNode
  onError?: (error: Error, errorInfo: ErrorInfo) => void
}

interface ErrorBoundaryState {
  hasError: boolean
  error: Error | null
}

export class ErrorBoundary extends Component<ErrorBoundaryProps, ErrorBoundaryState> {
  constructor(props: ErrorBoundaryProps) {
    super(props)
    this.state = { hasError: false, error: null }
  }

  static getDerivedStateFromError(error: Error): ErrorBoundaryState {
    return { hasError: true, error }
  }

  componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    console.error('ErrorBoundary caught an error:', error, errorInfo)
    this.props.onError?.(error, errorInfo)
  }

  handleRetry = () => {
    this.setState({ hasError: false, error: null })
  }

  render() {
    if (this.state.hasError) {
      if (this.props.fallback) {
        return this.props.fallback
      }

      return (
        <Translation ns="errors">
          {(t) => (
            <Center mih="100vh" bg="gray.0">
              <Paper shadow="lg" p="xl" radius="md" w={{ base: '100%', sm: 400 }}>
                <Stack align="center">
                  <ThemeIcon size="xl" radius="xl" color="red" variant="light">
                    <IconAlertTriangle size={24} />
                  </ThemeIcon>
                  <Title order={3} ta="center">
                    {t('boundary.title')}
                  </Title>
                  <Text c="dimmed" ta="center">
                    {t('boundary.message')}
                  </Text>
                  {this.state.error && import.meta.env.DEV && (
                    <Code block color="red" w="100%">
                      {this.state.error.message}
                    </Code>
                  )}
                  <Group>
                    <Button onClick={this.handleRetry}>{t('boundary.retry')}</Button>
                    <Button variant="default" onClick={() => window.location.reload()}>
                      {t('boundary.reload')}
                    </Button>
                  </Group>
                </Stack>
              </Paper>
            </Center>
          )}
        </Translation>
      )
    }

    return this.props.children
  }
}

interface ErrorMessageProps {
  title?: string
  message: string
  onRetry?: () => void
}

export function ErrorMessage({ title, message, onRetry }: ErrorMessageProps) {
  return (
    <Translation ns="errors">
      {(t) => (
        <Alert icon={<IconXboxX size={16} />} title={title || t('generic.title')} color="red">
          <Text size="sm">{message}</Text>
          {onRetry && (
            <Button variant="subtle" color="red" size="xs" mt="sm" onClick={onRetry}>
              {t('generic.retry')}
            </Button>
          )}
        </Alert>
      )}
    </Translation>
  )
}
