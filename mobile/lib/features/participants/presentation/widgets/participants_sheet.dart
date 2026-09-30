import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../api/generated/export.dart';
import '../../../../core/pagination/pagination.dart';
import '../../../../core/pdl/pdl.dart';
import '../../../../core/theme/enum_colors.dart';
import '../../../../core/theme/pdl_colors.dart';
import '../../../../core/theme/pdl_tokens.dart';
import '../../../../core/theme/pdl_typography.dart';
import '../../../../core/utils/api_error_handler.dart';
import '../../../auth/domain/auth_state.dart';
import '../../../auth/providers/auth_provider.dart';
import '../../../moderation/presentation/moderation_menu.dart';
import '../../../../keys.dart';
import '../../domain/participant_query.dart';
import '../../providers/participant_list_provider.dart';

/// La feuille « Participants » d'un groupe de sortie, d'une sortie entière ou
/// d'un voyage.
///
/// **Lue et cherchée côté serveur** (ledger `API-12`) : le détail n'embarque
/// que les premiers participants, de quoi dessiner des avatars. La feuille
/// lit la liste complète page par page, la recherche part au serveur, et le
/// pied dit « N participants sur M » — M étant le total que le serveur renvoie
/// avec la page.
class ParticipantsSheet extends ConsumerStatefulWidget {
  const ParticipantsSheet({
    super.key,
    required this.subtitle,
    required this.source,
    required this.count,
    this.organizerId,
    this.emptyMessageKey = 'participants.emptyMessage',
    this.team,
  });

  /// L'équipe de la sortie ou du voyage : c'est elle qui modère un membre
  /// signalé depuis cette liste. Sans elle, les lignes ne s'ouvrent pas.
  final TeamPublicationDto? team;

  /// Ce à quoi ces gens participent : le nom du groupe, ou celui du voyage.
  final String subtitle;

  /// De qui lister les participants.
  final ParticipantSource source;

  /// Le total affiché en pastille, connu du détail avant la première page.
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
  static Future<void> open(
    BuildContext context,
    RideDto ride,
    RideGroupDto group,
  ) {
    return PdlSheet.show<void>(
      context: context,
      builder: (BuildContext _) => ParticipantsSheet(
        subtitle: group.name,
        source: RideParticipantSource(
          teamSlug: ride.team.slug,
          rideSlug: ride.slug,
          groupId: group.id,
        ),
        count: group.countParticipants,
        organizerId: group.leader?.id,
        team: ride.team,
      ),
    );
  }

  /// La même feuille pour une sortie entière, tous groupes confondus.
  ///
  /// Le bouton « voir la liste » affiche le total de la sortie
  /// (`ride.participantCount`) : la feuille liste donc les participants de
  /// **chaque** groupe. `organizerId` reste nul — une sortie n'a pas de meneur
  /// unique, chaque groupe a le sien (ou aucun), et rien ne justifie d'en
  /// mettre un en avant ici.
  static Future<void> openRide(BuildContext context, RideDto ride) {
    return PdlSheet.show<void>(
      context: context,
      builder: (BuildContext _) => ParticipantsSheet(
        subtitle: ride.name,
        source: RideParticipantSource(
          teamSlug: ride.team.slug,
          rideSlug: ride.slug,
        ),
        count: ride.participantCount,
        team: ride.team,
      ),
    );
  }

  /// La même feuille pour un voyage : pas de meneur, pas de groupe.
  static Future<void> openTrip(BuildContext context, TripDto trip) {
    return PdlSheet.show<void>(
      context: context,
      builder: (BuildContext _) => ParticipantsSheet(
        subtitle: trip.name,
        source: TripParticipantSource(
          teamSlug: trip.team.slug,
          tripSlug: trip.slug,
        ),
        count: trip.participantCount,
        emptyMessageKey: 'participants.emptyTripMessage',
        team: trip.team,
      ),
    );
  }

  @override
  ConsumerState<ParticipantsSheet> createState() => _ParticipantsSheetState();
}

class _ParticipantsSheetState extends ConsumerState<ParticipantsSheet> {
  /// La recherche partie au serveur — `PdlSearchField` débat déjà la saisie.
  String _search = '';

  Future<void> _openMemberMenu(PublicUserDto person) {
    final TeamPublicationDto team = widget.team!;
    return showModerationMenu(
      context,
      title: person.displayName,
      subject: ModerationSubject(
        teamSlug: team.slug,
        type: ReportTargetType.member,
        id: person.id,
        teamName: team.name,
        memberName: person.displayName,
      ),
      blockUserId: person.id,
      blockUserName: person.displayName,
    );
  }

