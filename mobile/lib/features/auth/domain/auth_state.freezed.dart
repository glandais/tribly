// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'auth_state.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;
/// @nodoc
mixin _$AuthState {

/// Currently authenticated user
 UserDto? get user;/// JWT access token (stored in memory only)
 String? get accessToken;/// Whether the user has any registered passkeys
 bool get hasPasskeys;/// Whether the auth state has been initialized
 bool get isInitialized;/// Whether initialization is in progress
 bool get isInitializing;/// Whether an auth operation is in progress
 bool get isLoading;/// Last error message
 String? get error;
/// Create a copy of AuthState
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$AuthStateCopyWith<AuthState> get copyWith => _$AuthStateCopyWithImpl<AuthState>(this as AuthState, _$identity);



@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is AuthState&&(identical(other.user, user) || other.user == user)&&(identical(other.accessToken, accessToken) || other.accessToken == accessToken)&&(identical(other.hasPasskeys, hasPasskeys) || other.hasPasskeys == hasPasskeys)&&(identical(other.isInitialized, isInitialized) || other.isInitialized == isInitialized)&&(identical(other.isInitializing, isInitializing) || other.isInitializing == isInitializing)&&(identical(other.isLoading, isLoading) || other.isLoading == isLoading)&&(identical(other.error, error) || other.error == error));
}


@override
int get hashCode => Object.hash(runtimeType,user,accessToken,hasPasskeys,isInitialized,isInitializing,isLoading,error);

@override
String toString() {
  return 'AuthState(user: $user, accessToken: $accessToken, hasPasskeys: $hasPasskeys, isInitialized: $isInitialized, isInitializing: $isInitializing, isLoading: $isLoading, error: $error)';
}


}

/// @nodoc
abstract mixin class $AuthStateCopyWith<$Res>  {
  factory $AuthStateCopyWith(AuthState value, $Res Function(AuthState) _then) = _$AuthStateCopyWithImpl;
@useResult
$Res call({
 UserDto? user, String? accessToken, bool hasPasskeys, bool isInitialized, bool isInitializing, bool isLoading, String? error
});


$UserDtoCopyWith<$Res>? get user;

}
/// @nodoc
class _$AuthStateCopyWithImpl<$Res>
    implements $AuthStateCopyWith<$Res> {
  _$AuthStateCopyWithImpl(this._self, this._then);

  final AuthState _self;
  final $Res Function(AuthState) _then;

/// Create a copy of AuthState
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? user = freezed,Object? accessToken = freezed,Object? hasPasskeys = null,Object? isInitialized = null,Object? isInitializing = null,Object? isLoading = null,Object? error = freezed,}) {
  return _then(_self.copyWith(
user: freezed == user ? _self.user : user // ignore: cast_nullable_to_non_nullable
as UserDto?,accessToken: freezed == accessToken ? _self.accessToken : accessToken // ignore: cast_nullable_to_non_nullable
as String?,hasPasskeys: null == hasPasskeys ? _self.hasPasskeys : hasPasskeys // ignore: cast_nullable_to_non_nullable
as bool,isInitialized: null == isInitialized ? _self.isInitialized : isInitialized // ignore: cast_nullable_to_non_nullable
as bool,isInitializing: null == isInitializing ? _self.isInitializing : isInitializing // ignore: cast_nullable_to_non_nullable
as bool,isLoading: null == isLoading ? _self.isLoading : isLoading // ignore: cast_nullable_to_non_nullable
as bool,error: freezed == error ? _self.error : error // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}
/// Create a copy of AuthState
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$UserDtoCopyWith<$Res>? get user {
    if (_self.user == null) {
    return null;
  }

  return $UserDtoCopyWith<$Res>(_self.user!, (value) {
    return _then(_self.copyWith(user: value));
  });
}
}


/// Adds pattern-matching-related methods to [AuthState].
extension AuthStatePatterns on AuthState {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _AuthState value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _AuthState() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _AuthState value)  $default,){
final _that = this;
switch (_that) {
case _AuthState():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _AuthState value)?  $default,){
final _that = this;
switch (_that) {
case _AuthState() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( UserDto? user,  String? accessToken,  bool hasPasskeys,  bool isInitialized,  bool isInitializing,  bool isLoading,  String? error)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _AuthState() when $default != null:
return $default(_that.user,_that.accessToken,_that.hasPasskeys,_that.isInitialized,_that.isInitializing,_that.isLoading,_that.error);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( UserDto? user,  String? accessToken,  bool hasPasskeys,  bool isInitialized,  bool isInitializing,  bool isLoading,  String? error)  $default,) {final _that = this;
switch (_that) {
case _AuthState():
return $default(_that.user,_that.accessToken,_that.hasPasskeys,_that.isInitialized,_that.isInitializing,_that.isLoading,_that.error);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( UserDto? user,  String? accessToken,  bool hasPasskeys,  bool isInitialized,  bool isInitializing,  bool isLoading,  String? error)?  $default,) {final _that = this;
switch (_that) {
case _AuthState() when $default != null:
return $default(_that.user,_that.accessToken,_that.hasPasskeys,_that.isInitialized,_that.isInitializing,_that.isLoading,_that.error);case _:
  return null;

}
}

}

/// @nodoc


class _AuthState extends AuthState {
  const _AuthState({this.user, this.accessToken, this.hasPasskeys = false, this.isInitialized = false, this.isInitializing = false, this.isLoading = false, this.error}): super._();
  

/// Currently authenticated user
@override final  UserDto? user;
/// JWT access token (stored in memory only)
@override final  String? accessToken;
/// Whether the user has any registered passkeys
@override@JsonKey() final  bool hasPasskeys;
/// Whether the auth state has been initialized
@override@JsonKey() final  bool isInitialized;
/// Whether initialization is in progress
@override@JsonKey() final  bool isInitializing;
/// Whether an auth operation is in progress
@override@JsonKey() final  bool isLoading;
/// Last error message
@override final  String? error;

/// Create a copy of AuthState
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$AuthStateCopyWith<_AuthState> get copyWith => __$AuthStateCopyWithImpl<_AuthState>(this, _$identity);



@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _AuthState&&(identical(other.user, user) || other.user == user)&&(identical(other.accessToken, accessToken) || other.accessToken == accessToken)&&(identical(other.hasPasskeys, hasPasskeys) || other.hasPasskeys == hasPasskeys)&&(identical(other.isInitialized, isInitialized) || other.isInitialized == isInitialized)&&(identical(other.isInitializing, isInitializing) || other.isInitializing == isInitializing)&&(identical(other.isLoading, isLoading) || other.isLoading == isLoading)&&(identical(other.error, error) || other.error == error));
}


@override
int get hashCode => Object.hash(runtimeType,user,accessToken,hasPasskeys,isInitialized,isInitializing,isLoading,error);

@override
String toString() {
  return 'AuthState(user: $user, accessToken: $accessToken, hasPasskeys: $hasPasskeys, isInitialized: $isInitialized, isInitializing: $isInitializing, isLoading: $isLoading, error: $error)';
}


}

