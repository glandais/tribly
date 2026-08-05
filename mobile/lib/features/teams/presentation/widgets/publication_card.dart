import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart' hide Visibility;
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../api/generated/export.dart';
import '../../../../config/paths.dart';
import '../../../../core/pdl/pdl.dart';
import '../../../../core/preferences/user_preferences_provider.dart';
import '../../../../core/theme/enum_colors.dart';
import '../../../../core/theme/pdl_colors.dart';
import '../../../../core/theme/pdl_icons.dart';
import '../../../../core/theme/pdl_tokens.dart';
import '../../../../core/theme/pdl_typography.dart';
import '../../../../core/utils/formatters.dart';
import '../../../../core/widgets/deleted_badge.dart';
import '../../../posts/domain/post_neighbours.dart';

/// Les quatre hauteurs de bandeau média de la charte, **partagées**.
///
/// Elles étaient recopiées à plusieurs endroits ; une carte de fil et une carte
/// de carrousel finissaient par diverger de deux pixels sans que personne ne
/// sache laquelle avait raison.
abstract final class PdlMediaHeights {
  /// `.media--16x9` — carte hero de l'accueil.
  static const double hero = PdlMetrics.media16x9;

  /// `.media` — carte de fil.
  static const double feed = PdlMetrics.media;

  /// Carte de carrousel.
  static const double carousel = 140;

  /// `.media--120` — carte compacte.
  static const double compact = PdlMetrics.media120;
}

/// La carte d'une publication dans un fil.
///
/// Le `switch` sur l'union scellée est **conservé** de la v1 : c'est la seule
/// chose qui garantisse qu'un nouveau type de publication au contrat casse la
/// compilation plutôt que de disparaître silencieusement du fil.
///
/// Trois corps sont rendus ici. Le quatrième, l'annonce, n'appartient pas à
/// `PublicationDto` — les annonces ont leur propre endpoint et leur propre DTO,
/// et leur carte arrive avec le lot 5.
class PublicationCard extends ConsumerWidget {
  const PublicationCard({
    super.key,
    required this.publication,
    this.showTeamLine = true,
    this.postNeighbours,
  });

  final PublicationDto publication;

  /// Voisins de cette publication **dans le fil courant**, calculés par la
  /// liste qui rend la carte. Ils voyagent dans l'`extra` de la route : le
  /// contrat n'expose aucun voisinage, et une URL partagée n'en porte donc
  /// aucun (§5.1). Sans objet pour une sortie ou un voyage.
  final PostNeighbours? postNeighbours;

  /// Masquée sur un fil d'équipe, où l'équipe est déjà l'écran.
  final bool showTeamLine;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return switch (publication) {
      PublicationDtoRide ride => _RideBody(
        ride: ride,
        showTeamLine: showTeamLine,
      ),
      PublicationDtoPost post => _PostBody(
        post: post,
        showTeamLine: showTeamLine,
        neighbours: postNeighbours,
      ),
      PublicationDtoTrip trip => _TripBody(
        trip: trip,
        showTeamLine: showTeamLine,
      ),
    };
  }
}

// ─────────────────────────────────────────────────────────── ossature commune

/// Ce que les trois corps partagent : bandeau, ligne d'équipe, titre, extrait,
/// pile de badges, ligne sociale et rangée de statistiques.
class _CardShell extends StatelessWidget {
  const _CardShell({
    required this.onTap,
    required this.tone,
    required this.icon,
    required this.title,
    required this.badges,
    this.imageUrl,
    this.teamName,
    this.teamSlug,
    this.excerpt,
    this.trailingThumbnailUrl,
    this.social,
    this.stats = const <PdlStat>[],
  });

  final VoidCallback onTap;
  final PdlMediaTone tone;
  final IconData icon;
  final String title;
  final List<Widget> badges;
  final String? imageUrl;
  final String? teamName;
  final String? teamSlug;
  final String? excerpt;

  /// La vignette carrée du parcours, à droite de la ligne sociale.
  final String? trailingThumbnailUrl;

  /// Avatars et barre de places — propres à chaque type.
  final Widget? social;

  final List<PdlStat> stats;

