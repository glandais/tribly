---
name: contract-first-api
description: Generate OpenAPI contract from backend and regenerate frontend/mobile API clients
---

# Contract-First API Workflow

Run this skill after modifying backend REST resources or DTOs to sync the OpenAPI contract and regenerate the frontend/mobile clients.

## One-shot (preferred)

The repo root has `regenerate.sh`, which runs the whole chain end-to-end and fails fast (`set -e`):

```bash
bash regenerate.sh
```

It performs, in order:
1. `cd backend && mvn clean package -DskipTests` — generates `contracts/openapi.{yaml,json}`
2. `cd frontend && pnpm check` — see below
3. `cd mobile && bash check.sh` — see below

Prefer this over running the steps by hand so nothing drifts out of sync.

## Steps (what the scripts do)

### 1. Generate OpenAPI Contract

```bash
cd backend && mvn clean package -DskipTests
```

Generates `contracts/openapi.yaml` and `contracts/openapi.json`.

### 2. Regenerate Frontend Client — `pnpm check`

```bash
cd frontend && pnpm check
```

`pnpm check` expands to: `pnpm install && pnpm generate-api && pnpm generate-routes && pnpm format && pnpm typecheck && pnpm lint && pnpm build`.

- `generate-api` runs Orval → `src/api/dto/`, `src/api/endpoints/`, `src/api/zod/`
- `generate-routes` regenerates the UI routes contract (`paths.generated.*`, AASA, deeplinks) from `contracts/routes.yaml` — **don't skip this**; it's part of the contract surface
- `typecheck` (`tsgo -b`) is the real type gate — not `build`

### 3. Regenerate Mobile Client — `check.sh`

```bash
cd mobile && bash check.sh
```

`check.sh` runs: `flutter pub get && dart run openapi_retrofit_generator && dart run build_runner build && flutter analyze`.

- Generates `lib/api/generated/clients/` (Retrofit) and `lib/api/generated/models/` (Freezed)
- `flutter analyze` verifies no Dart errors

## After Running

1. Report any errors from the generation or verification steps.
2. If there are TypeScript or Dart errors, help fix them.

## Common Issues

- **Empty schemas in OpenAPI**: Missing `@Schema(implementation = ...)` in `@APIResponse` annotations
- **Orval errors**: Usually caused by an invalid OpenAPI spec — check backend annotations
- **Mobile build_runner conflicts**: `build_runner build` deletes conflicting outputs by default (the old `--delete-conflicting-outputs` flag was removed in build_runner 2.5.0)
