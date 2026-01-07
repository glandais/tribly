package com.tribly.infrastructure.exception;

import com.tribly.dto.error.ErrorCode;
import com.tribly.dto.error.ErrorDetails;
import com.tribly.dto.error.NotFoundDetails;
import com.tribly.dto.error.SearchedBy;
import com.tribly.enums.AllEntityType;
import com.tribly.infrastructure.id.TsidUtils;
import jakarta.ws.rs.core.Response;
import org.jspecify.annotations.Nullable;

public class NotFoundException extends TriblyException {

  protected NotFoundException(@Nullable ErrorDetails errorDetails) {
    super(ErrorCode.NOT_FOUND, errorDetails, null);
  }

  public NotFoundException(AllEntityType entityType, String slug) {
    this(new NotFoundDetails(entityType, SearchedBy.SLUG, slug));
  }

  public NotFoundException(AllEntityType entityType, Long id) {
    this(new NotFoundDetails(entityType, SearchedBy.ID, TsidUtils.toString(id)));
  }

  @Override
  public Response.Status getStatus() {
    return Response.Status.NOT_FOUND;
  }
}
