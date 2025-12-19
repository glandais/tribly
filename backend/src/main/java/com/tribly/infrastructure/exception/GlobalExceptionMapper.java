package com.tribly.infrastructure.exception;

import com.tribly.api.dto.ErrorResponse;
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
    String path = uriInfo != null ? uriInfo.getPath() : "unknown";

    switch (exception) {
      case NotFoundException ignored -> {
        return notFound(path, exception.getMessage());
      }
      case EntityNotFoundException ignored -> {
        return notFound(path, exception.getMessage());
      }
      case NotAuthorizedException ignored -> {
        return unauthorized(path);
      }
      case ForbiddenException ignored -> {
        return forbidden(path, exception.getMessage());
      }
      case ConstraintViolationException cve -> {
        return validationError(path, cve);
      }
      case IllegalArgumentException ignored -> {
        return badRequest(path, exception.getMessage());
      }
      case BusinessException be -> {
        return businessError(path, be);
      }
      case WebApplicationException wae -> {
        Response originalResponse = wae.getResponse();
        return Response.status(originalResponse.getStatus())
            .entity(new ErrorResponse("HTTP_ERROR", exception.getMessage(), path))
            .build();
      }
      default -> {}
    }

    LOG.error("Unhandled exception", exception);
    return internal(path);
  }

  private Response notFound(String path, @Nullable String message) {
    return Response.status(Response.Status.NOT_FOUND)
        .entity(ErrorResponse.notFound(path, message != null ? message : "Resource not found"))
        .build();
  }

  private Response unauthorized(String path) {
    return Response.status(Response.Status.UNAUTHORIZED)
        .entity(ErrorResponse.unauthorized(path, "Authentication required"))
        .build();
  }

  private Response forbidden(String path, @Nullable String message) {
    return Response.status(Response.Status.FORBIDDEN)
        .entity(ErrorResponse.forbidden(path, message != null ? message : "Access denied"))
        .build();
  }

  private Response badRequest(String path, @Nullable String message) {
    return Response.status(Response.Status.BAD_REQUEST)
        .entity(ErrorResponse.badRequest(path, message != null ? message : "Invalid request"))
        .build();
  }

  private Response validationError(String path, ConstraintViolationException cve) {
    List<ErrorResponse.FieldError> fieldErrors =
        cve.getConstraintViolations().stream().map(this::toFieldError).collect(Collectors.toList());

    return Response.status(Response.Status.BAD_REQUEST)
        .entity(ErrorResponse.badRequest(path, "Validation failed", fieldErrors))
        .build();
  }

  private ErrorResponse.FieldError toFieldError(ConstraintViolation<?> violation) {
    String propertyPath = violation.getPropertyPath().toString();
    String field =
        propertyPath.contains(".")
            ? propertyPath.substring(propertyPath.lastIndexOf('.') + 1)
            : propertyPath;

    return new ErrorResponse.FieldError(field, violation.getMessage(), violation.getInvalidValue());
  }

  private Response businessError(String path, BusinessException be) {
    Response.Status status =
        switch (be.getErrorType()) {
          case NOT_FOUND -> Response.Status.NOT_FOUND;
          case CONFLICT -> Response.Status.CONFLICT;
          case FORBIDDEN -> Response.Status.FORBIDDEN;
          default -> Response.Status.BAD_REQUEST;
        };

    return Response.status(status)
        .entity(new ErrorResponse(be.getErrorCode(), be.getMessage(), path))
        .build();
  }

  private Response internal(String path) {
    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
        .entity(ErrorResponse.internal(path, "An unexpected error occurred"))
        .build();
  }
}
