// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:dio/dio.dart' hide Headers;

import 'clients/admin_domains_client.dart';
import 'clients/admin_teams_client.dart';
import 'clients/admin_users_client.dart';
import 'clients/authentication_client.dart';
import 'clients/passkeys_client.dart';
import 'clients/calendar_client.dart';
import 'clients/configuration_client.dart';
import 'clients/device_o_auth_client.dart';
import 'clients/garmin_routes_client.dart';
import 'clients/gps_services_client.dart';
import 'clients/karoo_routes_client.dart';
import 'clients/publications_client.dart';
import 'clients/router_client.dart';
import 'clients/routes_client.dart';
import 'clients/teams_client.dart';
import 'clients/ads_client.dart';
import 'clients/assets_client.dart';
import 'clients/team_members_client.dart';
import 'clients/team_pages_client.dart';
import 'clients/places_client.dart';
import 'clients/posts_client.dart';
import 'clients/post_comments_client.dart';
import 'clients/ride_templates_client.dart';
import 'clients/rides_client.dart';
import 'clients/ride_comments_client.dart';
import 'clients/route_comments_client.dart';
import 'clients/trips_client.dart';
import 'clients/trip_comments_client.dart';
import 'clients/users_client.dart';

/// Tribly API `v1.0.0`.
///
/// API for Tribly Cycling Team Management Platform.
class TriblyApiClient {
  TriblyApiClient(
    Dio dio, {
    String? baseUrl,
  }) : _dio = dio,
       _baseUrl = baseUrl;

  final Dio _dio;
  final String? _baseUrl;

  static String get version => '1.0.0';

  AdminDomainsClient? _adminDomains;
  AdminTeamsClient? _adminTeams;
  AdminUsersClient? _adminUsers;
  AuthenticationClient? _authentication;
  PasskeysClient? _passkeys;
  CalendarClient? _calendar;
  ConfigurationClient? _configuration;
  DeviceOAuthClient? _deviceOAuth;
  GarminRoutesClient? _garminRoutes;
  GpsServicesClient? _gpsServices;
  KarooRoutesClient? _karooRoutes;
  PublicationsClient? _publications;
  RouterClient? _router;
  RoutesClient? _routes;
  TeamsClient? _teams;
  AdsClient? _ads;
  AssetsClient? _assets;
  TeamMembersClient? _teamMembers;
  TeamPagesClient? _teamPages;
  PlacesClient? _places;
  PostsClient? _posts;
  PostCommentsClient? _postComments;
  RideTemplatesClient? _rideTemplates;
  RidesClient? _rides;
  RideCommentsClient? _rideComments;
  RouteCommentsClient? _routeComments;
  TripsClient? _trips;
  TripCommentsClient? _tripComments;
  UsersClient? _users;

  AdminDomainsClient get adminDomains =>
      _adminDomains ??= AdminDomainsClient(_dio, baseUrl: _baseUrl);

  AdminTeamsClient get adminTeams =>
      _adminTeams ??= AdminTeamsClient(_dio, baseUrl: _baseUrl);

  AdminUsersClient get adminUsers =>
      _adminUsers ??= AdminUsersClient(_dio, baseUrl: _baseUrl);

  AuthenticationClient get authentication =>
      _authentication ??= AuthenticationClient(_dio, baseUrl: _baseUrl);

  PasskeysClient get passkeys =>
      _passkeys ??= PasskeysClient(_dio, baseUrl: _baseUrl);

  CalendarClient get calendar =>
      _calendar ??= CalendarClient(_dio, baseUrl: _baseUrl);

  ConfigurationClient get configuration =>
      _configuration ??= ConfigurationClient(_dio, baseUrl: _baseUrl);

  DeviceOAuthClient get deviceOAuth =>
      _deviceOAuth ??= DeviceOAuthClient(_dio, baseUrl: _baseUrl);

  GarminRoutesClient get garminRoutes =>
      _garminRoutes ??= GarminRoutesClient(_dio, baseUrl: _baseUrl);

  GpsServicesClient get gpsServices =>
      _gpsServices ??= GpsServicesClient(_dio, baseUrl: _baseUrl);

  KarooRoutesClient get karooRoutes =>
      _karooRoutes ??= KarooRoutesClient(_dio, baseUrl: _baseUrl);

  PublicationsClient get publications =>
      _publications ??= PublicationsClient(_dio, baseUrl: _baseUrl);

  RouterClient get router => _router ??= RouterClient(_dio, baseUrl: _baseUrl);

  RoutesClient get routes => _routes ??= RoutesClient(_dio, baseUrl: _baseUrl);

  TeamsClient get teams => _teams ??= TeamsClient(_dio, baseUrl: _baseUrl);

  AdsClient get ads => _ads ??= AdsClient(_dio, baseUrl: _baseUrl);

  AssetsClient get assets => _assets ??= AssetsClient(_dio, baseUrl: _baseUrl);

  TeamMembersClient get teamMembers =>
      _teamMembers ??= TeamMembersClient(_dio, baseUrl: _baseUrl);

  TeamPagesClient get teamPages =>
      _teamPages ??= TeamPagesClient(_dio, baseUrl: _baseUrl);

  PlacesClient get places => _places ??= PlacesClient(_dio, baseUrl: _baseUrl);

  PostsClient get posts => _posts ??= PostsClient(_dio, baseUrl: _baseUrl);

  PostCommentsClient get postComments =>
      _postComments ??= PostCommentsClient(_dio, baseUrl: _baseUrl);

  RideTemplatesClient get rideTemplates =>
      _rideTemplates ??= RideTemplatesClient(_dio, baseUrl: _baseUrl);

  RidesClient get rides => _rides ??= RidesClient(_dio, baseUrl: _baseUrl);

  RideCommentsClient get rideComments =>
      _rideComments ??= RideCommentsClient(_dio, baseUrl: _baseUrl);

  RouteCommentsClient get routeComments =>
      _routeComments ??= RouteCommentsClient(_dio, baseUrl: _baseUrl);

  TripsClient get trips => _trips ??= TripsClient(_dio, baseUrl: _baseUrl);

  TripCommentsClient get tripComments =>
      _tripComments ??= TripCommentsClient(_dio, baseUrl: _baseUrl);

  UsersClient get users => _users ??= UsersClient(_dio, baseUrl: _baseUrl);
}
