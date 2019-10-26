package com.kgregorczyk.store.cqrs.command;

import com.kgregorczyk.store.cqrs.aggregate.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DomainCommandHandler<A extends Aggregate<A, ?>, C extends DomainCommand<A>> {

  private final Logger log = LoggerFactory.getLogger(getClass().getName());
  private final DomainCommandPublisher<A> commandPublisher;

  protected DomainCommandHandler(DomainCommandPublisher<A> commandPublisher) {
    this.commandPublisher = commandPublisher;
  }

  public void handle(C command) {
    log.info("Handling command [{}]", command);
    try {
      validateCommand(command);
    } catch (Exception e) {
      throw new DomainCommandException(
          String.format("Validation exception for [%s] command", command), e);
    }
    commandPublisher.publish(getCommandTopic(), command);
  }

  protected abstract void validateCommand(C command);

  protected abstract String getCommandTopic();
}
