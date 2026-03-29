import { defineConfig } from 'orval';

export default defineConfig({
  tribly: {
    input: {
      target: '../contracts/openapi.json',
    },
    output: {
      mode: 'tags-split',
      client: 'react-query',
      httpClient: 'axios',
      target: 'src/api/endpoints',
      schemas: 'src/api/dto',
      override: {
        header: false,
        mutator: {
          path: './src/lib/axiosInstance.ts',
          name: 'axiosMutator',
        },
      },
    },
  },
  triblyZod: {
    input: {
      target: '../contracts/openapi.json',
    },
    output: {
      mode: 'tags-split',
      client: 'zod',
      target: 'src/api/zod',
      fileExtension: '.zod.ts',
      override: {
        header: false,
      }
    },
  },
});