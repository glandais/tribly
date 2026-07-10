// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

@JsonEnum()
enum ErrorCode {
  @JsonValue('BAD_REQUEST')
  badRequest('BAD_REQUEST'),
  @JsonValue('NOT_FOUND')
  notFound('NOT_FOUND'),
  @JsonValue('UNAUTHORIZED')
  unauthorized('UNAUTHORIZED'),
  @JsonValue('FORBIDDEN')
  forbidden('FORBIDDEN'),
  @JsonValue('INTERNAL_ERROR')
  internalError('INTERNAL_ERROR'),
  @JsonValue('UNKNOWN')
  unknown('UNKNOWN'),
  @JsonValue('VALIDATION')
  validation('VALIDATION'),
  @JsonValue('BUSINESS_RULE')
  businessRule('BUSINESS_RULE'),
  @JsonValue('LAST_ADMIN')
  lastAdmin('LAST_ADMIN'),
  @JsonValue('ALREADY_REGISTERED')
  alreadyRegistered('ALREADY_REGISTERED'),
  @JsonValue('NOT_REGISTERED')
  notRegistered('NOT_REGISTERED'),
  @JsonValue('INVALID_SLUG')
  invalidSlug('INVALID_SLUG'),
  @JsonValue('SLUG_TAKEN')
  slugTaken('SLUG_TAKEN'),
  @JsonValue('USER_NOT_SYNCED')
  userNotSynced('USER_NOT_SYNCED'),
  @JsonValue('GROUP_FULL')
  groupFull('GROUP_FULL'),
  @JsonValue('INVALID_VISIBILITY')
  invalidVisibility('INVALID_VISIBILITY'),
  @JsonValue('PUBLIC_TRIP_PRIVATE_ROUTE')
  publicTripPrivateRoute('PUBLIC_TRIP_PRIVATE_ROUTE'),
  @JsonValue('GPX_FAILURE')
  gpxFailure('GPX_FAILURE'),
  @JsonValue('FILE_REQUIRED')
  fileRequired('FILE_REQUIRED'),
  @JsonValue('FILE_TYPE_REJECTED')
  fileTypeRejected('FILE_TYPE_REJECTED'),
  @JsonValue('FILE_DETECTION_FAILED')
  fileDetectionFailed('FILE_DETECTION_FAILED'),
  @JsonValue('TOO_MANY_TEAM_PAGES')
  tooManyTeamPages('TOO_MANY_TEAM_PAGES'),
  @JsonValue('GPX_EMPTY')
  gpxEmpty('GPX_EMPTY'),
  @JsonValue('INVALID_FORMAT')
  invalidFormat('INVALID_FORMAT'),
  @JsonValue('RENTAL_PERIOD_MISSING')
  rentalPeriodMissing('RENTAL_PERIOD_MISSING'),
  @JsonValue('STATUS_INVALID')
  statusInvalid('STATUS_INVALID'),
  @JsonValue('PUBLIC_RIDE_PRIVATE_ROUTE')
  publicRidePrivateRoute('PUBLIC_RIDE_PRIVATE_ROUTE'),
  @JsonValue('INVALID_CREDENTIALS')
  invalidCredentials('INVALID_CREDENTIALS'),
  @JsonValue('EMAIL_NOT_VERIFIED')
  emailNotVerified('EMAIL_NOT_VERIFIED'),
  @JsonValue('EMAIL_ALREADY_EXISTS')
  emailAlreadyExists('EMAIL_ALREADY_EXISTS'),
  @JsonValue('TOKEN_INVALID')
  tokenInvalid('TOKEN_INVALID'),
  @JsonValue('TOKEN_EXPIRED')
  tokenExpired('TOKEN_EXPIRED'),
  @JsonValue('SESSION_EXPIRED')
  sessionExpired('SESSION_EXPIRED'),
  @JsonValue('PASSWORD_NOT_SET')
  passwordNotSet('PASSWORD_NOT_SET'),
  @JsonValue('PASSKEY_NOT_FOUND')
  passkeyNotFound('PASSKEY_NOT_FOUND'),
  @JsonValue('USER_NOT_FOUND')
  userNotFound('USER_NOT_FOUND'),
  @JsonValue('DOMAIN_NOT_FOUND')
  domainNotFound('DOMAIN_NOT_FOUND'),
  @JsonValue('TEAM_NOT_FOUND')
  teamNotFound('TEAM_NOT_FOUND'),
  @JsonValue('TEAM_CREATION_DISABLED')
  teamCreationDisabled('TEAM_CREATION_DISABLED'),
  @JsonValue('USER_TEAM_LIMIT_REACHED')
  userTeamLimitReached('USER_TEAM_LIMIT_REACHED'),
  @JsonValue('TEAM_JOIN_NOT_ALLOWED')
  teamJoinNotAllowed('TEAM_JOIN_NOT_ALLOWED'),
  @JsonValue('TEAM_ADD_MEMBER_NOT_ALLOWED')
  teamAddMemberNotAllowed('TEAM_ADD_MEMBER_NOT_ALLOWED'),
  @JsonValue('GPS_SERVICE_ALREADY_CONNECTED')
  gpsServiceAlreadyConnected('GPS_SERVICE_ALREADY_CONNECTED'),
  @JsonValue('GPS_SERVICE_NOT_CONNECTED')
  gpsServiceNotConnected('GPS_SERVICE_NOT_CONNECTED'),
  @JsonValue('GPS_SERVICE_NOT_CONFIGURED')
  gpsServiceNotConfigured('GPS_SERVICE_NOT_CONFIGURED'),
  @JsonValue('GPS_INVALID_STATE')
  gpsInvalidState('GPS_INVALID_STATE'),
  @JsonValue('GPS_STATE_EXPIRED')
  gpsStateExpired('GPS_STATE_EXPIRED'),
  @JsonValue('GPX_NOT_FOUND')
  gpxNotFound('GPX_NOT_FOUND'),
  @JsonValue('AUTHORIZATION_PENDING')
  authorizationPending('AUTHORIZATION_PENDING'),
  @JsonValue('GPS_CREDENTIAL_ALREADY_EXISTS')
  gpsCredentialAlreadyExists('GPS_CREDENTIAL_ALREADY_EXISTS'),
  @JsonValue('GPS_TOKEN_EXCHANGE_FAILED')
  gpsTokenExchangeFailed('GPS_TOKEN_EXCHANGE_FAILED'),
  @JsonValue('ENCRYPTION_FAILED')
  encryptionFailed('ENCRYPTION_FAILED'),
  @JsonValue('DECRYPTION_FAILED')
  decryptionFailed('DECRYPTION_FAILED'),
  @JsonValue('DOMAIN_ALIAS_NOT_FOUND')
  domainAliasNotFound('DOMAIN_ALIAS_NOT_FOUND'),
  @JsonValue('DOMAIN_ALIAS_HOSTNAME_EXISTS')
  domainAliasHostnameExists('DOMAIN_ALIAS_HOSTNAME_EXISTS'),
  @JsonValue('DOMAIN_ALIAS_TEAM_MISMATCH')
  domainAliasTeamMismatch('DOMAIN_ALIAS_TEAM_MISMATCH'),

  /// Default value for all unparsed values, allows backward compatibility when adding new values on the backend.
  $unknown(null);

  const ErrorCode(this.json);

  factory ErrorCode.fromJson(String json) => values.firstWhere(
    (e) => e.json == json,
    orElse: () => $unknown,
  );

  final String? json;

  String toJson() => json ?? 'null';

  @override
  String toString() => json ?? super.toString();

  /// Returns all defined enum values excluding the $unknown value.
  static List<ErrorCode> get $valuesDefined =>
      values.where((value) => value != $unknown).toList();
}
