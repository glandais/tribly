import { defineConfig } from 'orval';

export default defineConfig({
  petstore: {
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
        mutator: {
          path: './src/lib/axiosInstance.ts',
          name: 'axiosMutator',
        },
      },
    },
  },
  petstoreZod: {
    input: {
      target: '../contracts/openapi.json',
    },
    output: {
      mode: 'tags-split',
      client: 'zod',
      target: 'src/api/zod',
      fileExtension: '.zod.ts',
    },
  },
});