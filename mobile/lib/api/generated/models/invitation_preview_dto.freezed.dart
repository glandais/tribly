// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'invitation_preview_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$InvitationPreviewDto {

/// Team name
 String get teamName;/// Team URL slug
 String get teamSlug;/// Display name of whoever sent the invitation
 String get inviterName;/// Invited address, masked (a***@example.com)
 String get maskedEmail;/// Role granted on acceptance
 String get role;/// Where the invitation stands
 String get status;/// Whether the invitation can still be redeemed. False for anything but a live PENDING one.
 bool get redeemable;
/// Create a copy of InvitationPreviewDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$InvitationPreviewDtoCopyWith<InvitationPreviewDto> get copyWith => _$InvitationPreviewDtoCopyWithImpl<InvitationPreviewDto>(this as InvitationPreviewDto, _$identity);

  /// Serializes this InvitationPreviewDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as InvitationPreviewDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is InvitationPreviewDto&&(identical(other.teamName, _this.teamName) || other.teamName == _this.teamName)&&(identical(other.teamSlug, _this.teamSlug) || other.teamSlug == _this.teamSlug)&&(identical(other.inviterName, _this.inviterName) || other.inviterName == _this.inviterName)&&(identical(other.maskedEmail, _this.maskedEmail) || other.maskedEmail == _this.maskedEmail)&&(identical(other.role, _this.role) || other.role == _this.role)&&(identical(other.status, _this.status) || other.status == _this.status)&&(identical(other.redeemable, _this.redeemable) || other.redeemable == _this.redeemable));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as InvitationPreviewDto;
  return Object.hash(runtimeType,_this.teamName,_this.teamSlug,_this.inviterName,_this.maskedEmail,_this.role,_this.status,_this.redeemable);
}

@override
String toString() {
  final _this = this as InvitationPreviewDto;
  return 'InvitationPreviewDto(teamName: ${_this.teamName}, teamSlug: ${_this.teamSlug}, inviterName: ${_this.inviterName}, maskedEmail: ${_this.maskedEmail}, role: ${_this.role}, status: ${_this.status}, redeemable: ${_this.redeemable})';
}


}

/// @nodoc
abstract mixin class $InvitationPreviewDtoCopyWith<$Res>  {
  factory $InvitationPreviewDtoCopyWith(InvitationPreviewDto value, $Res Function(InvitationPreviewDto) _then) = _$InvitationPreviewDtoCopyWithImpl;
@useResult
$Res call({
 String teamName, String teamSlug, String inviterName, String maskedEmail, String role, String status, bool redeemable
});




}
/// @nodoc
class _$InvitationPreviewDtoCopyWithImpl<$Res>
    implements $InvitationPreviewDtoCopyWith<$Res> {
  _$InvitationPreviewDtoCopyWithImpl(this._self, this._then);

  final InvitationPreviewDto _self;
  final $Res Function(InvitationPreviewDto) _then;

/// Create a copy of InvitationPreviewDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? teamName = null,Object? teamSlug = null,Object? inviterName = null,Object? maskedEmail = null,Object? role = null,Object? status = null,Object? redeemable = null,}) {
  return _then(InvitationPreviewDto(
teamName: null == teamName ? _self.teamName : teamName // ignore: cast_nullable_to_non_nullable
as String,teamSlug: null == teamSlug ? _self.teamSlug : teamSlug // ignore: cast_nullable_to_non_nullable
as String,inviterName: null == inviterName ? _self.inviterName : inviterName // ignore: cast_nullable_to_non_nullable
as String,maskedEmail: null == maskedEmail ? _self.maskedEmail : maskedEmail // ignore: cast_nullable_to_non_nullable
as String,role: null == role ? _self.role : role // ignore: cast_nullable_to_non_nullable
as String,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,redeemable: null == redeemable ? _self.redeemable : redeemable // ignore: cast_nullable_to_non_nullable
as bool,
  ));
}

}


