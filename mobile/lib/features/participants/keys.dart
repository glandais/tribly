import 'package:flutter/widgets.dart';

class _ParticipantsSheetKey extends ValueKey<String> {
  const _ParticipantsSheetKey(String value) : super('participantsSheet_$value');
}

class ParticipantsSheetKeys {
  /// La pastille du total, dans l'en-tête de la feuille.
  final count = const _ParticipantsSheetKey('count');

  /// La ligne d'un participant, par son identifiant.
  ValueKey<String> person(String userId) =>
      _ParticipantsSheetKey('person_$userId');
}
