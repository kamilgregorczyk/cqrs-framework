package com.kgregorczyk.store.product.event;

import com.kgregorczyk.store.cqrs.aggregate.Id;
import com.kgregorczyk.store.cqrs.event.DomainEvent;
import com.kgregorczyk.store.product.aggregate.ProductAggregate;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class ProductCreatedEvent extends DomainEvent<ProductAggregate> {
  private final String name;
  private final BigDecimal price;

  @Builder
  public ProductCreatedEvent(Id<ProductAggregate> id, String name, BigDecimal price) {
    super(checkNotNull(id), UUID.randomUUID(), Instant.now());
    this.name = checkNotNull(name);
    this.price = checkNotNull(price);
  }
}
