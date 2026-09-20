import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/widgets.dart';

import '../../../api/generated/export.dart';
import '../../../config/paths.dart';
import '../../../core/theme/pdl_icons.dart';

/// Ce qu'une notification dit, montre, et où elle mène.
///
/// L'API ne transporte **aucun texte rendu** (`docs/plans/2026-09-18-notifications.md`
/// §3) : une notification est un type et des champs structurés, et c'est le
/// client qui la formule dans la langue du lecteur — même principe que les
/// `ErrorCode`. Le libellé, l'icône et la destination vivent donc ici, et pas
/// sur le serveur.
extension NotificationDisplay on NotificationDto {
  NotificationType get typeEnum => NotificationType.fromJson(type);

  NotificationSubjectType get subjectTypeEnum =>
      NotificationSubjectType.fromJson(subjectType);

  /// La phrase affichée en titre.
  ///
  /// `actorName` manque pour une publication programmée (aucun acteur humain) :
  /// l'équipe prend alors sa place, plutôt que de laisser un trou dans la
  /// phrase.
  String title() {
    return 'notifications.type.$type'.tr(
      namedArgs: <String, String>{
        'actor': actorName ?? teamName,
        'team': teamName,
      },
    );
  }

  /// L'icône du type. Une annulation se lit comme une annulation d'un coup
  /// d'œil, quel que soit le sujet.
  IconData icon() => switch (typeEnum) {
    NotificationType.ridePublished => PdlIcons.ride,
    NotificationType.rideCancelled => PdlIcons.cancelled,
    NotificationType.tripPublished => PdlIcons.trip,
    NotificationType.tripCancelled => PdlIcons.cancelled,
    NotificationType.postPublished => PdlIcons.post,
    NotificationType.commentReply => PdlIcons.comment,
    NotificationType.$unknown => PdlIcons.notifications,
  };

  /// L'écran que la notification ouvre, déduit du **sujet** et jamais du type :
  /// un type de plus sur un sujet existant n'a donc rien à ajouter ici.
  ///
  /// `null` pour un sujet qu'une version plus ancienne de l'app ne connaît pas
  /// — la ligne reste lisible, elle n'est simplement pas cliquable, ce qui vaut
  /// mieux qu'un `go` vers une route inexistante.
  String? path() => switch (subjectTypeEnum) {
    NotificationSubjectType.ride => Paths.ride(teamSlug, subjectSlug),
    NotificationSubjectType.trip => Paths.trip(teamSlug, subjectSlug),
    NotificationSubjectType.post => Paths.post(teamSlug, subjectSlug),
    NotificationSubjectType.route => Paths.route(teamSlug, subjectSlug),
    NotificationSubjectType.$unknown => null,
  };
}
