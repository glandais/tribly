# Data Model: Cycling Team Management Platform

**Branch**: `001-cycling-team-platform` | **Date**: 2025-12-10
**Purpose**: Define all entities, relationships, and database schema

## Entity Relationship Overview

```
┌─────────────┐       ┌─────────────┐       ┌─────────────┐
│    User     │◄─────►│  UserTeam   │◄─────►│    Team     │
└─────────────┘       └─────────────┘       └─────────────┘
      │                                            │
      │ creates/participates                       │ owns
      ▼                                            ▼
┌─────────────┐       ┌─────────────┐       ┌─────────────┐
│    Ride     │◄─────►│ RideGroup   │       │   Route     │
└─────────────┘       └─────────────┘       └─────────────┘
      │                     │                      │
      │                     │ participants         │ has
      │                     ▼                      ▼
      │               ┌─────────────┐       ┌─────────────┐
      │               │RideParticip.│       │  GpxTrack   │
      │               └─────────────┘       └─────────────┘
      │
      │ part of
      ▼
┌─────────────┐       ┌─────────────┐       ┌─────────────┐
│    Trip     │◄─────►│  TripDay    │◄─────►│TripDayRide  │
└─────────────┘       └─────────────┘       └─────────────┘
                                                   │
                                                   │ uses
                                                   ▼
                                            ┌─────────────┐
                                            │    Place    │
                                            └─────────────┘

┌─────────────┐       ┌─────────────┐       ┌─────────────┐
│  Message    │       │Notification │       │  TeamDomain │
└─────────────┘       └─────────────┘       └─────────────┘
```

## Base Entity

All entities extend this base class:

```java
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @Version
    private Long version;
}
```

## Core Entities

### User

Represents a platform user who can belong to multiple teams.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | Long | PK, auto | Unique identifier |
| email | String | unique, not null | Login email |
| displayName | String | not null | Shown in UI |
| avatarUrl | String | nullable | Profile picture URL |
| stravaId | String | unique, nullable | Strava athlete ID |
| garminId | String | unique, nullable | Garmin Connect ID |
| googleId | String | unique, nullable | Google OAuth ID |
| facebookId | String | unique, nullable | Facebook OAuth ID |
| locale | String | default 'en' | Preferred language |
| timezone | String | default 'UTC' | Preferred timezone |
| lastLoginAt | Instant | nullable | Last successful login |

**Relationships**:
- `teams`: Many-to-Many via `UserTeam` (with role)
- `createdRides`: One-to-Many with `Ride`
- `rideParticipations`: One-to-Many with `RideParticipation`
- `notifications`: One-to-Many with `Notification`

### Team

Represents a cycling team/club with members and content.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | Long | PK, auto | Unique identifier |
| name | String | not null | Team display name |
| slug | String | unique, not null | URL-safe identifier |
| description | String | nullable | Team description |
| logoUrl | String | nullable | Team logo URL |
| coverImageUrl | String | nullable | Team cover image |
| isPublic | Boolean | default false | Visible in public catalog |
| settings | JSONB | default '{}' | Team-specific settings |
| maxMembers | Integer | nullable | Member limit (null = unlimited) |

**Relationships**:
- `members`: Many-to-Many via `UserTeam`
- `domains`: One-to-Many with `TeamDomain`
- `rides`: One-to-Many with `Ride`
- `trips`: One-to-Many with `Trip`
- `routes`: One-to-Many with `Route`
- `places`: One-to-Many with `Place`
- `messageThreads`: One-to-Many with `MessageThread`

### UserTeam (Join Table)

Associates users with teams including their role.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | Long | PK, auto | Unique identifier |
| userId | Long | FK, not null | Reference to User |
| teamId | Long | FK, not null | Reference to Team |
| role | Enum | not null | ADMIN, ORGANIZER, MEMBER |
| joinedAt | Instant | not null | When user joined |
| invitedBy | Long | FK, nullable | User who sent invite |

**Unique Constraint**: `(userId, teamId)`

**Roles**:
- `ADMIN`: Full team management (settings, members, billing)
- `ORGANIZER`: Can create/edit rides, trips, routes
- `MEMBER`: Can view and participate

### TeamDomain

Custom domains for team multi-tenancy.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | Long | PK, auto | Unique identifier |
| teamId | Long | FK, not null | Reference to Team |
| domain | String | unique, not null | Full domain name |
| verified | Boolean | default false | DNS verification status |
| verificationToken | String | nullable | DNS TXT record token |
| isPrimary | Boolean | default false | Primary domain for team |

