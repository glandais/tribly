# store-metadata

Store-submission declarations that have no home in either store's tooling but must stay under
version control, because getting them wrong means an App Review rejection or a privacy-policy
contradiction.

| File | What it is |
|---|---|
| [`data-safety.md`](data-safety.md) | Source of truth for what the app collects, the iOS privacy manifest contents, the App Store Connect *App Privacy* answers, and the Google Play *Data safety* answers |

Listing copy and screenshots live elsewhere and are not duplicated here:
`mobile/android/fastlane/metadata/` (Play) and App Store Connect (iOS).

## Rule

`data-safety.md`, `mobile/ios/Runner/PrivacyInfo.xcprivacy`, the two store forms, and
`mobile/privacy/privacy-policy.{en,fr}.md` describe the same thing in five places. Change one and
you must change the others in the same commit, or they drift and the drift is invisible until a
reviewer finds it.

`data-safety.md` §8 lists which feature additions force a re-declaration, and the "How to
re-verify" section at its end is a copy-pasteable audit of the code the declarations rest on.
