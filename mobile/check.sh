#!/usr/bin/env bash
set -e
cd "$(dirname "${BASH_SOURCE[0]}")"

# Install dependencies
flutter pub get

# Generate API clients and models from OpenAPI
dart run openapi_retrofit_generator

# Generate freezed/json_serializable code
dart run build_runner build

../format.sh mobile

flutter analyze

if [ -d test ]; then
  flutter test
else
  echo "No test directory found, skipping tests"
fi
