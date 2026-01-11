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
  // Responsive typography
  headings: {
    sizes: {
      h1: { fontSize: 'clamp(1.5rem, 5vw, 2.125rem)', lineHeight: '1.2' },
      h2: { fontSize: 'clamp(1.25rem, 4vw, 1.625rem)', lineHeight: '1.3' },
      h3: { fontSize: 'clamp(1.125rem, 3vw, 1.375rem)', lineHeight: '1.4' },
      h4: { fontSize: 'clamp(1rem, 2.5vw, 1.125rem)', lineHeight: '1.5' },
    },
  },
  // Component-level responsive defaults
  components: {
    Button: {
      styles: {
        root: {
          // Touch-friendly minimum on mobile (44px), smaller on desktop
          minHeight: 'var(--button-min-height, 44px)',
          '@media (min-width: 48em)': {
            '--button-min-height': '36px',
          },
        },
      },
    },
    ActionIcon: {
      defaultProps: {
        // Larger touch targets on mobile
        size: 'lg',
      },
    },
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
