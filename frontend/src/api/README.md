## @tribly/api@1.0.0

This generator creates TypeScript/JavaScript client that utilizes [axios](https://github.com/axios/axios). The generated Node module can be used in the following environments:

Environment
* Node.js
* Webpack
* Browserify

Language level
* ES5 - you must have a Promises/A+ library installed
* ES6

Module system
* CommonJS
* ES6 module system

It can be used in both TypeScript and JavaScript. In TypeScript, the definition will be automatically resolved via `package.json`. ([Reference](https://www.typescriptlang.org/docs/handbook/declaration-files/consumption.html))

### Building

To build and compile the typescript sources to javascript use:
```
npm install
npm run build
```

### Publishing

First build the package then run `npm publish`

### Consuming

navigate to the folder of your consuming project and run one of the following commands.

_published:_

```
npm install @tribly/api@1.0.0 --save
```

_unPublished (not recommended):_

```
npm install PATH_TO_GENERATED_PACKAGE --save
```

### Documentation for API Endpoints

All URIs are relative to *http://localhost:8080*

Class | Method | HTTP request | Description
------------ | ------------- | ------------- | -------------
*AssetsApi* | [**uploadAsset**](docs/AssetsApi.md#uploadasset) | **POST** /api/teams/{slug}/assets | Create asset
*ConfigurationApi* | [**getConfig**](docs/ConfigurationApi.md#getconfig) | **GET** /api/config | Get application configuration
*PlacesApi* | [**createPlace**](docs/PlacesApi.md#createplace) | **POST** /api/teams/{slug}/places | Create place
*PlacesApi* | [**deletePlace**](docs/PlacesApi.md#deleteplace) | **DELETE** /api/teams/{slug}/places/{placeId} | Delete place
*PlacesApi* | [**getPlace**](docs/PlacesApi.md#getplace) | **GET** /api/teams/{slug}/places/{placeId} | Get place details
*PlacesApi* | [**listPlaces**](docs/PlacesApi.md#listplaces) | **GET** /api/teams/{slug}/places | List places
*PlacesApi* | [**updatePlace**](docs/PlacesApi.md#updateplace) | **PUT** /api/teams/{slug}/places/{placeId} | Update place
*PostCommentsApi* | [**createPostComment**](docs/PostCommentsApi.md#createpostcomment) | **POST** /api/teams/{slug}/posts/{entitySlug}/comments | Create post comment
*PostCommentsApi* | [**deletePostComment**](docs/PostCommentsApi.md#deletepostcomment) | **DELETE** /api/teams/{slug}/posts/{entitySlug}/comments/{commentId} | Delete post comment
*PostCommentsApi* | [**listPostComments**](docs/PostCommentsApi.md#listpostcomments) | **GET** /api/teams/{slug}/posts/{entitySlug}/comments | List post comments
*PostsApi* | [**createPost**](docs/PostsApi.md#createpost) | **POST** /api/teams/{slug}/posts | Create post
*PostsApi* | [**deletePost**](docs/PostsApi.md#deletepost) | **DELETE** /api/teams/{slug}/posts/{postSlug} | Delete post
*PostsApi* | [**getPost**](docs/PostsApi.md#getpost) | **GET** /api/teams/{slug}/posts/{postSlug} | Get post details
*PostsApi* | [**listPosts**](docs/PostsApi.md#listposts) | **GET** /api/teams/{slug}/posts | List posts
*PostsApi* | [**updatePost**](docs/PostsApi.md#updatepost) | **PUT** /api/teams/{slug}/posts/{postSlug} | Update post
*PublicationsApi* | [**listAllPublications**](docs/PublicationsApi.md#listallpublications) | **GET** /api/publications | List all publications
*PublicationsApi* | [**listPublications**](docs/PublicationsApi.md#listpublications) | **GET** /api/teams/{slug}/publications | List publications
*RideCommentsApi* | [**createRideComment**](docs/RideCommentsApi.md#createridecomment) | **POST** /api/teams/{slug}/rides/{entitySlug}/comments | Create ride comment
*RideCommentsApi* | [**deleteRideComment**](docs/RideCommentsApi.md#deleteridecomment) | **DELETE** /api/teams/{slug}/rides/{entitySlug}/comments/{commentId} | Delete ride comment
*RideCommentsApi* | [**listRideComments**](docs/RideCommentsApi.md#listridecomments) | **GET** /api/teams/{slug}/rides/{entitySlug}/comments | List ride comments
*RideTemplatesApi* | [**createTemplate**](docs/RideTemplatesApi.md#createtemplate) | **POST** /api/teams/{slug}/ride-templates | Create ride template
*RideTemplatesApi* | [**deleteTemplate**](docs/RideTemplatesApi.md#deletetemplate) | **DELETE** /api/teams/{slug}/ride-templates/{templateSlug} | Delete ride template
*RideTemplatesApi* | [**getTemplate**](docs/RideTemplatesApi.md#gettemplate) | **GET** /api/teams/{slug}/ride-templates/{templateSlug} | Get ride template
*RideTemplatesApi* | [**listTemplates**](docs/RideTemplatesApi.md#listtemplates) | **GET** /api/teams/{slug}/ride-templates | List ride templates
*RideTemplatesApi* | [**updateTemplate**](docs/RideTemplatesApi.md#updatetemplate) | **PUT** /api/teams/{slug}/ride-templates/{templateSlug} | Update ride template
*RidesApi* | [**createRide**](docs/RidesApi.md#createride) | **POST** /api/teams/{slug}/rides | Create ride
*RidesApi* | [**deleteRide**](docs/RidesApi.md#deleteride) | **DELETE** /api/teams/{slug}/rides/{rideSlug} | Delete ride
*RidesApi* | [**getRide**](docs/RidesApi.md#getride) | **GET** /api/teams/{slug}/rides/{rideSlug} | Get ride details
*RidesApi* | [**joinGroup**](docs/RidesApi.md#joingroup) | **POST** /api/teams/{slug}/rides/{rideSlug}/groups/{groupId}/join | Join ride group
*RidesApi* | [**leaveGroup**](docs/RidesApi.md#leavegroup) | **POST** /api/teams/{slug}/rides/{rideSlug}/groups/{groupId}/leave | Leave ride group
*RidesApi* | [**listRides**](docs/RidesApi.md#listrides) | **GET** /api/teams/{slug}/rides | List rides
*RidesApi* | [**updateRide**](docs/RidesApi.md#updateride) | **PUT** /api/teams/{slug}/rides/{rideSlug} | Update ride
*RouteCommentsApi* | [**createRouteComment**](docs/RouteCommentsApi.md#createroutecomment) | **POST** /api/teams/{slug}/routes/{entitySlug}/comments | Create route comment
*RouteCommentsApi* | [**deleteRouteComment**](docs/RouteCommentsApi.md#deleteroutecomment) | **DELETE** /api/teams/{slug}/routes/{entitySlug}/comments/{commentId} | Delete route comment
*RouteCommentsApi* | [**listRouteComments**](docs/RouteCommentsApi.md#listroutecomments) | **GET** /api/teams/{slug}/routes/{entitySlug}/comments | List route comments
*RouterApi* | [**route**](docs/RouterApi.md#route) | **POST** /api/router | Calculate route
*RoutesApi* | [**createRoute**](docs/RoutesApi.md#createroute) | **POST** /api/teams/{slug}/routes | Create route
*RoutesApi* | [**deleteRoute**](docs/RoutesApi.md#deleteroute) | **DELETE** /api/teams/{slug}/routes/{routeSlug} | Delete route
*RoutesApi* | [**getRoute**](docs/RoutesApi.md#getroute) | **GET** /api/teams/{slug}/routes/{routeSlug} | Get route details
*RoutesApi* | [**listRoutes**](docs/RoutesApi.md#listroutes) | **GET** /api/teams/{slug}/routes | List routes
*RoutesApi* | [**updateRoute**](docs/RoutesApi.md#updateroute) | **PUT** /api/teams/{slug}/routes/{routeSlug} | Update route
*TeamMembersApi* | [**addMember**](docs/TeamMembersApi.md#addmember) | **POST** /api/teams/{slug}/members | Add team member
*TeamMembersApi* | [**getMembers**](docs/TeamMembersApi.md#getmembers) | **GET** /api/teams/{slug}/members | Get team members
*TeamMembersApi* | [**joinTeam**](docs/TeamMembersApi.md#jointeam) | **POST** /api/teams/{slug}/members/join | Join team
*TeamMembersApi* | [**leaveTeam**](docs/TeamMembersApi.md#leaveteam) | **POST** /api/teams/{slug}/members/leave | Leave team
*TeamMembersApi* | [**removeMember**](docs/TeamMembersApi.md#removemember) | **DELETE** /api/teams/{slug}/members/{memberId} | Remove team member
*TeamMembersApi* | [**updateMemberRole**](docs/TeamMembersApi.md#updatememberrole) | **PUT** /api/teams/{slug}/members/{memberId} | Update member role
*TeamsApi* | [**createTeam**](docs/TeamsApi.md#createteam) | **POST** /api/teams | Create team
*TeamsApi* | [**deleteTeam**](docs/TeamsApi.md#deleteteam) | **DELETE** /api/teams/{slug} | Delete team
*TeamsApi* | [**getTeam**](docs/TeamsApi.md#getteam) | **GET** /api/teams/{slug} | Get team by slug
*TeamsApi* | [**listTeams**](docs/TeamsApi.md#listteams) | **GET** /api/teams | List public teams
*TeamsApi* | [**updateTeam**](docs/TeamsApi.md#updateteam) | **PUT** /api/teams/{slug} | Update team
*TripCommentsApi* | [**createTripComment**](docs/TripCommentsApi.md#createtripcomment) | **POST** /api/teams/{slug}/trips/{entitySlug}/comments | Create trip comment
*TripCommentsApi* | [**deleteTripComment**](docs/TripCommentsApi.md#deletetripcomment) | **DELETE** /api/teams/{slug}/trips/{entitySlug}/comments/{commentId} | Delete trip comment
*TripCommentsApi* | [**listTripComments**](docs/TripCommentsApi.md#listtripcomments) | **GET** /api/teams/{slug}/trips/{entitySlug}/comments | List trip comments
*TripsApi* | [**createTrip**](docs/TripsApi.md#createtrip) | **POST** /api/teams/{slug}/trips | Create trip
*TripsApi* | [**deleteTrip**](docs/TripsApi.md#deletetrip) | **DELETE** /api/teams/{slug}/trips/{tripSlug} | Delete trip
*TripsApi* | [**getTrip**](docs/TripsApi.md#gettrip) | **GET** /api/teams/{slug}/trips/{tripSlug} | Get trip details
*TripsApi* | [**joinTrip**](docs/TripsApi.md#jointrip) | **POST** /api/teams/{slug}/trips/{tripSlug}/join | Join trip
*TripsApi* | [**leaveTrip**](docs/TripsApi.md#leavetrip) | **POST** /api/teams/{slug}/trips/{tripSlug}/leave | Leave trip
*TripsApi* | [**listTrips**](docs/TripsApi.md#listtrips) | **GET** /api/teams/{slug}/trips | List trips
*TripsApi* | [**updateTrip**](docs/TripsApi.md#updatetrip) | **PUT** /api/teams/{slug}/trips/{tripSlug} | Update trip
*UsersApi* | [**deleteAvatar**](docs/UsersApi.md#deleteavatar) | **DELETE** /api/users/me/avatar | Delete user avatar
*UsersApi* | [**deleteCurrentUser**](docs/UsersApi.md#deletecurrentuser) | **DELETE** /api/users/me | Delete current user
*UsersApi* | [**getCurrentUser**](docs/UsersApi.md#getcurrentuser) | **GET** /api/users/me | Get current user
*UsersApi* | [**getUserById**](docs/UsersApi.md#getuserbyid) | **GET** /api/users/{id} | Get user by ID
*UsersApi* | [**searchUsers**](docs/UsersApi.md#searchusers) | **GET** /api/users/search | Search users
*UsersApi* | [**updateCurrentUser**](docs/UsersApi.md#updatecurrentuser) | **PUT** /api/users/me | Update current user
*UsersApi* | [**uploadAvatar**](docs/UsersApi.md#uploadavatar) | **POST** /api/users/me/avatar | Upload user avatar


### Documentation For Models

 - [AddMemberRequest](docs/AddMemberRequest.md)
 - [AssetDimensionsDto](docs/AssetDimensionsDto.md)
 - [AssetDto](docs/AssetDto.md)
 - [AssetsDto](docs/AssetsDto.md)
 - [ClimbCategory](docs/ClimbCategory.md)
 - [ClimbDto](docs/ClimbDto.md)
 - [CommentDto](docs/CommentDto.md)
 - [CommentListResponse](docs/CommentListResponse.md)
 - [CommentRequest](docs/CommentRequest.md)
 - [ConfigDto](docs/ConfigDto.md)
 - [ErrorResponse](docs/ErrorResponse.md)
 - [FieldError](docs/FieldError.md)
 - [GeoJsonLineString](docs/GeoJsonLineString.md)
 - [GeoJsonPoint](docs/GeoJsonPoint.md)
 - [GeoPoint](docs/GeoPoint.md)
 - [GroupRequest](docs/GroupRequest.md)
 - [KeycloakConfig](docs/KeycloakConfig.md)
 - [MapConfig](docs/MapConfig.md)
 - [MediaDto](docs/MediaDto.md)
 - [MemberDto](docs/MemberDto.md)
 - [MemberListResponse](docs/MemberListResponse.md)
 - [PlaceDetailDto](docs/PlaceDetailDto.md)
 - [PlaceListResponse](docs/PlaceListResponse.md)
 - [PlaceRequest](docs/PlaceRequest.md)
 - [PostDto](docs/PostDto.md)
 - [PostListResponse](docs/PostListResponse.md)
 - [PostRequest](docs/PostRequest.md)
 - [PublicUserDto](docs/PublicUserDto.md)
 - [PublicationDto](docs/PublicationDto.md)
 - [PublicationListResponse](docs/PublicationListResponse.md)
 - [PublicationType](docs/PublicationType.md)
 - [RideDto](docs/RideDto.md)
 - [RideGroupDto](docs/RideGroupDto.md)
 - [RideListResponse](docs/RideListResponse.md)
 - [RideParticipationDto](docs/RideParticipationDto.md)
 - [RideRequest](docs/RideRequest.md)
 - [RideTemplateDto](docs/RideTemplateDto.md)
 - [RideTemplateGroupDto](docs/RideTemplateGroupDto.md)
 - [RideTemplateGroupRequest](docs/RideTemplateGroupRequest.md)
 - [RideTemplateListResponse](docs/RideTemplateListResponse.md)
 - [RideTemplateRequest](docs/RideTemplateRequest.md)
 - [RouteDetailDto](docs/RouteDetailDto.md)
 - [RouteDto](docs/RouteDto.md)
 - [RouteListResponse](docs/RouteListResponse.md)
 - [RouteRequest](docs/RouteRequest.md)
 - [RouterRequest](docs/RouterRequest.md)
 - [RouterResponse](docs/RouterResponse.md)
 - [StageRequest](docs/StageRequest.md)
 - [Status](docs/Status.md)
 - [SurfaceType](docs/SurfaceType.md)
 - [TeamDetailDto](docs/TeamDetailDto.md)
 - [TeamListResponse](docs/TeamListResponse.md)
 - [TeamPublicationDto](docs/TeamPublicationDto.md)
 - [TeamRequest](docs/TeamRequest.md)
 - [TeamRole](docs/TeamRole.md)
 - [TrackDto](docs/TrackDto.md)
 - [TripDto](docs/TripDto.md)
 - [TripListResponse](docs/TripListResponse.md)
 - [TripParticipationDto](docs/TripParticipationDto.md)
 - [TripRequest](docs/TripRequest.md)
 - [TripStageDto](docs/TripStageDto.md)
 - [UpdateMemberRoleRequest](docs/UpdateMemberRoleRequest.md)
 - [UpdateUserRequest](docs/UpdateUserRequest.md)
 - [UserDto](docs/UserDto.md)
 - [Visibility](docs/Visibility.md)
 - [WaypointDto](docs/WaypointDto.md)


<a id="documentation-for-authorization"></a>
## Documentation For Authorization


Authentication schemes defined for the API:
<a id="SecurityScheme"></a>
### SecurityScheme


