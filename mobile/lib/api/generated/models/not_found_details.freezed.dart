// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'not_found_details.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$NotFoundDetails {

/// Type
 String get type;/// Entity type
 String get entityType;/// Search type
 String get searchedBy;/// id/slug
 String get id;
/// Create a copy of NotFoundDetails
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$NotFoundDetailsCopyWith<NotFoundDetails> get copyWith => _$NotFoundDetailsCopyWithImpl<NotFoundDetails>(this as NotFoundDetails, _$identity);

  /// Serializes this NotFoundDetails to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is NotFoundDetails&&(identical(other.type, type) || other.type == type)&&(identical(other.entityType, entityType) || other.entityType == entityType)&&(identical(other.searchedBy, searchedBy) || other.searchedBy == searchedBy)&&(identical(other.id, id) || other.id == id));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,type,entityType,searchedBy,id);

@override
String toString() {
  return 'NotFoundDetails(type: $type, entityType: $entityType, searchedBy: $searchedBy, id: $id)';
}


}

/// @nodoc
abstract mixin class $NotFoundDetailsCopyWith<$Res>  {
  factory $NotFoundDetailsCopyWith(NotFoundDetails value, $Res Function(NotFoundDetails) _then) = _$NotFoundDetailsCopyWithImpl;
@useResult
$Res call({
 String type, String entityType, String searchedBy, String id
});




}
/// @nodoc
class _$NotFoundDetailsCopyWithImpl<$Res>
    implements $NotFoundDetailsCopyWith<$Res> {
  _$NotFoundDetailsCopyWithImpl(this._self, this._then);

  final NotFoundDetails _self;
  final $Res Function(NotFoundDetails) _then;

/// Create a copy of NotFoundDetails
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? type = null,Object? entityType = null,Object? searchedBy = null,Object? id = null,}) {
  return _then(NotFoundDetails(
type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as String,entityType: null == entityType ? _self.entityType : entityType // ignore: cast_nullable_to_non_nullable
as String,searchedBy: null == searchedBy ? _self.searchedBy : searchedBy // ignore: cast_nullable_to_non_nullable
as String,id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [NotFoundDetails].
extension NotFoundDetailsPatterns on NotFoundDetails {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _NotFoundDetails value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _NotFoundDetails() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _NotFoundDetails value)  $default,){
final _that = this;
switch (_that) {
case _NotFoundDetails():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _NotFoundDetails value)?  $default,){
final _that = this;
switch (_that) {
case _NotFoundDetails() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String type,  String entityType,  String searchedBy,  String id)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _NotFoundDetails() when $default != null:
return $default(_that.type,_that.entityType,_that.searchedBy,_that.id);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String type,  String entityType,  String searchedBy,  String id)  $default,) {final _that = this;
switch (_that) {
case _NotFoundDetails():
return $default(_that.type,_that.entityType,_that.searchedBy,_that.id);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String type,  String entityType,  String searchedBy,  String id)?  $default,) {final _that = this;
switch (_that) {
case _NotFoundDetails() when $default != null:
return $default(_that.type,_that.entityType,_that.searchedBy,_that.id);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _NotFoundDetails implements NotFoundDetails {
  const _NotFoundDetails({required this.type, required this.entityType, required this.searchedBy, required this.id});
  factory _NotFoundDetails.fromJson(Map<String, dynamic> json) => _$NotFoundDetailsFromJson(json);

/// Type
@override final  String type;
/// Entity type
@override final  String entityType;
/// Search type
@override final  String searchedBy;
/// id/slug
@override final  String id;

/// Create a copy of NotFoundDetails
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$NotFoundDetailsCopyWith<_NotFoundDetails> get copyWith => __$NotFoundDetailsCopyWithImpl<_NotFoundDetails>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$NotFoundDetailsToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _NotFoundDetails&&(identical(other.type, type) || other.type == type)&&(identical(other.entityType, entityType) || other.entityType == entityType)&&(identical(other.searchedBy, searchedBy) || other.searchedBy == searchedBy)&&(identical(other.id, id) || other.id == id));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,type,entityType,searchedBy,id);

@override
String toString() {
  return 'NotFoundDetails(type: $type, entityType: $entityType, searchedBy: $searchedBy, id: $id)';
}


}

/// @nodoc
abstract mixin class _$NotFoundDetailsCopyWith<$Res> implements $NotFoundDetailsCopyWith<$Res> {
  factory _$NotFoundDetailsCopyWith(_NotFoundDetails value, $Res Function(_NotFoundDetails) _then) = __$NotFoundDetailsCopyWithImpl;
@override @useResult
$Res call({
 String type, String entityType, String searchedBy, String id
});




}
/// @nodoc
class __$NotFoundDetailsCopyWithImpl<$Res>
    implements _$NotFoundDetailsCopyWith<$Res> {
  __$NotFoundDetailsCopyWithImpl(this._self, this._then);

  final _NotFoundDetails _self;
  final $Res Function(_NotFoundDetails) _then;

/// Create a copy of NotFoundDetails
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? type = null,Object? entityType = null,Object? searchedBy = null,Object? id = null,}) {
  return _then(_NotFoundDetails(
type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as String,entityType: null == entityType ? _self.entityType : entityType // ignore: cast_nullable_to_non_nullable
as String,searchedBy: null == searchedBy ? _self.searchedBy : searchedBy // ignore: cast_nullable_to_non_nullable
as String,id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
