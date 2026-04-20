/// Current UI locale for path generation. Synchronised from
/// `context.locale.languageCode` in `PedalonsApp.build()` — read statically
/// by [Paths] builders to return locale-aware URLs.
String _currentLocale = 'fr';

String getCurrentLocale() => _currentLocale;

void setCurrentLocale(String locale) {
  if (_currentLocale == locale) return;
  _currentLocale = locale;
}
