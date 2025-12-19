package com.tribly.api.teams;

import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Paginated team list response")
public record TeamListResponse(
    @Schema(description = "List of teams", required = true) List<TeamDetailDto> teams,
    @Schema(description = "Total number of teams", required = true) long total,
    @Schema(description = "Current page number", required = true) int page,
    @Schema(description = "Page size", required = true) int size) {}
