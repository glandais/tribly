package fr.pedalons.api.tags;

import fr.pedalons.dto.error.ErrorResponse;
import fr.pedalons.dto.tags.request.TagCreateRequest;
import fr.pedalons.dto.tags.request.TagUpdateRequest;
import fr.pedalons.dto.tags.response.TagDeletedDto;
import fr.pedalons.dto.tags.response.TagWithUsageDto;
import fr.pedalons.enums.TagTarget;
import fr.pedalons.service.tag.TagService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.jspecify.annotations.Nullable;

/**
 * A team's tag vocabulary (docs/LEDGER_*.md API-59). Tagging a content goes through that content's
 * own create/update request ({@code tagIds}), never through here.
 */
@Path("/api/teams/{teamSlug}/tags")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@org.eclipse.microprofile.openapi.annotations.tags.Tag(
    name = "Tags",
    description = "Team tag vocabulary, one set per kind of content")
public class TagResource {

  @Inject TagService tagService;

  @GET
  @PermitAll
  @Operation(
      operationId = "listTeamTags",
      summary = "List team tags",
      description =
          "The team's tags, sorted by label, each with its usage count. Open to whoever can see the"
              + " team.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Tags retrieved successfully",
        content = @Content(schema = @Schema(implementation = TagWithUsageDto[].class))),
    @APIResponse(
        responseCode = "403",
        description = "Team not visible to the caller",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response listTeamTags(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Only the tags of this kind of content; all kinds when absent")
          @QueryParam("type")
          @Nullable TagTarget type) {
    List<TagWithUsageDto> tags = tagService.listTags(teamSlug, type);
    return Response.ok(tags).build();
  }

  @POST
  @RolesAllowed("user")
  @Operation(
      operationId = "createTeamTag",
      summary = "Create a team tag",
      description = "Requires team admin permissions.")
  @APIResponses({
    @APIResponse(
        responseCode = "201",
        description = "Tag created",
        content = @Content(schema = @Schema(implementation = TagWithUsageDto.class))),
    @APIResponse(
        responseCode = "400",
        description =
            "Invalid label (TAG_LABEL_INVALID) or 100 tags of this kind already"
                + " (TAG_LIMIT_REACHED)",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "User is not a team admin",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "409",
        description =
            "Label already used in this team and kind, whatever the case (TAG_LABEL_TAKEN)",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response createTeamTag(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Valid TagCreateRequest request) {
    TagWithUsageDto tag = tagService.createTag(teamSlug, request);
    return Response.status(Response.Status.CREATED).entity(tag).build();
  }

  @PATCH
  @Path("/{tagId}")
  @RolesAllowed("user")
  @Operation(
      operationId = "updateTeamTag",
      summary = "Rename or recolour a team tag",
      description = "Absent fields are unchanged; the kind never changes. Requires team admin.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Tag updated",
        content = @Content(schema = @Schema(implementation = TagWithUsageDto.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid label (TAG_LABEL_INVALID)",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "User is not a team admin",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or tag not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "409",
        description =
            "Label already used in this team and kind, whatever the case (TAG_LABEL_TAKEN)",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response updateTeamTag(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Tag ID (TSID)") @PathParam("tagId") String tagId,
      @Valid TagUpdateRequest request) {
    TagWithUsageDto tag = tagService.updateTag(teamSlug, tagId, request);
    return Response.ok(tag).build();
  }

  @DELETE
  @Path("/{tagId}")
  @RolesAllowed("user")
  @Operation(
      operationId = "deleteTeamTag",
      summary = "Delete a team tag",
      description =
          "Detaches the tag from every content and deletes it for good. Requires team admin.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Tag deleted; how many contents lost it",
        content = @Content(schema = @Schema(implementation = TagDeletedDto.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "User is not a team admin",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or tag not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response deleteTeamTag(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Tag ID (TSID)") @PathParam("tagId") String tagId) {
    TagDeletedDto deleted = tagService.deleteTag(teamSlug, tagId);
    return Response.ok(deleted).build();
  }
}
