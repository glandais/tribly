#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"

./clean.sh

# Use the Homebrew Ruby (which carries the bundler version pinned in the
# fastlane Gemfile.lock) instead of the macOS system Ruby 2.6.
if command -v brew >/dev/null 2>&1; then
  export PATH="$(brew --prefix ruby)/bin:$PATH"
fi

# Each Gemfile.lock pins a bundler version (BUNDLED WITH). A Homebrew Ruby
# upgrade removes that gem's directory while leaving its gemspec behind, so the
# `bundle` shim dies with a LoadError before it even parses a subcommand — and
# `bundle check || bundle install` then aborts the script *after* the version
# bump has been committed and pushed. Reinstall the pinned version when the
# shim can no longer run. Call this from the directory holding the Gemfile.lock.
ensure_bundler() {
  bundle --version >/dev/null 2>&1 && return 0
  local pinned
  pinned=$(awk '/^BUNDLED WITH$/ { getline; gsub(/[[:space:]]/, ""); print; exit }' Gemfile.lock)
  echo ">>> bundler $pinned unusable in $(pwd), reinstalling"
  gem install bundler -v "$pinned"
}

# Bump build number in pubspec.yaml (the +N suffix). Single source of truth for
# both iOS (CFBundleVersion via $(FLUTTER_BUILD_NUMBER)) and Android (versionCode).
perl -i -pe 's/^(version:\s*\d+\.\d+\.\d+\+)(\d+)\s*$/$1 . ($2 + 1) . "\n"/e' pubspec.yaml
NEW_VERSION=$(grep -E '^version:' pubspec.yaml | awk '{print $2}')
echo ">>> Building $NEW_VERSION"

git add pubspec.yaml
git commit -m "chore(mobile): bump build number to $NEW_VERSION"
git push

flutter build ios --release --no-codesign
(cd ios && ensure_bundler && { bundle check || bundle install; })
(cd ios && bundle exec fastlane beta --verbose)

export JAVA_HOME="$(/usr/libexec/java_home -v 21)"
export PATH="$JAVA_HOME/bin:$PATH"
flutter build appbundle --release
(cd android && ensure_bundler && { bundle check || bundle install; })
(cd android && bundle exec fastlane internal --verbose)

# Stop the Gradle daemon spawned for this build so it doesn't linger holding JDK 21.
(cd android && ./gradlew --stop)
