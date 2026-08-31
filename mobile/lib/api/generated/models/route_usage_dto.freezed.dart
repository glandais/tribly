// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'route_usage_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$RouteUsageDto {

/// Publication type (RIDE or TRIP)
 String get type;/// Publication URL slug
 String get slug;/// Publication name
 String get name;/// Publication date/time
 String get dateTime;/// Slug of the team owning the publication
 String get teamSlug;/// Whether the publication references the route directly (not only via a child)
 bool get referencedDirectly;/// Names of the ride groups or trip stages that reference the route, if any
 List<String> get viaChildNames;
/// Create a copy of RouteUsageDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$RouteUsageDtoCopyWith<RouteUsageDto> get copyWith => _$RouteUsageDtoCopyWithImpl<RouteUsageDto>(this as RouteUsageDto, _$identity);

  /// Serializes this RouteUsageDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as RouteUsageDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is RouteUsageDto&&(identical(other.type, _this.type) || other.type == _this.type)&&(identical(other.slug, _this.slug) || other.slug == _this.slug)&&(identical(other.name, _this.name) || other.name == _this.name)&&(identical(other.dateTime, _this.dateTime) || other.dateTime == _this.dateTime)&&(identical(other.teamSlug, _this.teamSlug) || other.teamSlug == _this.teamSlug)&&(identical(other.referencedDirectly, _this.referencedDirectly) || other.referencedDirectly == _this.referencedDirectly)&&const DeepCollectionEquality().equals(other.viaChildNames, _this.viaChildNames));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as RouteUsageDto;
  return Object.hash(runtimeType,_this.type,_this.slug,_this.name,_this.dateTime,_this.teamSlug,_this.referencedDirectly,const DeepCollectionEquality().hash(_this.viaChildNames));
}

@override
String toString() {
  final _this = this as RouteUsageDto;
  return 'RouteUsageDto(type: ${_this.type}, slug: ${_this.slug}, name: ${_this.name}, dateTime: ${_this.dateTime}, teamSlug: ${_this.teamSlug}, referencedDirectly: ${_this.referencedDirectly}, viaChildNames: ${_this.viaChildNames})';
}


}

/// @nodoc
abstract mixin class $RouteUsageDtoCopyWith<$Res>  {
  factory $RouteUsageDtoCopyWith(RouteUsageDto value, $Res Function(RouteUsageDto) _then) = _$RouteUsageDtoCopyWithImpl;
@useResult
$Res call({
 String type, String slug, String name, String dateTime, String teamSlug, bool referencedDirectly, List<String> viaChildNames
});




}
/// @nodoc
class _$RouteUsageDtoCopyWithImpl<$Res>
    implements $RouteUsageDtoCopyWith<$Res> {
  _$RouteUsageDtoCopyWithImpl(this._self, this._then);

  final RouteUsageDto _self;
  final $Res Function(RouteUsageDto) _then;

/// Create a copy of RouteUsageDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? type = null,Object? slug = null,Object? name = null,Object? dateTime = null,Object? teamSlug = null,Object? referencedDirectly = null,Object? viaChildNames = null,}) {
  return _then(RouteUsageDto(
type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as String,slug: null == slug ? _self.slug : slug // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,dateTime: null == dateTime ? _self.dateTime : dateTime // ignore: cast_nullable_to_non_nullable
as String,teamSlug: null == teamSlug ? _self.teamSlug : teamSlug // ignore: cast_nullable_to_non_nullable
as String,referencedDirectly: null == referencedDirectly ? _self.referencedDirectly : referencedDirectly // ignore: cast_nullable_to_non_nullable
as bool,viaChildNames: null == viaChildNames ? _self.viaChildNames : viaChildNames // ignore: cast_nullable_to_non_nullable
as List<String>,
  ));
}

}


