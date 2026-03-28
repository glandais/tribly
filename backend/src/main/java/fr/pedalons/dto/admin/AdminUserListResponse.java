package fr.pedalons.dto.admin;

import fr.pedalons.dto.validation.ValidateSchema;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Paginated admin user list response")
@ValidateSchema
public record AdminUserListResponse(
    @Schema(description = "List of users", required = true) List<AdminUserDto> users,
    @Schema(description = "Total number of users", required = true) long total,
    @Schema(description = "Current page number", required = true) int page,
    @Schema(description = "Page size", required = true) int size) {}
