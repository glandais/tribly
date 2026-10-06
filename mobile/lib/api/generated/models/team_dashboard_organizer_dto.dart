// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'publication_list_response.dart';
import 'ride_template_list_response.dart';
import 'team_dashboard_reports_dto.dart';

part 'team_dashboard_organizer_dto.freezed.dart';
part 'team_dashboard_organizer_dto.g.dart';

/// The organizer part of a team dashboard: the « À traiter » tiles and the ride templates. Each list is a short page (at most 5 rows); its total is the tile's figure.
@Freezed()
abstract class TeamDashboardOrganizerDto with _$TeamDashboardOrganizerDto {
  const factory TeamDashboardOrganizerDto({
    /// The team's drafts (rides, posts, trips), newest first. total is the number of drafts, the rows their names.
    required PublicationListResponse drafts,

    /// The open reports of the team's moderation queue
    required TeamDashboardReportsDto reports,

    /// Published rides starting from now routed nowhere — neither the ride nor any of its groups has a route — soonest first. Same rows as GET …/publications?type=RIDE&withoutRoute=true. Null when rides are disabled.
    PublicationListResponse? ridesWithoutRoute,

    /// Published rides starting from now with at least one group at capacity, soonest first. Same rows as GET …/publications?type=RIDE&withFullGroup=true. Null when rides are disabled.
    PublicationListResponse? ridesWithFullGroup,

    /// « Créer depuis un modèle »: the team's ride templates (at most 5), each with its groupCount. Null when rides are disabled.
    RideTemplateListResponse? rideTemplates,
  }) = _TeamDashboardOrganizerDto;

  factory TeamDashboardOrganizerDto.fromJson(Map<String, Object?> json) =>
      _$TeamDashboardOrganizerDtoFromJson(json);
}
