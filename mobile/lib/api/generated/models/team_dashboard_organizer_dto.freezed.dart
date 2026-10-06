// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'team_dashboard_organizer_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$TeamDashboardOrganizerDto {

/// The team's drafts (rides, posts, trips), newest first. total is the number of drafts, the rows their names.
 PublicationListResponse get drafts;/// The open reports of the team's moderation queue
 TeamDashboardReportsDto get reports;/// Published rides starting from now routed nowhere — neither the ride nor any of its groups has a route — soonest first. Same rows as GET …/publications?type=RIDE&withoutRoute=true. Null when rides are disabled.
 PublicationListResponse? get ridesWithoutRoute;/// Published rides starting from now with at least one group at capacity, soonest first. Same rows as GET …/publications?type=RIDE&withFullGroup=true. Null when rides are disabled.
 PublicationListResponse? get ridesWithFullGroup;/// « Créer depuis un modèle »: the team's ride templates (at most 5), each with its groupCount. Null when rides are disabled.
 RideTemplateListResponse? get rideTemplates;
/// Create a copy of TeamDashboardOrganizerDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$TeamDashboardOrganizerDtoCopyWith<TeamDashboardOrganizerDto> get copyWith => _$TeamDashboardOrganizerDtoCopyWithImpl<TeamDashboardOrganizerDto>(this as TeamDashboardOrganizerDto, _$identity);

  /// Serializes this TeamDashboardOrganizerDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as TeamDashboardOrganizerDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is TeamDashboardOrganizerDto&&(identical(other.drafts, _this.drafts) || other.drafts == _this.drafts)&&(identical(other.reports, _this.reports) || other.reports == _this.reports)&&(identical(other.ridesWithoutRoute, _this.ridesWithoutRoute) || other.ridesWithoutRoute == _this.ridesWithoutRoute)&&(identical(other.ridesWithFullGroup, _this.ridesWithFullGroup) || other.ridesWithFullGroup == _this.ridesWithFullGroup)&&(identical(other.rideTemplates, _this.rideTemplates) || other.rideTemplates == _this.rideTemplates));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as TeamDashboardOrganizerDto;
  return Object.hash(runtimeType,_this.drafts,_this.reports,_this.ridesWithoutRoute,_this.ridesWithFullGroup,_this.rideTemplates);
}

@override
String toString() {
  final _this = this as TeamDashboardOrganizerDto;
  return 'TeamDashboardOrganizerDto(drafts: ${_this.drafts}, reports: ${_this.reports}, ridesWithoutRoute: ${_this.ridesWithoutRoute}, ridesWithFullGroup: ${_this.ridesWithFullGroup}, rideTemplates: ${_this.rideTemplates})';
}


}

