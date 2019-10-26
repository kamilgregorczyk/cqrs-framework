package com.kgregorczyk.store.product.aggregate;

import static io.vavr.API.Case;
import static io.vavr.API.Match.Pattern0.of;

import com.kgregorczyk.store.cqrs.aggregate.Aggregate;
import com.kgregorczyk.store.cqrs.aggregate.GenericState;
import com.kgregorczyk.store.cqrs.event.DomainEvent;
import com.kgregorczyk.store.product.event.ProductCreatedEvent;
import com.kgregorczyk.store.product.event.ProductNameUpdatedEvent;
import com.kgregorczyk.store.product.event.ProductPriceUpdatedEvent;
import io.vavr.API;
import java.math.BigDecimal;
import java.util.Objects;

public class ProductAggregate extends Aggregate<ProductAggregate, GenericState> {

  public static final String EVENT_TOPIC = "product-events";
  public static final String COMMAND_TOPIC = "product-commands";

  private String name;
  private BigDecimal price;

  public ProductAggregate() {}

  @Override
  public ProductAggregate applyEvent(DomainEvent<ProductAggregate> event) {
    API.Match(event)
        .of(
            Case(of(ProductNameUpdatedEvent.class), this::applyEvent),
            Case(of(ProductPriceUpdatedEvent.class), this::applyEvent),
            Case(of(ProductCreatedEvent.class), this::applyEvent));
    return this;
  }

  public ProductAggregate applyEvent(ProductCreatedEvent event) {
    id(event.id());
    state(GenericState.CREATED);
    createdAt(event.createdAt());
    updatedAt(event.createdAt());
    name = event.name();
    price = event.price();
    addPendingEvent(event);
    return this;
  }

  public ProductAggregate applyEvent(ProductNameUpdatedEvent event) {
    if (event.name().equals(name)) {
      return this;
    }
    updatedAt(event.createdAt());
    name = event.name();
    addPendingEvent(event);
    return this;
  }

  public ProductAggregate applyEvent(ProductPriceUpdatedEvent event) {
    if (event.price().equals(price)) {
      return this;
    }
    updatedAt(event.createdAt());
    price = event.price();
    addPendingEvent(event);
    return this;
  }

  public String name() {
    return name;
  }

  public BigDecimal price() {
    return price;
  }

  @Override
  public String getEventTopic() {
    return EVENT_TOPIC;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProductAggregate that = (ProductAggregate) o;
    return Objects.equals(name, that.name) && Objects.equals(price, that.price);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, price);
  }

  @Override
  public String toString() {
    return "ProductAggregate{" + "name='" + name + '\'' + ", price=" + price + '}';
  }
}
