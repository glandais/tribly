/// Nommer un fuseau pour la mention « heure de Tokyo » d'un rendez-vous
/// (docs/LEDGER_*.md API-60, plan §7 « La mention »).
///
/// Le fuseau se nomme par la ville de son identifiant IANA — dernier segment,
/// `_` → espace — avec une petite table française pour les cas courants. Ni le
/// nom long d'`Intl` (absent de Dart), ni l'abréviation, ni le décalage (il
/// change avec l'heure d'été).
///
/// C'est le portage de `frontend/src/utils/zoneLabel.ts` : **les deux tables
/// avancent ensemble**, et `test/core/utils/zone_label_test.dart` reprend les
/// cas de `zoneLabel.test.ts`.
///
/// Le français demande plus que le nom : « heure de Tokyo », mais « heure
/// d'Athènes », « heure du Caire », « heure des Açores ». [zoneCityOf] construit
/// ce complément ; le catalogue français l'emploie (« heure {ofCity} »),
/// l'anglais prend la ville nue ([zoneCityName], « {city} time »).
library;

// Dernier segment IANA → nom français. Seulement là où le français diffère de
// l'identifiant ; le reste retombe sur le segment (Tokyo, Berlin, New York…).
const Map<String, String> _frenchCityNames = <String, String>{
  // Europe
  'Athens': 'Athènes',
  'Brussels': 'Bruxelles',
  'Bucharest': 'Bucarest',
  'Copenhagen': 'Copenhague',
  'Lisbon': 'Lisbonne',
  'London': 'Londres',
  'Moscow': 'Moscou',
  'Vienna': 'Vienne',
  'Warsaw': 'Varsovie',
  // Afrique
  'Algiers': 'Alger',
  'Cairo': 'Le Caire',
  // Amériques
  'Mexico_City': 'Mexico',
  'Montreal': 'Montréal',
  'Sao_Paulo': 'São Paulo',
  // Asie
  'Singapore': 'Singapour',
  // Îles de l'Atlantique, de l'océan Indien et du Pacifique
  'Azores': 'Açores',
  'Canary': 'Canaries',
  'Noumea': 'Nouméa',
  'Reunion': 'La Réunion',
};

// Le complément là où l'élision seule se tromperait : un nom à article
// pluriel, ou que le segment IANA écrit sans lui. `Le …` se contracte
// génériquement (« du Caire »).
const Map<String, String> _frenchCityOf = <String, String>{
  'Azores': 'des Açores',
  'Canary': 'des Canaries',
  'Maldives': 'des Maldives',
};

// Villes au h muet : « d'Helsinki ». Tout autre h est traité comme aspiré
// (« de Hong Kong », « de Hobart »).
const Set<String> _frenchMuteH = <String>{
  'Helsinki',
  'Honolulu',
  'Ho Chi Minh',
};

// `Etc/GMT-9` vaut UTC+9 : POSIX inverse le signe. Le backend en rend en mer
// (plan §4), là où il n'y a aucune ville à nommer.
final RegExp _etcGmt = RegExp(r'^Etc/GMT([+-])(\d{1,2})$');
const Set<String> _utcAliases = <String>{
  'UTC',
  'Etc/UTC',
  'Etc/GMT',
  'Etc/UCT',
  'Etc/Zulu',
  'GMT',
};

final RegExp _frenchVowel = RegExp(
  r'^[AEIOUYÀÂÄÉÈÊËÎÏÔÖÙÛÜŸ]',
  caseSensitive: false,
);

bool _isFrench(String languageCode) =>
    languageCode.toLowerCase().startsWith('fr');

String _segment(String timezone) =>
    timezone.substring(timezone.lastIndexOf('/') + 1);

/// La ville qui nomme [timezone], dans [languageCode] (`fr…` prend la table
/// française, toute autre langue l'orthographe IANA) : `Asia/Tokyo` →
/// « Tokyo », `America/New_York` → « New York », `Europe/London` → « Londres »
/// en français. `Etc/GMT-9` → « UTC+9 », `UTC` → « UTC ».
String zoneCityName(String timezone, String languageCode) {
  if (_utcAliases.contains(timezone)) return 'UTC';
  final RegExpMatch? etc = _etcGmt.firstMatch(timezone);
  if (etc != null) {
    final int hours = int.parse(etc.group(2)!);
    if (hours == 0) return 'UTC';
    return 'UTC${etc.group(1) == '-' ? '+' : '-'}$hours';
  }
  final String segment = _segment(timezone);
  final String? french = _isFrench(languageCode)
      ? _frenchCityNames[segment]
      : null;
  return french ?? segment.replaceAll('_', ' ');
}

/// Le complément qui nomme [timezone], pour « heure {ofCity} » : en français
/// « de Tokyo », « d'Athènes », « du Caire », « des Açores », « de La
/// Réunion », et « UTC » / « UTC+9 » nus (« heure UTC ») ; dans toute autre
/// langue, la ville elle-même, comme [zoneCityName].
String zoneCityOf(String timezone, String languageCode) {
  final String city = zoneCityName(timezone, languageCode);
  if (!_isFrench(languageCode)) return city;
  if (city.startsWith('UTC')) return city;
  final String? fixed = _frenchCityOf[_segment(timezone)];
  if (fixed != null) return fixed;
  if (city.startsWith('Le ')) return 'du ${city.substring(3)}';
  if (city.startsWith('Les ')) return 'des ${city.substring(4)}';
  if (_frenchVowel.hasMatch(city) || _frenchMuteH.contains(city)) {
    return "d'$city";
  }
  return 'de $city';
}
