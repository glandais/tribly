---
name: contract-first-api
description: Generate OpenAPI contract from backend and regenerate frontend/mobile API clients
---

# Contract-First API Workflow

Run this skill after modifying backend REST resources or DTOs to sync the OpenAPI contract and regenerate clients.

## Steps

### 1. Generate OpenAPI Contract

```bash
cd backend && mvn package -DskipTests
```

This generates:
- `contracts/openapi.yaml`
- `contracts/openapi.json`

### 2. Regenerate Frontend Client

```bash
cd frontend && pnpm generate-api
```

This runs Orval to generate:
- `src/api/dto/` - TypeScript DTOs
- `src/api/endpoints/` - API functions
- `src/api/zod/` - Zod validation schemas

Then verify no TypeScript errors:

```bash
cd frontend && pnpm build
```

### 3. Regenerate Mobile Client

```bash
cd mobile && dart run openapi_retrofit_generator && dart run build_runner build
```

This generates:
- `lib/api/generated/clients/` - Retrofit API clients
- `lib/api/generated/models/` - Freezed DTOs

Then verify no Dart errors:

```bash
cd mobile && flutter analyze
```

## After Running

1. Report any errors from the generation or verification steps
2. If there are TypeScript or Dart errors, help fix them

## Common Issues

- **Empty schemas in OpenAPI**: Missing `@Schema(implementation = ...)` in `@APIResponse` annotations
- **Orval errors**: Usually caused by invalid OpenAPI spec - check backend annotations
- **Mobile build_runner conflicts**: `build_runner build` deletes conflicting outputs by default (the old `--delete-conflicting-outputs` flag was removed in build_runner 2.5.0)