/// @nodoc
abstract mixin class $TeamDashboardOrganizerDtoCopyWith<$Res>  {
  factory $TeamDashboardOrganizerDtoCopyWith(TeamDashboardOrganizerDto value, $Res Function(TeamDashboardOrganizerDto) _then) = _$TeamDashboardOrganizerDtoCopyWithImpl;
@useResult
$Res call({
 PublicationListResponse drafts, TeamDashboardReportsDto reports, PublicationListResponse? ridesWithoutRoute, PublicationListResponse? ridesWithFullGroup, RideTemplateListResponse? rideTemplates
});


$PublicationListResponseCopyWith<$Res> get drafts;$TeamDashboardReportsDtoCopyWith<$Res> get reports;$PublicationListResponseCopyWith<$Res>? get ridesWithoutRoute;$PublicationListResponseCopyWith<$Res>? get ridesWithFullGroup;$RideTemplateListResponseCopyWith<$Res>? get rideTemplates;

}
/// @nodoc
class _$TeamDashboardOrganizerDtoCopyWithImpl<$Res>
    implements $TeamDashboardOrganizerDtoCopyWith<$Res> {
  _$TeamDashboardOrganizerDtoCopyWithImpl(this._self, this._then);

  final TeamDashboardOrganizerDto _self;
  final $Res Function(TeamDashboardOrganizerDto) _then;

/// Create a copy of TeamDashboardOrganizerDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? drafts = null,Object? reports = null,Object? ridesWithoutRoute = freezed,Object? ridesWithFullGroup = freezed,Object? rideTemplates = freezed,}) {
  return _then(TeamDashboardOrganizerDto(
drafts: null == drafts ? _self.drafts : drafts // ignore: cast_nullable_to_non_nullable
as PublicationListResponse,reports: null == reports ? _self.reports : reports // ignore: cast_nullable_to_non_nullable
as TeamDashboardReportsDto,ridesWithoutRoute: freezed == ridesWithoutRoute ? _self.ridesWithoutRoute : ridesWithoutRoute // ignore: cast_nullable_to_non_nullable
as PublicationListResponse?,ridesWithFullGroup: freezed == ridesWithFullGroup ? _self.ridesWithFullGroup : ridesWithFullGroup // ignore: cast_nullable_to_non_nullable
as PublicationListResponse?,rideTemplates: freezed == rideTemplates ? _self.rideTemplates : rideTemplates // ignore: cast_nullable_to_non_nullable
as RideTemplateListResponse?,
  ));
}
/// Create a copy of TeamDashboardOrganizerDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$PublicationListResponseCopyWith<$Res> get drafts {
  
  return $PublicationListResponseCopyWith<$Res>(_self.drafts, (value) {
    return _then(_self.copyWith(drafts: value));
  });
}/// Create a copy of TeamDashboardOrganizerDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$TeamDashboardReportsDtoCopyWith<$Res> get reports {
  
  return $TeamDashboardReportsDtoCopyWith<$Res>(_self.reports, (value) {
    return _then(_self.copyWith(reports: value));
  });
}/// Create a copy of TeamDashboardOrganizerDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$PublicationListResponseCopyWith<$Res>? get ridesWithoutRoute {
    if (_self.ridesWithoutRoute == null) {
    return null;
  }

  return $PublicationListResponseCopyWith<$Res>(_self.ridesWithoutRoute!, (value) {
    return _then(_self.copyWith(ridesWithoutRoute: value));
  });
}/// Create a copy of TeamDashboardOrganizerDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$PublicationListResponseCopyWith<$Res>? get ridesWithFullGroup {
    if (_self.ridesWithFullGroup == null) {
    return null;
  }

  return $PublicationListResponseCopyWith<$Res>(_self.ridesWithFullGroup!, (value) {
    return _then(_self.copyWith(ridesWithFullGroup: value));
  });
}/// Create a copy of TeamDashboardOrganizerDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$RideTemplateListResponseCopyWith<$Res>? get rideTemplates {
    if (_self.rideTemplates == null) {
    return null;
  }

  return $RideTemplateListResponseCopyWith<$Res>(_self.rideTemplates!, (value) {
    return _then(_self.copyWith(rideTemplates: value));
  });
}
}


