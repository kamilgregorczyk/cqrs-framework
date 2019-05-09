package com.kgregorczyk.store.product.aggregate;

import com.kgregorczyk.store.cqrs.aggregate.Aggregate;
import com.kgregorczyk.store.cqrs.aggregate.GenericState;
import com.kgregorczyk.store.cqrs.event.DomainEvent;
import com.kgregorczyk.store.product.event.ProductCreatedEvent;
import com.kgregorczyk.store.product.event.ProductNameUpdatedEvent;
import io.vavr.API;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

import static io.vavr.API.Case;
import static io.vavr.API.Match.Pattern0.of;

@EqualsAndHashCode(callSuper = true)
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProductAggregate extends Aggregate<ProductAggregate, GenericState> {
  public static final String EVENT_TOPIC = "product-events";
  public static final String COMMAND_TOPIC = "product-commands";
  public static final String EVENT_SNAPSHOT = "product-snapshots";

  private String name;
  private BigDecimal price;

  public ProductAggregate applyEvent(DomainEvent<ProductAggregate> event) {
    API.Match(event)
        .of(
            Case(of(ProductNameUpdatedEvent.class), this::applyEvent),
            Case(of(ProductCreatedEvent.class), this::applyEvent));
    return this;
  }

  public ProductAggregate applyEvent(ProductCreatedEvent event) {
    setState(GenericState.CREATED);
    setCreatedAt(event.getCreatedAt());
    setUpdatedAt(event.getCreatedAt());
    name = event.getName();
    price = event.getPrice();
    return this;
  }

  public ProductAggregate applyEvent(ProductNameUpdatedEvent event) {
    setUpdatedAt(event.getCreatedAt());
    name = event.getName();
    return this;
  }

  @Override
  public String getEventTopic() {
    return EVENT_TOPIC;
  }
}