/// @nodoc
abstract mixin class _$AuthStateCopyWith<$Res> implements $AuthStateCopyWith<$Res> {
  factory _$AuthStateCopyWith(_AuthState value, $Res Function(_AuthState) _then) = __$AuthStateCopyWithImpl;
@override @useResult
$Res call({
 UserDto? user, String? accessToken, bool hasPasskeys, bool isInitialized, bool isInitializing, bool isLoading, String? error
});


@override $UserDtoCopyWith<$Res>? get user;

}
/// @nodoc
class __$AuthStateCopyWithImpl<$Res>
    implements _$AuthStateCopyWith<$Res> {
  __$AuthStateCopyWithImpl(this._self, this._then);

  final _AuthState _self;
  final $Res Function(_AuthState) _then;

/// Create a copy of AuthState
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? user = freezed,Object? accessToken = freezed,Object? hasPasskeys = null,Object? isInitialized = null,Object? isInitializing = null,Object? isLoading = null,Object? error = freezed,}) {
  return _then(_AuthState(
user: freezed == user ? _self.user : user // ignore: cast_nullable_to_non_nullable
as UserDto?,accessToken: freezed == accessToken ? _self.accessToken : accessToken // ignore: cast_nullable_to_non_nullable
as String?,hasPasskeys: null == hasPasskeys ? _self.hasPasskeys : hasPasskeys // ignore: cast_nullable_to_non_nullable
as bool,isInitialized: null == isInitialized ? _self.isInitialized : isInitialized // ignore: cast_nullable_to_non_nullable
as bool,isInitializing: null == isInitializing ? _self.isInitializing : isInitializing // ignore: cast_nullable_to_non_nullable
as bool,isLoading: null == isLoading ? _self.isLoading : isLoading // ignore: cast_nullable_to_non_nullable
as bool,error: freezed == error ? _self.error : error // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

/// Create a copy of AuthState
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$UserDtoCopyWith<$Res>? get user {
    if (_self.user == null) {
    return null;
  }

  return $UserDtoCopyWith<$Res>(_self.user!, (value) {
    return _then(_self.copyWith(user: value));
  });
}
}

// dart format on
