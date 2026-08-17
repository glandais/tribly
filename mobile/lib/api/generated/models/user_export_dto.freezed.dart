// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'user_export_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$UserExportDto {

/// Export job identifier
 String get id;/// Current status
 String get status;/// When the export was requested
 String get requestedAt;/// When the export finished building
 String? get completedAt;/// When the download link stops working
 String? get expiresAt;/// Size of the archive in bytes
 int? get fileSize;
/// Create a copy of UserExportDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$UserExportDtoCopyWith<UserExportDto> get copyWith => _$UserExportDtoCopyWithImpl<UserExportDto>(this as UserExportDto, _$identity);

  /// Serializes this UserExportDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is UserExportDto&&(identical(other.id, id) || other.id == id)&&(identical(other.status, status) || other.status == status)&&(identical(other.requestedAt, requestedAt) || other.requestedAt == requestedAt)&&(identical(other.completedAt, completedAt) || other.completedAt == completedAt)&&(identical(other.expiresAt, expiresAt) || other.expiresAt == expiresAt)&&(identical(other.fileSize, fileSize) || other.fileSize == fileSize));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,status,requestedAt,completedAt,expiresAt,fileSize);

@override
String toString() {
  return 'UserExportDto(id: $id, status: $status, requestedAt: $requestedAt, completedAt: $completedAt, expiresAt: $expiresAt, fileSize: $fileSize)';
}


}

/// @nodoc
abstract mixin class $UserExportDtoCopyWith<$Res>  {
  factory $UserExportDtoCopyWith(UserExportDto value, $Res Function(UserExportDto) _then) = _$UserExportDtoCopyWithImpl;
@useResult
$Res call({
 String id, String status, String requestedAt, String? completedAt, String? expiresAt, int? fileSize
});




}
/// @nodoc
class _$UserExportDtoCopyWithImpl<$Res>
    implements $UserExportDtoCopyWith<$Res> {
  _$UserExportDtoCopyWithImpl(this._self, this._then);

  final UserExportDto _self;
  final $Res Function(UserExportDto) _then;

/// Create a copy of UserExportDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? status = null,Object? requestedAt = null,Object? completedAt = freezed,Object? expiresAt = freezed,Object? fileSize = freezed,}) {
  return _then(UserExportDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,requestedAt: null == requestedAt ? _self.requestedAt : requestedAt // ignore: cast_nullable_to_non_nullable
as String,completedAt: freezed == completedAt ? _self.completedAt : completedAt // ignore: cast_nullable_to_non_nullable
as String?,expiresAt: freezed == expiresAt ? _self.expiresAt : expiresAt // ignore: cast_nullable_to_non_nullable
as String?,fileSize: freezed == fileSize ? _self.fileSize : fileSize // ignore: cast_nullable_to_non_nullable
as int?,
  ));
}

}


/// Adds pattern-matching-related methods to [UserExportDto].
extension UserExportDtoPatterns on UserExportDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _UserExportDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _UserExportDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _UserExportDto value)  $default,){
final _that = this;
switch (_that) {
case _UserExportDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _UserExportDto value)?  $default,){
final _that = this;
switch (_that) {
case _UserExportDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  String status,  String requestedAt,  String? completedAt,  String? expiresAt,  int? fileSize)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _UserExportDto() when $default != null:
return $default(_that.id,_that.status,_that.requestedAt,_that.completedAt,_that.expiresAt,_that.fileSize);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  String status,  String requestedAt,  String? completedAt,  String? expiresAt,  int? fileSize)  $default,) {final _that = this;
switch (_that) {
case _UserExportDto():
return $default(_that.id,_that.status,_that.requestedAt,_that.completedAt,_that.expiresAt,_that.fileSize);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  String status,  String requestedAt,  String? completedAt,  String? expiresAt,  int? fileSize)?  $default,) {final _that = this;
switch (_that) {
case _UserExportDto() when $default != null:
return $default(_that.id,_that.status,_that.requestedAt,_that.completedAt,_that.expiresAt,_that.fileSize);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _UserExportDto implements UserExportDto {
  const _UserExportDto({required this.id, required this.status, required this.requestedAt, this.completedAt, this.expiresAt, this.fileSize});
  factory _UserExportDto.fromJson(Map<String, dynamic> json) => _$UserExportDtoFromJson(json);

/// Export job identifier
@override final  String id;
/// Current status
@override final  String status;
/// When the export was requested
@override final  String requestedAt;
/// When the export finished building
@override final  String? completedAt;
/// When the download link stops working
@override final  String? expiresAt;
/// Size of the archive in bytes
@override final  int? fileSize;

/// Create a copy of UserExportDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$UserExportDtoCopyWith<_UserExportDto> get copyWith => __$UserExportDtoCopyWithImpl<_UserExportDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$UserExportDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _UserExportDto&&(identical(other.id, id) || other.id == id)&&(identical(other.status, status) || other.status == status)&&(identical(other.requestedAt, requestedAt) || other.requestedAt == requestedAt)&&(identical(other.completedAt, completedAt) || other.completedAt == completedAt)&&(identical(other.expiresAt, expiresAt) || other.expiresAt == expiresAt)&&(identical(other.fileSize, fileSize) || other.fileSize == fileSize));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,status,requestedAt,completedAt,expiresAt,fileSize);

@override
String toString() {
  return 'UserExportDto(id: $id, status: $status, requestedAt: $requestedAt, completedAt: $completedAt, expiresAt: $expiresAt, fileSize: $fileSize)';
}


}

/// @nodoc
abstract mixin class _$UserExportDtoCopyWith<$Res> implements $UserExportDtoCopyWith<$Res> {
  factory _$UserExportDtoCopyWith(_UserExportDto value, $Res Function(_UserExportDto) _then) = __$UserExportDtoCopyWithImpl;
@override @useResult
$Res call({
 String id, String status, String requestedAt, String? completedAt, String? expiresAt, int? fileSize
});




}
/// @nodoc
class __$UserExportDtoCopyWithImpl<$Res>
    implements _$UserExportDtoCopyWith<$Res> {
  __$UserExportDtoCopyWithImpl(this._self, this._then);

  final _UserExportDto _self;
  final $Res Function(_UserExportDto) _then;

/// Create a copy of UserExportDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? status = null,Object? requestedAt = null,Object? completedAt = freezed,Object? expiresAt = freezed,Object? fileSize = freezed,}) {
  return _then(_UserExportDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,requestedAt: null == requestedAt ? _self.requestedAt : requestedAt // ignore: cast_nullable_to_non_nullable
as String,completedAt: freezed == completedAt ? _self.completedAt : completedAt // ignore: cast_nullable_to_non_nullable
as String?,expiresAt: freezed == expiresAt ? _self.expiresAt : expiresAt // ignore: cast_nullable_to_non_nullable
as String?,fileSize: freezed == fileSize ? _self.fileSize : fileSize // ignore: cast_nullable_to_non_nullable
as int?,
  ));
}


}

// dart format on
