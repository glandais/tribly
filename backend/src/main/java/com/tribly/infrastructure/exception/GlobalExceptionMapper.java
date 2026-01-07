package com.tribly.infrastructure.exception;

import com.tribly.common.exception.TriblyException;
import com.tribly.dto.error.ErrorCode;
import com.tribly.dto.error.ErrorResponse;
import com.tribly.dto.error.FieldError;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.List;
import java.util.stream.Collectors;
import org.jboss.logging.Logger;
import org.jspecify.annotations.Nullable;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

  private static final Logger LOG = Logger.getLogger(GlobalExceptionMapper.class);

  @Context @Nullable UriInfo uriInfo;

  @Override
  public Response toResponse(Throwable exception) {
    LOG.error("Error", exception);
    switch (exception) {
      case NotFoundException ignored -> {
        return notFound();
      }
      case EntityNotFoundException ignored -> {
        return notFound();
      }
      case NotAuthorizedException ignored -> {
        return unauthorized();
      }
      case ForbiddenException ignored -> {
        return forbidden();
      }
      case ConstraintViolationException cve -> {
        return validationError(cve);
      }
      case IllegalArgumentException ignored -> {
        return badRequest();
      }
      case TriblyException be -> {
        return triblyError(be);
      }
      case WebApplicationException wae -> {
        Response originalResponse = wae.getResponse();
        return Response.status(originalResponse.getStatus())
            .entity(new ErrorResponse(ErrorCode.UNKNOWN))
            .build();
      }
      default -> {}
    }
    return internal();
  }

  private Response notFound() {
    return Response.status(Response.Status.NOT_FOUND).entity(ErrorResponse.notFound()).build();
  }

  private Response unauthorized() {
    return Response.status(Response.Status.UNAUTHORIZED)
        .entity(ErrorResponse.unauthorized())
        .build();
  }

  private Response forbidden() {
    return Response.status(Response.Status.FORBIDDEN).entity(ErrorResponse.forbidden()).build();
  }

  private Response badRequest() {
    return Response.status(Response.Status.BAD_REQUEST).entity(ErrorResponse.badRequest()).build();
  }

  private Response validationError(ConstraintViolationException cve) {
    List<FieldError> fieldErrors =
        cve.getConstraintViolations().stream().map(this::toFieldError).collect(Collectors.toList());

    return Response.status(Response.Status.BAD_REQUEST)
        .entity(ErrorResponse.validation(fieldErrors))
        .build();
  }

  private FieldError toFieldError(ConstraintViolation<?> violation) {
    String propertyPath = violation.getPropertyPath().toString();
    String field =
        propertyPath.contains(".")
            ? propertyPath.substring(propertyPath.lastIndexOf('.') + 1)
            : propertyPath;

    return new FieldError(field, violation.getMessage(), violation.getInvalidValue());
  }

  private Response triblyError(TriblyException be) {
    Response.Status status = be.getStatus();
    return Response.status(status)
        .entity(new ErrorResponse(be.getErrorCode(), be.getErrorDetails()))
        .build();
  }

  private Response internal() {
    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
        .entity(ErrorResponse.internal())
        .build();
  }
}
