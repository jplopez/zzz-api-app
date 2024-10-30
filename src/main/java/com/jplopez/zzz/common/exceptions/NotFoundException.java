package com.jplopez.zzz.common.exceptions;

public class NotFoundException extends RuntimeException {

  public NotFoundException() { super(); }
  public NotFoundException(String message, Throwable cause) { super(message,cause); }
}
