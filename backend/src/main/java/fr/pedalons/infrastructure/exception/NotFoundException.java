package fr.pedalons.infrastructure.exception;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.common.exception.PedalonsException;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.dto.error.ErrorDetails;
import fr.pedalons.dto.error.NotFoundDetails;
import fr.pedalons.dto.error.SearchedBy;
import fr.pedalons.enums.EntityType;
import jakarta.ws.rs.core.Response;
import org.jspecify.annotations.Nullable;

public class NotFoundException extends PedalonsException {

  protected NotFoundException(@Nullable ErrorDetails errorDetails) {
    super(ErrorCode.NOT_FOUND, errorDetails, null);
  }

  public NotFoundException(EntityType entityType, String slug) {
    this(new NotFoundDetails(entityType, SearchedBy.SLUG, slug));
  }

  public NotFoundException(EntityType entityType, Long id) {
    this(new NotFoundDetails(entityType, SearchedBy.ID, TsidUtils.toString(id)));
  }

  @Override
  public Response.Status getStatus() {
    return Response.Status.NOT_FOUND;
  }
}
