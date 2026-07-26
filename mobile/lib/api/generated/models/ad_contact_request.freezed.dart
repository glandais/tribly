// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'ad_contact_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$AdContactRequest {

/// The message body, plain text. Rendered as text in the email: any markup is escaped rather than interpreted.
 String get message;
/// Create a copy of AdContactRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$AdContactRequestCopyWith<AdContactRequest> get copyWith => _$AdContactRequestCopyWithImpl<AdContactRequest>(this as AdContactRequest, _$identity);

  /// Serializes this AdContactRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is AdContactRequest&&(identical(other.message, message) || other.message == message));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,message);

@override
String toString() {
  return 'AdContactRequest(message: $message)';
}


}

/// @nodoc
abstract mixin class $AdContactRequestCopyWith<$Res>  {
  factory $AdContactRequestCopyWith(AdContactRequest value, $Res Function(AdContactRequest) _then) = _$AdContactRequestCopyWithImpl;
@useResult
$Res call({
 String message
});




}
/// @nodoc
class _$AdContactRequestCopyWithImpl<$Res>
    implements $AdContactRequestCopyWith<$Res> {
  _$AdContactRequestCopyWithImpl(this._self, this._then);

  final AdContactRequest _self;
  final $Res Function(AdContactRequest) _then;

/// Create a copy of AdContactRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? message = null,}) {
  return _then(_self.copyWith(
message: null == message ? _self.message : message // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [AdContactRequest].
extension AdContactRequestPatterns on AdContactRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _AdContactRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _AdContactRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _AdContactRequest value)  $default,){
final _that = this;
switch (_that) {
case _AdContactRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _AdContactRequest value)?  $default,){
final _that = this;
switch (_that) {
case _AdContactRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String message)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _AdContactRequest() when $default != null:
return $default(_that.message);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String message)  $default,) {final _that = this;
switch (_that) {
case _AdContactRequest():
return $default(_that.message);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String message)?  $default,) {final _that = this;
switch (_that) {
case _AdContactRequest() when $default != null:
return $default(_that.message);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _AdContactRequest implements AdContactRequest {
  const _AdContactRequest({required this.message});
  factory _AdContactRequest.fromJson(Map<String, dynamic> json) => _$AdContactRequestFromJson(json);

/// The message body, plain text. Rendered as text in the email: any markup is escaped rather than interpreted.
@override final  String message;

/// Create a copy of AdContactRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$AdContactRequestCopyWith<_AdContactRequest> get copyWith => __$AdContactRequestCopyWithImpl<_AdContactRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$AdContactRequestToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _AdContactRequest&&(identical(other.message, message) || other.message == message));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,message);

@override
String toString() {
  return 'AdContactRequest(message: $message)';
}


}

/// @nodoc
abstract mixin class _$AdContactRequestCopyWith<$Res> implements $AdContactRequestCopyWith<$Res> {
  factory _$AdContactRequestCopyWith(_AdContactRequest value, $Res Function(_AdContactRequest) _then) = __$AdContactRequestCopyWithImpl;
@override @useResult
$Res call({
 String message
});




}
/// @nodoc
class __$AdContactRequestCopyWithImpl<$Res>
    implements _$AdContactRequestCopyWith<$Res> {
  __$AdContactRequestCopyWithImpl(this._self, this._then);

  final _AdContactRequest _self;
  final $Res Function(_AdContactRequest) _then;

/// Create a copy of AdContactRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? message = null,}) {
  return _then(_AdContactRequest(
message: null == message ? _self.message : message // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
