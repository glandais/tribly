# store-metadata

Store-submission declarations that have no home in either store's tooling but must stay under
version control, because getting them wrong means an App Review rejection or a privacy-policy
contradiction.

| File | What it is |
|---|---|
| [`data-safety.md`](data-safety.md) | Source of truth for what the app collects, the iOS privacy manifest contents, the App Store Connect *App Privacy* answers, and the Google Play *Data safety* answers |
| [`app-privacy.json`](app-privacy.json) | App Store Connect *App Privacy*, as `asc web privacy` reads it — §4 of `data-safety.md`, machine-readable |
| [`data-safety.csv`](data-safety.csv) | Google Play *Data safety*, in the Console's own CSV format — §5 of `data-safety.md`, machine-readable |

## Pushing the two forms

Both forms are applied from these files, never typed into the consoles: a hand edit there is
overwritten by the next push, and would not be in git anyway.

```bash
# App Store Connect — needs a web session (2FA): the provider is the ASC team, not the Developer one
asc web auth login --apple-id "gabriel.landais@gmail.com" --provider-id 128752970
asc web privacy plan    --app 6761760079 --file mobile/store-metadata/app-privacy.json --pretty
asc web privacy apply   --app 6761760079 --file mobile/store-metadata/app-privacy.json --allow-deletes --confirm
asc web privacy publish --app 6761760079 --confirm

# Google Play — the Play API service account from android/fastlane/Appfile
cd mobile/android && PATH=/opt/homebrew/opt/ruby/bin:$PATH bundle exec fastlane data_safety
```

`asc web privacy plan` diffs against what Apple holds, so it doubles as a drift check. Play's
API is **write-only**: to compare, export the form (*Contenu de l'application → Sécurité des
données → Exporter au format CSV*) and diff it against `data-safety.csv`. A Play push then sits in
*Vue d'ensemble de la publication* until someone sends it for review.

Editing the CSV: every row of the Console template is kept, answered or not, because the import
expects the full questionnaire. Start from a fresh export if Google adds questions.

Listing copy and screenshots live elsewhere and are not duplicated here:
`mobile/android/fastlane/metadata/` (Play) and App Store Connect (iOS).

**TODO**: `android/fastlane/metadata/android/en-US/images/phoneScreenshots/` currently reuses the
`fr-FR` screenshots as a placeholder (French UI copy, dev seed data — "Bonjour, Gaby!", "Publi
test"). Retake them from a clean English-locale device/simulator before the English listing goes
live.

## Rule

`data-safety.md`, `mobile/ios/Runner/PrivacyInfo.xcprivacy`, the two store forms, and
`privacy/privacy-policy.{en,fr}.md` describe the same thing in five places. Change one and
you must change the others in the same commit, or they drift and the drift is invisible until a
reviewer finds it.

`data-safety.md` §8 lists which feature additions force a re-declaration, and the "How to
re-verify" section at its end is a copy-pasteable audit of the code the declarations rest on.
