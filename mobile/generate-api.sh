#!/bin/bash
set -e

cd "$(dirname "$0")"

# Setup Java (Homebrew install location)
export PATH="/opt/homebrew/opt/openjdk/bin:$PATH"
export JAVA_HOME="/opt/homebrew/opt/openjdk"

# Verify Java is available
if ! command -v java &> /dev/null; then
    echo "❌ Java not found. Please install Java: brew install openjdk"
    exit 1
fi

echo "🔄 Generating Dart API client from OpenAPI spec..."

# Copy the latest OpenAPI spec from contracts
cp ../contracts/openapi.json ./openapi.json
echo "📋 Copied OpenAPI spec from contracts/"

# Clean previous generated code
rm -rf lib/api/generated

# Ensure the openapi-generator JAR is available
JAR_PATH="$HOME/.pub-cache/hosted/pub.dev/openapi_generator_cli-6.1.0/lib/openapi-generator.jar"
if [ ! -f "$JAR_PATH" ]; then
    echo "📥 Downloading openapi-generator JAR..."
    dart run openapi_generator_cli fetch
    # Copy from cache location to expected location
    mkdir -p "$(dirname "$JAR_PATH")"
    cp .dart_tool/openapi_generator_cache/openapi-generator-cli-*.jar "$JAR_PATH"
fi

# Run build_runner to generate API client and serialization code
echo "🔧 Running build_runner..."
dart run build_runner build --delete-conflicting-outputs

echo "✅ API client generated successfully!"
