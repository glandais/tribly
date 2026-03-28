package fr.pedalons.dto.error;

import fr.pedalons.enums.EntityType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@RequiredArgsConstructor
@Getter
public class NotFoundDetails implements ErrorDetails {
  @Schema(description = "Type", required = true)
  final ErrorCode type = ErrorCode.NOT_FOUND;

  @Schema(description = "Entity type", required = true)
  final EntityType entityType;

  @Schema(description = "Search type", required = true)
  final SearchedBy searchedBy;

  @Schema(description = "id/slug", required = true)
  final String id;
}
