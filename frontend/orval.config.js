import { defineConfig } from 'orval';

export default defineConfig({
  pedalons: {
    input: {
      target: '../contracts/openapi.json',
    },
    output: {
      mode: 'tags-split',
      client: 'react-query',
      httpClient: 'axios',
      target: 'src/api/endpoints',
      schemas: 'src/api/dto',
      formatter: 'prettier',
      override: {
        header: false,
        query: {
          usePrefetch: true,
        },
        mutator: {
          path: './src/lib/axiosInstance.ts',
          name: 'axiosMutator',
        },
      },
    },
  },
  pedalonsZod: {
    input: {
      target: '../contracts/openapi.json',
    },
    output: {
      mode: 'tags-split',
      client: 'zod',
      target: 'src/api/zod',
      fileExtension: '.zod.ts',
      formatter: 'prettier',
      override: {
        header: false,
      }
    },
  },
});