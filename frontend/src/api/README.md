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
*ConfigurationApi* | [**getConfig**](docs/ConfigurationApi.md#getconfig) | **GET** /api/config | Get application configuration
*RidesApi* | [**createGroup**](docs/RidesApi.md#creategroup) | **POST** /api/teams/{slug}/rides/{rideSlug}/groups | Create ride group
*RidesApi* | [**createRide**](docs/RidesApi.md#createride) | **POST** /api/teams/{slug}/rides | Create ride
*RidesApi* | [**deleteGroup**](docs/RidesApi.md#deletegroup) | **DELETE** /api/teams/{slug}/rides/{rideSlug}/groups/{groupId} | Delete ride group
*RidesApi* | [**deleteRide**](docs/RidesApi.md#deleteride) | **DELETE** /api/teams/{slug}/rides/{rideSlug} | Delete ride
*RidesApi* | [**getRide**](docs/RidesApi.md#getride) | **GET** /api/teams/{slug}/rides/{rideSlug} | Get ride details
*RidesApi* | [**joinGroup**](docs/RidesApi.md#joingroup) | **POST** /api/teams/{slug}/rides/{rideSlug}/groups/{groupId}/join | Join ride group
*RidesApi* | [**leaveGroup**](docs/RidesApi.md#leavegroup) | **POST** /api/teams/{slug}/rides/{rideSlug}/groups/{groupId}/leave | Leave ride group
*RidesApi* | [**listGroups**](docs/RidesApi.md#listgroups) | **GET** /api/teams/{slug}/rides/{rideSlug}/groups | List ride groups
*RidesApi* | [**listRides**](docs/RidesApi.md#listrides) | **GET** /api/teams/{slug}/rides | List rides
*RidesApi* | [**updateGroup**](docs/RidesApi.md#updategroup) | **PATCH** /api/teams/{slug}/rides/{rideSlug}/groups/{groupId} | Update ride group
*RidesApi* | [**updateRide**](docs/RidesApi.md#updateride) | **PATCH** /api/teams/{slug}/rides/{rideSlug} | Update ride
*RouteDownloadsApi* | [**downloadFit**](docs/RouteDownloadsApi.md#downloadfit) | **GET** /api/download/teams/{slug}/routes/{routeId}/fit | Download FIT file
*RouteDownloadsApi* | [**downloadGpx**](docs/RouteDownloadsApi.md#downloadgpx) | **GET** /api/download/teams/{slug}/routes/{routeId}/gpx | Download GPX file
*RouteDownloadsApi* | [**getThumbnail**](docs/RouteDownloadsApi.md#getthumbnail) | **GET** /api/download/teams/{slug}/routes/{routeId}/thumbnail | Get route thumbnail
*RoutesApi* | [**createRoute**](docs/RoutesApi.md#createroute) | **POST** /api/teams/{slug}/routes | Create route
*RoutesApi* | [**deleteRoute**](docs/RoutesApi.md#deleteroute) | **DELETE** /api/teams/{slug}/routes/{routeId} | Delete route
*RoutesApi* | [**getClimbs**](docs/RoutesApi.md#getclimbs) | **GET** /api/teams/{slug}/routes/{routeId}/climbs | Get route climbs
*RoutesApi* | [**getRoute**](docs/RoutesApi.md#getroute) | **GET** /api/teams/{slug}/routes/{routeId} | Get route details
*RoutesApi* | [**getTrack**](docs/RoutesApi.md#gettrack) | **GET** /api/teams/{slug}/routes/{routeId}/track | Get route track
*RoutesApi* | [**listRoutes**](docs/RoutesApi.md#listroutes) | **GET** /api/teams/{slug}/routes | List routes
*RoutesApi* | [**updateRoute**](docs/RoutesApi.md#updateroute) | **PATCH** /api/teams/{slug}/routes/{routeId} | Update route
*TeamMembersApi* | [**addMember**](docs/TeamMembersApi.md#addmember) | **POST** /api/teams/{slug}/members | Add team member
*TeamMembersApi* | [**getMembers**](docs/TeamMembersApi.md#getmembers) | **GET** /api/teams/{slug}/members | Get team members
*TeamMembersApi* | [**joinTeam**](docs/TeamMembersApi.md#jointeam) | **POST** /api/teams/{slug}/members/join | Join team
*TeamMembersApi* | [**leaveTeam**](docs/TeamMembersApi.md#leaveteam) | **POST** /api/teams/{slug}/members/leave | Leave team
*TeamMembersApi* | [**removeMember**](docs/TeamMembersApi.md#removemember) | **DELETE** /api/teams/{slug}/members/{memberId} | Remove team member
*TeamMembersApi* | [**updateMemberRole**](docs/TeamMembersApi.md#updatememberrole) | **PUT** /api/teams/{slug}/members/{memberId} | Update member role
*TeamsApi* | [**createTeam**](docs/TeamsApi.md#createteam) | **POST** /api/teams | Create team
*TeamsApi* | [**deleteTeam**](docs/TeamsApi.md#deleteteam) | **DELETE** /api/teams/{slug} | Delete team
*TeamsApi* | [**getMyTeams**](docs/TeamsApi.md#getmyteams) | **GET** /api/teams/my | Get my teams
*TeamsApi* | [**getTeam**](docs/TeamsApi.md#getteam) | **GET** /api/teams/{slug} | Get team by slug
*TeamsApi* | [**listTeams**](docs/TeamsApi.md#listteams) | **GET** /api/teams | List public teams
*TeamsApi* | [**updateTeam**](docs/TeamsApi.md#updateteam) | **PUT** /api/teams/{slug} | Update team
*UsersApi* | [**deleteCurrentUser**](docs/UsersApi.md#deletecurrentuser) | **DELETE** /api/users/me | Delete current user
*UsersApi* | [**getCurrentUser**](docs/UsersApi.md#getcurrentuser) | **GET** /api/users/me | Get current user
*UsersApi* | [**getUserById**](docs/UsersApi.md#getuserbyid) | **GET** /api/users/{id} | Get user by ID
*UsersApi* | [**searchUsers**](docs/UsersApi.md#searchusers) | **GET** /api/users/search | Search users
*UsersApi* | [**updateCurrentUser**](docs/UsersApi.md#updatecurrentuser) | **PUT** /api/users/me | Update current user


### Documentation For Models

 - [AddMemberRequest](docs/AddMemberRequest.md)
 - [ClimbListResponse](docs/ClimbListResponse.md)
 - [ConfigDto](docs/ConfigDto.md)
 - [CreateGroupRequest](docs/CreateGroupRequest.md)
 - [CreateRideRequest](docs/CreateRideRequest.md)
 - [CreateTeamRequest](docs/CreateTeamRequest.md)
 - [GpxTrackDto](docs/GpxTrackDto.md)
 - [JoinGroupRequest](docs/JoinGroupRequest.md)
 - [KeycloakConfig](docs/KeycloakConfig.md)
 - [MapConfig](docs/MapConfig.md)
 - [MemberDto](docs/MemberDto.md)
 - [MemberListResponse](docs/MemberListResponse.md)
 - [PublicUserDto](docs/PublicUserDto.md)
 - [RideDetailDto](docs/RideDetailDto.md)
 - [RideDto](docs/RideDto.md)
 - [RideGroupDto](docs/RideGroupDto.md)
 - [RideGroupListResponse](docs/RideGroupListResponse.md)
 - [RideListResponse](docs/RideListResponse.md)
 - [RideParticipationDto](docs/RideParticipationDto.md)
 - [RideStatus](docs/RideStatus.md)
 - [RouteClimbDto](docs/RouteClimbDto.md)
 - [RouteDetailDto](docs/RouteDetailDto.md)
 - [RouteDifficulty](docs/RouteDifficulty.md)
 - [RouteDto](docs/RouteDto.md)
 - [RouteListResponse](docs/RouteListResponse.md)
 - [SurfaceType](docs/SurfaceType.md)
 - [TeamDetailDto](docs/TeamDetailDto.md)
 - [TeamDto](docs/TeamDto.md)
 - [TeamListResponse](docs/TeamListResponse.md)
 - [TeamWithRoleDto](docs/TeamWithRoleDto.md)
 - [TrackPointDto](docs/TrackPointDto.md)
 - [UpdateGroupRequest](docs/UpdateGroupRequest.md)
 - [UpdateMemberRoleRequest](docs/UpdateMemberRoleRequest.md)
 - [UpdateRideRequest](docs/UpdateRideRequest.md)
 - [UpdateRouteRequest](docs/UpdateRouteRequest.md)
 - [UpdateTeamRequest](docs/UpdateTeamRequest.md)
 - [UpdateUserRequest](docs/UpdateUserRequest.md)
 - [UserDto](docs/UserDto.md)


<a id="documentation-for-authorization"></a>
## Documentation For Authorization


Authentication schemes defined for the API:
<a id="SecurityScheme"></a>
### SecurityScheme


