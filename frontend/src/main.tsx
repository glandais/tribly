import React from 'react'
import ReactDOM from 'react-dom/client'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { MantineProvider, createTheme, virtualColor } from '@mantine/core'
import { Notifications } from '@mantine/notifications'
import App from './App'
import './index.css'

// Initialize i18n before rendering
import './i18n'

const theme = createTheme({
  primaryColor: 'primary',
  fontFamily: 'Inter, system-ui, sans-serif',
  defaultRadius: 'md',
  autoContrast: true,
  luminanceThreshold: 0.3,
  colors: {
    primary: virtualColor({
      name: 'primary',
      light: 'indigo',
      dark: 'indigo',
    }),
    success: virtualColor({
      name: 'success',
      light: 'green',
      dark: 'green',
    }),
    warning: virtualColor({
      name: 'warning',
      light: 'yellow',
      dark: 'yellow',
    }),
    danger: virtualColor({
      name: 'danger',
      light: 'red',
      dark: 'red',
    }),
  },
})

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: (failureCount, error) => {
        // Allow one retry for 401 errors to handle token refresh race condition
        if (error instanceof Error && error.message.includes('401')) {
          return failureCount < 1
        }
        return failureCount < 3
      },
      retryDelay: (attemptIndex) => Math.min(1000 * 2 ** attemptIndex, 5000),
      refetchOnWindowFocus: false,
    },
    mutations: {
      retry: false,
    },
  },
})

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <MantineProvider theme={theme} defaultColorScheme="auto">
      <Notifications position="top-right" />
      <QueryClientProvider client={queryClient}>
        <App />
      </QueryClientProvider>
    </MantineProvider>
  </React.StrictMode>
)
