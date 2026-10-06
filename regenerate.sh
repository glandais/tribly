#!/usr/bin/env bash
set -e

cd backend
mvn clean package -DskipTests
# The mobile client is generated before `pnpm check`: its generate-brand-colors reads the Freezed
# model of every coloured enum (docs/LEDGER_*.md BRAND-5), so a new enum needs it to exist first.
cd ../mobile
flutter pub get
dart run openapi_retrofit_generator
dart run build_runner build
cd ../frontend
pnpm check
cd ../mobile
bash check.sh
