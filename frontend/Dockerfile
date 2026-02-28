####
# Build stage
####
FROM node:25.7.0-alpine AS build

WORKDIR /app

RUN npm install --global --force corepack@latest && \
    corepack enable && \
    corepack prepare pnpm@latest-10 --activate && \
    pnpm config set store-dir .pnpm-store

# Copy package files
COPY package.json pnpm-lock.yaml ./

# Install dependencies
RUN pnpm install

# Copy source code
COPY . .

# Build the application
RUN pnpm build

####
# Runtime stage with Nginx
####
FROM steebchen/nginx-spa:stable

# Copy the built application
COPY --from=build /app/dist/ /app

EXPOSE 80

CMD ["nginx"]