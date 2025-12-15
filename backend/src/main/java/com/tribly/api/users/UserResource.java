package com.tribly.api.users;

import com.tribly.api.AbstractAuthenticatedResource;
import com.tribly.domain.user.User;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.infrastructure.id.TsidUtils;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.List;

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
public class UserResource extends AbstractAuthenticatedResource {

    private static final Logger LOG = Logger.getLogger(UserResource.class);

    @GET
    @Path("/me")
    public Response getCurrentUser() {
        User user = getCurrentUserEntity();
        return Response.ok(UserDto.from(user)).build();
    }

    @PUT
    @Path("/me")
    public Response updateCurrentUser(@Valid UpdateUserRequest request) {
        Long userId = getCurrentUserId();
        User user = userService.updateUser(userId, request.displayName(), request.locale(), request.timezone());
        return Response.ok(UserDto.from(user)).build();
    }

    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") String id) {
        Long userId = TsidUtils.toLong(id);
        User user = userService.getActiveById(userId);
        return Response.ok(PublicUserDto.from(user)).build();
    }

    @GET
    @Path("/search")
    public Response searchUsers(@QueryParam("q") String query, @QueryParam("limit") @DefaultValue("10") int limit) {
        if (query == null || query.trim().isEmpty()) {
            return Response.ok(List.of()).build();
        }

        List<User> users = userService.searchByDisplayName(query.trim(), Math.min(limit, 20));
        List<PublicUserDto> dtos = users.stream().map(PublicUserDto::from).toList();
        return Response.ok(dtos).build();
    }

    @DELETE
    @Path("/me")
    public Response deleteCurrentUser() {
        Long userId = getCurrentUserId();
        userService.deleteUser(userId);

        LOG.infov("User {0} deleted their account", userId);
        return Response.noContent().build();
    }

    public record UserDto(
            String id,
            String email,
            String displayName,
            String avatarUrl,
            String locale,
            String timezone,
            String createdAt
    ) {
        public static UserDto from(User user) {
            return new UserDto(
                    TsidUtils.toString(user.getId()),
                    user.getEmail(),
                    user.getDisplayName(),
                    user.getAvatarUrl(),
                    user.getLocale(),
                    user.getTimezone(),
                    user.getCreatedAt() != null ? user.getCreatedAt().toString() : null
            );
        }
    }

    public record PublicUserDto(
            String id,
            String displayName,
            String avatarUrl
    ) {
        public static PublicUserDto from(User user) {
            return new PublicUserDto(
                    TsidUtils.toString(user.getId()),
                    user.getDisplayName(),
                    user.getAvatarUrl()
            );
        }
    }

    public record UpdateUserRequest(
            @Size(min = 1, max = 255)
            String displayName,

            @Size(max = 10)
            String locale,

            @Size(max = 50)
            String timezone
    ) {}
}
