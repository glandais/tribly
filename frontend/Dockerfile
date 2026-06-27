####
# Build stage
####
FROM node:26.4.0-alpine AS build

WORKDIR /app

RUN npm install --global --force corepack@latest && \
    corepack enable && \
    corepack prepare pnpm@latest-10 --activate && \
    pnpm config set store-dir .pnpm-store

# Copy package files
COPY package.json pnpm-lock.yaml ./

# Install dependencies
RUN pnpm install

# Copy source code (legal markdown files pre-copied by build.sh)
COPY . .

# Build the application
RUN pnpm build

####
# Runtime stage with Nginx
####
FROM steebchen/nginx-spa:stable

# Copy the built application
COPY --from=build /app/dist/ /app

# Override default Nginx config to serve apple-app-site-association as JSON
COPY nginx-spa.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx"]