## Ride Entities

### Ride

A scheduled cycling event.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | Long | PK, auto | Unique identifier |
| teamId | Long | FK, not null | Owning team |
| createdById | Long | FK, not null | User who created |
| title | String | not null | Ride name |
| description | String | nullable | Ride details |
| date | LocalDate | not null | Ride date |
| startTime | LocalTime | nullable | Departure time |
| routeId | Long | FK, nullable | Associated route |
| meetingPointId | Long | FK, nullable | Starting place |
| status | Enum | default DRAFT | DRAFT, PUBLISHED, CANCELLED |
| visibility | Enum | default TEAM | TEAM, PUBLIC |
| recurrenceRule | String | nullable | iCal RRULE for recurring |
| parentRideId | Long | FK, nullable | Parent for recurring instances |

**Relationships**:
- `team`: Many-to-One with Team
- `createdBy`: Many-to-One with User
- `route`: Many-to-One with Route (optional)
- `meetingPoint`: Many-to-One with Place (optional)
- `groups`: One-to-Many with RideGroup
- `trip`: Many-to-One with Trip (optional, when part of trip)

### RideGroup

A subgroup within a ride with specific pace/route.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | Long | PK, auto | Unique identifier |
| rideId | Long | FK, not null | Parent ride |
| name | String | not null | Group name (e.g., "Fast", "Chill") |
| description | String | nullable | Group details |
| routeId | Long | FK, nullable | Group-specific route |
| averageSpeed | Integer | nullable | Target speed km/h |
| maxParticipants | Integer | nullable | Capacity limit (optional per clarification) |
| sortOrder | Integer | default 0 | Display ordering |

**Relationships**:
- `ride`: Many-to-One with Ride
- `route`: Many-to-One with Route (optional)
- `participations`: One-to-Many with RideParticipation

### RideParticipation

User registration for a ride group.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | Long | PK, auto | Unique identifier |
| rideGroupId | Long | FK, not null | Target group |
| userId | Long | FK, not null | Participating user |
| status | Enum | not null | REGISTERED, CONFIRMED, CANCELLED, COMPLETED |
| registeredAt | Instant | not null | Registration time |
| notes | String | nullable | Participant notes |

**Unique Constraint**: `(rideGroupId, userId)`

## Trip Entities

### Trip

A multi-day cycling trip/tour.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | Long | PK, auto | Unique identifier |
| teamId | Long | FK, not null | Owning team |
| createdById | Long | FK, not null | User who created |
| title | String | not null | Trip name |
| description | String | nullable | Trip overview |
| startDate | LocalDate | not null | First day |
| endDate | LocalDate | not null | Last day |
| status | Enum | default PLANNING | PLANNING, PUBLISHED, IN_PROGRESS, COMPLETED, CANCELLED |
| visibility | Enum | default TEAM | TEAM, PUBLIC |
| coverImageUrl | String | nullable | Trip cover image |
| maxParticipants | Integer | nullable | Trip capacity |

**Relationships**:
- `team`: Many-to-One with Team
- `createdBy`: Many-to-One with User
- `days`: One-to-Many with TripDay (ordered by date)
- `participants`: Many-to-Many via TripParticipation

### TripDay

A single day within a trip.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | Long | PK, auto | Unique identifier |
| tripId | Long | FK, not null | Parent trip |
| date | LocalDate | not null | Calendar date |
| title | String | nullable | Day title (e.g., "Mountain Stage") |
| description | String | nullable | Day details |
| accommodationId | Long | FK, nullable | Where to sleep |
| sortOrder | Integer | not null | Day sequence |

**Relationships**:
- `trip`: Many-to-One with Trip
- `rides`: One-to-Many with TripDayRide
- `accommodation`: Many-to-One with Place (optional)

### TripDayRide

Links rides to trip days.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | Long | PK, auto | Unique identifier |
| tripDayId | Long | FK, not null | Parent trip day |
| rideId | Long | FK, not null | Associated ride |
| sortOrder | Integer | default 0 | Ride sequence within day |

### TripParticipation

User participation in a trip.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | Long | PK, auto | Unique identifier |
| tripId | Long | FK, not null | Target trip |
| userId | Long | FK, not null | Participating user |
| status | Enum | not null | INTERESTED, REGISTERED, CONFIRMED, CANCELLED |
| registeredAt | Instant | not null | Registration time |

