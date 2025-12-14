package com.tribly.api.users;

import com.tribly.api.AbstractAuthenticatedResource;
import com.tribly.domain.user.User;
import com.tribly.infrastructure.exception.BusinessException;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

@Path("/v1/users")
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
    @Transactional
    public Response updateCurrentUser(@Valid UpdateUserRequest request) {
        User user = getCurrentUserEntity();

        if (request.displayName() != null) {
            user.setDisplayName(request.displayName());
        }
        if (request.locale() != null) {
            user.setLocale(request.locale());
        }
        if (request.timezone() != null) {
            user.setTimezone(request.timezone());
        }

        userRepository.persist(user);
        return Response.ok(UserDto.from(user)).build();
    }

    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") Long id) {
        User user = userRepository.findActiveById(id)
                .orElseThrow(() -> BusinessException.notFound("User", id));

        return Response.ok(PublicUserDto.from(user)).build();
    }

    @DELETE
    @Path("/me")
    @Transactional
    public Response deleteCurrentUser() {
        User user = getCurrentUserEntity();
        user.softDelete();
        userRepository.persist(user);

        LOG.infov("User {0} deleted their account", user.getId());
        return Response.noContent().build();
    }

    public record UserDto(
            Long id,
            String email,
            String displayName,
            String avatarUrl,
            String locale,
            String timezone,
            String createdAt
    ) {
        public static UserDto from(User user) {
            return new UserDto(
                    user.getId(),
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
            Long id,
            String displayName,
            String avatarUrl
    ) {
        public static PublicUserDto from(User user) {
            return new PublicUserDto(
                    user.getId(),
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
