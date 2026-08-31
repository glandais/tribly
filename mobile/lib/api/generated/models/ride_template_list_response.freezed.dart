// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'ride_template_list_response.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$RideTemplateListResponse {

/// List of templates
 List<RideTemplateDto> get templates;/// Total number of templates
 int get total;/// Current page number
 int get page;/// Page size
 int get size;
/// Create a copy of RideTemplateListResponse
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$RideTemplateListResponseCopyWith<RideTemplateListResponse> get copyWith => _$RideTemplateListResponseCopyWithImpl<RideTemplateListResponse>(this as RideTemplateListResponse, _$identity);

  /// Serializes this RideTemplateListResponse to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as RideTemplateListResponse;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is RideTemplateListResponse&&const DeepCollectionEquality().equals(other.templates, _this.templates)&&(identical(other.total, _this.total) || other.total == _this.total)&&(identical(other.page, _this.page) || other.page == _this.page)&&(identical(other.size, _this.size) || other.size == _this.size));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as RideTemplateListResponse;
  return Object.hash(runtimeType,const DeepCollectionEquality().hash(_this.templates),_this.total,_this.page,_this.size);
}

@override
String toString() {
  final _this = this as RideTemplateListResponse;
  return 'RideTemplateListResponse(templates: ${_this.templates}, total: ${_this.total}, page: ${_this.page}, size: ${_this.size})';
}


}

/// @nodoc
abstract mixin class $RideTemplateListResponseCopyWith<$Res>  {
  factory $RideTemplateListResponseCopyWith(RideTemplateListResponse value, $Res Function(RideTemplateListResponse) _then) = _$RideTemplateListResponseCopyWithImpl;
@useResult
$Res call({
 List<RideTemplateDto> templates, int total, int page, int size
});




}
/// @nodoc
class _$RideTemplateListResponseCopyWithImpl<$Res>
    implements $RideTemplateListResponseCopyWith<$Res> {
  _$RideTemplateListResponseCopyWithImpl(this._self, this._then);

  final RideTemplateListResponse _self;
  final $Res Function(RideTemplateListResponse) _then;

/// Create a copy of RideTemplateListResponse
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? templates = null,Object? total = null,Object? page = null,Object? size = null,}) {
  return _then(RideTemplateListResponse(
templates: null == templates ? _self.templates : templates // ignore: cast_nullable_to_non_nullable
as List<RideTemplateDto>,total: null == total ? _self.total : total // ignore: cast_nullable_to_non_nullable
as int,page: null == page ? _self.page : page // ignore: cast_nullable_to_non_nullable
as int,size: null == size ? _self.size : size // ignore: cast_nullable_to_non_nullable
as int,
  ));
}

}


/// Adds pattern-matching-related methods to [RideTemplateListResponse].
extension RideTemplateListResponsePatterns on RideTemplateListResponse {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _RideTemplateListResponse value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _RideTemplateListResponse() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _RideTemplateListResponse value)  $default,){
final _that = this;
switch (_that) {
case _RideTemplateListResponse():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _RideTemplateListResponse value)?  $default,){
final _that = this;
switch (_that) {
case _RideTemplateListResponse() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( List<RideTemplateDto> templates,  int total,  int page,  int size)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _RideTemplateListResponse() when $default != null:
return $default(_that.templates,_that.total,_that.page,_that.size);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( List<RideTemplateDto> templates,  int total,  int page,  int size)  $default,) {final _that = this;
switch (_that) {
case _RideTemplateListResponse():
return $default(_that.templates,_that.total,_that.page,_that.size);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( List<RideTemplateDto> templates,  int total,  int page,  int size)?  $default,) {final _that = this;
switch (_that) {
case _RideTemplateListResponse() when $default != null:
return $default(_that.templates,_that.total,_that.page,_that.size);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _RideTemplateListResponse implements RideTemplateListResponse {
  const _RideTemplateListResponse({required  List<RideTemplateDto> templates, required this.total, required this.page, required this.size}): _templates = templates;
  factory _RideTemplateListResponse.fromJson(Map<String, dynamic> json) => _$RideTemplateListResponseFromJson(json);

/// List of templates
 final  List<RideTemplateDto> _templates;
/// List of templates
@override List<RideTemplateDto> get templates {
  if (_templates is EqualUnmodifiableListView) return _templates;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_templates);
}

/// Total number of templates
@override final  int total;
/// Current page number
@override final  int page;
/// Page size
@override final  int size;

/// Create a copy of RideTemplateListResponse
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$RideTemplateListResponseCopyWith<_RideTemplateListResponse> get copyWith => __$RideTemplateListResponseCopyWithImpl<_RideTemplateListResponse>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$RideTemplateListResponseToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _RideTemplateListResponse&&const DeepCollectionEquality().equals(other.templates, _templates)&&(identical(other.total, total) || other.total == total)&&(identical(other.page, page) || other.page == page)&&(identical(other.size, size) || other.size == size));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,const DeepCollectionEquality().hash(_templates),total,page,size);
}

@override
String toString() {
    return 'RideTemplateListResponse(templates: $templates, total: $total, page: $page, size: $size)';
}


}

/// @nodoc
abstract mixin class _$RideTemplateListResponseCopyWith<$Res> implements $RideTemplateListResponseCopyWith<$Res> {
  factory _$RideTemplateListResponseCopyWith(_RideTemplateListResponse value, $Res Function(_RideTemplateListResponse) _then) = __$RideTemplateListResponseCopyWithImpl;
@override @useResult
$Res call({
 List<RideTemplateDto> templates, int total, int page, int size
});




}
/// @nodoc
class __$RideTemplateListResponseCopyWithImpl<$Res>
    implements _$RideTemplateListResponseCopyWith<$Res> {
  __$RideTemplateListResponseCopyWithImpl(this._self, this._then);

  final _RideTemplateListResponse _self;
  final $Res Function(_RideTemplateListResponse) _then;

/// Create a copy of RideTemplateListResponse
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? templates = null,Object? total = null,Object? page = null,Object? size = null,}) {
  return _then(_RideTemplateListResponse(
templates: null == templates ? _self._templates : templates // ignore: cast_nullable_to_non_nullable
as List<RideTemplateDto>,total: null == total ? _self.total : total // ignore: cast_nullable_to_non_nullable
as int,page: null == page ? _self.page : page // ignore: cast_nullable_to_non_nullable
as int,size: null == size ? _self.size : size // ignore: cast_nullable_to_non_nullable
as int,
  ));
}


}

// dart format on
