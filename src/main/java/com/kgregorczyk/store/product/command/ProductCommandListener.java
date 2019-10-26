package com.kgregorczyk.store.product.command;

import static com.kgregorczyk.store.product.event.ProductCreatedEvent.aProductCreatedEvent;
import static com.kgregorczyk.store.product.event.ProductNameUpdatedEvent.aProductNameUpdatedEvent;
import static com.kgregorczyk.store.product.event.ProductPriceUpdatedEvent.aProductPriceUpdatedEvent;
import static io.vavr.API.Case;
import static io.vavr.API.Match.Pattern0.of;
import static io.vavr.collection.Stream.ofAll;

import com.kgregorczyk.store.cqrs.aggregate.Id;
import com.kgregorczyk.store.cqrs.command.DomainCommand;
import com.kgregorczyk.store.cqrs.event.DomainEventRepository;
import com.kgregorczyk.store.product.aggregate.ProductAggregate;
import io.vavr.API;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProductCommandListener {
  private final DomainEventRepository<ProductAggregate> repository;

  @Autowired
  public ProductCommandListener(DomainEventRepository<ProductAggregate> repository) {
    this.repository = repository;
  }

  @KafkaListener(topics = ProductAggregate.COMMAND_TOPIC, containerFactory = "domainCommandContainerFactory")
  public void commandHandler(DomainCommand<ProductAggregate> command) {
    API.Match(command)
        .of(
            Case(of(CreateProductCommand.class), this::commandHandler),
            Case(of(UpdateProductPriceCommand.class), this::commandHandler),
            Case(of(UpdateProductNameCommand.class), this::commandHandler));
  }

  private ProductAggregate commandHandler(CreateProductCommand command) {
    final var aggregate = new ProductAggregate();
    final var event =
        aProductCreatedEvent(true)
            .id(command.id())
            .correlationId(command.correlationId())
            .price(command.price())
            .name(command.name())
            .build();
    return repository.save(aggregate.applyEvent(event));
  }

  private ProductAggregate commandHandler(UpdateProductNameCommand command) {
    final var aggregate = getAggregate(command.id());
    final var event =
        aProductNameUpdatedEvent(true)
            .id(command.id())
            .correlationId(command.correlationId())
            .name(command.name())
            .build();
    return repository.save(aggregate.applyEvent(event));
  }

  private ProductAggregate commandHandler(UpdateProductPriceCommand command) {
    final var aggregate = getAggregate(command.id());
    final var event =
        aProductPriceUpdatedEvent(true)
            .id(command.id())
            .correlationId(command.correlationId())
            .price(command.price())
            .build();
    return repository.save(aggregate.applyEvent(event));
  }

  private ProductAggregate getAggregate(Id<ProductAggregate> id) {
    return ofAll(repository.find(id))
        .foldLeft(new ProductAggregate(), (ProductAggregate::applyEvent));
  }
}
