package com.tribly.dto.device.response;

import com.tribly.dto.validation.ValidateSchema;
import java.util.List;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Response containing routes for device applications")
@Builder
@ValidateSchema
public record DeviceRoutesResponse(
    @Schema(description = "List of routes", required = true) List<DeviceRouteDto> routes) {}
