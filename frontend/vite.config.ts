import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import path from 'path'
import { visualizer } from 'rollup-plugin-visualizer'

export default defineConfig({
  plugins: [
    react(),
    visualizer({
      open: false,
      gzipSize: true,
      brotliSize: true,
      filename: 'stats.html',
    }),
  ],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  build: {
    rollupOptions: {
      output: {
        manualChunks(id) {
          // React core
          if (id.includes('node_modules/react/') ||
              id.includes('node_modules/react-dom/') ||
              id.includes('node_modules/react-router') ||
              id.includes('node_modules/@tanstack/react-query/') ||
              id.includes('node_modules/scheduler/')) {
            return 'react-vendor';
          }

          // Maps
          if (id.includes('node_modules/leaflet/') ||
              id.includes('node_modules/react-leaflet') ||
              id.includes('node_modules/@react-leaflet')) {
            return 'leaflet-vendor';
          }

          // Auth
          if (id.includes('node_modules/keycloak-js/')) {
            return 'keycloak-vendor';
          }

          if (id.includes('node_modules/axios')) {
            return 'axios-vendor';
          }

          // i18n
          if (id.includes('node_modules/i18next') ||
              id.includes('node_modules/react-i18next/')) {
            return 'i18n-vendor';
          }

          // Other node_modules go to vendor chunk
          if (id.includes('node_modules/')) {
            return 'vendor';
          }
        },
      },
    },
    chunkSizeWarningLimit: 600, // Temporarily increase to reduce noise while optimizing
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
    },
  },
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: './src/test/setup.ts',
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html'],
    },
  },
})
