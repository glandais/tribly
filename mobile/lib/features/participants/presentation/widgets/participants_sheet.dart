import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../api/generated/export.dart';
import '../../../../core/pdl/pdl.dart';
import '../../../../core/theme/enum_colors.dart';
import '../../../../core/theme/pdl_colors.dart';
import '../../../../core/theme/pdl_typography.dart';
import '../../../auth/domain/auth_state.dart';
import '../../../auth/providers/auth_provider.dart';

/// La feuille « Participants » d'un groupe de sortie ou d'un voyage.
///
/// **Recherche côté client** : les participants sont embarqués dans le détail
/// de la sortie comme dans celui du voyage, non paginés (§5.2-12). Filtrer
/// localement une liste déjà en mémoire est la bonne réponse ; un pied
/// « N sur M » serait un mensonge puisqu'il n'y a pas de seconde page — le
/// titre porte donc le total simple.
class ParticipantsSheet extends ConsumerStatefulWidget {
  const ParticipantsSheet({
    super.key,
    required this.subtitle,
    required this.people,
    required this.count,
    this.organizerId,
    this.emptyMessageKey = 'participants.emptyMessage',
  });

  /// Ce à quoi ces gens participent : le nom du groupe, ou celui du voyage.
  final String subtitle;

  final List<PublicUserDto> people;

  /// Le total affiché en pastille — celui du serveur, pas `people.length`, qui
  /// vaut zéro sans droit de lecture.
  final int count;

  /// Le meneur **désigné**, quand il y en a un. `null` est le cas courant, et
  /// il n'y a jamais de repli sur `createdBy`.
  final String? organizerId;

  final String emptyMessageKey;

  /// Ouvre la feuille au-dessus de la barre d'onglets.
  ///
  /// `PdlSheet.show` force `useRootNavigator: true` : c'était l'unique cause de
  /// F-DE-8, où les feuilles s'ouvraient *dans* la branche de la coquille et
  /// passaient sous la barre.
  static Future<void> open(BuildContext context, RideGroupDto group) {
    return PdlSheet.show<void>(
      context: context,
      builder: (BuildContext _) => ParticipantsSheet(
        subtitle: group.name,
        people: group.participants,
        count: group.countParticipants,
        organizerId: group.leader?.id,
      ),
    );
  }

  /// La même feuille pour un voyage : pas de meneur, pas de groupe.
  static Future<void> openTrip(BuildContext context, TripDto trip) {
    return PdlSheet.show<void>(
      context: context,
      builder: (BuildContext _) => ParticipantsSheet(
        subtitle: trip.name,
        people: trip.participants,
        count: trip.participantCount,
        emptyMessageKey: 'participants.emptyTripMessage',
      ),
    );
  }

  @override
  ConsumerState<ParticipantsSheet> createState() => _ParticipantsSheetState();
}

class _ParticipantsSheetState extends ConsumerState<ParticipantsSheet> {
  String _search = '';

  @override
  Widget build(BuildContext context) {
    final PdlColors c = context.pdl;
    final PdlTypography t = context.pdlText;
    final String? currentUserId = ref.watch(
      authProvider.select((AuthState s) => s.user?.id),
    );

    final List<PublicUserDto> all = widget.people;
    final String needle = _search.trim().toLowerCase();
    final List<PublicUserDto> shown = needle.isEmpty
        ? all
        : all
              .where(
                (PublicUserDto p) =>
                    p.displayName.toLowerCase().contains(needle),
              )
              .toList();

    // `leader` nul est le **cas courant** : la plupart des groupes n'en
    // désignent pas. Rien n'est alors rendu — et surtout jamais un repli sur
    // `createdBy`, qui vaut le créateur de la sortie sur tous ses groupes.
    final String? leaderId = widget.organizerId;

    return PdlSheet(
      header: Row(
        children: <Widget>[
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: <Widget>[
                Text('participants.title'.tr(), style: t.sectionTitle),
                Text(
                  widget.subtitle,
                  style: t.xs,
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                ),
              ],
            ),
          ),
          PdlBadge(
            label: '${widget.count}',
            tone: PdlDerivedTones.registered(c),
            size: PdlBadgeSize.lg,
          ),
        ],
      ),
      children: <Widget>[
        if (all.length > 8)
          Padding(
            padding: const EdgeInsets.fromLTRB(16, 0, 16, 12),
            child: PdlSearchField(
              value: _search,
              hintText: 'participants.search'.tr(),
              onChanged: (String? v) => setState(() => _search = v ?? ''),
              clearTooltip: 'common.clearSearch'.tr(),
            ),
          ),
        if (all.isEmpty)
          PdlEmptyState(
            variant: PdlEmptyVariant.empty,
            title: 'participants.emptyTitle'.tr(),
            message: widget.emptyMessageKey.tr(),
          )
        else if (shown.isEmpty)
          PdlEmptyState(
            variant: PdlEmptyVariant.filtered,
            title: 'participants.noMatchTitle'.tr(),
            message: 'participants.noMatchMessage'.tr(
              namedArgs: <String, String>{'search': _search},
            ),
            actions: <Widget>[
              PdlButton(
                label: 'common.clearSearch'.tr(),
                variant: PdlButtonVariant.outline,
                onPressed: () => setState(() => _search = ''),
              ),
            ],
          )
        else
          for (final PublicUserDto person in shown)
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              child: PdlPersonRow(
                name: person.displayName,
                imageUrl: person.avatarUrl,
                isCurrentUser: person.id == currentUserId,
                // Conditionnel deux fois : il faut un meneur désigné **et**
                // qu'il participe à ce groupe. Un meneur qui ne roule pas
                // n'apparaît pas dans cette liste, et rien ne le signale ici.
                organizerFlag: leaderId != null && person.id == leaderId,
                organizerLabel: 'participants.groupOrganizer'.tr(),
              ),
            ),
      ],
    );
  }
}