/// Adds pattern-matching-related methods to [TeamDashboardOrganizerDto].
extension TeamDashboardOrganizerDtoPatterns on TeamDashboardOrganizerDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _TeamDashboardOrganizerDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _TeamDashboardOrganizerDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _TeamDashboardOrganizerDto value)  $default,){
final _that = this;
switch (_that) {
case _TeamDashboardOrganizerDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _TeamDashboardOrganizerDto value)?  $default,){
final _that = this;
switch (_that) {
case _TeamDashboardOrganizerDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( PublicationListResponse drafts,  TeamDashboardReportsDto reports,  PublicationListResponse? ridesWithoutRoute,  PublicationListResponse? ridesWithFullGroup,  RideTemplateListResponse? rideTemplates)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _TeamDashboardOrganizerDto() when $default != null:
return $default(_that.drafts,_that.reports,_that.ridesWithoutRoute,_that.ridesWithFullGroup,_that.rideTemplates);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( PublicationListResponse drafts,  TeamDashboardReportsDto reports,  PublicationListResponse? ridesWithoutRoute,  PublicationListResponse? ridesWithFullGroup,  RideTemplateListResponse? rideTemplates)  $default,) {final _that = this;
switch (_that) {
case _TeamDashboardOrganizerDto():
return $default(_that.drafts,_that.reports,_that.ridesWithoutRoute,_that.ridesWithFullGroup,_that.rideTemplates);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( PublicationListResponse drafts,  TeamDashboardReportsDto reports,  PublicationListResponse? ridesWithoutRoute,  PublicationListResponse? ridesWithFullGroup,  RideTemplateListResponse? rideTemplates)?  $default,) {final _that = this;
switch (_that) {
case _TeamDashboardOrganizerDto() when $default != null:
return $default(_that.drafts,_that.reports,_that.ridesWithoutRoute,_that.ridesWithFullGroup,_that.rideTemplates);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _TeamDashboardOrganizerDto implements TeamDashboardOrganizerDto {
  const _TeamDashboardOrganizerDto({required this.drafts, required this.reports, this.ridesWithoutRoute, this.ridesWithFullGroup, this.rideTemplates});
  factory _TeamDashboardOrganizerDto.fromJson(Map<String, dynamic> json) => _$TeamDashboardOrganizerDtoFromJson(json);

/// The team's drafts (rides, posts, trips), newest first. total is the number of drafts, the rows their names.
@override final  PublicationListResponse drafts;
/// The open reports of the team's moderation queue
@override final  TeamDashboardReportsDto reports;
/// Published rides starting from now routed nowhere — neither the ride nor any of its groups has a route — soonest first. Same rows as GET …/publications?type=RIDE&withoutRoute=true. Null when rides are disabled.
@override final  PublicationListResponse? ridesWithoutRoute;
/// Published rides starting from now with at least one group at capacity, soonest first. Same rows as GET …/publications?type=RIDE&withFullGroup=true. Null when rides are disabled.
@override final  PublicationListResponse? ridesWithFullGroup;
/// « Créer depuis un modèle »: the team's ride templates (at most 5), each with its groupCount. Null when rides are disabled.
@override final  RideTemplateListResponse? rideTemplates;

/// Create a copy of TeamDashboardOrganizerDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$TeamDashboardOrganizerDtoCopyWith<_TeamDashboardOrganizerDto> get copyWith => __$TeamDashboardOrganizerDtoCopyWithImpl<_TeamDashboardOrganizerDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$TeamDashboardOrganizerDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _TeamDashboardOrganizerDto&&(identical(other.drafts, drafts) || other.drafts == drafts)&&(identical(other.reports, reports) || other.reports == reports)&&(identical(other.ridesWithoutRoute, ridesWithoutRoute) || other.ridesWithoutRoute == ridesWithoutRoute)&&(identical(other.ridesWithFullGroup, ridesWithFullGroup) || other.ridesWithFullGroup == ridesWithFullGroup)&&(identical(other.rideTemplates, rideTemplates) || other.rideTemplates == rideTemplates));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,drafts,reports,ridesWithoutRoute,ridesWithFullGroup,rideTemplates);
}

@override
String toString() {
    return 'TeamDashboardOrganizerDto(drafts: $drafts, reports: $reports, ridesWithoutRoute: $ridesWithoutRoute, ridesWithFullGroup: $ridesWithFullGroup, rideTemplates: $rideTemplates)';
}


}

/// @nodoc
abstract mixin class _$TeamDashboardOrganizerDtoCopyWith<$Res> implements $TeamDashboardOrganizerDtoCopyWith<$Res> {
  factory _$TeamDashboardOrganizerDtoCopyWith(_TeamDashboardOrganizerDto value, $Res Function(_TeamDashboardOrganizerDto) _then) = __$TeamDashboardOrganizerDtoCopyWithImpl;
@override @useResult
$Res call({
 PublicationListResponse drafts, TeamDashboardReportsDto reports, PublicationListResponse? ridesWithoutRoute, PublicationListResponse? ridesWithFullGroup, RideTemplateListResponse? rideTemplates
});


@override $PublicationListResponseCopyWith<$Res> get drafts;@override $TeamDashboardReportsDtoCopyWith<$Res> get reports;@override $PublicationListResponseCopyWith<$Res>? get ridesWithoutRoute;@override $PublicationListResponseCopyWith<$Res>? get ridesWithFullGroup;@override $RideTemplateListResponseCopyWith<$Res>? get rideTemplates;

}
/// @nodoc
class __$TeamDashboardOrganizerDtoCopyWithImpl<$Res>
    implements _$TeamDashboardOrganizerDtoCopyWith<$Res> {
  __$TeamDashboardOrganizerDtoCopyWithImpl(this._self, this._then);

  final _TeamDashboardOrganizerDto _self;
  final $Res Function(_TeamDashboardOrganizerDto) _then;

/// Create a copy of TeamDashboardOrganizerDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? drafts = null,Object? reports = null,Object? ridesWithoutRoute = freezed,Object? ridesWithFullGroup = freezed,Object? rideTemplates = freezed,}) {
  return _then(_TeamDashboardOrganizerDto(
drafts: null == drafts ? _self.drafts : drafts // ignore: cast_nullable_to_non_nullable
as PublicationListResponse,reports: null == reports ? _self.reports : reports // ignore: cast_nullable_to_non_nullable
as TeamDashboardReportsDto,ridesWithoutRoute: freezed == ridesWithoutRoute ? _self.ridesWithoutRoute : ridesWithoutRoute // ignore: cast_nullable_to_non_nullable
as PublicationListResponse?,ridesWithFullGroup: freezed == ridesWithFullGroup ? _self.ridesWithFullGroup : ridesWithFullGroup // ignore: cast_nullable_to_non_nullable
as PublicationListResponse?,rideTemplates: freezed == rideTemplates ? _self.rideTemplates : rideTemplates // ignore: cast_nullable_to_non_nullable
as RideTemplateListResponse?,
  ));
}

/// Create a copy of TeamDashboardOrganizerDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$PublicationListResponseCopyWith<$Res> get drafts {
  
  return $PublicationListResponseCopyWith<$Res>(_self.drafts, (value) {
    return _then(_self.copyWith(drafts: value));
  });
}/// Create a copy of TeamDashboardOrganizerDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$TeamDashboardReportsDtoCopyWith<$Res> get reports {
  
  return $TeamDashboardReportsDtoCopyWith<$Res>(_self.reports, (value) {
    return _then(_self.copyWith(reports: value));
  });
}/// Create a copy of TeamDashboardOrganizerDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$PublicationListResponseCopyWith<$Res>? get ridesWithoutRoute {
    if (_self.ridesWithoutRoute == null) {
    return null;
  }

  return $PublicationListResponseCopyWith<$Res>(_self.ridesWithoutRoute!, (value) {
    return _then(_self.copyWith(ridesWithoutRoute: value));
  });
}/// Create a copy of TeamDashboardOrganizerDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$PublicationListResponseCopyWith<$Res>? get ridesWithFullGroup {
    if (_self.ridesWithFullGroup == null) {
    return null;
  }

  return $PublicationListResponseCopyWith<$Res>(_self.ridesWithFullGroup!, (value) {
    return _then(_self.copyWith(ridesWithFullGroup: value));
  });
}/// Create a copy of TeamDashboardOrganizerDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$RideTemplateListResponseCopyWith<$Res>? get rideTemplates {
    if (_self.rideTemplates == null) {
    return null;
  }

  return $RideTemplateListResponseCopyWith<$Res>(_self.rideTemplates!, (value) {
    return _then(_self.copyWith(rideTemplates: value));
  });
}
}

// dart format on
