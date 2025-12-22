package com.tribly.dto.rides.request;

import com.tribly.dto.validation.ValidateSchema;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Request to join a ride group")
@ValidateSchema
public record JoinGroupRequest() {}
