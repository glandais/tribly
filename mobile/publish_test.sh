#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"

# Bump build number in pubspec.yaml (the +N suffix). Single source of truth for
# both iOS (CFBundleVersion via $(FLUTTER_BUILD_NUMBER)) and Android (versionCode).
perl -i -pe 's/^(version:\s*\d+\.\d+\.\d+\+)(\d+)\s*$/$1 . ($2 + 1) . "\n"/e' pubspec.yaml
NEW_VERSION=$(grep -E '^version:' pubspec.yaml | awk '{print $2}')
echo ">>> Building $NEW_VERSION"

flutter build ios --release --no-codesign
(cd ios && fastlane beta --verbose)

export JAVA_HOME="$(/usr/libexec/java_home -v 21)"
export PATH="$JAVA_HOME/bin:$PATH"
flutter build appbundle --release
(cd android && fastlane internal --verbose)
