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
  ///
  /// Une modification de sortie dit **ce qui** a changé : « une sortie a été
  /// modifiée » obligerait à l'ouvrir pour savoir s'il faut se lever plus tôt
  /// ou aller ailleurs.
  String title() {
    final String key = typeEnum == NotificationType.rideUpdated
        ? 'notifications.rideUpdated.${_rideUpdatedVariant()}'
        : 'notifications.type.$type';
    return key.tr(
      namedArgs: <String, String>{
        'actor': actorName ?? teamName,
        'team': teamName,
      },
    );
  }

  /// La variante du titre d'une modification, selon `changes`. Une liste vide
  /// ou faite de changements qu'une version plus ancienne ne connaît pas
  /// retombe sur la phrase générique plutôt que de ne rien dire.
  String _rideUpdatedVariant() {
    final bool date = changes.contains(NotificationChange.dateTime);
    final bool place = changes.contains(NotificationChange.startPlace);
    if (date && place) return 'both';
    if (date) return 'dateTime';
    if (place) return 'startPlace';
    return 'other';
  }

  /// La ligne sous le sujet, ou `null` s'il n'y a rien à ajouter.
  ///
  /// L'`excerpt` est une citation — le commentaire — sauf pour une
  /// inscription, où il porte le nom du groupe : le citer en italique tel quel
  /// le ferait passer pour un message.
  String? detail() {
    final String? text = excerpt;
    if (text == null || text.isEmpty) return null;
    if (typeEnum == NotificationType.rideJoined) {
      return 'notifications.joinedGroup'.tr(
        namedArgs: <String, String>{'group': text},
      );
    }
    return text;
  }

  /// `true` quand [detail] est une citation, rendue en italique.
  bool get detailIsQuote => typeEnum != NotificationType.rideJoined;

  /// L'icône du type. Une annulation se lit comme une annulation d'un coup
  /// d'œil, quel que soit le sujet.
  IconData icon() => switch (typeEnum) {
    NotificationType.ridePublished => PdlIcons.ride,
    NotificationType.rideCancelled => PdlIcons.cancelled,
    NotificationType.tripPublished => PdlIcons.trip,
    NotificationType.tripCancelled => PdlIcons.cancelled,
    NotificationType.postPublished => PdlIcons.post,
    NotificationType.commentReply => PdlIcons.comment,
    NotificationType.rideReminder => PdlIcons.reminder,
    NotificationType.rideUpdated => PdlIcons.edit,
    NotificationType.rideJoined => PdlIcons.personAdd,
    NotificationType.commentOnMyPublication => PdlIcons.comment,
    NotificationType.teamInvitation => PdlIcons.invitation,
    NotificationType.contentReported => PdlIcons.report,
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
    // Une invitation mène à la liste des équipes : c'est là que les
    // invitations en attente s'acceptent, et la page de l'équipe elle-même
    // peut être fermée à qui n'en est pas encore membre.
    NotificationSubjectType.team => Paths.teams(),
    // La file de signalements n'a pas d'écran dans l'app : voir [webPath].
    NotificationSubjectType.report => null,
    NotificationSubjectType.$unknown => null,
  };

  /// La page **du site** que la notification ouvre, quand l'app n'a pas
  /// d'écran pour elle — `null` sinon.
  ///
  /// Un signalement se décide dans la file de l'équipe, qui n'existe que sur
  /// le web : c'est le lien même que le serveur met dans le push. Son chemin
  /// est celui de la route `teamAdminReports`, qui n'est pas générée côté
  /// mobile puisqu'elle est `web` seulement.
  String? webPath() => switch (subjectTypeEnum) {
    NotificationSubjectType.report => '/teams/$teamSlug/admin/reports',
    _ => null,
  };
}
