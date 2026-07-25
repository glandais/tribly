package fr.pedalons.infrastructure.exception;

import fr.pedalons.common.exception.PedalonsException;
import fr.pedalons.common.exception.TooManyRequestsException;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.dto.error.ErrorResponse;
import fr.pedalons.dto.error.FieldError;
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
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.jspecify.annotations.Nullable;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

  private static final Logger LOG = Logger.getLogger(GlobalExceptionMapper.class);

  @Context @Nullable UriInfo uriInfo;

  @ConfigProperty(name = "pedalons.error.log-details", defaultValue = "false")
  boolean logDetails;

  @Override
  public Response toResponse(Throwable exception) {
    switch (exception) {
      case NotFoundException e -> {
        warn(e, "Not found: {0}", getPath());
        return notFound();
      }
      case EntityNotFoundException e -> {
        warn(e, "Entity not found: {0}", getPath());
        return notFound();
      }
      case NotAuthorizedException e -> {
        warn(e, "Unauthorized: {0}", getPath());
        return unauthorized();
      }
      case ForbiddenException e -> {
        warn(e, "Forbidden: {0}", getPath());
        return forbidden();
      }
      case ConstraintViolationException cve -> {
        warn(cve, "Validation error: {0}", getPath());
        return validationError(cve);
      }
      case IllegalArgumentException e -> {
        warn(e, "Bad request: {0}", getPath());
        return badRequest();
      }
      case PedalonsException be -> {
        if (be.getStatus().getFamily() == Response.Status.Family.CLIENT_ERROR) {
          warn(be, "Client error: {0} {1}", be.getErrorCode(), getPath());
        } else {
          LOG.errorv(be, "Server error: {0} {1}", be.getErrorCode(), getPath());
        }
        return pedalonsError(be);
      }
      case WebApplicationException wae -> {
        Response originalResponse = wae.getResponse();
        int status = originalResponse.getStatus();
        if (status >= 500) {
          LOG.errorv(wae, "Server error {0}: {1}", status, getPath());
        } else {
          warn(wae, "Client error {0}: {1}", status, getPath());
        }
        return Response.status(status).entity(new ErrorResponse(ErrorCode.UNKNOWN)).build();
      }
      default -> {
        LOG.error("Unexpected error: " + getPath(), exception);
      }
    }
    return internal();
  }

  private void warn(Throwable t, String format, Object... params) {
    if (logDetails) {
      LOG.warnv(t, format, params);
    } else {
      LOG.warnv(format, params);
    }
  }

  private String getPath() {
    return uriInfo != null ? uriInfo.getRequestUri().getPath() : "unknown";
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

  private Response pedalonsError(PedalonsException be) {
    Response.Status status = be.getStatus();
    Response.ResponseBuilder builder =
        Response.status(status).entity(new ErrorResponse(be.getErrorCode(), be.getErrorDetails()));
    // Rate limits tell the caller when to come back, instead of leaving them to guess.
    if (be instanceof TooManyRequestsException tmr) {
      builder.header("Retry-After", tmr.getRetryAfterSeconds());
    }
    return builder.build();
  }

  private Response internal() {
    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
        .entity(ErrorResponse.internal())
        .build();
  }
}