/// Adds pattern-matching-related methods to [RouteUsageDto].
extension RouteUsageDtoPatterns on RouteUsageDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _RouteUsageDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _RouteUsageDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _RouteUsageDto value)  $default,){
final _that = this;
switch (_that) {
case _RouteUsageDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _RouteUsageDto value)?  $default,){
final _that = this;
switch (_that) {
case _RouteUsageDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String type,  String slug,  String name,  String dateTime,  String teamSlug,  bool referencedDirectly,  List<String> viaChildNames)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _RouteUsageDto() when $default != null:
return $default(_that.type,_that.slug,_that.name,_that.dateTime,_that.teamSlug,_that.referencedDirectly,_that.viaChildNames);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String type,  String slug,  String name,  String dateTime,  String teamSlug,  bool referencedDirectly,  List<String> viaChildNames)  $default,) {final _that = this;
switch (_that) {
case _RouteUsageDto():
return $default(_that.type,_that.slug,_that.name,_that.dateTime,_that.teamSlug,_that.referencedDirectly,_that.viaChildNames);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String type,  String slug,  String name,  String dateTime,  String teamSlug,  bool referencedDirectly,  List<String> viaChildNames)?  $default,) {final _that = this;
switch (_that) {
case _RouteUsageDto() when $default != null:
return $default(_that.type,_that.slug,_that.name,_that.dateTime,_that.teamSlug,_that.referencedDirectly,_that.viaChildNames);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _RouteUsageDto implements RouteUsageDto {
  const _RouteUsageDto({required this.type, required this.slug, required this.name, required this.dateTime, required this.teamSlug, required this.referencedDirectly, required  List<String> viaChildNames}): _viaChildNames = viaChildNames;
  factory _RouteUsageDto.fromJson(Map<String, dynamic> json) => _$RouteUsageDtoFromJson(json);

/// Publication type (RIDE or TRIP)
@override final  String type;
/// Publication URL slug
@override final  String slug;
/// Publication name
@override final  String name;
/// Publication date/time
@override final  String dateTime;
/// Slug of the team owning the publication
@override final  String teamSlug;
/// Whether the publication references the route directly (not only via a child)
@override final  bool referencedDirectly;
/// Names of the ride groups or trip stages that reference the route, if any
 final  List<String> _viaChildNames;
/// Names of the ride groups or trip stages that reference the route, if any
@override List<String> get viaChildNames {
  if (_viaChildNames is EqualUnmodifiableListView) return _viaChildNames;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_viaChildNames);
}


/// Create a copy of RouteUsageDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$RouteUsageDtoCopyWith<_RouteUsageDto> get copyWith => __$RouteUsageDtoCopyWithImpl<_RouteUsageDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$RouteUsageDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _RouteUsageDto&&(identical(other.type, type) || other.type == type)&&(identical(other.slug, slug) || other.slug == slug)&&(identical(other.name, name) || other.name == name)&&(identical(other.dateTime, dateTime) || other.dateTime == dateTime)&&(identical(other.teamSlug, teamSlug) || other.teamSlug == teamSlug)&&(identical(other.referencedDirectly, referencedDirectly) || other.referencedDirectly == referencedDirectly)&&const DeepCollectionEquality().equals(other.viaChildNames, _viaChildNames));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,type,slug,name,dateTime,teamSlug,referencedDirectly,const DeepCollectionEquality().hash(_viaChildNames));
}

@override
String toString() {
    return 'RouteUsageDto(type: $type, slug: $slug, name: $name, dateTime: $dateTime, teamSlug: $teamSlug, referencedDirectly: $referencedDirectly, viaChildNames: $viaChildNames)';
}


}

/// @nodoc
abstract mixin class _$RouteUsageDtoCopyWith<$Res> implements $RouteUsageDtoCopyWith<$Res> {
  factory _$RouteUsageDtoCopyWith(_RouteUsageDto value, $Res Function(_RouteUsageDto) _then) = __$RouteUsageDtoCopyWithImpl;
@override @useResult
$Res call({
 String type, String slug, String name, String dateTime, String teamSlug, bool referencedDirectly, List<String> viaChildNames
});




}
/// @nodoc
class __$RouteUsageDtoCopyWithImpl<$Res>
    implements _$RouteUsageDtoCopyWith<$Res> {
  __$RouteUsageDtoCopyWithImpl(this._self, this._then);

  final _RouteUsageDto _self;
  final $Res Function(_RouteUsageDto) _then;

/// Create a copy of RouteUsageDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? type = null,Object? slug = null,Object? name = null,Object? dateTime = null,Object? teamSlug = null,Object? referencedDirectly = null,Object? viaChildNames = null,}) {
  return _then(_RouteUsageDto(
type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as String,slug: null == slug ? _self.slug : slug // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,dateTime: null == dateTime ? _self.dateTime : dateTime // ignore: cast_nullable_to_non_nullable
as String,teamSlug: null == teamSlug ? _self.teamSlug : teamSlug // ignore: cast_nullable_to_non_nullable
as String,referencedDirectly: null == referencedDirectly ? _self.referencedDirectly : referencedDirectly // ignore: cast_nullable_to_non_nullable
as bool,viaChildNames: null == viaChildNames ? _self._viaChildNames : viaChildNames // ignore: cast_nullable_to_non_nullable
as List<String>,
  ));
}


}

// dart format on
