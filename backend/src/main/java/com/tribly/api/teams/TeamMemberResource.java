package com.tribly.api.teams;

import com.tribly.domain.team.Team;
import com.tribly.domain.team.TeamRole;
import com.tribly.domain.team.UserTeam;
import com.tribly.domain.user.User;
import com.tribly.infrastructure.exception.BusinessException;
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
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.net.URI;
import java.util.List;

@Path("/v1/teams/{slug}/members")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
public class TeamMemberResource {

    @Inject
    TeamService teamService;

    @Inject
    TeamMembershipService membershipService;

    @Inject
    JsonWebToken jwt;

    @GET
    public Response getMembers(
            @PathParam("slug") String slug,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {

        Team team = getTeamBySlug(slug);
        Long userId = getCurrentUserId();

        TeamRole userRole = teamService.getUserRole(userId, team.getId()).orElse(null);
        if (userRole == null && !team.isPublic()) {
            throw BusinessException.forbidden("You must be a member to view this team's members");
        }

        List<UserTeam> members = membershipService.getTeamMembers(team.getId(), page, size);
        long total = membershipService.countTeamMembers(team.getId());

        List<MemberDto> dtos = members.stream().map(MemberDto::from).toList();
        return Response.ok(new MemberListResponse(dtos, total, page, size)).build();
    }

    @POST
    @Path("/join")
    public Response joinTeam(@PathParam("slug") String slug) {
        Team team = getTeamBySlug(slug);
        Long userId = getCurrentUserId();

        UserTeam membership = membershipService.joinTeam(team.getId(), userId);
        return Response.created(URI.create("/v1/teams/" + slug + "/members/" + userId))
                .entity(MemberDto.from(membership))
                .build();
    }

    @POST
    @Path("/leave")
    public Response leaveTeam(@PathParam("slug") String slug) {
        Team team = getTeamBySlug(slug);
        Long userId = getCurrentUserId();

        membershipService.leaveTeam(team.getId(), userId);
        return Response.noContent().build();
    }

    @POST
    public Response addMember(@PathParam("slug") String slug, @Valid AddMemberRequest request) {
        Team team = getTeamBySlug(slug);
        Long actingUserId = getCurrentUserId();

        TeamRole role = request.role() != null ? request.role() : TeamRole.MEMBER;
        UserTeam membership = membershipService.addMember(team.getId(), request.userId(), role, actingUserId);

        return Response.created(URI.create("/v1/teams/" + slug + "/members/" + request.userId()))
                .entity(MemberDto.from(membership))
                .build();
    }

    @PUT
    @Path("/{memberId}")
    @Transactional
    public Response updateMemberRole(
            @PathParam("slug") String slug,
            @PathParam("memberId") Long memberId,
            @Valid UpdateMemberRoleRequest request) {

        Team team = getTeamBySlug(slug);
        Long actingUserId = getCurrentUserId();

        UserTeam membership = membershipService.updateMemberRole(team.getId(), memberId, request.role(), actingUserId);
        return Response.ok(MemberDto.from(membership)).build();
    }

    @DELETE
    @Path("/{memberId}")
    public Response removeMember(
            @PathParam("slug") String slug,
            @PathParam("memberId") Long memberId) {

        Team team = getTeamBySlug(slug);
        Long actingUserId = getCurrentUserId();

        membershipService.removeMember(team.getId(), memberId, actingUserId);
        return Response.noContent().build();
    }

    private Team getTeamBySlug(String slug) {
        return teamService.getTeamBySlug(slug)
                .orElseThrow(() -> BusinessException.notFound("Team with slug '" + slug + "' not found"));
    }

    private Long getCurrentUserId() {
        String subject = jwt.getSubject();
        if (subject == null) {
            throw new NotAuthorizedException("No valid token");
        }
        return Long.parseLong(subject);
    }

    public record AddMemberRequest(
            @NotNull Long userId,
            TeamRole role
    ) {}

    public record UpdateMemberRoleRequest(
            @NotNull TeamRole role
    ) {}

    public record MemberDto(
            Long id,
            Long userId,
            String displayName,
            String avatarUrl,
            String email,
            String role,
            String joinedAt
    ) {
        public static MemberDto from(UserTeam userTeam) {
            User user = userTeam.getUser();
            return new MemberDto(
                    userTeam.getId(),
                    user.getId(),
                    user.getDisplayName(),
                    user.getAvatarUrl(),
                    user.getEmail(),
                    userTeam.getRole().name(),
                    userTeam.getJoinedAt() != null ? userTeam.getJoinedAt().toString() : null
            );
        }
    }

    public record MemberListResponse(
            List<MemberDto> members,
            long total,
            int page,
            int size
    ) {}
}
