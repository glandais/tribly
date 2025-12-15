package com.tribly.api.teams;

import com.tribly.api.AbstractAuthenticatedResource;
import com.tribly.domain.team.Team;
import com.tribly.domain.team.TeamRole;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.service.team.TeamService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;

@Path("/v1/teams")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TeamResource extends AbstractAuthenticatedResource {

    @Inject
    TeamService teamService;

    @GET
    @PermitAll
    public Response listTeams(
            @QueryParam("search") String search,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {

        List<Team> teams;
        long total;

        if (search != null && !search.isBlank()) {
            teams = teamService.searchPublicTeams(search, page, size);
            total = teams.size();
        } else {
            teams = teamService.getPublicTeams(page, size);
            total = teamService.countPublicTeams();
        }

        List<TeamDto> dtos = teams.stream().map(TeamDto::from).toList();
        return Response.ok(new TeamListResponse(dtos, total, page, size)).build();
    }

    @GET
    @Path("/my")
    @RolesAllowed("user")
    public Response getMyTeams() {
        Long userId = getCurrentUserId();
        List<Team> teams = teamService.getUserTeams(userId);
        List<TeamWithRoleDto> dtos = teams.stream()
                .map(t -> {
                    TeamRole role = teamService.getUserRole(userId, t.getId()).orElse(TeamRole.MEMBER);
                    return TeamWithRoleDto.from(t, role);
                })
                .toList();
        return Response.ok(dtos).build();
    }

    @GET
    @Path("/{slug}")
    @PermitAll
    public Response getTeam(@PathParam("slug") String slug) {
        Team team = teamService.getTeamBySlug(slug)
                .orElseThrow(() -> BusinessException.notFound("Team with slug '" + slug + "' not found"));

        Long userId = getCurrentUserIdOrNull();
        TeamRole role = null;
        if (userId != null) {
            role = teamService.getUserRoleBySlug(userId, slug).orElse(null);
        }

        if (!team.isPublic() && role == null) {
            throw BusinessException.forbidden("This team is private");
        }

        return Response.ok(TeamDetailDto.from(team, role)).build();
    }

    @POST
    @RolesAllowed("user")
    public Response createTeam(@Valid CreateTeamRequest request) {
        Long userId = getCurrentUserId();

        Team team = teamService.createTeam(
                new TeamService.CreateTeamRequest(
                        request.name(),
                        request.description(),
                        request.isPublic(),
                        request.maxMembers()
                ),
                userId
        );

        return Response.created(URI.create("/v1/teams/" + team.getSlug()))
                .entity(TeamDto.from(team))
                .build();
    }

    @PUT
    @Path("/{slug}")
    @RolesAllowed("user")
    @Transactional
    public Response updateTeam(@PathParam("slug") String slug, @Valid UpdateTeamRequest request) {
        Long userId = getCurrentUserId();

        Team team = teamService.getTeamBySlug(slug)
                .orElseThrow(() -> BusinessException.notFound("Team with slug '" + slug + "' not found"));

        Team updated = teamService.updateTeam(
                team.getId(),
                new TeamService.UpdateTeamRequest(
                        request.name(),
                        request.description(),
                        request.isPublic(),
                        request.logoUrl(),
                        request.coverImageUrl(),
                        request.maxMembers()
                ),
                userId
        );

        return Response.ok(TeamDto.from(updated)).build();
    }

    @DELETE
    @Path("/{slug}")
    @RolesAllowed("user")
    public Response deleteTeam(@PathParam("slug") String slug) {
        Long userId = getCurrentUserId();

        Team team = teamService.getTeamBySlug(slug)
                .orElseThrow(() -> BusinessException.notFound("Team with slug '" + slug + "' not found"));

        teamService.deleteTeam(team.getId(), userId);
        return Response.noContent().build();
    }

    public record CreateTeamRequest(
            @NotBlank @Size(min = 2, max = 255) String name,
            @Size(max = 2000) String description,
            Boolean isPublic,
            Integer maxMembers
    ) {}

    public record UpdateTeamRequest(
            @Size(min = 2, max = 255) String name,
            @Size(max = 2000) String description,
            Boolean isPublic,
            String logoUrl,
            String coverImageUrl,
            Integer maxMembers
    ) {}

    public record TeamDto(
            String id,
            String name,
            String slug,
            String description,
            String logoUrl,
            String coverImageUrl,
            boolean isPublic,
            int memberCount
    ) {
        public static TeamDto from(Team team) {
            return new TeamDto(
                    TsidUtils.toString(team.getId()),
                    team.getName(),
                    team.getSlug(),
                    team.getDescription(),
                    team.getLogoUrl(),
                    team.getCoverImageUrl(),
                    team.isPublic(),
                    team.getMemberCount()
            );
        }
    }

    public record TeamWithRoleDto(
            String id,
            String name,
            String slug,
            String description,
            String logoUrl,
            boolean isPublic,
            int memberCount,
            String role
    ) {
        public static TeamWithRoleDto from(Team team, TeamRole role) {
            return new TeamWithRoleDto(
                    TsidUtils.toString(team.getId()),
                    team.getName(),
                    team.getSlug(),
                    team.getDescription(),
                    team.getLogoUrl(),
                    team.isPublic(),
                    team.getMemberCount(),
                    role.name()
            );
        }
    }

    public record TeamDetailDto(
            String id,
            String name,
            String slug,
            String description,
            String logoUrl,
            String coverImageUrl,
            boolean isPublic,
            int memberCount,
            Integer maxMembers,
            String userRole,
            String createdAt
    ) {
        public static TeamDetailDto from(Team team, TeamRole role) {
            return new TeamDetailDto(
                    TsidUtils.toString(team.getId()),
                    team.getName(),
                    team.getSlug(),
                    team.getDescription(),
                    team.getLogoUrl(),
                    team.getCoverImageUrl(),
                    team.isPublic(),
                    team.getMemberCount(),
                    team.getMaxMembers(),
                    role != null ? role.name() : null,
                    team.getCreatedAt() != null ? team.getCreatedAt().toString() : null
            );
        }
    }

    public record TeamListResponse(
            List<TeamDto> teams,
            long total,
            int page,
            int size
    ) {}
}
