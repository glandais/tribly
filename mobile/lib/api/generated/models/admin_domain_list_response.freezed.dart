// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'admin_domain_list_response.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$AdminDomainListResponse {

/// List of domains
 List<AdminDomainDto> get domains;/// Total number of domains
 int get total;/// Current page number
 int get page;/// Page size
 int get size;
/// Create a copy of AdminDomainListResponse
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$AdminDomainListResponseCopyWith<AdminDomainListResponse> get copyWith => _$AdminDomainListResponseCopyWithImpl<AdminDomainListResponse>(this as AdminDomainListResponse, _$identity);

  /// Serializes this AdminDomainListResponse to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is AdminDomainListResponse&&const DeepCollectionEquality().equals(other.domains, domains)&&(identical(other.total, total) || other.total == total)&&(identical(other.page, page) || other.page == page)&&(identical(other.size, size) || other.size == size));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,const DeepCollectionEquality().hash(domains),total,page,size);

@override
String toString() {
  return 'AdminDomainListResponse(domains: $domains, total: $total, page: $page, size: $size)';
}


}

/// @nodoc
abstract mixin class $AdminDomainListResponseCopyWith<$Res>  {
  factory $AdminDomainListResponseCopyWith(AdminDomainListResponse value, $Res Function(AdminDomainListResponse) _then) = _$AdminDomainListResponseCopyWithImpl;
@useResult
$Res call({
 List<AdminDomainDto> domains, int total, int page, int size
});




}
/// @nodoc
class _$AdminDomainListResponseCopyWithImpl<$Res>
    implements $AdminDomainListResponseCopyWith<$Res> {
  _$AdminDomainListResponseCopyWithImpl(this._self, this._then);

  final AdminDomainListResponse _self;
  final $Res Function(AdminDomainListResponse) _then;

/// Create a copy of AdminDomainListResponse
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? domains = null,Object? total = null,Object? page = null,Object? size = null,}) {
  return _then(_self.copyWith(
domains: null == domains ? _self.domains : domains // ignore: cast_nullable_to_non_nullable
as List<AdminDomainDto>,total: null == total ? _self.total : total // ignore: cast_nullable_to_non_nullable
as int,page: null == page ? _self.page : page // ignore: cast_nullable_to_non_nullable
as int,size: null == size ? _self.size : size // ignore: cast_nullable_to_non_nullable
as int,
  ));
}

}


/// Adds pattern-matching-related methods to [AdminDomainListResponse].
extension AdminDomainListResponsePatterns on AdminDomainListResponse {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _AdminDomainListResponse value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _AdminDomainListResponse() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _AdminDomainListResponse value)  $default,){
final _that = this;
switch (_that) {
case _AdminDomainListResponse():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _AdminDomainListResponse value)?  $default,){
final _that = this;
switch (_that) {
case _AdminDomainListResponse() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( List<AdminDomainDto> domains,  int total,  int page,  int size)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _AdminDomainListResponse() when $default != null:
return $default(_that.domains,_that.total,_that.page,_that.size);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( List<AdminDomainDto> domains,  int total,  int page,  int size)  $default,) {final _that = this;
switch (_that) {
case _AdminDomainListResponse():
return $default(_that.domains,_that.total,_that.page,_that.size);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( List<AdminDomainDto> domains,  int total,  int page,  int size)?  $default,) {final _that = this;
switch (_that) {
case _AdminDomainListResponse() when $default != null:
return $default(_that.domains,_that.total,_that.page,_that.size);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _AdminDomainListResponse implements AdminDomainListResponse {
  const _AdminDomainListResponse({required final  List<AdminDomainDto> domains, required this.total, required this.page, required this.size}): _domains = domains;
  factory _AdminDomainListResponse.fromJson(Map<String, dynamic> json) => _$AdminDomainListResponseFromJson(json);

/// List of domains
 final  List<AdminDomainDto> _domains;
/// List of domains
@override List<AdminDomainDto> get domains {
  if (_domains is EqualUnmodifiableListView) return _domains;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_domains);
}

/// Total number of domains
@override final  int total;
/// Current page number
@override final  int page;
/// Page size
@override final  int size;

/// Create a copy of AdminDomainListResponse
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$AdminDomainListResponseCopyWith<_AdminDomainListResponse> get copyWith => __$AdminDomainListResponseCopyWithImpl<_AdminDomainListResponse>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$AdminDomainListResponseToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _AdminDomainListResponse&&const DeepCollectionEquality().equals(other._domains, _domains)&&(identical(other.total, total) || other.total == total)&&(identical(other.page, page) || other.page == page)&&(identical(other.size, size) || other.size == size));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,const DeepCollectionEquality().hash(_domains),total,page,size);

@override
String toString() {
  return 'AdminDomainListResponse(domains: $domains, total: $total, page: $page, size: $size)';
}


}

/// @nodoc
abstract mixin class _$AdminDomainListResponseCopyWith<$Res> implements $AdminDomainListResponseCopyWith<$Res> {
  factory _$AdminDomainListResponseCopyWith(_AdminDomainListResponse value, $Res Function(_AdminDomainListResponse) _then) = __$AdminDomainListResponseCopyWithImpl;
@override @useResult
$Res call({
 List<AdminDomainDto> domains, int total, int page, int size
});




}
/// @nodoc
class __$AdminDomainListResponseCopyWithImpl<$Res>
    implements _$AdminDomainListResponseCopyWith<$Res> {
  __$AdminDomainListResponseCopyWithImpl(this._self, this._then);

  final _AdminDomainListResponse _self;
  final $Res Function(_AdminDomainListResponse) _then;

/// Create a copy of AdminDomainListResponse
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? domains = null,Object? total = null,Object? page = null,Object? size = null,}) {
  return _then(_AdminDomainListResponse(
domains: null == domains ? _self._domains : domains // ignore: cast_nullable_to_non_nullable
as List<AdminDomainDto>,total: null == total ? _self.total : total // ignore: cast_nullable_to_non_nullable
as int,page: null == page ? _self.page : page // ignore: cast_nullable_to_non_nullable
as int,size: null == size ? _self.size : size // ignore: cast_nullable_to_non_nullable
as int,
  ));
}


}

// dart format on
