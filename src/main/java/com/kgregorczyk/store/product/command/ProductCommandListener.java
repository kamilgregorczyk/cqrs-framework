package com.kgregorczyk.store.product.command;

import com.kgregorczyk.store.cqrs.command.DomainCommand;
import com.kgregorczyk.store.cqrs.event.DomainEventRepository;
import com.kgregorczyk.store.product.aggregate.ProductAggregate;
import com.kgregorczyk.store.product.event.ProductCreatedEvent;
import com.kgregorczyk.store.product.event.ProductNameUpdatedEvent;
import io.vavr.API;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static io.vavr.API.Case;
import static io.vavr.API.Match.Pattern0.of;

@Component
public class ProductCommandListener {

  private final DomainEventRepository<ProductAggregate> repository;

  @Autowired
  public ProductCommandListener(DomainEventRepository<ProductAggregate> repository) {
    this.repository = repository;
  }

  @KafkaListener(topics = ProductAggregate.COMMAND_TOPIC)
  public void commandHandler(DomainCommand<ProductAggregate> command) {
    API.Match(command)
        .of(
            Case(of(CreateProductCommand.class), this::commandHandler),
            Case(of(UpdateProductNameCommand.class), this::commandHandler));
  }

  private ProductAggregate commandHandler(CreateProductCommand command) {
    var aggregate = new ProductAggregate();
    var event =
        ProductCreatedEvent.builder()
            .id(command.getId())
            .price(command.getPrice())
            .name(command.getName())
            .build();
    return repository.save(aggregate.addPendingEvent(event));
  }

  private ProductAggregate commandHandler(UpdateProductNameCommand command) {
    var aggregate = new ProductAggregate(); // TODO
    var event =
        ProductNameUpdatedEvent.builder().id(command.getId()).name(command.getName()).build();
    return repository.save(aggregate.addPendingEvent(event));
  }
}
