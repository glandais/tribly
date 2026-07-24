#!/bin/bash
set -e

flutter clean

# iOS — plugins ship as Swift Packages, resolved at build time (no CocoaPods)
rm -rf ios/.symlinks ios/Flutter/ephemeral

# Android
rm -rf android/.gradle android/app/build android/build

# Reinstall + regenerate
flutter pub get
dart run openapi_retrofit_generator
dart run build_runner build
