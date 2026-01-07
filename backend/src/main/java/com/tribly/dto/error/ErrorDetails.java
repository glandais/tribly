package com.tribly.dto.error;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tribly.dto.validation.ValidateSchema;
import org.eclipse.microprofile.openapi.annotations.media.DiscriminatorMapping;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

// Response DTOs
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type" // discriminator
    )
@JsonSubTypes({
  @JsonSubTypes.Type(value = ErrorValidationDetails.class, name = "VALIDATION"),
  @JsonSubTypes.Type(value = NotFoundDetails.class, name = "NOT_FOUND"),
})
@Schema(
    description = "Error details",
    discriminatorProperty = "type",
    discriminatorMapping = {
      @DiscriminatorMapping(value = "VALIDATION", schema = ErrorValidationDetails.class),
      @DiscriminatorMapping(value = "NOT_FOUND", schema = NotFoundDetails.class),
    },
    oneOf = {ErrorValidationDetails.class, NotFoundDetails.class})
@ValidateSchema
public interface ErrorDetails {

  ErrorCode getType();
}
