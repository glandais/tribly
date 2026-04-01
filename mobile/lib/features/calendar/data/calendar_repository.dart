import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../api/generated/export.dart';
import '../../../api/pedalons_api_client.dart';

final calendarRepositoryProvider = Provider<CalendarRepository>((ref) {
  return CalendarRepository(ref.watch(calendarClientProvider));
});

class CalendarRepository {
  final CalendarClient _calendarClient;

  CalendarRepository(this._calendarClient);

  /// Get calendar events for the current user (from all teams)
  Future<List<CalendarEventDto>> getMyCalendarEvents({
    required DateTime start,
    required DateTime end,
  }) async {
    final response = await _calendarClient.getEvents(
      from: start.toUtc().toIso8601String(),
      to: end.toUtc().toIso8601String(),
    );
    return response.events;
  }

  /// Get calendar events for a specific team
  Future<List<CalendarEventDto>> getTeamCalendarEvents(
    String teamSlug, {
    required DateTime start,
    required DateTime end,
  }) async {
    final response = await _calendarClient.getTeamEvents(
      teamSlug: teamSlug,
      from: start.toUtc().toIso8601String(),
      to: end.toUtc().toIso8601String(),
    );
    return response.events;
  }
}
