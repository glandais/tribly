import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_riverpod/legacy.dart';

import 'trip_detail_provider.dart';

/// L'étape sélectionnée sur l'écran 24, par voyage.
///
/// La sélection est **partagée** par la carte, la légende et la liste : c'est
/// elle qui fait des trois des vues du même objet plutôt que trois listes
/// juxtaposées. Elle vit hors des widgets pour survivre à leur reconstruction,
/// et surtout pour que changer de sélection **ne reconstruise pas la carte** —
/// `PdlMap` reçoit l'identifiant et repeint deux couches.
///
/// `autoDispose` : la sélection n'a de sens que sur l'écran ouvert.
final selectedStageProvider = StateProvider.autoDispose
    .family<String?, TripKey>((Ref ref, TripKey key) => null);
