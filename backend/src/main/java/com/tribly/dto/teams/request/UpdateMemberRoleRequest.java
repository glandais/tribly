package com.tribly.dto.teams.request;

import com.tribly.enums.TeamRole;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Request to update a member's role")
public record UpdateMemberRoleRequest(
    @Schema(description = "New role", required = true) TeamRole role) {}
