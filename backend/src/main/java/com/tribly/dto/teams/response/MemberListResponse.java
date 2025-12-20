package com.tribly.dto.teams.response;

import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Paginated member list response")
public record MemberListResponse(
    @Schema(description = "List of members", required = true) List<MemberDto> members,
    @Schema(description = "Total number of members", required = true) long total,
    @Schema(description = "Current page number", required = true) int page,
    @Schema(description = "Page size", required = true) int size) {}