  @override
  Widget build(BuildContext context) {
    final PdlTypography t = context.pdlText;

    return PdlCard(
      padding: PdlCardPadding.none,
      onTap: onTap,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: <Widget>[
          PdlCardMedia(
            tone: tone,
            icon: icon,
            imageUrl: imageUrl,
            height: PdlMediaHeights.feed,
            borderRadius: BorderRadius.zero,
          ),
          Padding(
            padding: const EdgeInsets.all(PdlSpacing.card),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: <Widget>[
                if (teamName != null)
                  PdlTeamLine(
                    label: teamName!,
                    onTap: teamSlug == null
                        ? null
                        : () => context.push(Paths.team(teamSlug!)),
                  ),
                const SizedBox(height: 4),
                Row(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: <Widget>[
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: <Widget>[
                          Text(
                            title,
                            style: t.cardTitle,
                            maxLines: 1,
                            overflow: TextOverflow.ellipsis,
                          ),
                          if (excerpt != null &&
                              excerpt!.isNotEmpty) ...<Widget>[
                            const SizedBox(height: 2),
                            Text(
                              excerpt!,
                              style: t.sub,
                              maxLines: 2,
                              overflow: TextOverflow.ellipsis,
                            ),
                          ],
                        ],
                      ),
                    ),
                    if (badges.isNotEmpty) ...<Widget>[
                      const SizedBox(width: PdlSpacing.chipGap),
                      PdlBadgeStack(badges: badges),
                    ],
                  ],
                ),
                if (social != null || trailingThumbnailUrl != null) ...<Widget>[
                  const SizedBox(height: PdlSpacing.cardTight),
                  Row(
                    children: <Widget>[
                      Expanded(child: social ?? const SizedBox.shrink()),
                      if (trailingThumbnailUrl != null) ...<Widget>[
                        const SizedBox(width: PdlSpacing.statsNowrap),
                        PdlThumb(
                          imageUrl: trailingThumbnailUrl,
                          size: PdlMetrics.thumbLg,
                        ),
                      ],
                    ],
                  ),
                ],
                if (stats.isNotEmpty) ...<Widget>[
                  const SizedBox(height: PdlSpacing.cardTight),
                  PdlStatRow(stats: stats),
                ],
              ],
            ),
          ),
        ],
      ),
    );
  }
}

/// Grappe d'avatars et barre de places : la « ligne sociale ».
class _SocialLine extends StatelessWidget {
  const _SocialLine({required this.people, required this.count});

  final List<PublicUserDto> people;
  final int count;

  @override
  Widget build(BuildContext context) {
    // **Pas de barre de places ici.** La capacité agrégée n'existe pas sur une
    // ligne de liste (§5.2-5), et une barre remplie au hasard vaut moins que
    // pas de barre du tout. Le compteur brut est dans la rangée de
    // statistiques ; la barre revient sur la carte « Ma prochaine sortie »,
    // qui connaît le groupe et donc sa capacité.
    if (people.isEmpty) return const SizedBox.shrink();
    return PdlAvatarStack(
      people: <PdlAvatarEntry>[
        for (final PublicUserDto p in people)
          PdlAvatarEntry(name: p.displayName, imageUrl: p.avatarUrl),
      ],
      semanticLabel: '$count',
    );
  }
}

/// La vignette thémée : `thumbnailUrl` est le repli servi par l'API pour un
/// client qui ne thème pas ses cartes — ici on thème, donc on choisit.
String? _themed(BuildContext context, String? light, String? dark) =>
    Theme.of(context).brightness == Brightness.dark
    ? (dark ?? light)
    : (light ?? dark);

// ───────────────────────────────────────────────────────────────────── sortie

class _RideBody extends ConsumerWidget {
  const _RideBody({required this.ride, required this.showTeamLine});

  final PublicationDtoRide ride;
  final bool showTeamLine;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final PdlColors c = context.pdl;
    final DateTime? at = DateTime.tryParse(ride.dateTime)?.toLocal();
    final bool isPast = at != null && at.isBefore(DateTime.now());

    return _CardShell(
      onTap: () => context.push(Paths.ride(ride.team.slug, ride.slug)),
      tone: PdlMediaTone.ride,
      icon: PdlIcons.ride,
      imageUrl: _themed(context, ride.thumbnailLightUrl, ride.thumbnailDarkUrl),
      teamName: showTeamLine ? ride.team.name : null,
      teamSlug: ride.team.slug,
      title: ride.name,
      // **`excerpt`, pas `media.markdown`** : en vue compacte le corps n'est
      // même pas envoyé, et l'aplatir côté client rendait des liens bruts.
      excerpt: ride.excerpt,
      badges: <Widget>[
        ...deletedBadgeFirst(ride.deleted),
        PdlBadge(
          label: 'publicationType.ride'.tr(),
          tone: PublicationType.ride.tone(c),
        ),
        if (ride.status == 'CANCELLED')
          PdlBadge(
            label: 'status.cancelled'.tr(),
            tone: Status.cancelled.tone(c),
          )
        else if (isPast)
          PdlBadge(label: 'rides.finished'.tr(), tone: PdlDerivedTones.done(c)),
        // L'apport principal de la v2 sur les listes : savoir, sans ouvrir la
        // sortie, qu'on y est inscrit.
        if (ride.registered)
          PdlBadge(
            label: 'rides.registered'.tr(),
            tone: PdlDerivedTones.registered(c),
            icon: PdlIcons.check,
          ),
      ],
      // La vignette du parcours, à droite de la ligne sociale — c'est la
      // seule image dont on dispose en plus du bandeau.
      trailingThumbnailUrl: ride.routeSlug == null ? null : ride.thumbnailUrl,
      social: _SocialLine(
        people: ride.topParticipants,
        count: ride.participantCount,
      ),
      stats: <PdlStat>[
        if (at != null)
          PdlStat(value: AppFormatters.formatRideDate(at), icon: PdlIcons.date),
        PdlStat(
          value: 'rides.participants'.tr(
            namedArgs: <String, String>{'count': '${ride.participantCount}'},
          ),
          icon: PdlIcons.people,
        ),
        if (ride.groupCount > 0)
          PdlStat(
            value: 'rides.groupCount'.plural(ride.groupCount),
            icon: PdlIcons.terrain,
          ),
        if (ride.commentCount != null && ride.commentCount! > 0)
          PdlStat(
            value: 'comments.count'.plural(ride.commentCount!),
            icon: PdlIcons.comment,
          ),
      ],
    );
  }
}

