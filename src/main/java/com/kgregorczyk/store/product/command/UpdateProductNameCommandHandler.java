package com.kgregorczyk.store.product.command;

import static com.google.common.base.Preconditions.checkArgument;

import com.kgregorczyk.store.cqrs.command.DomainCommandHandler;
import com.kgregorczyk.store.cqrs.command.DomainCommandPublisher;
import com.kgregorczyk.store.cqrs.event.DomainEventBus;
import com.kgregorczyk.store.product.aggregate.ProductAggregate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdateProductNameCommandHandler
    extends DomainCommandHandler<ProductAggregate, UpdateProductNameCommand> {

  @Autowired
  public UpdateProductNameCommandHandler(
      DomainCommandPublisher<ProductAggregate> commandPublisher, DomainEventBus domainEventBus) {
    super(commandPublisher, domainEventBus);
  }

  @Override
  protected void validateCommand(UpdateProductNameCommand command) {
    checkArgument(command.name().length() > 3, "Product's name has to have 4 or more characters");
  }

  @Override
  protected String getCommandTopic() {
    return ProductAggregate.COMMAND_TOPIC;
  }
}