/// Adds pattern-matching-related methods to [InvitationPreviewDto].
extension InvitationPreviewDtoPatterns on InvitationPreviewDto {
/// A variant of `map` that fallback to returning `orElse`.
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case final Subclass value:
///     return ...;
///   case _:
///     return orElse();
/// }
/// ```

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _InvitationPreviewDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _InvitationPreviewDto() when $default != null:
return $default(_that);case _:
  return orElse();

}
}
/// A `switch`-like method, using callbacks.
///
/// Callbacks receives the raw object, upcasted.
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case final Subclass value:
///     return ...;
///   case final Subclass2 value:
///     return ...;
/// }
/// ```

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _InvitationPreviewDto value)  $default,){
final _that = this;
switch (_that) {
case _InvitationPreviewDto():
return $default(_that);case _:
  throw StateError('Unexpected subclass');

}
}
/// A variant of `map` that fallback to returning `null`.
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case final Subclass value:
///     return ...;
///   case _:
///     return null;
/// }
/// ```

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _InvitationPreviewDto value)?  $default,){
final _that = this;
switch (_that) {
case _InvitationPreviewDto() when $default != null:
return $default(_that);case _:
  return null;

}
}
/// A variant of `when` that fallback to an `orElse` callback.
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case Subclass(:final field):
///     return ...;
///   case _:
///     return orElse();
/// }
/// ```

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String teamName,  String teamSlug,  String inviterName,  String maskedEmail,  String role,  String status,  bool redeemable)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _InvitationPreviewDto() when $default != null:
return $default(_that.teamName,_that.teamSlug,_that.inviterName,_that.maskedEmail,_that.role,_that.status,_that.redeemable);case _:
  return orElse();

}
}
/// A `switch`-like method, using callbacks.
///
/// As opposed to `map`, this offers destructuring.
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case Subclass(:final field):
///     return ...;
///   case Subclass2(:final field2):
///     return ...;
/// }
/// ```

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String teamName,  String teamSlug,  String inviterName,  String maskedEmail,  String role,  String status,  bool redeemable)  $default,) {final _that = this;
switch (_that) {
case _InvitationPreviewDto():
return $default(_that.teamName,_that.teamSlug,_that.inviterName,_that.maskedEmail,_that.role,_that.status,_that.redeemable);case _:
  throw StateError('Unexpected subclass');

}
}
/// A variant of `when` that fallback to returning `null`
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case Subclass(:final field):
///     return ...;
///   case _:
///     return null;
/// }
/// ```

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String teamName,  String teamSlug,  String inviterName,  String maskedEmail,  String role,  String status,  bool redeemable)?  $default,) {final _that = this;
switch (_that) {
case _InvitationPreviewDto() when $default != null:
return $default(_that.teamName,_that.teamSlug,_that.inviterName,_that.maskedEmail,_that.role,_that.status,_that.redeemable);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _InvitationPreviewDto implements InvitationPreviewDto {
  const _InvitationPreviewDto({required this.teamName, required this.teamSlug, required this.inviterName, required this.maskedEmail, required this.role, required this.status, required this.redeemable});
  factory _InvitationPreviewDto.fromJson(Map<String, dynamic> json) => _$InvitationPreviewDtoFromJson(json);

/// Team name
@override final  String teamName;
/// Team URL slug
@override final  String teamSlug;
/// Display name of whoever sent the invitation
@override final  String inviterName;
/// Invited address, masked (a***@example.com)
@override final  String maskedEmail;
/// Role granted on acceptance
@override final  String role;
/// Where the invitation stands
@override final  String status;
/// Whether the invitation can still be redeemed. False for anything but a live PENDING one.
@override final  bool redeemable;

/// Create a copy of InvitationPreviewDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$InvitationPreviewDtoCopyWith<_InvitationPreviewDto> get copyWith => __$InvitationPreviewDtoCopyWithImpl<_InvitationPreviewDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$InvitationPreviewDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _InvitationPreviewDto&&(identical(other.teamName, teamName) || other.teamName == teamName)&&(identical(other.teamSlug, teamSlug) || other.teamSlug == teamSlug)&&(identical(other.inviterName, inviterName) || other.inviterName == inviterName)&&(identical(other.maskedEmail, maskedEmail) || other.maskedEmail == maskedEmail)&&(identical(other.role, role) || other.role == role)&&(identical(other.status, status) || other.status == status)&&(identical(other.redeemable, redeemable) || other.redeemable == redeemable));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,teamName,teamSlug,inviterName,maskedEmail,role,status,redeemable);
}

@override
String toString() {
    return 'InvitationPreviewDto(teamName: $teamName, teamSlug: $teamSlug, inviterName: $inviterName, maskedEmail: $maskedEmail, role: $role, status: $status, redeemable: $redeemable)';
}


}

/// @nodoc
abstract mixin class _$InvitationPreviewDtoCopyWith<$Res> implements $InvitationPreviewDtoCopyWith<$Res> {
  factory _$InvitationPreviewDtoCopyWith(_InvitationPreviewDto value, $Res Function(_InvitationPreviewDto) _then) = __$InvitationPreviewDtoCopyWithImpl;
@override @useResult
$Res call({
 String teamName, String teamSlug, String inviterName, String maskedEmail, String role, String status, bool redeemable
});




}
/// @nodoc
class __$InvitationPreviewDtoCopyWithImpl<$Res>
    implements _$InvitationPreviewDtoCopyWith<$Res> {
  __$InvitationPreviewDtoCopyWithImpl(this._self, this._then);

  final _InvitationPreviewDto _self;
  final $Res Function(_InvitationPreviewDto) _then;

/// Create a copy of InvitationPreviewDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? teamName = null,Object? teamSlug = null,Object? inviterName = null,Object? maskedEmail = null,Object? role = null,Object? status = null,Object? redeemable = null,}) {
  return _then(_InvitationPreviewDto(
teamName: null == teamName ? _self.teamName : teamName // ignore: cast_nullable_to_non_nullable
as String,teamSlug: null == teamSlug ? _self.teamSlug : teamSlug // ignore: cast_nullable_to_non_nullable
as String,inviterName: null == inviterName ? _self.inviterName : inviterName // ignore: cast_nullable_to_non_nullable
as String,maskedEmail: null == maskedEmail ? _self.maskedEmail : maskedEmail // ignore: cast_nullable_to_non_nullable
as String,role: null == role ? _self.role : role // ignore: cast_nullable_to_non_nullable
as String,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,redeemable: null == redeemable ? _self.redeemable : redeemable // ignore: cast_nullable_to_non_nullable
as bool,
  ));
}


}

// dart format on
