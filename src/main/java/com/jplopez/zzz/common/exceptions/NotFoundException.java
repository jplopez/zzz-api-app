package com.jplopez.zzz.common.exceptions;

/**
 * Generic exception when an Entity isn't found. 
 * This exception is used only for methods to retrieve one entity.
 * For methods that return collections, the not found use case will
 * return an empty collection.
 * 
 * @since 1.0
 */
public class NotFoundException extends RuntimeException {

  public NotFoundException() { super(); }
  public NotFoundException(String message, Throwable cause) { super(message,cause); }
}