**Unique Constraint**: `(tripId, userId)`

## Route Entities

### Route

A cycling route with GPX data.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | Long | PK, auto | Unique identifier |
| teamId | Long | FK, not null | Owning team (strict ownership per clarification) |
| createdById | Long | FK, not null | User who created |
| name | String | not null | Route name |
| description | String | nullable | Route details |
| distance | Integer | nullable | Distance in meters |
| elevationGain | Integer | nullable | Elevation gain in meters |
| elevationLoss | Integer | nullable | Elevation loss in meters |
| difficulty | Enum | nullable | EASY, MODERATE, HARD, EXPERT |
| surfaceType | Enum | nullable | ROAD, GRAVEL, MTB, MIXED |
| isPublic | Boolean | default false | Visible in public catalog |
| thumbnailUrl | String | nullable | Route preview image |
| startLat | Decimal | nullable | Start point latitude |
| startLng | Decimal | nullable | Start point longitude |
| endLat | Decimal | nullable | End point latitude |
| endLng | Decimal | nullable | End point longitude |

**Relationships**:
- `team`: Many-to-One with Team
- `createdBy`: Many-to-One with User
- `gpxTracks`: One-to-Many with GpxTrack
- `climbs`: One-to-Many with RouteClimb
- `pointsOfInterest`: One-to-Many with RoutePointOfInterest

### GpxTrack

Parsed GPX track data.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | Long | PK, auto | Unique identifier |
| routeId | Long | FK, not null | Parent route |
| name | String | nullable | Track name from GPX |
| geometry | Geometry | not null | PostGIS LineString |
| originalFileName | String | nullable | Source GPX filename |
| trackPoints | JSONB | not null | Array of {lat, lng, ele, time} |
| processedAt | Instant | not null | When parsed |

### RouteClimb

Detected climb segments within a route.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | Long | PK, auto | Unique identifier |
| routeId | Long | FK, not null | Parent route |
| name | String | nullable | Climb name (if known) |
| startDistance | Integer | not null | Start position in meters |
| endDistance | Integer | not null | End position in meters |
| elevationGain | Integer | not null | Climb elevation in meters |
| averageGradient | Decimal | not null | Average gradient % |
| maxGradient | Decimal | not null | Maximum gradient % |
| category | Enum | nullable | HC, CAT1, CAT2, CAT3, CAT4 |

### RoutePointOfInterest

Points of interest along a route.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | Long | PK, auto | Unique identifier |
| routeId | Long | FK, not null | Parent route |
| placeId | Long | FK, nullable | Linked place |
| name | String | not null | POI name |
| type | Enum | not null | CAFE, WATER, VIEWPOINT, DANGER, INFO |
| distance | Integer | not null | Position in meters from start |
| lat | Decimal | not null | Latitude |
| lng | Decimal | not null | Longitude |
| notes | String | nullable | Additional info |

## Place Entities

### Place

A saved location (cafe, meetup point, accommodation).

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | Long | PK, auto | Unique identifier |
| teamId | Long | FK, not null | Owning team |
| createdById | Long | FK, not null | User who created |
| name | String | not null | Place name |
| type | Enum | not null | MEETUP, CAFE, RESTAURANT, HOTEL, CAMPSITE, WATER, OTHER |
| address | String | nullable | Street address |
| lat | Decimal | not null | Latitude |
| lng | Decimal | not null | Longitude |
| phone | String | nullable | Contact phone |
| website | String | nullable | Website URL |
| notes | String | nullable | Additional info |
| isPublic | Boolean | default false | Visible in public catalog |

## Messaging Entities

### MessageThread

A conversation thread (team-wide, ride-specific, or direct).

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | Long | PK, auto | Unique identifier |
| teamId | Long | FK, not null | Owning team |
| type | Enum | not null | TEAM, RIDE, TRIP, DIRECT |
| rideId | Long | FK, nullable | Associated ride (if RIDE type) |
| tripId | Long | FK, nullable | Associated trip (if TRIP type) |
| title | String | nullable | Thread title |
| lastMessageAt | Instant | nullable | For sorting |

### Message

A single message within a thread.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | Long | PK, auto | Unique identifier |
| threadId | Long | FK, not null | Parent thread |
| authorId | Long | FK, not null | Message author |
| content | String | not null | Message text |
| parentId | Long | FK, nullable | Reply-to message |
| editedAt | Instant | nullable | Last edit time |

### MessageThreadParticipant

