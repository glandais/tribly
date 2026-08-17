// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'ride_template_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$RideTemplateRequest {

/// Template name
 String get name;/// Template description (markdown)
 String get markdown;/// Visibility level
 String get visibility;/// Default status for rides created from this template
 String get status;/// Template groups
 List<RideTemplateGroupRequest> get groups;
/// Create a copy of RideTemplateRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$RideTemplateRequestCopyWith<RideTemplateRequest> get copyWith => _$RideTemplateRequestCopyWithImpl<RideTemplateRequest>(this as RideTemplateRequest, _$identity);

  /// Serializes this RideTemplateRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is RideTemplateRequest&&(identical(other.name, name) || other.name == name)&&(identical(other.markdown, markdown) || other.markdown == markdown)&&(identical(other.visibility, visibility) || other.visibility == visibility)&&(identical(other.status, status) || other.status == status)&&const DeepCollectionEquality().equals(other.groups, groups));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,name,markdown,visibility,status,const DeepCollectionEquality().hash(groups));

@override
String toString() {
  return 'RideTemplateRequest(name: $name, markdown: $markdown, visibility: $visibility, status: $status, groups: $groups)';
}


}

/// @nodoc
abstract mixin class $RideTemplateRequestCopyWith<$Res>  {
  factory $RideTemplateRequestCopyWith(RideTemplateRequest value, $Res Function(RideTemplateRequest) _then) = _$RideTemplateRequestCopyWithImpl;
@useResult
$Res call({
 String name, String markdown, String visibility, String status, List<RideTemplateGroupRequest> groups
});




}
/// @nodoc
class _$RideTemplateRequestCopyWithImpl<$Res>
    implements $RideTemplateRequestCopyWith<$Res> {
  _$RideTemplateRequestCopyWithImpl(this._self, this._then);

  final RideTemplateRequest _self;
  final $Res Function(RideTemplateRequest) _then;

/// Create a copy of RideTemplateRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? name = null,Object? markdown = null,Object? visibility = null,Object? status = null,Object? groups = null,}) {
  return _then(RideTemplateRequest(
name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,markdown: null == markdown ? _self.markdown : markdown // ignore: cast_nullable_to_non_nullable
as String,visibility: null == visibility ? _self.visibility : visibility // ignore: cast_nullable_to_non_nullable
as String,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,groups: null == groups ? _self.groups : groups // ignore: cast_nullable_to_non_nullable
as List<RideTemplateGroupRequest>,
  ));
}

}


/// Adds pattern-matching-related methods to [RideTemplateRequest].
extension RideTemplateRequestPatterns on RideTemplateRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _RideTemplateRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _RideTemplateRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _RideTemplateRequest value)  $default,){
final _that = this;
switch (_that) {
case _RideTemplateRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _RideTemplateRequest value)?  $default,){
final _that = this;
switch (_that) {
case _RideTemplateRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String name,  String markdown,  String visibility,  String status,  List<RideTemplateGroupRequest> groups)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _RideTemplateRequest() when $default != null:
return $default(_that.name,_that.markdown,_that.visibility,_that.status,_that.groups);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String name,  String markdown,  String visibility,  String status,  List<RideTemplateGroupRequest> groups)  $default,) {final _that = this;
switch (_that) {
case _RideTemplateRequest():
return $default(_that.name,_that.markdown,_that.visibility,_that.status,_that.groups);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String name,  String markdown,  String visibility,  String status,  List<RideTemplateGroupRequest> groups)?  $default,) {final _that = this;
switch (_that) {
case _RideTemplateRequest() when $default != null:
return $default(_that.name,_that.markdown,_that.visibility,_that.status,_that.groups);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _RideTemplateRequest implements RideTemplateRequest {
  const _RideTemplateRequest({required this.name, required this.markdown, required this.visibility, required this.status, required  List<RideTemplateGroupRequest> groups}): _groups = groups;
  factory _RideTemplateRequest.fromJson(Map<String, dynamic> json) => _$RideTemplateRequestFromJson(json);

/// Template name
@override final  String name;
/// Template description (markdown)
@override final  String markdown;
/// Visibility level
@override final  String visibility;
/// Default status for rides created from this template
@override final  String status;
/// Template groups
 final  List<RideTemplateGroupRequest> _groups;
/// Template groups
@override List<RideTemplateGroupRequest> get groups {
  if (_groups is EqualUnmodifiableListView) return _groups;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_groups);
}


/// Create a copy of RideTemplateRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$RideTemplateRequestCopyWith<_RideTemplateRequest> get copyWith => __$RideTemplateRequestCopyWithImpl<_RideTemplateRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$RideTemplateRequestToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _RideTemplateRequest&&(identical(other.name, name) || other.name == name)&&(identical(other.markdown, markdown) || other.markdown == markdown)&&(identical(other.visibility, visibility) || other.visibility == visibility)&&(identical(other.status, status) || other.status == status)&&const DeepCollectionEquality().equals(other._groups, _groups));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,name,markdown,visibility,status,const DeepCollectionEquality().hash(_groups));

@override
String toString() {
  return 'RideTemplateRequest(name: $name, markdown: $markdown, visibility: $visibility, status: $status, groups: $groups)';
}


}

/// @nodoc
abstract mixin class _$RideTemplateRequestCopyWith<$Res> implements $RideTemplateRequestCopyWith<$Res> {
  factory _$RideTemplateRequestCopyWith(_RideTemplateRequest value, $Res Function(_RideTemplateRequest) _then) = __$RideTemplateRequestCopyWithImpl;
@override @useResult
$Res call({
 String name, String markdown, String visibility, String status, List<RideTemplateGroupRequest> groups
});




}
/// @nodoc
class __$RideTemplateRequestCopyWithImpl<$Res>
    implements _$RideTemplateRequestCopyWith<$Res> {
  __$RideTemplateRequestCopyWithImpl(this._self, this._then);

  final _RideTemplateRequest _self;
  final $Res Function(_RideTemplateRequest) _then;

/// Create a copy of RideTemplateRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? name = null,Object? markdown = null,Object? visibility = null,Object? status = null,Object? groups = null,}) {
  return _then(_RideTemplateRequest(
name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,markdown: null == markdown ? _self.markdown : markdown // ignore: cast_nullable_to_non_nullable
as String,visibility: null == visibility ? _self.visibility : visibility // ignore: cast_nullable_to_non_nullable
as String,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,groups: null == groups ? _self._groups : groups // ignore: cast_nullable_to_non_nullable
as List<RideTemplateGroupRequest>,
  ));
}


}

// dart format on
