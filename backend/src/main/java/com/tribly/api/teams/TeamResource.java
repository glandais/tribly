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
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.net.URI;
import java.util.List;

@Path("/api/teams")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Teams", description = "Team management operations")
public class TeamResource extends AbstractAuthenticatedResource {

    @Inject
    TeamService teamService;

    @GET
    @PermitAll
    @Operation(summary = "List public teams", description = "Get a paginated list of public teams with optional search")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Teams retrieved successfully",
                    content = @Content(schema = @Schema(implementation = TeamListResponse.class))
            )
    })
    public Response listTeams(
            @Parameter(description = "Search query to filter teams by name") @QueryParam("search") String search,
            @Parameter(description = "Page number (0-indexed)") @QueryParam("page") @DefaultValue("0") int page,
            @Parameter(description = "Page size") @QueryParam("size") @DefaultValue("20") int size) {

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
    @Operation(summary = "Get my teams", description = "Get all teams the current user is a member of")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "User teams retrieved successfully",
                    content = @Content(schema = @Schema(implementation = TeamWithRoleDto[].class))
            ),
            @APIResponse(responseCode = "401", description = "Unauthorized")
    })
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
    @Operation(summary = "Get team by slug", description = "Get detailed team information by URL slug")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Team retrieved successfully",
                    content = @Content(schema = @Schema(implementation = TeamDetailDto.class))
            ),
            @APIResponse(responseCode = "404", description = "Team not found"),
            @APIResponse(responseCode = "403", description = "Team is private and user is not a member")
    })
    public Response getTeam(@Parameter(description = "Team URL slug") @PathParam("slug") String slug) {
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
    @Operation(summary = "Create team", description = "Create a new team. The current user will be set as the team owner.")
    @APIResponses({
            @APIResponse(
                    responseCode = "201",
                    description = "Team created successfully",
                    content = @Content(schema = @Schema(implementation = TeamDto.class))
            ),
            @APIResponse(responseCode = "400", description = "Invalid request"),
            @APIResponse(responseCode = "401", description = "Unauthorized")
    })
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

        return Response.created(URI.create("/api/teams/" + team.getSlug()))
                .entity(TeamDto.from(team))
                .build();
    }

    @PUT
    @Path("/{slug}")
    @RolesAllowed("user")
    @Transactional
    @Operation(summary = "Update team", description = "Update team information. Requires ADMIN role.")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Team updated successfully",
                    content = @Content(schema = @Schema(implementation = TeamDto.class))
            ),
            @APIResponse(responseCode = "400", description = "Invalid request"),
            @APIResponse(responseCode = "401", description = "Unauthorized"),
            @APIResponse(responseCode = "403", description = "User is not a team admin"),
            @APIResponse(responseCode = "404", description = "Team not found")
    })
    public Response updateTeam(@Parameter(description = "Team URL slug") @PathParam("slug") String slug, @Valid UpdateTeamRequest request) {
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
    @Operation(summary = "Delete team", description = "Soft delete a team. Requires OWNER role.")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Team deleted successfully"),
            @APIResponse(responseCode = "401", description = "Unauthorized"),
            @APIResponse(responseCode = "403", description = "User is not the team owner"),
            @APIResponse(responseCode = "404", description = "Team not found")
    })
    public Response deleteTeam(@Parameter(description = "Team URL slug") @PathParam("slug") String slug) {
        Long userId = getCurrentUserId();

        Team team = teamService.getTeamBySlug(slug)
                .orElseThrow(() -> BusinessException.notFound("Team with slug '" + slug + "' not found"));

        teamService.deleteTeam(team.getId(), userId);
        return Response.noContent().build();
    }

    @Schema(description = "Team creation request")
    public record CreateTeamRequest(
            @Schema(description = "Team name", examples = "Awesome Cycling Team", required = true)
            @NotBlank @Size(min = 2, max = 255) String name,

            @Schema(description = "Team description", examples = "A team for weekend warriors")
            @Size(max = 2000) String description,

            @Schema(description = "Whether the team is publicly visible", examples = "true")
            Boolean isPublic,

            @Schema(description = "Maximum number of members (null = unlimited)", examples = "50")
            Integer maxMembers
    ) {
    }

    @Schema(description = "Team update request")
    public record UpdateTeamRequest(
            @Schema(description = "Team name")
            @Size(min = 2, max = 255) String name,

            @Schema(description = "Team description")
            @Size(max = 2000) String description,

            @Schema(description = "Whether the team is publicly visible")
            Boolean isPublic,

            @Schema(description = "Logo image URL")
            String logoUrl,

            @Schema(description = "Cover image URL")
            String coverImageUrl,

            @Schema(description = "Maximum number of members (null = unlimited)")
            Integer maxMembers
    ) {
    }

    @Schema(description = "Team summary data")
    public record TeamDto(
            @Schema(description = "Team ID (TSID)", examples = "0h4a8xzk8jv80")
            String id,

            @Schema(description = "Team name", examples = "Awesome Cycling Team")
            String name,

            @Schema(description = "Team URL slug", examples = "awesome-cycling-team")
            String slug,

            @Schema(description = "Team description")
            String description,

            @Schema(description = "Logo image URL")
            String logoUrl,

            @Schema(description = "Cover image URL")
            String coverImageUrl,

            @Schema(description = "Whether the team is public")
            boolean isPublic,

            @Schema(description = "Number of team members")
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

    @Schema(description = "Team data with user's role")
    public record TeamWithRoleDto(
            @Schema(description = "Team ID (TSID)", examples = "0h4a8xzk8jv80")
            String id,

            @Schema(description = "Team name")
            String name,

            @Schema(description = "Team URL slug")
            String slug,

            @Schema(description = "Team description")
            String description,

            @Schema(description = "Logo image URL")
            String logoUrl,

            @Schema(description = "Whether the team is public")
            boolean isPublic,

            @Schema(description = "Number of team members")
            int memberCount,

            @Schema(description = "User's role in the team", examples = "MEMBER", enumeration = {"OWNER", "ADMIN", "MEMBER"})
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

    @Schema(description = "Detailed team information")
    public record TeamDetailDto(
            @Schema(description = "Team ID (TSID)", examples = "0h4a8xzk8jv80")
            String id,

            @Schema(description = "Team name")
            String name,

            @Schema(description = "Team URL slug")
            String slug,

            @Schema(description = "Team description")
            String description,

            @Schema(description = "Logo image URL")
            String logoUrl,

            @Schema(description = "Cover image URL")
            String coverImageUrl,

            @Schema(description = "Whether the team is public")
            boolean isPublic,

            @Schema(description = "Number of team members")
            int memberCount,

            @Schema(description = "Maximum number of members (null = unlimited)")
            Integer maxMembers,

            @Schema(description = "Current user's role (null if not a member)", enumeration = {"OWNER", "ADMIN", "MEMBER"})
            String userRole,

            @Schema(description = "Team creation timestamp")
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

    @Schema(description = "Paginated team list response")
    public record TeamListResponse(
            @Schema(description = "List of teams")
            List<TeamDto> teams,

            @Schema(description = "Total number of teams")
            long total,

            @Schema(description = "Current page number")
            int page,

            @Schema(description = "Page size")
            int size
    ) {
    }
}
