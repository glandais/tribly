import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import path from 'path'
import { visualizer } from 'rollup-plugin-visualizer'

export default defineConfig({
  plugins: [
    react(),
    visualizer({
      open: false,
      template: 'flamegraph',
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
        // Order matters: specific checks MUST come before broad ones
        manualChunks(id) {
          if (!id.includes('node_modules/')) {
            return; // Let Rollup handle app code
          }

          // === MAPS (check first - react-map-gl must not fall to react checks) ===
          if (
            id.includes('node_modules/maplibre-gl/') ||
            id.includes('node_modules/maplibre-theme/') ||
            id.includes('node_modules/react-map-gl/') ||
            id.includes('node_modules/@versatiles/') ||
            id.includes('node_modules/geokdbush/') ||
            id.includes('node_modules/kdbush/')
          ) {
            return 'map-vendor';
          }

          // === EDITOR (unified ecosystem must be together to avoid cycles) ===
          if (
            // MDX/Lexical editors
            id.includes('node_modules/@mdxeditor/') ||
            id.includes('node_modules/@lexical/') ||
            id.includes('node_modules/lexical/') ||
            // React markdown
            id.includes('node_modules/react-markdown/') ||
            // Syntax highlighting
            id.includes('node_modules/prismjs/') ||
            id.includes('node_modules/remove-markdown/') ||
            // Unified ecosystem (remark, rehype, mdast, hast, micromark, etc.)
            id.includes('node_modules/unified/') ||
            id.includes('node_modules/remark') ||
            id.includes('node_modules/rehype') ||
            id.includes('node_modules/mdast') ||
            id.includes('node_modules/hast') ||
            id.includes('node_modules/micromark') ||
            id.includes('node_modules/unist') ||
            id.includes('node_modules/vfile') ||
            // Unified utilities
            id.includes('node_modules/bail/') ||
            id.includes('node_modules/trough/') ||
            id.includes('node_modules/devlop/') ||
            id.includes('node_modules/ccount/') ||
            id.includes('node_modules/zwitch/') ||
            id.includes('node_modules/character-entities') ||
            id.includes('node_modules/character-reference') ||
            id.includes('node_modules/comma-separated-tokens/') ||
            id.includes('node_modules/space-separated-tokens/') ||
            id.includes('node_modules/property-information/') ||
            id.includes('node_modules/stringify-entities/') ||
            id.includes('node_modules/decode-named-character-reference/') ||
            id.includes('node_modules/estree-util-') ||
            id.includes('node_modules/parse-entities/') ||
            id.includes('node_modules/trim-lines/') ||
            id.includes('node_modules/is-plain-obj/')
          ) {
            return 'editor-vendor';
          }

          // === CHARTS (check before react - react-chartjs-2 must not fall to react) ===
          if (
            id.includes('node_modules/chart.js/') ||
            id.includes('node_modules/react-chartjs-2/')
          ) {
            return 'chart-vendor';
          }

          // === I18N (check before react - react-i18next must not fall to react) ===
          if (
            id.includes('node_modules/i18next') ||
            id.includes('node_modules/react-i18next/')
          ) {
            return 'i18n-vendor';
          }

          // === UI: Mantine (large, separate chunk) ===
          if (
            id.includes('node_modules/@mantine/') ||
            id.includes('node_modules/mantine-form-zod-resolver/')
          ) {
            return 'mantine-vendor';
          }

          // === ICONS (project uses @tabler, not @heroicons) ===
          if (id.includes('node_modules/@tabler/icons-react/')) {
            return 'icons-vendor';
          }

          // === REACT CORE (now safe - specific react-* packages already handled) ===
          if (
            id.includes('node_modules/react/') ||
            id.includes('node_modules/react-dom/') ||
            id.includes('node_modules/scheduler/')
          ) {
            return 'react-vendor';
          }

          // === REACT ROUTER ===
          if (id.includes('node_modules/react-router')) {
            return 'router-vendor';
          }

          // === DATA LAYER (state, fetching, validation) ===
          if (
            id.includes('node_modules/@tanstack/') ||
            id.includes('node_modules/zustand/') ||
            id.includes('node_modules/axios/') ||
            id.includes('node_modules/zod/')
          ) {
            return 'data-vendor';
          }

          // === DATES ===
          if (
            id.includes('node_modules/date-fns') ||
            id.includes('node_modules/dayjs/')
          ) {
            return 'date-vendor';
          }

          // === AUTH ===
          if (id.includes('node_modules/keycloak-js/')) {
            return 'auth-vendor';
          }

          // === UI UTILITIES ===
          if (id.includes('node_modules/react-resizable-panels/')) {
            return 'ui-utils-vendor';
          }

          // === REMAINING VENDOR ===
          return 'vendor';
        },
      },
    },
    chunkSizeWarningLimit: 500,
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
