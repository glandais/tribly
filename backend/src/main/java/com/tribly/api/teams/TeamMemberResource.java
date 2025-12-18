package com.tribly.api.teams;

import com.tribly.api.AbstractAuthenticatedResource;
import com.tribly.domain.team.Team;
import com.tribly.domain.team.TeamRole;
import com.tribly.domain.team.UserTeam;
import com.tribly.domain.user.User;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.service.team.TeamMembershipService;
import com.tribly.service.team.TeamService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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

@Path("/api/teams/{slug}/members")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
@Tag(name = "Team Members", description = "Team membership management operations")
public class TeamMemberResource extends AbstractAuthenticatedResource {

    @Inject
    TeamService teamService;

    @Inject
    TeamMembershipService membershipService;

    @GET
    @Operation(summary = "Get team members", description = "Get paginated list of team members")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Members retrieved successfully",
                    content = @Content(schema = @Schema(implementation = MemberListResponse.class))
            ),
            @APIResponse(responseCode = "401", description = "Unauthorized"),
            @APIResponse(responseCode = "404", description = "Team not found")
    })
    public Response getMembers(
            @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
            @Parameter(description = "Page number") @QueryParam("page") @DefaultValue("0") int page,
            @Parameter(description = "Page size") @QueryParam("size") @DefaultValue("50") int size) {

        Team team = getTeamBySlug(slug);
        Long userId = getCurrentUserId();

        List<UserTeam> members = membershipService.getTeamMembers(team, userId, page, size);
        long total = membershipService.countTeamMembers(team, userId);

        List<MemberDto> dtos = members.stream().map(MemberDto::from).toList();
        return Response.ok(new MemberListResponse(dtos, total, page, size)).build();
    }

    @POST
    @Path("/join")
    @Operation(summary = "Join team", description = "Request to join a team")
    @APIResponses({
            @APIResponse(
                    responseCode = "201",
                    description = "Successfully joined team",
                    content = @Content(schema = @Schema(implementation = MemberDto.class))
            ),
            @APIResponse(responseCode = "400", description = "Team is full or user is already a member"),
            @APIResponse(responseCode = "401", description = "Unauthorized"),
            @APIResponse(responseCode = "404", description = "Team not found")
    })
    public Response joinTeam(@Parameter(description = "Team URL slug") @PathParam("slug") String slug) {
        Team team = getTeamBySlug(slug);
        Long userId = getCurrentUserId();

        UserTeam membership = membershipService.joinTeam(team.getId(), userId);
        return Response.created(URI.create("/api/teams/" + slug + "/members/" + userId))
                .entity(MemberDto.from(membership))
                .build();
    }

    @POST
    @Path("/leave")
    @Operation(summary = "Leave team", description = "Leave a team")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Successfully left team"),
            @APIResponse(responseCode = "400", description = "Cannot leave - user is the owner"),
            @APIResponse(responseCode = "401", description = "Unauthorized"),
            @APIResponse(responseCode = "404", description = "Team not found or user is not a member")
    })
    public Response leaveTeam(@Parameter(description = "Team URL slug") @PathParam("slug") String slug) {
        Team team = getTeamBySlug(slug);
        Long userId = getCurrentUserId();

        membershipService.leaveTeam(team.getId(), userId);
        return Response.noContent().build();
    }

    @POST
    @Operation(summary = "Add team member", description = "Add a member to the team. Requires ADMIN role.")
    @APIResponses({
            @APIResponse(
                    responseCode = "201",
                    description = "Member added successfully",
                    content = @Content(schema = @Schema(implementation = MemberDto.class))
            ),
            @APIResponse(responseCode = "400", description = "Invalid request or user already a member"),
            @APIResponse(responseCode = "401", description = "Unauthorized"),
            @APIResponse(responseCode = "403", description = "User is not a team admin"),
            @APIResponse(responseCode = "404", description = "Team or user not found")
    })
    public Response addMember(@Parameter(description = "Team URL slug") @PathParam("slug") String slug, @Valid AddMemberRequest request) {
        Team team = getTeamBySlug(slug);
        Long actingUserId = getCurrentUserId();

        TeamRole role = request.role() != null ? request.role() : TeamRole.MEMBER;
        Long targetUserId = TsidUtils.toLong(request.userId());
        UserTeam membership = membershipService.addMember(team.getId(), targetUserId, role, actingUserId);

        return Response.created(URI.create("/api/teams/" + slug + "/members/" + request.userId()))
                .entity(MemberDto.from(membership))
                .build();
    }

    @PUT
    @Path("/{memberId}")
    @Transactional
    @Operation(summary = "Update member role", description = "Update a team member's role. Requires ADMIN role.")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Role updated successfully",
                    content = @Content(schema = @Schema(implementation = MemberDto.class))
            ),
            @APIResponse(responseCode = "400", description = "Invalid request"),
            @APIResponse(responseCode = "401", description = "Unauthorized"),
            @APIResponse(responseCode = "403", description = "User is not a team admin"),
            @APIResponse(responseCode = "404", description = "Team or member not found")
    })
    public Response updateMemberRole(
            @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
            @Parameter(description = "Member user ID (TSID)") @PathParam("memberId") String memberId,
            @Valid UpdateMemberRoleRequest request) {

        Team team = getTeamBySlug(slug);
        Long actingUserId = getCurrentUserId();

        UserTeam membership = membershipService.updateMemberRole(team.getId(), TsidUtils.toLong(memberId), request.role(), actingUserId);
        return Response.ok(MemberDto.from(membership)).build();
    }

    @DELETE
    @Path("/{memberId}")
    @Operation(summary = "Remove team member", description = "Remove a member from the team. Requires ADMIN role.")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Member removed successfully"),
            @APIResponse(responseCode = "401", description = "Unauthorized"),
            @APIResponse(responseCode = "403", description = "User is not a team admin"),
            @APIResponse(responseCode = "404", description = "Team or member not found")
    })
    public Response removeMember(
            @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
            @Parameter(description = "Member user ID (TSID)") @PathParam("memberId") String memberId) {

        Team team = getTeamBySlug(slug);
        Long actingUserId = getCurrentUserId();

        membershipService.removeMember(team.getId(), TsidUtils.toLong(memberId), actingUserId);
        return Response.noContent().build();
    }

    private Team getTeamBySlug(String slug) {
        return teamService.getTeamBySlug(slug)
                .orElseThrow(() -> BusinessException.notFound("Team with slug '" + slug + "' not found"));
    }

    @Schema(description = "Request to add a member to the team")
    public record AddMemberRequest(
            @Schema(description = "User ID (TSID) to add", examples = "0h4a8xzk8jv80", required = true)
            @NotNull String userId,

            @Schema(description = "Role to assign (defaults to MEMBER)", enumeration = {"ADMIN", "MEMBER"})
            TeamRole role
    ) {
    }

    @Schema(description = "Request to update a member's role")
    public record UpdateMemberRoleRequest(
            @Schema(description = "New role", enumeration = {"ADMIN", "MEMBER"}, required = true)
            @NotNull TeamRole role
    ) {
    }

    @Schema(description = "Team member information")
    public record MemberDto(
            @Schema(description = "Membership ID (TSID)")
            String id,

            @Schema(description = "User ID (TSID)")
            String userId,

            @Schema(description = "User display name")
            String displayName,

            @Schema(description = "User avatar URL")
            String avatarUrl,

            @Schema(description = "Member role", enumeration = {"OWNER", "ADMIN", "MEMBER"})
            String role,

            @Schema(description = "When the user joined the team")
            String joinedAt
    ) {
        public static MemberDto from(UserTeam userTeam) {
            User user = userTeam.getUser();
            return new MemberDto(
                    TsidUtils.toString(userTeam.getId()),
                    TsidUtils.toString(user.getId()),
                    user.getDisplayName(),
                    user.getAvatarUrl(),
                    userTeam.getRole().name(),
                    userTeam.getJoinedAt() != null ? userTeam.getJoinedAt().toString() : null
            );
        }
    }

    @Schema(description = "Paginated member list response")
    public record MemberListResponse(
            @Schema(description = "List of members")
            List<MemberDto> members,

            @Schema(description = "Total number of members")
            long total,

            @Schema(description = "Current page number")
            int page,

            @Schema(description = "Page size")
            int size
    ) {
    }
}
