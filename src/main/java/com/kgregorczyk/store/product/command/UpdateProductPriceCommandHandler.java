package com.kgregorczyk.store.product.command;

import static com.google.common.base.Preconditions.checkArgument;

import com.kgregorczyk.store.cqrs.command.DomainCommandHandler;
import com.kgregorczyk.store.cqrs.command.DomainCommandPublisher;
import com.kgregorczyk.store.cqrs.event.DomainEventBus;
import com.kgregorczyk.store.product.aggregate.ProductAggregate;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdateProductPriceCommandHandler
    extends DomainCommandHandler<ProductAggregate, UpdateProductPriceCommand> {

  @Autowired
  public UpdateProductPriceCommandHandler(
      DomainCommandPublisher<ProductAggregate> commandPublisher, DomainEventBus domainEventBus) {
    super(commandPublisher, domainEventBus);
  }

  @Override
  protected void validateCommand(UpdateProductPriceCommand command) {
    checkArgument(command.price().compareTo(BigDecimal.ZERO) > 0,
        "Price has to be higher than 0.0");
  }

  @Override
  protected String getCommandTopic() {
    return ProductAggregate.COMMAND_TOPIC;
  }
}
