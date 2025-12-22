package com.tribly.dto.teams.request;

import com.tribly.dto.validation.ValidateSchema;
import com.tribly.enums.TeamRole;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Request to add a member to the team")
@ValidateSchema
public record AddMemberRequest(
    @Schema(description = "User ID (TSID) to add", examples = "0h4a8xzk8jv80", required = true)
        String userId,
    @Nullable @Schema(description = "Role to assign (defaults to MEMBER)") TeamRole role) {}
