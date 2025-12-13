# tribly Development Guidelines

Auto-generated from all feature plans. Last updated: 2025-12-10

## Active Technologies

- Java 21 (backend), TypeScript 5.x (frontend) + Quarkus 3.x (backend), React 18+ with Vite (frontend), OpenAPI Generator (contract-first) (001-cycling-team-platform)

## Project Structure

```text
src/
tests/
```

## Commands

- Backend: Use `mvn` (not `./mvnw`) for Maven commands
- Frontend: `npm test && npm run lint`

## Code Style

Java 21 (backend), TypeScript 5.x (frontend): Follow standard conventions

## Recent Changes

- 001-cycling-team-platform: Added Java 21 (backend), TypeScript 5.x (frontend) + Quarkus 3.x (backend), React 18+ with Vite (frontend), OpenAPI Generator (contract-first)

<!-- MANUAL ADDITIONS START -->

## Session Learnings (2025-12-11)

### Quarkus/Hibernate Testing Issues

#### 1. RestAssured Follows Redirects by Default
- **Problem**: `AuthContractTest` expected 302 but got 400
- **Root Cause**: RestAssured followed the redirect to Strava (with invalid test client_id)
- **Fix**: Add `.redirects().follow(false)` to test OAuth redirect endpoints without actually calling the external provider
```java
given()
    .redirects().follow(false)
    .queryParam("redirect_uri", "...")
    .when()
    .get("/v1/auth/oauth/strava")
    .then()
    .statusCode(302);
```

#### 2. JWT Claims Don't Accept Null Values
- **Problem**: `NullPointerException` when generating JWT tokens
- **Root Cause**: `Jwt.claim("avatar", user.getAvatarUrl())` fails if avatarUrl is null
- **Fix**: Conditionally add claims only when values are non-null
```java
var builder = Jwt.issuer(issuer)...
if (user.getAvatarUrl() != null) {
    builder.claim("avatar", user.getAvatarUrl());
}
return builder.sign();
```

#### 3. Entity IDs Null After Persist with IDENTITY Strategy
- **Problem**: `user.getId()` returns null even after `persist()`
- **Root Cause**: With `GenerationType.IDENTITY`, IDs are only assigned after INSERT
- **Fix**: Use `persistAndFlush()` instead of `persist()` when you need the ID immediately
```java
userRepository.persistAndFlush(testUser);  // ID available after this
validToken = jwtService.generateToken(testUser);  // Now works
```

#### 4. Foreign Key Constraint Violations in Test Cleanup
- **Problem**: `ConstraintViolationException` when deleting users in `@BeforeEach`
- **Root Cause**: Users still referenced in `user_teams` table
- **Fix**: Delete dependent records first (correct FK cleanup order)
```java
@BeforeEach
@Transactional
void setUp() {
    userTeamRepository.delete("user.email like ?1", "team-test-%");  // First
    teamRepository.delete("slug like ?1", "test-%");                  // Second
    userRepository.delete("email like ?1", "team-test-%");           // Last
    // ... create new test data
}
```

#### 5. SecurityIdentityAugmentor Needs Request Context
- **Problem**: `ContextNotActiveException` - no transaction/request context active
- **Root Cause**: `runBlocking()` lambda runs outside CDI request context
- **Fix**: Extract to separate method with `@ActivateRequestContext`
```java
@Override
public Uni<SecurityIdentity> augment(SecurityIdentity identity, AuthenticationRequestContext context) {
    return context.runBlocking(() -> augmentIdentity(identity));
}

@ActivateRequestContext
SecurityIdentity augmentIdentity(SecurityIdentity identity) {
    // Database access works here
}
```

#### 6. @Transactional + RestAssured HTTP = Transaction Isolation Issue
- **Problem**: HTTP requests can't see data created in the same test method
- **Root Cause**: `@Transactional` on test holds transaction open; RestAssured runs in separate thread with its own transaction that can't see uncommitted data
- **Fix**: Don't mix service calls and HTTP requests in `@Transactional` tests. Choose one pattern:
  - **Pure service tests**: Use `@Transactional`, only call injected services, use JUnit assertions
  - **Pure HTTP tests**: No `@Transactional`, use RestAssured for all setup and assertions
```java
// ❌ WRONG: Mixed pattern - HTTP can't see uncommitted team
@Test
@Transactional
void joinTeam_wrong() {
    Team team = teamService.createTeam(...);  // Not committed yet
    given().post("/v1/teams/" + team.getSlug() + "/join");  // Can't find team!
}

// ✅ RIGHT: Pure HTTP pattern - each request commits
@Test
void joinTeam_correct() {
    String slug = given()
        .body("{\"name\": \"Team\"}")
        .post("/v1/teams")
        .extract().path("slug");  // Committed!
    given().post("/v1/teams/" + slug + "/join");  // Works!
}

// ✅ RIGHT: Pure service pattern - same transaction
@Test
@Transactional
void createTeam_correct() {
    Team team = teamService.createTeam(...);
    assertEquals("Team", team.getName());  // Same transaction, works!
}
```

### Quarkus Configuration Notes

#### Test Profile Database Configuration
- Use `%test.` prefix for test-specific configuration
- DevServices requires removing default JDBC URL (use profile-specific URLs for prod/dev only)
- For PostGIS, use image: `postgis/postgis:16-3.4-alpine`

```properties
# Production/Dev only - tests use DevServices
%prod.quarkus.datasource.jdbc.url=jdbc:postgresql://...
%dev.quarkus.datasource.jdbc.url=jdbc:postgresql://...

# Test profile - DevServices auto-starts container
%test.quarkus.datasource.devservices.enabled=true
%test.quarkus.datasource.devservices.image-name=postgis/postgis:16-3.4-alpine
```

#### JWT Test Keys
- Generate test keys in `src/test/resources/keys/`
- Configure test paths without leading slash:
```properties
%test.mp.jwt.verify.publickey.location=keys/public.pem
%test.smallrye.jwt.sign.key.location=keys/private.pem
```

### Frontend/Vite Notes

#### Environment Variables
- Use `import.meta.env.DEV` instead of `process.env.NODE_ENV` for Vite projects
- Use `globalThis` instead of `global` for browser compatibility

<!-- MANUAL ADDITIONS END -->
