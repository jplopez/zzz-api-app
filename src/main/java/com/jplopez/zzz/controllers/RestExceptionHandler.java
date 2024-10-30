package com.jplopez.zzz.controllers;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.jplopez.zzz.common.exceptions.NotFoundException;

import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {


  @ExceptionHandler({ NotFoundException.class })
  protected ResponseEntity<Object> handleNotFound(
    Exception ex, WebRequest request) {
      return handleExceptionInternal(ex, "Not found", 
        new HttpHeaders(), HttpStatus.NOT_FOUND, request);
  }

  @ExceptionHandler({ ConstraintViolationException.class,
      DataIntegrityViolationException.class })
  public ResponseEntity<Object> handleBadRequest(
      Exception ex, WebRequest request) {
    return handleExceptionInternal(ex, ex.getLocalizedMessage(),
        new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }
}