// ──────────────────────────────────────────────────────────────── publication

class _PostBody extends StatelessWidget {
  const _PostBody({
    required this.post,
    required this.showTeamLine,
    this.neighbours,
  });

  final PublicationDtoPost post;
  final bool showTeamLine;
  final PostNeighbours? neighbours;

  @override
  Widget build(BuildContext context) {
    final PdlColors c = context.pdl;
    final DateTime? at = DateTime.tryParse(post.dateTime)?.toLocal();

    return _CardShell(
      onTap: () => context.push(
        Paths.post(post.team.slug, post.slug),
        extra: neighbours,
      ),
      tone: PdlMediaTone.post,
      icon: PdlIcons.post,
      // Le post n'a pas de variante thémée au contrat : `thumbnailUrl` est
      // sa seule image de bandeau.
      imageUrl: post.thumbnailUrl,
      teamName: showTeamLine ? post.team.name : null,
      teamSlug: post.team.slug,
      title: post.name,
      excerpt: post.excerpt,
      badges: <Widget>[
        ...deletedBadgeFirst(post.deleted),
        PdlBadge(
          label: 'publicationType.post'.tr(),
          tone: PublicationType.post.tone(c),
        ),
        if (post.status == 'DRAFT')
          PdlBadge(label: 'status.draft'.tr(), tone: Status.draft.tone(c)),
      ],
      stats: <PdlStat>[
        if (at != null)
          PdlStat(value: AppFormatters.formatLongDate(at), icon: PdlIcons.date),
        if (post.commentCount != null && post.commentCount! > 0)
          PdlStat(
            value: 'comments.count'.plural(post.commentCount!),
            icon: PdlIcons.comment,
          ),
      ],
    );
  }
}

// ───────────────────────────────────────────────────────────────────── voyage

class _TripBody extends ConsumerWidget {
  const _TripBody({required this.trip, required this.showTeamLine});

  final PublicationDtoTrip trip;
  final bool showTeamLine;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final PdlColors c = context.pdl;
    final UnitSystem units = ref.watch(unitSystemProvider);
    final DateTime? start = DateTime.tryParse(trip.dateTime)?.toLocal();
    final DateTime? end = trip.endDate == null
        ? null
        : DateTime.tryParse(trip.endDate!)?.toLocal();

    return _CardShell(
      onTap: () => context.push(Paths.trip(trip.team.slug, trip.slug)),
      tone: PdlMediaTone.trip,
      icon: PdlIcons.trip,
      imageUrl: _themed(context, trip.thumbnailLightUrl, trip.thumbnailDarkUrl),
      teamName: showTeamLine ? trip.team.name : null,
      teamSlug: trip.team.slug,
      title: trip.name,
      excerpt: trip.excerpt,
      badges: <Widget>[
        ...deletedBadgeFirst(trip.deleted),
        PdlBadge(
          label: 'publicationType.trip'.tr(),
          tone: PublicationType.trip.tone(c),
        ),
        if (trip.registered)
          PdlBadge(
            label: 'rides.registered'.tr(),
            tone: PdlDerivedTones.registered(c),
            icon: PdlIcons.check,
          ),
      ],
      social: _SocialLine(
        // Le voyage rend ses participants en clair (`participants`), la sortie
        // un aperçu (`topParticipants`) ; la grappe borne à cinq de toute
        // façon.
        people: trip.participants,
        count: trip.participantCount,
      ),
      stats: <PdlStat>[
        if (start != null)
          PdlStat(
            // `endDate`, `totalDistance` et `totalElevationGain` sont des
            // champs de la 1.3.0 : la v1 ne pouvait pas rendre cette ligne.
            value: end == null
                ? AppFormatters.formatDayMonth(start)
                : '${AppFormatters.formatDayMonth(start)} → '
                      '${AppFormatters.formatDayMonth(end)}',
            icon: PdlIcons.date,
          ),
        if (trip.stageCount > 0)
          PdlStat(
            value: 'trips.stageCount'.plural(trip.stageCount),
            icon: PdlIcons.stage,
          ),
        if (trip.totalDistance != null)
          PdlStat(
            value: AppFormatters.formatDistance(trip.totalDistance!, units),
            icon: PdlIcons.distance,
          ),
        if (trip.totalElevationGain != null)
          PdlStat(
            value: AppFormatters.formatElevation(
              trip.totalElevationGain!,
              units,
            ),
            icon: PdlIcons.elevationUp,
            trend: PdlStatTrend.up,
          ),
      ],
    );
  }
}
