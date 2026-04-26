package com.bootcamp.userservice.exception;

public class FeignExceptionNotFound extends RuntimeException {
  public FeignExceptionNotFound(String message) {
    super(message);
  }
}
