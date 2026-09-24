// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/moderation_decision_request.dart';
import '../models/moderation_queue_response.dart';
import '../models/report_queue_status.dart';

part 'admin_reports_client.g.dart';

@RestApi()
abstract class AdminReportsClient {
  factory AdminReportsClient(Dio dio, {String? baseUrl}) = _AdminReportsClient;

  /// List the platform moderation queue.
  ///
  /// Reports of every team of the domain, grouped by target, with the reporters. OPEN lists every target waiting for a decision; RESOLVED the 100 most recently decided.
  ///
  /// [status] - OPEN (default) or RESOLVED.
  @GET('/api/admin/reports')
  Future<ModerationQueueResponse> listAdminReports({
    @Query('status') ReportQueueStatus? status,
  });

  /// Decide about a reported target, in any team.
  ///
  /// Applies the decision to every open report of the target. REMOVE_CONTENT deletes the content (not allowed on a member); DISMISS keeps it, and shows it again if reports had hidden it.
  ///
  /// [body] - Name not received - field will be skipped.
  @POST('/api/admin/reports/resolve')
  Future<void> resolveAdminReports({
    @Body() required ModerationDecisionRequest body,
  });
}
