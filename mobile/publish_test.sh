#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"

# Use the Homebrew Ruby (which carries the bundler version pinned in the
# fastlane Gemfile.lock) instead of the macOS system Ruby 2.6.
if command -v brew >/dev/null 2>&1; then
  export PATH="$(brew --prefix ruby)/bin:$PATH"
fi

# Bump build number in pubspec.yaml (the +N suffix). Single source of truth for
# both iOS (CFBundleVersion via $(FLUTTER_BUILD_NUMBER)) and Android (versionCode).
perl -i -pe 's/^(version:\s*\d+\.\d+\.\d+\+)(\d+)\s*$/$1 . ($2 + 1) . "\n"/e' pubspec.yaml
NEW_VERSION=$(grep -E '^version:' pubspec.yaml | awk '{print $2}')
echo ">>> Building $NEW_VERSION"

flutter build ios --release --no-codesign
(cd ios && bundle exec fastlane beta --verbose)

export JAVA_HOME="$(/usr/libexec/java_home -v 21)"
export PATH="$JAVA_HOME/bin:$PATH"
flutter build appbundle --release
(cd android && bundle exec fastlane internal --verbose)
