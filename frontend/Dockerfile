####
# Build stage
####
FROM node:26.5.0-alpine AS build

WORKDIR /app

RUN npm install --global --force corepack@latest && \
    corepack enable && \
    corepack prepare pnpm@latest-10 --activate && \
    pnpm config set store-dir .pnpm-store

# Copy package files
COPY package.json pnpm-lock.yaml pnpm-workspace.yaml ./

# Install dependencies
RUN pnpm install

# Copy source code (legal markdown files pre-copied by build.sh)
COPY . .

# Build client + server bundles (dist/client + dist/server)
RUN pnpm build

# Prune to production dependencies only (drops vite, typescript, etc.)
RUN pnpm prune --prod

####
# Runtime stage with Node.js for SSR
####
FROM node:26.5.0-alpine

WORKDIR /app
ENV NODE_ENV=production

# Copy built bundles, server entry, and production node_modules
COPY --from=build /app/dist/ ./dist/
COPY --from=build /app/server.js ./
COPY --from=build /app/node_modules/ ./node_modules/
COPY --from=build /app/package.json ./

EXPOSE 3000

USER node

CMD ["node", "server.js"]