  @override
  Widget build(BuildContext context) {
    final PdlColors c = context.pdl;
    final PdlTypography t = context.pdlText;
    final String? currentUserId = ref.watch(
      authProvider.select((AuthState s) => s.user?.id),
    );

    final ParticipantQuery query = ParticipantQuery(
      widget.source,
      search: _search,
    );
    final PagedListState<PublicUserDto> state = ref.watch(
      participantListProvider(query),
    );
    final ParticipantListNotifier notifier = ref.read(
      participantListProvider(query).notifier,
    );

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
            key: keys.participants.count,
            label: '${widget.count}',
            tone: PdlDerivedTones.registered(c),
            size: PdlBadgeSize.lg,
          ),
        ],
      ),
      children: <Widget>[
        // Au-delà de ce que le détail embarque, la liste ne tient plus d'un
        // coup d'œil : la recherche devient utile.
        if (widget.count > 8)
          Padding(
            padding: const EdgeInsets.fromLTRB(16, 0, 16, 12),
            child: PdlSearchField(
              value: _search,
              hintText: 'participants.search'.tr(),
              onChanged: (String? v) =>
                  setState(() => _search = (v ?? '').trim()),
              clearTooltip: 'common.clearSearch'.tr(),
            ),
          ),
        if (state.isLoadingInitial && state.items.isEmpty)
          const Padding(
            padding: EdgeInsets.all(24),
            child: Center(child: CircularProgressIndicator.adaptive()),
          )
        else if (state.initialError != null)
          PdlEmptyState(
            variant: PdlEmptyVariant.error,
            title: 'common.loadError'.tr(),
            message: getErrorMessage(state.initialError!),
            actions: <Widget>[
              PdlButton(
                label: 'common.retry'.tr(),
                variant: PdlButtonVariant.outline,
                size: PdlButtonSize.sm,
                onPressed: notifier.loadFirstPage,
              ),
            ],
          )
        else if (state.items.isEmpty && _search.isEmpty)
          PdlEmptyState(
            variant: PdlEmptyVariant.empty,
            title: 'participants.emptyTitle'.tr(),
            message: widget.emptyMessageKey.tr(),
          )
        else if (state.items.isEmpty)
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
        else ...<Widget>[
          for (final PublicUserDto person in state.items)
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              child: PdlPersonRow(
                key: keys.participants.person(person.id),
                name: person.displayName,
                imageUrl: person.avatarUrl,
                isCurrentUser: person.id == currentUserId,
                // Conditionnel deux fois : il faut un meneur désigné **et**
                // qu'il participe à ce groupe. Un meneur qui ne roule pas
                // n'apparaît pas dans cette liste, et rien ne le signale ici.
                organizerFlag: leaderId != null && person.id == leaderId,
                organizerLabel: 'participants.groupOrganizer'.tr(),
                // Signaler ou bloquer un participant — jamais soi-même. La
                // liste, elle, ne change pas : un blocage ne touche pas
                // l'organisation des sorties.
                onTap: widget.team == null || person.id == currentUserId
                    ? null
                    : () => _openMemberMenu(person),
              ),
            ),
          // Un bouton plutôt qu'un préchargement au défilement : la feuille
          // construit toutes ses lignes d'un coup, un déclenchement « à trois
          // lignes de la fin » y chargerait toutes les pages d'affilée.
          if (state.hasMore && !state.isLoadingNext && state.nextError == null)
            Padding(
              padding: const EdgeInsets.fromLTRB(16, 8, 16, 0),
              child: Column(
                children: <Widget>[
                  // Le pied commun se tait entre deux pages ; ici la liste
                  // attend un geste, elle doit dire où on en est.
                  Text(
                    'pagination.progressNamed'.tr(
                      namedArgs: <String, String>{
                        'loaded': '${state.items.length}',
                        'noun': 'participants.noun'.tr(),
                        'total': '${state.total ?? widget.count}',
                      },
                    ),
                    key: keys.participants.progress,
                    textAlign: TextAlign.center,
                    style: t.xs,
                  ),
                  const SizedBox(height: PdlSpacing.chipGap),
                  PdlButton(
                    key: keys.participants.loadMore,
                    label: 'participants.loadMore'.tr(),
                    variant: PdlButtonVariant.outline,
                    size: PdlButtonSize.sm,
                    onPressed: notifier.loadNextPage,
                  ),
                ],
              ),
            ),
          PagedListFooter(
            key: keys.participants.footer,
            state: state,
            onRetry: notifier.retryNextPage,
            itemNoun: 'participants.noun'.tr(),
          ),
        ],
      ],
    );
  }
}