Users in a message thread (for direct messages).

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | Long | PK, auto | Unique identifier |
| threadId | Long | FK, not null | Thread |
| userId | Long | FK, not null | Participant |
| lastReadAt | Instant | nullable | Last read timestamp |
| mutedUntil | Instant | nullable | Notification mute |

## Notification Entities

### Notification

User notifications.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | Long | PK, auto | Unique identifier |
| userId | Long | FK, not null | Recipient |
| type | Enum | not null | RIDE_CREATED, RIDE_UPDATED, MESSAGE, TRIP_INVITE, etc. |
| title | String | not null | Notification title |
| body | String | nullable | Notification body |
| data | JSONB | default '{}' | Type-specific payload |
| readAt | Instant | nullable | When read |
| sentAt | Instant | not null | When created |
| channel | Enum | not null | IN_APP, EMAIL, PUSH |

## Database Schema

### Migration: V1__initial_schema.sql

```sql
-- Enable PostGIS
CREATE EXTENSION IF NOT EXISTS postgis;

-- Users
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    display_name VARCHAR(255) NOT NULL,
    avatar_url VARCHAR(500),
    strava_id VARCHAR(50) UNIQUE,
    garmin_id VARCHAR(50) UNIQUE,
    google_id VARCHAR(100) UNIQUE,
    facebook_id VARCHAR(100) UNIQUE,
    locale VARCHAR(10) DEFAULT 'en',
    timezone VARCHAR(50) DEFAULT 'UTC',
    last_login_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT DEFAULT 0
);

-- Teams
CREATE TABLE teams (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    logo_url VARCHAR(500),
    cover_image_url VARCHAR(500),
    is_public BOOLEAN NOT NULL DEFAULT FALSE,
    settings JSONB DEFAULT '{}',
    max_members INTEGER,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT DEFAULT 0
);

-- User-Team association
CREATE TABLE user_teams (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    team_id BIGINT NOT NULL REFERENCES teams(id),
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'ORGANIZER', 'MEMBER')),
    joined_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    invited_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    UNIQUE(user_id, team_id)
);

-- Team domains for multi-tenancy
CREATE TABLE team_domains (
    id BIGSERIAL PRIMARY KEY,
    team_id BIGINT NOT NULL REFERENCES teams(id),
    domain VARCHAR(255) NOT NULL UNIQUE,
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    verification_token VARCHAR(100),
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT DEFAULT 0
);

-- Places
CREATE TABLE places (
    id BIGSERIAL PRIMARY KEY,
    team_id BIGINT NOT NULL REFERENCES teams(id),
    created_by_id BIGINT NOT NULL REFERENCES users(id),
    name VARCHAR(255) NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('MEETUP', 'CAFE', 'RESTAURANT', 'HOTEL', 'CAMPSITE', 'WATER', 'OTHER')),
    address TEXT,
    lat DECIMAL(10, 8) NOT NULL,
    lng DECIMAL(11, 8) NOT NULL,
    phone VARCHAR(50),
    website VARCHAR(500),
    notes TEXT,
    is_public BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT DEFAULT 0
);

-- Routes
CREATE TABLE routes (
    id BIGSERIAL PRIMARY KEY,
    team_id BIGINT NOT NULL REFERENCES teams(id),
    created_by_id BIGINT NOT NULL REFERENCES users(id),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    distance INTEGER,
    elevation_gain INTEGER,
    elevation_loss INTEGER,
    difficulty VARCHAR(20) CHECK (difficulty IN ('EASY', 'MODERATE', 'HARD', 'EXPERT')),
    surface_type VARCHAR(20) CHECK (surface_type IN ('ROAD', 'GRAVEL', 'MTB', 'MIXED')),
    is_public BOOLEAN NOT NULL DEFAULT FALSE,
    thumbnail_url VARCHAR(500),
    start_lat DECIMAL(10, 8),
    start_lng DECIMAL(11, 8),
    end_lat DECIMAL(10, 8),
    end_lng DECIMAL(11, 8),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT DEFAULT 0
);

-- GPX tracks
CREATE TABLE gpx_tracks (
    id BIGSERIAL PRIMARY KEY,
    route_id BIGINT NOT NULL REFERENCES routes(id),
    name VARCHAR(255),
    geometry GEOMETRY(LineString, 4326) NOT NULL,
    original_file_name VARCHAR(255),
    track_points JSONB NOT NULL,
    processed_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT DEFAULT 0
);

-- Route climbs
CREATE TABLE route_climbs (
    id BIGSERIAL PRIMARY KEY,
    route_id BIGINT NOT NULL REFERENCES routes(id),
    name VARCHAR(255),
    start_distance INTEGER NOT NULL,
    end_distance INTEGER NOT NULL,
    elevation_gain INTEGER NOT NULL,
    average_gradient DECIMAL(5, 2) NOT NULL,
    max_gradient DECIMAL(5, 2) NOT NULL,
    category VARCHAR(10) CHECK (category IN ('HC', 'CAT1', 'CAT2', 'CAT3', 'CAT4')),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT DEFAULT 0
);

-- Route POIs
CREATE TABLE route_points_of_interest (
    id BIGSERIAL PRIMARY KEY,
    route_id BIGINT NOT NULL REFERENCES routes(id),
    place_id BIGINT REFERENCES places(id),
    name VARCHAR(255) NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('CAFE', 'WATER', 'VIEWPOINT', 'DANGER', 'INFO')),
    distance INTEGER NOT NULL,
    lat DECIMAL(10, 8) NOT NULL,
    lng DECIMAL(11, 8) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT DEFAULT 0
);

-- Rides
CREATE TABLE rides (
    id BIGSERIAL PRIMARY KEY,
    team_id BIGINT NOT NULL REFERENCES teams(id),
    created_by_id BIGINT NOT NULL REFERENCES users(id),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    date DATE NOT NULL,
    start_time TIME,
    route_id BIGINT REFERENCES routes(id),
    meeting_point_id BIGINT REFERENCES places(id),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'PUBLISHED', 'CANCELLED')),
    visibility VARCHAR(20) NOT NULL DEFAULT 'TEAM' CHECK (visibility IN ('TEAM', 'PUBLIC')),
    recurrence_rule VARCHAR(255),
    parent_ride_id BIGINT REFERENCES rides(id),
    trip_id BIGINT,  -- FK added after trips table
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT DEFAULT 0
);

-- Ride groups
CREATE TABLE ride_groups (
    id BIGSERIAL PRIMARY KEY,
    ride_id BIGINT NOT NULL REFERENCES rides(id),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    route_id BIGINT REFERENCES routes(id),
    average_speed INTEGER,
    max_participants INTEGER,
    sort_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT DEFAULT 0
);

-- Ride participations
CREATE TABLE ride_participations (
    id BIGSERIAL PRIMARY KEY,
    ride_group_id BIGINT NOT NULL REFERENCES ride_groups(id),
    user_id BIGINT NOT NULL REFERENCES users(id),
    status VARCHAR(20) NOT NULL CHECK (status IN ('REGISTERED', 'CONFIRMED', 'CANCELLED', 'COMPLETED')),
    registered_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    UNIQUE(ride_group_id, user_id)
);

-- Trips
CREATE TABLE trips (
    id BIGSERIAL PRIMARY KEY,
    team_id BIGINT NOT NULL REFERENCES teams(id),
    created_by_id BIGINT NOT NULL REFERENCES users(id),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PLANNING' CHECK (status IN ('PLANNING', 'PUBLISHED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
    visibility VARCHAR(20) NOT NULL DEFAULT 'TEAM' CHECK (visibility IN ('TEAM', 'PUBLIC')),
    cover_image_url VARCHAR(500),
    max_participants INTEGER,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT DEFAULT 0
);

-- Add FK from rides to trips
ALTER TABLE rides ADD CONSTRAINT fk_rides_trip FOREIGN KEY (trip_id) REFERENCES trips(id);

-- Trip days
CREATE TABLE trip_days (
    id BIGSERIAL PRIMARY KEY,
    trip_id BIGINT NOT NULL REFERENCES trips(id),
    date DATE NOT NULL,
    title VARCHAR(255),
    description TEXT,
    accommodation_id BIGINT REFERENCES places(id),
    sort_order INTEGER NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT DEFAULT 0
);

-- Trip day rides
CREATE TABLE trip_day_rides (
    id BIGSERIAL PRIMARY KEY,
    trip_day_id BIGINT NOT NULL REFERENCES trip_days(id),
    ride_id BIGINT NOT NULL REFERENCES rides(id),
    sort_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT DEFAULT 0
);

-- Trip participations
CREATE TABLE trip_participations (
    id BIGSERIAL PRIMARY KEY,
    trip_id BIGINT NOT NULL REFERENCES trips(id),
    user_id BIGINT NOT NULL REFERENCES users(id),
    status VARCHAR(20) NOT NULL CHECK (status IN ('INTERESTED', 'REGISTERED', 'CONFIRMED', 'CANCELLED')),
    registered_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    UNIQUE(trip_id, user_id)
);

-- Message threads
CREATE TABLE message_threads (
    id BIGSERIAL PRIMARY KEY,
    team_id BIGINT NOT NULL REFERENCES teams(id),
    type VARCHAR(20) NOT NULL CHECK (type IN ('TEAM', 'RIDE', 'TRIP', 'DIRECT')),
    ride_id BIGINT REFERENCES rides(id),
    trip_id BIGINT REFERENCES trips(id),
    title VARCHAR(255),
    last_message_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT DEFAULT 0
);

-- Messages
CREATE TABLE messages (
    id BIGSERIAL PRIMARY KEY,
    thread_id BIGINT NOT NULL REFERENCES message_threads(id),
    author_id BIGINT NOT NULL REFERENCES users(id),
    content TEXT NOT NULL,
    parent_id BIGINT REFERENCES messages(id),
    edited_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT DEFAULT 0
);

-- Message thread participants
CREATE TABLE message_thread_participants (
    id BIGSERIAL PRIMARY KEY,
    thread_id BIGINT NOT NULL REFERENCES message_threads(id),
    user_id BIGINT NOT NULL REFERENCES users(id),
    last_read_at TIMESTAMP WITH TIME ZONE,
    muted_until TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    UNIQUE(thread_id, user_id)
);

-- Notifications
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    body TEXT,
    data JSONB DEFAULT '{}',
    read_at TIMESTAMP WITH TIME ZONE,
    sent_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    channel VARCHAR(20) NOT NULL CHECK (channel IN ('IN_APP', 'EMAIL', 'PUSH')),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT DEFAULT 0
);

-- Indexes
CREATE INDEX idx_users_email ON users(email) WHERE deleted = FALSE;
CREATE INDEX idx_users_strava ON users(strava_id) WHERE deleted = FALSE;
CREATE INDEX idx_teams_slug ON teams(slug) WHERE deleted = FALSE;
CREATE INDEX idx_teams_public ON teams(is_public) WHERE deleted = FALSE AND is_public = TRUE;
CREATE INDEX idx_user_teams_user ON user_teams(user_id) WHERE deleted = FALSE;
CREATE INDEX idx_user_teams_team ON user_teams(team_id) WHERE deleted = FALSE;
CREATE INDEX idx_team_domains_domain ON team_domains(domain) WHERE deleted = FALSE;
CREATE INDEX idx_routes_team ON routes(team_id) WHERE deleted = FALSE;
CREATE INDEX idx_rides_team_date ON rides(team_id, date) WHERE deleted = FALSE;
CREATE INDEX idx_rides_status ON rides(team_id, status) WHERE deleted = FALSE;
CREATE INDEX idx_trips_team ON trips(team_id) WHERE deleted = FALSE;
CREATE INDEX idx_notifications_user ON notifications(user_id, read_at) WHERE deleted = FALSE;
CREATE INDEX idx_gpx_tracks_geometry ON gpx_tracks USING GIST(geometry);
CREATE INDEX idx_places_location ON places USING GIST(ST_SetSRID(ST_MakePoint(lng, lat), 4326));
```

## Enum Definitions

```java
public enum TeamRole {
    ADMIN, ORGANIZER, MEMBER
}

public enum RideStatus {
    DRAFT, PUBLISHED, CANCELLED
}

public enum TripStatus {
    PLANNING, PUBLISHED, IN_PROGRESS, COMPLETED, CANCELLED
}

public enum Visibility {
    TEAM, PUBLIC
}

public enum ParticipationStatus {
    INTERESTED, REGISTERED, CONFIRMED, CANCELLED, COMPLETED
}

public enum PlaceType {
    MEETUP, CAFE, RESTAURANT, HOTEL, CAMPSITE, WATER, OTHER
}

public enum RouteDifficulty {
    EASY, MODERATE, HARD, EXPERT
}

public enum SurfaceType {
    ROAD, GRAVEL, MTB, MIXED
}

public enum ClimbCategory {
    HC, CAT1, CAT2, CAT3, CAT4
}

public enum PoiType {
    CAFE, WATER, VIEWPOINT, DANGER, INFO
}

public enum MessageThreadType {
    TEAM, RIDE, TRIP, DIRECT
}

public enum NotificationChannel {
    IN_APP, EMAIL, PUSH
}
```
