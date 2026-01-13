import {
  Loader,
  Center,
  Text,
  Stack,
  Box,
  LoadingOverlay as MantineLoadingOverlay,
} from '@mantine/core'

interface LoadingPageProps {
  message: string
}

export function LoadingPage({ message }: LoadingPageProps) {
  return (
    <Center mih="60vh">
      <Stack align="center">
        <Loader size="lg" />
        <Text c="dimmed">{message}</Text>
      </Stack>
    </Center>
  )
}

interface LoadingOverlayProps {
  isLoading: boolean
  children: React.ReactNode
}

export function LoadingOverlay({ isLoading, children }: LoadingOverlayProps) {
  return (
    <Box pos="relative">
      <MantineLoadingOverlay visible={isLoading} overlayProps={{ blur: 2 }} />
      {children}
    </Box>
  )
}
