package ru.translator.translateit.commonhandler;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CommonExceptionHandler {

  @ExceptionHandler
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleIllegalArgumentException(final IllegalArgumentException e) {
    return new ErrorResponse(HttpStatus.BAD_REQUEST, "Incorrectly made request.", e.getMessage(), LocalDateTime.now());
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleIllegalStateException(final IllegalStateException e) {
    return new ErrorResponse(HttpStatus.BAD_REQUEST, "IllegalStateException", e.getMessage(), LocalDateTime.now());
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
    return new ErrorResponse(HttpStatus.BAD_REQUEST, "Incorrectly made request.", e.getMessage(), LocalDateTime.now());
  }
}
