package com.tribly.infrastructure.exception;

public class NotFoundException extends BusinessException {
  public NotFoundException(String message, String errorCode) {
    super(message, ErrorType.NOT_FOUND, errorCode);
  }

  public NotFoundException(String message) {
    super(message, ErrorType.NOT_FOUND);
  }
}
