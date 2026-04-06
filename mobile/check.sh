# Install dependencies
flutter pub get

# Generate API clients and models from OpenAPI
dart run openapi_retrofit_generator

# Generate freezed/json_serializable code
dart run build_runner build --delete-conflicting-outputs

flutter analyze
