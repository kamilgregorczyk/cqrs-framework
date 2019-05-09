package com.kgregorczyk.store.cqrs.command;

public class DomainCommandException extends RuntimeException {

  public DomainCommandException(String message) {
    super(message);
  }

  public DomainCommandException(String message, Throwable e) {
    super(message, e);
  }
}
