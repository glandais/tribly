import {
  Loader,
  Center,
  Text,
  Stack,
  Box,
  LoadingOverlay as MantineLoadingOverlay,
} from '@mantine/core'

interface LoadingSpinnerProps {
  size?: 'sm' | 'md' | 'lg'
  color?: 'primary' | 'white' | 'gray'
}

const sizeMap = {
  sm: 'sm',
  md: 'md',
  lg: 'lg',
} as const

const colorMap = {
  primary: 'indigo',
  white: 'white',
  gray: 'gray',
}

export function LoadingSpinner({ size = 'md', color = 'primary' }: LoadingSpinnerProps) {
  return <Loader size={sizeMap[size]} color={colorMap[color]} />
}

interface LoadingPageProps {
  message: string
}

export function LoadingPage({ message }: LoadingPageProps) {
  return (
    <Center mih="60vh">
      <Stack align="center">
        <LoadingSpinner size="lg" />
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
